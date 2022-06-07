package Messaging;

import org.bouncycastle.openpgp.*;

import javax.crypto.SecretKey;
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


    public User(String username, String password) {
        this.username = username;
        this.password = password;

        userMap.put(username, this);

    }

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

    public static PGPSecretKeyRing getSecretKeyRing(String username) {
        return secretKeyRingHashMap.get(username);
    }

    public static PGPPublicKeyRing getPublicKeyRing(String username) {
        return publicKeyRingHashMap.get(username);
    }

    public static Vector<String> getSecretKeys(String username) {
        User user = userMap.get(username);

        Vector<String> returnVector = new Vector<>();
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
    }

    public static void addPublicSubKey(String username, PGPPublicKeyRing pkr, PGPPublicKey pk) {
        publicMap.put(getString(pk), pk);
//        publicKeyRings.remove(username);
//        publicKeyRings.addElement();

        publicKeyRingHashMap.replace(username, pkr);
    }

    // mora da se napravi i kad ima subkey-eve da radi
    public static void addSecretKeyRing(String username, PGPSecretKeyRing skr) {
        User user = userMap.get(username);
        user.secretKeyRing = skr;
        secretMap.put(username, skr.getSecretKey());
        secretKeyRingHashMap.put(username, skr);

    }

    public static void addSecretSubKey(String username, PGPSecretKeyRing skr, PGPSecretKey sk) {
        secretMap.put(getString(sk), sk);
        User user = userMap.get(username);
        user.secretKeyRing = skr;
        secretKeyRingHashMap.replace(username, skr);
    }

    public static void removePublicKey(String keyId) {
        // uklanjanje kljuca iz mape
        PGPPublicKey pk = publicMap.get(keyId);
        publicMap.remove(keyId);

        //
        PGPPublicKeyRing pkr = publicKeyRingHashMap.get(pk.getUserIDs().next());
        pkr = pkr.removePublicKey(pkr, pk);
        publicKeyRingHashMap.replace(pk.getUserIDs().next(), pkr);
    }

    public static void removePrivateKey(String keyId) {
        PGPSecretKey sk = secretMap.get(keyId);
        secretMap.remove(keyId);
        PGPSecretKeyRing skr = secretKeyRingHashMap.get(sk.getUserIDs().next());
        skr = skr.removeSecretKey(skr, sk);
        secretKeyRingHashMap.replace(sk.getUserIDs().next(), skr);
        userMap.get(sk.getUserIDs().next()).secretKeyRing = skr;
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
    

}
