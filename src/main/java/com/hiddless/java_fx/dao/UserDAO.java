package com.hiddless.java_fx.dao;

import com.hiddless.java_fx.database.SingletonDBConnection;
import com.hiddless.java_fx.dto.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements IDaoImplements<UserDTO> {
    // Injection
    private Connection connection;

    // Parametresiz Constructor
    public UserDAO() {
        this.connection = SingletonDBConnection.getInstance().getConnection();
    }

    /// ////////////////////////////////////////////////////////////////////
    // CRUD
    // CREATE
    @Override
    public Optional<UserDTO> create(UserDTO userDTO) {
        String sql = "INSERT INTO users (username,password,email) VALUES(?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, userDTO.getUsername());
            preparedStatement.setString(2, userDTO.getPassword());
            preparedStatement.setString(3, userDTO.getEmail());
            // CREATE, DELETE, UPDATE
            int affectedRows = preparedStatement.executeUpdate();

            // Eğer Ekleme başarılıysa
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userDTO.setId(generatedKeys.getInt(1)); // OTomatik ID set et
                        return Optional.of(userDTO);
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // Eğer Ekleme başarısızsa boş veri dönder
        return Optional.empty();
    }

    // LIST
    @Override
    public Optional<List<UserDTO>> list() {
        List<UserDTO> userDTOList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery(sql);

            // Veritabanından gelen verileri almak
            while(resultSet.next()){
                userDTOList.add(UserDTO.builder()
                        .id(resultSet.getInt("id"))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .email(resultSet.getString("email"))
                        .build());
            }
            return userDTOList.isEmpty() ? Optional.empty() : Optional.of(userDTOList);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // Eğer Listeleme başarısızsa boş veri dönder
        return Optional.empty();
    }

    // FIND BY NAME
    @Override
    public Optional<UserDTO> findByName(String name) {
        return Optional.empty();
    }

    // FIND BY ID
    @Override
    public Optional<UserDTO> findById(int id) {
        return Optional.empty();
    }

    // UPDATE
    @Override
    public Optional<UserDTO> update(int id, UserDTO entity) {
        return Optional.empty();
    }

    // DELETE
    @Override
    public Optional<UserDTO> delete(int id) {
        return Optional.empty();
    }

} //end class
