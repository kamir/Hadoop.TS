/*
 *
 * 
 */
package hadoopts.core;

import hadoopts.buckets.BucketLoader;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author kamir
 */
public abstract class AbstractTSProcessor implements TSProcessor {
    
    TSOperation operation = null;

    public TSOperation getTSOperation() {
        return operation;
    }

    public void setTSOperation(TSOperation operation) {
        this.operation = operation;
    }

    /**
     * load and get the TSBucket
     */
    public static TSBucket loadBucket(String name) throws IOException {
        BucketLoader bl = new BucketLoader();
        TSBucket tsb = bl.loadBucket(name);
        return tsb;
    }
    
    abstract public void workOnBucketFolder(File[] files) throws IOException;
        
    abstract public void setFilter( String fn ) throws IOException;
    abstract public void initFilter() throws IOException;
    
        /**
     * Get an unique analysis run id from the 
     * Metadatastore.
     * 
     * @return 
     */
    public String getRunID() { 
        return null;
    };

    /**
     * Store a link to the result set.
     */
    public void storeResultset( String resultSet ) { 
    
    };
    
    public void initMetaDataStore() { 
    
    };
    
}
