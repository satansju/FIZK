package Test.FIZK;

import Fizk.Protocol;
import Fizk.PublicKeyPair;
import Fizk.SecretkeyPair;
import Fizk.Signature;
import org.junit.Assert;
import org.junit.Test;

import java.security.spec.ECField;

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
}
