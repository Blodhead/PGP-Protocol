package Messaging;

import org.bouncycastle.openpgp.*;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class User {

    private String username;

    static private ArrayList<PublicKeyElem> publicKeyList = new ArrayList<PublicKeyElem>();
    private ArrayList<PrivateKeyElem> privateKeyList;

    private static HashMap<String, User> userMap = new HashMap<String, User>();

    private User(String username) {
        this.username = username;
        this.privateKeyList = new ArrayList<PrivateKeyElem>();
    }

    public static User getUser(String username) {
        if (!userMap.containsKey(username))
            userMap.put(username, new User(username));

        return userMap.get(username);
    }

    public void addPublicKey(String algo, PGPPublicKey pk) {
        publicKeyList.add(new PublicKeyElem(algo, username, pk));
    }

    public void addPrivateKey(String algo, PGPSecretKey pkr) {
        privateKeyList.add(new PrivateKeyElem(algo, username, pkr));
    }

    public ArrayList<PrivateKeyElem> getUserPrivateKeys() {
        return privateKeyList;
    }

    public static ArrayList<PublicKeyElem> getPublicKeys() {
        return publicKeyList;
    }

    public static PublicKeyElem getPublicKey(String username, long ID) {
        for (PublicKeyElem elem: publicKeyList)
            if (elem.getUsername().equals(username) && elem.getKeyId() == ID)
                return elem;

        return null;
    }

    public static PrivateKeyElem getSecretKey(String username, long ID) {
        for (PrivateKeyElem elem: userMap.get(username).getUserPrivateKeys())
            if (elem.getKeyId() == ID)
                return elem;

        return null;
    }


}
