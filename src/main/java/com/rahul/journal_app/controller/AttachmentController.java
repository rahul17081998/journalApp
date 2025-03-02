package com.rahul.journal_app.controller;

import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.model.ResponseData;
import com.rahul.journal_app.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/attachment")
@Slf4j
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;


    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment=null;
        String downloadURI = "";
        attachment = attachmentService.saveAttachment(file);
        downloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/attachment/download/")
                .path(attachment.getId().toString())
                .toUriString();

        return new ResponseData(
                attachment.getFileName(),
                downloadURI,
                file.getContentType(),
                file.getSize());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") ObjectId fileId) throws Exception {
        Attachment attachment=null;
        attachment = attachmentService.getAttachment(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attchment; fileName=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

}
