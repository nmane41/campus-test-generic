package com.campusplacement.repository;

import com.campusplacement.entity.Answer;
import com.campusplacement.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByAttempt(TestAttempt attempt);
}

