package com.hiddless.java_fx.utils;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ResourceBundle;

public class ObservableResourceFactory {
    private final ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();


    public ResourceBundle getResources() {
        return resources.get();
    }


    public void setResources(ResourceBundle bundle) {
        resources.set(bundle);
    }

    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resources);
            }

            @Override
            protected String computeValue() {
                ResourceBundle bundle = getResources();
                if (bundle != null && bundle.containsKey(key)) {
                    return bundle.getString(key);
                } else {
                    return "???" + key + "???";
                }
            }
        };
    }

}
