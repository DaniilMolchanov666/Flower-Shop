package com.flowerShop.Flower_Shop.util.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@NoArgsConstructor
public class MinioBucketProvider {

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public void addFileInBucket(byte[] fileLikeByteArray, String nameOfFile) {
        if (fileLikeByteArray.length != 0) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileLikeByteArray)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(nameOfFile)
                        .stream(byteArrayInputStream, -1, 10485760)
                        .build());
            } catch (ErrorResponseException | NoSuchAlgorithmException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | InsufficientDataException | ServerException |
                     XmlParserException e) {
                log.error("не удалось сохранить изображение в minio! {}", e.getMessage());
            }
        }
    }

    public void deleteFileFromBucket(String nameOfFile) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(nameOfFile)
                    .build());
        } catch (ErrorResponseException | NoSuchAlgorithmException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | InsufficientDataException | ServerException |
                 XmlParserException e) {
            log.error("не удалось удалить изображение в minio! {}", e.getMessage());
        }
    }
}
