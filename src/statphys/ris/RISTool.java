package statphys.ris;

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

import chart.panels.MessreiheWindow3Frame;
import chart.simple.MultiChart;
import data.series.Messreihe;
import data.export.OriginProject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger; 

import statistics.HaeufigkeitsZaehler;
import statistics.HaeufigkeitsZaehlerDouble;


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

public class RISTool {
    
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

    private int binning = 300;   // anzahl der Werte pro Intervall
    private int scale = 24;    // größter Wert
    
    // DEFAULT, wird nicht im Konstruktor definiert!!!
    private static int binningSKALIERT = 8;   // anzahl der Werte pro Intervall
    private static int scaleSKALIERT = 5;    // größter Wert

    private double _Rq = 0.0;
    private int zElements = 0;

    /* 
     * die "gebinnten" Werte, also ein Feld von Zählern 
     */
    int data[] = null;
    int dataSKALIERT[] = null;
    double probSKALIERT[] = null;
    
    Vector<Double> dataRawFILTERED = null;
    Vector<Double> dataRawTIMES = null;
    
    static public boolean debug = false;
    static public boolean verbose = false;
    
    // ab welchem Wert für r soll es losgehen?
//    public static final double offset = 24*1;
    public static double offset = 0;

    public RISTool(String l, int bin, int s) {
        binning = bin;
        scale = s;
        name = l;
        initArray();
        label = this.getClass().getName();
        dataRawFILTERED = new Vector<Double>();
        dataRawTIMES = new Vector<Double>();
    }

