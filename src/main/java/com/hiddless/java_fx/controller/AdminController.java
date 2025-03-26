package com.hiddless.java_fx.controller;

import com.hiddless.java_fx.dao.UserDAO;
import com.hiddless.java_fx.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AdminController {
    private UserDAO userDAO;

    public AdminController() {
        userDAO = new UserDAO();
    }

    @FXML
    private TableView<UserDTO> userTable;

    @FXML
    private TableColumn<UserDTO, Integer> idColumn;

    @FXML
    private TableColumn<UserDTO, String> usernameColumn;

    @FXML
    private TableColumn<UserDTO, String> emailColumn;

    @FXML
    private TableColumn<UserDTO, String> passwordColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String password, boolean empty) {
                super.updateItem(password, empty);
                setText((empty || password == null) ? null : "******");
            }
        });

        refreshTable();
    }

    @FXML
    private void refreshTable() {
        Optional<List<UserDTO>> optionalUsers = userDAO.list();
        List<UserDTO> userList = optionalUsers.orElseGet(List::of);
        ObservableList<UserDTO> userObservableList = FXCollections.observableArrayList(userList);
        userTable.setItems(userObservableList);
        showAlert("Information", "Table successfully refreshed!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Confirm your action.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hiddless/java_fx/view/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to redirect to login page!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void addUser(ActionEvent actionEvent) {
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add New User");
        usernameDialog.setHeaderText("Enter Username:");
        Optional<String> optionalUsername = usernameDialog.showAndWait();

        if (optionalUsername.isPresent()) {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Set Password");
            passwordDialog.setHeaderText("Enter Password:");
            Optional<String> optionalPassword = passwordDialog.showAndWait();

            if (optionalPassword.isPresent()) {
                TextInputDialog emailDialog = new TextInputDialog();
                emailDialog.setTitle("Set Email");
                emailDialog.setHeaderText("Enter Email:");
                Optional<String> optionalEmail = emailDialog.showAndWait();

                if (optionalEmail.isPresent()) {
                    UserDTO newUser = new UserDTO(0, optionalUsername.get(), optionalPassword.get(), optionalEmail.get());
                    userDAO.create(newUser).ifPresent(user -> {
                        showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
                        refreshTable();
                    });
                }
            }
        }
    }
    @FXML
    public void deleteUser(ActionEvent actionEvent) {
        UserDTO selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to delete this user?");
            confirmationAlert.setContentText("User: " + selectedUser.getUsername());

            Optional<ButtonType> isDelete = confirmationAlert.showAndWait();
            if (isDelete.isPresent() && isDelete.get() == ButtonType.OK) {
                userDAO.delete(selectedUser.getId()).ifPresent(user -> {
                    showAlert("Success", "User deleted successfully!", Alert.AlertType.INFORMATION);
                    refreshTable();
                });
            }
        } else {
            showAlert("Warning", "Please select a user to delete.", Alert.AlertType.WARNING);
        }
    }
}
