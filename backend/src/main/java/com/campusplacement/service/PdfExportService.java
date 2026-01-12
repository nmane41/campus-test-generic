package com.campusplacement.service;

import com.campusplacement.dto.StudentResultDto;
import com.campusplacement.repository.AnswerRepository;
import com.campusplacement.repository.TestAttemptRepository;
import com.campusplacement.util.TimeFormatter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {
    @Autowired
    private AdminService adminService;

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private static final int TEST_DURATION_MINUTES = 50;
    private static final int TOTAL_QUESTIONS = 50;

    public byte[] generateResultsPdf() throws Exception {
        List<StudentResultDto> results = adminService.getAllStudentResults();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Header
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph header = new Paragraph("Young Element Placement Test Results", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);

        // Export date (in IST)
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        String exportDate = TimeFormatter.formatDateTimeToIST(LocalDateTime.now());
        Paragraph datePara = new Paragraph("Exported on: " + exportDate + " IST", dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(20);
        document.add(datePara);

        // Table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 3f, 2f, 2f, 2f, 2f, 3f});

        // Table headers
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        addTableHeader(table, "Student Name", tableHeaderFont);
        addTableHeader(table, "Email", tableHeaderFont);
        addTableHeader(table, "Score", tableHeaderFont);
        addTableHeader(table, "Total Questions", tableHeaderFont);
        addTableHeader(table, "Attempted", tableHeaderFont);
        addTableHeader(table, "Time Taken", tableHeaderFont);
        addTableHeader(table, "Submission Time", tableHeaderFont);

        // Table rows
        Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (StudentResultDto result : results) {
            table.addCell(createCell(result.getUsername(), tableFont));
            table.addCell(createCell(result.getEmail(), tableFont));
            table.addCell(createCell(String.valueOf(result.getScore()), tableFont));
            table.addCell(createCell(String.valueOf(TOTAL_QUESTIONS), tableFont));
            
            // Get attempted questions count
            int attemptedCount = getAttemptedQuestionsCount(result.getAttemptId());
            table.addCell(createCell(String.valueOf(attemptedCount), tableFont));
            
            // Format time taken as mm:ss
            String timeTaken = result.getTimeTakenFormatted() != null 
                ? result.getTimeTakenFormatted() 
                : TimeFormatter.formatTimeTaken(result.getTimeTakenSeconds());
            table.addCell(createCell(timeTaken, tableFont));
            
            // Use IST formatted time from DTO
            String submissionTime = result.getEndTimeIST() != null 
                ? result.getEndTimeIST() 
                : "N/A";
            table.addCell(createCell(submissionTime, tableFont));
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
        table.addCell(cell);
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private int getAttemptedQuestionsCount(Long attemptId) {
        return testAttemptRepository.findById(attemptId)
                .map(attempt -> {
                    try {
                        return answerRepository.findByAttempt(attempt).size();
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .orElse(0);
    }
}
