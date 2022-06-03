package Messaging;

import javafx.util.Pair;
import org.bouncycastle.openpgp.*;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class User {

    private String username;

    static private ArrayList<Pair<String, PGPPublicKey>> publicKeyList;
    private ArrayList<PGPSecretKey> privateKeyList;

    private static HashMap<String, User> userMap = new HashMap<String, User>();

    private User(String username) {
        this.username = username;
        this.privateKeyList = new ArrayList<PGPSecretKey>();
    }

    public static User getUser(String username) {
        if (!userMap.containsKey(username))
            userMap.put(username, new User(username));

        return userMap.get(username);
    }

    public void addPublicKey(PGPPublicKey pk) {
        publicKeyList.add(new Pair<String, PGPPublicKey>(username, pk));
    }

    public void addPrivateKey(PGPSecretKey pkr) {
        privateKeyList.add(pkr);
    }

    public ArrayList<PGPSecretKey> getUserPrivateKeys() {
        return privateKeyList;
    }

    public static ArrayList<Pair<String, PGPPublicKey>> getPublicKeys() {
        return publicKeyList;
    }

    public static PGPPublicKey getPublicKey(String username, long ID) {
        for (Pair<String, PGPPublicKey> pair: publicKeyList)
            if (pair.getKey().equals(username) && pair.getValue().getKeyID() == ID)
                return pair.getValue();

        return null;
    }

    public static PGPSecretKey getSecretKey(String username, long ID) {
        for (PGPSecretKey elem: userMap.get(username).getUserPrivateKeys())
            if (elem.getKeyID() == ID)
                return elem;

        return null;
    }


}
