package Messaging;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPPrivateKey;


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
import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.security.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;


public class KeyRings {

    private static PGPSecretKeyRingCollection privateKeyRing;
    private static PGPPublicKeyRingCollection publicKeyRing;

    private static KeyPairGenerator DSAkpg = null;
    private static KeyPairGenerator EGkpg = null;

    KeyRings(){
        try {
            privateKeyRing = new PGPSecretKeyRingCollection(Collections.EMPTY_LIST);
            publicKeyRing  = new PGPPublicKeyRingCollection(Collections.EMPTY_LIST);

            DSAkpg = KeyPairGenerator.getInstance("DSA", "BC");
            DSAkpg.initialize(1024);

            EGkpg = KeyPairGenerator.getInstance("ELGAMAL", "BC");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (PGPException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public static void generateNewKeyPair(String algo, int size, String password){
        try {

            KeyPair kp;

            if (algo.equals("DSA")) {
                kp = DSAkpg.generateKeyPair();

                PGPKeyPair DSAkp = new JcaPGPKeyPair(PGPPublicKey.DSA, kp, new Date());
                

            }
            else if (algo.equals("ElGamal")) {
                // podesavanje parametara za ElGamal
                // videti kako se biraju parametri

                BigInteger bi1 = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
                BigInteger bi2 = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
                DHParameterSpec dhParam = new DHParameterSpec(bi1, bi2);

                EGkpg.initialize(dhParam);
                kp = EGkpg.generateKeyPair();
            }
            else {
                return;
            }

            PublicKey pub = kp.getPublic();
            PGPPublicKeyRing pubKR = publicKeyRing

        } catch (InvalidAlgorithmParameterException | PGPException e) {
            e.printStackTrace();
        }



/*
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", Admin.getProvider());

            kpg.initialize(size);
            KeyPair kp = kpg.generateKeyPair();

            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");

            keyPairGenerator.initialize(size, secureRandom);

            KeyPair keyPair =  keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            PGPPublicKey bPublicKey = new JcaPGPKeyConverter().getPGPPublicKey(PGPPublicKey.ELGAMAL_ENCRYPT, publicKey, new Date());

//            JcaPGPPrivateKey jpk = new JcaPGPPrivateKey(bPublicKey, privateKey);

            PGPPrivateKey bpk = new JcaPGPKeyConverter().getPGPPrivateKey(bPublicKey, privateKey);

//            PGPPrivateKey bPrivateKey = (new JcaPGPPrivateKey(bPublicKey, privateKey)).getPrivateKey();
//
//            PGPPrivateKey bPprivateKey = new JcaPGPKeyConverter().getPGPPrivateKey(bPublicKey, privateKey);
//
//            PGPPublicKeyRing.insertPublicKey(publicKeyRing, bPublicKey);




//            PGPSecretKeyRing.insertSecretKey(privateKeyRing, );


        } catch (NoSuchAlgorithmException e) {
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

 */
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
