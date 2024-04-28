package com.flowerShop.Flower_Shop.util.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class FileManager {

    private static final String PATH_FOR_FLOWERS = "./src/main/resources/flowers/";

    public static void deleteFile(String nameOfFile) {
        if (new File(PATH_FOR_FLOWERS + nameOfFile).exists()) {
            try {
                Files.delete(Paths.get(PATH_FOR_FLOWERS + nameOfFile));
            } catch (IOException e) {
                log.error("Вам не удалось удалить файл {} ({})", nameOfFile, e.getMessage());
            }
        }
    }

    public static void createFileAndSaveInDirectory(MultipartFile file, String name) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(PATH_FOR_FLOWERS + name));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                log.error("Вам не удалось загрузить файл {} => {}", name, e.getMessage());
            }
        } else {
            log.error("Вам не удалось загрузить файл {} => потому что файл пустой!", name);
        }
    }

    public static void deleteOldFileAndSaveNew(MultipartFile newFile, String oldName, String newName) {
        if (newFile != null) {
            if (new File(PATH_FOR_FLOWERS + oldName).exists()) {
                try {
                    Files.delete(Paths.get(PATH_FOR_FLOWERS + oldName));
                } catch (IOException e) {
                    log.error("Вам не удалось удалить файл {} => {}", oldName, e.getMessage());
                }
            }
            createFileAndSaveInDirectory(newFile, newName);
        }
    }
}
