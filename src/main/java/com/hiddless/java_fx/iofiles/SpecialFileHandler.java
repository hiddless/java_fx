package com.hiddless.java_fx.iofiles;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
                logger.info("The file already exists: " + filePath);
            } else {
                if (file.createNewFile()) {
                    logger.info("A new file was created: " + filePath);
                } else {
                    logger.warning("Could not create the file: " + filePath);
                }
            }
        } catch (IOException e) {
            logger.severe("Error creating the file! File path: " + filePath + " - Error: " + e.getMessage());
        }
    }

    public void writeFile(String data) {
        if (data == null || data.trim().isEmpty()) {
            logger.warning("Empty data cannot be written! File path: " + filePath);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
            logger.info("Data successfully written to the file: " + filePath);
        } catch (IOException e) {
            logger.severe("Error writing to the file! File path: " + filePath + " - Error: " + e.getMessage());
        }
    }

    public List<String> readFile() {
        File file = new File(filePath);
        List<String> fileLines = new ArrayList<>();

        if (!file.exists()) {
            logger.warning("The file to read was not found: " + filePath);
            return fileLines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            logger.info("Reading file content...");
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e) {
            logger.severe("Error reading the file! File path: " + filePath + " - Error: " + e.getMessage());
        }

        if (fileLines.isEmpty()) {
            logger.warning("File was read, but it contains no data.");
        } else {
            logger.info("Successfully read " + fileLines.size() + " lines from the file.");
        }

        return fileLines;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.warning("Invalid file path! Default file name is assigned: default.txt");
            this.filePath = "default.txt";
        } else {
            this.filePath = filePath;
        }
    }
}

