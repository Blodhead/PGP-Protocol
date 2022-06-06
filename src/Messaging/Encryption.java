package Messaging;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.*;
import org.bouncycastle.util.io.Streams;

import java.io.*;
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
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        OutputStream out = new FileOutputStream(fileName + ".pgp");

        if (radix64) {
            out = new ArmoredOutputStream(out);
        }

        // ENCRYPT
        BcPGPDataEncryptorBuilder dataEncryptor;
//        OutputStream encryptedOut = out;
        PGPEncryptedDataGenerator encryptedDataGenerator = null;

        if (encrypt) {
            if (is3DES) {
                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
            }
            else {
                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.IDEA);
            }
            dataEncryptor.setWithIntegrityPacket(true);
            dataEncryptor.setSecureRandom(new SecureRandom());

            encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);

            if (!publicKey.isEncryptionKey()) {
                // error
                return;
            }

            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));
            out = encryptedDataGenerator.open(out, new byte[Encryption.BUFFER_SIZE]);

        }

        // COMPRESS
        PGPCompressedDataGenerator compressedDataGenerator =
                new PGPCompressedDataGenerator(compress ? PGPCompressedData.ZIP : PGPCompressedData.UNCOMPRESSED);
        out = compressedDataGenerator.open(out, new byte [Encryption.BUFFER_SIZE]);

        // SIGN
        PGPSignatureGenerator signatureGenerator = null;

        if (sign) {

            if (!secretKey.isSigningKey()) {
                // error
                return;
            }

            PGPPrivateKey privateKey = findPrivateKey(secretKey, password);
            PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(),
                    HashAlgorithmTags.SHA1);
            signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);


            Iterator<String> it = secretKey.getPublicKey().getUserIDs();
//            if (it.hasNext()) {
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
                //noinspection deprecation
            spGen.setSignerUserID(false, secretKey.getUserIDs().next());
            signatureGenerator.setHashedSubpackets(spGen.generate());
//            }
            signatureGenerator.generateOnePassVersion(false).encode(out);
        }

        // Initialize literal data generator
        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        OutputStream literalOut = literalDataGenerator.open(
                out,
                PGPLiteralData.BINARY,
                fileName,
                new Date(),
                new byte [Encryption.BUFFER_SIZE] );

        // Main loop - read the "in" stream, compress, encrypt and write to the "out" stream
        FileInputStream in = new FileInputStream(fileName);
        byte[] buf = new byte[Encryption.BUFFER_SIZE];
        int len;
        while ((len = in.read(buf)) > 0) {
            literalOut.write(buf, 0, len);
            if (sign)
                signatureGenerator.update(buf, 0, len);
        }

        in.close();
        literalDataGenerator.close();
        // Generate the signature, compress, encrypt and write to the "out" stream
        if (sign)
            signatureGenerator.generate().encode(out);
        if (compress)
            compressedDataGenerator.close();
        if (encrypt)
            encryptedDataGenerator.close();
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