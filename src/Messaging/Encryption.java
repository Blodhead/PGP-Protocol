//package Messaging;
//
//import org.bouncycastle.bcpg.ArmoredOutputStream;
//import org.bouncycastle.bcpg.HashAlgorithmTags;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openpgp.*;
//import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
//import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
//import org.bouncycastle.openpgp.operator.bc.*;
//import org.bouncycastle.util.io.Streams;
//
//import java.io.*;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.Provider;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.security.SignatureException;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.Optional;
//
///**
// * Class encapsulating OpenPGP actions
// */
//public class PGPUtility {
//
//    private static final int BUFFER_SIZE = 1 << 16;
//
//    /**
//     * Runs sign and encrypt of the given file, saves result to fileName.pgp
//     * @param fileName file path to sign/encrypt
//     * @param publicKey used for encryption
//     * @param secretKey used for signing
//     * @param password used for accessing the secretKey
//     * @param encrypt whether to encrypt
//     * @param sign whether to sign
//     * @param compress whether to compress
//     * @param radix64 whether to run radix-64 conversion
//     * @param isCAST5 whether CAST5 or 3DES is used (true for CAST5, false for 3DES)
//     * @throws Exception if any operation fails
//     */
//    public static void signEncryptFile(
//            String fileName,
//            PGPPublicKey publicKey,
//            PGPSecretKey secretKey,
//            char[] password,
//            boolean encrypt,
//            boolean sign,
//            boolean compress,
//            boolean radix64,
//            boolean isCAST5)
//            throws Exception
//    {
//
//        // Initialize Bouncy Castle security provider
//        Provider provider = new BouncyCastleProvider();
//        Security.addProvider(provider);
//
//        OutputStream out = new FileOutputStream(fileName + ".pgp");
//
//        if (radix64) {
//            out = new ArmoredOutputStream(out);
//        }
//
//        // ENCRYPT
//        BcPGPDataEncryptorBuilder dataEncryptor;
//        OutputStream encryptedOut = out;
//        PGPEncryptedDataGenerator encryptedDataGenerator = null;
//
//        if (encrypt) {
//            if (isCAST5) {
//                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.CAST5);
//            }
//            else {
//                dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
//            }
//            dataEncryptor.setWithIntegrityPacket(true);
//            dataEncryptor.setSecureRandom(new SecureRandom());
//
//            encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
//
//            if (!publicKey.isEncryptionKey()) {
//                // error
//                return;
//            }
//
//            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));
//            encryptedOut = encryptedDataGenerator.open(out, new byte[PGPUtility.BUFFER_SIZE]);
//
//        }
//
//        // COMPRESS
//        PGPCompressedDataGenerator compressedDataGenerator =
//                new PGPCompressedDataGenerator(compress ? PGPCompressedData.ZIP : PGPCompressedData.UNCOMPRESSED);
//        OutputStream compressedOut = compressedDataGenerator.open(encryptedOut, new byte [PGPUtility.BUFFER_SIZE]);
//
//        // SIGN
//        PGPSignatureGenerator signatureGenerator = null;
//
//        if (sign) {
//
//            if (!secretKey.isSigningKey()) {
//                // error
//                return;
//            }
//
//            PGPPrivateKey privateKey = findPrivateKey(secretKey, password);
//            PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(),
//                    HashAlgorithmTags.SHA1);
//            signatureGenerator = new PGPSignatureGenerator(signerBuilder);
//            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
//
//            Iterator<String> it = secretKey.getPublicKey().getUserIDs();
//            if (it.hasNext()) {
//                PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
//                //noinspection deprecation
//                spGen.setSignerUserID(false, it.next());
//                signatureGenerator.setHashedSubpackets(spGen.generate());
//            }
//            signatureGenerator.generateOnePassVersion(false).encode(compressedOut);
//        }
//
//        // Initialize literal data generator
//        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
//        OutputStream literalOut = literalDataGenerator.open(
//                compressedOut,
//                PGPLiteralData.BINARY,
//                fileName,
//                new Date(),
//                new byte [PGPUtility.BUFFER_SIZE] );
//
//        // Main loop - read the "in" stream, compress, encrypt and write to the "out" stream
//        FileInputStream in = new FileInputStream(fileName);
//        byte[] buf = new byte[PGPUtility.BUFFER_SIZE];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            literalOut.write(buf, 0, len);
//            if (sign)
//                signatureGenerator.update(buf, 0, len);
//        }
//
//        in.close();
//        literalDataGenerator.close();
//        // Generate the signature, compress, encrypt and write to the "out" stream
//        if (sign)
//            signatureGenerator.generate().encode(compressedOut);
//        if (compress)
//            compressedDataGenerator.close();
//        if (encrypt)
//            encryptedDataGenerator.close();
//        out.close();
//    }
//
//    private static PGPPrivateKey findPrivateKey(PGPSecretKey pgpSecKey, char[] pass)
//            throws PGPException
//    {
//        if (pgpSecKey == null) return null;
//
//        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(
//                new BcPGPDigestCalculatorProvider()).build(pass);
//        return pgpSecKey.extractPrivateKey(decryptor);
//    }
//
//    /**
//     * Runs decryption and verification of the given file, saves result to decrypted.txt
//     * @param filename file path to decrypt/verify
//     * @param passphrase used for accessing the secret key for decryption
//     * @return optional message for the UI regarding this operation
//     * @throws IOException if IO operation has failed
//     * @throws PGPException if PGP operation has failed
//     * @throws SignatureException if unsuccessful signature check
//     */
//    public static Optional<String> decryptAndVerify(
//            String filename,
//            char[] passphrase)
//            throws IOException, PGPException, SignatureException {
//        PGPSecretKeyRingCollection secretKeyCollection = new PGPSecretKeyRingCollection(Menu.privateKeyRingTableModel.getPrivateKeys());
//        PGPPublicKeyRingCollection publicKeyCollection = new PGPPublicKeyRingCollection(Menu.publicKeyRingTableModel.getPublicKeys());
//
//        Path outPath = Paths.get(filename);
//        OutputStream fOut = new FileOutputStream(outPath.getParent() + "/decrypted.txt");
//
//        PGPObjectFactory pgpF = new PGPObjectFactory(
//                PGPUtil.getDecoderStream(new FileInputStream(filename)),
//                new BcKeyFingerprintCalculator());
//
//        PGPEncryptedDataList enc;
//
//        Object o = pgpF.nextObject();
//        PGPObjectFactory plainFact = pgpF;
//
//        if (o instanceof PGPEncryptedDataList) {
//            enc = (PGPEncryptedDataList) o;
//
//            Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
//            PGPPrivateKey sKey = null;
//            PGPEncryptedData pbe = null;
//
//            while (sKey == null && it.hasNext()) {
//                pbe = it.next();
//
//                try {
//                    sKey = findPrivateKey(secretKeyCollection.getSecretKey(((PGPPublicKeyEncryptedData) pbe).getKeyID()), passphrase);
//                } catch (Exception ignored) {
//
//                }
//            }
//
//            if (sKey == null) {
//                return Optional.empty();
//            }
//
//            InputStream clear = ((PGPPublicKeyEncryptedData) pbe).getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
//            plainFact = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());
//        }
//
//        PGPOnePassSignatureList onePassSignatureList = null;
//        PGPSignatureList signatureList = null;
//        PGPCompressedData compressedData;
//
//        Object msgDecryption = plainFact.nextObject();
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//
//        while (msgDecryption != null) {
//
//            if (msgDecryption instanceof PGPCompressedData) {
//                compressedData = (PGPCompressedData) msgDecryption;
//                plainFact = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
//                msgDecryption = plainFact.nextObject();
//            }
//
//            if (msgDecryption instanceof PGPLiteralData) {
//
//                Streams.pipeAll(((PGPLiteralData) msgDecryption).getInputStream(), outStream);
//            }
//            if (msgDecryption instanceof PGPSignatureList) {
//                signatureList = (PGPSignatureList) msgDecryption;
//            }
//            if (msgDecryption instanceof PGPOnePassSignatureList) {
//                onePassSignatureList = (PGPOnePassSignatureList) msgDecryption;
//            }
//
//            msgDecryption = plainFact.nextObject();
//        }
//
//        outStream.close();
//        fOut.write(outStream.toByteArray());
//        fOut.close();
//
//        PGPPublicKey publicKey;
//        byte[] output = outStream.toByteArray();
//
//        if (onePassSignatureList == null || signatureList == null) {
//            return Optional.of("FILE IS NOT SIGNED");
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//
//        for (int i = 0; i < onePassSignatureList.size(); i++) {
//            PGPOnePassSignature ops = onePassSignatureList.get(0);
//            publicKey = publicKeyCollection.getPublicKey(ops.getKeyID());
//
//            if (publicKey == null) {
//                return Optional.of("FILE IS SIGNED, BUT NOT VERIFIED");
//            }
//
//            ops.init(new BcPGPContentVerifierBuilderProvider(), publicKey);
//            ops.update(output);
//            PGPSignature signature = signatureList.get(i);
//
//            if (!ops.verify(signature)) {
//                throw new SignatureException("Unsuccessful signature check");
//            }
//
//            Iterator<?> userIds = publicKey.getUserIDs();
//            while (userIds.hasNext()) {
//                String userId = (String) userIds.next();
//                stringBuilder.append("Signed by: ").append(userId).append(System.lineSeparator());
//            }
//        }
//
//        return Optional.of(stringBuilder.toString());
//    }
//}