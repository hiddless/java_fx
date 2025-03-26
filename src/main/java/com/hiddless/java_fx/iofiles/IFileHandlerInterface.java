package com.hiddless.java_fx.iofiles;

import java.io.IOException;

public interface IFileHandlerInterface {

    void createFileIfNotExists() throws IOException;

    void writeFile(String data) throws IOException;

    void readFile() throws IOException;

    default void logInfo(String message) {
        System.out.println("ℹ️ " + message);
    }
}
