package Messaging;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;


public class Ksenija {

    private static byte publicKey[] = new byte[128];
    private static final String DSA = "DSA";


    private static String hashMe(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] messageDigest = md.digest(msg.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(byte sessionKey[], byte publicKey[]) {


        try {

            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DSA);

            keyPairGenerator.initialize(1024, secureRandom);

            KeyPair keyPair =  keyPairGenerator.generateKeyPair();

            System.out.println("Public Key is: " + keyPair.getPublic().toString());
            System.out.println("Privte Key is: " + keyPair.getPrivate().toString());

            PrivateKey privateKey = keyPair.getPrivate();

            Cipher cipher = Cipher.getInstance(DSA);

            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            return cipher.doFinal(sessionKey);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }


        return null;
    }

    private static String encryption(String msg) {

        // 1. hesirati msg
        String hashMsg = hashMe(msg);

        // 2. random sesijski kljuc
        SecureRandom random = new SecureRandom();
        byte sessionKey[] = new byte[128];
        random.nextBytes(sessionKey);


        // 3. sifruj
        byte[] encKey = encrypt(sessionKey, publicKey);

        System.out.println(encKey.toString());


        // 4. sifruj poruku
        return null;

    }

    public static void main(String[] args) {
        String msg = "porukica";

        encryption(msg);

    }


}
