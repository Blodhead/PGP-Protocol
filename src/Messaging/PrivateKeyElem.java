package Messaging;


import org.bouncycastle.openpgp.PGPSecretKey;

public class PrivateKeyElem extends KeyElem {

    private  PGPSecretKey pk;

    PrivateKeyElem(String algo, String username, PGPSecretKey pk) {
        super(algo, username);
        this.pk = pk;
    }

    @Override
    public long getKeyId() {
        return pk.getKeyID();
    }
}
