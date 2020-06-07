package com.gmail.cristianberme.practices.passbook;

import com.ryantenney.passkit4j.Pass;
import com.ryantenney.passkit4j.PassResource;
import com.ryantenney.passkit4j.PassSerializationException;
import com.ryantenney.passkit4j.PassSerializer;
import com.ryantenney.passkit4j.model.Barcode;
import com.ryantenney.passkit4j.model.BarcodeFormat;
import com.ryantenney.passkit4j.model.Generic;
import com.ryantenney.passkit4j.model.TextField;
import com.ryantenney.passkit4j.sign.PassSigner;
import com.ryantenney.passkit4j.sign.PassSignerImpl;
import com.ryantenney.passkit4j.sign.PassSigningException;

import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException, PassSigningException, CertificateException {
        // Create a file called "pass.properties" in the resources folder and include the following keys:
        //      - pass.passTypeIdentifier       : The pass type ID's name, generated on Apple's developer portal
        //      - pass.teamIdentifier           : The team ID associated with your developer account
        //      - keystore.password             : The password of the provided .p12 file (see comment below)
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/pass.properties"));
        String serialNumber = UUID.randomUUID().toString();

        Pass pass = new Pass()
                .passTypeIdentifier(properties.getProperty("pass.passTypeIdentifier"))
                .serialNumber(serialNumber)
                .teamIdentifier(properties.getProperty("pass.teamIdentifier"))
                .description("Passbook practice")
                .organizationName("Practice")
                .barcodes(
                        new Barcode(BarcodeFormat.QR, "https://github.com/crberme")
                                .messageEncoding("iso-8859-1")
                )
                .passInformation(
                        new Generic()
                                .primaryFields(
                                        new TextField("Pass serial number", serialNumber)
                                )
                )
                .files(
                        // Create a 29x29px png image and save it in the resources folder as "icon.png"
                        new PassResource("src/main/resources/icon.png")
                );

        String password = properties.getProperty("keystore.password");
        // Copy the .p12 file generated from the pass type ID certificate in the resources folder as "keystore.p12"
        // Also copy the certificate itself as "pass.cer"
        // The following code implies that the keystore and the private key share the same password
        // If that's not the case change the following instructions accordingly
        PassSigner signer = PassSignerImpl.builder()
                .signingCertificate((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream("src/main/resources/pass.cer")))
                .intermediateCertificate(new URL("https://developer.apple.com/certificationauthority/AppleWWDRCA.cer").openStream())
                .keystore(new FileInputStream("src/main/resources/keystore.p12"), password)
                .password(password)
                .build();

        PassSerializer.writePkPassArchive(pass, signer, new FileOutputStream(System.getProperty("user.home") + File.separator + "Practice.pkpass"));
    }
}
