package data.series;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math.stat.regression.SimpleRegression;

import org.jfree.data.xy.XYSeries;
import statistics.DatasetNormalizationTool;

import stdlib.StdStats;


public class Messreihe implements IMessreihe, Serializable {
    
    static final long serialVersionUID = 3519446562958441036L;


    
    public Messreihe( double[] d ) {
        this();
        for( int i = 0; i < d.length; i++ ) {
            this.addValue(d[i]);
        }
    }
    
    public static double[] calcPeriodeTrend( Messreihe mr, int d ) {

        double[] sum = new double[d]; 
        double[] count = new double[d]; 
        double[] trend = new double[d]; 

        double[][] data = mr.getData();
        int z = mr.yValues.size();
        
        for( int i = 0; i < z; i++ ) {
            double v = data[1][i];
            int c = i % d;
            sum[c] = sum[c] + v;
            count[c]++;
        }
        
        for( int i = 0; i < d; i++ ) {
            trend[i] = sum[i] / count[i];
        }
        
        return trend;
    }
    
    public static Messreihe normalizeByPeriodeTrend( Messreihe mr, int d ) {

        Messreihe mr2 = new Messreihe();
        mr2.setLabel( mr.getLabel() + "_t"+d+"_removed");

        double[] trend = calcPeriodeTrend(mr,d);
        double[][] data = mr.getData();
 
        int z = mr.yValues.size();
        
        for( int i = 0; i < z; i++ ) {
            int c = i % d;
            //System.out.println( c + " " + i );
            double t = data[0][i];
            double v = data[1][i] / trend[c];
            mr2.addValuePair(t, v);
        }
        return mr2;
    }
    
    public boolean isNotEmpty() {
        boolean v = true;
        if ( this.xValues == null ) {
            v = false;
        }
        else {
            if ( xValues.size() == 0 ) v = false;
        }
        return v;
    };
    
    // String[] enthält dann id1 und id2 in einem String[2] Array
    public Vector<String[]> xLabels = new Vector<String[]>();
    public Vector<String> xLabels2 = new Vector<String>();

    public void doFilter( double aboveMW ) {
        for( int i = 0; i < yValues.size(); i++ ) {
            double vx = (Double)xValues.elementAt(i);
            double vy = (Double)yValues.elementAt(i);

            if ( (vy / aboveMW) > this.av ) yValues.setElementAt( av, i);

        }
    }

    public Messreihe replaceZeroWithAverage() {
        Messreihe mr = new Messreihe();
        mr.setLabel(label);
        mr.setLabel_X(label_X);
        mr.setLabel_Y(label_Y);

        this.calcAverage();

        double av = this.getAvarage();

        for( int i = 0; i < yValues.size(); i++ ) {
            double vx = (Double)xValues.elementAt(i);
            double vy = (Double)yValues.elementAt(i);
            if (vy == 0.0) vy = av;
            mr.addValuePair(vx, vy);
        };

        return mr;
    };

    private static int getShortestLength(Messreihe[] mrs) {
        boolean isFirst = true;
        int l = 0;
        for ( Messreihe mr : mrs ) {
            int l2 = mr.xValues.size();
            if ( isFirst ) {
               l = l2;
               isFirst = false;
            }
            else {
               if ( l2 < l ) l = l2;
            }
        };
        return l;
    }

    public DecimalFormat getDecimalFormat_STAT() {
        return decimalFormat_STAT;
    }

    public DecimalFormat getDecimalFormat_X() {
        return decimalFormat_X;
    }

    public DecimalFormat getDecimalFormat_Y() {
        return decimalFormat_Y;
    }

    public StringBuffer getStatus() {
        return status;
    }

    int anzahlOfAllAddedMessreihen = 1;

