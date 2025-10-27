package com.lcsk42.frameworks.starter.file.core.service;

import com.lcsk42.frameworks.starter.common.util.time.LocalDateTimeUtil;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.file.core.config.FileUploadProperties;
import com.lcsk42.frameworks.starter.file.core.enums.FileUploadType;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定义文件管理操作，包括上传、下载、删除和重命名。 支持临时和永久文件存储，采用基于日期的目录组织方式。
 */
public interface FileService {

    String TMP_DIR = "tmp/";

    /**
     * 配置并返回具有指定属性的 FileService 实例。
     *
     * @param properties 文件操作的配置属性
     * @return 配置好的 FileService 实现
     */
    FileService of(FileUploadProperties properties);

    /**
     * 获取当前文件服务使用的存储类型。
     *
     * @return 文件存储类型（定义在 FileUploadType 枚举中）
     */
    FileUploadType getFileUploadType();

    /**
     * 获取当前文件上传配置。
     *
     * @return 包含配置详情的 FileUploadProperties 对象
     */
    FileUploadProperties getUploadProperties();

    /**
     * 根据当前日期生成目录路径（格式为 yyyyMMdd）。
     *
     * @return 基于日期格式化的目录路径字符串
     */
    default String generateDateBasedDirectory() {
        return LocalDateTimeUtil.PURE_DATE.format(LocalDateTime.now());
    }

