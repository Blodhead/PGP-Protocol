package Messaging;

import App.View_User;
import org.bouncycastle.bcpg.LiteralDataPacket;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.*;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Iterator;
import java.util.Optional;

public class Decryption {

    public static void decryptAndVerify(
            File filename,
            char[] passphrase)
            throws IOException, PGPException, SignatureException {
//        PGPSecretKeyRingCollection secretKeyCollection = new PGPSecretKeyRingCollection(Menu.privateKeyRingTableModel.getPrivateKeys());
//        PGPPublicKeyRingCollection publicKeyCollection = new PGPPublicKeyRingCollection(Menu.publicKeyRingTableModel.getPublicKeys());

        InputStream in = new FileInputStream(filename);
        OutputStream out = new FileOutputStream("decrypted.txt");
        String outFileName;
        PGPPublicKeyEncryptedData data = null;

        boolean signed = false;


        PGPObjectFactory pgpFactory = new PGPObjectFactory(
                PGPUtil.getDecoderStream(in),
                new BcKeyFingerprintCalculator());

        PGPEncryptedDataList encryptedDataList;
        Object o = pgpFactory.nextObject();

        //dohvatanje headera
        if (o instanceof PGPEncryptedDataList)
            encryptedDataList = (PGPEncryptedDataList) o;
        else
            encryptedDataList = (PGPEncryptedDataList) pgpFactory.nextObject();

        //Object iterator
        Iterator encObjects = encryptedDataList.getEncryptedDataObjects();

        PGPPrivateKey sKey = null;
        PGPSecretKey secretKey;

        while (encObjects.hasNext()) {
            // Pronalazak kljuca koji oddgovara mom privatnom kljucu
            Object obj = encObjects.next();
            if (!(obj instanceof PGPPublicKeyEncryptedData))
                continue;
            PGPPublicKeyEncryptedData encData = (PGPPublicKeyEncryptedData) obj;
            //radi samo za ElGamal
            secretKey = User.getSecretKey(String.valueOf('#' + encData.getKeyID()));

            if (secretKey == null)
                continue;

            // Nasli smo tajni kljuc i sad radimo dohvatanje privatnog, ukoliko je moguce
            PBESecretKeyDecryptor secretDecryptorFactory = new BcPBESecretKeyDecryptorBuilder(
                    new BcPGPDigestCalculatorProvider()).build(passphrase);
            sKey = secretKey.extractPrivateKey(secretDecryptorFactory);

            if (sKey != null) {
                // Nasli smo kljuc
                data = encData;
                break;
            }
        }

        BcPublicKeyDataDecryptorFactory pubdecgen = new BcPublicKeyDataDecryptorFactory(sKey);
        InputStream clear = data.getDataStream(pubdecgen);

        PGPObjectFactory objFactory = new PGPObjectFactory(clear, null);

        Object file_buffer = objFactory.nextObject();
        Object sigedLiteralData = null;
        PGPObjectFactory pgpFact = null;

        //ZIP checks
        if (file_buffer instanceof PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) file_buffer;
            pgpFact = new PGPObjectFactory(cData.getDataStream(), null);
            file_buffer = pgpFact.nextObject();
            if (file_buffer instanceof PGPOnePassSignatureList)
                sigedLiteralData = pgpFact.nextObject();
        }