    RISTool(int binning, int scale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    private void clear() {
        initArray();
    }

    private void initArray() {
        
        data = new int[ ( binning * scale ) + 1 ];
        
        dataSKALIERT = new int[ ( binningSKALIERT * scaleSKALIERT ) + 1 ];
        probSKALIERT = new double[ ( binningSKALIERT * scaleSKALIERT ) + 1 ];
        for( int i = 0; i < dataSKALIERT.length ; i++ ) { 
            dataSKALIERT[i] = 0;
        }
        for( int i = 0; i < probSKALIERT.length ; i++ ) { 
            probSKALIERT[i] = 0.0;
        }
    }

    /** 
     *
     * Aufsammeln von mehreren RIS aus vielen Zeitreihen einer Gruppe 
     * wobei die Werte noch nicht normiert (mit Rq) sind.
     * 
     */
    Vector<Double> allRqs = new Vector<Double>();
    Messreihe test = new Messreihe();
    public void add( RISTool ris ) throws Exception {
        if (!_checkRIS( ris ) ) { 
            throw new Exception( "[RIS] ist ungültig!");
        }
        
        if ( !this.isContainerInstanz ) {
            throw new Exception( "[RIS] Keine ContainerInstanz!");
        }
        
        if ( ris.data.length != data.length ) {
            throw new Exception( "[RIS] Unterschiedliches Binning !!!");
        }
        
        if ( debug ) {
            System.out.println("added single RIS to collect. R_q="+ris._Rq );
            // ris.showData();
        }
        
        if ( ris.dataRawFILTERED.size() < 1 ) {
            if ( debug ) {
                System.out.println(">>> " + ris.label + " ist leer ..." + ris._Rq );
            }        
            return;
        } 

        for( int i = 0; i < probSKALIERT.length; i++ ) { 
            double prob = ris.probSKALIERT[i];
            
            if ( Double.isNaN( prob ) || Double.isInfinite( prob ) ) {
                System.err.println("ERROR 3:");
                System.err.println( ris.label + "\ti: (" + probSKALIERT.length + ") "+ i + "\t" + prob + " \t" + ris.dataSKALIERT[i] + " #=" + ris.dataRawFILTERED.size() );
            }
            
            probSKALIERT[i] = probSKALIERT[i] + ris.probSKALIERT[i];
            dataSKALIERT[i] = dataSKALIERT[i] + 1;
            
        }
        
        allRqs.add(ris._Rq);
        
        if ( debug ) {
            System.out.println( ris.dataRawFILTERED.size() + " Werte kommen an ...");
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
    public void _addData( Vector<Double> times ) throws Exception {

        if ( this.isContainerInstanz ) {
            throw new Exception("[RIS] ist eine Container-Instanz!");
        }

        if ( debug ) {
            System.out.println( 
                    ">>> berechne n=" + (times.size()-1) + " Intervalle." );
        }

        
        // Container neu erstellen ...
        dataRawTIMES = new Vector<Double>();

        int nullDistCounter = 0;
        
        // Zeitpunkte 
        Enumeration<Double> en = times.elements();
        
        // erstes Element (Index: 0)
        double vor = en.nextElement(); 
        
        while( en.hasMoreElements() ) {
            Double now = en.nextElement();
            double dist = (double)(now - vor);
            if ( dist > 0.0 ) {
                
                // INTERVALLE in Stunden ...
                dist = dist / (60 * 60 * 1000);
                dataRawTIMES.add(dist);
            }
            else {
                nullDistCounter++;
            }
            if (debug )System.out.println(dist + " \t now:=" + now + "\t vor:=" + vor);
            vor = now;
        } 

        if ( preCheck( dataRawTIMES ) ) {
            addDistData(dataRawTIMES);
        }
        else { 
            errosLENGTH++;            
        }
        
        
        // System.out.println( "#>>> nullDistCounter=" + nullDistCounter + ";");
    }
    
    public static int errosLENGTH = 0;
    
    
    /**
     * bleibt über den gesamten Lauf erhalten ...
     */
    public static HaeufigkeitsZaehler anzRowRAW = new HaeufigkeitsZaehler("length of rows (raw)");
    public static HaeufigkeitsZaehler anzRowsFILTERED = new HaeufigkeitsZaehler("length of rows (filtered, offset="+RISTool.offset+"h)");

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
                System.out.println("  Filtered: =>  " + da  );
                cutOff++;
            }
        }
        
        long anzahlNach = (long)dataRawFILTERED.size();
        anzRowsFILTERED.addData( anzahlNach );
        long delta = (anzahlVor-anzahlNach);
        
        if ( delta > 0) {
            String line = label + "\t" + anzahlVor + "\t" + anzahlNach + "\t" + delta + "\n";
            logLineToFile( logFileKey, line);
        }
        
        
        if ( debug ) {
            System.out.println( 
                    ">>> n_cutoff=" + cutOff + 
                    " Intervalle zu klein (offset:="+offset+")");
        }
   
        if ( cutOff != 0 && offset == 0 ) throw new Exception( 
                "SCHWEERER FEHLER" );
        
        anzFiltered = dataRawFILTERED.size();
        
        if ( dataRawFILTERED.size() >= 512 ) {
            MessreiheWindow3Frame f = new MessreiheWindow3Frame();
            f.show( dataRawTIMES , dataRawFILTERED, dataRawNOT_FILTERED );
            System.out.println( "***>  " + dataRawFILTERED.size() );
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
        
        // Rq_neu für die neuen Daten ermitteln ...
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
       
        if ( debug ) System.out.println(">>> R_q_alt=" + Rq_alt + "\tR_q_neu=" + Rq_filtered );
        _Rq = Rq_filtered;

        
        
        
        // SKALIERTEN WERT zaehlen ... hier wird mit Rq_neu normiert ... 
        // es werden die gefilterten Daten benutzt.
        Enumeration en = dataRawFILTERED.elements();
        while( en.hasMoreElements() ) {
             double dist1 = (Double) en.nextElement();
             double v = dist1  / (double)Rq_filtered;
             int index = (int)(v * (double)scaleSKALIERT);
             System.out.println( "v="+ v + "\tindex=" + index + "\t dist=" + dist1 + "\tRq_neu=" + Rq_filtered );
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
        try {
            projekt.logLine(key, line);
        } 
        catch (IOException ex) {
            Logger.getLogger(RISTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void calcMessreihen() throws Exception {
        if ( this.isContainerInstanz ) { 
            calcContainerMR();
        }
        else { 
            // calcSingleMR();
        }
    }
    
    private void calcContainerMR() throws Exception {
        
        mrVerteilungSkaliert = new Messreihe( "Vert_SK");
        
        for (int i = 0; i < dataSKALIERT.length; i++) {
            if ( dataSKALIERT[i] != 0 ) {
                double vx = ((double)i + 0.5) / scaleSKALIERT;
                double vy = probSKALIERT[i] / (double)dataSKALIERT[i];
                try {
                    if ( vy != 0.0 ) {
                        double logVy = Math.log(vy);
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
    
    
    void _calcSingleMR() throws Exception {

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
           
            
//        // erste Werte weglassen ....
//        if ( i >= (offset*scale / Rq) ) { 
//                
//          anzahlProSpalte = dataSKALIERT[i];
//                                
//          relAnzahl = dataSKALIERT[i] / this.zElements;
// 
//          mrVerteilungSkaliert.addValuePair( i / ( scale ), relAnzahl);
//        }
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
        calcMessreihen();
        v1 = new Vector<Messreihe>();
        showRawData();      
        showVerteilungen();
        MultiChart.open(v1,true, name);
        if ( !this.isContainerInstanz ) MultiChart.open(v2,true, 
                this.name + "(RawData)");
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
        Messreihe mr = new Messreihe( this.name + " (rawdata)");
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
     */
    int zahl = 0;
    int verlust = 0;
    private void calcRq(double offset) throws Exception {
        if ( this.isContainerInstanz ) {
            throw new Exception( "[RIS] R_q ist für Containerinstanz" + 
                                 " nicht berechnbar!" );
        }
        zahl = 0;
        verlust = 0;
        double summe = 0.0;
        Enumeration<Double> en = this.dataRawFILTERED.elements();
        while( en.hasMoreElements() ) {
            Double v = en.nextElement();
            if ( v > offset ) {
                summe = summe + v;
                zahl = zahl + 1;
            }
            else { 
                verlust = verlust + 1;
            }
        }
        setRQ( summe / zahl );
    }
        private static boolean storeSingleDistributions = false;
    
    static FileWriter fw;
    
    static Vector<Messreihe> mrv = null;
    
    static int risBinning = 300*24;  // Auflösung in Stunden
    static int risScale = 1;

    static int maxB = 5; // anzahl der Input-Files
    static int maxROWS = -1; // default -1
    
    public static String logFileNames[] = null;
    public static OriginProject projekt = null;
    
    static int anzNullEdits[] = null;

    static HaeufigkeitsZaehlerDouble[] mrRQ = null;
    static HaeufigkeitsZaehlerDouble hzANZ = new HaeufigkeitsZaehlerDouble();
    static HaeufigkeitsZaehlerDouble[] mrANZ = null;
    
    public static void initRqCollectors() {
        
        anzNullEdits = new int[maxB];
        RISTool.debug1 = new HaeufigkeitsZaehlerDouble[grenzen.length];
                
        mrRQ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        mrANZ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        for( int i = 0; i < grenzen.length; i++ ) { 
            mrRQ[i] = new HaeufigkeitsZaehlerDouble(); // "R_q " + labels[i] );
            mrRQ[i].intervalle = 300*24;
            mrRQ[i].max = 300*24;

            mrANZ[i] = new HaeufigkeitsZaehlerDouble();
            mrANZ[i].intervalle = 300*24;
            mrANZ[i].max = 300*24;
            
            RISTool.debug1[i] = new HaeufigkeitsZaehlerDouble();
            RISTool.debug1[i].label = "bis="+grenzen[i];
        }
    }
    
    public static void main( String[] args ) throws Exception {
        
        initRqMIN();
        
        initRqCollectors();
        
        debug = true;
        
        Vector<Double> data1 = new Vector<Double>();
        Vector<Double> data2 = new Vector<Double>();
        Vector<Double> data3 = new Vector<Double>();

        int _binning = 12;
        int _scale = 10;         
        
        int n1 = 2;
        int n2 = 3;
        
        data1.add( 1.0 );
        data1.add( 1.0 );            
        data1.add( 5.5 );
        data1.add( 5.5 );            
        data1.add( 5.0 );
        data1.add( 5.0 );            

        data2.add( 2.5 );
        data2.add( 2.5 );            
        data2.add( 5.5 );
        data2.add( 5.5 );            
        data2.add( 6.5 );
        data2.add( 6.5 );    
        data2.add( 6.5 );
        data2.add( 6.5 );   
        data2.add( 6.5 );
        data2.add( 6.5 );   
        
        data3.add( 10.0 );
        data3.add( 10.0 );  
        data3.add( 3.5 );
        data3.add( 3.5 );   
        data3.add( 3.5 );
        data3.add( 3.5 );   
        
        for ( int i = 0; i < 20; i++ ) { 
            data1.add( 3.0 );
            data2.add( 3.0 );
            data3.add( 3.0 );
        }

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
        
        RISTool ris1 = new RISTool( 
                "ris1", _binning, _scale);
               
        RISTool ris2 = new RISTool( 
                "ris2", _binning, _scale);
       
        RISTool ris3 = new RISTool( 
                "ris3", _binning, _scale);
       
        // die Distancen sind nun hinzugefügt ....
        data1 = RISTool.initTestData( 100, 10, 3);
        ris1.addDistData(data1);
        ris1.calcMessreihen();
        
        data2 = RISTool.initTestData( 100, 10, 10);
        ris2.addDistData(data2);
        ris2.calcMessreihen();

        data3 = RISTool.initTestData( 100, 10, 100);
        ris3.addDistData(data3);
        ris3.calcMessreihen();

        RISTool ris4 = new RISTool( 
                "ris4", _binning, _scale);
        ris4.isContainerInstanz = true;
        
        ris4.add(ris1);
        ris4.add(ris2);
        ris4.add(ris3);
        
        ris4.calcMessreihen();
        
        RISTool ris5 = new RISTool( 
                "ris5", _binning, _scale);
        ris5.isContainerInstanz = true;
        
        ris5.add(ris3);
        ris5.add(ris2);
        ris5.add(ris1);
        ris5.calcMessreihen();
        
//        System.out.print(ris1.toString_Haeufigkeit());
//        System.out.print(ris1.toString_Verteilung());
        
//        ris1.add(ris2);
//        ris1.calcMessreihen();
//        
//        ris3.add(ris2);
//        ris3.calcMessreihen();
        
        Vector<Messreihe> mrv = new Vector<Messreihe>();
        
        ris1.toString_Haeufigkeit();
        ris2.toString_Haeufigkeit();
        ris3.toString_Haeufigkeit();
        ris4.toString_Haeufigkeit();
        ris5.toString_Haeufigkeit();
        
        ris1.toString_Verteilung();
        ris2.toString_Verteilung();
        ris3.toString_Verteilung();
        ris4.toString_Verteilung();
        ris5.toString_Verteilung();
//        
//        
//        mrv.add( ris1.mrHaeufigkeit );
//        mrv.add( ris2.mrHaeufigkeit);
//        mrv.add( ris3.mrHaeufigkeit);
//        
//        mrv.add( ris4.mrHaeufigkeit);
//        mrv.add( ris5.mrHaeufigkeit);
        
//        mrv.add(ris1.mrVerteilungSkaliert);
//        mrv.add(ris2.mrVerteilungSkaliert);
//        mrv.add(ris3.mrVerteilungSkaliert);
        
        
//        System.out.print(ris1.toString_Haeufigkeit());
//        System.out.print(ris2.toString_Haeufigkeit());
//        System.out.print(ris3.toString_Haeufigkeit());
//        System.out.print(ris4.toString_Haeufigkeit());
//        System.out.print(ris5.toString_Haeufigkeit());
        
//        System.out.print(ris1.toString_Verteilung());
    
//        MultiChart.open(mrv, true);
        
        ris1.showData();
        ris2.showData();
        ris3.showData();
        ris4.showData();
        ris5.showData();
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
    
        
    public static double rqMIN[] = null;
    public static void initRqMIN() { 
       
        
        rqMIN = new double[grenzen.length];
        
        for( int i = 1; i < grenzen.length; i++ ) {
             double Rq_max = (double)( 300 * 24 )/ (double) grenzen[i-1];
             
             rqMIN[i] = RISTool.offset / Rq_max;
             
             System.out.println( grenzen[i-1] + " ... " + grenzen[i] + "\tRq_max=" + Rq_max + "\t" + rqMIN[i] );
        }
//        javax.swing.JOptionPane.showMessageDialog(null, "go");
    }    

    private static boolean _checkRIS(RISTool ris) throws IOException {
        boolean b = true;
        
        if ( ris.dataRawFILTERED == null ) return false;
        
        if ( ris == null ) return false;
        
        for ( int i = 1; i < grenzen.length ; i++ ) {
            if ( ris.anzFiltered <= grenzen[i] && ris.anzFiltered > grenzen[i-1] ) { 

                // ist die Summe aller r größer als die gesamte Zeitreihe?
                double sum = 0.0;
                for( double v3 : ris.dataRawFILTERED ) {
                    sum = sum + v3;
                }
                
                
                for( double v : ris.dataRawFILTERED ) {
               
                                  
                    String line = "\t\tr=" + v + "\tRq=" + ris._Rq + "\tr/Rq=(" + (v/ris._Rq) + ")\t rq_min=" + rqMIN[i] + "\t summe(r_i)=" + sum;
                    // String line = "\t\tr=" + v + "\tRq=" + ris._Rq + "\tr/Rq=(" + (v/ris._Rq) + ")\t summe(r_i)=" + sum;
 
                    if ( ( v / ris._Rq ) < rqMIN[i] ) {
                        
                       
                        
                        // javax.swing.JOptionPane.showMessageDialog(null,"r=" + v + "\nRq=" + ris.Rq + "\n" + (v/ris.Rq) +"\n"+ Topic2.rqMIN[i]  );
                        System.out.println( line  );
                        debug1[i].addData( v/ris._Rq );
                        line = "* " + line;
                    }
                    
                    projekt.logLine( logFileNames[i-1] , line +"\n");

                }
                
                if ( sum > 300*24 ) {
                    System.out.println( "SUMME >>> 300*24 " + sum );
                }
                double RQMAX = 300.0 * 24.0 / (1.0 * grenzen[i-1] );
                if( ris._Rq > RQMAX ) {
                    projekt.logLine(  "RzuGROS" , ris._Rq + " " + ris.label );
                }
            }
        }     
        return b;
    }

    private boolean preCheck(Vector<Double> dataRawTIMES) {
        
        boolean b = true;
        
        double sum = 0.0;

        for( double v : dataRawTIMES ) { 
            sum = sum + v;
        }
        if ( sum > (300 * 24) ) { 
            b = false;
            try {
                projekt.logLine("LENGTH", sum + " : " + dataRawTIMES +"\n");
            } catch (IOException ex) {
                Logger.getLogger(RISTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return b;
    }
  
}