    /**
     * 构建完整的文件路径，包含目录结构、时间戳和文件名。 临时文件前缀为 TMP_DIR："[TMP_DIR]/yyyyMMdd/epochSecond-filename"
     * 永久文件格式为："yyyyMMdd/epochSecond-filename"
     *
     * @param fileName 要包含在路径中的原始文件名
     * @param temporary true 表示临时文件，false 表示永久文件
     * @return 用于文件存储的完整路径字符串
     * @throws IllegalArgumentException 如果文件名为空或 null
     */
    default String buildKey(String fileName, boolean temporary) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name must not be blank or null");
        }

        return (temporary ? TMP_DIR : StringUtils.EMPTY) + generateDateBasedDirectory()
                + StringConstant.SLASH
                + Instant.now().getEpochSecond() + StringConstant.DASHED + fileName;
    }

    /**
     * 从指定输入流上传文件到默认存储桶。
     *
     * @param inputStream 要上传的源数据流
     * @param fileName 分配给上传文件的名称
     * @return 上传文件的存储键/路径
     */
    default String uploadFile(InputStream inputStream, String fileName) {
        return uploadFile(inputStream, fileName, getUploadProperties().getBucketName());
    }

    /**
     * 上传文件到指定存储桶。
     *
     * @param inputStream 要上传的源数据流
     * @param fileName 分配给上传文件的名称
     * @param bucketName 目标存储桶名称
     * @return 上传文件的存储键/路径
     */
    default String uploadFile(InputStream inputStream, String fileName, String bucketName) {
        return uploadFile(inputStream, fileName, bucketName, false);
    }

    /**
     * 上传临时文件到默认存储桶。
     *
     * @param inputStream 要上传的源数据流
     * @param fileName 分配给上传文件的名称
     * @return 临时文件的存储键/路径
     */
    default String uploadTemporaryFile(InputStream inputStream, String fileName) {
        return uploadTemporaryFile(inputStream, fileName, getUploadProperties().getBucketName());
    }

    /**
     * 上传临时文件到指定存储桶。
     *
     * @param inputStream 要上传的源数据流
     * @param fileName 分配给上传文件的名称
     * @param bucketName 目标存储桶名称
     * @return 临时文件的存储键/路径
     */
    default String uploadTemporaryFile(InputStream inputStream, String fileName,
            String bucketName) {
        return uploadFile(inputStream, fileName, bucketName, true);
    }

    /**
     * 核心文件上传操作，处理临时和永久存储。
     *
     * @param inputStream 要上传的源数据流
     * @param fileName 分配给上传文件的名称
     * @param bucketName 目标存储桶名称
     * @param temporary true 表示作为临时文件存储，false 表示永久存储
     * @return 上传文件的存储键/路径
     */
    String uploadFile(InputStream inputStream, String fileName, String bucketName,
            boolean temporary);

    /**
     * 将文件从当前位置复制到新路径。
     *
     * @param oldKey 文件的当前存储键/路径
     * @param fileName 分配给复制文件的新名称
     * @param bucketName 包含源文件的存储桶
     * @return 新文件副本的存储键/路径
     */
    default String copyFile(String oldKey, String fileName, String bucketName) {
        InputStream inputStream = downloadFile(oldKey, bucketName);
        return uploadFile(inputStream, fileName);
    }

    /**
     * 从默认存储桶删除文件。
     *
     * @param key 要删除的文件的存储键/路径
     */
    default void deleteFile(String key) {
        deleteFile(key, getUploadProperties().getBucketName());
    }

    /**
     * 从指定存储桶删除文件。
     *
     * @param key 要删除的文件的存储键/路径
     * @param bucketName 包含文件的存储桶
     */
    void deleteFile(String key, String bucketName);

    /**
     * 从默认存储桶删除多个文件。
     *
     * @param keys 要删除的存储键/路径集合
     */
    default void deleteFiles(List<String> keys) {
        deleteFiles(keys, getUploadProperties().getBucketName());
    }

    /**
     * 从指定存储桶删除多个文件。
     *
     * @param keys 要删除的存储键/路径集合
     * @param bucketName 包含文件的存储桶
     */
    default void deleteFiles(List<String> keys, String bucketName) {
        keys.forEach(key -> deleteFile(key, bucketName));
    }

    /**
     * 重命名默认存储桶中的文件。
     *
     * @param oldKey 文件的当前存储键/路径
     * @param newFileName 分配给文件的新名称
     */
    default void renameFile(String oldKey, String newFileName) {
        renameFile(oldKey, newFileName, getUploadProperties().getBucketName());
    }

    /**
     * 重命名指定存储桶中的文件。
     *
     * @param oldKey 文件的当前存储键/路径
     * @param newFileName 分配给文件的新名称
     * @param bucketName 包含文件的存储桶
     */
    void renameFile(String oldKey, String newFileName, String bucketName);

    /**
     * 从默认存储桶下载文件。
     *
     * @param key 要下载的文件的存储键/路径
     * @return 包含文件数据的 InputStream
     */
    default InputStream downloadFile(String key) {
        return downloadFile(key, getUploadProperties().getBucketName());
    }

    /**
     * 从指定存储桶下载文件。
     *
     * @param key 要下载的文件的存储键/路径
     * @param bucketName 包含文件的源存储桶
     * @return 包含文件数据的 InputStream
     */
    InputStream downloadFile(String key, String bucketName);

    /**
     * 为安全文件下载生成有时间限制的预签名 URL。
     * <p>
     * URL 将在默认超时期限后过期。调用方应对过期的 URL 实现适当的错误处理。
     * </p>
     *
     * @param key 要生成 URL 的文件标识符
     * @param bucketName 源存储桶名称
     * @param signatureDuration URL 过期前的持续时间
     * @return 用于直接下载访问的预签名 HTTPS URL
     * @throws UnsupportedOperationException 如果具体类未实现此功能
     */
    default URL generatePreSignedDownloadUrl(String key, String bucketName,
            Duration signatureDuration) {
        throw new UnsupportedOperationException("Pre-signed URL generation not implemented");
    }

    /**
     * 为安全文件上传生成有时间限制的预签名 URL。
     * <p>
     * 生成的 URL 允许直接上传，无需额外权限。
     * </p>
     *
     * @param key 文件将要存储的目标路径
     * @param bucketName 目标存储桶名称
     * @param signatureDuration URL 过期前的持续时间
     * @return 用于直接上传的预签名 HTTPS URL
     * @throws UnsupportedOperationException 如果具体类未实现此功能
     */
    default URL generatePreSignedUploadUrl(String key, String bucketName,
            Duration signatureDuration) {
        throw new UnsupportedOperationException("Pre-signed URL generation not implemented");
    }
}
