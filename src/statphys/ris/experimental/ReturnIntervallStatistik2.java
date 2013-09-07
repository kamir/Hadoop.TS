package statphys.ris.experimental;

/**
 * Berechnung der Verteilung der Wiederkehr-Intervalle
 * 
 * - Normierung mit Rq erfolgt mit Aufruf der Methode calcMessreihen() und
 *   sobald die Instanz zu einer ContainerInstanz hinzugefügt wird 
 * - Zusammenfassung verschiedener Auswertungen mehrerer Zeit-(Intervall)-
 *   Reihen in einer ContainerInstanz
 * - Histogramm von Rq-Werten ausgeben wenn es eine ContainerInstanz ist
 * - Anzeige der Daten für einzelne Zeit-Reihen
 */

import chart.simple.MultiChart;
import data.series.MRT;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import data.export.OriginProject;
import hadoopts.core.TSData;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import panels.MessreiheWindow3Frame;

import statistics.HaeufigkeitsZaehler;
import statistics.HaeufigkeitsZaehlerDouble;
import tstool.random.LongTermCorrelationSeriesGenerator;


/**
 * 
 * Die Zusammenfassung von einzelnen Return-Intervall-Statistik Resultaten
 * kann nur auf Reihen mit gleichem Rq angewendet werden.
 * 
 * Ist Rq sehr verschieden, dann wird mit Rq skaliert
 * 
 * Die skalierten Werte können zusammegefasst werden.
 * 
 * add( RIS2 )                  => sammeln im Container
 * addData( Vector<Long> ...  ) => Zeitpunkte
 * addDistData( Vector<Double)  => Zeitintervalle sammeln
 * 
 * 
 * 
 * @author Mirko Kämpf
 */

public class ReturnIntervallStatistik2 {
    
    static public int[] grenzen = { 1,8,16,32,64,128,256,512,1024 };
    
    public static HaeufigkeitsZaehlerDouble debug1[] = null;
    
    /** 
     * wird beim setzen von Daten umgeschaltet und zum Konsistenz-Check 
     * benutzt.
     */
    public boolean isContainerInstanz = false;

    
    public String label;
    public String name;
    public String folder = null;
    
    /*
     * einfach nur gezählte Werte
     */
    public Messreihe mrHaeufigkeit = new Messreihe();
    /*
     * normierte Häufigkeiten, somit ergibt die summe aller Werte 1
     */
    public Messreihe mrVerteilung = new Messreihe();
    public Messreihe mrVerteilungSkaliert = new Messreihe();

    public static int binning = 400;   // anzahl der Werte pro Intervall
    public static int scale = 8;    // größter Wert
    
//    // DEFAULT, wird nicht im Konstruktor definiert!!!
//    private static int binningSKALIERT = 8;   // anzahl der Werte pro Intervall
//    private static int scaleSKALIERT = 5;    // größter Wert

    private double _Rq = 0.0;
    private int zElements = 0;
    
    /* 
     * die "gebinnten" Werte, also ein Feld von Zählern 
     */
    int data[] = null;
    int dataSKALIERT[] = null;
    double probSKALIERT[] = null;
    
    Vector<Double> dataRawFILTERED = null;
    Vector<Double> dataRaw_InterEventTIMES = null;
    
    static public boolean debug = false;
    static public boolean verbose = false;
    
    // ab welchem Wert für r soll es losgehen?
//    public static final double offset = 24*1;
    public static double offset = 0;

    public ReturnIntervallStatistik2(String l, int bin, int s) {
        binning = bin;
        scale = s;
        name = l;
        initArray();
        label = this.getClass().getName();
        dataRawFILTERED = new Vector<Double>();
        dataRaw_InterEventTIMES = new Vector<Double>();
        
        ZZZ = bin * s;
    }

    public ReturnIntervallStatistik2(int binning, int scale) {
        this( "unnamed" , binning, scale );
    }
    
    public ReturnIntervallStatistik2(String name) {
        this( name , ReturnIntervallStatistik2.binning, ReturnIntervallStatistik2.scale );
    }
 
    private void clear() {
        initArray();
    }

