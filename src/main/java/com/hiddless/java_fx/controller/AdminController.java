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

    /// ////////////////////////////////////////////////////////////////////////////
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

    /// ////////////////////////////////////////////////////////////////////////////
    ///
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
                if (empty || password == null) {
                    setText(null);
                } else {
                    setText("******");
                }
            }
        });
        refreshTable();
    }

    ///////////////////////////////////////////////////////////////////////////
    @FXML
    private void refreshTable() {

        Optional<List<UserDTO>> optionalUsers = userDAO.list();

        List<UserDTO> userDTOList= optionalUsers.orElseGet(List::of);

        ObservableList<UserDTO> userObservableList= FXCollections.observableArrayList(userDTOList);

        userTable.setItems(userObservableList);

        // Show Alert
        showAlert("Information", "Table refreshed successfully!", Alert.AlertType.INFORMATION);
    }

    ///////////////////////////////////////////////////////////////////////////
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    ///////////////////////////////////////////////////////////////////////////
    @FXML
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Do you want to logout?");
        alert.setContentText("Are You Sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("com/hiddless/java_fx/view/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) userTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Failed to redirect to login page!", Alert.AlertType.ERROR);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    /// ADD USER
    @FXML
    public void addUser(ActionEvent actionEvent) {
        TextInputDialog usernameDialog= new TextInputDialog();
        usernameDialog.setTitle("Add new user");
        usernameDialog.setHeaderText("Add new user");
        usernameDialog.setContentText("Username:");

        Optional<String> optionalUsername= usernameDialog.showAndWait();

        if(optionalUsername.isPresent()){
            String username= optionalUsername.get();


            TextInputDialog passwordDialog= new TextInputDialog();
            passwordDialog.setTitle("New user Password");
            passwordDialog.setHeaderText("New user Password");
            passwordDialog.setContentText("User password:");

            Optional<String> optionalPassword= usernameDialog.showAndWait();

            if(optionalPassword.isPresent()){
                String password= optionalPassword.get();

                TextInputDialog emailDialog= new TextInputDialog();
                emailDialog.setTitle("New user Email");
                emailDialog.setHeaderText("New user Email");
                emailDialog.setContentText("Email:");

                Optional<String> optionalEmail= usernameDialog.showAndWait();

                if(optionalEmail.isPresent()) {
                    String email = optionalPassword.get();

                    Optional<UserDTO> newUser = Optional.of(new UserDTO(0, username, password, email));

                    newUser.ifPresent(user->{
                        Optional<UserDTO> createdUser = userDAO.create(user);

                        if(createdUser.isPresent()){
                            showAlert("Succesful","User Added Successfully",Alert.AlertType.INFORMATION);
                            refreshTable();
                        }else{
                            showAlert("Failed","Error received while adding user Added",Alert.AlertType.ERROR);
                        }
                    });
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    /// Update
    @FXML
    public void updateUser(ActionEvent actionEvent) {
        UserDTO selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {

            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Update User");
            usernameDialog.setHeaderText("Update User");
            usernameDialog.setContentText("Username:");

            Optional<String> optionalUsername = usernameDialog.showAndWait();

            if (optionalUsername.isPresent()) {
                String username = optionalUsername.get();

                TextInputDialog passwordDialog = new TextInputDialog();
                passwordDialog.setTitle("Update Password");
                passwordDialog.setHeaderText("Update Password");
                passwordDialog.setContentText("User Password:");

                Optional<String> optionalPassword = usernameDialog.showAndWait();

                if (optionalPassword.isPresent()) {
                    String password = optionalPassword.get();

                    TextInputDialog emailDialog = new TextInputDialog();
                    emailDialog.setTitle("Update Email");
                    emailDialog.setHeaderText("Update Email");
                    emailDialog.setContentText("User Email:");

                    Optional<String> optionalEmail = usernameDialog.showAndWait();


                    if (optionalEmail.isPresent()) {
                        String email = optionalPassword.get();

                        Optional<UserDTO> newUser = Optional.of(new UserDTO(0, username, password, email));

                        newUser.ifPresent(user -> {
                            Optional<UserDTO> createdUser = userDAO.update(selectedUser.getId(),selectedUser);

                            if (createdUser.isPresent()) {
                                showAlert("Succeful", "User updated Succefully", Alert.AlertType.INFORMATION);
                                refreshTable();
                            } else {
                                showAlert("Failed", "Failed to update user", Alert.AlertType.ERROR);
                            }
                        });
                    }
                }
            } else {
                showAlert("Error", "Please select an user", Alert.AlertType.ERROR);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    /// Delete
    @FXML
    public void deleteUser(ActionEvent actionEvent) {

        Optional<UserDTO> selectedUser = Optional.ofNullable(userTable.getSelectionModel().getSelectedItem());
        if (selectedUser != null) {


            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Deletion confirmation");
            confirmationAlert.setHeaderText("Do you want to delete user?");
            confirmationAlert.setContentText("User that will delete: "+ selectedUser.get().getUsername());

            Optional<ButtonType> isDelete= confirmationAlert.showAndWait();
            if(isDelete.isPresent() && isDelete.get()== ButtonType.OK){

                selectedUser.ifPresent(user -> {
                    Optional<UserDTO> deleteUser = userDAO.delete(selectedUser.get().getId());

                    if (deleteUser.isPresent()) {
                        showAlert("Succesful", "User Deleted succesfully", Alert.AlertType.INFORMATION);
                        refreshTable();
                    } else {
                        showAlert("Error", "Failed to delete used", Alert.AlertType.ERROR);
                    }
                });
            }
        }
    }

}