package com.campusplacement.service;

import com.campusplacement.entity.TestStatus;
import com.campusplacement.repository.TestStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class TestStatusService {
    @Autowired
    private TestStatusRepository testStatusRepository;

    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_NOT_STARTED = "NOT_STARTED";

    public TestStatus getTestStatus() {
        Optional<TestStatus> status = testStatusRepository.findFirstByOrderByIdAsc();
        if (status.isPresent()) {
            return status.get();
        }
        // Initialize if not exists
        TestStatus newStatus = new TestStatus(STATUS_NOT_STARTED);
        return testStatusRepository.save(newStatus);
    }

    public TestStatus startTest() {
        TestStatus status = getTestStatus();
        status.setStatus(STATUS_STARTED);
        status.setTestStartTime(LocalDateTime.now());
        return testStatusRepository.save(status);
    }

    public boolean isTestStarted() {
        TestStatus status = getTestStatus();
        return STATUS_STARTED.equals(status.getStatus());
    }

    public LocalDateTime getTestStartTime() {
        TestStatus status = getTestStatus();
        return status.getTestStartTime();
    }
}
