package Test.ZKBoo;

import BooleanCircuit.Gate;
import org.junit.Assert;
import org.junit.Test;

public class GateTest {

    @Test
    public void evalXORTest() {
        Assert.assertFalse(Gate.evalXOR(false, false));
        Assert.assertTrue(Gate.evalXOR(false, true));
        Assert.assertTrue(Gate.evalXOR(true, false));
        Assert.assertFalse(Gate.evalXOR(true, true));
    }

    @Test
    public void evalANDTest() {
        Assert.assertTrue(Gate.evalAND(false, false, false, false, false, true));
        Assert.assertTrue(Gate.evalAND(false, false, false, false, true, false));
        Assert.assertFalse(Gate.evalAND(false, false, false, false, true, true));
        Assert.assertTrue(Gate.evalAND(true, true, false, false, false, false));
        Assert.assertFalse(Gate.evalAND(true, true, true, false, false, false));
        Assert.assertTrue(Gate.evalAND(true, true, true, true, true, true));
    }

    @Test
    public void evalINVTest() {
        Assert.assertTrue(Gate.evalINV(false));
        Assert.assertFalse(Gate.evalINV(true));
    }


}