    private void initArray() {
        
        data = new int[ ( binning ) + 1 ];
        
        dataSKALIERT = new int[ ( binning ) + 1 ];
        probSKALIERT = new double[ (binning ) + 1 ];
        for( int i = 0; i < dataSKALIERT.length ; i++ ) { 
            dataSKALIERT[i] = 0;
        }
        for( int i = 0; i < probSKALIERT.length ; i++ ) { 
            probSKALIERT[i] = 0.0;
        }
    }
    
    private double calcAverageOfDist(Vector<Double> rr1) {
        double sum = 0.0;
        double c = 0.0;

        double last = 0;
        double delta = 0;
        
        for( double l : rr1 ) { 
            delta = l - last;
            sum = sum + delta;
            last = l;
            c++;
        }
        return sum / (1.0*c);
    }
    

    /** 
     *
     * Aufsammeln von mehreren RIS aus vielen Zeitreihen einer Gruppe 
     * wobei die Werte noch nicht normiert (mit Rq) sind.
     * 
     */
    Vector<Double> allRqs = new Vector<Double>();
    Messreihe test = new Messreihe();
    
    public void add( ReturnIntervallStatistik2 ris ) throws Exception {
//        if (!_checkRIS( ris ) ) { 
//            throw new Exception( "[RIS] ist ungültig!");
//        }
        
        if ( !this.isContainerInstanz ) {
            throw new Exception( "[RIS] Keine ContainerInstanz!");
        }
        
        if ( ris.data.length != data.length ) {
            throw new Exception( "[RIS] Unterschiedliches Binning !!!");
        }
        
        if ( debug ) {
            System.out.println("--> added single RIS to collect. R_q="+ris._Rq );
            // ris.showData();
        }
        
        if ( ris.dataRawFILTERED.size() < 1 ) {
            if ( debug ) {
                System.out.println(">>> " + ris.label + " ist leer ..." + ris._Rq );
            }        
            return;
        } 
        
        // _addData( ris._dataRawFILTERED, ris._Rq );

        int c = 0;
        for( int i = 0; i < probSKALIERT.length; i++ ) { 
            
            double prob = ris.probSKALIERT[i];
            
            if ( Double.isNaN( prob ) || Double.isInfinite( prob ) ) {
                System.err.println("ERROR 3:");
                System.err.println( ris.label + "\ti: (" + probSKALIERT.length + ") "+ i + "\t" + prob + " \t" + ris.dataSKALIERT[i] + " #=" + ris.dataRawFILTERED.size() );
            }
            
            probSKALIERT[i] = probSKALIERT[i] + ris.probSKALIERT[i];
            dataSKALIERT[i] = dataSKALIERT[i] + 1;
            c++;
        }
        
        allRqs.add(ris._Rq);
        
        if ( debug ) {
            System.out.println( "~~~" + ris.dataRawFILTERED.size() + " Werte kommen an ... (c=" + c +") " + probSKALIERT.length);
        }
        
    }

    /** 
     * Vector<Long> times ==> Zeitpunkte, in geordneter Reihenfolge.
     *                        keine y-Werte !!! Nur Folge von t ....
     *  
     * Ein Vector mit "Zeiten" wird übergeben. 
     * 
     * Daraus sind zunächst die "Wiederkehrzeiten" 
     * zu bestimmen.
     **/
    public void _addData( Vector<Double> times, double Rq ) throws Exception {

//        if ( this.isContainerInstanz ) {
//            throw new Exception("[RIS] ist eine Container-Instanz!");
//        }

        if ( debug ) {
            System.out.println( 
                    ">>> berechne n=" + (times.size()-1) + " Intervalle." );
        }

        
        // Container neu erstellen ...
        // dataRaw_InterEventTIMES = new Vector<Double>();

        int nullDistCounter = 0;
        
        // Zeitpunkte 
        Enumeration<Double> en = times.elements();
        
        // erstes Element (Index: 0)
        double vor = en.nextElement(); 
        
        while( en.hasMoreElements() ) {
            Double now = en.nextElement();
            Double dist = now - vor;
            //dataRaw_InterEventTIMES.add( Math.abs( dist ) );
            vor = now;
        } 

        addDistData(dataRaw_InterEventTIMES);
        
        // System.out.println( "#>>> nullDistCounter=" + nullDistCounter + ";");
    }
    
