package Messaging;

import org.bouncycastle.openpgp.*;

import java.io.IOException;
import java.security.KeyStore;
import java.time.LocalDate;
import java.util.Collections;

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

    public void storePrivateKey(PGPSecretKey _privateKey, String _password){



    }

    public static PGPSecretKey getPrivateKeyByID(long _id) throws PGPException {
        return privateKeyRing.getSecretKey(_id);
    }
    public static PGPPublicKey getPublicKeyByID(long _id) throws PGPException {
        return publicKeyRing.getPublicKey(_id);
    }

}
