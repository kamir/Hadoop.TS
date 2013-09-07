package data.series;

/**
 * Eine Klasse zur Einschränkung des Bereiches einer Messreihe.
 * Mit u und o wird die Untere und die Obere Grenze des Bereichs
 * der zulässigen X-Werte angegeben.
 *
 * In der Variabel mr wird eine Referenz auf die "originale" Messreihe
 * vorgehalten.
 */
public class MessIntervall extends Messreihe {

    Messreihe mr = null;

    public MessIntervall( Messreihe mr , double u, double o ) {
        super();
        this.mr = mr;
        this.init(u,o);
    };

    public void init(double u, double o) {

        int max = mr.getXValues().size();

        for (int i = 0 ; i < max; i++  ) {
            double x = (Double)mr.getXValues().elementAt(i);
            double y = (Double)mr.getYValues().elementAt(i);

            if ( x >= u && x <= o )
                 addValuePair(x, y);
        };
    };



}
