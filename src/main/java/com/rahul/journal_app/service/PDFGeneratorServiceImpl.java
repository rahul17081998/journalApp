package com.rahul.journal_app.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;

@Service
public class PDFGeneratorServiceImpl implements PDFGeneratorService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public void export(HttpServletResponse response, String userName) throws IOException {
        UserDto user = userService.getUserDetail(userName);
        if(user==null){
            throw new RuntimeException("User not found");
        }

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Set title with user's name
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 102, 204));
        Paragraph title = new Paragraph("Client Report: " + user.getFirstName() + " " + user.getLastName(), titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // User Information Section
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(50, 50, 50));
        Paragraph userInfoTitle = new Paragraph("User Information", sectionFont);
        userInfoTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(userInfoTitle);
        document.add(new Paragraph("\n"));

        Font keyFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
        Font valueFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);

        // Two-column user details layout
        PdfPTable userTable = new PdfPTable(2);
        userTable.setWidthPercentage(100);
        userTable.setSpacingBefore(2);
        userTable.setSpacingAfter(10);

        addTableRow(userTable, "Name:", user.getFirstName() + " " + user.getLastName(), keyFont, valueFont);
        addTableRow(userTable, "Email:", user.getUserName(), keyFont, valueFont);
        addTableRow(userTable, "Gender:", user.getGender(), keyFont, valueFont);
        addTableRow(userTable, "D.O.B:", user.getDateOfBirth().toString(), keyFont, valueFont);
        addTableRow(userTable, "Phone:", user.getPhoneNo(), keyFont, valueFont);
        addTableRow(userTable, "City:", user.getCity().toUpperCase(), keyFont, valueFont);
        addTableRow(userTable, "Pin Code:", user.getPinCode(), keyFont, valueFont);
        addTableRow(userTable, "Country:", user.getCountry().toUpperCase(), keyFont, valueFont);
        addTableRow(userTable, "Verified:", user.isVerified() ? "Yes" : "No", keyFont, valueFont);

        document.add(userTable);

        // Journal Entries Section
        Paragraph journalTitle = new Paragraph("Journal Entries", sectionFont);
        journalTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(journalTitle);
        document.add(new Paragraph("\n"));

        for (var journal : user.getJournalEntities()) {
            // Create a background cell (light grayish blue)
            PdfPCell backgroundCell = new PdfPCell();
            backgroundCell.setPadding(10);
            backgroundCell.setBorder(Rectangle.NO_BORDER);
            backgroundCell.setBackgroundColor(new Color(230, 240, 250)); // Light blueish-gray

            PdfPTable journalTable = new PdfPTable(1);
            journalTable.setWidthPercentage(100);
            journalTable.setSpacingBefore(5);
            journalTable.setSpacingAfter(10);

            // Journal Title
            Font journalTitleFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 102, 204));
            Paragraph journalHeader = new Paragraph(journal.getTitle(), journalTitleFont);

            // Journal Date
            Font dateFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
            Paragraph date = new Paragraph(journal.getDate().toString(), dateFont);
            date.setAlignment(Paragraph.ALIGN_RIGHT);

            // Journal Content
            Font contentFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Paragraph content = new Paragraph(journal.getContent(), contentFont);

            // Sentiment
            Font sentimentFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(50, 150, 50));
            Paragraph sentiment = new Paragraph("Sentiment: " + (journal.getSentiment() != null ? journal.getSentiment().toString() : "NA"), sentimentFont);

            // Add all elements to the background cell
            backgroundCell.addElement(journalHeader);
            backgroundCell.addElement(date);
            backgroundCell.addElement(content);
            backgroundCell.addElement(sentiment);

            journalTable.addCell(backgroundCell);
            document.add(journalTable);
        }
        document.close();
    }

    private void addTableRow(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.BOX);
        keyCell.setBackgroundColor(new Color(240, 240, 240));
        keyCell.setPadding(4);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBackgroundColor(new Color(240, 240, 240));
        valueCell.setPadding(4);

        table.addCell(keyCell);
        table.addCell(valueCell);
    }
}
