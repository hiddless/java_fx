package com.hiddless.java_fx.iofiles;

public interface IFileHandlerInterface {

    void createFileIfNotExists();
    void writeFile(String data);
    void readFile();
    default void logInfo(String message) {
        System.out.println("Info: " + message);
    }
}