    public static int errosLENGTH = 0;
    
    
    /**
     * bleibt über den gesamten Lauf erhalten ...
     */
    public static HaeufigkeitsZaehler anzRowRAW = new HaeufigkeitsZaehler("length of rows (raw)");
    public static HaeufigkeitsZaehler anzRowsFILTERED = new HaeufigkeitsZaehler("length of rows (filtered, offset="+ReturnIntervallStatistik2.offset+"h)");

    /** 
     * Ein Vector mit "Wiederkehrzeiten" wird hinzugefügt.
     * 
     * Es erfolgt die Einsortierung in das Binning, und eine 
     * sofortige Normierung mit Rq.
     * 
     * Zuvor wird das vorhandene Array geleert.
     * 
     **/
    int cutOff = 0;
    public int anzFiltered = 0;
    public void addDistData( Vector dists ) throws Exception {
        if ( this.isContainerInstanz ) {
            throw new Exception("[RIS] ist eine Container-Instanz!");
        }
        
        if ( debug ) {
            System.out.println( 
                    ">>> n=" + dists.size() + " Intervalle einfügen ...");
        }
        
        long anzahlVor = (long)dists.size();
        anzRowRAW.addData( anzahlVor  );
        
        dataRawFILTERED = new Vector<Double>();
        Vector<Double> dataRawNOT_FILTERED = new Vector<Double>();
        
        Enumeration en1 = dists.elements();
        while( en1.hasMoreElements() ) {
            
            double da = (Double)en1.nextElement();
            dataRawNOT_FILTERED.add(da);
            
            
            if ( da >= offset && da != 0.0  ) {
                dataRawFILTERED.add( da );
            }
            else {
                System.out.println("  Filtered: =>  (offset="+offset+") " + da  );
                cutOff++;
            }
        }
        
        long anzahlNach = (long)dataRawFILTERED.size(); 
        
//        anzRowsFILTERED.addData( anzahlNach );
        
        long delta = (anzahlVor-anzahlNach);
        
        if ( delta > 0) {
            String line = "### FILTER : " + label + "\t" + anzahlVor + "\t" + anzahlNach + "\t" + delta + "\n";
            logLineToFile( logFileKey, line);
        }
        
        
        if ( debug ) {
            System.out.println( 
                    ">>> n_cutoff=" + cutOff + 
                    " Intervalle zu klein (offset:="+offset+")");
        }
   
//        if ( cutOff != 0 && offset == 0 ) throw new Exception( 
//                "SCHWEERER FEHLER" );
        
        anzFiltered = dataRawFILTERED.size();
        
        if ( dataRawFILTERED.size() >= 512 ) {
//            MessreiheWindow3Frame f = new MessreiheWindow3Frame();
//            f.show( dataRawTIMES , dataRawFILTERED, dataRawNOT_FILTERED );
//            System.out.println( "***>  " + dataRawFILTERED.size() );
        }
        
        clear();
        
        double sumRq_alt = 0.0;
        int anzRq_alt =0;
        
        // Rq_alt für Daten ermitteln ...
        Enumeration enUM_Rq_alt = dists.elements();
        while( enUM_Rq_alt.hasMoreElements() ) {
            double v = (Double) enUM_Rq_alt.nextElement();
             // System.out.println( v);
             sumRq_alt = sumRq_alt + v;
             anzRq_alt++;
        }
        double Rq_alt = sumRq_alt /(1.0*anzRq_alt);
        
        // Rq_neu für die gefilterten Daten ermitteln ...
        double sumRq_filteres = 0.0;
        int anzRq_filterd = 0;
        Enumeration enUM_Rq_filteres = dataRawFILTERED.elements();
        while( enUM_Rq_filteres.hasMoreElements() ) {
            double v = (Double) enUM_Rq_filteres.nextElement();
             // System.out.println( v);
             sumRq_filteres = sumRq_filteres + v;
             anzRq_filterd++;
        }
        double Rq_filtered = sumRq_filteres/(1.0*anzRq_filterd);
        zElements = anzRq_filterd;
       
        if ( debug ) System.out.println("### INFLUENCE of the filter:  R_q_alt=" + Rq_alt + "\tR_q_neu=" + Rq_filtered );
        _Rq = Rq_filtered;

        
        
        
        // SKALIERTEN WERT zaehlen ... hier wird mit Rq_neu normiert ... 
        // es werden die gefilterten Daten benutzt.
        Enumeration en = dataRawNOT_FILTERED.elements();
        if ( debug ) System.out.println("### process " + dataRawFILTERED.size() + " : " + dataRawFILTERED.subList(0, 10) );
        
        while( en.hasMoreElements() ) {
             
             double dist1 = (Double) en.nextElement();
             
             double v = dist1 / Rq_alt;
             
             v = v * ( (double)binning / (double)scale );
             
             int index = (int)v;
             
//             System.out.println( "v="+ v + "\tindex=" + index + "\t dist=" + dist1 + "\tRq_neu=" + Rq_filtered );
             if ( index >= dataSKALIERT.length ) index = dataSKALIERT.length-1;
             dataSKALIERT[index]++;  // hochzählen ...             
        }
        
        // Mittelwert der Wahrscheinlichkeiten berechnen ...
        for( int i = 0; i < probSKALIERT.length; i++ ) { 
            probSKALIERT[i] = (double)( dataSKALIERT[i] / (double)zElements );
        }              
    }
    
