package Messaging;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import javax.crypto.spec.DHParameterSpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.util.Date;

public class Admin {
    private static Provider provider;
    private static Admin admin;

    public static _DSAKeyPairGenerator randomKeyGen = new _DSAKeyPairGenerator();
    public static KeyPairGenerator keyPairGen = null;

    private static KeyPairGenerator DSAKeyPairGenerator = null;
    private static KeyPairGenerator ElGamalKeyPairGenerator = null;


    private static void exportKeyPair(
            OutputStream    secretOut,
            OutputStream    publicOut,
            KeyPair         dsaKp,
            KeyPair         elgKp,
            String          identity,
            char[]          passPhrase,
            boolean         armor)
            throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPKeyPair dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
        PGPKeyPair        elgKeyPair = new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyRingGenerator    keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("BC").build(passPhrase));

        keyRingGen.addSubKey(elgKeyPair);

        keyRingGen.generateSecretKeyRing().encode(secretOut);

        secretOut.close();

        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        keyRingGen.generatePublicKeyRing().encode(publicOut);

        publicOut.close();
    }


    private Admin(){

        Signature sr = null;
        try {
//            sr = Signature.getInstance("SHA1withDSA");
//
//            Admin.provider = sr.getProvider();
//            Security.addProvider(Admin.provider);

            Security.addProvider(new BouncyCastleProvider());

            DSAKeyPairGenerator = KeyPairGenerator.getInstance("DSA", "BC");
            DSAKeyPairGenerator.initialize(1024);
            KeyPair DSAkp = DSAKeyPairGenerator.generateKeyPair();

            ElGamalKeyPairGenerator = KeyPairGenerator.getInstance("ELGAMAL", "BC");

            BigInteger bi1 = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
            BigInteger bi2 = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
            DHParameterSpec dhParam = new DHParameterSpec(bi1, bi2);

            ElGamalKeyPairGenerator.initialize(dhParam);
            KeyPair ElGamalKP = ElGamalKeyPairGenerator.generateKeyPair();

            String[] args = new String[3];
            args[0] = "-a";
            args[1] = "-a"; //  identity
            args[2] = "-a"; // passPhrase

            if (args[0].equals("-a"))
            {
                if (args.length < 3)
                {
                    System.out.println("DSAElGamalKeyRingGenerator [-a] identity passPhrase");
                    System.exit(0);
                }

                FileOutputStream    out1 = new FileOutputStream("secret.asc");
                FileOutputStream    out2 = new FileOutputStream("pub.asc");

                exportKeyPair(out1, out2, DSAkp, ElGamalKP, args[1], args[2].toCharArray(), true);
            }
            else
            {
                FileOutputStream    out1 = new FileOutputStream("secret.bpg");
                FileOutputStream    out2 = new FileOutputStream("pub.bpg");

                exportKeyPair(out1, out2, DSAkp, ElGamalKP, args[0], args[1].toCharArray(), false);
            }




//            FileOutputStream fos1 = new FileOutputStream("secret.asc");
//            FileOutputStream fos2 = new FileOutputStream("pub.asc");

//            OutputStream aos = new ArmoredOutputStream(fos1);
//
//            JcaPGPKeyPair jcaPGPKeyPairDSA = new JcaPGPKeyPair(17, DSAkp, new Date());
//            JcaPGPKeyPair jcaPGPKeyPairElGamal = new JcaPGPKeyPair(16, ElGamalKP, new Date());
//
//            PGPDigestCalculator dc = (new JcaPGPDigestCalculatorProviderBuilder()).build().get(2);
//
//            PGPKeyRingGenerator krg = new PGPKeyRingGenerator(
//                    19,
//                    jcaPGPKeyPairDSA,
//                    "nesto",
//                    dc,
//                    null,
//                    null,
//                    new JcaPGPContentSignerBuilder(jcaPGPKeyPairDSA.getPublicKey().getAlgorithm(), 2),
//                    (new JcePBESecretKeyEncryptorBuilder(9, dc)).setProvider("BC").build(new char['a']));
//
//            krg.addSubKey(jcaPGPKeyPairElGamal);
//            krg.generateSecretKeyRing().encode(aos);
//            aos.close();
//
//            ArmoredOutputStream aos2 = new ArmoredOutputStream(fos2);
//
//            krg.generatePublicKeyRing().encode(aos2);
//            aos2.close();


        } catch (Exception e) {
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
