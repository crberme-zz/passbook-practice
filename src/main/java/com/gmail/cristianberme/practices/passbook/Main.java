package com.gmail.cristianberme.practices.passbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.cristianberme.practices.passbook.models.Pass;
import com.gmail.cristianberme.practices.passbook.models.constants.BarcodeFormat;
import com.gmail.cristianberme.practices.passbook.models.styles.GenericPass;
import com.gmail.cristianberme.practices.passbook.models.visuals.Barcode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, NoSuchProviderException, KeyStoreException, CMSException, OperatorCreationException {
        ObjectMapper jsonMapper = new ObjectMapper();

        // Create a file called "pass.properties" in the resources folder and include the following keys:
        //      - pass.passTypeIdentifier       : The pass type ID's name, generated on Apple's developer portal
        //      - pass.teamIdentifier           : The team ID associated with your developer account
        //      - keystore.password             : The password of the provided .p12 file (see comment below)
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/pass.properties"));
        String serialNumber = UUID.randomUUID().toString();

        // Create the raw pass package and copy the resources to it
        File passFolder = Files.createTempDirectory(null).toFile();
        // Create a 29x29px png image and save it in the resources folder as "icon.png"
        FileUtils.copyFile(new File("src/main/resources/icon.png"), new File(passFolder.getAbsolutePath() + File.separator + "icon.png"));

        // Create the pass.json file
        Pass pass = new Pass();
        pass.setFormatVersion(1);
        pass.setPassTypeIdentifier(properties.getProperty("pass.passTypeIdentifier"));
        pass.setSerialNumber(serialNumber);
        pass.setTeamIdentifier(properties.getProperty("pass.teamIdentifier"));
        pass.setDescription("Passbook practice");
        pass.setOrganizationName("Practice");

        Barcode barcode = new Barcode();
        barcode.setFormat(BarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("https://github.com/crberme");
        barcode.setMessageEncoding("iso-8859-1");
        pass.setBarcodes(Collections.singletonList(barcode));
        pass.setBarcode(barcode);

        GenericPass generic = new GenericPass();
        Map<String, Object> primaryField = new HashMap<>();
        primaryField.put("key", "Pass serial number");
        primaryField.put("value", serialNumber);
        generic.setPrimaryFields(Arrays.asList(primaryField));
        pass.setGeneric(generic);

        File passJson = new File(passFolder.getAbsolutePath() + File.separator + "pass.json");
        jsonMapper.writeValue(new FileOutputStream(passJson), pass);

        // Create the manifest.json
        Map<String, String> manifest = new HashMap<>();
        for(File passFile : passFolder.listFiles()) {
            manifest.put(passFile.getName(), getSHA1Hash(passFile));
        }

        File manifestJson = new File(passFolder.getAbsolutePath() + File.separator + "manifest.json");
        jsonMapper.writeValue(new FileOutputStream(manifestJson), manifest);

        // Create the signature file
        File signatureFile = new File(passFolder.getAbsolutePath() + File.separator + "signature");

        // Copy the .p12 file generated from the pass type ID certificate in the resources folder as "keystore.p12"
        // Also copy the certificate itself as "pass.cer"
        // The following code implies that the keystore and the private key share the same password
        // If that's not the case change the following instructions accordingly
        File keyStoreFile = new File("src/main/resources/keystore.p12");
        File certFile = new File("src/main/resources/pass.cer");
        String password = properties.getProperty("keystore.password");
        IOUtils.write(getSignature(manifestJson, keyStoreFile, certFile, password, password), new FileOutputStream(signatureFile));

        // Write the result package to a .pkpass file
        File pkpassFile = new File(System.getProperty("user.home") + File.separator + "Practice.pkpass");
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(pkpassFile))) {
            ZipEntry zipEntry;
            byte[] passFileData;
            for(File passFile : passFolder.listFiles()) {
                zipEntry = new ZipEntry(passFile.getName());
                outputStream.putNextEntry(zipEntry);

                passFileData = IOUtils.toByteArray(new FileInputStream(passFile));
                for(int i = 0; i < passFileData.length; i++) {
                    outputStream.write(passFileData[i]);
                }
            }
        }
    }

    private static String getSHA1Hash(final File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        return DatatypeConverter.printHexBinary(digest.digest(IOUtils.toByteArray(new FileInputStream(file)))).toLowerCase();
    }

    private static byte[] getSignature(final File manifestFile, final File keyStoreFile, final File certFile,
                                       final String keyStorePassword, final String keyPassword)
            throws CertificateException, NoSuchProviderException, IOException, KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CMSException, OperatorCreationException {

        // Initialize the Bouncy Castle provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        // Load the certificates
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
        X509Certificate intermediateCertificate = (X509Certificate) certificateFactory
                .generateCertificate(new URL("https://developer.apple.com/certificationauthority/AppleWWDRCA.cer").openStream());
        X509Certificate signingCertificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(certFile));

        // Load the private key
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());
        PrivateKey key = (PrivateKey) keyStore.getKey("1", keyPassword.toCharArray());

        // Encrypt the data
        CMSSignedDataGenerator dataGenerator = new CMSSignedDataGenerator();
        dataGenerator.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build("SHA1withRSA", key, signingCertificate));
        dataGenerator.addCertificates(new JcaCertStore(Arrays.asList(intermediateCertificate, signingCertificate)));

        CMSProcessableByteArray typedData = new CMSProcessableByteArray(IOUtils.toByteArray(new FileInputStream(manifestFile)));
        CMSSignedData data = dataGenerator.generate(typedData);
        return data.getEncoded();
    }
}
