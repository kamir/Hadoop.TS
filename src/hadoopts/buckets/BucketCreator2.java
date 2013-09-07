/*
 *  An HDFS client to create a SequenceFile with :
 *
 *  Key   = LongWriteable
 *  Value = TSData
 *
 *  based on single files with time-series data from 
 *  Wikipedia Page-Counts project.
 */

package hadoopts.buckets;

import hadoopts.core.TSBucket;
import hadoopts.topics.wikipedia.AccessFileFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
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
public class BucketCreator2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

//        args[]
        
        // String local base path
        String baseIn = "/media/sda/BUFFER/";
        String baseOut = "/media/sda/BUFFER/";

        System.out.println( new Date( System.currentTimeMillis() ) );
        
        // inputfolder

                String[] folders = {              
//                              "60_part_3_2.dat",
//                             "60_part_4_2.dat",
//                             "60_part_5_2.dat",
//                             "60_part_0_2.dat" ,
//                             "60_part_1_2.dat",
//                             "60_part_2_2.dat",
//                             "60_part_0.dat" ,
//                             "60_part_1.dat",
//                             "60_part_2.dat",
                             "60_part_3.dat",
                             "60_part_4.dat",
                             "60_part_5.dat"
                
                              };

//                String[] folders = { "60_part_0.dat" ,
//                             "60_part_1.dat",
//                             "60_part_2.dat",
//                             "60_part_3.dat",
//                             "60_part_4.dat",
//                             "60_part_5.dat" };

        for( String s : folders ) {

            File f = new File( baseIn + s );
            System.out.println( "--> process folder: " + f.getAbsolutePath() );

//            File[] liste =  f.listFiles( new AccessFileFilter() );

//            System.out.println("\tz="+liste.length  );
  
            TSBucket tsb = new TSBucket();
            tsb.setFolder( baseIn, baseOut  );

            int limit = Integer.MAX_VALUE;
            
//            int limit = 10;
            
            tsb.createBucketFromLocalFilesInDirectory(s, limit);
        }
    }

}


