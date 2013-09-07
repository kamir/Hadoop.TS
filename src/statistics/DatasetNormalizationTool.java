package statistics;

import data.series.Messreihe;
import java.util.Vector;
import stdlib.StdRandom;

/**
 *
 * @author kamir
 */
public class DatasetNormalizationTool {

        public static Messreihe normalizeToMWZeroANDSTDevONE(Messreihe mrA) {
            
            Messreihe mrB = mrA.subtractAverage();
            
            double stdev = mrA.getStddev();
            double stdevR = 1.0/stdev;
            mrB = mrB.scaleY_2( stdevR );
            
            return mrB;
        }
        
}
