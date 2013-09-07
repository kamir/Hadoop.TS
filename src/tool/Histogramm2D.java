package tool;


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

/**
 * Eine Tabelle in n Spalten wird mittel log-Binning in ein 2D Histogramm 
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
public class Histogramm2D {

    static boolean debug = true;
    static String delim = ",";
    
    static int limit = 999999999;

    public static void main(String[] args) throws IOException {
        
        String pathoffset = "";
        
        if( args.length > 0 ) pathoffset = args[0];
        String workpath = pathoffset + "P:/ARBEIT/WIKI/degree distribution/";
        System.out.println( " >> Pathoffset: " + pathoffset );
        javax.swing.JOptionPane.showMessageDialog(
                null, " >> Pathoffset: " + pathoffset );
        String fileNameIN = "Workbook_Out_vs_In_DEGREE_Joined.csv";
        String fileNameOUT = "MATRIX_Out_vs_In_DEGREE.dat";
        String fileNameOUT2 = "MATRIX_Out_vs_In_DEGREE_log.dat";
        
        
        File f = new File( workpath + fileNameIN);
        data.io.datafile.DataFileTool.head( f , 10 );

        
        int sX = 3;
        int sY = 4;
        
        String label = sX+", "+sY + " : " + f.getAbsolutePath();
        
        int[][] d = loadHistogram2D(f, 1, sX, sY, 1.1, 10000.0);
        ColorPan pan = new ColorPan(d);
        
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
 
    
    /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @return
     */
    static double[] widthOfBin = null;
    static double[] lowerBorder = null;
    static double[] conuterBin = null;
    static double[] conuterBin2 = null;
    
    static int logSkipped = 0;
    static int logUsed = 0;
        
    public static int[][] loadHistogram2D(
            File fn, int _spalteX, int spalteY, int spalteZ, 
            double factor, double maxY) {

        int[][] dataF = null;

        //
        // Anzahl der BINS
        //
        double z = 1.0;
        int c = 0;
        while (z < maxY) {
            z = z * factor;
            System.err.println(c + "\t" + z);
            c++;
        }

        widthOfBin = new double[c];
        lowerBorder = new double[c];
        conuterBin = new double[c];
        conuterBin2 = new double[c];

        dataF = new int[c][c];
        for (int i = 0; i < c; i++) {
            widthOfBin[i] = 0.0;
            conuterBin[i] = 0.0;
            conuterBin2[i] = 0.0;
            for (int j = 0; j < c; j++) {
                dataF[i][j] = 0;
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
            System.err.println(c + "\t" + widthOfBin[c]);
            c++;
        }

        System.out.println("File: " + fn.getAbsolutePath());
      

        BufferedReader br = null;

        int skipped = 0;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            while (line.startsWith("#") || line.length() < 1) {
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

            int zahlSpalten = st.countTokens();
            System.out.println(
                    "Zahl Spalten in der Datei (" + fn + ") : " + zahlSpalten);

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int limitCounter = 0;

            while (br.ready()) {
                line = br.readLine();
                if (!line.startsWith("#") && 
                        limitCounter < limit && 
                        line.length() > 0) {
                    
                    limitCounter++;
                    
                    line = line.replaceAll("\"", "");
            
                    if (delim != null) {
                        st = new StringTokenizer(line, delim);
                    } 
                    else {
                        st = new StringTokenizer(line);
                    }

                    int i = 0;
                    while (st.hasMoreElements()) {
                        data[i] = st.nextToken();
                        // System.out.println( i + " [" + data[i] + "]" );
                        i++;
                    }
                    // wir zählen ab 1 ....
                    String sx = data[_spalteX - 1];
                    String sy = data[spalteY - 1];
                    String sz = data[spalteZ - 1];
                    Double dx = Double.parseDouble(sx);
                    Double dy = Double.parseDouble(sy);
                    Double dz = Double.parseDouble(sz);

                    System.out.print(line + " : "+dx+" "+dy+" "+dz+" ... ");

                    boolean notCorrectBin = true;
                    
                    int zi = getBin(dy);
                    int zj = getBin(dz);
                    
                    dataF[zi][zj] = dataF[zi][zj] + 1;
                    
                    System.out.print( "\t" + dataF[zi][zj] );
                    System.out.println( );

                }
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

        return dataF;
    }

    private static int getBin(double d) {
        System.out.print( "--- d=" + d );
        
        boolean notCorrectBin = true;
        
        int z = 0;
        int b = 0;
        
        while (z < conuterBin.length) {

            if (d > lowerBorder[z]) {
               b = z;
            }    
            z++;      
        }
        System.out.print( "bin=>" + b );
        
        return b;
    }
}

class ColorPan extends JComponent {

    int[][] data = null;
    int width = 8;
    int height = 8;

    public void store( Writer out ) throws IOException { 
        BufferedWriter bw = new BufferedWriter( out );
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bw.write( data[i][j] + " " );
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    } 
    
    public void storeLog( Writer out ) throws IOException { 
        BufferedWriter bw = new BufferedWriter( out );
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bw.write( Math.log( data[i][j] ) + " " );
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    } 
    
    public ColorPan(int[][] _data) {
        data = _data;
        width = _data[0].length;
        height = _data[0].length;
    }

    public void paint(Graphics g) {

                Graphics2D g2d = (Graphics2D)g;
        
        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB);
        
        AffineTransform transformer = new AffineTransform();
	// transformer.translate(5,5);
	transformer.scale(6,6);
	
	g2d.setTransform(transformer);
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {                 
                image.setRGB(i, j, data[i][j] );               
            }             
        }
        
        g2d.drawImage(image, 10, 10, this);
    
    }
}
