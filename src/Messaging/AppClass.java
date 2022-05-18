package Messaging;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static Messaging.Provider.*;

public class AppClass {

    BouncyCastleProvider provider = getProvider();

    public static void main(String[] args) {
        String msg = "";
        System.out.println("Hello world");
    }
}
