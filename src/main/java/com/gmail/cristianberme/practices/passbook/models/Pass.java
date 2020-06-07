package com.gmail.cristianberme.practices.passbook.models;

import com.gmail.cristianberme.practices.passbook.models.styles.GenericPass;
import com.gmail.cristianberme.practices.passbook.models.visuals.Barcode;

import java.util.List;

public class Pass {
    private String description;
    private Integer formatVersion;
    private String organizationName;
    private String passTypeIdentifier;
    private String serialNumber;
    private String teamIdentifier;
    private GenericPass generic;
    private List<Barcode> barcodes;
    private Barcode barcode;

    public Pass(String description, Integer formatVersion, String organizationName, String passTypeIdentifier, String serialNumber, String teamIdentifier, GenericPass generic, List<Barcode> barcodes, Barcode barcode) {
        this.description = description;
        this.formatVersion = formatVersion;
        this.organizationName = organizationName;
        this.passTypeIdentifier = passTypeIdentifier;
        this.serialNumber = serialNumber;
        this.teamIdentifier = teamIdentifier;
        this.generic = generic;
        this.barcodes = barcodes;
        this.barcode = barcode;
    }

    public Pass() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(Integer formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPassTypeIdentifier() {
        return passTypeIdentifier;
    }

    public void setPassTypeIdentifier(String passTypeIdentifier) {
        this.passTypeIdentifier = passTypeIdentifier;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTeamIdentifier() {
        return teamIdentifier;
    }

    public void setTeamIdentifier(String teamIdentifier) {
        this.teamIdentifier = teamIdentifier;
    }

    public GenericPass getGeneric() {
        return generic;
    }

    public void setGeneric(GenericPass generic) {
        this.generic = generic;
    }

    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<Barcode> barcodes) {
        this.barcodes = barcodes;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }
}
