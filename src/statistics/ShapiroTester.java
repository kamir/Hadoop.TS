/**
 *
 * 
 **/
package statistics;

import java.util.Vector;
 
/**
 *
 * @author kamir
 */
public class ShapiroTester {
   
    Vector<Double> pValues = new Vector<Double>();
    
    public void init() { 
        pValues = new Vector<Double>();    
    }
        
    public double[] test( double[] data ) { 
        double[] pV = Statistical.shapiroWilk(data);     
        // pValues.add( pV[0] );
        return pV;
    }
    
}
