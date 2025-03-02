package com.rahul.journal_app.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.rahul.journal_app.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PDFGeneratorServiceImpl implements PDFGeneratorService{

    @Autowired
    private UserRepository userRepository;


    @Override
    public void export(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontTitle.setSize(18);
        Paragraph paragraph = new Paragraph("Today is Sunday", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
        fontParagraph.setSize(12);
        Paragraph paragraph2 = new Paragraph("This is a paragraph.", fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(paragraph);
        document.add(paragraph2);
        document.close();
    }
}
