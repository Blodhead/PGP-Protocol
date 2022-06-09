package Messaging;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSigner;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.bc.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
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
        OutputStream out = new FileOutputStream(fileName + ".pgp");
        FileInputStream in = new FileInputStream(fileName);

        PGPSignatureGenerator signatureGenerator = null;
        if (radix64)
            out = new ArmoredOutputStream(out);

        // Provera odabranog algoritma
        int algo = PGPEncryptedData.CAST5;

        if (is3DES == false)
            algo = PGPEncryptedData.IDEA;
        else
            algo = PGPEncryptedData.TRIPLE_DES;

        JcePGPDataEncryptorBuilder c = new JcePGPDataEncryptorBuilder(algo).setWithIntegrityPacket(sign).setSecureRandom(new SecureRandom()).setProvider("BC");
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(c);
        PGPKeyEncryptionMethodGenerator method_gen = new JcePBEKeyEncryptionMethodGenerator(password).setProvider("BC");
        //org.apache.nifi.processors.standard.util.PGPUtil.encrypt(in, out, algorithm, provider, PGPEncryptedData.AES_128, filename, encryptionMethodGenerator);
        encryptedDataGenerator.addMethod(method_gen);

        OutputStream compressedOut = encryptedDataGenerator.open(out, new byte[BUFFER_SIZE]);

        // Inicijalizacija generatora za kompresiju
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

        if (compress)
            compressedOut = compressedDataGenerator.open(compressedOut);

        // Ako imamo kljuc za potpis:
        if (secretKey != null && sign == true) {
            PGPPublicKey pubSigKey = secretKey.getPublicKey();

            PBESecretKeyDecryptor decryptorFactory = new BcPBESecretKeyDecryptorBuilder(
                    new BcPGPDigestCalculatorProvider()).build(password);
            PGPPrivateKey privateKey = secretKey.extractPrivateKey(decryptorFactory);
            //secretKey = secretKey.extractPrivateKey(password);

            int digest;
            PGPContentSignerBuilder singbuilder = new JcaPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(),HashAlgorithmTags.SHA1);
            singbuilder.build(publicKey.getAlgorithm(),privateKey);
            signatureGenerator = new PGPSignatureGenerator(singbuilder);

            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT,privateKey);
            Iterator it = pubSigKey.getUserIDs();

            if (it.hasNext()) {
                PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
                spGen.setSignerUserID(false, (String) it.next());
                signatureGenerator.setHashedSubpackets(spGen.generate());
            }
            signatureGenerator.generateOnePassVersion(false).encode(compressedOut);
        }

        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        OutputStream literalOut = literalDataGenerator.open(compressedOut, PGPLiteralData.BINARY, fileName, new Date(),
                new byte[BUFFER_SIZE]);
        FileInputStream inputFileStream = new FileInputStream(fileName);
        byte[] buf = new byte[BUFFER_SIZE];
        int len;
        while ((len = inputFileStream.read(buf)) > 0) {
            literalOut.write(buf, 0, len);
            if (signatureGenerator != null)
                signatureGenerator.update(buf, 0, len);
        }

        literalOut.close();
        literalDataGenerator.close();
        if (signatureGenerator != null)
            signatureGenerator.generate().encode(compressedOut);

        compressedOut.close();
        compressedDataGenerator.close();
        encryptedDataGenerator.close();
        inputFileStream.close();
        out.close();

////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*
        // Initialize Bouncy Castle security provider
        OutputStream out = new FileOutputStream(fileName + ".pgp");
        FileInputStream in = new FileInputStream(fileName);

        if (radix64) {
            out = new ArmoredOutputStream(out);
        }

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        if(sign){
            Signature dsa = Signature.getInstance("SHA/DSA");
            //dsa.initSign(User.get);
        }

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                PGPCompressedData.ZIP);

        org.bouncycastle.openpgp.PGPUtil.writeFileToLiteralData(comData.open(bOut),
                PGPLiteralData.BINARY, new File(fileName));
        comData.close();

        JcePGPDataEncryptorBuilder c = new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(sign).setSecureRandom(new SecureRandom()).setProvider("BC");

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(c);

        JcePublicKeyKeyEncryptionMethodGenerator d = new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider(new BouncyCastleProvider()).setSecureRandom(new SecureRandom());

        cPk.addMethod(d);

        byte[] bytes = bOut.toByteArray();

        OutputStream cOut = cPk.open(out, bytes.length);

        cOut.write(bytes);

        cOut.close();
/////////////////////////////////////////////////////////////////////////////////////////////////////

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

        //////////////////////////////////////////////////////////////////////////////////////
        /*in.close();
        out.close();*/
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