package Messaging;

import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;

import java.security.*;
import java.util.Date;

public class Encryption {


    public static void encrypt() {


        // biranje algoritma za enkripciju
        // 3DES i IDEA i ElGamal?

        int algo = PGPEncryptedData.TRIPLE_DES;

        PGPEncryptedDataGenerator edGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(algo).setSecureRandom(new SecureRandom()).setProvider("BC"));

        KeyPairGenerator kpg = null;
        try {

            kpg = KeyPairGenerator.getInstance("DSA", "BC");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();

            PGPKeyPair pair2 = new PGPKeyPair()
            PGPKeyPair pair = new PGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, kp, new Date(), "BC");




            PGPKeyPair rsaKeyPair = new PGPKeyPair(PGPPublicKey.RSA_SIGN, rsaKp, new Date(), "BC");

            PGPPublicKey myKey = new PGPPublicKey()

            edGen.addMethod(PGPPublicKey.ELGAMAL_ENCRYPT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        kpg.initialize(1024);










    }


}
