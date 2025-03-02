package com.rahul.journal_app.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface PDFGeneratorService {

    void export(HttpServletResponse response) throws IOException;
}
