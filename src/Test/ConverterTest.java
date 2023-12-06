package Test;

import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;

import static Util.Converter.*;

public class ConverterTest {

    @Test
    public void intToBooleanArrayTest() {
        Assert.assertTrue(Arrays.equals(intToBooleanArray(0, 5), new boolean[]{false, false, false, false, false}));
        Assert.assertTrue(Arrays.equals(intToBooleanArray(1, 1), new boolean[] {true}));
        Assert.assertTrue(Arrays.equals(intToBooleanArray(0, 1), new boolean[] {false}));
        Assert.assertTrue(Arrays.equals(intToBooleanArray(1, 2), new boolean[] {false, true}));
        Assert.assertTrue(Arrays.equals(intToBooleanArray(2, 5), new boolean[] {false, false, false, true, false}));
        Assert.assertTrue(Arrays.equals(intToBooleanArray(8, 5), new boolean[] {false, true, false, false, false}));
    }

    @Test
    public void convertByteArrayToBooleanArrayTest() {
        byte[] bytes = new byte[] {0, 0, 1};
        boolean[] result = convertByteArrayToBooleanArray(bytes);
        Assert.assertTrue(Arrays.equals(result, new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true}));
    }

    @Test
    public void convertBooleanArrayToByteArrayTestEightyFive() {
        boolean[] bool = {true, false, true, false, true, false, true, false};
        // 85 = 01010101 = 1 + 4 + 16 + 64
        byte[] bytes = {85};
        Arrays.equals(bytes, convertBooleanArrayToByteArray(bool));
    }

    @Test
    public void convertBooleanArrayToByteArrayTestZero() {
        boolean[] bool = {false, false, false, false, false, false, false, false};
        byte[] bytes = {0};
        Assert.assertTrue(Arrays.equals(bytes, convertBooleanArrayToByteArray(bool)));
    }

    @Test
    public void convertBooleanArrayToByteArrayTestOne() {
        boolean[] bool = {false, false, false, false, false, false, false, true};
        byte[] bytes = {1};
        Assert.assertTrue(Arrays.equals(bytes, convertBooleanArrayToByteArray(bool)));
    }

    @Test
    public void convertBooleanArrayToByteArrayTest() {
        boolean[] bool = {false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, true};
        byte[] bytes = {5, 1};
        Assert.assertTrue(Arrays.equals(bytes, convertBooleanArrayToByteArray(bool)));
    }
}
