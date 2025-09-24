package com.lcsk42.frameworks.starter.file.aliyun.service;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.lcsk42.frameworks.starter.file.core.config.FileUploadProperties;
import com.lcsk42.frameworks.starter.file.core.service.FileService;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
public class AliyunOssFileService implements FileService {

    private FileUploadProperties properties;

    private OSS ossClient;

    public AliyunOssFileService(FileUploadProperties properties) {
        this.properties = properties;

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        this.ossClient = OSSClientBuilder.create().endpoint(properties.getEndpoint())
                .credentialsProvider(CredentialsProviderFactory
                        .newDefaultCredentialProvider(properties.getAccessKeyId(),
                                properties.getAccessKeySecret()))
                .clientConfiguration(clientBuilderConfiguration).build();
    }

    @Override
    public FileService of(FileUploadProperties properties) {
        return new AliyunOssFileService(properties);
    }

    @Override
    public FileUploadProperties getUploadProperties() {
        return properties;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String bucketName,
            boolean temporary) {
        String key = buildKey(fileName, temporary);
        ossClient.putObject(bucketName, key, inputStream);
        return key;
    }

    @Override
    public String copyFile(String oldKey, String fileName, String bucketName) {
        String key = buildKey(fileName, false);
        ossClient.copyObject(bucketName, oldKey, bucketName, key);
        return key;
    }

    @Override
    public void deleteFile(String key, String bucketName) {
        ossClient.deleteObject(bucketName, key);
    }

    @Override
    public void deleteFiles(List<String> keys, String bucketName) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(keys);
        ossClient.deleteObjects(request);
    }

    @Override
    public void renameFile(String oldKey, String newFileName, String bucketName) {
        String key = buildKey(newFileName, false);
        ossClient.copyObject(bucketName, oldKey, bucketName, key);
        deleteFile(oldKey);
    }

    @Override
    public InputStream downloadFile(String key, String bucketName) {
        return ossClient.getObject(bucketName, key).getObjectContent();
    }

    @Override
    public URL generatePreSignedDownloadUrl(String key, String bucketName,
            Duration signatureDuration) {
        return ossClient.generatePresignedUrl(bucketName, key,
                Date.from(LocalDateTime.now().plus(signatureDuration).atZone(ZoneId.systemDefault())
                        .toInstant()));
    }

    @Override
    public URL generatePreSignedUploadUrl(String key, String bucketName,
            Duration signatureDuration) {
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.PUT);
        request.setExpiration(
                Date.from(LocalDateTime.now().plus(signatureDuration).atZone(ZoneId.systemDefault())
                        .toInstant()));
        return ossClient.generatePresignedUrl(request);
    }
}
