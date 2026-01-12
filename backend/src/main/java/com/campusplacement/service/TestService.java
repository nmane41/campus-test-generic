package com.campusplacement.service;

import com.campusplacement.dto.SubmitTestRequest;
import com.campusplacement.dto.TestResultDto;
import com.campusplacement.entity.Answer;
import com.campusplacement.entity.Question;
import com.campusplacement.entity.TestAttempt;
import com.campusplacement.entity.User;
import com.campusplacement.repository.AnswerRepository;
import com.campusplacement.repository.QuestionRepository;
import com.campusplacement.repository.TestAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestService {
    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    public boolean hasUserAttemptedTest(User user) {
        Optional<TestAttempt> attempt = testAttemptRepository.findByUser(user);
        return attempt.isPresent() && attempt.get().getEndTime() != null;
    }

    public TestAttempt startTest(User user) {
        if (hasUserAttemptedTest(user)) {
            throw new RuntimeException("User has already attempted the test");
        }

        Optional<TestAttempt> existingAttempt = testAttemptRepository.findByUser(user);
        if (existingAttempt.isPresent()) {
            TestAttempt attempt = existingAttempt.get();
            // If test was already started but not submitted, return existing
            if (attempt.getEndTime() == null) {
                return attempt;
            }
        }

        TestAttempt attempt = new TestAttempt();
        attempt.setUser(user);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setScore(0);
        return testAttemptRepository.save(attempt);
    }

    public TestResultDto submitTest(User user, SubmitTestRequest request) {
        TestAttempt attempt = testAttemptRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No active test attempt found"));

        if (attempt.getEndTime() != null) {
            throw new RuntimeException("Test has already been submitted");
        }

        // Validate timer - test should not exceed 50 minutes (3000 seconds)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = attempt.getStartTime();
        long elapsedSeconds = java.time.Duration.between(startTime, now).getSeconds();
        if (elapsedSeconds > 3000) {
            throw new RuntimeException("Test time limit exceeded. Test cannot be submitted after 50 minutes.");
        }

        Map<Long, String> answers = request.getAnswers();
        Map<Long, Integer> questionTimes = request.getQuestionTimes() != null 
            ? request.getQuestionTimes() 
            : new HashMap<>();
        int score = 0;
        int attemptedQuestions = 0;

        // Get all questions for the test (to track time for unanswered questions too)
        List<Question> allQuestions = questionRepository.findAll();
        Set<Long> answeredQuestionIds = new HashSet<>(answers.keySet());

        // Process answered questions
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String selectedOption = entry.getValue();
            
            // Skip if answer is empty or null
            if (selectedOption == null || selectedOption.trim().isEmpty()) {
                continue;
            }

            try {
                Question question = questionService.findById(questionId);
                attemptedQuestions++;

                Answer answer = new Answer();
                answer.setAttempt(attempt);
                answer.setQuestion(question);
                answer.setSelectedOption(selectedOption.toUpperCase());
                
                // Set time taken for this question (default to 0 if not provided)
                Integer timeTaken = questionTimes.getOrDefault(questionId, 0);
                answer.setTimeTakenSeconds(timeTaken);
                
                answerRepository.save(answer);

                if (question.getCorrectOption().equalsIgnoreCase(selectedOption)) {
                    score++;
                }
            } catch (Exception e) {
                // Skip invalid question IDs
                continue;
            }
        }

        // Process unanswered questions that have time tracked
        for (Map.Entry<Long, Integer> timeEntry : questionTimes.entrySet()) {
            Long questionId = timeEntry.getKey();
            Integer timeTaken = timeEntry.getValue();
            
            // Only create answer record if question was viewed (time > 0) but not answered
            if (timeTaken > 0 && !answeredQuestionIds.contains(questionId)) {
                try {
                    Question question = questionService.findById(questionId);
                    
                    Answer answer = new Answer();
                    answer.setAttempt(attempt);
                    answer.setQuestion(question);
                    answer.setSelectedOption(null); // null for unanswered
                    answer.setTimeTakenSeconds(timeTaken);
                    
                    answerRepository.save(answer);
                } catch (Exception e) {
                    // Skip invalid question IDs
                    continue;
                }
            }
        }

        attempt.setScore(score);
        attempt.setEndTime(now);
        testAttemptRepository.save(attempt);

        return new TestResultDto(
                attempt.getId(),
                score,
                attemptedQuestions,
                attempt.getStartTime(),
                attempt.getEndTime()
        );
    }

    public TestResultDto getTestResult(User user) {
        TestAttempt attempt = testAttemptRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No test attempt found"));

        if (attempt.getEndTime() == null) {
            throw new RuntimeException("Test has not been submitted yet");
        }

        List<Answer> answers = answerRepository.findByAttempt(attempt);
        int totalQuestions = answers.size();

        return new TestResultDto(
                attempt.getId(),
                attempt.getScore(),
                totalQuestions,
                attempt.getStartTime(),
                attempt.getEndTime()
        );
    }

    public List<TestAttempt> getAllTestAttempts() {
        return testAttemptRepository.findAllByOrderByEndTimeDesc();
    }

    public long getTotalTestAttempts() {
        return testAttemptRepository.count();
    }

    public double getAverageScore() {
        List<TestAttempt> attempts = testAttemptRepository.findAll();
        if (attempts.isEmpty()) {
            return 0.0;
        }

        List<TestAttempt> completedAttempts = attempts.stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());

        if (completedAttempts.isEmpty()) {
            return 0.0;
        }

        double sum = completedAttempts.stream()
                .mapToInt(TestAttempt::getScore)
                .sum();

        return sum / completedAttempts.size();
    }

    public Optional<TestAttempt> getUserTestAttempt(User user) {
        return testAttemptRepository.findByUser(user);
    }
}

