package statphys.ris.experimental.agh;

import data.series.Messreihe;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReturnIntervallStatistik {

    public String label;
    public String folder = null;
    
    /*
     *  Die Verteilungsfunktion wird mit Rq skaliert
     */
    public boolean doScale_by_Rq = true;
    
    /*
     * einfach nur gezählte Werte
     */
    public Messreihe mrHaeufigkeit = new Messreihe();
    
    /*
     * normierte Häufigkeiten, somit ergibt die summe aller Werte 1
     */
    public Messreihe mrVerteilung = new Messreihe();

    private int binning = 5;   // anzahl der Werte pro Intervall
    private int scale = 10;    // größter Wert

    private double Rq = 0.0;
    private int zElements = 0;

    /* 
     * die "gebinnten" Werte, also ein Feld von Zählern 
     */
    int data[] = null;
    Vector<Double> dataRaw = null;
    
    static public boolean debug = false;
    public static boolean verbose = true;
    
    // ab welchem Index-Wert für r/R_q soll es losgehen?
    static public int offset = 2;

    public ReturnIntervallStatistik(int bin, int s) {
        binning = bin;
        scale = s;
        initArray();
        label = this.getClass().getName();
        dataRaw = new Vector<Double>();
    }
 
    /** 
     *
     * Aufsammeln von mehreren RIS aus vielen Zeitreihen einer Gruppe 
     * wobei die Werte stets normiert (mit Rq) sind.
     * 
     */
    public void add( ReturnIntervallStatistik ris ) throws Exception {
        if ( ris.data.length != data.length ) throw new Exception( "[RIS] Unterschiedliches Binning !!!");
        
        if ( debug ) System.out.println("added single RIS to collection ... R_q="+ris.Rq );
        
        // klassifizierte Werte in das vorhandene Array übernehmen ...
        for ( int i = 0; i < data.length ; i++ ) {
            this.data[i] = this.data[i] + ris.data[i];
        }
        
        // anzahl hochrechnen
        this.zElements = this.zElements + ris.zElements;
        
        System.out.println( ris.dataRaw.size() + " Werte kommen an ...");
        
        dataRaw.addAll(ris.dataRaw);
        
        addDistData(dataRaw);
    }

    /** 
     * Vector<Long> times ==> Zeitpunkte, in geordneter Reihenfolge.
     *                        keine y-Werte !!! Nur Folge von t ....
     *  
     * Ein Vector mit "Zeiten" wird übergeben. 
     * Daraus sind zunächst die "Wiederkehrzeiten" 
     * zu bestimmen.
     **/
    public void addData( Vector<Long> times ) {
        Enumeration<Long> en = times.elements();
        Long vor = en.nextElement();
        dataRaw = new Vector<Double>();

        while( en.hasMoreElements() ) {

            Long now = en.nextElement();
            double dist = (double)(now - vor);
            vor = now;

            dataRaw.add(dist);

        }
        addDistData(dataRaw);
    }

    /** 
     * Ein Vector mit "Wiederkehrzeiten" wird hinzugefügt.
     * 
     * Es erfolgt die Einsortierung in das Binning, und eine 
     * sofortige Normierung mit Rq.
     * 
     * Zuvor wird das vorhandene Array geleert.
     * 
     **/
    public void addDistData( Vector dists ) {
        
        dataRaw = dists;
        
        System.out.println( dists.size() + " Werte einfügen ...");
        
        clear();
        
        double sum = 0.0;
        int ii =0;
        
        // Rq für die neuen Daten ermitteln ...
        Enumeration enUM = dists.elements();
        while( enUM.hasMoreElements() ) {
            double v = (Double) enUM.nextElement();
             // System.out.println( v);
             sum = sum + v;
             ii++;
        }
        
        Rq = sum/(1.0*ii);
        zElements = ii;
        
        if ( debug ) System.out.println("size=" + dists.size() + "; R_q=" + Rq );

        if ( !doScale_by_Rq ) {
            System.out.println(">>> no scaling by Rq in RIS-Tool.");
            Rq = 1.0;
        }

        Enumeration en = dists.elements();
        while( en.hasMoreElements() ) {
             double dist1 = (Double) en.nextElement();
             double v = ( (dist1 / (double)Rq) * ((double)scale  ) );
             int index = (int)v;
             if ( index >= data.length ) index = data.length-1;
             data[index]++;  // hochzählen ...
        }
        
        // NUN sind die Werte der Messreihe mit Rq skaliert in das 
        // Array abgelegt worde.
    }
    
    double epsilon = 1e-6;
//    public boolean scaleY_LN = false;
//    public boolean scaleY_LOG = false;
    
    public void calcMessreihen() throws Exception {

        double summe = 0.0;

        for (int i = 0; i < data.length; i++) {

            boolean useTheValue = true;

            double relAnzahl = 0.0;
            double anzahlProSpalte = 0.0;
            
            // erste Werte weglassen ....
            if ( i >= offset ) { 
                anzahlProSpalte = data[i];
                relAnzahl = (double) data[i] / (double) this.zElements;
            }    

//            if ( scaleY_LN || scaleY_LOG ) {
//                // mrVerteilung.addValuePair((double)i / (double)scale, yNORM);
//                if ( yNORM < epsilon ) {
//                    yNORM = 0.0;
//                    useTheValue = false;
//                }
//                else {
//                    if ( scaleY_LN ) yNORM = Math.log(yNORM);
//                    if (scaleY_LOG ) yNORM = Math.log10(yNORM);
//                }
//            }

            // wir lassen die unterdrückten Werte hier weg, damit diese nicht 
            // den Graphen zerstören...
            
            if ( useTheValue ) {
                mrHaeufigkeit.addValuePair((double)i / (double)scale,  anzahlProSpalte  );
                mrVerteilung.addValuePair((double)i / (double)scale, relAnzahl * Rq);
            }

            summe = summe + relAnzahl;
        }
        // if ( summe-1.0 > epsilon ) throw new Exception("Normierte Summe zu ungenau !!! \n(epsilon="+epsilon+", summe="+summe+")");
        System.out.println("(a) SUMME=" + summe + " : " + this.zElements);
        System.out.println("(b) VERLUST=" + (1-summe) + " durch ofset=" + this.offset);
        System.out.println("(c) Rq=" + Rq);
        
    }

    @Override
    public String toString() {
        //return toString_Haeufigkeit();
        return toString_Verteilung();
    }

    public String toString_Haeufigkeit() {

        StringBuffer sb = new StringBuffer();
        sb.append("\n#---------------------\n# " +
                 label + "\n#---------------------\n");
        sb.append("# Rq            =" + Rq + "\n");
        sb.append("# Anzahl-Werte  =" + zElements + "\n");
        sb.append("# Binning       =" + binning + "\n" );
        sb.append("# [ --- Häufigkeit --- ] \n" );
        mrHaeufigkeit.setLabel(sb.toString());
        return mrHaeufigkeit.toString();
    }


    public String toString_Verteilung() {

        StringBuffer sb = new StringBuffer();
        sb.append("\n#---------------------\n# " +
                 label + "\n#---------------------\n");
        sb.append("# Rq            =" + Rq + "\n");
        sb.append("# Anzahl-Werte  =" + zElements + "\n");
        sb.append("# Binning       =" +binning + "\n" );
        sb.append("# Scale         =" +scale + "\n" );
        sb.append("# [ --- Verteilung --- ] \n" );
        mrVerteilung.setLabel(sb.toString());
        return mrVerteilung.toString();
    }


    public void store() throws IOException {
            FileWriter fw = new FileWriter( folder + label );
            fw.write( mrVerteilung.toString() );
            fw.flush();
            fw.close();
            
            if ( verbose ) { 
                System.out.println("\n>>> RIS-Data:\n" + mrVerteilung.toString() );
            }
    };

    public void setRQ(double vRQ) {
        if ( doScale_by_Rq ) Rq = vRQ;
        else Rq = 1.0;
    }

    private void calcRq(int offset) {
        int zahl = 0;
        double summe = 0.0;
        for ( int i = offset; i < data.length ; i++ ) {
            summe = summe + data[i];
            zahl=zahl+1;
        }
        setRQ( summe / zahl );
    }
    
    
    
    public static void main( String[] args ) throws Exception {
        
        Vector<Double> data1 = new Vector<Double>();
        Vector<Double> data2 = new Vector<Double>();
        Vector<Double> data3 = new Vector<Double>();

        int _binning = 5;
        int _scale = 10;
        int _ofset = 0;
        
        int n1 = 2;
        int n2 = 3;
        
        data1.add( 1.0 );
        data1.add( 1.0 );            
        data1.add( 3.0 );
        data1.add( 3.0 );            
        data1.add( 5.0 );
        data1.add( 5.0 );            

        data2.add( 10.0 );
        data2.add( 10.0 );            
        data2.add( 30.0 );
        data2.add( 30.0 );            
        data2.add( 50.0 );
        data2.add( 50.0 );            
        
        data3.add( 1.0 );
        data3.add( 1.0 );            
        data3.add( 3.0 );
        data3.add( 3.0 );            
        data3.add( 5.0 );
        data3.add( 5.0 );            
        data3.add( 1.0 );
        data3.add( 3.0 );            
        data3.add( 5.0 );


//        for( int i = 1; i <= 25; i++  ) {            
//            
//            double z = new Double( i );
//            double z2 = new Double( 1 );
//            
//            data1.add( z );
//            data1.add( z2 );            
//        
//            double zz = new Double(i+5);
//            double zz2 = new Double(1);
//            
//            data2.add( zz );
//            data2.add( zz2 );            
//        
//        }    
 
        ReturnIntervallStatistik ris1 = new ReturnIntervallStatistik(_binning, _scale);
        ris1.doScale_by_Rq = true;
        ris1.offset = _ofset;
        
        ReturnIntervallStatistik ris2 = new ReturnIntervallStatistik(_binning, _scale);
        ris2.doScale_by_Rq = true;
        ris2.offset = _ofset;

        ReturnIntervallStatistik ris3 = new ReturnIntervallStatistik(_binning, _scale);
        ris3.doScale_by_Rq = true;
        ris3.offset = _ofset;

        // die Distancen sind nun hinzugefügt ....
        ris1.addDistData(data1);
        ris1.calcMessreihen();
        
        ris2.addDistData(data2);
        ris2.calcMessreihen();

        ris3.addDistData(data3);
        ris3.calcMessreihen();

//        try {
//            ris1.calcMessreihen();
//        }
//        catch (Exception ex) {
//            Logger.getLogger(ReturnIntervallStatistik.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
//        System.out.print(ris1.toString_Haeufigkeit());
//        System.out.print(ris1.toString_Verteilung());
        
        ris1.add(ris2);
        ris1.calcMessreihen();
        
        ris3.add(ris2);
        ris3.calcMessreihen();
        
        Vector<Messreihe> mrv = new Vector<Messreihe>();
        
        mrv.add( ris1.mrVerteilung );
        mrv.add( ris2.mrVerteilung );
        mrv.add( ris3.mrVerteilung );
        
        System.out.print(ris1.toString_Haeufigkeit());
        System.out.print(ris3.toString_Haeufigkeit());
//        System.out.print(ris1.toString_Verteilung());
        
        
    };

    private void calcBinning() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void clear() {
        initArray();
    }

    private void initArray() {
        data = new int[ ( binning * scale ) + 1 ];
    }

}
