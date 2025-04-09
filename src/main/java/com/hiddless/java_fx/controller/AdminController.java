package com.hiddless.java_fx.controller;

import com.google.gson.*;
import com.hiddless.java_fx.dao.KdvDAO;
import com.hiddless.java_fx.dao.NotebookDAO;
import com.hiddless.java_fx.dao.UserDAO;
import com.hiddless.java_fx.database.SingletonPropertiesDBConnection;
import com.hiddless.java_fx.dto.KdvDTO;
import com.hiddless.java_fx.dto.NotebookDTO;
import com.hiddless.java_fx.dto.UserDTO;
import com.hiddless.java_fx.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AdminController {

    @FXML
    public MenuButton languageMenuButton;
    @FXML
    public Button restoreDataButton;

    // @FXML
    //public VBox rootVBox;

    private UserDAO userDAO;
    private KdvDAO kdvDAO;

    public AdminController() {
        userDAO = new UserDAO();
        kdvDAO = new KdvDAO();
    }

    // User İçin
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
    private TableColumn<UserDTO, String> roleColumn;
    //@FXML private ComboBox<String> roleComboBox; //// Sayfa açılır açılmaz geliyor
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<ERole> filterRoleComboBox;

    // KDV için
    @FXML
    private TableView<KdvDTO> kdvTable;
    @FXML
    private TableColumn<KdvDTO, Integer> idColumnKdv;
    @FXML
    private TableColumn<KdvDTO, Double> amountColumn;
    @FXML
    private TableColumn<KdvDTO, Double> kdvRateColumn;
    @FXML
    private TableColumn<KdvDTO, Double> kdvAmountColumn;
    @FXML
    private TableColumn<KdvDTO, Double> totalAmountColumn;
    @FXML
    private TableColumn<KdvDTO, String> receiptColumn;
    @FXML
    private TableColumn<KdvDTO, LocalDate> dateColumn;
    @FXML
    private TableColumn<KdvDTO, String> descColumn;
    @FXML
    private TextField searchKdvField;

    @FXML
    private Label clockLabel;

    @FXML
    private Button btnKdvExportTxt, btnKdvExportPdf, btnKdvExportExcel, btnKdvPrint, btnKdvMail;


    private Locale currentLocale = new Locale("tr");
    private ResourceBundle bundle;


    // KULLANICI
    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase().trim();
        ERole selectedRole = filterRoleComboBox.getValue();

        Optional<List<UserDTO>> optionalUsers = userDAO.list();
        List<UserDTO> fullList = optionalUsers.orElseGet(List::of);

        List<UserDTO> filteredList = fullList.stream()
                .filter(user -> {
                    boolean matchesKeyword = keyword.isEmpty() ||
                            user.getUsername().toLowerCase().contains(keyword) ||
                            user.getEmail().toLowerCase().contains(keyword) ||
                            user.getRole().getDescription().toLowerCase().contains(keyword);

                    boolean matchesRole = (selectedRole == null) || user.getRole() == selectedRole;

                    return matchesKeyword && matchesRole;
                })
                .toList();

        userTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    public void clearFilters() {
        searchField.clear();
        filterRoleComboBox.setValue(null);
    }

    @FXML
    public void openKdvPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hiddless/java_fx/view/kdv.fxml"));
            Parent kdvRoot = loader.load();
            Stage stage = new Stage();
            stage.setTitle("KDV Paneli");
            stage.setScene(new Scene(kdvRoot));
            stage.show();
        } catch (IOException e) {
            showAlert("Hata", "KDV ekranı açılamadı!", Alert.AlertType.ERROR);
            NotificationUtil.showNotification("KDV ekranı açılamadı!", NotificationType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("Admincontroller initialize çağrıldı!");
        loadLanguage(currentLocale);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                    clockLabel.setText(now.format(formatter));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        // Kullanıcı tablosu
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        filterRoleComboBox.getItems().add(null);
        filterRoleComboBox.getItems().addAll(ERole.values());
        filterRoleComboBox.setValue(null);

        searchField.textProperty().addListener((observable, oldVal, newVal) -> applyFilters());
        filterRoleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String password, boolean empty) {
                super.updateItem(password, empty);
                setText((empty || password == null) ? null : "******");
            }
        });

        refreshTable();

        // KDV tablosu
        idColumnKdv.setCellValueFactory(new PropertyValueFactory<>("id"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        kdvRateColumn.setCellValueFactory(new PropertyValueFactory<>("kdvRate"));
        kdvAmountColumn.setCellValueFactory(new PropertyValueFactory<>("kdvAmount"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        receiptColumn.setCellValueFactory(new PropertyValueFactory<>("receiptNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        searchKdvField.textProperty().addListener((obs, oldVal, newVal) -> applyKdvFilter());

        refreshKdvTable();
    }

    @FXML
    private void refreshTable() {
        applyFilters();
        Optional<List<UserDTO>> optionalUsers = userDAO.list();
        List<UserDTO> userDTOList = optionalUsers.orElseGet(List::of);
        ObservableList<UserDTO> observableList = FXCollections.observableArrayList(userDTOList);
        userTable.setItems(observableList);
        showAlert("Bilgi", "Tablo başarıyla yenilendi!", Alert.AlertType.INFORMATION);
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
        alert.setTitle("Çıkış Yap");
        alert.setHeaderText("Oturumdan çıkmak istiyor musunuz?");
        alert.setContentText("Emin misiniz?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.LOGIN));
                Parent root = loader.load();
                Stage stage = (Stage) userTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Hata", "Giriş sayfasına yönlendirme başarısız!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void printTable() {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            showAlert("Yazıcı Bulunamadı", "Yazıcı sistemde tanımlı değil.", Alert.AlertType.ERROR);
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(userTable.getScene().getWindow())) {
            boolean success = job.printPage(userTable);
            if (success) {
                job.endJob();
                showAlert("Yazdırma", "Tablo başarıyla yazdırıldı.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Yazdırma Hatası", "Yazdırma işlemi başarısız oldu.", Alert.AlertType.ERROR);
            }
        }
    }

    // Eğer uygulaman Linux/macOS'ta çalışabilir olacaksa, şu şekilde platform kontrolü de ekleyebilirsin:
    @FXML
    public void openCalculator() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("calc");
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open -a Calculator");
            } else if (os.contains("nux")) {
                Runtime.getRuntime().exec("gnome-calculator"); // Linux için
            } else {
                showAlert("Hata", "Bu işletim sistemi desteklenmiyor!", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Hata", "Hesap makinesi açılamadı.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void openKdvCalculator() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("KDV Hesapla");
        dialog.setHeaderText("KDV Hesaplayıcı");

        TextField amountField = new TextField();
        ComboBox<String> kdvBox = new ComboBox<>();
        kdvBox.getItems().addAll("1%", "8%", "18%", "Özel");
        kdvBox.setValue("18%");
        TextField customKdv = new TextField();
        customKdv.setDisable(true);
        TextField receiptField = new TextField();
        DatePicker datePicker = new DatePicker();
        Label resultLabel = new Label();

        kdvBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            customKdv.setDisable(!"Özel".equals(newVal));
            if (!"Özel".equals(newVal)) customKdv.clear();
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Tutar:"), amountField);
        grid.addRow(1, new Label("KDV Oranı:"), kdvBox);
        grid.addRow(2, new Label("Özel Oran:"), customKdv);
        grid.addRow(3, new Label("Fiş No:"), receiptField);
        grid.addRow(4, new Label("Tarih:"), datePicker);
        grid.add(resultLabel, 0, 5, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Hesapla", ButtonBar.ButtonData.OK_DONE), ButtonType.CLOSE);

        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    double rate = switch (kdvBox.getValue()) {
                        case "1%" -> 1;
                        case "8%" -> 8;
                        case "18%" -> 18;
                        default -> Double.parseDouble(customKdv.getText());
                    };
                    double kdv = amount * rate / 100;
                    double total = amount + kdv;

                    String result = String.format("""
                                    Fiş No: %s
                                    Tarih: %s
                                    Ara Toplam: %.2f ₺
                                    KDV (%%%.1f): %.2f ₺
                                    Genel Toplam: %.2f ₺
                                    """,
                            receiptField.getText(), datePicker.getValue(),
                            amount, rate, kdv, total);

                    resultLabel.setText(result);
                    showExportOptions(result);
                } catch (Exception e) {
                    showAlert("Hata", "Geçersiz giriş.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showExportOptions(String content) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("TXT", "TXT", "PDF", "EXCEL", "MAIL");
        dialog.setTitle("Dışa Aktar");
        dialog.setHeaderText("KDV sonucu nasıl dışa aktarılsın?");
        dialog.setContentText("Format:");
        dialog.showAndWait().ifPresent(choice -> {
            switch (choice) {
                case "TXT" -> exportAsTxt(content);
                case "PDF" -> exportAsPdf(content);
                case "EXCEL" -> exportAsExcel(content);
                case "MAIL" -> sendMail(content);
            }
        });
    }

    private void sendMail(String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("E-Posta Gönder");
        dialog.setHeaderText("KDV sonucunu göndereceğiniz e-posta adresini girin:");
        dialog.setContentText("E-posta:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(receiver -> {
            String senderEmail = "seninmailin@gmail.com"; // değiştir
            String senderPassword = "uygulama-sifresi"; // değiştir
            String host = "smtp.gmail.com";
            int port = 587;

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject("KDV Hesaplama Sonucu");
                message.setText(content);

                Transport.send(message);

                showAlert("Başarılı", "Mail başarıyla gönderildi!", Alert.AlertType.INFORMATION);
            } catch (MessagingException e) {
                e.printStackTrace();
                showAlert("Hata", "Mail gönderilemedi.", Alert.AlertType.ERROR);
            }
        });
    }


    private void exportAsTxt(String content) {
        try {
            Path path = Paths.get(System.getProperty("user.home"), "Desktop",
                    "kdv_" + System.currentTimeMillis() + ".txt");
            Files.writeString(path, content);
            showAlert("Başarılı", "TXT masaüstüne kaydedildi", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Hata", "TXT kaydedilemedi.", Alert.AlertType.ERROR);
            NotificationUtil.showNotification("TXT kaydedilemedi.", NotificationType.ERROR);
        }
    }

    private void exportAsPdf(String content) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA, 12);
            stream.setLeading(14.5f);
            stream.newLineAtOffset(50, 750);

            for (String line : content.split("\n")) {
                String safeLine = line.replace("\t", "    ");
                stream.showText(safeLine);
                stream.newLine();
            }

            stream.endText();
            stream.close();

            File file = new File(System.getProperty("user.home") + "/Desktop/kdv_" + System.currentTimeMillis() + ".pdf");
            doc.save(file);
            showAlert("Başarılı", "PDF masaüstüne kaydedildi", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Hata", "PDF kaydedilemedi.", Alert.AlertType.ERROR);
        }
    }


    private void exportAsExcel(String content) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("KDV");

            // Stil tanımı (isteğe bağlı)
            var headerStyle = wb.createCellStyle();
            var font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Başlıkları yaz
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Tutar", "KDV Oranı", "KDV Tutarı", "Toplam", "Fiş No", "Tarih", "Açıklama"};
            for (int i = 0; i < headers.length; i++) {
                var cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Satırları yaz
            int rowNum = 1;
            for (KdvDTO kdv : kdvTable.getItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(kdv.getId());
                row.createCell(1).setCellValue(kdv.getAmount());
                row.createCell(2).setCellValue(kdv.getKdvRate());
                row.createCell(3).setCellValue(kdv.getKdvAmount());
                row.createCell(4).setCellValue(kdv.getTotalAmount());
                row.createCell(5).setCellValue(kdv.getReceiptNumber());
                row.createCell(6).setCellValue(String.valueOf(kdv.getTransactionDate()));
                row.createCell(7).setCellValue(kdv.getDescription());
            }

            // Otomatik sütun genişliği ayarla
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Kaydet
            File file = new File(System.getProperty("user.home") + "/Desktop/kdv_" + System.currentTimeMillis() + ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }

            showAlert("Başarılı", "Excel masaüstüne kaydedildi", Alert.AlertType.INFORMATION);

            NotificationUtil.showNotification("Excel masaüstüne kaydedildi", NotificationType.SUCCESS);
        } catch (IOException e) {
            showAlert("Hata", "Excel kaydedilemedi.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void exportKdvAsTxt() {
        exportAsTxt(generateKdvSummary());
    }

    @FXML
    public void exportKdvAsPdf() {
        exportAsPdf(generateKdvSummary());
    }

    @FXML
    public void exportKdvAsExcel() {
        exportAsExcel(generateKdvSummary());
    }

    @FXML
    public void printKdvTable() {
        // Kdv tablosunu yazdır
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(kdvTable.getScene().getWindow())) {
            boolean success = job.printPage(kdvTable);
            if (success) {
                job.endJob();
                showAlert("Yazdırma", "KDV tablosu yazdırıldı.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Hata", "Yazdırma başarısız.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void sendKdvByMail() {
        sendMail(generateKdvSummary());
    }


    private String generateKdvSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID\tTutar\tKDV Oranı\tKDV Tutarı\tToplam\tFiş No\tTarih\tAçıklama\n");
        for (KdvDTO kdv : kdvTable.getItems()) {
            builder.append(String.format("%d\t%.2f\t%.2f%%\t%.2f\t%.2f\t%s\t%s\t%s\n",
                    kdv.getId(),
                    kdv.getAmount(),
                    kdv.getKdvRate(),
                    kdv.getKdvAmount(),
                    kdv.getTotalAmount(),
                    kdv.getReceiptNumber(),
                    kdv.getTransactionDate(),
                    kdv.getDescription()));
        }
        return builder.toString();
    }


    @FXML
    private void handleNew() {
        System.out.println("Yeni oluşturuluyor...");
    }

    @FXML
    private void handleOpen() {
        System.out.println("Dosya açılıyor...");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void goToUsers(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/path/to/user.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void goToSettings(ActionEvent event) throws IOException {
       /* Parent root = FXMLLoader.load(getClass().getResource("/path/to/settings.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();*/
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hakkında");
        alert.setHeaderText("Uygulama Bilgisi");
        alert.setContentText("Bu uygulama JavaFX ile geliştirilmiştir.");
        alert.showAndWait();
    }


    //Dil seçeneği


    @FXML
    private void languageTheme() {
        currentLocale = currentLocale.getLanguage().equals("tr") ? new Locale("en") : new Locale("tr");
        loadLanguage(currentLocale);
    }


    //Header FXML

    @FXML
    private Label headerLabel;
    @FXML
    private Button darkModeButton;
    @FXML
    private Button notificationButton;
    @FXML
    private Button backupButton;
    @FXML
    private Button restoreButton;
    @FXML
    private Button notebookButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;

//menü FXML

    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem menuItemExit;

    @FXML
    private Menu menuUser;
    @FXML
    private MenuItem menuItemAddUser;
    @FXML
    private MenuItem menuItemUpdateUser;
    @FXML
    private MenuItem menuItemDeleteUser;

    @FXML
    private Menu menuKdv;
    @FXML
    private MenuItem menuItemAddKdv;
    @FXML
    private MenuItem menuItemUpdateKdv;
    @FXML
    private MenuItem menuItemDeleteKdv;

    @FXML
    private Menu menuOther;
    @FXML
    private MenuItem menuItemCalculator;
    @FXML
    private MenuItem menuItemNotebook;

    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private Label userTitleLabel;
    @FXML
    private Button btnAddUser;
    @FXML
    private Button btnUpdateUser;
    @FXML
    private Button btnDeleteUser;
    @FXML
    private Button btnPrintUser;

    @FXML
    private Button btnAddKdv;
    @FXML
    private Button btnUpdateKdv;
    @FXML
    private Button btnDeleteKdv;

    @FXML
    private Label kdvTitleLabel;

    @FXML
    private Label footerLabel;


    private void loadLanguage(Locale locale) {
        bundle = ResourceBundle.getBundle("com.hiddless.java_fx.view.messages", locale);

        headerLabel.setText(bundle.getString("header.panel"));
        darkModeButton.setText(bundle.getString("theme.dark"));
        languageMenuButton.setText(bundle.getString("language"));
        notificationButton.setText(bundle.getString("notifications"));
        backupButton.setText(bundle.getString("backup"));
        restoreButton.setText(bundle.getString("restore"));
        notebookButton.setText(bundle.getString("notebook"));
        profileButton.setText(bundle.getString("profile"));
        logoutButton.setText(bundle.getString("logout"));

        menuFile.setText(bundle.getString("menu.file"));
        menuItemExit.setText(bundle.getString("menu.exit"));

        menuUser.setText(bundle.getString("menu.user"));
        menuItemAddUser.setText(bundle.getString("menu.addUser"));
        menuItemUpdateUser.setText(bundle.getString("menu.updateUser"));
        menuItemDeleteUser.setText(bundle.getString("menu.deleteUser"));

        menuKdv.setText(bundle.getString("menu.kdv"));
        menuItemAddKdv.setText(bundle.getString("menu.addKdv"));
        menuItemUpdateKdv.setText(bundle.getString("menu.updateKdv"));
        menuItemDeleteKdv.setText(bundle.getString("menu.deleteKdv"));

        menuOther.setText(bundle.getString("menu.other"));
        menuItemCalculator.setText(bundle.getString("menu.calculator"));
        menuItemNotebook.setText(bundle.getString("menu.notebook"));

        menuHelp.setText(bundle.getString("menu.help"));
        menuItemAbout.setText(bundle.getString("menu.about"));


        searchField.setPromptText(bundle.getString("user.searchPrompt"));
        filterRoleComboBox.setPromptText(bundle.getString("user.rolePrompt"));


        kdvTitleLabel.setText(bundle.getString("kdv.title"));


        btnAddKdv.setText(bundle.getString("kdv.add"));
        btnUpdateKdv.setText(bundle.getString("kdv.update"));
        btnDeleteKdv.setText(bundle.getString("kdv.delete"));

        searchKdvField.setPromptText(bundle.getString("kdv.searchPrompt"));

        btnKdvExportTxt.setText(bundle.getString("kdv.exportTxt"));
        btnKdvExportPdf.setText(bundle.getString("kdv.exportPdf"));
        btnKdvExportExcel.setText(bundle.getString("kdv.exportExcel"));
        btnKdvPrint.setText(bundle.getString("kdv.print"));
        btnKdvMail.setText(bundle.getString("kdv.mail"));


        userTitleLabel.setText(bundle.getString("user.title"));
        searchField.setPromptText(bundle.getString("user.searchPrompt"));
        filterRoleComboBox.setPromptText(bundle.getString("user.rolePrompt"));
        btnAddUser.setText(bundle.getString("user.add"));
        btnUpdateUser.setText(bundle.getString("user.update"));
        btnDeleteUser.setText(bundle.getString("user.delete"));
        btnPrintUser.setText(bundle.getString("user.print"));

        // KDV tablosu başlıkları
        idColumnKdv.setText(bundle.getString("kdv.id"));
        amountColumn.setText(bundle.getString("kdv.amount"));
        kdvRateColumn.setText(bundle.getString("kdv.rate"));
        kdvAmountColumn.setText(bundle.getString("kdv.amountValue"));
        totalAmountColumn.setText(bundle.getString("kdv.total"));
        receiptColumn.setText(bundle.getString("kdv.receipt"));
        dateColumn.setText(bundle.getString("kdv.date"));
        descColumn.setText(bundle.getString("kdv.description"));

// Kullanıcı tablosu başlıkları
        idColumn.setText(bundle.getString("user.id"));
        usernameColumn.setText(bundle.getString("user.username"));
        emailColumn.setText(bundle.getString("user.email"));
        passwordColumn.setText(bundle.getString("user.password"));
        roleColumn.setText(bundle.getString("user.role"));

        footerLabel.setText(bundle.getString("footer"));

    }

    @FXML
    private void switchToTurkish() {
        currentLocale = new Locale("tr");
        loadLanguage(currentLocale);
    }

    @FXML
    private void switchToEnglish() {
        currentLocale = new Locale("en");
        loadLanguage(currentLocale);
    }

    /// //////////////////////////////////////////////////////////
    private static class AddUserDialog extends Dialog<UserDTO> {
        private final TextField usernameField = new TextField();
        private final PasswordField passwordField = new PasswordField();
        private final TextField emailField = new TextField();
        private final ComboBox<String> roleComboBox = new ComboBox<>();

        public AddUserDialog() {
            setTitle("Yeni Kullanıcı Ekle");
            setHeaderText("Yeni kullanıcı bilgilerini girin");

            // Manuel Ekleme
            //roleComboBox.getItems().addAll("USER", "ADMIN", "MODERATOR");
            //roleComboBox.setValue("USER");

            ComboBox<ERole> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll(ERole.values());
            roleComboBox.setValue(ERole.USER); // Varsayılan seçim


            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(new Label("Kullanıcı Adı:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Şifre:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("E-posta:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(new Label("Rol:"), 0, 3);
            grid.add(roleComboBox, 1, 3);

            getDialogPane().setContent(grid);

            ButtonType addButtonType = new ButtonType("Ekle", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return UserDTO.builder()
                            .username(usernameField.getText().trim())
                            .password(passwordField.getText().trim())
                            .email(emailField.getText().trim())
                            .role(roleComboBox.getValue())
                            .build();
                }
                return null;
            });
        }
    }

    @FXML
    public void addUser(ActionEvent actionEvent) {
        AddUserDialog dialog = new AddUserDialog();
        Optional<UserDTO> result = dialog.showAndWait();

        result.ifPresent(newUser -> {
            if (newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty() || newUser.getEmail().isEmpty()) {
                showAlert("Hata", "Tüm alanlar doldurulmalı!", Alert.AlertType.ERROR);
                return;
            }

            if (userDAO.isUsernameExists(newUser.getUsername())) {
                showAlert("Uyarı", "Bu kullanıcı adı zaten kayıtlı!", Alert.AlertType.WARNING);
                return;
            }

            if (userDAO.isEmailExists(newUser.getEmail())) {
                showAlert("Uyarı", "Bu e-posta zaten kayıtlı!", Alert.AlertType.WARNING);
                return;
            }

            Optional<UserDTO> createdUser = userDAO.create(newUser);
            if (createdUser.isPresent()) {
                showAlert("Başarılı", "Kullanıcı başarıyla eklendi!", Alert.AlertType.INFORMATION);
                refreshTable();
            } else {
                showAlert("Hata", "Kullanıcı eklenemedi!", Alert.AlertType.ERROR);
            }
        });
    }


    @FXML
    public void addUserEski(ActionEvent actionEvent) {
        // Sayfa açılır açılmaz geliyor
        //String role = roleComboBox.getValue();

        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Kullanıcı Ekle");
        usernameDialog.setHeaderText("Kullanıcı Adı");
        usernameDialog.setContentText("Yeni kullanıcı adı giriniz:");
        Optional<String> optionalUsername = usernameDialog.showAndWait();
        if (optionalUsername.isEmpty()) return;
        String username = optionalUsername.get().trim();

        if (userDAO.isUsernameExists(username)) {
            showAlert("Uyarı", "Bu kullanıcı adı zaten kayıtlı!", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Kullanıcı Ekle");
        passwordDialog.setHeaderText("Şifre");
        passwordDialog.setContentText("Yeni şifre giriniz:");
        Optional<String> optionalPassword = passwordDialog.showAndWait();
        if (optionalPassword.isEmpty()) return;
        String password = optionalPassword.get().trim();

        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Kullanıcı Ekle");
        emailDialog.setHeaderText("E-posta");
        emailDialog.setContentText("Yeni e-posta giriniz:");
        Optional<String> optionalEmail = emailDialog.showAndWait();
        if (optionalEmail.isEmpty()) return;
        String email = optionalEmail.get().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun!", Alert.AlertType.ERROR);
            return;
        }

        if (userDAO.isEmailExists(email)) {
            showAlert("Uyarı", "Bu e-posta zaten kayıtlı!", Alert.AlertType.WARNING);
            return;
        }

        UserDTO newUser = UserDTO.builder()
                .username(username)
                .password(password)
                .email(email)
                //.role(role) //// Sayfa açılır açılmaz geliyor
                .build();

        Optional<UserDTO> createdUser = userDAO.create(newUser);
        if (createdUser.isPresent()) {
            showAlert("Başarılı", "Kullanıcı başarıyla eklendi!", Alert.AlertType.INFORMATION);
            refreshTable();
        } else {
            showAlert("Hata", "Kullanıcı eklenirken hata oluştu!", Alert.AlertType.ERROR);
        }
    }

    private static class UpdateUserDialog extends Dialog<UserDTO> {
        private final TextField usernameField = new TextField();
        private final PasswordField passwordField = new PasswordField();
        private final TextField emailField = new TextField();
        private final ComboBox<ERole> roleComboBox = new ComboBox<>();

        public UpdateUserDialog(UserDTO existingUser) {
            setTitle("Kullanıcı Güncelle");
            setHeaderText("Kullanıcı bilgilerini düzenleyin");

            usernameField.setText(existingUser.getUsername());
            emailField.setText(existingUser.getEmail());

            // 🔥 ENUM kullanımıyla rol listesi
            roleComboBox.getItems().addAll(ERole.values());

            // 🔥 Mevcut role'u enum olarak seç
            try {
                roleComboBox.setValue(ERole.fromString(String.valueOf(existingUser.getRole())));
            } catch (RuntimeException e) {
                roleComboBox.setValue(ERole.USER); // Yedek: varsayılan rol
            }

            // Layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(new Label("Kullanıcı Adı:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Yeni Şifre:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("E-posta:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(new Label("Rol:"), 0, 3);
            grid.add(roleComboBox, 1, 3);

            getDialogPane().setContent(grid);

            ButtonType updateButtonType = new ButtonType("Güncelle", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Sonuç döndür
            setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    return UserDTO.builder()
                            .username(usernameField.getText().trim())
                            .password(passwordField.getText().trim().isEmpty()
                                    ? existingUser.getPassword()
                                    : passwordField.getText().trim())
                            .email(emailField.getText().trim())
                            .role(ERole.valueOf(roleComboBox.getValue().name())) // Enum’dan string’e dönüşüm
                            .build();
                }
                return null;
            });
        }
    }


    @FXML
    public void updateUserEski(ActionEvent actionEvent) {
        UserDTO selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Uyarı", "Lütfen güncellenecek bir kullanıcı seçin!", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog usernameDialog = new TextInputDialog(selectedUser.getUsername());
        usernameDialog.setTitle("Kullanıcı Adı Güncelle");
        usernameDialog.setHeaderText("Yeni kullanıcı adını girin:");
        Optional<String> newUsername = usernameDialog.showAndWait();
        if (newUsername.isEmpty()) return;

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Şifre Güncelle");
        passwordDialog.setHeaderText("Yeni şifreyi girin:");
        Optional<String> newPassword = passwordDialog.showAndWait();
        if (newPassword.isEmpty()) return;

        TextInputDialog emailDialog = new TextInputDialog(selectedUser.getEmail());
        emailDialog.setTitle("Email Güncelle");
        emailDialog.setHeaderText("Yeni e-posta adresini girin:");
        Optional<String> newEmail = emailDialog.showAndWait();
        if (newEmail.isEmpty()) return;

        // Sayfa açılır açılmaz geliyor
        //String role = roleComboBox.getValue();

        UserDTO updatedUser = UserDTO.builder()
                .username(newUsername.get())
                .password(newPassword.get())
                .email(newEmail.get())
                //.role(role) //// Sayfa açılır açılmaz geliyor
                .build();

        Optional<UserDTO> result = userDAO.update(selectedUser.getId(), updatedUser);
        if (result.isPresent()) {
            showAlert("Başarılı", "Kullanıcı başarıyla güncellendi!", Alert.AlertType.INFORMATION);
            refreshTable();
        } else {
            showAlert("Hata", "Güncelleme sırasında hata oluştu!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void updateUser(ActionEvent actionEvent) {
        UserDTO selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Uyarı", "Lütfen güncellenecek bir kullanıcı seçin!", Alert.AlertType.WARNING);
            return;
        }

        UpdateUserDialog dialog = new UpdateUserDialog(selectedUser);
        Optional<UserDTO> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            if (updatedUser.getUsername().isEmpty() || updatedUser.getPassword().isEmpty() || updatedUser.getEmail().isEmpty()) {
                showAlert("Hata", "Tüm alanlar doldurulmalı!", Alert.AlertType.ERROR);
                return;
            }

            Optional<UserDTO> updated = userDAO.update(selectedUser.getId(), updatedUser);
            if (updated.isPresent()) {
                showAlert("Başarılı", "Kullanıcı güncellendi!", Alert.AlertType.INFORMATION);
                refreshTable();
            } else {
                showAlert("Hata", "Güncelleme işlemi başarısız!", Alert.AlertType.ERROR);
            }
        });
    }


    @FXML
    public void deleteUser(ActionEvent actionEvent) {
        Optional<UserDTO> selectedUser = Optional.ofNullable(userTable.getSelectionModel().getSelectedItem());
        selectedUser.ifPresent(user -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Silme Onayı");
            confirmationAlert.setHeaderText("Kullanıcıyı silmek istiyor musunuz?");
            confirmationAlert.setContentText("Silinecek kullanıcı: " + user.getUsername());
            Optional<ButtonType> isDelete = confirmationAlert.showAndWait();
            if (isDelete.isPresent() && isDelete.get() == ButtonType.OK) {
                Optional<UserDTO> deleteUser = userDAO.delete(user.getId());
                if (deleteUser.isPresent()) {
                    showAlert("Başarılı", "Kullanıcı başarıyla silindi", Alert.AlertType.INFORMATION);
                    refreshTable();
                } else {
                    showAlert("Başarısız", "Silme işlemi başarısız oldu", Alert.AlertType.ERROR);
                }
            }
        });
    }

    // KDV
    // 📄 Listeyi yenile
    private void refreshKdvTable() {
        Optional<List<KdvDTO>> list = kdvDAO.list();
        list.ifPresent(data -> kdvTable.setItems(FXCollections.observableArrayList(data)));
    }

    // 🔎 Arama filtreleme
    private void applyKdvFilter() {
        String keyword = searchKdvField.getText().trim().toLowerCase();
        Optional<List<KdvDTO>> all = kdvDAO.list();
        List<KdvDTO> filtered = all.orElse(List.of()).stream()
                .filter(kdv -> kdv.getReceiptNumber().toLowerCase().contains(keyword))
                .toList();
        kdvTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // ➕ KDV ekle
    @FXML
    public void addKdv() {
        KdvDTO newKdv = showKdvForm(null);
        if (newKdv != null && newKdv.isValid()) {
            kdvDAO.create(newKdv);
            refreshKdvTable();
            showAlert("Başarılı", "KDV kaydı eklendi.", Alert.AlertType.INFORMATION);
        }
    }

    // ✏️ KDV güncelle
    @FXML
    public void updateKdv() {
        KdvDTO selected = kdvTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Güncellenecek bir kayıt seçin.", Alert.AlertType.WARNING);
            return;
        }

        KdvDTO updated = showKdvForm(selected);
        if (updated != null && updated.isValid()) {
            kdvDAO.update(selected.getId(), updated);
            refreshKdvTable();
            showAlert("Başarılı", "KDV kaydı güncellendi.", Alert.AlertType.INFORMATION);
        }
    }

    //
    @FXML
    public void deleteKdv() {
        KdvDTO selected = kdvTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Silinecek bir kayıt seçin.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Silmek istiyor musunuz?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Fiş: " + selected.getReceiptNumber());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            kdvDAO.delete(selected.getId());
            refreshKdvTable();
            showAlert("Silindi", "KDV kaydı silindi.", Alert.AlertType.INFORMATION);
        }
    }

    // 💬 Ortak form (ekle/güncelle)
    private KdvDTO showKdvForm(KdvDTO existing) {
        Dialog<KdvDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Yeni KDV Ekle" : "KDV Güncelle");

        TextField amountField = new TextField();
        TextField rateField = new TextField();
        TextField receiptField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descField = new TextField();
        ComboBox<String> exportCombo = new ComboBox<>();
        exportCombo.getItems().addAll("TXT", "PDF", "EXCEL");
        exportCombo.setValue("TXT");

        if (existing != null) {
            amountField.setText(String.valueOf(existing.getAmount()));
            rateField.setText(String.valueOf(existing.getKdvRate()));
            receiptField.setText(existing.getReceiptNumber());
            datePicker.setValue(existing.getTransactionDate());
            descField.setText(existing.getDescription());
            exportCombo.setValue(existing.getExportFormat());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Tutar:"), amountField);
        grid.addRow(1, new Label("KDV Oranı (%):"), rateField);
        grid.addRow(2, new Label("Fiş No:"), receiptField);
        grid.addRow(3, new Label("Tarih:"), datePicker);
        grid.addRow(4, new Label("Açıklama:"), descField);
        grid.addRow(5, new Label("Format:"), exportCombo);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return KdvDTO.builder()
                            .amount(Double.parseDouble(amountField.getText()))
                            .kdvRate(Double.parseDouble(rateField.getText()))
                            .receiptNumber(receiptField.getText())
                            .transactionDate(datePicker.getValue())
                            .description(descField.getText())
                            .exportFormat(exportCombo.getValue())
                            .build();
                } catch (Exception e) {
                    showAlert("Hata", "Geçersiz veri!", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<KdvDTO> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // BİTİRME PROJESİ

    //Dark Mode
    private boolean isDarkMode = false; // Başlangıçta açık tema

    @FXML
    private void toggleTheme() {
        Scene scene = darkModeButton.getScene();
        if (scene == null) return;

        String darkTheme = getClass().getResource("/com/hiddless/java_fx/css/dark-theme.css").toExternalForm();
        String lightTheme = getClass().getResource("/com/hiddless/java_fx/css/admin.css").toExternalForm();

        if (isDarkMode) {
            scene.getStylesheets().remove(darkTheme);
            scene.getStylesheets().add(lightTheme);
            isDarkMode = false;
            NotificationUtil.showNotification("Tema değiştirildi", NotificationType.SUCCESS);
        } else {
            scene.getStylesheets().remove(lightTheme);
            scene.getStylesheets().add(darkTheme);
            isDarkMode = true;
            NotificationUtil.showNotification("Tema değiştirildi", NotificationType.SUCCESS);
        }
    }


    @FXML
    private void showNotifications(ActionEvent event) {
        try {
            System.out.println(getClass().getResource("/notification.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notification.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Bildirim Geçmişi");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Kullanıcı profil bilgileri gösterilecek pencere
    private UserDTO currentUser;

    public void setUser(UserDTO user) {
        System.out.println("✅ AdminController#setUser: " + user);

        this.currentUser = user;
    }


    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hiddless/java_fx/view/profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            UserDTO currentUser = SessionManager.getCurrentUser();

            // 🔥 Eğer currentUser null'sa patlamasın diye kontrol
            if (currentUser != null) {
                controller.setUser(currentUser);
            } else {
                System.err.println("currentUser null! setUser(...) çağrılmamış olabilir.");
                return; // pencereyi bile açma
            }

            Stage stage = new Stage();
            stage.setTitle("Profil");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backupData(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Yedekleme Dosyasını Kaydet");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
        File saveLocation = fileChooser.showSaveDialog(backupButton.getScene().getWindow());

        if (saveLocation != null) {
            try (FileOutputStream fos = new FileOutputStream(saveLocation);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                Optional<List<UserDTO>> allUsers = userDAO.list();

                if (allUsers.isPresent()) {
                    List<UserDTO> users = allUsers.get();

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    String json = gson.toJson(users);

                    // 1. Zip içerisine bir dosya girişi ekleniyor
                    ZipEntry zipEntry = new ZipEntry("backup.json");
                    zos.putNextEntry(zipEntry);
                    zos.write(json.getBytes());

                    // 3. Giriş kapatılıyor
                    zos.closeEntry();

                    NotificationUtil.showNotification("Yedekleme tamamlandı", NotificationType.SUCCESS);
                } else {
                    System.out.println("Yedeklenecek kullanıcı verisi bulunamadı.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                NotificationUtil.showNotification("Yedekleme başarısız oldu", NotificationType.ERROR);
            }
        }
    }

    @FXML
    private void restoreData(ActionEvent event) {
        // Daha önce alınmış bir yedek dosyadan veri geri yüklenecek
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Yedek Dosyasını Seç");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup File", "*.json"));

        java.io.File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        try {
            String jsonContent = Files.readString(Path.of(file.getAbsolutePath()));
            JsonArray jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray();

            Connection conn = SingletonPropertiesDBConnection.getInstance().getConnection();

            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM users");

            // Yeni kullanıcıları eklemek için INSERT sorgusu hazırlanır
            String insertSQL = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            for (JsonElement jsonElement : jsonArray){
                JsonObject user = jsonElement.getAsJsonObject();

                int id = user.get("id").getAsInt();
                String username = user.get("username").getAsString();
                String password = user.get("password").getAsString();
                String email = user.get("email").getAsString();


                // Verileri sırayla SQL'e yerleştir
                pstmt.setInt(1, id);
                pstmt.setString(2, username);
                pstmt.setString(3, password);
                pstmt.setString(4, email);


                // Sorguyu çalıştır
                pstmt.executeUpdate();
            }
            pstmt.close();
            System.out.println("Kullanıcılar başarıyla yüklendi!");


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Notebook
    private NotebookDAO notebookDAO = new NotebookDAO();

    @FXML
    private void notebook(ActionEvent event) {
        try {
            System.out.println(getClass().getResource("/noteForm.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/noteForm.fxml"));
            Parent root = loader.load();

            NoteController controller = loader.getController();

            UserDTO currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                System.err.println("currentUser null! setUser(...) çağrılmamış olabilir.");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("Yeni Not Ekle");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            NotebookDTO createdNote = controller.getCreatedNote();
            controller.setCreatedNote(createdNote);

            if (createdNote != null) {
                System.out.println("Yeni not oluşturuldu:");
                NotificationUtil.showNotification("Yeni not oluşturuldu:", NotificationType.SUCCESS);
                createdNote.setUserDTO(currentUser);
                notebookDAO.save(createdNote);
                notebookDAO.saveToFile(createdNote);
                System.out.println(createdNote.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

