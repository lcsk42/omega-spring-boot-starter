package com.lcsk42.frameworks.starter.file.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileUploadType {
  AWS_S3, ALIYUN_OSS, SFTP, LOCAL,;
}
