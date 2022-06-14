package etf.openpgp.cm170333dbk190730d.Messaging;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

import java.io.*;
import java.security.Security;
import java.security.SignatureException;
import java.util.Iterator;

public class Decryption {

    public static void decryptAndVerify(
            File filename,
            char[] passphrase)
            throws IOException, PGPException, SignatureException {

        int index = filename.getName().indexOf( ".pgp");

        InputStream in = new FileInputStream(filename);
        OutputStream out = new FileOutputStream(""+filename.getName().substring(0,index));
        InputStream keyIn = null;

        Security.addProvider(new BouncyCastleProvider());

        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpF = new PGPObjectFactory(in, null);
        PGPEncryptedDataList encryptedDataList;

        Object o = pgpF.nextObject();
        //GetHeader
        if (o instanceof  PGPEncryptedDataList) {
            encryptedDataList = (PGPEncryptedDataList) o;
        } else {
            encryptedDataList = (PGPEncryptedDataList) pgpF.nextObject();
        }

        //
        // find the secret key
        //
        Iterator<PGPEncryptedData> it = encryptedDataList.getEncryptedDataObjects();
        PGPPrivateKey sKey = null;
        PGPPublicKeyEncryptedData pbe = null;

        while (sKey == null && it.hasNext()) {
            Object temp = it.next();
            pbe = (PGPPublicKeyEncryptedData)temp;
            //sKey = findSecretKey(keyIn, pbe.getKeyID(), passphrase);
            sKey = Encryption.findPrivateKey(User.getSecretKey("#"+ String.valueOf(pbe.getKeyID())),passphrase);
        }

        /*if (sKey == null) {
            throw new IllegalArgumentException("Secret key for message not found.");
        }*/

        PublicKeyDataDecryptorFactory b = new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").setContentProvider("BC").build(sKey);

        InputStream clear = pbe.getDataStream(b);

        PGPObjectFactory plainFact = new PGPObjectFactory(clear, null);

        Object message = plainFact.nextObject();

        PGPObjectFactory pgpFact;

        if (message instanceof  PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) message;
            //unizip
            pgpFact = new PGPObjectFactory(cData.getDataStream(), null);
            message = pgpFact.nextObject();
        }
        PGPOnePassSignatureList onePassSignatureList;
        if (message instanceof  PGPOnePassSignatureList){

            onePassSignatureList = (PGPOnePassSignatureList)message;

            if(onePassSignatureList == null) return; //error

            for (int i = 0; i < onePassSignatureList.size(); i++){
                PGPOnePassSignature ops = onePassSignatureList.get(0);
                if(String.valueOf(ops.getKeyID()) == String.valueOf(pbe.getKeyID()));
            }
            message = plainFact.nextObject();
        }

        if (message instanceof  PGPLiteralData) {
            PGPLiteralData ld = (PGPLiteralData) message;
            InputStream unc = ld.getInputStream();
            int ch;
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
        }

        if (pbe.isIntegrityProtected()) {
            if (!pbe.verify()) {
                throw new PGPException("Message failed integrity check");
            }
        }


        }
    }
