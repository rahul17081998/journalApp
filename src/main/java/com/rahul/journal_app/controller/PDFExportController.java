package com.rahul.journal_app.controller;

import com.rahul.journal_app.service.PDFGeneratorService;
import com.rahul.journal_app.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/pdf")
@Slf4j
public class PDFExportController {

    @Autowired
    private PDFGeneratorService pdfGeneratorService;

    @Autowired
    private UserService userService;

    @GetMapping("/generate")
    public void generatePDF(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime=dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";

        String userFirstName=userService.findByUserName(username).getFirstName();
        String name="pdf";
        if(userFirstName!=null && !userFirstName.trim().isEmpty()){
            name = userFirstName;
        }
        String headerValue = "attachment; filename="+name +"_"+ currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfGeneratorService.export(response, username);

    }
}
