package com.lcsk42.frameworks.starter.file.aws.service;

import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.file.core.config.FileUploadProperties;
import com.lcsk42.frameworks.starter.file.core.service.FileService;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@NoArgsConstructor
public class AwsS3FileService implements FileService {

  private FileUploadProperties properties;

  private S3Client s3Client;

  private S3Presigner preSigner;

  public AwsS3FileService(FileUploadProperties properties) {
    this.properties = properties;
    AwsBasicCredentials credentials =
        AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getAccessKeySecret());
    StaticCredentialsProvider staticCredentialsProvider =
        StaticCredentialsProvider.create(credentials);
    Region region = Region.of(properties.getRegion());
    this.s3Client =
        S3Client.builder().region(region).serviceConfiguration(s -> s.chunkedEncodingEnabled(false))
            .credentialsProvider(staticCredentialsProvider)
            .endpointOverride(URI.create(properties.getEndpoint())).forcePathStyle(true).build();
    this.preSigner = S3Presigner.builder().region(region)
        .serviceConfiguration(S3Configuration.builder().chunkedEncodingEnabled(true)
            .pathStyleAccessEnabled(true).build())
        .credentialsProvider(staticCredentialsProvider)
        .endpointOverride(URI.create(properties.getEndpoint())).build();

  }

  @Override
  public FileService of(FileUploadProperties properties) {
    return new AwsS3FileService(properties);
  }

  @Override
  public FileUploadProperties getUploadProperties() {
    return properties;
  }

  @Override
  public String uploadFile(InputStream inputStream, String fileName, String bucketName,
      boolean temporary) {

    String key = buildKey(fileName, temporary);

    PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key).build();

    File tempFile = null;
    try {
      tempFile = File.createTempFile("upload-", ".tmp");
      tempFile.deleteOnExit();

      try (FileOutputStream out = new FileOutputStream(tempFile)) {
        IoUtils.copy(inputStream, out);
      }
      InputStream fileStream = new FileInputStream(tempFile);

      s3Client.putObject(request, RequestBody.fromInputStream(fileStream, tempFile.length()));
    } catch (IOException exception) {
      throw new ServiceException("File upload failed: " + fileName);
    } finally {
      FileUtils.deleteQuietly(tempFile);
    }
    return key;
  }

  @Override
  public String copyFile(String oldKey, String fileName, String bucketName) {
    String key = buildKey(fileName, false);

    CopyObjectRequest request =
        CopyObjectRequest.builder().sourceBucket(bucketName).sourceKey(oldKey)
            .destinationBucket(properties.getBucketName()).destinationKey(key).build();
    s3Client.copyObject(request);

    return key;
  }

  @Override
  public void deleteFile(String key, String bucketName) {
    DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
    s3Client.deleteObject(request);
  }

  @Override
  public void deleteFiles(List<String> keys, String bucketName) {
    List<ObjectIdentifier> objects =
        keys.stream().map(key -> ObjectIdentifier.builder().key(key).build()).toList();

    DeleteObjectsRequest request = DeleteObjectsRequest.builder().bucket(bucketName)
        .delete(builder -> builder.objects(objects)).build();
    s3Client.deleteObjects(request);
  }

  @Override
  public void renameFile(String oldKey, String newFileName, String bucketName) {

    CopyObjectRequest copyRequest =
        CopyObjectRequest.builder().sourceBucket(bucketName).sourceKey(oldKey)
            .destinationBucket(bucketName).destinationKey(buildKey(newFileName, false)).build();
    s3Client.copyObject(copyRequest);

    DeleteObjectRequest deleteRequest =
        DeleteObjectRequest.builder().bucket(bucketName).key(oldKey).build();
    s3Client.deleteObject(deleteRequest);
  }

  @Override
  public InputStream downloadFile(String key, String bucketName) {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();

    return s3Client.getObject(request);
  }

  @Override
  public URL generatePreSignedDownloadUrl(String key, String bucketName,
      Duration signatureDuration) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(key).build();

    return preSigner.presignGetObject(GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest).signatureDuration(signatureDuration).build()).url();
  }

  @Override
  public URL generatePreSignedUploadUrl(String key, String bucketName, Duration signatureDuration) {
    PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();

    return preSigner.presignPutObject(PutObjectPresignRequest.builder().putObjectRequest(putRequest)
        .signatureDuration(signatureDuration).build()).url();
  }
}