        if (file_buffer instanceof PGPLiteralData) {
            // Poruka samo enkriptovana
            PGPLiteralData header = (PGPLiteralData) file_buffer;
            String head_file_name = header.getFileName();
            InputStream unc = header.getInputStream();
            int ch;

            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }

        } else if (file_buffer instanceof PGPOnePassSignatureList) {
            // Poruka enkriptovana i potpisana sa OnePass
            signed = true;
        }
        if (file_buffer instanceof PGPSignatureList) {
            // Poruka je potpisana i enkriptovana
            signed = true;

           /* PGPPublicKey pubKey = User.getPublicKey(sigWrap.getKeyID());
            if (pubKey == null) {
                JOptionPane.showMessageDialog(error_msg,
                        "No such key to decrypt!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                in.close();
                out.close();
                return;
            } else {
                decryptionRes.setSignee(new PPGPPJavniKljuc(pubKey));
                sigWrap.initVerify(pubKey, "BC");
                sigLiteralData = (PGPLiteralData) pgpFact.nextObject();
                outFileName = processLiteralData((PGPLiteralData) sigLiteralData, out, sigWrap);
                decryptionRes.setIsSignatureValid(sigWrap.verify(null));
            }
*/











/*

        InputStream clear = data.getDataStream(publicDecryptorFactory);

        PGPObjectFactory plainFact = new PGPObjectFactory(clear);

        Object message = plainFact.nextObject();
        Object sigLiteralData = null;
        PGPObjectFactory pgpFact = null;




        RezultatDekripcije decryptionRes = new RezultatDekripcije();
        String outFileName = "";
        PGPPublicKeyEncryptedData pbe = null;

        Path outPath = Paths.get(filename);
        OutputStream fOut = new FileOutputStream(outPath.getParent() + "/decrypted.txt");

        PGPObjectFactory pgpFactory = new PGPObjectFactory(
                PGPUtil.getDecoderStream(new FileInputStream(filename)),
                new BcKeyFingerprintCalculator());

        PGPEncryptedDataList enc;

        Object o = pgpFactory.nextObject();
//        PGPObjectFactory plainFact = pgpFactory;

        if (o instanceof PGPEncryptedDataList) {
            encryptedDataList = (PGPEncryptedDataList) o;

            Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPEncryptedData pbe = null;

            while (sKey == null && it.hasNext()) {
                pbe = it.next();

                try {

                    String keyId = String.valueOf(((PGPPublicKeyEncryptedData) pbe).getKeyID());

                    PGPSecretKey secretKey = User.getSecretKey(keyId);

                    sKey = Encryption.findPrivateKey(secretKey, passphrase);
                } catch (Exception ignored) {

                }
            }

            if (sKey == null) {
                return Optional.empty();
            }

            InputStream clear = ((PGPPublicKeyEncryptedData) pbe).getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
            pgpFactory = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());
        }

        PGPOnePassSignatureList onePassSignatureList = null;
        PGPSignatureList signatureList = null;
        PGPCompressedData compressedData;

        Object msgDecryption = pgpFactory.nextObject();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while (msgDecryption != null) {

            if (msgDecryption instanceof PGPCompressedData) {
                compressedData = (PGPCompressedData) msgDecryption;
                pgpFactory = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
                msgDecryption = pgpFactory.nextObject();
            }

            if (msgDecryption instanceof PGPLiteralData) {

                Streams.pipeAll(((PGPLiteralData) msgDecryption).getInputStream(), outStream);
            }
            if (msgDecryption instanceof PGPSignatureList) {
                signatureList = (PGPSignatureList) msgDecryption;
            }
            if (msgDecryption instanceof PGPOnePassSignatureList) {
                onePassSignatureList = (PGPOnePassSignatureList) msgDecryption;
            }

            msgDecryption = pgpFactory.nextObject();
        }

        outStream.close();
        fOut.write(outStream.toByteArray());
        fOut.close();

        PGPPublicKey publicKey;
        byte[] output = outStream.toByteArray();

        if (onePassSignatureList == null || signatureList == null) {
            return Optional.of("FILE IS NOT SIGNED");
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < onePassSignatureList.size(); i++) {
            PGPOnePassSignature ops = onePassSignatureList.get(0);

//            String keyId = String.valueOf(ops.getKeyID());

            publicKey = User.getPublicKeyDSA(ops.getKeyID());

            if (publicKey == null) {
                return Optional.of("FILE IS SIGNED, BUT NOT VERIFIED");
            }

            ops.init(new BcPGPContentVerifierBuilderProvider(), publicKey);
            ops.update(output);
            PGPSignature signature = signatureList.get(i);

            if (!ops.verify(signature)) {
                throw new SignatureException("Unsuccessful signature check");
            }

            Iterator<?> userIds = publicKey.getUserIDs();
            while (userIds.hasNext()) {
                String userId = (String) userIds.next();
                stringBuilder.append("Signed by: ").append(userId).append(System.lineSeparator());
            }
        }

        return Optional.of(stringBuilder.toString());
    }
*/


        }
    }
}