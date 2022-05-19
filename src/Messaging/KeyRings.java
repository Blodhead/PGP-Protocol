package Messaging;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;


public class KeyRings {

    private static PGPSecretKeyRingCollection privateKeyRing;
    private static PGPPublicKeyRingCollection publicKeyRing;

    KeyRings(){
        try {
            privateKeyRing = new PGPSecretKeyRingCollection(Collections.EMPTY_LIST);
            publicKeyRing  = new PGPPublicKeyRingCollection(Collections.EMPTY_LIST);
        }
        catch (IOException | PGPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void generateNewKeyPair(int _userId, long _keyId, String _password){
        //private LocalDate date = LocalDate.now();   //yyyy-mm-dd date of generated keys
        //private KeyStore.PrivateKeyEntry privateKey;         //encrypted private key by IDEA and hashed password
        //private PGPPublicKey publicKey;             //public key
        //private String userId;                      //mail
        //private KeyStore.ProtectionParameter password;
        //ovde ubaciti kod za generisanje kljuceva
    }

    public void storeToPrivateKeyRing(InputStream _stream){
        PGPSecretKeyRing newKey;

        BufferedInputStream stream_buff = new BufferedInputStream(_stream);

        stream_buff.mark(1024 * 128);//ne znam zasto

        PGPSecretKeyRingCollection.addSecretKeyRing(privateKeyRing, new PGPSecretKeyRing((List<PGPSecretKey>) stream_buff));
    }

    public void storeToPublicKeyRing(PGPPublicKey _publicKey, String _password){



    }

    public static PGPSecretKey getPrivateKeyByID(long _id) throws PGPException {
        return privateKeyRing.getSecretKey(_id);
    }
    public static PGPPublicKey getPublicKeyByID(long _id) throws PGPException {
        return publicKeyRing.getPublicKey(_id);
    }

}
