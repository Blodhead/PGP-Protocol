package Messaging;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.cert.CertificateFactory;

public class Admin {
    private static Provider provider;
    private static Admin admin;

    public static _DSAKeyPairGenerator randomKeyGen = new _DSAKeyPairGenerator();
    public static KeyPairGenerator keyPairGen = null;
    private Admin(){

        Signature sr = null;
        try {
            sr = Signature.getInstance("SHA1withDSA");

            Admin.provider = sr.getProvider();
            Security.addProvider(Admin.provider);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    public static Admin getAdmin(){
        if(admin == null){ admin = new Admin(); return admin;}
        else return admin;
    }
    public static Provider getProvider() {

        return Admin.provider;
    }



}
