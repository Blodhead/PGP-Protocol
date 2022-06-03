package Messaging;

import App.View_User;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.*;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

import javax.crypto.spec.DHParameterSpec;
import javax.swing.*;
import java.security.*;
import java.util.Collections;


public class KeyRings {

    // KR Collection je zapravo obican KeyRing (kako mi koristimo)
    private static PGPSecretKeyRingCollection privateKeyRingCollection;
    private static PGPPublicKeyRingCollection publicKeyRingCollection;

    private static KeyPairGenerator DSAkpg = null;
    private static KeyPairGenerator EGkpg = null;

    private static HashMap<String, PGPKeyRingGenerator> generatorHashMap = new HashMap<>();

    private static PGPPublicKeyRing publicKeyRing = null;

    KeyRings(){
        try {
            Security.addProvider(new BouncyCastleProvider());

            privateKeyRingCollection = new PGPSecretKeyRingCollection(Collections.EMPTY_LIST);
            publicKeyRingCollection  = new PGPPublicKeyRingCollection(Collections.EMPTY_LIST);

            DSAkpg = KeyPairGenerator.getInstance("DSA", "BC");
            DSAkpg.initialize(1024);

            EGkpg = KeyPairGenerator.getInstance("ELGAMAL", "BC");

            publicKeyRing = new PGPPublicKeyRing(Collections.EMPTY_LIST);



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

    private static PGPKeyPair generateNewKeyPair(String algo, int size, String password){
        try {

            KeyPair kp;

            if (algo.equals("DSA")) {
                kp = DSAkpg.generateKeyPair();

                return  new JcaPGPKeyPair(PGPPublicKey.DSA, kp, new Date());
//                PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);

            }
            else if (algo.equals("ElGamal")) {
                // podesavanje parametara za ElGamal
                // videti kako se biraju parametri

                BigInteger bi1 = BigInteger.probablePrime(16, new Random());
                BigInteger bi2 = BigInteger.probablePrime(16, new Random());

                DHParameterSpec dhParam = new DHParameterSpec(bi1, bi2);

                EGkpg.initialize(dhParam);
                kp = EGkpg.generateKeyPair();

                return new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, kp, new Date());

            }
            else {
                return null;
            }

//            PublicKey pub = kp.getPublic();
            //PGPPublicKeyRing pubKR = publicKeyRing;

        } catch (InvalidAlgorithmParameterException | PGPException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String generateNewUserKeyPair(String algo, String username, String password, String mail, int size) throws  org.bouncycastle.openpgp.PGPException{

        if(algo.equals("DSA")) {
            PGPKeyPair kp = generateNewKeyPair(algo, size, password);

            PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);

            if (!privateKeyRingCollection.getKeyRings(username).hasNext() ) { // dohvata userov generator

                PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                        PGPSignature.POSITIVE_CERTIFICATION,
                        kp,
                        username,
                        sha1Calc,
                        null,
                        null,
                        new JcaPGPContentSignerBuilder(kp.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                        new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("BC").build(password.toCharArray()));

                generatorHashMap.put(username, keyRingGen);

                PGPSecretKeyRing skr = keyRingGen.generateSecretKeyRing();
//                skr = PGP.insertSecretKey(skr, keyRingGen.)
                privateKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing(privateKeyRingCollection,skr);

                // jedinstveni public Key Ring za sve usere
                PGPPublicKeyRing pkr = keyRingGen.generatePublicKeyRing();
//                pkr = PGPPublicKeyRing.insertPublicKey(pkr, kp.getPublicKey());
                publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyRingCollection, pkr);//DRUGI PUT KAD SE GENERISE KLJUC ISKACE ERROR


                User user = User.getUser(username);
                user.addPrivateKey(algo, skr.getSecretKey());
                user.addPublicKey(algo, pkr.getPublicKey());

            }
        }
//        else if (algo.equals("ElGamal")) {
//
//            try {
//
//
//
//
//                PGPSecretKeyRing privateKR = privateKeyRingCollection.getKeyRings(username).next();
//
//                PGPKeyPair kp = generateNewKeyPair(algo, size, password);
//
//                PGPKeyRingGenerator krg = generatorHashMap.get(username);
//
//                PGPPublicKeyRing.insertPublicKey(publicKeyRing, kp.getPublicKey());
//                krg.addSubKey(kp);
//
//            }
//            catch (NoSuchElementException e) {
//                return "NoSuchElementException";
//            }
//
//
//        }

        return null;

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
        return privateKeyRingCollection.getSecretKey(_id);
    }
    public static PGPPublicKey getPublicKeyByID(long _id) throws PGPException {
        return publicKeyRingCollection.getPublicKey(_id);
    }

}
