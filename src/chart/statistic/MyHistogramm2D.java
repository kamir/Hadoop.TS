package chart.statistic;

import data.io.IsNotZeroLineSelector;
import data.io.MessreihenLoader;
import data.series.Messreihe;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import chart.panels.ChartPanel3;

/**
 * Eine Tabelle in n Spalten wird mittels log-Binning in ein 2D Histogramm 
 * umgewandelt.
 * 
 * Dabei erfolgt das Erzeugen der BINS mit einem Faktor f und einer
 * maximalen Zahl, die noch in ein BIN einsortiert werden kann.
 * 
 * Die BIN-Matrix ist quadratisch.
 * 
 * Alle Werte Paare werden dann in das durch BIN(x) und BIN(y) bestimmte
 * Feld hineingezählt.
 * 
 * Das Bild der Spaltensummen und Zeilensummen muss der normalen 
 * Degree-Distribution entsprechen. 
 * 
 * @author kamir
 */
public class MyHistogramm2D {

    static boolean debug = true;
    static String delim = ",";
    
    public static int limit = 999999999; 
    
    /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @return
     */
    static double[] widthOfBin = null;
    static double[] widthOfBinCounted = null;
    static double[] lowerBorder = null;
    static double[] conuterBin = null;
    static double[] conuterBin2 = null;
    
    static int logSkipped = 0;
    static int logUsed = 0;

    private static boolean doNotUse(String line) {
        if ( filter == null ) {
            return false;
        }    
        else { 
            return filter.doSelectLine(line, delim);
        }
    }
        
    static IsNotZeroLineSelector filter = null;
        