    public static Messreihe getUniformDistribution(int i, double d, double d0) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => uniform (" +d+","+d0+")");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.uniform(d, d0));
        }
        return mr;    
    }
        
    public static Messreihe getGaussianDistribution(int i) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => gaussian");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.gaussian() );
        }
        return mr;
    }

    public static Messreihe getGaussianDistribution(int i, double mw, double std) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => gaussian (mu="+mw+", sigma="+std+")");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.gaussian(mw,std) );
        }
        return mr;
    }
    
    public static Messreihe getExpDistribution(int i, double mw) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => exp (lambda="+mw+")");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.exp(mw) );
        }
        return mr;
    }
    
    public static Messreihe getParetoDistribution(int i, double mw) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => pareto (alpha="+mw+")");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.pareto(mw) );
        }
        return mr;
    }

    public static Messreihe getGeometricDistribution(int i, double mw) {
        Messreihe mr = new Messreihe();
        mr.setLabel(i + " => geometric (p="+mw+")");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.geometric(mw) );
        }
        return mr;
    }

    
    public static MessreiheFFT getGaussianDistribution2(int i, double mw, double std) {
        MessreiheFFT mr = new MessreiheFFT();
        mr.setLabel(i + " values => gaussian distribution");
        mr.setDecimalFomrmatX( "0.00000" );
        mr.setDecimalFomrmatY( "0.00000" );
        for( int j = 0 ; j<i; j++ ) {
            mr.addValuePair( 1.0*j , stdlib.StdRandom.gaussian(mw,std) );
        }
        return mr;
    }
 
    public int[] getSize() {
        int[] d = new int[2];
        d[0] = xValues.size();
        d[1] = yValues.size();
        return d;
    };

    

    public static String dfx = "0.00000";
    public static String dfy = "0.00000";
    public static String dfstat = "0.00000";

    DecimalFormat decimalFormat_X = new DecimalFormat( dfx );
    DecimalFormat decimalFormat_Y = new DecimalFormat( dfy );
    DecimalFormat decimalFormat_STAT = new DecimalFormat( dfstat );

    public String label = null;
    public String addinfo = "";

    public Vector xValues = null;
    public Vector yValues = null;


    /**
     *
     * @param Label der messreihe
     * @param xL - Laber der x-Werte
     * @param yL - Label der y-Werte
     */public Messreihe(String label, String xL, String yL) {
        this(label);
        this.label_Y = yL;
        this.label_X = xL;
    };


    public Messreihe(String label) {
        this();
        this.label = label;
    };

    /**
     * Datencontainer werden instanziiert.
     */
    public Messreihe() {
        xValues = new Vector<Double>();
        yValues = new Vector<Double>();
        if ( !noStatus ) {
            label = "unnamed - " + DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ));
            status = new StringBuffer();
            status.append( "[CREATION] \t" + new Date( System.currentTimeMillis() ) +"\n" );
            status.append( "[FORMAT]   \tx:" + dfx + "\ty:" + dfy +"\n" );        
        };    
        dateHash = new Hashtable<Date,Double>();
        hashedValues = new Hashtable<String,Double>();
    }
    
    static public boolean noStatus = false;


    /**
     * Ein Wertepaar wird hinzugefügt.
     *
     * @param x
     * @param y
     */
    public void addValuePair( double x, double y ) {
        xValues.add(x);
        yValues.add(y);
    };


    public Vector getXValues() {
        return xValues;
    }

    public Vector getYValues() {
        return yValues;
    }

    /**
     * Die Daten zur Ausgabe auf der Konsole ausgeben.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        int size = xValues.size();

        sb.append("#\n# M e s s r e i h e \n# Klasse : " + this.getClass() + " \n# Label  : " + this.label + "\n#\n# Anzahl Wertepaare = " + size + "\n#\n");
        sb.append("#\n#" + this.addinfo + "\n#");
        sb.append( getStatisticData("# ") +"\n" );
        for (int i = 0 ; i < size; i++  ) {
            double x = (Double)getXValues().elementAt(i);
            double y = (Double)getYValues().elementAt(i);

            sb.append(  decimalFormat_X.format(x) + " " + decimalFormat_Y.format(y) + "\n");
        }
        sb.append("#\n" + sbComments.toString() );
        return sb.toString();
    };

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAddinfo(String addinfo) {
        this.addinfo = addinfo;
    }

    public String getAddinfo() {
        return addinfo;
    }

    public String getLabel() {
        return this.label;
    }

    public double[][] getData() {
        int z = this.xValues.size();
        double[][] data = new double[2][z];

        int max = xValues.size();

        for (int i = 0 ; i < max; i++  ) {
            double x = (Double)getXValues().elementAt(i);
            double y = (Double)getYValues().elementAt(i);
            data[0][i]= x;
            data[1][i]= y;
        }
        return data;
    };

    /**
     * berechnet den Mittelwert für die Werte der Y-Spalte
     */
    public void calcAverage() {
        int z = this.xValues.size();
        double sum = 0.0;

        for (int i = 0 ; i < z; i++  ) {
            sum = sum + (Double)getYValues().elementAt(i);

        }
        av = sum / ((double)z);
        // System.out.println(getLabel() + " mw=" + av + " z=" + z + " sum=" + sum);

    };

    double av = 1.0;

    /**
     * Wird an dieser Stelle nich neu berechnet - enthält also die
     * letzte Größe, seit dem Aufruf von calcAverage() !
     *
     * @return
     */
    public double getAvarage2() {
        calcAverage();
        return av;
    };

    public double getAvarage() {
        return av;
    };


    public void setDecimalFomrmatX(String format) {
        this.dfx = format;
        this.decimalFormat_X = new DecimalFormat( format );
    }
    public void setDecimalFomrmatY(String format) {
        this.dfy = format;
        this.decimalFormat_Y = new DecimalFormat( format );
    }

    public double getMinX() {
        double[][] data = this.getData();
        double[] dx = data[0];
        return StdStats.min( dx );
    };
    
    public double getMaxX() {
        double[][] data = this.getData();
        double[] dx = data[0];
        return StdStats.max( dx );
    };

    public double getMaxY() {
        double[][] data = this.getData();
        double[] dy = data[1];
        return StdStats.max( dy );
    };

    public double sumYValues() { 
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];
        return StdStats.sum( dy );    
    };


    public String get_Y_StatisticsData_Line() {

        StringBuffer sb = new StringBuffer();

        try {
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];

        sb.append("max_Y=" + decimalFormat_STAT.format(StdStats.max( dy ) )  + "\t" );
        sb.append("min_Y=" + decimalFormat_STAT.format(StdStats.min( dy ) )  + "\t" );
        sb.append("mw__Y=" + decimalFormat_STAT.format(StdStats.mean( dy ) )  + "\t" );
        sb.append("std_Y=" + decimalFormat_STAT.format(StdStats.stddev( dy ) )  + "\t" );
        sb.append("var_Y=" + decimalFormat_STAT.format(StdStats.var( dy ) )+ "\t" );
        sb.append("sum_Y=" + decimalFormat_STAT.format( StdStats.sum( dy ) ) + "\t" );
        sb.append("nr__Y=" + dy.length + "\t" );
        }
        catch(Exception ex) {
            sb.append(( " NO STATISTICS - " + ex.getMessage() ));
        }

        return sb.toString();
    };


    public String getStatisticData( String pre ) {
        StringBuffer sb = new StringBuffer();
        try {
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];


        sb.append(pre+"X:\n");
        sb.append(pre+"==\n");

        sb.append(pre+"Max    \t max_X=" + decimalFormat_STAT.format(StdStats.max( dx ) ) + "\n" );
        sb.append(pre+"Min    \t min_X=" + decimalFormat_STAT.format(StdStats.min( dx ) ) + "\n" );
        sb.append(pre+"Mean   \t mw__X=" + decimalFormat_STAT.format(StdStats.mean( dx ) ) + "\n" );
        sb.append(pre+"StdAbw \t std_X=" + decimalFormat_STAT.format(StdStats.stddev( dx ) ) + "\n" );
        sb.append(pre+"Var    \t var_X=" + decimalFormat_STAT.format(StdStats.var( dx ) ) + "\n" );
        sb.append(pre+"Sum    \t sum_X=" + decimalFormat_STAT.format(StdStats.sum( dx ) ) + "\n" ) ;
        sb.append(pre+"Nr     \t nr__X=" +  dx.length + "\n" );

        sb.append(pre+"\n");

        sb.append(pre+"Y:\n");
        sb.append(pre+"==\n");

        sb.append(pre+"Max    \t max_Y=" + decimalFormat_STAT.format(StdStats.max( dy ) )  + "\n" );
        sb.append(pre+"Min    \t min_Y=" + decimalFormat_STAT.format(StdStats.min( dy ) )  + "\n" );
        sb.append(pre+"Mean   \t mw__Y=" + decimalFormat_STAT.format(StdStats.mean( dy ) )  + "\n" );
        sb.append(pre+"StdAbw \t std_Y=" + decimalFormat_STAT.format(StdStats.stddev( dy ) )  + "\n" );
        sb.append(pre+"Var    \t var_Y=" + decimalFormat_STAT.format(StdStats.var( dy ) )+ "\n" );
        sb.append(pre+"Sum    \t sum_Y=" + decimalFormat_STAT.format( StdStats.sum( dy ) ) + "\n" );
        sb.append(pre+"Nr     \t nr__Y=" + dy.length + "\n#" );
        }
        catch(Exception ex) {
            sb.append(( pre + " NO STATISTICS - " + ex.getMessage() ));
        }

        return sb.toString();
    };

    public double[] getYData() {
        double[] yV = new double[ this.getYValues().size()];

        for (int i = 0; i < this.getYValues().size(); i++) {
               Double y = (Double)this.getYValues().get(i);
               yV[i] = y;
        }

        return yV;
    }

    public XYSeries getXYSeries() {
        XYSeries series = new XYSeries( this.getLabel() );
        
        for (int i = 0; i < this.getXValues().size(); i++) {
               Double x = (Double)this.getXValues().get(i);
               Double y = (Double)this.getYValues().get(i);
               series.add( x , y );
        }

        return series;
    }

    /**
     * Speichert die Messreihe in der Datei f und legt 
     * das Verzeichnis an, falls dieses nicht schon existiert.
     * 
     * @param f 
     */
    public void writeToFile(File f) {
        if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();

        FileWriter fw = null;
        try {
           fw = new FileWriter(f);     
           fw.write( this.toString());
           
           System.out.println( "go.. " + f.getAbsolutePath() );
           
           fw.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            try {
                if ( fw != null ) {
                    fw.close();
                }
                else System.err.println( "FW war bereits NULL !!!");
            }
            catch (IOException ex) {
                Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void writeToFile(File f, Character chara) {
        if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();

        FileWriter fw = null;
        try {
           fw = new FileWriter(f);    
           String s = this.toString();
           s = s.replace(',', chara);
           fw.write( s );
           fw.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            try {
                fw.close();
            }
            catch (IOException ex) {
                Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addValue(double y) {
        int x = this.getXValues().size() + 1;
        this.addValuePair( 1.0 * x , y );
    }

    String label_X = "X";
    String label_Y = "Y";

    public String getLabel_X() {
        return this.label_X;
    }

    public void setLabel_X(String label_X) {
        this.label_X = label_X;
    }

    public void setLabel_Y(String label_Y) {
        this.label_Y = label_Y;
    }

    public  String getLabel_Y() {
       return this.label_Y;
    }

    /**
     * Für spalte Y !!! 
     * @return
     */
    public double getStddev() {
//        double[][] data = this.getData();
//        double[] dx = data[0];
//        double[] dy = data[1];
//        double sigma = StdStats.stddev( dy );
        Object[] data = yValues.toArray();
        double[] dy = new double[ data.length ];
        for( int i = 0; i < data.length ; i++ ) {
            dy[i] = (Double)data[i];
        }
        double sigma = StdStats.stddev( dy );
        return sigma;
    }

    /**
     * Normalizes to Maximum
     **/ 
    public void normalize() {
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];

        double maxY = StdStats.max( dy );

        for( int i = 0; i < this.yValues.size(); i++ ) {
            double d = (Double)this.yValues.elementAt(i);

            this.yValues.set(i, new Double( d / maxY ) );
        }
        this.label = this.label.concat(" (max=" + maxY +")" );
    }
    
    public void calcLog10_for_Y() {
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];

         

        for( int i = 0; i < this.yValues.size(); i++ ) {
            double d = (Double)this.yValues.elementAt(i);
            if ( d > 0) this.yValues.set(i, Math.log10(d) );
            else { 
                this.yValues.set(i, 0.0 );
            }
        }
        this.label = this.label.concat("_log10" );
    }
    
    public void calcLn_for_Y() {
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];

         

        for( int i = 0; i < this.yValues.size(); i++ ) {
            double d = (Double)this.yValues.elementAt(i);

            double v = Math.log(d);
            if ( v != Double.NEGATIVE_INFINITY && v != Double.POSITIVE_INFINITY ) {
                this.yValues.set(i, v );
            }    
        }
        this.label = this.label.concat("_ln" );
    }
    
    public Messreihe calcMR_Ln_for_Y() {
        Messreihe mr = new Messreihe( this.label.concat("_ln" ) );
        
        double[][] data = this.getData();
        double[] dx = data[0];
        double[] dy = data[1];

        for( int i = 0; i < this.yValues.size(); i++ ) {
            double d = (Double)this.yValues.elementAt(i);
            double x = (Double)this.xValues.elementAt(i);

            double v = Math.log(d);
            if ( v != Double.NEGATIVE_INFINITY && v != Double.POSITIVE_INFINITY ) {
                mr.addValuePair( x, v );
            }    
        }
        return mr;
    }

    public double getYValueForX2( double x, double epsilon ) {
 
        double returnValueY = Double.NaN;
        
        int counter = 0; // für den Zugriff auf das Datenfeld
        
        // die Y Werte durchgehen ...
        Enumeration<Double> vx = this.xValues.elements();
        while( vx.hasMoreElements() ) {
            // Vergleichswert ermitteln  
            double wx = vx.nextElement();
            if ( x > (wx-epsilon) && (x <= (wx+epsilon) ) ) {
                returnValueY = ((Double)this.yValues.elementAt(counter)).doubleValue();
            }
            counter++;
        }
        return returnValueY;
    };
    
    public double getYValueForX2( double x ) {
        int i = this.xValues.indexOf( x );
        double y = (Double)this.yValues.elementAt(i);
        return y;
    };

    public double getYValueForX(int i) {
        // index von X-Wert bestimmen ...
        int indexX = this.xValues.indexOf( new Double(i) );
        Double value = null;
        if( indexX >= 0 ) value = (Double)this.yValues.elementAt(indexX) ;
        else value = new Double( 0 );
        return value.doubleValue();
    }

    int binning = 1;

    // jeweils "bin" Werte werden addiert und dem ersten Feld des Blocks
    // gespeichert.
    public Messreihe setBinningX_sum( int bin ) {

        Messreihe mr = new Messreihe();
        mr.binning = bin;
        mr.setLabel( this.getLabel() + "_BIN=" +bin+"" );
        Enumeration<Double> en = this.yValues.elements();
        int anzahl = this.yValues.size();

        int rest = anzahl % bin;
        int elements = anzahl - rest;
        int blocks = elements / bin;

        double summeA = this.summeY();

        // System.out.println( "[BINNING] bin=" + bin + "\tRest=" + rest + "\tBlöcke=" + blocks  );
        mr.addStatusInfo( "[BINNING] bin=" + bin + "\tRest=" + rest + "\tBlöcke=" + blocks  );

        int i=0;
        int j=0;
        double v = 0;

        while ( i < elements ) {
            v = v + en.nextElement();

            if( i % bin == (bin-1) ) {
                mr.addValuePair( j , v);
                v = 0;
                j++;
            }
            i++;
        }
        return mr;

    };

    StringBuffer status = null;

    /**
     *
     * @param bin
     * @return
     */
    public Messreihe setBinningX_average( int bin ) {

        Messreihe resultat = new Messreihe();
        resultat.setLabel( getLabel() + "_AV("+bin+")");
       
        double summe = 0;
        
        Enumeration en = yValues.elements();
        int c = 0;
        int z = -1;
        while ( en.hasMoreElements() ) {
            z++;
            
            summe = summe + (Double)en.nextElement();
            
            if ( z == (bin-1) ) {
               double wert = summe/(double)bin;
               //System.out.println( c+"#"+bin+"-"+ summe+"---" + wert);
               resultat.addValuePair( c , wert );
               summe = 0.0;
               z=-1;
               c++;
            }
            
        }

        return resultat;
    };

    public void divide_Y_by( double divBy ) {
        Vector<Double> y = new Vector<Double>();
        Enumeration<Double> en = this.yValues.elements();
        while( en.hasMoreElements() ) {
            y.add( en.nextElement() / divBy );
        }
        this.yValues = y;
    };

    public void add_to_Y( double offs ) {
        Vector<Double> y = new Vector<Double>();
        Enumeration<Double> en = this.yValues.elements();
        while( en.hasMoreElements() ) {
            y.add( en.nextElement() + offs);
        }
        this.yValues = y;
    };
        
    public double summeY() {
        double summe = 0;
        Enumeration<Double> en = this.yValues.elements();
        while( en.hasMoreElements() ) {
            summe = summe + en.nextElement();
        }
        return summe;
    };

    // berechnet die Differenz zwischen den beiden Messreihen ... punktweise
    public Messreihe divide_by( Messreihe mr2 ) {

        System.out.println( "Quotient der Reihen: " + (this.yValues.size() + " ... " + mr2.yValues.size()) );

        double difff = 0.0;
        Messreihe mr = new Messreihe();
        if ( mr2 == null ) return mr;
        if ( this.xValues.size() < mr2.xValues.size() ) return mr;

        int max = mr2.xValues.size();
        for( int k = 0; k < max; k++ ) {

            double q = 0;
            double v1 = (Double)this.yValues.elementAt(k);
            double v2 = (Double)mr2.yValues.elementAt(k);
            if ( v2 != 0.0 ) q = v1/v2;
            mr.addValuePair(k, q);
        }
        mr.setLabel( this.getLabel() + " / " + mr2.getLabel() );
        return mr;
    };
    
    // berechnet die Differenz zwischen den beiden Messreihen ... punktweise
    public Messreihe diff( Messreihe mr2 ) {

        System.out.println( "Differenz der Anzahl der Werte: " + (this.yValues.size() - mr2.yValues.size()) );
        double difff = 0.0;
        Messreihe mr = new Messreihe();

        int max = mr2.xValues.size();
        for( int k = 0; k < max; k++ ) {

            double v1 = (Double)this.yValues.elementAt(k);
            double v2 = (Double)mr2.yValues.elementAt(k);
            double delta = v1-v2;
            difff = difff + Math.abs(delta);
            mr.addValuePair(k, delta);
        }
        return mr;
    };

    // berechnet die Summe der beiden Messreihen ... punktweise
    public Messreihe add( Messreihe mr2 ) {

        this.anzahlOfAllAddedMessreihen++;

        // System.out.println( "Add:" + mr2.yValues.size() + " To:" + this.yValues.size() );

        Messreihe mr = null;

        // nur beim ersten mal, wenn die Basis-Messreihe noch leer ist
        if ( this.yValues.size() == 0 ) {
            mr = mr2.copy();
        }
        else {
            mr = new Messreihe();
            int max = mr2.yValues.size();
            for( int k = 0; k < max; k++ ) {

                double v1 = 0.0;
                double v2 = 0.0;

                try {
                    v1 = (Double)this.yValues.elementAt(k);
                }
                catch( Exception ex) {

                };
                try {
                    v2 = (Double)mr2.yValues.elementAt(k);
                }
                catch( Exception ex) {

                };
     
                double sum = v1+v2;
                mr.addValuePair(k, sum);
            }
        }
        return mr;
    };
    
    public Messreihe add(Messreihe messreihe, int shortestLength) {
        
        Messreihe mr = mr = new Messreihe();

        int max = shortestLength;

        for( int k = 0; k < max; k++ ) {

            double v1 = 0.0;
            double v2 = 0.0;

            try {
                v1 = (Double)this.yValues.elementAt(k);
            }
            catch( Exception ex) {

            };
            try {
                v2 = (Double)messreihe.yValues.elementAt(k);
            }
            catch( Exception ex) {

            };

            double sum = v1+v2;
            mr.addValuePair(k, sum);
        }

        return mr;
    }

    /**
     * Hängt Werte an bestehende Reihe an...
     * 
     */ 
    public void addValues( Messreihe mr ) {
        Enumeration en = mr.xValues.elements();
        int i = 0;
        while( en.hasMoreElements() ) {
            double x = (Double)en.nextElement();
            double y = (Double) mr.yValues.elementAt(i);
            this.addValuePair(x, y);
            i++;
        }
    };

    public Messreihe scaleX(int maxX) {

        double summeOriginal = summeY();
        Messreihe mr = new Messreihe();
        double a = this.yValues.size()*1.0;
        double b = (maxX*1.0);
        double fieldsPerBlock = b / a;
        mr.addStatusInfo("[SCALE] \tFields per Block:" + fieldsPerBlock + "\tvalues:" + a + "\tmaxX:" +b +"\n");
        
        mr.setLabel( this.getLabel() + "SCALED:x="+maxX+"");
        Enumeration<Double> en = this.yValues.elements();
        int j = 0;
        while( en.hasMoreElements() ) {
            double wert = en.nextElement();
            double v = wert / (fieldsPerBlock*1.0);
            for( int i = 0; i < fieldsPerBlock; i++ ) {
                
                mr.addValuePair(j, v);
                j++;
            }
        }
        mr.addStatusInfo("[SCALE] \tError: " + (mr.summeY() - summeOriginal)+"\n" );
        return mr;
    }

    public void addStatusInfo(String string) {
        this.status.append( "\n#" + string );
    }
    public String getStatusInfo() {
        return this.status.toString();
    }

    public Messreihe cut( int nrOfValues ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );

        for( int i = 0; i < nrOfValues ; i++ ) {
            mr.addValuePair( (Double)xValues.elementAt(i),(Double)yValues.elementAt(i) );
        };
        mr.addStatusInfo("[CUT] \tLength old:" + this.xValues.size() + "\tLength new:" + nrOfValues +"\n");

        return mr;
    };

    /**
     * verschiebe die Werte der Reihe im "offset" stellen.
     *
     *   offset > 0 rechts
     *   offset < 0 links
     *
     * @param offset
     *
     * @return neue Reihe
     *
     * @throws Exception
     */
    public Messreihe shift( int offset ) throws Exception {
        if( Math.abs(offset) > yValues.size() ) throw new Exception("Messreihe kürzer als SHIFT-Weite!");
        if ( offset < 0 ) return this.shiftLeft( Math.abs(offset));
        else return this.shiftRight( Math.abs(offset));
    };

        /**
     * verschiebe die Werte der Reihe im "offset" stellen.
     *
     *   offset > 0 rechts
     *   offset < 0 links
     *
     * @param offset
     *
     * @return neue Reihe
     *
     * @throws Exception
     */
    public Messreihe shift( int offset , double v ) throws Exception {
        if( Math.abs(offset) > yValues.size() ) throw new Exception("Messreihe kürzer als SHIFT-Weite!");
        if ( offset < 0 ) return this.shiftLeft( Math.abs(offset), v);
        else return this.shiftRight( Math.abs(offset), v);
    };

    public Messreihe copy( int limit ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        mr.hashedValues = new Hashtable<String,Double>();
        mr.xLabels = new Vector<String[]>();
        
        for( int i = 0; i < limit && i < yValues.size(); i++ ) {
            mr.addValuePair( (double)i , (Double)yValues.elementAt(i) );
        }
        
//        Set s = this.hashedValues.keySet();
//        if ( s != null ) {
//            Iterator it = s.iterator();
//            for( int i = 0; i < limit; i++ ) {
//                
//                String key = (String)it.next();
//                Double value = (Double)this.hashedValues.get(key);
//                
//                mr.hashedValues.put( key , value );
//            }
//        }
        return mr;
    }
        
    public Messreihe copy() {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            mr.addValuePair( (double)i , (Double)yValues.elementAt(i) );
        }
        return mr;
    }

    public Messreihe cutFromStart( int n ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() + "( -" + n + " Werte )" );

        int max = this.yValues.size();

        for( int i = n; i < max ; i++ ) {
            mr.addValuePair( (double)i , (Double)yValues.elementAt(i) );
        }
        return mr;
    };

    public Messreihe addToX( int v ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );

        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            mr.addValuePair( 1.0*(i+v) , (Double)this.yValues.elementAt(i) );
        }
  
        mr.addStatusInfo("[Add to x]=" + v +"\n");

        return mr;
    };
    
    private Messreihe shiftRight( int nrOfValues , double missingValue ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );

        int max = this.yValues.size();

        for( int i = 0; i < nrOfValues ; i++ ) {
            mr.addValuePair( (double)i , missingValue );
        }
        for( int i = nrOfValues; i < max ; i++ ) {
            mr.addValuePair( (double)i,(Double)yValues.elementAt(i-nrOfValues) );
        };
        mr.addStatusInfo("[SHIFT] \tRIGHT: " + nrOfValues +"\n");

        return mr;
    };

    private Messreihe shiftRight( int nrOfValues ) {
        return shiftRight(nrOfValues, 0.0);
    };

    private Messreihe shiftLeft( int nrOfValues, double missingValue ) {

        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );

        int max = this.yValues.size();

        int j = 0;
        for( int i = nrOfValues; i < max ; i++ ) {
            mr.addValuePair( (double)j,(Double)yValues.elementAt(i) );
            j++;
        };
        for( int i = 0; i < nrOfValues ; i++ ) {
            mr.addValuePair( (double)j , missingValue );
            j++;
        }
        mr.addStatusInfo("[SHIFT] \tLEFT: " + nrOfValues +"\n");

        return mr;
    };

    private Messreihe shiftLeft( int nrOfValues ) {
        return shiftLeft(nrOfValues, 0.0 );
    };


    private void setStatusInfo(String statusInfo) {
        this.status = new StringBuffer();
        this.status.append(statusInfo );

    }
    
        public Messreihe scaleX_2(double f) {

        Messreihe mr = new Messreihe();
        //mr.setLabel( "x"+f );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            mr.addValuePair( (Double)xValues.elementAt(i) * f , (Double)yValues.elementAt(i)  );
        }
        return mr;
    }



    public Messreihe scaleY_2(double f) {

        Messreihe mr = new Messreihe();
        mr.setLabel( "x"+f );
            mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            mr.addValuePair( (Double)xValues.elementAt(i)  , (Double)yValues.elementAt(i) * f  );
        }
        return mr;
    }

    public void setLabels(String label, String xL, String yL) {
        this.label = label;
        this.label_Y = yL;
        this.label_X = xL;
    }

