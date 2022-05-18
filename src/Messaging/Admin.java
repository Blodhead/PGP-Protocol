package Messaging;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPairGenerator;

public class Admin {
    private static BouncyCastleProvider provider;
    private static Admin admin;

    public static _DSAKeyPairGenerator randomKeyGen = new _DSAKeyPairGenerator();
    public static KeyPairGenerator keyPairGen = null;
    private Admin(){
        this.provider = new BouncyCastleProvider();
    }

    public static Admin getAdmin(){
        if(admin == null){ admin = new Admin(); return admin;}
        else return admin;
    }
    public static BouncyCastleProvider getProvider() {
        return provider;
    }



}
