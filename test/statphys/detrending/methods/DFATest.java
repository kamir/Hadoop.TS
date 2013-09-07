/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package statphys.detrending.methods;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sebastian
 */
public class DFATest {

    public DFATest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testFit1() {
        System.out.println("fit1");

        DFA instance = new DFA();
        double data[] = new double[6];
        
        int m = 4;
        int n = 1;

        data[0] = m*0+n;
        data[1] = m*1+n;
        data[2] = m*2+n;
        data[3] = m*3+n;
        data[4] = m*4+n;
        data[5] = m*5+n;

        double[] expected = new double[2];
        expected[0] = n;
        expected[1] = m;

        instance.pr = data;

        double[] result = instance.fit1(0,5);
        for ( int i = 0 ; i < result.length; i++ ) {
            System.out.println( "Order: " + i + ") " + result[i] );
            assertEquals(expected[i],result[i], 0e-12 );
        }
        System.out.println();
               
        int grad = 1;
        result = instance.fitn(0,5,grad);
        for ( int i = 0 ; i < result.length ; i++ ) {
            System.out.println( "[Order: " + i + "] " + result[i] );
            assertEquals(expected[i],result[i], 0e-12 );
        }
    }

    @Test
    public void testFit2() {
        System.out.println("fit2");

        DFA instance = new DFA();
        double data[] = new double[10];

        int a = 4;
        int b = 7;
        int c = 1;

        for( int i = 0 ; i < 10; i++) {
            data[i] = a*i*i+b*i+c;
            System.out.println( data[i] );
        }

        System.out.println();

        double[] expected = new double[3];
        expected[0] = c;
        expected[1] = b;
        expected[2] = a;

        instance.pr = data;

        double[] result = instance.fit2(0,8);
        for ( int i = 0 ; i < result.length; i++ ) {
            System.out.println( "Order: " + i + ") " + result[i] );
            assertEquals(expected[i],result[i], 0e-12 );
        }
        System.out.println();

        int grad = 2;
        result = instance.fitn(0,8,grad);
        for ( int i = 0 ; i < result.length ; i++ ) {
            System.out.println( "[" + i + "] " + result[i] );
            assertEquals(expected[i],result[i], 0e-12 );
        }
    }

    @Test
    public void testInitIntervalS() {
        System.out.println("initIntervalS");

        DFA instance = new DFA();
        instance.initParameter();
        
        instance.para.setGradeOfPolynom(3);
        instance.para.setN(1000);
        instance.initIntervalS();


        double data[] = new double[1000];

        int m = 4;
        int n = 1;

        for( int i = 0; i < 1000 ; i++ ) {
            data[i] = m*i+n;
        }

        double[] expected = new double[2];

        expected[0] = n;
        expected[1] = m;

        //instance.calc();


        int[] s = instance.getIntervalS();

        System.out.println( "Länge: " + s.length );

        for( int i = 0; i < s.length ; i++ ) {
            System.out.println( s[i] );
        };


    }


}