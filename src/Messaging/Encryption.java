package Messaging;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.*;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

/**
 * Class encapsulating OpenPGP actions
 */
public class Encryption {

    private static final int BUFFER_SIZE = 1 << 16;


    public static void signEncryptFile(
            String fileName,
            PGPPublicKey publicKey,
            PGPSecretKey secretKey,
            char[] password,
            boolean encrypt,
            boolean sign,
            boolean compress,
            boolean radix64,
            boolean is3DES)
            throws Exception
    {

        // Initialize Bouncy Castle security provider
        OutputStream out = new FileOutputStream(fileName + ".pgp");
        FileInputStream in = new FileInputStream(fileName);

        if (radix64) {//RADIX64
            out = new ArmoredOutputStream(out);
        }
        //zasto
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        if(compress) {//ZIP

            PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                    PGPCompressedData.ZIP);

            //write to header that file is zipped
            org.bouncycastle.openpgp.PGPUtil.writeFileToLiteralData(comData.open(byteOut),
                    PGPLiteralData.BINARY, new File(fileName));
            comData.close();
        }

        JcePGPDataEncryptorBuilder c = new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(sign).setSecureRandom(new SecureRandom()).setProvider("BC");

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(c);

        JcePublicKeyKeyEncryptionMethodGenerator d = new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider(new BouncyCastleProvider()).setSecureRandom(new SecureRandom());

        cPk.addMethod(d);

        byte[] bytes = byteOut.toByteArray();

        OutputStream cOut = cPk.open(out, bytes.length);

        cOut.write(bytes);

        cOut.close();



        // ENCRYPT
        /*BcPGPDataEncryptorBuilder dataEncryptor;
        PGPEncryptedDataGenerator encryptedDataGenerator = null;*/
/*
        if (encrypt) {

            // biranje algoritma
            if (is3DES) {
                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
            }
            else {
                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.IDEA);
            }

            // nesto radi
            dataEncryptor.setWithIntegrityPacket(true);

            // generisanje sesijskog kljuca
            dataEncryptor.setSecureRandom(new SecureRandom());

            // inicijalizacija enkriptora podataka
            encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);

            // provarava da li je kljuc ElGamal
            if (!publicKey.isEncryptionKey()) {
                // error
                return;
            }

            // enkriptovanje sesijskog kljuca
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

            // enkriptovanje poruke
            out = encryptedDataGenerator.open(out, new byte[Encryption.BUFFER_SIZE]);

        }

        // COMPRESS
        PGPCompressedDataGenerator compressedDataGenerator = null;
        if(compress) {
            compressedDataGenerator =
                    new PGPCompressedDataGenerator(compress ? PGPCompressedData.ZIP : PGPCompressedData.UNCOMPRESSED);
            out = compressedDataGenerator.open(out, new byte[Encryption.BUFFER_SIZE]);
        }
        // SIGN
        PGPSignatureGenerator signatureGenerator = null;
        if (sign) {

            // da li je DSA kljuc
            if (!secretKey.isSigningKey()) {
                // error
                return;
            }

            // dohvata pravi private key
            PGPPrivateKey privateKey = findPrivateKey(secretKey, password);

            // generise i nicijalizuje sign generator
            PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(),
                    HashAlgorithmTags.SHA1);
            signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);


            String username = secretKey.getPublicKey().getUserIDs().next();

            //
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
                //noinspection deprecation

            // podesi koji je user
            spGen.setSignerUserID(false, username.getBytes(StandardCharsets.UTF_8)); // mozda dodje do greske
//            spGen.setSignerUserID(false, username);

            // doda heshiran deo potpisa
            signatureGenerator.setHashedSubpackets(spGen.generate());

            signatureGenerator.generateOnePassVersion(false).encode(out);
        }

        // Pravljenje header-a
        /*PGPLiteralDataGenerator headerDataGenerator = new PGPLiteralDataGenerator();
        OutputStream literalOut = headerDataGenerator.open(
                out,
                PGPLiteralData.BINARY,
                fileName,
                new Date(),
                new byte [Encryption.BUFFER_SIZE] );
        // Main loop - read the "in" stream, compress, encrypt and write to the "out" stream

        // dohvatanje plain teksta

        byte[] buf = new byte[Encryption.BUFFER_SIZE];
        int len;

        // sifruje chunk po chunk
        while ((len = in.read(buf)) > 0) {

            // enkriptuje,
            literalOut.write(buf, 0, len);
            if (sign)
                signatureGenerator.update(buf, 0, len);
        }


        headerDataGenerator.close();
        // Generate the signature, compress, encrypt and write to the "out" stream

        if (sign)
            signatureGenerator.generate().encode(out);
        if (compress)
            compressedDataGenerator.close();*/
        /*if (encrypt)
            encryptedDataGenerator.close();*/
        //literalOut.close();
        in.close();
        out.close();
    }

    static PGPPrivateKey findPrivateKey(PGPSecretKey pgpSecKey, char[] pass)
            throws PGPException
    {
        if (pgpSecKey == null) return null;

        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(
                new BcPGPDigestCalculatorProvider()).build(pass);
        return pgpSecKey.extractPrivateKey(decryptor);
    }


}