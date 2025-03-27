module com.hiddless.java_fx {

    requires javafx.controls;

    requires javafx.fxml;


    requires org.controlsfx.controls;


    requires com.dlsc.formsfx;


    requires net.synedra.validatorfx;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires static lombok;
    requires java.sql;
    requires org.apache.poi.poi;
    requires java.desktop;
    requires jbcrypt;

    opens com.hiddless.java_fx to javafx.fxml;
    opens com.hiddless.java_fx.dto to javafx.base, lombok;
    opens com.hiddless.java_fx.controller to javafx.fxml;
    opens com.hiddless.java_fx.dao to java.sql;
    opens com.hiddless.java_fx.database to java.sql;

    exports com.hiddless.java_fx;
    exports com.hiddless.java_fx.database;
    exports com.hiddless.java_fx.dao;

    opens com.hiddless.java_fx.utils to javafx.base, lombok;
}