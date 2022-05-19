package Messaging;

public class User {
    private KeyRings keyRings;

    User(){
        keyRings = new KeyRings();
    }

    public KeyRings getKeyRings(){
        return this.keyRings;
    }

}