    double epsilon = 1e-6; 
    
    public static void logLineToFile( String key, String line ) { 
//        try {
//            // projekt.logLine(key, line);
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(ReturnIntervallStatistik2.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public void calcMessreihen() throws Exception {
        if ( this.isContainerInstanz ) { 
            calcContainerMR();
            System.out.println( "*** calc rows in RIS ...");
        
        }
        else { 
            calcSingleMR();
        }
    }
    
    private void calcContainerMR() throws Exception {
        
        mrVerteilungSkaliert = new Messreihe( name + " (res=" + ((double)scale/(double)binning) );
        
        
        for (int i = 0; i < dataSKALIERT.length; i++) {
            if ( dataSKALIERT[i] != 0 ) {
                double vx = ((double)i + 0.5) * (double)scale / (double)binning;
                double vy = probSKALIERT[i] / (double)dataSKALIERT[i];
                try {
                    if ( vy != 0.0 ) {
                        double logVy = vy; // Math.log(vy);
                        if ( Double.isInfinite( logVy ) || Double.isNaN( logVy ) ) {
                            System.err.println("ERROR 2:" + vx + "\t" + vy + "\t" );
                            
                        }
                        else {
                            mrVerteilungSkaliert.addValuePair( vx, logVy );
                        }
                    }    
                    
                }
                catch(Exception ex) {
                    System.err.println("ERROR 1:" + vx + "\t" + vy + "\t" );
                    
                }
            }
        }
    }
    
    
    void calcSingleMR() throws Exception {

        double summe = 0.0;
        
        // für jedes BIN i
        for (int i = 0; i < data.length; i++) {

            double relAnzahl = 0.0;
            double anzahlProSpalte = 0.0;
            
               
            anzahlProSpalte = data[i];
            relAnzahl = (double) data[i] / (double) this.zElements;
                
            mrHaeufigkeit.addValuePair(
                        (double)i / (double)scale,  anzahlProSpalte  );
                
            mrVerteilung.addValuePair(
                        (double)i / (double)scale , relAnzahl );
                
            mrVerteilungSkaliert.addValuePair(
                        (double)i  / ( (double)scale * _Rq), relAnzahl * _Rq);
                
                // System.out.println( i+ "\t"+anzahlProSpalte + "\t" + 
                // Rq+ "\t" + relAnzahl );
                
            summe = summe + relAnzahl;
           
            
        // erste Werte weglassen ....
        if ( i >= (offset*scale / _Rq) ) { 
                
          anzahlProSpalte = dataSKALIERT[i];
                                
          relAnzahl = dataSKALIERT[i] / this.zElements;
 
          mrVerteilungSkaliert.addValuePair( i / ( scale ), relAnzahl);
        }
        }
        // mrVerteilungSkaliert = mrVerteilungSkaliert.scaleY_2(1.0/summe);
        // if ( summe-1.0 > epsilon ) 
        //   throw new Exception(
        //     "Norm. Sum wrong! \n(epsilon="+epsilon+", summe="+summe+")");
        if ( debug ) {
            System.out.println(
               "(a) Anzahl Elemente=" + this.zElements);
            System.out.println(
               "(b) Rq=" + _Rq + "\t summe=" + summe );    
        }
    }
    
    public void showData() throws Exception {
        
        v1 = new Vector<Messreihe>();
        v2 = new Vector<Messreihe>();

        calcMessreihen();
        
        showRawData();      
        
        showVerteilungen();
        
        MultiChart.open(v1,true, name + " distribution");
        
        MultiChart.open(v2,true, 
                this.name + " (RawData)");
    }
    
    Vector<Messreihe> v1 = new Vector<Messreihe>();
    Vector<Messreihe> v2 = new Vector<Messreihe>();
    
    private void showVerteilungen() {
        // Verteilungen
        if ( !this.isContainerInstanz ) v1.add( this.mrVerteilung);
        v1.add( this.mrVerteilungSkaliert);        
    }


    private void showRawData() {
        // Rohdaten (Intervalle)
        Messreihe mr = new Messreihe( this.name + " (rawdata) " + dataRawFILTERED.size() );
        for( double y: this.dataRawFILTERED ) {
            mr.addValue(y);
        }
        v2.add(mr);
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
        sb.append("# Rq            =" + _Rq + "\n");
        sb.append("# Anzahl-Werte  =" + zElements + "\n");
        sb.append("# Binning       =" + binning + "\n" );
        sb.append("# [ --- Häufigkeit --- ]" );
        mrHaeufigkeit.setLabel(sb.toString());
        return mrHaeufigkeit.toString();
    }


    public String toString_Verteilung() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n#---------------------\n# " +
                 label + "\n#---------------------\n");
        sb.append("# Rq            =" + _Rq + "\n");
        sb.append("# Anzahl-Werte  =" + zElements + "\n");
        sb.append("# Binning       =" +binning + "\n" );
        sb.append("# Scale         =" +scale + "\n" );
        sb.append("# [ --- Verteilung --- ]" );
        mrVerteilung.setLabel(sb.toString());
        return mrVerteilung.toString();
    }


