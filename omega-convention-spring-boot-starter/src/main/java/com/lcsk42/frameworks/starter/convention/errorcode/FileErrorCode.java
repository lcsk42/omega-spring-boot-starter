package com.lcsk42.frameworks.starter.convention.errorcode;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode {

    FILE_NOT_FOUND(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(1),
            "File Not Found"),
    FILE_IS_EMPTY(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(2),
            "File Is Empty"),
    FILE_NAME_ILLEGAL(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(3),
            "File Name Illegal"),
    FILE_PATH_ILLEGAL(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(4),
            "File Path Illegal"),
    FILE_EXTENSION_MISSING(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(5),
            "File Extension Missing"),
    FILE_EXTENSION_MISMATCH(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(6),
            "File Extension Mismatch"),
    FILE_TYPE_NOT_ALLOWED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(7),
            "File Type Not Allowed"),
    FILE_SIZE_EXCEEDED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(8),
            "File Size Exceeded"),
    FILE_TOTAL_SIZE_EXCEEDED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(9),
            "File Total Size Exceeded"),
    FILE_COUNT_EXCEEDED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(10),
            "File Count Exceeded"),
    FILE_ALREADY_EXISTS(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(11),
            "File Already Exists"),
    FILE_ACCESS_DENIED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(12),
            "File Access Denied"),
    FILE_DOWNLOAD_FORBIDDEN(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(13),
            "File Download Forbidden"),
    FILE_UPLOAD_INTERRUPTED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(14),
            "File Upload Interrupted"),
    FILE_EXPIRED(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(15),
            "File Expired"),
    FILE_ID_INVALID(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(16),
            "File Id Invalid"),


    FILE_IO_EXCEPTION(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(1),
            "File Io Exception"),
    FILE_UPLOAD_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(2),
            "File Upload Failed"),
    FILE_DOWNLOAD_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(3),
            "File Download Failed"),
    FILE_READ_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(4),
            "File Read Failed"),
    FILE_WRITE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(5),
            "File Write Failed"),
    FILE_SAVE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(6),
            "File Save Failed"),
    FILE_DELETE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(7),
            "File Delete Failed"),
    FILE_COPY_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(8),
            "File Copy Failed"),
    FILE_MOVE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(9),
            "File Move Failed"),
    FILE_COMPRESS_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(10),
            "File Compress Failed"),
    FILE_DECOMPRESS_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(11),
            "File Decompress Failed"),
    FILE_PARSE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(12),
            "File Parse Failed"),
    FILE_FORMAT_INVALID(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(13),
            "File Format Invalid"),
    FILE_MD5_MISMATCH(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(14),
            "File Md5 Mismatch"),
    FILE_CHECKSUM_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(15),
            "File Checksum Failed"),
    FILE_STORAGE_UNAVAILABLE(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(16),
            "File Storage Unavailable"),
    FILE_STORAGE_FULL(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(17),
            "File Storage Full"),
    FILE_UPLOAD_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(18),
            "File Upload Timeout"),
    FILE_DOWNLOAD_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(19),
            "File Download Timeout"),
    FILE_STREAM_CLOSE_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(20),
            "File Stream Close Failed"),

    REMOTE_FILE_FETCH_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(1),
            "Remote File Fetch Failed"),
    OSS_UPLOAD_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(2),
            "Oss Upload Failed"),
    OSS_DOWNLOAD_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(3),
            "Oss Download Failed"),
    OSS_DELETE_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(4),
            "Oss Delete Failed"),
    OSS_BUCKET_NOT_FOUND(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(5),
            "Oss Bucket Not Found"),
    OSS_ACCESS_DENIED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(6),
            "Oss Access Denied"),
    OSS_TIMEOUT(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(7),
            "Oss Timeout"),
    FTP_OPERATION_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(8),
            "Ftp Operation Failed"),
    SFTP_OPERATION_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.FILE,
            ErrorNumber.of(9),
            "Sftp Operation Failed"),
            ;

    /**
     * 错误来源（客户端/服务端/远程）。
     */
    private final ErrorSourceEnum errorSourceEnum;

    /**
     * 错误发生的业务域。
     */
    private final BusinessDomainEnum businessDomainEnum;

    /**
     * 业务域内的唯一错误编号。
     */
    private final ErrorNumber errorNumber;

    /**
     * 人类可读的错误消息。
     * <p>
     * 应提供足够的排错上下文信息，同时确保可以安全暴露给客户端。
     * </p>
     */
    private final String message;
}
