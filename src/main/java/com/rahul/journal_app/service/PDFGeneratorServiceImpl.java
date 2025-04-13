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
        canvas.rectangle(0, PageSize.A4.getHeight() - 40, PageSize.A4.getWidth(), 90);
        canvas.fill();

        canvas.setColorFill(Color.WHITE);
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 24);
        canvas.beginText();
        canvas.showTextAligned(Element.ALIGN_CENTER, "Journals", PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() - 30, 0);
        canvas.endText();

        // Add decorative line
        canvas.setColorStroke(new Color(0, 51, 102));
        canvas.setLineWidth(2f);
        canvas.moveTo(40, PageSize.A4.getHeight() - 60);
        canvas.lineTo(PageSize.A4.getWidth() - 80, PageSize.A4.getHeight() - 60);
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

        // User Details Section
        Font sectionFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 51, 102));
        Paragraph userDetails = new Paragraph("User Profile", sectionFont);
        userDetails.setSpacingBefore(30);
        userDetails.setSpacingAfter(20);
        document.add(userDetails);

        // User Details Table
        PdfPTable userTable = new PdfPTable(2);
        userTable.setWidthPercentage(100);
        userTable.setSpacingBefore(10);
        userTable.setSpacingAfter(30);
        userTable.setWidths(new float[]{1, 2});

        addBankStyleRow(userTable, "Full Name", user.getFirstName() + " " + user.getLastName());
        addBankStyleRow(userTable, "Email", user.getUserName());
        addBankStyleRow(userTable, "Gender", user.getGender());
        addBankStyleRow(userTable, "Date of Birth", user.getDateOfBirth().toString());
        addBankStyleRow(userTable, "Contact", user.getPhoneNo());
        addBankStyleRow(userTable, "Location", user.getCity().toUpperCase() + ", " + user.getCountry().toUpperCase());
        addBankStyleRow(userTable, "Postal Code", user.getPinCode());
        addBankStyleRow(userTable, "Status", user.isVerified() ? "ACTIVE" : "INACTIVE");

        document.add(userTable);

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
                PdfPCell titleCell = new PdfPCell(new Phrase(
                    journal.getTitle(),
                    new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(51, 51, 51))
                ));
                titleCell.setPadding(8);
                titleCell.setBorder(Rectangle.BOTTOM);
                titleCell.setBorderColor(new Color(204, 204, 204));
                titleCell.setBackgroundColor(new Color(250, 250, 250));
                tocTable.addCell(titleCell);

                // Get the stored page number from the map and add the offset
                Integer pageNumber = journalPageNumbers.get(journal.getTitle());
                int adjustedPageNumber = pageNumber != null ? pageNumber  : 0;
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

    private void addBankStyleRow(PdfPTable table, String key, String value) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, 
            new Font(Font.HELVETICA, 10, Font.BOLD, new Color(51, 51, 51))));
        keyCell.setPadding(8);
        keyCell.setBackgroundColor(new Color(250, 250, 250));
        keyCell.setBorder(Rectangle.BOTTOM);
        keyCell.setBorderColor(new Color(204, 204, 204));

        PdfPCell valueCell = new PdfPCell(new Phrase(value, 
            new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(51, 51, 51))));
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(Color.WHITE);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(new Color(204, 204, 204));

        table.addCell(keyCell);
        table.addCell(valueCell);
    }

    private void addTableHeaderCell(PdfPTable table, String text, Color color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, 
            new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(color);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
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
