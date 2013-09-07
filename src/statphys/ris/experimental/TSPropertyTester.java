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
import data.series.MesswertTabelle;
import data.export.OriginProject;
import hadoopts.core.TSData;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.stat.regression.SimpleRegression;
//import panels.MessreiheWindow3Frame;

import statistics.HaeufigkeitsZaehler;
import statistics.HaeufigkeitsZaehlerDouble;
import statphys.detrending.DetrendingMethodFactory;
import statphys.detrending.MultiDFATool;
import statphys.detrending.methods.IDetrendingMethod;
import tstool.random.LongTermCorrelationSeriesGenerator;
import static tstool.random.LongTermCorrelationSeriesGenerator.nrOfSValues;


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

public class TSPropertyTester {
    
    public static int zSAMPLES = 1;

    static Vector<Messreihe> reihen = new Vector<Messreihe>();
    static Vector<Messreihe> reihen2 = new Vector<Messreihe>();
    
    public static void addSample(Messreihe mr) {    
        reihen.add(mr);
    }

    public static void addSampleAddNoCorrelations(Messreihe mr) {    
        reihen2.add(mr);
    }

    private static void runDFA(Vector<Messreihe> mrs, int i) throws Exception {
    
         Vector<Messreihe> vr = new Vector<Messreihe>();
         Vector<Messreihe> v = new Vector<Messreihe>();
        
        int order = 0;
        for( Messreihe d4 : mrs ) { 
        
            DecimalFormat df = new DecimalFormat("0.000");
            int N = d4.yValues.size();
            double[] zr = new double[N];
            

            Messreihe rawN = d4.copy();
            rawN.normalize();
            vr.add( rawN );

            zr = d4.getData()[1];

            IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
            order = dfa.getPara().getGradeOfPolynom();
            dfa.getPara().setzSValues( nrOfSValues );

            // Anzahl der Werte in der Zeitreihe
            dfa.setNrOfValues(N);

            // die Werte für die Fensterbreiten sind zu wählen ...
            //dfa.initIntervalS();
            dfa.initIntervalSlog();
            if ( debug ) dfa.showS();


            // http://stackoverflow.com/questions/12049407/build-sample-data-for-apache-commons-fast-fourier-transform-algorithm

            dfa.setZR( zr );

            dfa.calc();

            Messreihe mr4 = dfa.getResultsMRLogLog();
            mr4.setLabel( d4.getLabel() );
            v.add(mr4);

            String status = dfa.getStatus();

            SimpleRegression alphaSR = mr4.linFit(1.2, 3.5);

            double alpha = alphaSR.getSlope();
                        

        
        }
        
        Messreihe ref1 = getRef( 0.5 , 2.5);
        Messreihe ref2 = getRef( 0.9 , -5 );
        
        v.add( ref1 );
        v.add( ref2 );
        
        MultiChart.open(v, "fluctuation function F(s) [order:" + order + "] ", "log(s)", "log(F(s))", true );
        MultiChart.open(vr, "RAW data", "t", "y(t)", true );
        
        MesswertTabelle tab1 = new MesswertTabelle();
        tab1.singleX = false;
        tab1.setHeader("# fluctuation function F(s) [order:" + order + "] , log(s), log(F(s))");
        tab1.setMessReihen(v);
        tab1.writeToFile( new File("./hadoopts_image_b.dat") );
        
        MesswertTabelle tab2 = new MesswertTabelle();
        tab2.singleX = false;
        tab2.setHeader("# RAW data");
        tab2.setMessReihen(vr);
        tab2.writeToFile( new File("./hadoopts_image_d.dat") );
            
        
    }

    private static Messreihe getRef(double d, double n) {
        Messreihe mr = new Messreihe();
        mr.setLabel("m=" + d +")");
        int i = 0;
        while( i < 10 ) {
            mr.addValuePair(i , i*d + n );
            i++;
        }
        return mr;
        
    }

