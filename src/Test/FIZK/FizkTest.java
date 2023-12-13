package Test.FIZK;

import Fizk.Protocol;
import Fizk.PublicKeyPair;
import Fizk.SecretkeyPair;
import Fizk.Signature;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class FizkTest {

    @Test
    public void testThatSignatureSchemeWorks() throws Exception {
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.getSecretkeyPair();
        PublicKeyPair publicKeyPair = protocol.getPublicKeyPair();
        byte[] message = "Hello World".getBytes();
        Signature signature = protocol.sign(secretkeyPair, message);
        boolean verification = protocol.verify(publicKeyPair, message, signature);
        Assert.assertTrue(verification);
    }

    @Test
    public void shouldAcceptWithDifferentProtocolInstances() throws Exception {
        byte[] message = "Hello World".getBytes();
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.getSecretkeyPair();
        Signature signature = protocol.sign(secretkeyPair, message);

        Protocol protocol2 = new Protocol();
        protocol2.generateSecretkeyPair();
        SecretkeyPair secretkeyPair2 = protocol2.getSecretkeyPair();
        PublicKeyPair publicKeyPair2 = protocol2.getPublicKeyPair();
        Signature signature2 = protocol2.sign(secretkeyPair2, message);

        Assert.assertFalse(Arrays.equals(signature.getChallenge(), signature2.getChallenge()));
        Assert.assertFalse(Arrays.equals(signature.getProof().aArray, signature2.getProof().aArray));

        boolean verification = protocol.verify(publicKeyPair2, message, signature2);
        Assert.assertTrue(verification);
    }

    @Test
    public void signatureSchemeShouldReject() throws Exception {
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.getSecretkeyPair();
        PublicKeyPair publicKeyPair = protocol.getPublicKeyPair();
        byte[] message = "Hello World".getBytes();
        Signature signature = protocol.sign(secretkeyPair, message);
        byte[] wrongMessage = "World! Hello".getBytes();
        boolean verification = protocol.verify(publicKeyPair, wrongMessage, signature);
        Assert.assertFalse(verification);
    }

    @Test
    public void differentMessagesShouldReject() throws Exception {
        byte[] message = "Hello World".getBytes();
        byte[] wrongMessage = "World! Hello".getBytes();
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.getSecretkeyPair();
        PublicKeyPair publicKeyPair = protocol.getPublicKeyPair();


        Signature wrongSignature = protocol.sign(secretkeyPair, wrongMessage);

        boolean verification = protocol.verify(publicKeyPair, message, wrongSignature);
        Assert.assertFalse(verification);
    }

    @Test
    public void shouldRejectWithDifferentSignatures() throws Exception {
        byte[] message = "Hello World".getBytes();
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.getSecretkeyPair();
        PublicKeyPair publicKeyPair = protocol.getPublicKeyPair();
        Signature signature = protocol.sign(secretkeyPair, message);

        Protocol protocol2 = new Protocol();
        protocol2.generateSecretkeyPair();
        SecretkeyPair secretkeyPair2 = protocol2.getSecretkeyPair();
        Signature signature2 = protocol2.sign(secretkeyPair2, message);

        Assert.assertFalse(Arrays.equals(signature.getChallenge(), signature2.getChallenge()));
        Assert.assertFalse(Arrays.equals(signature.getProof().aArray, signature2.getProof().aArray));

        boolean verification = protocol.verify(publicKeyPair, message, signature2);
        Assert.assertFalse(verification);
    }
}
