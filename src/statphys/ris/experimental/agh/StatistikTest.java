package statphys.ris.experimental.agh;

import data.series.Messreihe;

import org.apache.commons.math.stat.regression.SimpleRegression;

public class StatistikTest {

    public static void main( String[] args ) throws Exception {
        
        stdlib.StdRandom.initRandomGen((long) 1.0);
        
        Messreihe mr = Messreihe.getGaussianDistribution(25);
        Messreihe test1 = new Messreihe();
        Messreihe test2 = new Messreihe();

        for ( int i = 1; i < 20; i ++ ) {

            Messreihe mr2 = mr.scaleY_2((double) i );
            
            SimpleRegression reg1 = mr.linFit(5, 20);
            SimpleRegression reg2 = mr2.linFit(5, 20);
            
            test1.addValuePair(i,reg1.getRSquare()-reg2.getRSquare());
            test2.addValuePair(i,reg1.getRSquare()/reg2.getRSquare());
        }        

        System.out.println( test1 );
        System.out.println( test2 );

    };

}
