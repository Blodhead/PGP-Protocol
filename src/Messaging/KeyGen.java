package Messaging;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

public class KeyGen {
    private String id = "damico";
    private String passwd = "******";
    private boolean integrityCheck = true;

    private String pubKeyFile = "/tmp/pub.dat";
    private String privKeyFile = "/tmp/secret.dat";

    private String plainTextFile = "/tmp/plain-text.txt"; //fajl koji se unosi za enkripciju
    private String cipherTextFile = "/tmp/cypher-text.dat";
    private String decPlainTextFile = "/tmp/dec-plain-text.txt";
    private String signatureFile = "/tmp/signature.txt";

    public void genKeyPair() throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException, PGPException, NoSuchAlgorithmException {

        Security.addProvider(new BouncyCastleProvider());// poziv ce glasiti app.getProvider();

        KeyPairGenerator    kpg = KeyPairGenerator.getInstance("DSA", "BC");

        kpg.initialize(1024);

        KeyPair                    kp = kpg.generateKeyPair();

        FileOutputStream    out1 = new FileOutputStream(privKeyFile);
        FileOutputStream    out2 = new FileOutputStream(pubKeyFile);

        //randomKeyGen.exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), id, passwd.toCharArray());//poziv glasi app.getProvider().getRandomKeyGen();


    }

    /*public void encrypt() throws NoSuchProviderException, IOException, PGPException{
        FileInputStream pubKeyIs = new FileInputStream(pubKeyFile);
        FileOutputStream cipheredFileIs = new FileOutputStream(cipherTextFile);
        //PgpHelper.getInstance().encryptFile(cipheredFileIs, plainTextFile, PgpHelper.getInstance().readPublicKey(pubKeyIs), isArmored, integrityCheck);
        cipheredFileIs.close();
        pubKeyIs.close();
    }*/

    /*public void decrypt() throws Exception{

        FileInputStream cipheredFileIs = new FileInputStream(cipherTextFile);
        FileInputStream privKeyIn = new FileInputStream(privKeyFile);
        FileOutputStream plainTextFileIs = new FileOutputStream(decPlainTextFile);
        //PgpHelper.getInstance().decryptFile(cipheredFileIs, plainTextFileIs, privKeyIn, passwd.toCharArray());
        cipheredFileIs.close();
        plainTextFileIs.close();
        privKeyIn.close();
    }*/


   /* public void signAndVerify() throws Exception{
        FileInputStream privKeyIn = new FileInputStream(privKeyFile);
        FileInputStream pubKeyIs = new FileInputStream(pubKeyFile);
        FileInputStream plainTextInput = new FileInputStream(plainTextFile);
        FileOutputStream signatureOut = new FileOutputStream(signatureFile);

        //byte[] bIn = PgpHelper.getInstance().inputStreamToByteArray(plainTextInput);
        //byte[] sig = PgpHelper.getInstance().createSignature(plainTextFile, privKeyIn, signatureOut, passwd.toCharArray(), true);
        //PgpHelper.getInstance().verifySignature(plainTextFile, sig, pubKeyIs);
    }*/

}
