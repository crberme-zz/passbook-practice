package com.gmail.cristianberme.practices.passbook.models.visuals;

import com.gmail.cristianberme.practices.passbook.models.constants.BarcodeFormat;

public class Barcode {
    private BarcodeFormat format;
    private String message;
    private String messageEncoding;

    public Barcode(BarcodeFormat format, String message, String messageEncoding) {
        this.format = format;
        this.message = message;
        this.messageEncoding = messageEncoding;
    }

    public Barcode() {
    }

    public BarcodeFormat getFormat() {
        return format;
    }

    public void setFormat(BarcodeFormat format) {
        this.format = format;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }
}
