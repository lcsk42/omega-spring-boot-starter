package com.lcsk42.frameworks.starter.file.local.service;

import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.file.core.config.FileUploadProperties;
import com.lcsk42.frameworks.starter.file.core.enums.FileUploadType;
import com.lcsk42.frameworks.starter.file.core.service.FileService;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

@NoArgsConstructor
public class LocalFileService implements FileService {

    private FileUploadProperties properties;

    public LocalFileService(FileUploadProperties properties) {
        this.properties = properties;
    }

    @Override
    public FileService of(FileUploadProperties properties) {
        return new LocalFileService(properties);
    }

    @Override
    public FileUploadType getFileUploadType() {
        return FileUploadType.LOCAL;
    }

    @Override
    public FileUploadProperties getUploadProperties() {
        return properties;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String bucketName,
            boolean temporary) {
        String key = buildKey(fileName, temporary);

        try (inputStream;
                FileOutputStream out =
                        new FileOutputStream(bucketName + StringConstant.SLASH + key)) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        } catch (Exception ignored) {
            throw new ServiceException("File upload failed: " + key);
        }

        return key;
    }

    @Override
    public void deleteFile(String key, String bucketName) {
        File file = new File(bucketName + File.separator + key);
        if (file.exists()) {
            boolean delete = file.delete();
            if (!delete) {
                throw new ServiceException("File deletion failed: " + key);
            }
        }
    }

    @Override
    public void renameFile(String oldKey, String newFileName, String bucketName) {
        File file = new File(oldKey);
        if (file.exists()) {
            boolean rename = file.renameTo(new File(buildKey(newFileName, false)));
            if (!rename) {
                throw new ServiceException("File renaming failed: " + oldKey);
            }
        }
    }

    @Override
    public InputStream downloadFile(String key, String bucketName) {

        File file;
        if (key.startsWith(bucketName)) {
            file = new File(key);
        } else {
            file = new File(bucketName + StringConstant.SLASH + key);
        }

        if (!file.exists()) {
            throw new ServiceException("File not found: " + key);
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ServiceException("File not found: " + key);
        }
    }

    private boolean createDirectoryAndChangeToIt(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdir = file.mkdirs();
            if (!mkdir) {
                throw new ServiceException("Failed to create directory: " + directoryPath);
            }
        }
        return true;
    }

}
