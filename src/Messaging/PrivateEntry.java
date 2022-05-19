package Messaging;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;

import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.Provider;
import java.time.LocalDate;

public class PrivateEntry extends KeyStore {

    private LocalDate date = LocalDate.now();   //yyyy-mm-dd date of generated keys
    private PrivateKeyEntry privateKey;         //encrypted private key by IDEA and hashed password
    private PGPPublicKey publicKey;             //public key
    private String userId;                      //mail
    private ProtectionParameter password;
    /**
     * Creates a KeyStore object of the given type, and encapsulates the given
     * provider implementation (SPI object) in it.
     *
     * @param keyStoreSpi the provider implementation.
     * @param provider    the provider.
     * @param type        the keystore type.
     */
    protected PrivateEntry(LocalDate _date, KeyStoreSpi keyStoreSpi, Provider provider, String type, PGPPrivateKey privateKey) {
        super(keyStoreSpi, provider, type);
        date = _date;
    }
}
