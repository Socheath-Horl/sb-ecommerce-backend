package com.ecommerce.project.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
}
