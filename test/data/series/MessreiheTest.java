/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.series;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;
import junit.framework.TestCase;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author napierala
 */
public class MessreiheTest extends TestCase {
    
    public MessreiheTest(String testName) {
        super(testName);
    }

    /**
     * Test of isNotEmpty method, of class Messreihe.
     */
    public void testIsNotEmpty() {
        System.out.println("isNotEmpty");
        Messreihe instance = new Messreihe();
        boolean expResult = false;
        boolean result = instance.isNotEmpty();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDecimalFormat_STAT method, of class Messreihe.
     */
    public void testGetDecimalFormat_STAT() {
        System.out.println("getDecimalFormat_STAT");
        Messreihe instance = new Messreihe();
        DecimalFormat expResult = null;
        DecimalFormat result = instance.getDecimalFormat_STAT();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDecimalFormat_X method, of class Messreihe.
     */
    public void testGetDecimalFormat_X() {
        System.out.println("getDecimalFormat_X");
        Messreihe instance = new Messreihe();
        DecimalFormat expResult = null;
        DecimalFormat result = instance.getDecimalFormat_X();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDecimalFormat_Y method, of class Messreihe.
     */
    public void testGetDecimalFormat_Y() {
        System.out.println("getDecimalFormat_Y");
        Messreihe instance = new Messreihe();
        DecimalFormat expResult = null;
        DecimalFormat result = instance.getDecimalFormat_Y();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatus method, of class Messreihe.
     */
    public void testGetStatus() {
        System.out.println("getStatus");
        Messreihe instance = new Messreihe();
        StringBuffer expResult = null;
        StringBuffer result = instance.getStatus();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGaussianDistribution method, of class Messreihe.
     */
    public void testGetGaussianDistribution() {
        System.out.println("getGaussianDistribution");
        int i = 0;
        Messreihe expResult = null;
        Messreihe result = Messreihe.getGaussianDistribution(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class Messreihe.
     */
    public void testGetSize() {
        System.out.println("getSize");
        Messreihe instance = new Messreihe();
        int[] expResult = null;
        int[] result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addValuePair method, of class Messreihe.
     */
    public void testAddValuePair() {
        System.out.println("addValuePair");
        double x = 0.0;
        double y = 0.0;
        Messreihe instance = new Messreihe();
        instance.addValuePair(x, y);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getXValues method, of class Messreihe.
     */
    public void testGetXValues() {
        System.out.println("getXValues");
        Messreihe instance = new Messreihe();
        Vector expResult = null;
        Vector result = instance.getXValues();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getYValues method, of class Messreihe.
     */
    public void testGetYValues() {
        System.out.println("getYValues");
        Messreihe instance = new Messreihe();
        Vector expResult = null;
        Vector result = instance.getYValues();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Messreihe.
     */
    public void testToString() {
        System.out.println("toString");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLabel method, of class Messreihe.
     */
    public void testSetLabel() {
        System.out.println("setLabel");
        String label = "";
        Messreihe instance = new Messreihe();
        instance.setLabel(label);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAddinfo method, of class Messreihe.
     */
    public void testSetAddinfo() {
        System.out.println("setAddinfo");
        String addinfo = "";
        Messreihe instance = new Messreihe();
        instance.setAddinfo(addinfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAddinfo method, of class Messreihe.
     */
    public void testGetAddinfo() {
        System.out.println("getAddinfo");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getAddinfo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLabel method, of class Messreihe.
     */
    public void testGetLabel() {
        System.out.println("getLabel");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getLabel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getData method, of class Messreihe.
     */
    public void testGetData() {
        System.out.println("getData");
        Messreihe instance = new Messreihe();
        double[][] expResult = null;
        double[][] result = instance.getData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calcAverage method, of class Messreihe.
     */
    public void testCalcAverage() {
        System.out.println("calcAverage");
        Messreihe instance = new Messreihe();
        instance.calcAverage();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvarage method, of class Messreihe.
     */
    public void testGetAvarage() {
        System.out.println("getAvarage");
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getAvarage();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDecimalFomrmatX method, of class Messreihe.
     */
    public void testSetDecimalFomrmatX() {
        System.out.println("setDecimalFomrmatX");
        String format = "";
        Messreihe instance = new Messreihe();
        instance.setDecimalFomrmatX(format);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDecimalFomrmatY method, of class Messreihe.
     */
    public void testSetDecimalFomrmatY() {
        System.out.println("setDecimalFomrmatY");
        String format = "";
        Messreihe instance = new Messreihe();
        instance.setDecimalFomrmatY(format);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxX method, of class Messreihe.
     */
    public void testGetMaxX() {
        System.out.println("getMaxX");
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getMaxX();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxY method, of class Messreihe.
     */
    public void testGetMaxY() {
        System.out.println("getMaxY");
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getMaxY();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatisticData method, of class Messreihe.
     */
    public void testGetStatisticData() {
        System.out.println("getStatisticData");
        String pre = "";
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getStatisticData(pre);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getYData method, of class Messreihe.
     */
    public void testGetYData() {
        System.out.println("getYData");
        Messreihe instance = new Messreihe();
        double[] expResult = null;
        double[] result = instance.getYData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getXYSeries method, of class Messreihe.
     */
    public void testGetXYSeries() {
        System.out.println("getXYSeries");
        Messreihe instance = new Messreihe();
        XYSeries expResult = null;
        XYSeries result = instance.getXYSeries();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeToFile method, of class Messreihe.
     */
    public void testWriteToFile() {
        System.out.println("writeToFile");
        File f = null;
        Messreihe instance = new Messreihe();
        instance.writeToFile(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addValue method, of class Messreihe.
     */
    public void testAddValue() {
        System.out.println("addValue");
        double y = 0.0;
        Messreihe instance = new Messreihe();
        instance.addValue(y);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLabel_X method, of class Messreihe.
     */
    public void testGetLabel_X() {
        System.out.println("getLabel_X");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getLabel_X();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLabel_X method, of class Messreihe.
     */
    public void testSetLabel_X() {
        System.out.println("setLabel_X");
        String label_X = "";
        Messreihe instance = new Messreihe();
        instance.setLabel_X(label_X);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLabel_Y method, of class Messreihe.
     */
    public void testSetLabel_Y() {
        System.out.println("setLabel_Y");
        String label_Y = "";
        Messreihe instance = new Messreihe();
        instance.setLabel_Y(label_Y);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLabel_Y method, of class Messreihe.
     */
    public void testGetLabel_Y() {
        System.out.println("getLabel_Y");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getLabel_Y();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStddev method, of class Messreihe.
     */
    public void testGetStddev() {
        System.out.println("getStddev");
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getStddev();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalize method, of class Messreihe.
     */
    public void testNormalize() {
        System.out.println("normalize");
        Messreihe instance = new Messreihe();
        instance.normalize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getYValueForX method, of class Messreihe.
     */
    public void testGetYValueForX() {
        System.out.println("getYValueForX");
        int i = 0;
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getYValueForX(i);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBinningX_sum method, of class Messreihe.
     */
    public void testSetBinningX_sum() {
        System.out.println("setBinningX_sum");
        int bin = 0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.setBinningX_sum(bin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBinningX_average method, of class Messreihe.
     */
    public void testSetBinningX_average() {
        System.out.println("setBinningX_average");
        int bin = 0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.setBinningX_average(bin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divY method, of class Messreihe.
     */
    public void testDivY() {
        System.out.println("divY");
        double divBy = 0.0;
        Messreihe instance = new Messreihe();
        instance.divide_Y_by(divBy);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of summeY method, of class Messreihe.
     */
    public void testSummeY() {
        System.out.println("summeY");
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.summeY();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of diff method, of class Messreihe.
     */
    public void testDiff() {
        System.out.println("diff");
        Messreihe mr2 = null;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.diff(mr2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class Messreihe.
     */
    public void testAdd() {
        System.out.println("add");
        Messreihe mr2 = null;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.add(mr2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addValues method, of class Messreihe.
     */
    public void testAddValues() {
        System.out.println("addValues");
        Messreihe mr = null;
        Messreihe instance = new Messreihe();
        instance.addValues(mr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scaleX method, of class Messreihe.
     */
    public void testScaleX() {
        System.out.println("scaleX");
        int maxX = 0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.scaleX(maxX);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addStatusInfo method, of class Messreihe.
     */
    public void testAddStatusInfo() {
        System.out.println("addStatusInfo");
        String string = "";
        Messreihe instance = new Messreihe();
        instance.addStatusInfo(string);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatusInfo method, of class Messreihe.
     */
    public void testGetStatusInfo() {
        System.out.println("getStatusInfo");
        Messreihe instance = new Messreihe();
        String expResult = "";
        String result = instance.getStatusInfo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cut method, of class Messreihe.
     */
    public void testCut() {
        System.out.println("cut");
        int nrOfValues = 0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.cut(nrOfValues);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shift method, of class Messreihe.
     */
    public void testShift() throws Exception {
        System.out.println("shift");
        int offset = 0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.shift(offset);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class Messreihe.
     */
    public void testCopy() {
        System.out.println("copy");
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.copy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scaleX_2 method, of class Messreihe.
     */
    public void testScaleX_2() {
        System.out.println("scaleX_2");
        double f = 0.0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.scaleX_2(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scaleY_2 method, of class Messreihe.
     */
    public void testScaleY_2() {
        System.out.println("scaleY_2");
        double f = 0.0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.scaleY_2(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLabels method, of class Messreihe.
     */
    public void testSetLabels() {
        System.out.println("setLabels");
        String label = "";
        String xL = "";
        String yL = "";
        Messreihe instance = new Messreihe();
        instance.setLabels(label, xL, yL);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of averageForAll method, of class Messreihe.
     */
    public void testAverageForAll() {
        System.out.println("averageForAll");
        Messreihe[] mrs = null;
        Messreihe expResult = null;
        Messreihe result = Messreihe.averageForAll(mrs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getX_for_Y method, of class Messreihe.
     */
    public void testGetX_for_Y() {
        System.out.println("getX_for_Y");
        double y = 0.0;
        Messreihe instance = new Messreihe();
        double expResult = 0.0;
        double result = instance.getX_for_Y(y);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of linFit method, of class Messreihe.
     */
    public void testLinFit() throws Exception {
        System.out.println("linFit");
        double x_min = 0.0;
        double x_max = 0.0;
        Messreihe instance = new Messreihe();
        SimpleRegression expResult = null;
        SimpleRegression result = instance.linFit(x_min, x_max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of split method, of class Messreihe.
     */
    public void testSplit() {
        System.out.println("split");
        int length = 0;
        int anzahl = 0;
        Messreihe instance = new Messreihe();
        Messreihe[] expResult = null;
        Messreihe[] result = instance.split(length, anzahl);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shrinkX method, of class Messreihe.
     */
    public void testShrinkX() {
        System.out.println("shrinkX");
        double min = 0.0;
        double max = 0.0;
        Messreihe instance = new Messreihe();
        Messreihe expResult = null;
        Messreihe result = instance.shrinkX(min, max);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addComment method, of class Messreihe.
     */
    public void testAddComment() {
        System.out.println("addComment");
        String string = "";
        Messreihe instance = new Messreihe();
        instance.addComment(string);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scaleXto method, of class Messreihe.
     */
    public void testScaleXto() {
        System.out.println("scaleXto");
        int i = 0;
        Messreihe instance = new Messreihe();
        instance.scaleXto(i);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkKonsistenz method, of class Messreihe.
     */
    public void testCheckKonsistenz() {
        System.out.println("checkKonsistenz");
        Messreihe instance = new Messreihe();
        instance.checkKonsistenz();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of show method, of class Messreihe.
     */
    public void testShow() {
        System.out.println("show");
        Messreihe instance = new Messreihe();
        instance.show();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
