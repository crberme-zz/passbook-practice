package com.gmail.cristianberme.practices.passbook.models;

import java.util.Map;

public class PassStyle {
    Map<String, Object> primaryFields;

    public PassStyle(Map<String, Object> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public PassStyle() {
    }

    public Map<String, Object> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(Map<String, Object> primaryFields) {
        this.primaryFields = primaryFields;
    }
}
