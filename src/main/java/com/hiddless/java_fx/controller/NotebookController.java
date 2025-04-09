package com.hiddless.java_fx.controller;
import com.hiddless.java_fx.dto.NotebookDTO;
import com.hiddless.java_fx.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class NotebookController {
    @FXML
    public TextField titleField;

    @FXML
    public TextArea contentArea;

    @FXML
    public CheckBox pinnedCheckBox;

    @FXML
    public Button btnSaveNote;

    private NotebookDTO createdNote;

    public void setCreatedNote(NotebookDTO createdNote) {
        this.createdNote = createdNote;
    }

    @FXML
    private void saveNote(ActionEvent event) {
        System.out.println("Not Başarıyla Kaydedildi");

        createdNote = NotebookDTO.builder()
                .title(titleField.getText())
                .content(contentArea.getText())
                .pinned(pinnedCheckBox.isSelected())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        System.out.println("Note succesfully saved");
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public NotebookDTO getCreatedNote() {

        System.out.println(createdNote.toString());
        //  setCreatedNote(this.createdNote);

        return createdNote;
    }

    public void setUser(UserDTO currentUser) {
        createdNote.setUserDTO(currentUser);
    }
}