package com.hiddless.java_fx;

import com.hiddless.java_fx.utils.ResourceManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Locale;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.textProperty().bind(ResourceManager.getFactory().getStringBinding("welcome"));
    }

    @FXML
    private void onLanguageChange() {
        String selected = languageComboBox.getValue();
        if ("English".equals(selected)) {
            ResourceManager.changeLanguage(new Locale("en", "US"));
        } else {
            ResourceManager.changeLanguage(new Locale("tr", "TR"));
        }
    }

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button exitButton;

    @FXML
    private Label greetingLabel;


    @FXML
    public void initialize() {
        greetingLabel.textProperty().bind(ResourceManager.getFactory().getStringBinding("greeting"));
        exitButton.textProperty().bind(ResourceManager.getFactory().getStringBinding("exit"));
        welcomeText.textProperty().bind(ResourceManager.getFactory().getStringBinding("welcome"));
        languageComboBox.getItems().addAll("Türkçe", "English");
        languageComboBox.setValue("Türkçe");
    }

}