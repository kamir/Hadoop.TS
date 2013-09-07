/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package statphys.detrending;

import data.series.Messreihe;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class FluctuationFunktion {

    Messreihe Fs = null;

    FluctuationFunktion( Messreihe mr, String label ) {
        this.Fs = mr;
        this.label = label;
        Fs.setLabel( label + " " + Fs.getLabel() );
    };

    public double calcAlpha( Fitbereich fb ) {
        double alpha = 0.0;
            try {

                double fu = fb.getULog10();
                double fo = fb.getOLog10();

                alpha = Fs.linFit( fu, fo).getSlope();
                System.out.println(">>> Alpha wurde neu berechnet ["+ fu +","+fo+"] ... a=" + alpha + " beta=" + calcBeta(alpha) );
            }
            catch (Exception ex) {
                Logger.getLogger(FluctuationFunktion.class.getName()).log(Level.SEVERE, null, ex);
                alpha = Double.NaN;
            }

        return alpha;
    };

    public double calcBeta( Fitbereich fb ) {
        double alpha = calcAlpha(fb);
        return 2.0 * alpha - 1.0;
    };

    public double calcBeta( double alpha ) {
        return 2.0 * alpha - 1.0;
    };

    String label = null;
    public String getLabel() {
        return label;
    }


}
