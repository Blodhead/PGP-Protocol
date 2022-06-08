package Messaging;

import App.View_User;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.*;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import javax.crypto.Cipher;
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

                byte[] input = "ab".getBytes();
                Cipher cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
                KeyPairGenerator generator = KeyPairGenerator.getInstance("ElGamal", "BC");
                SecureRandom random = new SecureRandom();

                generator.initialize(size, random);

                kp = generator.generateKeyPair();
                Key pubKey = kp.getPublic();
                Key privKey = kp.getPrivate();
//                cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);
//                byte[] cipherText = cipher.doFinal(input);
//                System.out.println("cipher: " + new String(cipherText));
//
//                cipher.init(Cipher.DECRYPT_MODE, privKey);
//                byte[] plainText = cipher.doFinal(cipherText);
//                System.out.println("plain : " + new String(plainText));


//                BigInteger bi1 = BigInteger.probablePrime(16, new Random());
//                BigInteger bi2 = BigInteger.probablePrime(16, new Random());
//
//                DHParameterSpec dhParam = new DHParameterSpec(bi1, bi2);
//
//                EGkpg.initialize(dhParam);
//                kp = EGkpg.generateKeyPair();

                return new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, kp, new Date());

            }


//            PublicKey pub = kp.getPublic();
            //PGPPublicKeyRing pubKR = publicKeyRing;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String generateNewUserKeyPair(String algo, String username, String password, String mail, int size) throws  org.bouncycastle.openpgp.PGPException{

        if(algo.equals("DSA")) {
            PGPKeyPair kp = generateNewKeyPair(algo, size, password);

            PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);

            if (generatorHashMap.get(username) == null ) { // dohvata userov generator

                PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                        PGPSignature.POSITIVE_CERTIFICATION,
                        kp,
                        username + " <" + mail + ">",
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


                User.addPublicKeyRing(username, pkr);
                User.addSecretKeyRing(username, skr);
//                user.addPrivateKey(algo, skr.getSecretKey());
//                user.addPublicKey(algo, pkr.getPublicKey());

            }
        }
        else if (algo.equals("ElGamal")) {

            try {



//                PGPSecretKeyRing privateKR = privateKeyRingCollection.getKeyRings(username).next();
                PGPKeyPair kp = null;
                if(size != 4096)
                kp = generateNewKeyPair(algo, size, password);

                if(size == 4096) {// ne radi

                    /*FIRST TRY
                    ElGamalKeyPairGenerator elGamalKeyPairGenerator = new ElGamalKeyPairGenerator();
                    elGamalKeyPairGenerator.init(new KeyGenerationParameters(new SecureRandom(), 4096));
                    AsymmetricCipherKeyPair elgKp = elGamalKeyPairGenerator.generateKeyPair();
                    kp = new BcPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
                    */

                    /*SECOND TRY
                                    BigInteger bi2 = BigInteger.probablePrime(16, new Random());

                    BigInteger maxLimit = new BigInteger("5000000000000");
                    BigInteger minLimit = new BigInteger("25000000000");

                    BigInteger bigInteger1 = maxLimit.subtract(minLimit);
                    Random randNum1 = new Random();
                    int len1 = maxLimit.bitLength();

                    BigInteger bigInteger2 = maxLimit.subtract(minLimit);
                    Random randNum2 = new Random();
                    int len2 = maxLimit.bitLength();

                    ElGamalKeyPairGenerator elGamalKeyPairGenerator = new ElGamalKeyPairGenerator();
                    elGamalKeyPairGenerator.init(new ElGamalKeyGenerationParameters(new SecureRandom(), new ElGamalParameters(bigInteger1,bigInteger2,4096)));
                    AsymmetricCipherKeyPair elgKp = elGamalKeyPairGenerator.generateKeyPair();
                    kp = new BcPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
                    */
                    return "";
                }

                PGPKeyRingGenerator krg = generatorHashMap.get(username);

//                PGPPublicKeyRing.insertPublicKey(publicKeyRing, kp.getPublicKey());
                krg.addSubKey(kp);

                PGPSecretKeyRing skr = krg.generateSecretKeyRing();
                PGPPublicKeyRing pkr = krg.generatePublicKeyRing();

                PGPPublicKey pk = null;
                PGPSecretKey sk = null;

                for (Iterator<PGPSecretKey> iterator = skr.getSecretKeys(); iterator.hasNext(); ) {
                    sk = iterator.next();
                }

                for (Iterator<PGPPublicKey> iterator = skr.getPublicKeys(); iterator.hasNext(); ) {
                    pk = iterator.next();
                }

                User.addPublicSubKey(username, pkr, pk);
                User.addSecretSubKey(username, skr, sk);

//                User user = User.getUser(username);
//                user.addPrivateKey(algo, krg.generateSecretKeyRing().getSecretKey());
//                user.addPublicKey(algo, krg.generatePublicKeyRing().getPublicKey());

            }
            catch (NoSuchElementException e) {
                return "NoSuchElementException";
            }


        }

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