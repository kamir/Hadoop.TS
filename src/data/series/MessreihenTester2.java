/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.series;

/**
 *
 * @author kamir
 */
public class MessreihenTester2 {

    public static void main( String[] args ) {
        stdlib.StdRandom.initRandomGen(1);
        Messreihe mr = Messreihe.getGaussianDistribution( 50 );
        mr = mr.cutFromStart( 10 );
        System.out.println( mr );
        
        
        
    
    };
}
