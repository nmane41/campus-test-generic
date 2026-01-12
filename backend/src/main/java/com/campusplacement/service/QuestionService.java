package com.campusplacement.service;

import com.campusplacement.dto.QuestionDto;
import com.campusplacement.dto.QuestionRequest;
import com.campusplacement.entity.Question;
import com.campusplacement.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public List<QuestionDto> getAllQuestionsForTest() {
        List<Question> questions = questionRepository.findAll();
        Collections.shuffle(questions);
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::convertToDtoWithCorrectAnswer)
                .collect(Collectors.toList());
    }

    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return convertToDtoWithCorrectAnswer(question);
    }

    public QuestionDto createQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption().toUpperCase());

        Question saved = questionRepository.save(question);
        return convertToDtoWithCorrectAnswer(saved);
    }

    public QuestionDto updateQuestion(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectOption(request.getCorrectOption().toUpperCase());

        Question saved = questionRepository.save(question);
        return convertToDtoWithCorrectAnswer(saved);
    }

    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found");
        }
        questionRepository.deleteById(id);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public long getTotalQuestions() {
        return questionRepository.count();
    }

    private QuestionDto convertToDto(Question question) {
        return new QuestionDto(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        );
    }

    private QuestionDto convertToDtoWithCorrectAnswer(Question question) {
        QuestionDto dto = convertToDto(question);
        dto.setCorrectOption(question.getCorrectOption());
        return dto;
    }
}

