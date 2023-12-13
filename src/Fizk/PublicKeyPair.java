package Fizk;

import javax.crypto.SecretKey;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class PublicKeyPair {
    byte[] y;
    SecretKey secretKey;
    public PublicKeyPair(byte[] y, SecretKey secretKey) {
        this.y = y;
        this.secretKey = secretKey;
    }
}