    private static Messreihe getRefExp(double d, double n) {
        Messreihe mr = new Messreihe();
        mr.setLabel("exp(tau * x) (tau=" + d +")");
        int i = 0;
        while( i < 10 ) {
            mr.addValuePair(i , d*(double)i + n );
            i++;
        }
        return mr;    
    }

    private static Messreihe getRefStretchedExp(double a, double b,double gamme) {
        Messreihe mr = new Messreihe();
        mr.setLabel("stretched_exp(x) (alphau=" + a +",beta="+b+",gamma="+gamme+")");
        int i = 0;
        while( i < 10 ) {
            double B = Math.pow( b*(double)i, gamme);
            double y = a * Math.exp(-1.0 * B);
            mr.addValuePair(i , y );
            i++;
        }
        return mr;    
        
    }
    
    public TSPropertyTester() { }
    
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

    public int binning = 400;   // anzahl der Werte pro Intervall
    public int scale = 8;    // größter Wert
    
    public static int binningD = 400;   // anzahl der Werte pro Intervall
    public static int scaleD = 8;    // größter Wert
    
//    // DEFAULT, wird nicht im Konstruktor definiert!!!
//    private static int binningSKALIERT = 8;   // anzahl der Werte pro Intervall
//    private static int scaleSKALIERT = 5;    // größter Wert

    private double _Rq = 1.0;
    private int zElements = 0;
    private double Rq = 1.0;
    
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

    
    public TSPropertyTester(String l, int bin, int s) {
        binning = bin;
        scale = s;
        name = l;
        initArray();
        label = this.getClass().getName();
        dataRawFILTERED = new Vector<Double>();
        dataRaw_InterEventTIMES = new Vector<Double>();
        
        ZZZ = bin * s;
        
        initRqCollectors();
    }

    public TSPropertyTester(int binning, int scale) {
        this( "unnamed" , binning, scale );
    }
    
