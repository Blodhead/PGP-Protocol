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
            throws Exception {
        OutputStream out = new FileOutputStream(fileName + ".pgp");
        FileInputStream in = new FileInputStream(fileName);


        if (radix64)
            out = new ArmoredOutputStream(out);

        // Provera odabranog algoritma
        int algo;

        if (is3DES == false)
            algo = PGPEncryptedData.IDEA;
        else
            algo = PGPEncryptedData.TRIPLE_DES;

        JcePGPDataEncryptorBuilder c = new JcePGPDataEncryptorBuilder(algo).setWithIntegrityPacket(sign).setSecureRandom(new SecureRandom()).setProvider("BC");
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(c);

        //encrypt with key
        //PGPKeyEncryptionMethodGenerator method_gen = new JcePBEKeyEncryptionMethodGenerator(password).setProvider("BC");
        //encryptedDataGenerator.addMethod(method_gen);

        JcePublicKeyKeyEncryptionMethodGenerator d = new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC").setSecureRandom(new SecureRandom());

        encryptedDataGenerator.addMethod(d);

        OutputStream compressedOut = encryptedDataGenerator.open(out, new byte[BUFFER_SIZE]);

        // Inicijalizacija generatora za kompresiju
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

        //ZIP
        if (compress)
            compressedOut = compressedDataGenerator.open(compressedOut);

        PGPSignatureGenerator signatureGenerator = null;
        // Ako imamo kljuc za potpis:
        if (secretKey != null && sign == true) {
            PGPPublicKey pubSigKey = secretKey.getPublicKey();

            PBESecretKeyDecryptor decryptorFactory = new BcPBESecretKeyDecryptorBuilder(
                    new BcPGPDigestCalculatorProvider()).build(password);
            PGPPrivateKey privateKey = secretKey.extractPrivateKey(decryptorFactory);
            //secretKey = secretKey.extractPrivateKey(password);

            PGPContentSignerBuilder singbuilder = new JcaPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);
            singbuilder.build(publicKey.getAlgorithm(), privateKey);

            signatureGenerator = new PGPSignatureGenerator(singbuilder);

            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
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
        in.close();

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