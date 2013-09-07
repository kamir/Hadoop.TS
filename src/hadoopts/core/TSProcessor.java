/*
 * It's a marker interface for the adapter to an
 * external Metadata service.
 * 
 */
package hadoopts.core;

/**
 *
 * @author kamir
 */
interface TSProcessor {
    
    /**
     * Get an unique analysis run id from the 
     * Metadatastore.
     * 
     * @return 
     */
    public String getRunID();

    /**
     * Store a link to the result set.
     */
    public void storeResultset( String resultSet );
    
    public void initMetaDataStore();
    
    
}
