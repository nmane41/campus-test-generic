package com.campusplacement.repository;

import com.campusplacement.entity.TestAttempt;
import com.campusplacement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    Optional<TestAttempt> findByUser(User user);
    List<TestAttempt> findAllByOrderByEndTimeDesc();
    long count();
}

