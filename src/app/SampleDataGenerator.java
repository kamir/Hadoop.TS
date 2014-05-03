/**
 * We create some sample data here ...
 */
package app;

import hadoopts.buckets.generator.TSBucketCreator_FFMLRC;
import hadoopts.buckets.generator.TSBucketCreator_Sinus;
import hadoopts.buckets.generator.TSBucketCreator_Uncorrelated;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import statphys.ris.experimental.TSPropertyTester;

/**
 *
 * @author kamir
 */
public class SampleDataGenerator {

    public static TSPropertyTester tester = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        
        TSPropertyTester tester = new TSPropertyTester();

        String folder = "sample/";

        String[] a = new String[2];
        a[0] = folder;

        try {

            /**
             * LRC time sries - simple Fourier Filter Method             *
             */
            TSBucketCreator_FFMLRC.main(a);

            /**
             * Uncorrelated time series             *
             */
            a[1] = TSBucketCreator_Uncorrelated.mode_GAUSSIAN + "";
//            Properties props = new Properties();
//            props.put("mu", 5);
//            props.put("sigma", 0.5);
//            TSBucketCreator_Uncorrelated.PARAM = props;
            TSBucketCreator_Uncorrelated.main(a);

            /**
             * Sine waves ...
             *
             */
            TSBucketCreator_Sinus.main(a);
            // RANDOM PEAK time series (for Event Synchronization ) ...
            // .... to be done ...

            tester.showTestResult();
        } catch (Exception ex) {
            Logger.getLogger(SampleDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
