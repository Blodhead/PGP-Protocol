//package Messaging;
//
//import org.bouncycastle.openpgp.*;
//import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
//import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
//import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
//import org.bouncycastle.util.io.Streams;
//
//import java.io.*;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.SignatureException;
//import java.util.Iterator;
//import java.util.Optional;
//
//public class Decryption {
//
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
//        PGPObjectFactory pgpFactory = new PGPObjectFactory(
//                PGPUtil.getDecoderStream(new FileInputStream(filename)),
//                new BcKeyFingerprintCalculator());
//
//        PGPEncryptedDataList enc;
//
//        Object o = pgpFactory.nextObject();
////        PGPObjectFactory plainFact = pgpFactory;
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
//                    sKey = Encryption.findPrivateKey(secretKeyCollection.getSecretKey(((PGPPublicKeyEncryptedData) pbe).getKeyID()), passphrase);
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
//            pgpFactory = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());
//        }
//
//        PGPOnePassSignatureList onePassSignatureList = null;
//        PGPSignatureList signatureList = null;
//        PGPCompressedData compressedData;
//
//        Object msgDecryption = pgpFactory.nextObject();
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//
//        while (msgDecryption != null) {
//
//            if (msgDecryption instanceof PGPCompressedData) {
//                compressedData = (PGPCompressedData) msgDecryption;
//                pgpFactory = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
//                msgDecryption = pgpFactory.nextObject();
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
//            msgDecryption = pgpFactory.nextObject();
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
//
//
//
//
//
//
//
//}