    public static double[][] loadHistogram2D(
            File fn, int _spalteX, int spalteY, int spalteZ, 
            double factor, double maxY) {

        int[][] dataF = null;
        double[][] dataB = null;
        
        

        //
        // Anzahl der BINS
        //
        double z = 1.0;
        int c = 0;
        while (z < maxY) {
            z = z * factor;
            // System.err.println(c + "\t" + z);
            c++;
        }

        widthOfBin = new double[c];
        lowerBorder = new double[c];
        conuterBin = new double[c];
        conuterBin2 = new double[c];
        widthOfBinCounted = new double[c];

        dataF = new int[c][c];
        dataB = new double[c][c];
        for (int i = 0; i < c; i++) {
            widthOfBin[i] = 0.0;
            conuterBin[i] = 0.0;
            conuterBin2[i] = 0.0;
            widthOfBinCounted[i] = 0.0;
            
            for (int j = 0; j < c; j++) {
                dataF[i][j] = 0;
                dataB[i][j] = 0.0;
            }
        }


        //
        // BIN Breiten ermitteln ...
        //
        z = 1;
        c = 0;
        double b = 1.0;
        while (z < maxY) {
            z = z * factor;
            widthOfBin[c] = z - b;
            lowerBorder[c] = b;

            b = z;
            // System.err.println(c + "\t" + widthOfBin[c]);
            c++;
        }
        
        // und nun mal mittels auszählen die Breite ermitteln ...
        initBinWidth( maxY );
        
        System.out.println("### File: " + fn.getAbsolutePath());
      

        BufferedReader br = null;

        int skipped = 0;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            while (line.startsWith("#") || line.length() < 1 || doNotUse( line ) ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println(
                    "> skipped z=" + skipped + " lines in file: " + fn);
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if (delim != null) {
                st = new StringTokenizer(line, delim);
            }

            int zahlSpalten = 5; //st.countTokens();
            System.out.println(
                    "Zahl Spalten in der Datei (" + fn + ") : " + zahlSpalten);

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int limitCounter = 0;
            
            boolean notLimited = true;
            
            while (br.ready() && notLimited) {
                line = br.readLine();
                limitCounter++;
                if (!line.startsWith("#") && 
                        limitCounter < limit && 
                        line.length() > 0 &&
                        doUse( line ) ) {
                    
                    if ( limitCounter % 10000 == 0 ) 
                        // System.out.println( limitCounter + "\n");
//                    
//                    line = line.replaceAll("\"", "");
//                    
//                    while ( line.contains(",,"))
//                    {
//                        line = line.replaceAll(",,", ",0,");
//                    }
                    
                    if (delim != null) {
                        st = new StringTokenizer(line, delim);
                    } 
                    else {
                        st = new StringTokenizer(line);
                    }

                    int i = 0;
                    while (st.hasMoreElements()) {
                        data[i] = st.nextToken();
                        //System.out.println( i + " [" + data[i] + "]" );
                        i++;
                    }
                    
                    // wir zählen ab 1 ....
                    String sx = data[_spalteX - 1];
                    String sy = data[spalteY - 1];
                    String sz = data[spalteZ - 1];
                    Double dx = Double.parseDouble(sx);
                    Double dy = Double.parseDouble(sy);
                    Double dz = Double.parseDouble(sz);

                    //System.out.print(line + " : "+dx+" "+dy+" "+dz+" ... ");

                    boolean notCorrectBin = true;
                    
                    int zi = getBin(dy);
                    int zj = getBin(dz);
                    
                    dataF[zi][zj] = dataF[zi][zj] + 1;
                    
                    //System.out.print( "("+ limitCounter +")\t" + dataF[zi][zj] );
                    //System.out.println( );
                    
                }
                if ( limitCounter > limit ) notLimited = false;
            }
            br.close();
        } catch (Exception ex) {
            Logger.getLogger(
                    MessreihenLoader.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(
                        MessreihenLoader.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        // zusammenfassen ...
//      for (int j = 1; j < conuterBin.length; j++) {
//          double x = lowerBorder[j] + widthOfBin[j] * 0.5;
//          double y = conuterBin[j] / widthOfBin[j];  // DEGREE DISTRIBUTION
//          double y2 = conuterBin[j] / conuterBin2[j]; // DICHTE DER ZEITREIHE
//
//           System.out.println(" >>> use: " + x + " \t " + y + "\t --> " + 
//                  conuterBin[j] + "\t" + conuterBin2[j]);
//
//           mr.addValuePair(x, y2);
//        }



//      System.out.println(logSkipped/(logSkipped + logUsed) + "% verworfen");

        return normalize( dataF , dataB, c);
        // return justMapToDouble( dataF , dataB, c);
    }

    private static int getBin(double d) {
        //System.out.print( "--- d=" + d );
        
        boolean notCorrectBin = true;
        
        int z = 0;
        int b = 0;
        
        while (z < conuterBin.length) {

            if (d > lowerBorder[z]) {
               b = z;
            }    
            z++;      
        }
        //System.out.print( "bin=>" + b );
        
        return b;
    }

    private static void head(File f, int i) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader( new FileReader( f ));
        boolean stop = false;
        int j = 0;
        while ( br.ready() && !stop ) { 
            String line = br.readLine();
            System.out.println( "["+j+"] " +  line );
            j = j + 1;
            if ( j == i ) stop = true;
        }
    }
    
        public static void main(String[] args) throws IOException {
        
        String pathoffset = "";
        
        String t = "1,1,,,0";
        while ( t.contains(",,"))
        {
            t = t.replaceAll(",,", ",0,");
        }
        System.out.println(t);
        
        if( args.length > 0 ) pathoffset = args[0];
        
        String workpath = pathoffset + "P:/DATA/";
        System.out.println( " >> Pathoffset: " + pathoffset );
        
        
        String fileNameIN = "MasterJoin_Degrees_for_Links_and_Redirects";
        String fileNameOUT = "MATRIX_Out_vs_In_DEGREE_redirects.dat";
        String fileNameOUT2 = "MATRIX_Out_vs_In_DEGREE_log_redirects.dat";
        
        File f = new File( workpath + fileNameIN);
        head( f, 10 );

        int sX = 4;
        int sY = 5;
        
        String label = sX+", "+sY + " : " + f.getAbsolutePath();
        
        
        javax.swing.JOptionPane.showMessageDialog(
                null, " >> Pathoffset: " + pathoffset );
        
        double[][] d = loadHistogram2D(f, 1, sX, sY, 1.1, 10000.0);
        
        ChartPanel3 pan = new ChartPanel3(d);
        
        FileWriter fw = new FileWriter( fileNameOUT );
        pan.store( fw );

        FileWriter fw2 = new FileWriter( fileNameOUT2 );
        pan.storeLog( fw2 );

        JFrame frame = new JFrame("ColorPan -> " + label);
        frame.getContentPane().add( pan );
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static double[][] loadHistogram2D(File file, int i, int i0, int i1, double d, double d0, IsNotZeroLineSelector filter1) {
        filter = filter1;
        return loadHistogram2D( file, i, i0, i1, d, d0 );        
    }

    private static boolean doUse(String line) {
        return !doNotUse(line);
    }

    private static void initBinWidth(double maxY) {
        for( int i = 0; i < maxY; i++ ) {
            int index = getBin( i * 1.0 ); 
            widthOfBinCounted[ index ] = widthOfBinCounted[ index ] + 1;  
        }
        System.out.println( ">>> Binbreite wurde ausgezählt ... ");
        for( int i = 0; i < widthOfBinCounted.length; i++ ) {
            System.out.println("("+i+") " + widthOfBinCounted[ i ] );  
        }
    }

    private static double[][] normalize(int[][] dataF, double[][] dataB, int c) {

        for (int i = 0; i < c; i++) {
            for (int j = 0; j < c; j++) {
                double z =  ( widthOfBinCounted[i] * widthOfBinCounted[j] );
                if ( z == 0.0 ) { 
                    System.out.println(">> BEEP " );
                    // dataB[i][j] = 5000.0;
                } 
                else { 
                    dataB[i][j] = dataF[i][j] / z ;
                }    
            }
        }        
        System.out.println(">> Normalized by counted Bin-Width ... ");
        return dataB;
    }

    private static double[][] justMapToDouble(int[][] dataF, double[][] dataB, int c) {
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < c; j++) {
                dataB[i][j] = dataF[i][j] * 1.0;
            }
        }        
        System.out.println(">> Not normalized by counted Bin-Width ...  just counted Values ... ");
        return dataB;
    }
        
        
}


