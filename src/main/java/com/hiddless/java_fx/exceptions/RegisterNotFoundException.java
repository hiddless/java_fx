package com.hiddless.java_fx.exceptions;

public class RegisterNotFoundException extends RuntimeException {

  public RegisterNotFoundException() {
    super("Register Not Found");
  }

  public RegisterNotFoundException(String message) {
        super(message);
    }
}
