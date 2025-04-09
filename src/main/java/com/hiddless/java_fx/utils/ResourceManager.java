package com.hiddless.java_fx.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceManager {
    private static final ObservableResourceFactory factory = new ObservableResourceFactory();

    static {
        Locale defaultLocale = new Locale("tr");
        ResourceBundle bundle = ResourceBundle
                .getBundle("com.hiddless.java_fx.view.languages", defaultLocale);
        factory.setResources(bundle);
    }

    public static ObservableResourceFactory getFactory() {
        return factory;
    }


    public static void changeLanguage(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("com.hiddless.java_fx.view.languages", locale);
        factory.setResources(bundle);
    }

}
