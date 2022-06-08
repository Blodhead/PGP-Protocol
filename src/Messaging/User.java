package Messaging;

import org.bouncycastle.openpgp.*;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLEngineResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class User {

    private String username;
    private String userID;
    private String password;

    private static HashMap<String, User> userMap = new HashMap<>();

    private PGPSecretKeyRing secretKeyRing;
    private static HashMap<String, PGPSecretKey> secretMap = new HashMap<>();
    private static HashMap<String, PGPSecretKeyRing> secretKeyRingHashMap = new HashMap<>();

//    private static Vector<PGPPublicKeyRing> publicKeyRings = new Vector<>();
    private static HashMap<String, PGPPublicKey> publicMap = new HashMap<>();
    private static HashMap<String, PGPPublicKeyRing> publicKeyRingHashMap = new HashMap<>();

    private static HashMap<String, String> mapKeyUser = new HashMap<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;

        userMap.put(username, this);

    }

    public String getUsername(){ return username;}
    public User(String username, String password, PGPPublicKeyRing pkr, PGPSecretKeyRing skr) {
        // ima samo DSA

        userMap.put(username, this);

        this.username = username;
        this.password = password;
        this.secretKeyRing = skr;
        secretMap.put(username, skr.getSecretKey()); // vraca master key tj. DSA
        secretKeyRingHashMap.put(username, skr);

//        publicKeyRings.add(pkr);
        publicMap.put(username, pkr.getPublicKey());
        publicKeyRingHashMap.put(username, pkr);
    }

    private static String getString(PGPSecretKey sk) { // ElGamal
        return "#" + sk.getKeyID();
    }

    private static String getString(PGPPublicKey pk) {
        return "#" + pk.getKeyID();
    }

    // moze da bubde null
    public static PGPSecretKeyRing getSecretKeyRing(String username) {
        return secretKeyRingHashMap.get(username);
    }

    // moze da bude null
    public static PGPPublicKeyRing getPublicKeyRing(String username) {
        return publicKeyRingHashMap.get(username);
    }

    public static Vector<String> getSecretKeys(String username) {
        User user = userMap.get(username);

        Vector<String> returnVector = new Vector<>();

        if (user == null || user.secretKeyRing == null)
            return returnVector;

        boolean isMaster = true;

        for (Iterator<PGPSecretKey> iterator = user.secretKeyRing.getSecretKeys(); iterator.hasNext(); ) {
            if (isMaster) {
                iterator.next();
                isMaster = false;
                returnVector.addElement(username);
            }else
            returnVector.addElement(getString((PGPSecretKey) iterator.next()));
        }

        return returnVector;

    }

    public static Vector<String> getPublicKeys() {
        // nije sortirano!

        Vector<String> returnVector = new Vector<>();

        for (User user: userMap.values()) {

            if (publicKeyRingHashMap.get(user.username) == null)
                continue;

            boolean isMaster = true;

            for (Iterator<PGPPublicKey> iterator = publicKeyRingHashMap.get(user.username).getPublicKeys(); iterator.hasNext(); ) {
                if (isMaster) {
                    iterator.next();
                    isMaster = false;
                    returnVector.addElement(user.username);
                }else
                returnVector.addElement(getString((PGPPublicKey) iterator.next()));
            }
        }

        return returnVector;
    }

    // mora da se napravi i kad ima subkey-eve da radi
    public static void addPublicKeyRing(String username, PGPPublicKeyRing pkr) {
        User user = userMap.get(username);
//        publicKeyRings.addElement(pkr);
        publicMap.put(username, pkr.getPublicKey());
        publicKeyRingHashMap.put(username, pkr);
        mapKeyUser.put(username, username);
    }

    public static void addPublicSubKey(String username, PGPPublicKeyRing pkr, PGPPublicKey pk) {
        publicMap.put(getString(pk), pk);
//        publicKeyRings.remove(username);
//        publicKeyRings.addElement();

        publicKeyRingHashMap.replace(username, pkr);
        mapKeyUser.put(getString(pk), username);
    }

    // mora da se napravi i kad ima subkey-eve da radi
    public static void addSecretKeyRing(String username, PGPSecretKeyRing skr) {
        User user = userMap.get(username);
        user.secretKeyRing = skr;
        secretMap.put(username, skr.getSecretKey());
        secretKeyRingHashMap.put(username, skr);

        mapKeyUser.put(username, username);

    }

    public static void addSecretSubKey(String username, PGPSecretKeyRing skr, PGPSecretKey sk) {
        secretMap.put(getString(sk), sk);
        User user = userMap.get(username);
        user.secretKeyRing = skr;
        secretKeyRingHashMap.replace(username, skr);

        mapKeyUser.put(getString(sk), username);

    }

    public static void removePublicKey(String keyId) {

        if (keyId.charAt(0) == '#')
            removePublicSubKey(keyId);
        else
            removePublicKeyRing(keyId);


    }

    private static void removePublicKeyRing(String username) {

        PGPPublicKeyRing pkr = publicKeyRingHashMap.get(username);
        publicKeyRingHashMap.remove(username);

        boolean master = true;

        for (Iterator<PGPPublicKey> pk = pkr.getPublicKeys(); pk.hasNext();) {
            if (master) {
                publicMap.remove(username);
                pk.next();
            }
            else
                publicMap.remove(getString(pk.next()));
        }
    }

    private static void removePublicSubKey(String keyId) {
        String username = mapKeyUser.get(keyId);
        PGPPublicKey pk = publicMap.get(keyId);
        publicMap.remove(keyId);

        PGPPublicKeyRing pkr = publicKeyRingHashMap.get(username);
        pkr = pkr.removePublicKey(pkr, pk);
        publicKeyRingHashMap.replace(username, pkr);

    }


    public static void removePrivateKey(String keyId) {

        if (keyId.charAt(0) == '#')
            removePrivateSubKey(keyId);
        else
            removePrivateKeyRing(keyId);

    }

    private static void removePrivateKeyRing(String username) {

        PGPSecretKeyRing skr = secretKeyRingHashMap.get(username);
        secretKeyRingHashMap.remove(username);

        boolean master = true;

        for (Iterator<PGPSecretKey> sk = skr.getSecretKeys(); sk.hasNext();) {
            if (master) {
                secretMap.remove(username);
                sk.next();
            }
            else
                secretMap.remove(getString(sk.next()));
        }
        getUser(username).secretKeyRing = null;
    }

    private static void removePrivateSubKey(String keyId) {
        String username = mapKeyUser.get(keyId);
        PGPSecretKey sk = secretMap.get(keyId);
        secretMap.remove(keyId);

        PGPSecretKeyRing skr = secretKeyRingHashMap.get(username);
        skr = skr.removeSecretKey(skr, sk);
        secretKeyRingHashMap.replace(username, skr);
        userMap.get(username).secretKeyRing = skr;

    }




    public static Vector<String> getAllUsers() {
        Vector<String> vector = new Vector<>();
        vector.addAll(userMap.keySet());
        return vector;
    }

    public static User getUser(String username){
        return userMap.get(username);
    }


    public static PGPPublicKey getPublicKey(String keyId) {
        return publicMap.get(keyId);
    }

    public static PGPSecretKey getSecretKey(String keyId) {
        return secretMap.get(keyId);
    }

    public static PGPPublicKey getPublicKeyDSA(long keyId) {
        for (PGPPublicKey pk: publicMap.values()) {
            if (pk.getKeyID() == keyId)
                return pk;
        }
        return null;
    }
    

}
