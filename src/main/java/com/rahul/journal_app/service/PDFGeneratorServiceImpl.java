package com.rahul.journal_app.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

@Slf4j
@Service
public class PDFGeneratorServiceImpl implements PDFGeneratorService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public void export(HttpServletResponse response, String userName) throws IOException {
        UserDto user = userService.getUserDetail(userName);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Step 1: Create PDF for journal entries first and store page numbers
        Map<String, Integer> journalPageNumbers = new HashMap<>();
        ByteArrayOutputStream journalEntriesPdf = createJournalEntriesPdf(user, journalPageNumbers);
        
        // Step 2: Create PDF for user details and TOC using stored page numbers
        ByteArrayOutputStream userDetailsPdf = createUserDetailsPdf(user, journalPageNumbers);
        
        // Step 3: Merge PDFs in the specified order (user details + journal entries)
        mergePDFs(response, userDetailsPdf, journalEntriesPdf);
    }

    private ByteArrayOutputStream createJournalEntriesPdf(UserDto user, Map<String, Integer> journalPageNumbers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new RegularPageEventHelper());
        document.open();

        // Add header background and text
        PdfContentByte canvas = writer.getDirectContent();
        canvas.setColorFill(new Color(0, 51, 102));
        canvas.rectangle(0, PageSize.A4.getHeight() - 60, PageSize.A4.getWidth(), 60);
        canvas.fill();

        canvas.setColorFill(Color.WHITE);
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 24);
        canvas.beginText();
        canvas.showTextAligned(Element.ALIGN_CENTER, "Journal", PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() - 30, 0);
        canvas.endText();

        // Add decorative line
        canvas.setColorStroke(new Color(0, 51, 102));
        canvas.setLineWidth(2f);
        canvas.moveTo(40, PageSize.A4.getHeight() - 100);
        canvas.lineTo(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 100);
        canvas.stroke();