//    public Messreihe add( Messreihe mr2 ) {
//
//        Messreihe mr = new Messreihe();
//
//        return mr;
//
//
//    };

    /**
     * Nutzt elementare Funktionen ...
     *
     * UNGEPRÜFT ...
     *
     * @param mrs
     * @return
     */
    static public Messreihe averageForAll( Messreihe[] mrs ) {
        int anz = mrs.length;

        int shortestLength = getShortestLength( mrs );
        
        Messreihe mr = new Messreihe("MW_" + anz );

        for ( int i = 0; i < anz; i++ ) {
            mr = mr.add(mrs[i] , shortestLength );
        };
        mr.divide_Y_by( anz );
        return mr;
    };

    /**
     * In einer EPSILON Umgebung nachsehen und die erste nehmen
     * 
     * @param y
     * @return 
     */
    public double getX_for_Y2( double y , double epsilon ) {
        
        double returnValueX = Double.NaN;
 
        // die Y Werte durchgehen ...
        Enumeration<Double> vy = this.yValues.elements();
        while( vy.hasMoreElements() ) {
            // Vergleichswert ermitteln  
            double wy = vy.nextElement();
            if ( returnValueX == Double.NaN ) {
                if( y > (wy-epsilon) && y < (wy+epsilon) ) { 
                    returnValueX = wy;
                }    
            }
            else { 
                return returnValueX;
            } 
        }
        return returnValueX;
    };

    public double getX_for_Y( double y ) {
        int counter = 0;
        double value = 0.0;
        Enumeration<Double> vy = this.yValues.elements();
        while( vy.hasMoreElements() ) {
            double wy = vy.nextElement();
            if ( y > wy ) {
                value = ((Double)this.xValues.elementAt(counter)).doubleValue();
            }
            counter++;
        }
        return value;
    };

    /**
     * Calulates a liner Fit for all values ...
     * @param x_min
     * @param x_max
     * @return
     */
    public SimpleRegression linFit(double x_min, double x_max) throws Exception {
        
        SimpleRegression regression = new SimpleRegression();

        //System.out.println("[ " + x_min + ", " + x_max +" ] " );
        Vector<Double> x = new Vector<Double>();
        Vector<Double> y = new Vector<Double>();
        double m = 0.0;
        int counter = 0;

        double maks = this.getMaxX();

        //if ( x_max > maks ) throw new Exception(" Reihe zu kurz. xMax=" + maks + ", x_max=" + x_max );

        for( int i = 0; i < this.xValues.size(); i++ ) {

           Double x1 = (Double)xValues.elementAt(i);
           if ( x1 >= x_min && x1 <= x_max ) {
               double ax = x1;
               double ay = ((Double)yValues.elementAt(counter)).doubleValue();
               regression.addData( ax,ay) ;
               //System.out.println(ax + " , " + ay );
           }
           counter++;
        }

//
//        System.out.println(regression.getIntercept());
//        System.out.println(regression.getSlope());
//        System.out.println(regression.getSlopeStdErr());

        return regression;
    }

 


    public Messreihe[] split(int length, int anzahl) {
        Messreihe[] rows = new Messreihe[anzahl];
        Enumeration en = yValues.elements();

        int c = 0;
        for( int i = 0; i <  anzahl; i++ ) {
            Messreihe mr = new Messreihe( this.label+" ("+i+")" );
            mr.setLabel(""+i);

            double puffer = 0.0;
            double last = 0.0;

            for( int j = 0; j < length; j++ ) {
                try {
                    puffer = (Double)en.nextElement();
                    mr.addValue( puffer );
                    last = puffer;
                }
                catch( NoSuchElementException e ) { 
  //                  mr.addValue(last);
                    c++;
                }
            }
            mr.calcAverage(); // Rq
            if ( c > 0 ) {
                mr.addComment(  c + " fehlende Werte mit RQ=" + mr.getAvarage() + " ersetzt." );
                System.out.println( mr.getLabel() + " >>> " + c + " fehlende Werte mit RQ=" + mr.getAvarage() + " ersetzt." );
                for( int d = 0 ; d < c ; d++ ){ 
                    mr.addValue( mr.getAvarage() );
                }
            }
            rows[i] = mr;
        };
        return rows;
    }
    
    public Messreihe shrinkX(double min, double max) {
        Vector<Double> x = new Vector<Double>();
        Vector<Double> y = new Vector<Double>();
        
        Enumeration en = yValues.elements();

        int i = 0;
        while ( en.hasMoreElements() ) {
            double yv = (Double)en.nextElement();
            double xv = (Double)xValues.elementAt(i);
            i++;
            
            if( xv>=min && xv <= max ) {
                x.add( xv );
                y.add( yv );
            }
        }    

        Messreihe mr = new Messreihe( this.label+" shrinked("+min+", "+max+")" );
 
        for( int j = 0; j < x.size(); j++ ) {
            mr.addValuePair( (Double)x.elementAt( j ), (Double)y.elementAt( j ));
        }
        return mr;
    }

    public StringBuffer sbComments = new StringBuffer();
    public Vector<String> comments = new Vector<String>();
    public void addComment(String string) {
        sbComments.append("# " + string + "\n");
        comments.add( string );
    }

    public void scaleXto(int i) {
        Enumeration<Double> en = this.xValues.elements();
        double max = getMaxX();
        double f = (double)i / max;
        int x = 0;
        while ( en.hasMoreElements() ) {
            Double v = en.nextElement();
            v=v*f;
            xValues.setElementAt(v, x);
            x++;
        }
    }

    public boolean hasNAN_values() {
        boolean back = false;
        Enumeration<Double> en = this.getYValues().elements();
        while( en.hasMoreElements() ) {
            Double d = en.nextElement();
            if ( Double.isNaN( d.doubleValue() ) ) back = true;
        };
        return back;
    };

    public void checkKonsistenz() {
        System.out.println( xValues.size() + " " + yValues.size() );

    };

    public void show() {
        


        
    }

    public Messreihe getProfile() {

        Messreihe mr = new Messreihe();
        mr.setLabel( getLabel() + "_Profil");

        int len = this.xValues.size();

        // mean value of data
        this.calcAverage();
        double mw = this.getAvarage();

        // calc profile from data
        double prsum = 0.0;
        for (int i = 0; i < len; i++) {
            prsum = prsum + ( (Double)this.yValues.elementAt(i) - mw);
            mr.addValuePair(i, prsum);
        }

        return mr;
    };

    /**
     * Es wird vorausgesetzt und geprüft, dass alle x-Werte identisch sind sonst
     * gibt es eine Exception.
     *
     * @param rows
     * @return
     */
    public static Messreihe calcAveragOfRows( Vector<Messreihe> rows ) throws Exception{
        System.out.println( ">>> AVG: " + rows.size() );
        Messreihe r = new Messreihe();
        r.setLabel("MW aus " + rows.size() + " Reihen");
        r.setLabel_X( rows.elementAt(0).getLabel_X() );
        r.setLabel_Y( rows.elementAt(0).getLabel_Y() );

        // i Werte werden betrachtet ...
        int i = rows.elementAt(0).xValues.size();
        int j = rows.size();
        int a = 0;
        int c = 0;
        while( a < i ) {
         
            // System.out.println( "> Stelle: a=" + a + " von i=" + i );
            
            Messreihe ref = rows.elementAt(0);

            Double vx0 = (Double)ref.xValues.elementAt(a);

            double sum = (Double)ref.yValues.elementAt(a);

            for( int z=1; z<j; z++) {
                
                try{
                    Messreihe vergl = rows.elementAt(z);
                    // System.out.println( "> Reihe: z=" + z + " von j=" + j );

                    Double vxn = (Double)vergl.xValues.elementAt(a);
                    double vyn = (Double)vergl.yValues.elementAt(a);

                    String info="\n z=" + z + ", a=" + a;

    //                if ( vx0.doubleValue() != vxn.doubleValue() )
    //                    throw new Exception( " x-values are not equal X0=" + vx0 + " != X"+z+"="+vxn + info);

                    sum = sum + vyn;
                    c++;
                }
                catch( Exception ex ) { 
                    
                }

            }
            
            double mw = sum / (double)c;
            
            r.addValuePair(vx0, mw);

            a++;
        }
        return r;
    };

    public Messreihe[] splitInto(int bin) {

        int l = this.xValues.size();
        int z = l / bin;

        return split(bin, z);
    }

    public Messreihe subtractAverage() {

        this.calcAverage();

        double av = this.getAvarage();
        // System.out.println("> subtrahiere den Mittelwert von Reihe : " + this.getLabel() + " mw=" + av );

        int c = 0;
        Messreihe mr = new Messreihe();
        Enumeration<Double> en = this.yValues.elements();
        while ( en.hasMoreElements() ) {
            double v = en.nextElement();
            v = v - av;
            mr.addValuePair( (Double)xValues.elementAt(c), v );
            c++;
        }
        return mr;
    }

    public void append(Messreihe mr0) {
        for( int i = 0; i < mr0.getSize()[0]; i++ ) {
            this.addValuePair( (Double)mr0.getXValues().elementAt(i),  (Double)mr0.getYValues().elementAt(i) );
        }
    }


    public void supressAtStart( int length, double value ) {
        // wir kürzen hier nicht die Access-Reihe, wir setzen die Werte nur auf 0;
            for( int i = 0; i < length; i++ ) {
                yValues.setElementAt( value ,i );
            }
    }

    
    public Messreihe combineAsXWithY(Messreihe mrY) {
        Messreihe mr = new Messreihe();
        for( int i = 0; i < xValues.size(); i++ ) {
            mr.addValuePair( (Double)yValues.elementAt(i), (Double)mrY.yValues.elementAt(i) );
        }
        return mr;
    }

    public Messreihe abs() {
        Messreihe mr = new Messreihe();
        mr.setLabel( "abs( " + this.getLabel() + ") ");
        int c = 0;

        Enumeration<Double> en = this.yValues.elements();
        while ( en.hasMoreElements() ) {
            double v = en.nextElement();
            v = Math.abs(v);
            mr.addValuePair( (Double)xValues.elementAt(c), v );
            c++;
        }
        return mr;

    }

    public Hashtable<Double,Vector<Double>> get_Y_hashedData() {
        Hashtable<Double,Vector<Double>> h = new Hashtable<Double,Vector<Double>>();
        int i = 0;
        for( Object x : this.xValues ) {
            Double y = (Double)yValues.elementAt(i);
            Vector<Double> ids = h.get(y);
            if ( ids == null ) {
                ids = new Vector<Double>();
                h.put(y, ids);
            }
            ids.add((Double) x);
            i++;
        }
        return h;
    }

    public Vector<ValuePair> getValuePairs() {
        double[][] d = this.getData();
        System.out.println( this.xValues.size() );
        Vector<ValuePair> vps = new Vector<ValuePair>();
        for( int i= 0; i < this.xValues.size(); i++ ) {
            ValuePair vp = new ValuePair();
            vp.x = d[0][i];
            vp.y = d[1][i];
            vps.add(vp);
            // System.out.println( vp );
        }
        return vps;
    };

    public void addValuePair(ValuePair vp) {
        this.addValuePair( vp.x, vp.y );
    }

    public void supressAtEnd(int START_CUTTOF_LENGTH, double value) {

        // wir kürzen hier nicht die Access-Reihe, wir setzen die Werte nur auf 0;
            for( int i = START_CUTTOF_LENGTH; i < this.yValues.size(); i++ ) {
                yValues.setElementAt( value ,i );
            }
   
    }

    public void shuffleXValues() {
        Collections.shuffle( this.xValues );
    }
    
    /**
     * Use Collections for shuffelfing the content of
     * the yValues Vector.
     */
    public void shuffleYValues() {
        Collections.shuffle( this.yValues );
        // System.out.println( "shuffeld ...");
        
    }
    
        /**
     * Use Collections for shuffelfing the content of
     * the yValues Vector.
     */
    public void shuffleYValues( int max ) {
        
        int i = 0;
        while( i < max ) {
            Collections.shuffle( this.yValues );
            i++;
        }
    }

    public Messreihe cutOut(int begin, int ende) {
       Messreihe r = new Messreihe();
       // wir kürzen hier nicht die Access-Reihe, wir setzen die Werte nur auf 0;
       for( int i = begin; i < ende; i++ ) {
              if ( i < xValues.size() ) {
                  double x = (Double)xValues.elementAt( i );
                  double y = (Double)yValues.elementAt( i );
                  r.addValuePair(x, y);
           }
       }

       return r;
    }

    /**
     * Für das automatische Speichern in einem Projekt-Verzeichnis.
     * 
     */
    String filename = null;
    public void setFileName(String string) {
        filename = string;
    }

    public String getFileName() {
        return filename;
    }

    public void addValues(Vector<Double> dataRawTIMES) {
        for( double da : dataRawTIMES ) {
            this.addValue(da);
        }
    }

    public Messreihe calcLogLog() {
        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() + "_LogLog" );
        
        int c = 0;

        Enumeration<Double> en = this.yValues.elements();
        while ( en.hasMoreElements() ) {
            double v = en.nextElement();
            v = Math.abs(v);
            
            double lx = Math.log10( (Double)xValues.elementAt(c) );
            double ly = Math.log10( (Double)yValues.elementAt(c) );
            
            mr.addValuePair( lx, ly );
            c++;
        }
        return mr;
        
    }

    public static Messreihe createFromStringLine( String s1 ) { 
        Messreihe m = new Messreihe();
        StringTokenizer t1 = new StringTokenizer( s1 , ";" );
        while( t1.hasMoreTokens() ) { 
            String s = t1.nextToken();
            Double y = Double.parseDouble(s);
            m.addValue(y);
        }
        return m;
    };

    public Messreihe normalizeToStdevIsOne() {
        Messreihe mrNeu = DatasetNormalizationTool.normalizeToMWZeroANDSTDevONE(this);
        mrNeu.xLabels = this.xLabels;
        mrNeu.label = mrNeu.label + " (stdv=1.0;mw=0.0)";
        mrNeu.label_X = this.label_X;
        mrNeu.label_Y = this.label_Y;
        return mrNeu;
    }

    // einen Link hinzufügen ...
    public void addValue(double d, String[] label) {
        this.addValue(d);
        this.hashValueByLabel( label[0]+"_"+label[1] , d);
        this.xLabels.add( label );
    }
    
        // einen Link hinzufügen ...
    public void addValue(double d, String label) {
        this.addValue(d);
        this.hashValueByLabel( label , d);
        this.xLabels2.add( label );
    }

    public Hashtable hashedValues = new Hashtable<String,Double>();
    public void hashValueByLabel(String string, double d) {
        hashedValues.put(string, d);
    }
    
    public Hashtable<Date, Double> dateHash;
    public void hashValueByDate(Date date, double d) {
        dateHash.put(date, d);
    }


    public Messreihe limitTo(int i) {
        Messreihe mr = copy( i );
        return mr;
    }

    public void mapDateHashedValuesToRow() {
        List<Date> l = getOrderedDateKeys();
        Iterator it = l.iterator();
        while( it.hasNext() ) { 
            Date d = (Date)it.next();
            double v = dateHash.get( d );
            //System.out.println( d + " ==> " + v );
            this.addValue( v );
        };
        
    }
    
    public List<Date> getOrderedDateKeys(Date begin, Date end) {
        Enumeration<Date> s = dateHash.keys();
        List<Date> l = new ArrayList<Date>();
        while( s.hasMoreElements() ) { 
            Date now = s.nextElement();
            if ( now.after(begin) && now.before(end) ) l.add( now );
        };
        Collections.sort(l);
        // System.out.println( l.size() );
        return l;
    }

    public List<Date> getOrderedDateKeys() {
        Enumeration<Date> s = dateHash.keys();
        List<Date> l = new ArrayList<Date>();
        while( s.hasMoreElements() ) { 
            l.add(s.nextElement());
        };
        Collections.sort(l);
        // System.out.println( l.size() );
        return l;
    }

    public void mapDataToDateHash(Calendar fd) {
        // alle Werte in den Date-Hash mappen ...
        this.dateHash.clear();        
        for( Object o : yValues ) { 
            Double v = (Double)o;
            this.hashValueByDate( fd.getTime(), v);
            // System.out.println( "DATE-MAPPER: " + fd.getTime().toString() + " ~~~~ >>> " + v );
            fd.add( Calendar.DAY_OF_MONTH, 1);        
        }
    }

    /**
     * 
     * We just extract data from the list 
     * 
     * @param liste 
     */
    public void filterAndMapBack(List<Date> liste) {
        // System.out.println( l );
        this.yValues = new Vector<Double>();
        this.xValues = new Vector<Double>();
        
        Iterator it = liste.iterator();
        while( it.hasNext() ) { 
            Date d = (Date)it.next();
            Double v = dateHash.get( d );
            if ( v != null ) this.addValue( v );
            else this.addValue(0.0);
        }
    }
    
    public void mapToStartingDate( int year, int month, int day ) { 
        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set( year, month-1, day );
        mapDataToDateHash(cal);    
    }

    public double getMinY() {
        double[][] data = this.getData();
        double[] dy = data[1];
        return StdStats.min( dy );
    
    }

    public String getY_values_as_Line() {
        StringBuffer s = new StringBuffer();
        for( Object y : this.yValues ) { 
            double yv = (Double)y;
            s.append( yv + "\t" );
        }
        return s.toString();
    }

    String descr = null;
    public void setDescription(String string) {
        descr = string;
    }
    public String getDescription() {
        return descr;
    }

    public Messreihe getYLogData() {
            
        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            double v = 0.0;
            double y = (Double)yValues.elementAt(i);
            if ( y > 0.0 ) v = Math.log( y ) ;
            mr.addValuePair( (double)i , v );
        }
        return mr;
    
    }

    String identifier = null;
    public void setIdentifier(String toString) {
         identifier = toString;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int[] getYData_as_INT() {
        double[] d = this.getYData();
        int[] i = new int[ d.length ];
        int j = 0;
        for( double dd : d ) { 
            i[j] = (int)dd;
            j++;
        }
        return i;
    }

    
    public long getTime_t0() {
        return t0;
    }    
 

    public double getSamplingRate() {
        return samplingRate;
    }

 
    long t0 = -1;
    
    double samplingRate = 1.0;

    public void add_value_to_Y(double v) {
        Vector y = new Vector();
        Enumeration en = this.yValues.elements();
        while (en.hasMoreElements()) {
            y.add(Double.valueOf(((Double) en.nextElement()).doubleValue() + v));
        }
        this.yValues = y;
    }

    public Messreihe log() {
     
        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            double y=0.0;
            double a = (Double)yValues.elementAt(i);
            if ( a == 0 ) y = 0.0;
            else
                y = Math.log10( a );
            mr.addValuePair( (double)i , y );
        }
        return mr;
    
    }

      public Messreihe subtractFromX(double d) {
        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            double x = (Double)xValues.elementAt(i);
            double y = (Double)yValues.elementAt(i);

            mr.addValuePair( x - d , y );
        }
        return mr;    
    
    }
      
    public static Messreihe calcAVOfRows(Vector<Messreihe> grIWL, Vector<Messreihe> grCN, String l) {
        Messreihe r = new Messreihe();
        int z = 0;
        
        for( Messreihe mr : grIWL ) { 
            r = r.add(mr);
            z++;
        }
        
        for( Messreihe mr : grCN ) { 
            r = r.add(mr);
            z++;
        }
        
        r.divide_Y_by(z);
        
        System.err.println( "> nr of rows added: " + z );
        r.setLabel( l );
        return r;
    }  
      
    public static Messreihe calcSumOfRows(Vector<Messreihe> grIWL, Vector<Messreihe> grCN, String l) {
        Messreihe r = new Messreihe();
        int z = 0;
        
        for( Messreihe mr : grIWL ) { 
            r = r.add(mr);
            z++;
        }
        
        for( Messreihe mr : grCN ) { 
            r = r.add(mr);
            z++;
        }
        System.err.println( "> nr of rows added: " + z );
        r.setLabel( l );
        return r;
    }

    public Messreihe divideXBy(double d) {
        Messreihe mr = new Messreihe();
        mr.setLabel( this.getLabel() );
        mr.binning = this.binning;
        mr.decimalFormat_STAT = this.decimalFormat_STAT;
        mr.decimalFormat_X = this.decimalFormat_X;
        mr.decimalFormat_Y = this.decimalFormat_Y;
        mr.status = new StringBuffer();
        mr.label_X = this.label_X;
        mr.label_Y = this.label_Y;
        int max = this.yValues.size();

        for( int i = 0; i < max ; i++ ) {
            double x = (Double)xValues.elementAt(i);
            double y = (Double)yValues.elementAt(i);

            mr.addValuePair( x / d , y );
        }
        return mr;        
    }

    public void addProperty(String k, int length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Hashtable<String,String> props = new Hashtable<String,String>();
            
    public void addProperty(String k, String v) {
        
        props.put( k , v );
        
    }
    
    
    /**
     * Cardinality as in Aris et al. (Aris2005) ...
     * 
     * @return 
     */
    public int getCardinality() {
        return this.xValues.size();
    }
 
    

    
}
