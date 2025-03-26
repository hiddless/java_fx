package com.hiddless.java_fx.iofiles;

import com.hiddless.java_fx.utils.SpecialColor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpecialFileHandler {

    private static final Logger logger = Logger.getLogger(SpecialFileHandler.class.getName());
    private String filePath;

    public SpecialFileHandler() {
        this.filePath = "default.txt";
    }

    public void createFileIfNotExists() {
        File file = new File(filePath);
        try {
            if (file.exists()) {
                logger.info("File already exists: " + filePath);
            } else {
                if (file.createNewFile()) {
                    System.out.println(SpecialColor.GREEN+"New file created: " + filePath+SpecialColor.RESET);
                } else {
                    logger.warning("Failed to create file: " + filePath);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File creation error: " + e.getMessage(), e);
        }
    }

    public void writeFile(String data) {
        if (data == null || data.trim().isEmpty()) {
            logger.warning("Empty data cannot be written!");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
            logger.info(" Data successfully written to file: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, " Error writing to file: " + e.getMessage(), e);
        }
    }

    public List<String> readFile() {
        File file = new File(filePath);
        List<String> fileLines = new ArrayList<>();

        if (!file.exists()) {
            logger.warning("No file found to read: " + filePath);
            return fileLines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            logger.info("Reading file content...");
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File reading error: " + e.getMessage(), e);
        }

        if (fileLines.isEmpty()) {
            logger.warning("The file is read but the content is empty.");
        } else {
            logger.info("From File " + fileLines.size() + " line read successfully.");
        }

        return fileLines;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.warning("Invalid file path! Setting default file name: default.txt");
            this.filePath = "default.txt";
        } else {
            this.filePath = filePath;
        }
    }
}