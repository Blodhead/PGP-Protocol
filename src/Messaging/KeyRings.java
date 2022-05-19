package Messaging;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAKeyPairGenerator;
import java.security.interfaces.DSAParams;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;


public class KeyRings {

    private static PGPSecretKeyRing privateKeyRing;
    private static PGPPublicKeyRing publicKeyRing;

    KeyRings(){
        privateKeyRing = new PGPSecretKeyRing(Collections.EMPTY_LIST);
        publicKeyRing  = new PGPPublicKeyRing(Collections.EMPTY_LIST);
    }

    private static void generateNewKeyPair(int size){

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "BC");

            kpg.initialize(size);
            KeyPair kp = kpg.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");

            keyPairGenerator.initialize(size, secureRandom);

            KeyPair keyPair =  keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            PGPPublicKey bPublicKey = new JcaPGPKeyConverter().getPGPPublicKey(PGPPublicKey.ELGAMAL_ENCRYPT, publicKey, new Date());
            PGPPrivateKey bPprivateKey = new JcaPGPKeyConverter().getPGPPrivateKey(bPublicKey, privateKey);

            PGPPublicKeyRing.insertPublicKey(publicKeyRing, bPublicKey);
//            PGPSecretKeyRing.insertSecretKey(privateKeyRing, );


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (PGPException e) {
            e.printStackTrace();
        }


        //private LocalDate date = LocalDate.now();   //yyyy-mm-dd date of generated keys
        //private KeyStore.PrivateKeyEntry privateKey;         //encrypted private key by IDEA and hashed password
        //private PGPPublicKey publicKey;             //public key
        //private String userId;                      //mail
        //private KeyStore.ProtectionParameter password;
        //ovde ubaciti kod za generisanje kljuceva
    }


    public static void generateNewUserKeyPair(String userId, int size) {

    }


    public void storeToPrivateKeyRing(InputStream _stream){
        PGPSecretKeyRing newKey;

        BufferedInputStream stream_buff = new BufferedInputStream(_stream);

        stream_buff.mark(1024 * 128);//ne znam zasto

//        PGPSecretKeyRingCollection.addSecretKeyRing(privateKeyRing, new PGPSecretKeyRing((List<PGPSecretKey>) stream_buff));
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
