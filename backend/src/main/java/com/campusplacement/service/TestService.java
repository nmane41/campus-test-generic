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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            return existingAttempt.get();
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

        Map<Long, String> answers = request.getAnswers();
        int score = 0;
        int totalQuestions = 0;

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String selectedOption = entry.getValue().toUpperCase();

            Question question = questionService.findById(questionId);
            totalQuestions++;

            Answer answer = new Answer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selectedOption);
            answerRepository.save(answer);

            if (question.getCorrectOption().equalsIgnoreCase(selectedOption)) {
                score++;
            }
        }

        attempt.setScore(score);
        attempt.setEndTime(LocalDateTime.now());
        testAttemptRepository.save(attempt);

        return new TestResultDto(
                attempt.getId(),
                score,
                totalQuestions,
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
}

