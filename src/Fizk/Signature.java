package Fizk;

import ZKBoo.Proof;

import static Util.Util.hash;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Signature {
    private final Proof prove;
    private final byte[] message;

    private byte[] challenge;

    public byte[] getChallenge() {
        return challenge;
    }

    public Signature(Proof prove, byte[] message) {
        this.prove = prove;
        this.message = message;
        byte[] challengeArray = new byte[prove.aArray.length + message.length];
        System.arraycopy(prove.aArray, 0, challengeArray, 0, prove.aArray.length);
        System.arraycopy(message, 0, challengeArray, prove.aArray.length, message.length);
        this.challenge = hash(challengeArray);
    }

    public Proof getProof() {
        return prove;
    }
}
