package Fizk;

import javax.crypto.SecretKey;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class SecretkeyPair {
    int x;
    PublicKeyPair publicKeyPair;
    public SecretkeyPair(PublicKeyPair publicKeyPair, int x) {
        this.publicKeyPair = publicKeyPair;
        this.x = x;
    }
}