//        // Add Journal heading
//        Font sectionFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102));
//        Paragraph journalHeading = new Paragraph("Journal", sectionFont);
//        journalHeading.setSpacingBefore(30);
//        journalHeading.setSpacingAfter(20);
//        journalHeading.setAlignment(Element.ALIGN_CENTER);
//        document.add(journalHeading);


        if (user.getJournalEntities() != null && !user.getJournalEntities().isEmpty()) {
            for (var journal : user.getJournalEntities()) {
                // Get the current page number before adding the journal
                int currentPage = writer.getPageNumber();
                log.info("Current page number before adding journal '{}': {}", journal.getTitle(), currentPage);

                // Journal Entry Card
                PdfPTable journalCard = new PdfPTable(1);
                journalCard.setWidthPercentage(100);
                journalCard.setSpacingBefore(20);
                journalCard.setSpacingAfter(20);

                // Title
                PdfPCell titleCell = new PdfPCell(new Phrase(
                    journal.getTitle(),
                    new Font(Font.HELVETICA, 14, Font.BOLD, new Color(0, 51, 102))
                ));

                // Get the page number after adding the journal Title
                int newPageNumber = writer.getPageNumber();
                log.info("Page number after adding journal '{}': {}", journal.getTitle(), newPageNumber);

                titleCell.setPadding(8);
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setBackgroundColor(new Color(250, 250, 250));
                journalCard.addCell(titleCell);

                // Content
                Paragraph contentParagraph = new Paragraph();
                contentParagraph.add(new Phrase(
                    journal.getContent(),
                    new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(51, 51, 51))
                ));
                
                contentParagraph.add(new Phrase("\n\n"));
                
                Paragraph footerLine = new Paragraph();
                footerLine.setAlignment(Element.ALIGN_RIGHT);
                footerLine.add(new Chunk(
                    new SimpleDateFormat("MMM dd, yyyy").format(journal.getDate()),
                    new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(102, 102, 102))
                ));
                
                footerLine.add(new Chunk("  "));
                footerLine.add(new Chunk(
                    journal.getSentiment() != null ? journal.getSentiment().toString() : "N/A",
                    new Font(Font.HELVETICA, 8, Font.BOLD, new Color(0, 102, 0))
                ));
                
                contentParagraph.add(footerLine);

                PdfPCell contentCell = new PdfPCell(contentParagraph);
                contentCell.setPadding(8);
                contentCell.setBorder(Rectangle.NO_BORDER);
                contentCell.setBackgroundColor(Color.WHITE);
                journalCard.addCell(contentCell);

                document.add(journalCard);


                // Store the page number for this journal
                journalPageNumbers.put(journal.getTitle(), newPageNumber);
            }
        }

        document.close();
        log.info("Journal entries PDF completed with final page numbers: {}", journalPageNumbers);
        return baos;
    }

    private ByteArrayOutputStream createUserDetailsPdf(UserDto user, Map<String, Integer> journalPageNumbers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new RomanNumeralPageEventHelper());
        document.open();

        log.info("Starting user details PDF with journal page numbers: {}", journalPageNumbers);

        // Add header background
        PdfContentByte canvas = writer.getDirectContent();
        canvas.setColorFill(new Color(0, 51, 102));
        canvas.rectangle(0, PageSize.A4.getHeight() - 100, PageSize.A4.getWidth(), 100);
        canvas.fill();

        // Add logo and header text
        canvas.setColorFill(Color.WHITE);
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 24);
        canvas.beginText();
        canvas.showTextAligned(Element.ALIGN_LEFT, "VELINQ", 50, PageSize.A4.getHeight() - 50, 0);
        canvas.endText();

        // Add user ID at top-right
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 10);
        canvas.beginText();
        canvas.showTextAligned(Element.ALIGN_RIGHT, "User ID: " + user.getUserName(), PageSize.A4.getWidth() - 50, PageSize.A4.getHeight() - 50, 0);
        canvas.endText();

        // Add decorative line
        canvas.setColorStroke(new Color(0, 51, 102));
        canvas.setLineWidth(2f);
        canvas.moveTo(40, PageSize.A4.getHeight() - 120);
        canvas.lineTo(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 120);
        canvas.stroke();

        // User Details Section - Government ID/Passport Style
        Font sectionFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102));
        Paragraph userDetails = new Paragraph("Official User Profile", sectionFont);
        userDetails.setSpacingBefore(30);
        userDetails.setSpacingAfter(20);
        document.add(userDetails);

        // ID Card - Government Style
        PdfPTable idCard = new PdfPTable(1);
        idCard.setWidthPercentage(95);
        idCard.setSpacingBefore(0);
        idCard.setSpacingAfter(0);
        idCard.setHorizontalAlignment(Element.ALIGN_CENTER);

        // ID Card Body
        PdfPCell bodyCell = new PdfPCell();
        bodyCell.setPadding(0);
        bodyCell.setBorder(Rectangle.NO_BORDER);
        
        // Create the body content table
        PdfPTable bodyTable = new PdfPTable(1);
        bodyTable.setWidthPercentage(100);
        
        // Name section with security pattern background
        PdfPCell nameSectionCell = new PdfPCell();
        nameSectionCell.setPadding(20);
        nameSectionCell.setBorder(Rectangle.NO_BORDER);
        nameSectionCell.setBackgroundColor(new Color(240, 240, 245)); // Very light blue/gray
        
        // Draw security pattern
        drawSecurityPattern(writer, nameSectionCell);
        
        // ID Number at top-right
        Font idNumberFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(100, 100, 100));
        Paragraph idNumberPara = new Paragraph("User ID: " + generateIdNumber(user.getUserName()), idNumberFont);
        idNumberPara.setAlignment(Element.ALIGN_RIGHT);
        nameSectionCell.addElement(idNumberPara);
        
        // Full name in large, bold text
        Font fullNameFont = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(25, 25, 25));
        Paragraph fullNamePara = new Paragraph(user.getFirstName().toUpperCase() + " " + user.getLastName().toUpperCase(), fullNameFont);
        fullNamePara.setAlignment(Element.ALIGN_CENTER);
        fullNamePara.setSpacingBefore(10);
        fullNamePara.setSpacingAfter(15);
        nameSectionCell.addElement(fullNamePara);
        
        // Status as a badge
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(40);
        statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusTable.setSpacingBefore(5);
        statusTable.setSpacingAfter(10);
        
        PdfPCell statusCell = new PdfPCell();
        statusCell.setPadding(6);
        statusCell.setBorder(Rectangle.NO_BORDER);
        
        // Choose color based on verification status
        Color statusColor = user.isVerified() ? new Color(40, 167, 69) : new Color(220, 53, 69);
        statusCell.setBackgroundColor(statusColor);
        
        // Status text
        Font statusFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Paragraph statusPara = new Paragraph(user.isVerified() ? "VERIFIED" : "UNVERIFIED", statusFont);
        statusPara.setAlignment(Element.ALIGN_CENTER);
        statusCell.addElement(statusPara);
        
        // Add rounded corners to status cell
        statusCell.setCellEvent(new RoundedCornersCellEvent(12f, null));
        
        statusTable.addCell(statusCell);
        nameSectionCell.addElement(statusTable);
        
        bodyTable.addCell(nameSectionCell);
        
        // Personal information section
        PdfPCell infoSectionCell = new PdfPCell();
        infoSectionCell.setPadding(20);
        infoSectionCell.setBorder(Rectangle.NO_BORDER);
        infoSectionCell.setBackgroundColor(new Color(255, 255, 255));
        
        // Define nameHeaderFont for field labels
        Font nameHeaderFont = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(25, 54, 103));
        
        // Information grid - passport style
        PdfPTable infoGrid = new PdfPTable(2);
        infoGrid.setWidthPercentage(100);
        infoGrid.setWidths(new float[]{1, 1});
        
        // Role information from user's actual roles
        String rolesDisplay = "USER";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            // If user has ADMIN role, display ADMIN regardless of other roles
            if (user.getRoles().contains("ADMIN")) {
                rolesDisplay = "ADMIN";
            } else {
                // Otherwise, show the first role
                rolesDisplay = user.getRoles().get(0);
            }
        }
        addPassportField(infoGrid, "Role", rolesDisplay, nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // Number of journals
        int journalCount = user.getJournalEntities() != null ? user.getJournalEntities().size() : 0;
        addPassportField(infoGrid, "Journal Entries", String.valueOf(journalCount), nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // User Creation Date
        String creationDate = "N/A";
        if (user.getUserCreatedDate() != null) {
            creationDate = user.getUserCreatedDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
        addPassportField(infoGrid, "Account Created", creationDate, nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // Gender
        addPassportField(infoGrid, "Gender", user.getGender(), nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // Nationality
        addPassportField(infoGrid, "Nationality", user.getCountry().toUpperCase(), nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // Date of Birth
        addPassportField(infoGrid, "Date of Birth", formatDate(user.getDateOfBirth().toString()), nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        // Place of Birth (using city)
        addPassportField(infoGrid, "Place of Birth", user.getCity().toUpperCase(), nameHeaderFont, new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 0)));
        
        infoSectionCell.addElement(infoGrid);
        
        // Contact Information header
        Paragraph contactHeader = new Paragraph("Contact Information", nameHeaderFont);
        contactHeader.setSpacingBefore(15);
        contactHeader.setSpacingAfter(5);
        infoSectionCell.addElement(contactHeader);
        
        // Contact grid
        PdfPTable contactGrid = new PdfPTable(2);
        contactGrid.setWidthPercentage(100);
        contactGrid.setWidths(new float[]{1, 1});
        
        // Email
        addPassportField(contactGrid, "Email", user.getUserName(), nameHeaderFont, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        
        // Phone
        addPassportField(contactGrid, "Phone", user.getPhoneNo(), nameHeaderFont, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        
        // Address (using city and country)
        addPassportField(contactGrid, "Address", user.getCity() + ", " + user.getCountry(), nameHeaderFont, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        
        // Postal Code
        addPassportField(contactGrid, "Postal Code", user.getPinCode(), nameHeaderFont, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0, 0, 0)));
        
        infoSectionCell.addElement(contactGrid);
        
        // Issue and expiry dates
        PdfPTable dateTable = new PdfPTable(2);
        dateTable.setWidthPercentage(100);
        dateTable.setWidths(new float[]{1, 1});
        dateTable.setSpacingBefore(15);
        
        infoSectionCell.addElement(dateTable);
        
        bodyTable.addCell(infoSectionCell);
        
        bodyCell.addElement(bodyTable);
        idCard.addCell(bodyCell);
        
        // Add rounded corners and shadow
        RoundedCornersCellEvent roundedCorners = new RoundedCornersCellEvent(8f, null);
        
        for (PdfPCell cell : idCard.getRows().get(0).getCells()) {
            cell.setCellEvent(roundedCorners);
        }

        // Add shadow container
        PdfPTable shadowContainer = new PdfPTable(1);
        shadowContainer.setWidthPercentage(95);
        shadowContainer.setSpacingBefore(20);
        shadowContainer.setSpacingAfter(30);
        shadowContainer.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell shadowCell = new PdfPCell(idCard);
        shadowCell.setPadding(5);
        shadowCell.setBorder(Rectangle.NO_BORDER);
        shadowCell.setCellEvent(new ShadowCellEvent());
        
        shadowContainer.addCell(shadowCell);
        document.add(shadowContainer);

        // Add footer using existing canvas
        canvas.beginText();
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9);
        canvas.setColorFill(new Color(102, 102, 102));
        canvas.showTextAligned(
            Element.ALIGN_RIGHT,
            "Generated on: " + new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a").format(new Date()),
            PageSize.A4.getWidth() - 50,
            50,
            0
        );
        canvas.endText();

        // Table of Contents
        document.newPage();
        PdfPTable tocHeader = new PdfPTable(1);
        tocHeader.setWidthPercentage(100);
        tocHeader.setSpacingBefore(20);

        PdfPCell tocTitleCell = new PdfPCell(new Phrase("Table of Contents", 
            new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102))));
        tocTitleCell.setBorder(Rectangle.NO_BORDER);
        tocTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tocTitleCell.setPadding(10);
        tocHeader.addCell(tocTitleCell);
        document.add(tocHeader);

        if (user.getJournalEntities() != null && !user.getJournalEntities().isEmpty()) {
            PdfPTable tocTable = new PdfPTable(2);
            tocTable.setWidthPercentage(100);
            tocTable.setSpacingBefore(20);
            tocTable.setSpacingAfter(20);
            tocTable.setWidths(new float[]{4, 1});

            addTableHeaderCell(tocTable, "Journal Title", new Color(0, 51, 102));
            addTableHeaderCell(tocTable, "Page", new Color(0, 51, 102));

            // Calculate the offset for page numbers (user details + TOC page)
            int pageOffset = 2; // User details (1) + TOC (1)
            log.info("TOC page offset: {}", pageOffset);

        for (var journal : user.getJournalEntities()) {
                PdfPCell tocEntryCell = new PdfPCell(new Phrase(
                    journal.getTitle(),
                    new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(51, 51, 51))
                ));
                tocEntryCell.setPadding(8);
                tocEntryCell.setBorder(Rectangle.BOTTOM);
                tocEntryCell.setBorderColor(new Color(204, 204, 204));
                tocEntryCell.setBackgroundColor(new Color(250, 250, 250));
                tocTable.addCell(tocEntryCell);

                // Get the stored page number from the map and add the offset
                Integer pageNumber = journalPageNumbers.get(journal.getTitle());
                int adjustedPageNumber = pageNumber != null ? pageNumber : 0;
                log.info("TOC entry - Journal: '{}', Original page: {}, Adjusted page: {}", 
                    journal.getTitle(), pageNumber, adjustedPageNumber);
                
                PdfPCell pageCell = new PdfPCell(new Phrase(
                    adjustedPageNumber > 0 ? String.valueOf(adjustedPageNumber) : "N/A",
                    new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(51, 51, 51))
                ));
                pageCell.setPadding(8);
                pageCell.setBorder(Rectangle.BOTTOM);
                pageCell.setBorderColor(new Color(204, 204, 204));
                pageCell.setBackgroundColor(new Color(250, 250, 250));
                pageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tocTable.addCell(pageCell);
            }

            document.add(tocTable);
        }

        document.close();
        log.info("User details PDF completed");
        return baos;
    }
    
    // Table header cell helper method
    private void addTableHeaderCell(PdfPTable table, String text, Color color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, 
            new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(color);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    // Helper methods for government ID card style
    private void drawSecurityPattern(PdfWriter writer, PdfPCell cell) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        cb.setColorStroke(new Color(200, 200, 220, 40));
        cb.setLineWidth(0.5f);
        
        // Draw a guilloche-like pattern
        float centerX = PageSize.A4.getWidth() / 2;
        float centerY = PageSize.A4.getHeight() - 250;
        
        for (int i = 0; i < 360; i += 10) {
            float angle1 = (float) Math.toRadians(i);
            float angle2 = (float) Math.toRadians((i + 90) % 360);
            
            float x1 = centerX + 40 * (float) Math.cos(angle1);
            float y1 = centerY + 40 * (float) Math.sin(angle1);
            float x2 = centerX + 40 * (float) Math.cos(angle2);
            float y2 = centerY + 40 * (float) Math.sin(angle2);
            
            cb.moveTo(x1, y1);
            cb.lineTo(x2, y2);
        }
        cb.stroke();
        cb.restoreState();
    }
    
    private String generateIdNumber(String userName) {
        // Create a deterministic ID based on username
        return userName;
    }
    
    private String formatDate(String dateString) {
        // Format date in a more readable form
        // This is a simple implementation - could be enhanced for proper parsing
        if (dateString == null || dateString.isEmpty()) {
            return "Unknown";
        }
        
        // Simple formatting assuming format like "yyyy-MM-dd"
        String[] parts = dateString.split("-");
        if (parts.length >= 3) {
            return parts[2] + " " + getMonthName(parts[1]) + " " + parts[0];
        }
        
        return dateString;
    }
    
    private String getMonthName(String monthNumber) {
        switch(monthNumber) {
            case "01": return "JAN";
            case "02": return "FEB";
            case "03": return "MAR";
            case "04": return "APR";
            case "05": return "MAY";
            case "06": return "JUN";
            case "07": return "JUL";
            case "08": return "AUG";
            case "09": return "SEP";
            case "10": return "OCT";
            case "11": return "NOV";
            case "12": return "DEC";
            default: return monthNumber;
        }
    }
    
    private String calculateExpiryDate(String issueDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date issueDate = sdf.parse(issueDateStr);
            
            // Add 5 years
            Calendar cal = Calendar.getInstance();
            cal.setTime(issueDate);
            cal.add(Calendar.YEAR, 5);
            
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String generateMicrotext() {
        // Generate repeating microtext for security
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append("VELINQ SECURE OFFICIAL DOCUMENT ");
        }
        return sb.toString();
    }
    
    private void addPassportField(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        
        Paragraph labelPara = new Paragraph(label, labelFont);
        labelPara.setSpacingAfter(2);
        cell.addElement(labelPara);
        
        Paragraph valuePara = new Paragraph(value, valueFont);
        cell.addElement(valuePara);
        
        table.addCell(cell);
    }

    private void mergePDFs(HttpServletResponse response, ByteArrayOutputStream userDetailsPdf, ByteArrayOutputStream journalEntriesPdf) throws IOException {
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, response.getOutputStream());
        document.open();

        PdfReader reader1 = new PdfReader(userDetailsPdf.toByteArray());
        PdfReader reader2 = new PdfReader(journalEntriesPdf.toByteArray());

        int n1 = reader1.getNumberOfPages();
        int n2 = reader2.getNumberOfPages();

        log.info("Merging PDFs: User Details has {} pages, Journal Entries has {} pages", n1, n2);

        for (int i = 1; i <= n1; i++) {
            copy.addPage(copy.getImportedPage(reader1, i));
        }

        for (int i = 1; i <= n2; i++) {
            copy.addPage(copy.getImportedPage(reader2, i));
        }

        document.close();
        reader1.close();
        reader2.close();
    }

    private static class RoundedCornersCellEvent implements PdfPCellEvent {
        private float radius;
        private Color borderColor;

        public RoundedCornersCellEvent(float radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
            
            // Skip drawing the border if borderColor is null
            if (borderColor != null) {
                canvas.setColorStroke(borderColor);
                canvas.setLineWidth(0.5f);
                canvas.roundRectangle(
                    position.getLeft() + 1,
                    position.getBottom() + 1,
                    position.getWidth() - 2,
                    position.getHeight() - 2,
                    radius
                );
                canvas.stroke();
            } else {
                // Just fill with background color to maintain rounded corners
                Color bgColor = cell.getBackgroundColor();
                if (bgColor != null) {
                    canvas.setColorFill(bgColor);
                    canvas.roundRectangle(
                        position.getLeft() + 1,
                        position.getBottom() + 1,
                        position.getWidth() - 2,
                        position.getHeight() - 2,
                        radius
                    );
                    canvas.fill();
                }
            }
        }
    }

    // Shadow effect for cards
    private static class ShadowCellEvent implements PdfPCellEvent {
        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
            canvas.saveState();
            canvas.setColorFill(new Color(0, 0, 0, 20)); // Very light black for shadow
            canvas.roundRectangle(
                position.getLeft() + 3,     // Offset to the right
                position.getBottom() - 3,   // Offset downward
                position.getWidth() - 5,    // Slightly smaller
                position.getHeight() - 5,   // Slightly smaller
                20f                         // Match the corner radius
            );
            canvas.fill();
            canvas.restoreState();
        }
    }
}

class RomanNumeralPageEventHelper extends PdfPageEventHelper {
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setColorStroke(new Color(0, 51, 102));
            canvas.setLineWidth(1f);
            canvas.moveTo(40, 40);
            canvas.lineTo(555, 40);
            canvas.stroke();
            
            canvas.beginText();
            canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 8);
            canvas.setColorFill(new Color(102, 102, 102));
            canvas.showTextAligned(Element.ALIGN_CENTER, "Page " + toRoman(writer.getPageNumber()), 297.5f, 30, 0);
            canvas.endText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toRoman(int number) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if (number <= 0 || number > 10) return String.valueOf(number);
        return romanNumerals[number - 1];
    }
}

class RegularPageEventHelper extends PdfPageEventHelper {
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setColorStroke(new Color(0, 51, 102));
            canvas.setLineWidth(1f);
            canvas.moveTo(40, 40);
            canvas.lineTo(555, 40);
            canvas.stroke();
            
            canvas.beginText();
            canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 8);
            canvas.setColorFill(new Color(102, 102, 102));
            canvas.showTextAligned(Element.ALIGN_CENTER, "Page " + writer.getPageNumber(), 297.5f, 30, 0);
            canvas.endText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
