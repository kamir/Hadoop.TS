/*
 *  A HDFS client to create a SequenceFile with a
 *
 *  Key=LongWriteable
 *  Value=MessreiheData
 *
 *  based
 *
 *
 */

package hadoopts.buckets;

import hadoopts.topics.wikipedia.AccessFileFilter;
import hadoopts.core.TSBucket;
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
 * @author sebastian
 */
public class BucketCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

        // String local base path
        String baseIn = "/media/esata/wiki/extract/FD3/clean_";
        String baseOut = "/media/esata/wiki/extract/FD3/clean_";

        System.out.println( new Date( System.currentTimeMillis() ) );
        
        // inputfolder
        String[] folders = {
//                             "ng_sv_222.dat" ,   x
//                             "ng_fa_66.dat",     x
//                             "ng_fi_68.dat" x
//                             "ng_he_88.dat"  x
//                             "ng_ko_122.dat"  x
//                             "ng_nl_166.dat"     x
//                               "nl_allIWL_IDS.dat", x
//                               "nl_ons0_IDS.dat",   x
//                               "he_allIWL_IDS.dat", x
//                               "he_ons0_IDS.dat",  x
//                               "fi_allIWL_IDS.dat", x
//                               "fi_ons0_IDS.dat",  x
//                               "en_allIWL_IDS.dat", x
//                               "en_ons0_IDS.dat", x
//                               "ko_allIWL_IDS.dat", x
//                               "ko_ons0_IDS.dat",  x
//                               "ja_allIWL_IDS.dat", x
//                               "ja_ons0_IDS.dat", x
//                               "sv_allIWL_IDS.dat", x
//                               "sv_ons0_IDS.dat" x

                               "dax_iwl_all.dat",
                               "sp500_iwl_all.dat",
                               "dax_iwl_ons0.dat",
                               "sp500_iwl_ons0.dat"



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

            File[] liste =  f.listFiles( new AccessFileFilter() );

            System.out.println("\tz="+liste.length  );

//            TSBucket.LIMIT = 100;
//            TSBucket.outputDir = baseOut;
//            TSBucket.sourcFolder = baseIn;

            TSBucket tsb = new TSBucket();
            tsb.createBucketFromLocalFilesInDirectory(s, 100);
        }
    }

}


