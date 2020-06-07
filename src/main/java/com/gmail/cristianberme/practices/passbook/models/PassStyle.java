package com.gmail.cristianberme.practices.passbook.models;

import java.util.List;
import java.util.Map;

public class PassStyle {
    List<Map<String, Object>> primaryFields;

    public PassStyle(List<Map<String, Object>> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public PassStyle() {
    }

    public List<Map<String, Object>> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(List<Map<String, Object>> primaryFields) {
        this.primaryFields = primaryFields;
    }
}
