package Messaging;


import org.bouncycastle.openpgp.PGPPublicKey;

public class PublicKeyElem extends KeyElem {

    private  PGPPublicKey pk;

    PublicKeyElem(String algo, String username, PGPPublicKey pk) {
        super(algo, username);
        this.pk = pk;
    }

    @Override
    public long getKeyId() {
        return pk.getKeyID();
    }
}
