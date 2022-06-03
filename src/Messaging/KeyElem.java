package Messaging;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

public abstract class KeyElem {

    private String algo;
    private String username;

    protected KeyElem(String algo, String username) {
        this.algo = algo;
        this.username = username;
    }

    @Override
    public String toString() {
        return algo +
                " @" + username +
                " #" + getKeyId();
    }

    public abstract long getKeyId();

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
