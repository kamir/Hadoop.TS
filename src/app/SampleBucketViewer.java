/**
 *
 * Load and show some Bucket Content ...
 * 
 **/
package app;

import java.io.IOException;
import app.experimental.SimpleBucketTool;

/**
 *
 * @author kamir
 */
public class SampleBucketViewer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    
        SimpleBucketTool.LIMIT = 25;
        
        String folder = "sample/";
            
        String[] a = new String[2];
        a[0] = folder;
        
        
//        a[1] = "sinus";
//        SimpleBucketTool.main( a );
//        
        a[1] = "alpha_0.5";
        SimpleBucketTool.main( a );
        
//        a[1] = "LRC_beta_2.9";
//        SimpleBucketTool.main( a );
                            
    }
}
