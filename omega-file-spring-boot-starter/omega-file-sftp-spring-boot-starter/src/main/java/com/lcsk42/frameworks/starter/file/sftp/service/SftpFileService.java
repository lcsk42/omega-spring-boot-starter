package com.lcsk42.frameworks.starter.file.sftp.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.lcsk42.frameworks.starter.convention.exception.ServiceException;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import com.lcsk42.frameworks.starter.file.core.config.FileUploadProperties;
import com.lcsk42.frameworks.starter.file.core.enums.FileUploadType;
import com.lcsk42.frameworks.starter.file.core.service.FileService;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@NoArgsConstructor
public class SftpFileService implements FileService {

    private FileUploadProperties properties;

    private ChannelSftp channelSftp;

    public SftpFileService(FileUploadProperties properties) {
        this.properties = properties;
    }

    @Override
    public FileService of(FileUploadProperties properties) {
        return new SftpFileService(properties);
    }

    @Override
    public FileUploadType getFileUploadType() {
        return FileUploadType.SFTP;
    }

    @Override
    public FileUploadProperties getUploadProperties() {
        return properties;
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String bucketName,
            boolean temporary) {
        String key = buildKey(fileName, temporary);
        channelSftp = getFtpClient();

        String path = StringUtils.substringBeforeLast(key, StringConstant.SLASH);
        String filename = StringUtils.substringAfterLast(key, StringConstant.SLASH);

        try {
            if (createDirectoryAndChangeToIt(bucketName + StringConstant.SLASH + path)) {
                channelSftp.put(inputStream, filename);
            }
        } catch (Exception e) {
            throw new ServiceException("Failed to upload file: %s/%s".formatted(bucketName, key));
        }

        return key;
    }

    @Override
    public void deleteFile(String key, String bucketName) {
        channelSftp = getFtpClient();
        try {
            if (!StringUtils.startsWith(key, bucketName)) {
                channelSftp.cd(bucketName);
            }
            channelSftp.rm(key);
        } catch (Exception e) {
            throw new ServiceException("Failed to delete file: %s/%s".formatted(bucketName, key));
        }
    }

    @Override
    public void renameFile(String oldKey, String newFileName, String bucketName) {
        channelSftp = getFtpClient();
        String newPath = StringUtils.replace(oldKey, FilenameUtils.getName(oldKey), newFileName);

        try {
            if (!StringUtils.startsWith(oldKey, bucketName)) {
                channelSftp.cd(bucketName);
            }
            channelSftp.rename(oldKey, newPath);
        } catch (Exception e) {
            throw new ServiceException(
                    "Failed to rename file: %s/%s".formatted(bucketName, oldKey));
        }
    }

    @Override
    public InputStream downloadFile(String key, String bucketName) {
        channelSftp = getFtpClient();

        try {
            if (!StringUtils.startsWith(key, bucketName)) {
                channelSftp.cd(bucketName);
            }
            return channelSftp.get(key);
        } catch (Exception e) {
            throw new ServiceException("Failed to download file: %s/%s".formatted(bucketName, key));
        }
    }

    private ChannelSftp getFtpClient() {
        if (Objects.nonNull(channelSftp) && channelSftp.isConnected()) {
            return channelSftp;
        }

        String endpoint = properties.getEndpoint();
        String[] split = StringUtils.split(endpoint, StringConstant.COLON);

        String hostname = split[0];
        int port = StringUtils.isNumeric(split[1]) ? Integer.parseInt(split[1]) : 22;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(properties.getAccessKeyId(), hostname, port);
            session.setPassword(properties.getAccessKeySecret());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(60_000);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            return (ChannelSftp) channel;
        } catch (Exception e) {
            throw new ServiceException(
                    "Connection to FTP server failed: %s@%s:%s"
                            .formatted(properties.getAccessKeyId(), hostname, port));
        }
    }

    private boolean isDirectoryExist(String directoryPath) {
        try {
            channelSftp = getFtpClient();
            channelSftp.lstat(directoryPath);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new ServiceException("Failed to check directory existence: " + directoryPath);
        }
    }

    private boolean createDirectoryAndChangeToIt(String directoryPath) {
        channelSftp = getFtpClient();
        String[] directories = directoryPath.split(StringConstant.SLASH);
        StringBuilder currentPath = new StringBuilder();
        try {
            for (String dir : directories) {
                if (dir.isEmpty()) {
                    continue;
                }
                currentPath.append(dir).append(StringConstant.SLASH);
                String path = currentPath.toString();

                if (!isDirectoryExist(path)) {
                    channelSftp.mkdir(path);
                }
                channelSftp.cd(path);
            }
            return true;
        } catch (SftpException ignored) {
            throw new ServiceException("Failed to create directory: " + directoryPath);
        }
    }

}