    public void store() throws IOException {
        FileWriter fw = new FileWriter( folder + label );
        fw.write( mrVerteilung.toString() );
        fw.flush();
        fw.close();

        if ( verbose ) { 
            System.out.println(
               "\n>>> RIS-Data:\n" + mrVerteilung.toString() );
        }
    };

    public void setRQ(double vRQ) {
        _Rq = vRQ;
    }
    
    
    public double getRq() {
        return _Rq;
    }

    /**
     * auf dem rawDataVector rechnen ...
     * @param offset 
//     */
//    int zahl = 0;
//    int verlust = 0;
//    private void calcRq(double offset) throws Exception {
//        if ( this.isContainerInstanz ) {
//            throw new Exception( "[RIS] R_q ist für Containerinstanz" + 
//                                 " nicht berechnbar!" );
//        }
//        zahl = 0;
//        verlust = 0;
//        double summe = 0.0;
//        Enumeration<Double> en = this._dataRawFILTERED.elements();
//        while( en.hasMoreElements() ) {
//            Double v = en.nextElement();
//            if ( v > offset ) {
//                summe = summe + v;
//                zahl = zahl + 1;
//            }
//            else { 
//                verlust = verlust + 1;
//            }
//        }
//        setRQ( summe / zahl );
//    }
        
    
    private static boolean storeSingleDistributions = false;
    
    static FileWriter fw;
    
    static Vector<Messreihe> mrv2 = null;
    static Vector<Messreihe> mrv = null;

    static Vector<Messreihe> mrv2B = null;
    static Vector<Messreihe> mrvB = null;

    static int maxB = 5; // anzahl der Input-Files
    static int maxROWS = -1; // default -1
    
    public static String logFileNames[] = null;
    public static OriginProject projekt = null;
    
//    static int anzNullEdits[] = null;

    static HaeufigkeitsZaehlerDouble hzANZ = new HaeufigkeitsZaehlerDouble();
    static HaeufigkeitsZaehlerDouble[] mrRQ = null;
    static HaeufigkeitsZaehlerDouble[] mrANZ = null;
    
