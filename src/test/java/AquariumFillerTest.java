import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ci3n on 04/1/16.
 */
public class AquariumFillerTest {
    @Rule
    public TestName testName = new TestName();

    private AquariumFiller aquariumFiller = new AquariumFiller();
    private int[] testWaterResult;

    @Before
    public void printMethodName() {
        System.err.println("Starting method " + testName.getMethodName());
    }

    @After
    public void clean() {
        System.err.println();
        testWaterResult = null;
    }

    @Test
    public void testGetWaterAmount() throws Exception {
        int[] testAquarium = new int[]{3, 1, 1, 2, 1, 4};
        aquariumFiller.setAquarium(testAquarium);
        System.err.println("Test subject: " + Arrays.toString(testAquarium));
        int expectedWaterAmountResult = 7;
        int testWaterAmountResult = aquariumFiller.getWaterAmount();
        System.err.println("Expected: " + expectedWaterAmountResult);
        System.err.println("Actual:   " + testWaterAmountResult);
        assertEquals(expectedWaterAmountResult, testWaterAmountResult);
    }

    public void testGetWaterHelper(int[] testAquarium, int[] expectedWaterResult) throws Exception {
        aquariumFiller.setAquarium(testAquarium);
        testWaterResult = aquariumFiller.getWater();
        System.err.println("Test subject: " + Arrays.toString(testAquarium));
        System.err.println("Expected: " + Arrays.toString(expectedWaterResult));
        System.err.println("Actual:   " + Arrays.toString(testWaterResult));
        assertArrayEquals(expectedWaterResult, testWaterResult);
    }

    @Test
    public void testGetWater1() throws Exception {
        testGetWaterHelper(new int[]{3, 1, 1, 2, 1, 4}, new int[]{0, 2, 2, 1, 2, 0});
    }

    @Test
    public void testGetWater2() throws Exception {
        testGetWaterHelper(new int[]{3, 1, 0, 3, 1, 4, 1, 2}, new int[]{0, 0, 0, 0, 2, 0, 1, 0});
    }

    @Test
    public void testGetWater3() throws Exception {
        testGetWaterHelper(new int[]{0, 0, 0, 0, 5, 1, 3, 2, 7, 0, 1, 4, 2, 5, 0, 0, 2, 3, 0, 1, 0, 1, 3, 1, 3, 0, 1, 2, 3},
                new int[]{0, 0, 0, 0, 0, 4, 2, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0});
    }

    @Test
    public void testGetWaterZeroes() throws Exception {
        testGetWaterHelper(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0});
    }

    @Test
    public void testGetWaterSameHeight() throws Exception {
        testGetWaterHelper(new int[]{1, 1, 1, 1}, new int[]{0, 0, 0, 0});
    }


    public void testPrivateGetLocalMaximumsHelper(final int[] testArray, List<Integer> expected) throws Exception {
        Method method = AquariumFiller.class.getDeclaredMethod("getLocalMaximums", int[].class);
        method.setAccessible(true);

        System.err.println("PRIVATE METHOD");
        aquariumFiller.setAquarium(testArray);
        List<Integer> testResult = (List<Integer>)method.invoke(aquariumFiller, testArray);
        System.err.println("Test subject: "+Arrays.toString(testArray));
        System.err.println("Expected: "+ expected);
        System.err.println("Result:   "+ testResult);
        assertEquals(expected.size(), testResult.size());

        Iterator<Integer> i1 = expected.iterator();
        Iterator<Integer> i2 = testResult.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            assertTrue(i1.next().equals(i2.next()));
        }
    }

    @Test
    public void testPrivateGetLocalMaximums1() throws Exception {
        testPrivateGetLocalMaximumsHelper(new int[]{2,1,2,3,4,5,3,2,1,5}, Arrays.asList(0,5,9));
    }

    @Test
    public void testPrivateGetLocalMaximumsSame() throws Exception {
        testPrivateGetLocalMaximumsHelper(new int[]{2,2,2,2}, Arrays.asList(0));
    }

    @Test
    public void testPrivateGetLocalMaximumsSame2() throws Exception {
        testPrivateGetLocalMaximumsHelper(new int[]{1,1,2,2,3,3,4,4,3,3}, Arrays.asList(6));
    }

    public void testPrivateGetSeparateAquariumsHelper(final int[] testArray, int[][] expected) throws Exception {
        Method method = AquariumFiller.class.getDeclaredMethod("getSeparateAquariums");
        method.setAccessible(true);

        System.err.println("PRIVATE METHOD");
        aquariumFiller.setAquarium(testArray);
        int[][] testResult = (int[][])method.invoke(aquariumFiller);
        System.err.println("Test subject: "+Arrays.toString(testArray));
        System.err.print("Expected: ");
        for (int i = 0; i < expected.length; i++) {
            System.err.print(Arrays.toString(expected[i]));
        }
        System.err.println();
        System.err.print("Result:   ");
        for (int i = 0; i < testResult.length; i++) {
            System.err.print(Arrays.toString(testResult[i]));
        }
        System.err.println();
        assertEquals(expected.length, testResult.length);

        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i],testResult[i]);
        }
    }

    @Test
    public void testPrivateGetSeparateAquariumsNoZeroes() throws Exception {
        testPrivateGetSeparateAquariumsHelper(new int[]{1,2,3,4},new int[][]{{1,2,3,4}});
    }

    @Test
    public void testPrivateGetSeparateAquariumsAllZeroes() throws Exception {
        testPrivateGetSeparateAquariumsHelper(new int[]{0,0,0,0},new int[][]{{},{},{},{},{}});
    }

    @Test
    public void testPrivateGetSeparateAquariumsSeveralZeroes() throws Exception {
        testPrivateGetSeparateAquariumsHelper(new int[]{0,1,0,2,0,0,0,3,4,5,0},new int[][]{{},{1},{2},{},{},{3,4,5},{}});
    }

}