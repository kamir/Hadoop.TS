/**
 *  An HDFS client to create a SequenceFile with time series.
 *
 *  KEY   = LongWriteable
 *  VALUE = MessreiheData
 *
 *  We do no load data, but we use a GENERATOR here ...
 * 
 *  A loop defines several BETA values and creates one 
 *  TS Bucket File for each beta.
 *
 **/

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
 * @author sebastian
 */
public class TSBucketCreator_FFMLRC {

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
        
        System.out.println( ">>> FFMLRCBucketCreator (" + new Date( System.currentTimeMillis() ) +")" );
        System.out.println( ">   OUT : " + baseOut );
        
        String s = "abucket.ts.seq";
 
        // We do no load data, but we use a GENERATOR here ...

        int Z = 15;
        double STRETCH = 4;

        int LOOPS = 100;
        
        int EXP = 16;

        for( int i = 0; i < Z; i++ ) {
            
            double beta = 0.1 + ( i * STRETCH * 0.05 ); 
            
            System.out.println(">>> i=" +  i + "\t beta=" + beta );
        
            int z = LOOPS;

            TSBucket tsb = new TSBucket();
            
            try {
                tsb.createBucketWithRandomTS( baseOut + s, z, EXP, beta );
            } 
            catch (Exception ex) {
                Logger.getLogger(TSBucketCreator_FFMLRC.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }        

    }

}


