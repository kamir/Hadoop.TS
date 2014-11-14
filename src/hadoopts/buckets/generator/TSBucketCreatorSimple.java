/**
 * An HDFS client to create a SequenceFile with time series.
 *
 * KEY = LongWriteable VALUE = MessreiheData
 *
 * We do no load data, but we use a GENERATOR here ...
 *
 *
 *
 */
package hadoopts.buckets.generator;

import data.series.Messreihe;
import hadoopts.topics.wikipedia.AccessFileFilter;
import hadoopts.core.TSBucket;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author kamir
 */
public class TSBucketCreatorSimple {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

        stdlib.StdRandom.initRandomGen(1);

        String baseOut = "./tstest/";
        
        if ( args!= null ) { 
            if ( args.length > 0 ) baseOut = baseOut + args[0];
            File f = new File( baseOut );
            if ( !f.exists() ) { 
                f.mkdirs();
            }
        }

        System.out.println(">>> SinusTSBucketCreator (" + new Date(System.currentTimeMillis()) + ")");
        System.out.println(">   OUT : " + baseOut);

        String s = "abucket.ts.seq";

        // We do no load data, but we use a GENERATOR here ...

        int ANZ = 100;

        TSBucket tsb = new TSBucket();

        double fMIN = 1.0; 
        double fMAX = 2000.0; 
        double aMIN = 1.0; 
        double aMAX = 10.0; 
        
        double SR = 4.0 * fMAX;         // pro Sekunde
        
        double time = 10;           // Sekunden
        
        try {
            tsb.createBucketWithRandomTS_sinus(baseOut + s, ANZ, fMIN, fMAX, aMIN, aMAX, SR, time );
        } catch (Exception ex) {
            Logger.getLogger(TSBucketCreatorSimple.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
