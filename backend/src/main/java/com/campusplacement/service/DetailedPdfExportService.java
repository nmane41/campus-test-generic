package com.campusplacement.service;

import com.campusplacement.dto.DetailedAnswerDto;
import com.campusplacement.dto.DetailedResultDto;
import com.campusplacement.util.TimeFormatter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DetailedPdfExportService {
    @Autowired
    private AdminService adminService;

    private static final String TEST_NAME = "Young Element Placement Test";

    /**
     * Generate ZIP file containing detailed PDF for each user
     */
    public byte[] generateDetailedResultsZip() throws Exception {
        List<DetailedResultDto> detailedResults = adminService.getAllDetailedResults();

        ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(zipBaos);

        for (DetailedResultDto result : detailedResults) {
            // Generate PDF for this user
            byte[] pdfBytes = generateUserDetailedPdf(result);

            // Create filename: username_email_detailed_result.pdf
            String filename = sanitizeFilename(result.getUserName()) + "_" +
                            sanitizeFilename(result.getEmail()) + "_detailed_result.pdf";

            // Add to ZIP
            ZipEntry zipEntry = new ZipEntry(filename);
            zipEntry.setSize(pdfBytes.length);
            zipOut.putNextEntry(zipEntry);
            zipOut.write(pdfBytes);
            zipOut.closeEntry();
        }

        zipOut.finish();
        zipOut.close();

        return zipBaos.toByteArray();
    }

    /**
     * Generate detailed PDF for a single user
     */
    private byte[] generateUserDetailedPdf(DetailedResultDto result) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Header
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph header = new Paragraph(TEST_NAME + " - Detailed Results", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(15);
        document.add(header);

        // User Information Section
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font infoLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 3f});
        infoTable.setSpacingAfter(15);

        addInfoRow(infoTable, "User Name:", result.getUserName(), infoLabelFont, infoFont);
        addInfoRow(infoTable, "Email:", result.getEmail(), infoLabelFont, infoFont);
        addInfoRow(infoTable, "Score:", String.valueOf(result.getScore()), infoLabelFont, infoFont);
        addInfoRow(infoTable, "Time Taken:", result.getTimeTaken(), infoLabelFont, infoFont);
        addInfoRow(infoTable, "Test Start Time:", result.getStartTimeIST() + " IST", infoLabelFont, infoFont);
        addInfoRow(infoTable, "Test End Time:", result.getEndTimeIST() + " IST", infoLabelFont, infoFont);

        document.add(infoTable);

        // Questions Table
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font correctFont = FontFactory.getFont(FontFactory.HELVETICA, 8, java.awt.Color.GREEN);
        Font wrongFont = FontFactory.getFont(FontFactory.HELVETICA, 8, java.awt.Color.RED);
        Font notAnsweredFont = FontFactory.getFont(FontFactory.HELVETICA, 8, java.awt.Color.ORANGE);

        PdfPTable questionTable = new PdfPTable(6);
        questionTable.setWidthPercentage(100);
        questionTable.setWidths(new float[]{0.5f, 2.5f, 1f, 1f, 1.5f, 1f});

        // Table headers
        addTableHeader(questionTable, "Q#", tableHeaderFont);
        addTableHeader(questionTable, "Question", tableHeaderFont);
        addTableHeader(questionTable, "Selected", tableHeaderFont);
        addTableHeader(questionTable, "Correct", tableHeaderFont);
        addTableHeader(questionTable, "Result", tableHeaderFont);
        addTableHeader(questionTable, "Time Taken", tableHeaderFont);

        // Table rows
        int questionNumber = 1;
        for (DetailedAnswerDto answer : result.getAnswers()) {
            // Question number
            questionTable.addCell(createCell(String.valueOf(questionNumber++), tableFont));

            // Question text (truncate if too long)
            String questionText = answer.getQuestionText();
            if (questionText.length() > 100) {
                questionText = questionText.substring(0, 97) + "...";
            }
            questionTable.addCell(createCell(questionText, tableFont));

            // Selected option
            String selected = answer.getSelectedOption() != null ? answer.getSelectedOption() : "Not Answered";
            questionTable.addCell(createCell(selected, tableFont));

            // Correct option
            questionTable.addCell(createCell(answer.getCorrectOption(), tableFont));

            // Result with color coding
            Font statusFont = tableFont;
            if ("CORRECT".equals(answer.getStatus())) {
                statusFont = correctFont;
            } else if ("WRONG".equals(answer.getStatus())) {
                statusFont = wrongFont;
            } else {
                statusFont = notAnsweredFont;
            }
            questionTable.addCell(createCell(answer.getStatus(), statusFont));
            
            // Time taken (format as mm:ss)
            Integer timeSeconds = answer.getTimeTakenSeconds() != null ? answer.getTimeTakenSeconds() : 0;
            String timeFormatted = formatTimeTaken(timeSeconds);
            questionTable.addCell(createCell(timeFormatted, tableFont));
        }

        document.add(questionTable);

        // Footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        String generatedOn = TimeFormatter.formatDateTimeToIST(LocalDateTime.now());
        Paragraph footer = new Paragraph("Generated on: " + generatedOn + " IST", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        return baos.toByteArray();
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(new java.awt.Color(240, 240, 240));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
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
        cell.setPadding(4);
        return cell;
    }

    private String sanitizeFilename(String filename) {
        // Remove or replace invalid filename characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String formatTimeTaken(Integer timeSeconds) {
        if (timeSeconds == null || timeSeconds < 0) {
            return "00:00";
        }
        long minutes = timeSeconds / 60;
        long seconds = timeSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
