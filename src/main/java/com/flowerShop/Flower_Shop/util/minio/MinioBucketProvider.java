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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
@Slf4j
@NoArgsConstructor
public class MinioBucketProvider {

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private static final int unknownObjectSize = -1;

    private static final int unknownPartSize = 10485760;

    private static final String PATH_FOR_FLOWERS = "./flowers/";

    public void addFileInBucket(byte[] fileLikeByteArray, String nameOfFile) {
        if (fileLikeByteArray.length != 0) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileLikeByteArray)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(nameOfFile)
                        .stream(byteArrayInputStream, unknownObjectSize, unknownPartSize)
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

    public void updateFileFromBucket(String oldName, String newName) throws IOException {
        if (!Objects.equals(oldName, newName)) {
            addFileInBucket(Files.readAllBytes(Path.of(PATH_FOR_FLOWERS + oldName)), newName);
            deleteFileFromBucket(oldName);
        }
    }
}