    public static void initRqCollectors() {
        
//        anzNullEdits = new int[maxB];
        
        ReturnIntervallStatistik2.debug1 = new HaeufigkeitsZaehlerDouble[grenzen.length];
                
        mrRQ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        mrANZ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        
        for( int i = 0; i < grenzen.length; i++ ) { 
            mrRQ[i] = new HaeufigkeitsZaehlerDouble(); // "R_q " + labels[i] );
            mrRQ[i].intervalle = binning;
            mrRQ[i].max = binning;

            mrANZ[i] = new HaeufigkeitsZaehlerDouble();
            mrANZ[i].intervalle = binning;
            mrANZ[i].max = binning;
            
            ReturnIntervallStatistik2.debug1[i] = new HaeufigkeitsZaehlerDouble();
            ReturnIntervallStatistik2.debug1[i].label = "bis="+grenzen[i];
        }
    }
    
    
    
    public static void main( String[] args ) throws Exception {
        
        stdlib.StdRandom.initRandomGen(1);
        
        binning = 200;
        scale = 4;   
        
//        initRqMIN();
        initRqCollectors();
        
        mrv = new Vector<Messreihe>();
        mrv2 = new Vector<Messreihe>();
        
        mrvB = new Vector<Messreihe>();
        mrv2B = new Vector<Messreihe>();
        debug = true;
        
        double min;
        // V1
        Messreihe mr1 = Messreihe.getGaussianDistribution( (int)Math.pow(2, 20), 5.0, 0.5);
        addRow( mr1 );

        mr1 = Messreihe.getGaussianDistribution( (int)Math.pow(2, 22), 5.0, 4.5);
        min = mr1.getMinY();
        mr1.add_value_to_Y( -1.0 * min);
        addRow( mr1 ); 
        
        
        // V1
        Messreihe mrx = Messreihe.getUniformDistribution( (int)Math.pow(2, 20), 0.0, 5.0);
        addRow( mrx );
        
//        int ex = 20;
//        for( int i = 0; i < 12; i++ ) {
//            // V2
//            double beta = 0.1 + ( (double)i / 5.0 );
//            if ( i > 6 ) ex = 22;
//            MessreiheFFT mr2 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, ex), beta, false, false);
//            min = mr2.getMinY();
//            mr2.add_value_to_Y( -1.0 * min);
//            addRow( mr2 );
//        }  
        // V3
        MessreiheFFT mr3 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, 20), 1.4, false, false);
        min = mr3.getMinY();
        System.out.println( min );
        mr3.add_value_to_Y( -1.0 * min);
        min = mr3.getMinY();
        System.out.println( min );
        addRow( mr3 );
        
        // V3
        mr3 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, 20), 1.2, false, false);
        min = mr3.getMinY();
        System.out.println( min );
        mr3.add_value_to_Y( -1.0 * min);
        min = mr3.getMinY();
        System.out.println( min );
        
        addRow( mr3 );
        
        
        MultiChart.open(mrv, true);
        MultiChart.open(mrv2, true);

        MultiChart.open(mrvB, true);
        MultiChart.open(mrv2B, true);
    }
    
    /**
     * 
     * @param m
     * @throws Exception 
     */     
    public static void addRowB( Messreihe m ) throws Exception {
        
        Vector<Double> data1 = m.yValues;
        
        ReturnIntervallStatistik2 ris1 = new ReturnIntervallStatistik2( 
               m.getLabel() , binning, scale);

//        double min = m.getMinY();
//        m.add_value_to_Y(-1.0 * min);
//        
//        m.normalize(); 
//        m.scaleY_2(1000);
          
        Vector<Double> p = MRT.getPeaksDaysOverTSdouble( 0.5 , m );
 
        
        
        ris1.addDistData(p);

        ris1.calcMessreihen(); 

        ReturnIntervallStatistik2 ris4 = new ReturnIntervallStatistik2( 
                m.getLabel(), binning, scale);
        ris4.isContainerInstanz = true;
        ris4.add(ris1);
        ris4.calcMessreihen();
        

        mrvB.add( ris4.mrVerteilungSkaliert ); 
        mrv2B.add( ris4.mrHaeufigkeit ); 
        
    }
        
    public static void addRow( Messreihe m ) throws Exception {
        
        addRowB(m);
        
        Vector<Double> data1 = m.yValues;
        System.out.println( m.yValues.size() );
        
        ReturnIntervallStatistik2 ris1 = new ReturnIntervallStatistik2( 
               m.getLabel() , binning, scale);
                       
        
        // die Distancen sind nun hinzugefügt ....
        // data1 = ReturnIntervallStatistik2.initTestData( 100, 10, 3);
        double rq = ris1.calcAverageOfDist(data1);
        System.out.println( "RQ : " + rq );
        
        ris1.addDistData(data1);
        ris1.calcMessreihen();
//        ris1.showData();

        ReturnIntervallStatistik2 ris4 = new ReturnIntervallStatistik2( 
                m.getLabel(), binning, scale);
        ris4.isContainerInstanz = true;
        ris4.add(ris1);
        ris4.calcMessreihen();
        

        mrv.add( ris4.mrVerteilungSkaliert ); 
        mrv2.add( ris4.mrHaeufigkeit ); 
        
    }

    static public Vector<Double> initTestData( int mi, int sj, int loops) { 
        Vector<Double> dv = new Vector<Double>();
        for( int l = 0; l < loops; l++ ) {
            for( int i = 1; i < mi; i++ ) {
                for( int j = 1; j < (mi- i * sj) ; j++ ) { 
                    dv.add( i * 1.0 );   
                }
            }
        }
        for( int l = 0; l < 500; l++ ) {
            dv.add( 1.0 );
        }
        return dv;
    };

    String logFileKey = null;
    public void setLofFileNameKey_1(String string) {
        logFileKey = string;
    }
    
    public static int ZZZ = 0;
        
