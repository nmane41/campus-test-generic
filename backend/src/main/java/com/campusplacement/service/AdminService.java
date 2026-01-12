package com.campusplacement.service;

import com.campusplacement.dto.DashboardStatsDto;
import com.campusplacement.dto.DetailedAnswerDto;
import com.campusplacement.dto.DetailedResultDto;
import com.campusplacement.dto.PasswordResetResponse;
import com.campusplacement.dto.StudentResultDto;
import com.campusplacement.entity.Answer;
import com.campusplacement.entity.Question;
import com.campusplacement.entity.TestAttempt;
import com.campusplacement.entity.User;
import com.campusplacement.repository.AnswerRepository;
import com.campusplacement.repository.QuestionRepository;
import com.campusplacement.repository.TestAttemptRepository;
import com.campusplacement.repository.UserRepository;
import com.campusplacement.util.TimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public DashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long totalTestAttempts = testService.getTotalTestAttempts();
        double averageScore = testService.getAverageScore();

        return new DashboardStatsDto(totalUsers, activeUsers, totalTestAttempts, averageScore);
    }

    public List<StudentResultDto> getAllStudentResults() {
        List<TestAttempt> attempts = testAttemptRepository.findAllByOrderByEndTimeDesc();
        return attempts.stream()
                .filter(attempt -> attempt.getEndTime() != null)
                .map(attempt -> {
                    StudentResultDto dto = new StudentResultDto();
                    dto.setUserId(attempt.getUser().getId());
                    dto.setUsername(attempt.getUser().getUsername());
                    dto.setEmail(attempt.getUser().getEmail());
                    dto.setAttemptId(attempt.getId());
                    dto.setScore(attempt.getScore());
                    dto.setStartTime(attempt.getStartTime());
                    dto.setEndTime(attempt.getEndTime());
                    
                    // Calculate time taken in seconds
                    if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                        Duration duration = Duration.between(attempt.getStartTime(), attempt.getEndTime());
                        dto.setTimeTakenSeconds(duration.getSeconds());
                        dto.setTimeTakenFormatted(TimeFormatter.formatTimeTaken(duration.getSeconds()));
                    }
                    
                    // Format times in IST 12-hour format
                    dto.setStartTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getStartTime()));
                    dto.setEndTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getEndTime()));
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PasswordResetResponse resetUserPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String temporaryPassword = generateTemporaryPassword();
        userService.resetPassword(userId, temporaryPassword);

        return new PasswordResetResponse(temporaryPassword, "Password reset successfully");
    }

    public List<DetailedResultDto> getAllDetailedResults() {
        List<TestAttempt> attempts = testAttemptRepository.findAllByOrderByEndTimeDesc();
        return attempts.stream()
                .filter(attempt -> attempt.getEndTime() != null)
                .map(this::convertToDetailedResult)
                .collect(Collectors.toList());
    }

    private DetailedResultDto convertToDetailedResult(TestAttempt attempt) {
        User user = attempt.getUser();
        DetailedResultDto dto = new DetailedResultDto();
        dto.setUserId(user.getId());
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setScore(attempt.getScore());

        // Calculate time taken
        if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
            Duration duration = Duration.between(attempt.getStartTime(), attempt.getEndTime());
            dto.setTimeTaken(TimeFormatter.formatTimeTaken(duration.getSeconds()));
            dto.setStartTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getStartTime()));
            dto.setEndTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getEndTime()));
        }

        // Get all questions (to find unanswered ones)
        List<Question> allQuestions = questionRepository.findAll();
        Map<Long, Question> questionMap = allQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        // Get user's answers
        List<Answer> userAnswers = answerRepository.findByAttempt(attempt);
        Map<Long, Answer> answerMap = userAnswers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        // Build detailed answers list
        List<DetailedAnswerDto> detailedAnswers = new ArrayList<>();
        for (Question question : allQuestions) {
            DetailedAnswerDto answerDto = new DetailedAnswerDto();
            answerDto.setQuestionId(question.getId());
            answerDto.setQuestionText(question.getQuestionText());
            
            // Set options
            Map<String, String> options = new HashMap<>();
            options.put("A", question.getOptionA());
            options.put("B", question.getOptionB());
            options.put("C", question.getOptionC());
            options.put("D", question.getOptionD());
            answerDto.setOptions(options);
            
            answerDto.setCorrectOption(question.getCorrectOption());
            
            // Check if user answered or viewed this question
            Answer userAnswer = answerMap.get(question.getId());
            if (userAnswer != null) {
                String selectedOption = userAnswer.getSelectedOption();
                answerDto.setSelectedOption(selectedOption != null && !selectedOption.isEmpty() 
                    ? selectedOption 
                    : null);
                
                // Determine status
                if (selectedOption != null && !selectedOption.isEmpty()) {
                    if (question.getCorrectOption().equalsIgnoreCase(selectedOption)) {
                        answerDto.setStatus("CORRECT");
                    } else {
                        answerDto.setStatus("WRONG");
                    }
                } else {
                    answerDto.setStatus("NOT_ANSWERED");
                }
                
                // Set time taken (from answer record)
                answerDto.setTimeTakenSeconds(userAnswer.getTimeTakenSeconds() != null 
                    ? userAnswer.getTimeTakenSeconds() 
                    : 0);
            } else {
                answerDto.setSelectedOption(null);
                answerDto.setStatus("NOT_ANSWERED");
                answerDto.setTimeTakenSeconds(0); // No time spent if not viewed
            }
            
            detailedAnswers.add(answerDto);
        }

        dto.setAnswers(detailedAnswers);
        return dto;
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }
}

