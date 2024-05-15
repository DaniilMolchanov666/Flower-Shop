
package com.flowerShop.Flower_Shop.config;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@PropertySource("application.yaml")
@Slf4j
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.secure}")
    private Boolean minioSecure;

    @Bean
    public MinioClient getConnectionToMinio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioUrl, 9000, minioSecure)
                .credentials(accessKey, secretKey)
                .build();
        boolean isExistBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket("flowers").build());
        if (!isExistBucket) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("flowers").build());
        }

        Iterable<Result<Item>> listOfFiles = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("flowers")
                .build());

        for (Result<Item> r: listOfFiles) {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket("flowers")
                            .overwrite(true)
                            .object(r.get().objectName())
                            .filename("./flowers/" + r.get().objectName())
                            .build());
        }

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("flowers")
                        .object("без фото.jpg")
                        .filename("./flowers/без фото.jpg")
                        .build());
        return minioClient;
    }
}
