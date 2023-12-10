package Test.Shares;

import BooleanCircuit.Shares;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;


/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class SharesTest {

    @Test
    public void encodingOfTwoSecretKeysShouldBeDifferent() {
        Shares shares = new Shares();
        SecretKey secretKey1 = shares.generateSecretKey();
        SecretKey secretKey2 = shares.generateSecretKey();
        byte[] encoded1 = secretKey1.getEncoded();
        byte[] encoded2 = secretKey2.getEncoded();
        Assert.assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void encodingOfTheSameSecretKeysShouldEqual() {
        Shares shares = new Shares();
        SecretKey secretKey1 = shares.generateSecretKey();
        byte[] encoded1 = secretKey1.getEncoded();
        byte[] encoded2 = secretKey1.getEncoded();
        Assert.assertArrayEquals(encoded1, encoded2);
    }
}
