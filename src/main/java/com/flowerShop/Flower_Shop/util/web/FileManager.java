package com.flowerShop.Flower_Shop.util.web;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
public class FileManager {

    private static final String PATH_FOR_FLOWERS = "./flowers/";

    public static void deleteFile(String nameOfFile) {
        if (new File(PATH_FOR_FLOWERS + nameOfFile).exists()) {
            try {
                Files.delete(Paths.get(PATH_FOR_FLOWERS + nameOfFile));
            } catch (IOException e) {
                log.error("Вам не удалось удалить файл {} ({})", nameOfFile, e.getMessage());
            }
        }
    }

    public static void createFileAndSaveInDirectory(byte[] file, String name) {
        if (file.length > 0) {
            try {
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(PATH_FOR_FLOWERS + name));
                stream.write(file);
                stream.close();
            } catch (Exception e) {
                log.error("Вам не удалось загрузить файл {} => {}", name, e.getMessage());
            }
        } else {
            log.error("Вам не удалось загрузить файл {} => потому что файл пустой!", name);
        }
    }

    public static void deleteOldFileAndSaveNew(byte[] newFile, String oldName, String newName) {
        if (newFile != null) {
            deleteFile(oldName);
            createFileAndSaveInDirectory(newFile, newName);
        }
    }

    public static void renameFile(String oldName, String newName) {
        if (new File(PATH_FOR_FLOWERS + oldName).exists()) {
            try {
                if (!Objects.equals(oldName, newName) && !oldName.equals("без фото.jpg")) {
                    createFileAndSaveInDirectory(Files.readAllBytes(Paths.get(PATH_FOR_FLOWERS + oldName)), newName);
                    deleteFile(oldName);
                }
            } catch (IOException e) {
                log.error("Вам не удалось обновить файл {} ({})", oldName, e.getMessage());
            }
        }
    }
}
