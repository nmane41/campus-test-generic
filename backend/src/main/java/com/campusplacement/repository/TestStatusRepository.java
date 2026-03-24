package com.campusplacement.repository;

import com.campusplacement.entity.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestStatusRepository extends JpaRepository<TestStatus, Long> {
    Optional<TestStatus> findFirstByOrderByIdAsc();
}
