package hadoopts.buckets;

import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.SingleRowTSO;
import hadoopts.core.TSBucket;
import hadoopts.core.TSOperation;
import java.io.IOException;
import java.util.Vector;

/**
 * A TSBucket is the date container for managing a set of time series.
 * 
 * The BucketLoader knows how to read data from disk or DB.
 * 
 * @author kamir
 */

public class BucketLoader {
    
    TSBucket b = null;

    public TSBucket getTSBucket() {
        return b;
    }

    public Vector<Integer> getIds() {
        return ids;
    }
    
    boolean DO_FILTER = true;
    Vector<Integer> ids = null;
    
    public static int default_LIMIT = 50;
    public static boolean default_INMEM = true;
    
    /**
     * load bucket and store it in a variable called : b
     * 
     * @param fn
     * @throws IOException 
     */
    public void loadBucketData( String fn ) throws IOException {
                
        TSBucket bucket = TSBucket.createEmptyBucketFull();
        bucket.inMEM = default_INMEM;
        
        bucket.loadFromLocalFS( fn, null );
        b = bucket;
    }  
    
    /**
     * Load and get the TSBucket.
     * 
     * @param fn
     * @return
     * @throws IOException 
     */
    public TSBucket loadBucket( String fn ) throws IOException {
        
        TSBucket bucket = TSBucket.createEmptyBucketFull();
        bucket.inMEM = default_INMEM;
        
        bucket.loadFromLocalFS( fn, ids );
        b = bucket;
        return b;
    }  
    
    
    /**
     * Load and preprocess the data.
     * The result bucket contains the preprocessed data.
     * 
     * @param fn
     * @param tst
     * @return
     * @throws IOException 
     */
    public TSBucket processBucket( String fn, TSOperation tst ) throws IOException {

        TSBucket bucket = TSBucket.createEmptyBucketFull();
        
        bucket.isProcessed = true;
        bucket.isProcessedBy = tst.getClass();

        bucket.loadAndProcess( fn, tst, ids );
        
        // store the preprocessed bucket also localy...
        
        b = bucket;
        return bucket;
    }  

    // get the bucketdata ...
    public Vector<Messreihe> getBucketData() {
      return b.getBucketData();
    }

    public void loadBucket(String absolutePath, Vector<Integer> i) throws IOException {
        if ( i == null ) {
            loadBucket( absolutePath );
        }
        else {
            DO_FILTER = true;
            ids = i;
            loadBucket( absolutePath );
        } 
    }

    public void processBucket(String absolutePath, SingleRowTSO singleTsTool, Vector<Integer> i) throws IOException {
        if ( i == null ) {
            processBucket( absolutePath, singleTsTool  );
        }
        else {
            DO_FILTER = true;
            ids = i;
            processBucket( absolutePath, singleTsTool );
        } 
    }

    public void setLimit(int i) {
       default_LIMIT = i;
       TSBucket.default_LIMIT = i;
    }
    
    
            
    
}