//    public static double rqMIN[] = null;
    
//    public static void initRqMIN() { 
//        
//        rqMIN = new double[grenzen.length];
//        
//        for( int i = 1; i < grenzen.length; i++ ) {
//             double Rq_max = 1.0/ (double) grenzen[i-1];
//             
//             rqMIN[i] = ReturnIntervallStatistik2.offset / Rq_max;
//             
//             System.out.println( grenzen[i-1] + " ... " + grenzen[i] + "\tRq_max=" + Rq_max + "\t" + rqMIN[i] );
//        }
////        javax.swing.JOptionPane.showMessageDialog(null, "go");
//    }    

    
//    private static boolean _checkRIS(ReturnIntervallStatistik2 ris) throws IOException {
//        boolean b = true;
//        
//        if ( ris._dataRawFILTERED == null ) return false;
//        
//        if ( ris == null ) return false;
//        
//        for ( int i = 1; i < grenzen.length ; i++ ) {
//            if ( ris.anzFiltered <= grenzen[i] && ris.anzFiltered > grenzen[i-1] ) { 
//
//                // ist die Summe aller r größer als die gesamte Zeitreihe?
//                double sum = 0.0;
//                for( double v3 : ris._dataRawFILTERED ) {
//                    sum = sum + v3;
//                }
//                
//                
//                for( double v : ris._dataRawFILTERED ) {
//               
//                                  
//                    String line = "\t\tr=" + v + "\tRq=" + ris._Rq + "\tr/Rq=(" + (v/ris._Rq) + ")\t rq_min=" + rqMIN[i] + "\t summe(r_i)=" + sum;
//                    // String line = "\t\tr=" + v + "\tRq=" + ris._Rq + "\tr/Rq=(" + (v/ris._Rq) + ")\t summe(r_i)=" + sum;
// 
//                    if ( ( v / ris._Rq ) < rqMIN[i] ) {
//                        
//                       
//                        
//                        // javax.swing.JOptionPane.showMessageDialog(null,"r=" + v + "\nRq=" + ris.Rq + "\n" + (v/ris.Rq) +"\n"+ Topic2.rqMIN[i]  );
//                        System.out.println( line  );
//                        debug1[i].addData( v/ris._Rq );
//                        line = "* " + line;
//                    }
//                    
//                    projekt.logLine( logFileNames[i-1] , line +"\n");
//
//                }
//                
//                if ( sum > 300*24 ) {
//                    System.out.println( "SUMME >>> 300*24 " + sum );
//                }
//                double RQMAX = 300.0 * 24.0 / (1.0 * grenzen[i-1] );
//                if( ris._Rq > RQMAX ) {
//                    projekt.logLine(  "RzuGROS" , ris._Rq + " " + ris.label );
//                }
//            }
//        }     
//        return b;
//    }


  
}
