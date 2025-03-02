package com.rahul.journal_app.service;

import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.repository.AttachmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AttachmentServiceImpl implements AttachmentService{

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
       String fileName = StringUtils.cleanPath(file.getOriginalFilename());
       try{
            if(fileName.contains("..")){
                throw new Exception("File contains invalid path sequence" + fileName);
            }

            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setFileType(file.getContentType());
            attachment.setData(file.getBytes());

            return attachmentRepository.save(attachment);
       }catch (Exception e){
           throw new Exception("Could not save File: "+fileName);
       }
    }

    @Override
    public Attachment getAttachment(ObjectId fileId) throws Exception {
        try{
            return attachmentRepository.findById(fileId).get();
        }catch (Exception e){
            log.info("File not found with id: {}", fileId);
            throw new Exception("File not found with id: "+fileId);
        }
    }
}
