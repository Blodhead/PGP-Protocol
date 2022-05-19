package Messaging;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

<<<<<<< Updated upstream
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

=======
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
>>>>>>> Stashed changes

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

        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("DSA", "BC");

            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");

            keyPairGenerator.initialize(1024, secureRandom);

            KeyPair keyPair =  keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            PGPKeyPair pair2 = new PGPKeyPair(new PGPKeyPair())

            PGPKeyPair pair = new PGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, kp, new Date(), "BC");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

//        try {
//        // byte sessionKey[], byte publicKey[]
//
//            SecureRandom secureRandom = new SecureRandom();
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
//
//            keyPairGenerator.initialize(1024, secureRandom);
//
//            KeyPair keyPair =  keyPairGenerator.generateKeyPair();
//
//            System.out.println("Public Key is: " + keyPair.getPublic().toString());
//            System.out.println("Privte Key is: " + keyPair.getPrivate().toString());
//
//            PrivateKey privateKey = keyPair.getPrivate();
//            PublicKey myPublicKey = keyPair.getPublic();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

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
