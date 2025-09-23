package com.lcsk42.frameworks.starter.file.core.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件上传功能的配置属性 这些属性可在 application.yml/application.properties 中配置，使用前缀 'framework.file.upload'
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(FileUploadProperties.PREFIX)
public class FileUploadProperties {
  /**
   * 文件上传属性的配置前缀 在属性文件中的使用示例： framework.file.upload.fileUploadType=LOCAL
   */
  public static final String PREFIX = "framework.file.upload";

  /**
   * 云存储服务的终端 URL（例如 S3 终端 URL） 使用云存储服务时必须配置
   */
  private String endpoint;

  /**
   * 存储文件的默认 bucket/container 名称 使用云存储服务时必须配置
   */
  private String bucketName;

  /**
   * 云存储服务的区域标识符 某些云存储提供商需要此配置
   */
  private String region;

  /**
   * 用于云存储服务身份验证的访问密钥 ID 使用云存储服务时必须配置
   */
  private String accessKeyId;

  /**
   * 用于云存储服务身份验证的密钥访问密钥 使用云存储服务时必须配置
   */
  private String accessKeySecret;

  /**
   * 额外的/扩展的配置选项
   */
  private Extra extra;

  /**
   * 包含扩展配置属性的嵌套类
   */
  @Getter
  @Setter
  @NoArgsConstructor
  public static class Extra {
    /**
     * 存储公共文件的 bucket/container 名称 用于为私有和公共文件维护单独的 bucket 时使用
     */
    private String publicBucketName;
  }
}
