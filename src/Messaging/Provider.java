package Messaging;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Provider {
    private static BouncyCastleProvider provider;
    public static _DSAKeyPairGenerator randomKeyGen = new _DSAKeyPairGenerator();
    private Provider(){
        this.provider = new BouncyCastleProvider();
    }

    public static BouncyCastleProvider getProvider() {
        if (provider == null) return (new Provider()).provider;
        return provider;
    }



}