    public TSPropertyTester(String name) {
        this( name , TSPropertyTester.binningD, TSPropertyTester.scaleD );
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
    
    private static Vector<Double> calcDist(Vector<Double> rr1, double ts) {
        double sum = 0.0;
        double c = 0.0;

        Vector<Double> dat = new Vector<Double>();
        
        double t = 1;
        
        double last = 0;
        double delta = 0;
        
        
        for( double l : rr1 ) { 

//            System.out.println(c + ") l="+l + " " + ts );
            if ( l > ts ) {
                t++;
                dat.add(t);
                t=1;
                c++;
            }
            else {
                t++;
                c++;
            }
            

            
        }
        return dat;
    }
    
    static private double calcAverageOfDist(Vector<Double> rr1, double ts) {
        double sum = 0.0;
        double c = 0.0;

        int t = 1;
        
        double last = 0;
        double delta = 0;
        
        for( double l : rr1 ) { 
        
            if ( l > ts ) {
                t++;
                sum = sum + t;
                t=1;
                c++;
            }
            else {
                t++;
            }
            
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
    
    public void add( TSPropertyTester ris ) throws Exception {
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
    public static HaeufigkeitsZaehler anzRowsFILTERED = new HaeufigkeitsZaehler("length of rows (filtered, offset="+TSPropertyTester.offset+"h)");

    Vector<Double> dataRawNOT_FILTERED = new Vector<Double>();
    
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
        
        if ( debug ) System.out.println( ">>> n=" + dists.size() + " Intervalle einfügen ...");
        
        
        long anzahlVor = (long)dists.size();
        anzRowRAW.addData( anzahlVor  );
        
        dataRawFILTERED = new Vector<Double>();
        dataRawNOT_FILTERED = new Vector<Double>();
        
        Enumeration en1 = dists.elements();
        while( en1.hasMoreElements() ) {
            
            double da = (Double)en1.nextElement();
            dataRawNOT_FILTERED.add(da);
            
            
//            if ( da >= offset && da != 0.0  ) {
                dataRawFILTERED.add( da );
//            }
//            else {
//                System.out.println("  Filtered: =>  (offset="+offset+") " + da  );
//                cutOff++;
//            }
        }
        
        long anzahlNach = (long)dataRawFILTERED.size(); 
        
//        anzRowsFILTERED.addData( anzahlNach );
        
        long delta = (anzahlVor-anzahlNach);
        
        if ( delta > 0) {
            String line = "### FILTER : " + label + "\t" + anzahlVor + "\t" + anzahlNach + "\t" + delta + "\n";
            // logLineToFile( logFileKey, line);
        }
        
        
        if ( debug ) {
            System.out.println( 
                    ">>> n_cutoff=" + cutOff + 
                    " Intervalle zu klein (offset:="+offset+")");
        }
        
        anzFiltered = dataRawFILTERED.size();
        
//        if ( dataRawFILTERED.size() >= 512 ) {
//            MessreiheWindow3Frame f = new MessreiheWindow3Frame();
//            f.show( dataRawTIMES , dataRawFILTERED, dataRawNOT_FILTERED );
//            System.out.println( "***>  " + dataRawFILTERED.size() );
//        }
        
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
             
             double v = dist1 / getRq() ;
             
             v = v * ( (double)binning / (double)scale );
             
             int index = (int)v;
             
//             System.out.println( "v="+ v + "\tindex=" + index + "\t dist=" + dist1 + "\tRq_neu=" + Rq_filtered );
             if ( index >= dataSKALIERT.length ) index = dataSKALIERT.length-1;
             else if ( index < 0 ) index = 0;
             else dataSKALIERT[index]++;  // hochzählen ...             
        }
        
        // Mittelwert der Wahrscheinlichkeiten berechnen ...
        for( int i = 0; i < probSKALIERT.length; i++ ) { 
            probSKALIERT[i] =  (double)( dataSKALIERT[i] / (double)zElements );
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
    
    boolean useLog = false;
    
    private void calcContainerMR() throws Exception {
        
        mrVerteilungSkaliert = new Messreihe( name + " (res=" + ((double)scale/(double)binning) + ")");
        
        
        for (int i = 0; i < dataSKALIERT.length; i++) {
            if ( dataSKALIERT[i] != 0 ) {
                double vx = ((double)i + 0.5) * (double)scale / (double)binning;
                double vy = probSKALIERT[i] / (double)dataSKALIERT[i];
                try {
                    if ( vy != 0.0 ) {
                        double logVy = 0.0;
                        
                        if ( useLog ) logVy = Math.log(vy);
                        else logVy = vy;
                        
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
        Rq = vRQ;
    }
    
    
    public double getRq() {
        return Rq;
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
    
    public void initRqCollectors() {
        
//        anzNullEdits = new int[maxB];
        
        TSPropertyTester.debug1 = new HaeufigkeitsZaehlerDouble[grenzen.length];
                
        mrRQ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        mrANZ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        
        for( int i = 0; i < grenzen.length; i++ ) { 
            mrRQ[i] = new HaeufigkeitsZaehlerDouble(); // "R_q " + labels[i] );
            mrRQ[i].intervalle = binning;
            mrRQ[i].max = binning;

            mrANZ[i] = new HaeufigkeitsZaehlerDouble();
            mrANZ[i].intervalle = binning;
            mrANZ[i].max = binning;
            
            TSPropertyTester.debug1[i] = new HaeufigkeitsZaehlerDouble();
            TSPropertyTester.debug1[i].label = "bis="+grenzen[i];
        }
    }
    
    
    
    public static void main( String[] args ) throws Exception {
        
        stdlib.StdRandom.initRandomGen(1);
        
        mrv = new Vector<Messreihe>();
        mrv2 = new Vector<Messreihe>();
        
        mrvB = new Vector<Messreihe>();
        mrv2B = new Vector<Messreihe>();
        debug = true;
        
        double min;
        
        Messreihe mr1 = null;
        

        
        // V1
        mr1 = Messreihe.getGaussianDistribution( (int)Math.pow(2, N), 1.0, 1.0);
        TSPropertyTester.addSample(mr1);

//        mr1 = Messreihe.getGaussianDistribution( (int)Math.pow(2, N), 5.0, 2.5);
//        min = mr1.getMinY();
//        mr1.add_value_to_Y( -1.0 * min);
//        TSPropertyTester.addSample(mr1);
 
        mr1 = Messreihe.getGaussianDistribution( (int)Math.pow(2, N), 1.0, 0.5);
        min = mr1.getMinY();
        mr1.add_value_to_Y( -1.0 * min);
        TSPropertyTester.addSample(mr1);
                
        // V1
        mr1 = Messreihe.getUniformDistribution( (int)Math.pow(2, N), 0.0, 2.0);
        TSPropertyTester.addSample(mr1);
 
        // V1
//        mr1 = Messreihe.getExpDistribution((int)Math.pow(2, N), 0.1);
//        TSPropertyTester.addSample(mr1);
        mr1 = Messreihe.getExpDistribution((int)Math.pow(2, N), 5.0);
        TSPropertyTester.addSample(mr1);
//        mr1 = Messreihe.getExpDistribution((int)Math.pow(2, N), 3.0);
//        TSPropertyTester.addSample(mr1);
//        mr1 = Messreihe.getExpDistribution((int)Math.pow(2, N), 10.0);
//        TSPropertyTester.addSample(mr1);

        // V1
        mr1 = Messreihe.getParetoDistribution((int)Math.pow(2, N), 1.0);
        TSPropertyTester.addSample(mr1);

        // V1
//        mr1 = Messreihe.getGeometricDistribution((int)Math.pow(2, N), 1.0);
//        TSPropertyTester.addSample(mr1);

//        int ex = 20;
//        for( int i = 0; i < 12; i++ ) {
//            // V2
//            double beta = 0.1 + ( (double)i / 5.0 );
//            if ( i > 6 ) ex = 22;
//            MessreiheFFT mr2 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, ex), beta, false, false);
//            min = mr2.getMinY();
//            mr2.add_value_to_Y( -1.0 * min);
//            TSPropertyTester.addSample(mr2);

//        }  
        // V3
        MessreiheFFT mr3 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, N), 0.8, false, false);
        mr3.normalize();
        min = mr3.getMinY();
        System.out.println( min );
        mr3.add_value_to_Y( -1.0 * min);
        min = mr3.getMinY();
        System.out.println( min );
        TSPropertyTester.addSample(mr3);
      
        // V3
        mr3 = (MessreiheFFT) LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, N), 0.9, false, false);
        mr3.normalize();
        min = mr3.getMinY();
        System.out.println( min );
        mr3.add_value_to_Y( -1.0 * min);
        min = mr3.getMinY();
        System.out.println( min );
        TSPropertyTester.addSample(mr3);
        
        Color[] colors = new Color[8];
        colors[0] = Color.BLUE;
        colors[1] = Color.cyan ;
        colors[2] = Color.ORANGE;
        colors[3] = Color.GREEN;
        colors[4] = Color.red;
        colors[5] = Color.darkGray;
        colors[6] = Color.lightGray;
        
        
        MultiChart._initColors(colors);
      
  
        TSPropertyTester.showTestResult();
    }
    
    static int N = 16;
    static double ts = 0.5;
    /**
     * 
     * @param m
     * @throws Exception 
     */     
    public static void addRowB( Messreihe m ) throws Exception {
 
        int binA = 15;
        int scaleA = 6;
        
 
        TSPropertyTester ris1 = new TSPropertyTester( 
               m.getLabel() , binA, scaleA);
          
        
        Vector<Double> p = calcDist( m.yValues, ts );
        System.out.println( "??? *** >>> " + m.label + " p=" + p.size() ); 
        
//        HaeufigkeitsZaehlerDouble hzd = new HaeufigkeitsZaehlerDouble();
//        hzd.addData(p);
//        hzd.calcWS();
//                
//        Messreihe mr = hzd.getHistogramNORM();
//        mr = mr.log();
//        mr.setLabel( m.label + " iet( " + ts + ")" );
        
        if ( p.size() > 10 ) {
            
//            ris1.setRQ(  calcAverageOfDist(p,ts) );
            ris1.addDistData(p);
            ris1.useLog = true;
        
//        ris1.addDistData(mr.yValues);
        
            ris1.calcMessreihen(); 

            TSPropertyTester ris4 = new TSPropertyTester( m.getLabel(), binA, scaleA);
            ris4.isContainerInstanz = true;
            ris4.add(ris1);
            ris4.useLog = true;
            ris4.calcMessreihen();

            mrvB.add( ris4.mrVerteilungSkaliert );
        }
//        mrv2B.add( mr );         
    }
        
    public static void addRow( Messreihe m ) throws Exception {
        
        int binA = 120;
        int scaleA = 4;
                
        addRowB(m.copy());
        
        Vector<Double> data1 = m.yValues;
        System.out.println(">>> " + m.label + " N=" + m.yValues.size() );
        
        TSPropertyTester ris1 = new TSPropertyTester( 
               m.getLabel() , binA, scaleA);
                       
        double ts = 0.0;
        double rq = ris1.calcAverageOfDist(data1,ts);
        System.out.println( ">>> RQ : " + rq );
        
        m.calcAverage();
        ris1.useLog = false;
        
        ris1.setRQ( m.getAvarage() );
//        ris1.setRQ( 1.0);
        ris1.addDistData(data1);
        ris1.calcMessreihen();

        TSPropertyTester ris4 = new TSPropertyTester( 
                m.getLabel(), binA, scaleA);
        ris4.isContainerInstanz = true;
        ris4.add(ris1);
        ris4.setRQ( 1.0 );
        ris4.calcMessreihen();

        mrv.add( ris4.mrVerteilungSkaliert ); 
        mrv2.add( ris4.mrHaeufigkeit ); 
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

    static public void showTestResult() throws Exception {
        
        Vector<Messreihe> mrs = new Vector<Messreihe>();
        
        for( Messreihe mr : reihen ) {
            addRow( mr );
//            addRowB(mr);  // RIS
            mrs.add(mr);  // DFA
        }
        
        
        

//        for( Messreihe mr : reihen2 ) {
//            addRow( mr );
//        }

         
        runDFA(mrs, 2);
      
        Messreihe ref2 = getRefExp( -1.0, 0.0 );
        mrvB.add( ref2);
        
        Messreihe ref3 = getRefStretchedExp( 126.0, 15120.0 , 0.2);
        ref3.calcLog10_for_Y();
        mrvB.add( ref3);
        
        MultiChart.open(mrv, "distribution (raw data)", "k/<k>", "P(X=k)", true);
//        MultiChart.open(mrv2, true);

        MultiChart.open(mrvB, "RIS", "r/RQ", "RQ * P(r)",  true);
        //MultiChart.open(mrv2B, "raw inter event times" , "index", "iet", true);
 
        MesswertTabelle tab1 = new MesswertTabelle();
        tab1.singleX = false;
        tab1.setHeader("# distribution (raw data), k/<k>, P(X=k)");
        tab1.setMessReihen(mrv);
        tab1.writeToFile( new File("./hadoopts_image_a.dat") );
        
        
        MesswertTabelle tab2 = new MesswertTabelle();
        tab2.singleX = false;
        tab2.setHeader("#RIS, r/RQ, RQ * P(r)");
        tab2.setMessReihen(mrvB);
        tab2.writeToFile( new File("./hadoopts_image_c.dat") );
        
        
    }


  
}


