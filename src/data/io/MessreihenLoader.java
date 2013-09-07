/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.io;

import data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessreihenLoader {

    public static boolean debug = true;
    public static String delim = ",";

    public static MessreihenLoader getLoader() {
        return new MessreihenLoader();
    };

    public static boolean checkFileAccess( String name ) {
        boolean state = true;
        
        File f = new File( name );
        //System.out.println("> use selected file ... " + name );
        
        String info = f.getName();
        boolean error = false;
        if (!f.getName().endsWith(".txt") && !f.getName().endsWith(".dat")) {
            error = true;
            System.out.println("not .dat or .txt...");
        }
        if (!f.canRead()) {
            error = true;
            System.out.println("nicht lesbar...");
        };

        if (error) {
            System.out.println("Fehler ... MessreihenLoader.checkFileAccess().");
            state = false;
        }
        else {
            state = true;
        }
        return state;
    };

    
    /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @param fn
     * @param spalteX
     * @param spalteY
     * @return
     */
    public Messreihe _loadLogBinnedMessreihe( File fn , int _spalteX , int spalteY, double factor, double maxY  ) {

        int logSkipped = 0;
        int logUsed = 0;
        
        //
        // Anzahl der BINS
        //
        double z = 1.0;
        int c = 0;
        while( z < maxY ) {
            z = z * factor;
            System.err.println(c + "\t" + z );
            c++;
        }
        
        double[] widthOfBin = new double[c];
        double[] lowerBorder = new double[c];
        double[] conuterBin = new double[c];
        double[] conuterBin2 = new double[c];
        
        for( int i = 0; i < c; i++ ) { 
            widthOfBin[i] = 0.0;
            conuterBin[i] = 0.0;
            conuterBin2[i] = 0.0;
        }
        
        //
        // BIN Breiten ermitteln ...
        //
        z=1;
        c=0;
        double b = 1;
        while( z < maxY ) {
            z = z * factor;
            widthOfBin[c] = z - b;
            lowerBorder[c] = b;
            
            b = z;
            System.err.println(c + "\t" + widthOfBin[c] );
            c++;
        }
        
        Messreihe mr = new Messreihe();

        System.out.println("File: "+fn.getAbsolutePath());

        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) { 
            mr = null;
            
            return mr;
        };

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( "> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( delim != null ) {
                st = new StringTokenizer(line, delim );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( "Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;

            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                line = line.replace(',','.');
                if ( debug ) System.out.println( "> line=[" + line + "]" );

                if ( delim != null ) {
                    st = new StringTokenizer(line, delim );
                }
                else {
                    st = new StringTokenizer(line);
                };

                int i = 0;
                while ( st.hasMoreElements() ) {
                   data[i] = st.nextToken();
                   // System.out.println( i + " [" + data[i] + "]" );
                   i++;
                }
                // wir zählen ab 1 ....
                String sx = data[_spalteX-1];
                String sy = data[spalteY-1];
                Double dx = Double.parseDouble(sx);
                Double dy = Double.parseDouble(sy);

                // System.out.println( line + " : " + dx + " " + dy );
                boolean notCorrectBin = true;
                int zi = 1;
                
                while( notCorrectBin ) {
                    
                    if ( dx > lowerBorder[zi] ) { 
                        zi++;        
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                            notCorrectBin = false;
                        }
                    }
                    else { 
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                        }
                        else { 
                            logUsed++;
                            conuterBin[zi-1] = conuterBin[zi-1] + dy; 
                            conuterBin2[zi-1] = conuterBin2[zi-1] + 1; 
                            
                            notCorrectBin = false;
                            System.out.println( dx + "\t " + dy + "\t" + zi + "\t" + lowerBorder[zi]);
                        }
                    }
                }
                    
                
//                    mr.addValuePair(dx, dy);
//                       limitCounter++;

//                
                };

                

            };
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // zusammenfassen ...
        for( int j = 1; j < conuterBin.length ; j++ ) { 
            double x = lowerBorder[j] + widthOfBin[j] * 0.5;
            double y = conuterBin[j] / widthOfBin[j];  // DEGREE DISTRIBUTION
            double y2 = conuterBin[j] / conuterBin2[j]; // DICHTE DER ZEITREIHE
            
            System.out.println(" >>> use: " +  x + " \t " + y2 + "\t --> " + conuterBin[j] + "\t" + conuterBin2[j] );
            
            mr.addValuePair(x, y2);
        }
        
        System.out.println( logSkipped / ( logSkipped + logUsed ) + "% verworfen");
        return mr;
    };

    

    
    /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @param fn
     * @param spalteX
     * @param spalteY
     * @return
     */
    public Messreihe _loadLogBinnedMessreihe_DIV_BY_BINWIDTH( File fn , int _spalteX , int spalteY, double factor, double maxY  ) {

        int logSkipped = 0;
        int logUsed = 0;
        
        //
        // Anzahl der BINS
        //
        double z = 1.0;
        int c = 0;
        while( z < maxY ) {
            z = z * factor;
            System.err.println(c + "\t" + z );
            c++;
        }
        
        double[] widthOfBin = new double[c];
        double[] lowerBorder = new double[c];
        double[] conuterBin = new double[c];
        double[] conuterBin2 = new double[c];
        
        for( int i = 0; i < c; i++ ) { 
            widthOfBin[i] = 0.0;
            conuterBin[i] = 0.0;
            conuterBin2[i] = 0.0;
        }
        
        //
        // BIN Breiten ermitteln ...
        //
        z=1;
        c=0;
        double b = 1;
        while( z < maxY ) {
            z = z * factor;
            widthOfBin[c] = z - b;
            lowerBorder[c] = b;
            
            b = z;
            System.err.println(c + "\t" + widthOfBin[c] );
            c++;
        }
        
        Messreihe mr = new Messreihe();

        System.out.println("File: "+fn.getAbsolutePath());
//
//        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) { 
//            mr = null;
//            
//            return mr;
//        };

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( "> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( delim != null ) {
                st = new StringTokenizer(line, delim );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( "Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;

            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                                       
                // line = line.replace(',','.');
                
                if ( debug ) System.out.println( "> line=[" + line + "]" );

                if ( delim != null ) {
                    st = new StringTokenizer(line, delim );
                }
                else {
                    st = new StringTokenizer(line);
                };

                int i = 0;
                while ( st.hasMoreElements() ) {
                   data[i] = st.nextToken();
                   // System.out.println( i + " [" + data[i] + "]" );
                   i++;
                }
                // wir zählen ab 1 ....
                String sx = data[_spalteX-1];
                String sy = data[spalteY-1];
                Double dx = Double.parseDouble(sx);
                Double dy = Double.parseDouble(sy);

                // System.out.println( "LINE: " + line + " : " + dx + " " + dy );
                boolean notCorrectBin = true;
                int zi = 1;
                
                while( notCorrectBin ) {
                    
                    if ( dx > lowerBorder[zi] ) { 
                        zi++;        
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                            notCorrectBin = false;
                        }
                    }
                    else { 
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                        }
                        else { 
                            logUsed++;
                            conuterBin[zi-1] = conuterBin[zi-1] + dy; 
                            conuterBin2[zi-1] = conuterBin2[zi-1] + 1; 
                            
                            notCorrectBin = false;
                            // System.out.println( dx + "\t " + dy + "\t" + zi + "\t" + lowerBorder[zi]);
                        }
                    }
                }
                    
                
//                    mr.addValuePair(dx, dy);
//                       limitCounter++;

//                
                };

                

            };
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // zusammenfassen ...
        for( int j = 1; j < conuterBin.length ; j++ ) { 
            double x = lowerBorder[j] + widthOfBin[j] * 0.5;
            double y = conuterBin[j] / widthOfBin[j];  // DEGREE DISTRIBUTION
            //double y2 = conuterBin[j] / conuterBin2[j]; // DICHTE DER ZEITREIHE
            
            System.out.println(" >>> use: " +  x + " \t " + y + "\t --> " + conuterBin[j] + "\t" + conuterBin2[j] );
            
            mr.addValuePair(x, y);
        }
        
        if ( ( logSkipped + logUsed ) > 0 ) {
            System.out.println( ( logSkipped + logUsed ) + " counted ... ");
        
            System.out.println( logSkipped / ( logSkipped + logUsed ) + "% verworfen");
        }
        else { 
            System.out.println( "PROBLEMS in data !");
        }
        return mr;
    };
    
    
     /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @param fn
     * @param spalteX
     * @param spalteY
     * @return
     */
    public Messreihe _loadLogBinnedMessreihe_DIV_BY_BINWIDTH( File fn , int spalteX , int spalteY, double factor, double maxY, String delim , LineFilter filter ) {

        int logSkipped = 0;
        int logUsed = 0;
        
        //
        // Anzahl der BINS
        //
        double z = 1.0;
        int c = 0;
        while( z < maxY ) {
            z = z * factor;
            if ( debug ) System.err.println(c + "\t" + z );
            c++;
        }
        
        double[] widthOfBin = new double[c];
        double[] widthOfBinCounted = new double[c];
        
        double[] lowerBorder = new double[c];
        double[] conuterBin = new double[c];
        double[] conuterBin2 = new double[c];
        
        for( int i = 0; i < c; i++ ) { 
            widthOfBin[i] = 0.0;
            conuterBin[i] = 0.0;
            conuterBin2[i] = 0.0;
        }
        
        
        
        //
        // BIN Breiten ermitteln ...
        //
        z=1;
        c=0;
        double b = 1;
        while( z < maxY ) {
            z = z * factor;
            widthOfBin[c] = z - b;
            lowerBorder[c] = b;
            
            b = z;
            if ( debug ) System.err.println(c + "\t" + widthOfBin[c] );
            c++;
        }
        
        
        //     private static void initBinWidth(double maxY) {
        for( int i = 0; i < maxY; i++ ) {
            int index = getBin(i, lowerBorder, conuterBin );
            widthOfBinCounted[index] = widthOfBinCounted[index] + 1; 
        }
        System.out.println( ">>> Binbreite wurde ausgezählt ... ");
        for( int i = 0; i < widthOfBinCounted.length; i++ ) {
            System.out.println("("+i+") " + widthOfBinCounted[ i ] );  
        }
        
       
        
        Messreihe mr = new Messreihe();

        System.out.println("File: "+fn.getAbsolutePath());
//
//        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) { 
//            mr = null;
//            
//            return mr;
//        };

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( "> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            if ( filter != null ) { 
                line = filter.doFilterLine(line);
            }
            
            StringTokenizer st = new StringTokenizer(line);
            if ( delim != null ) {
                st = new StringTokenizer(line, delim );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( "Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;

            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                                       
                if ( filter != null ) { 
                    line = filter.doFilterLine(line);
                }
            

                
                if ( debug ) System.out.println( "> line=[" + line + "]" );

                if ( delim != null ) {
                    st = new StringTokenizer(line, delim );
                }
                else {
                    st = new StringTokenizer(line);
                };

                int i = 0;
                while ( st.hasMoreElements() ) {
                   data[i] = st.nextToken();
                   if ( debug ) System.out.println( i + " [" + data[i] + "]" );
                   i++;
                }
                
                // wir zählen ab 1 ....
                String sx = data[spalteX-1];
                String sy = data[spalteY-1];
                Double dx = Double.parseDouble(sx);
                Double dy = Double.parseDouble(sy);

                if ( debug ) System.out.println( line + " : " + dx + " " + dy );
                boolean notCorrectBin = true;
                int zi = 1;
                
                while( notCorrectBin ) {
                    
                    if ( dx > lowerBorder[zi] ) { 
                        zi++;        
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                            notCorrectBin = false;
                        }
                    }
                    else { 
                        if ( zi >= conuterBin.length ) {
                            logSkipped++;
                        }
                        else { 
                            logUsed++;
                            conuterBin[zi-1] = conuterBin[zi-1] + dy; 
                            conuterBin2[zi-1] = conuterBin2[zi-1] + 1; 
                            
                            notCorrectBin = false;
                            if ( debug ) System.out.println( dx + "\t " + dy + "\t" + zi + "\t" + lowerBorder[zi]);
                        }
                    }
                }
                    
                
//                    mr.addValuePair(dx, dy);
//                       limitCounter++;

//                
                };

                

            };
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // zusammenfassen ...
        for( int j = 1; j < conuterBin.length ; j++ ) { 
            double x = lowerBorder[j] + widthOfBin[j] * 0.5;
            
            double y = conuterBin[j] / widthOfBin[j];  // DEGREE DISTRIBUTION
            double y2 = conuterBin[j] / widthOfBinCounted[j]; // DICHTE DER ZEITREIHE
            
            // System.out.println(" >>> use: " +  x + " \t " + y + "\t --> " + conuterBin[j] + "\t" + conuterBin2[j] );
            
            mr.addValuePair(x, y2);
        }
        
        if ( ( logSkipped + logUsed ) > 0 ) {
            System.out.println( ( logSkipped + logUsed ) + " counted ... ");
        
            System.out.println( logSkipped / ( logSkipped + logUsed ) + "% verworfen");
        }
        else { 
            System.out.println( "PROBLEMS in data !");
        }
        return mr;
    };
    
        
    private static int getBin(double d, double[] lowerBorder, double[] conuterBin ) {
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



    /**
     * Es wird davon ausgegangen, dass keine Kopfzeile vorhanden ist und
     * alle Zeilen gleich viele Spalten haben.
     *
     * @param fn
     * @param spalteX
     * @param spalteY
     * @return
     */
    public Messreihe loadMessreihe_2( File fn , int spalteX , int spalteY ) {

        Messreihe mr = new Messreihe();

        System.out.println("File: "+fn.getAbsolutePath());

//        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) { 
//            mr = null;
//            
//            return mr;
//        };

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            
            br = new BufferedReader(new FileReader(fn));
            
            System.out.println(">>> File: "+fn.getAbsolutePath() + " " + fn.canRead() );
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( "> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( delim != null ) {
                st = new StringTokenizer(line, delim );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( "Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;

            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                line = line.replace(',','.');
                if ( debug ) System.out.println( "> line=[" + line + "]" );

                if ( delim != null ) {
                    st = new StringTokenizer(line, delim );
                }
                else {
                    st = new StringTokenizer(line);
                };

                int i = 0;
                while ( st.hasMoreElements() ) {
                   data[i] = st.nextToken();
                   // System.out.println( i + " [" + data[i] + "]" );
                   i++;
                }
                // wir zählen ab 1 ....
                String sx = data[spalteX-1];
                String sy = data[spalteY-1];
                Double dx = Double.parseDouble(sx);
                Double dy = Double.parseDouble(sy);

               // System.out.println( line + " : " + dx + " " + dy );
                mr.addValuePair(dx, dy);
                 limitCounter++;
                };

                

            };
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    };

    
    /**
    * Es soll eine Datei eingelesen werden, in der lediglich die y Werte stehen (also nur eine Spalte)
     */
    public Messreihe loadMessreihe_1( File fn ) {


        Messreihe mr = new Messreihe();

       if ( ! this.checkFileAccess( fn.getAbsolutePath() ) ) {
            mr = null;
            return mr;
        }

        BufferedReader br = null;
        mr.setLabel( fn.getName() );



        try {
            br = new BufferedReader(new FileReader(fn));
            String line = br.readLine();
            boolean goOn = true;
            while( goOn  ) {
                line = br.readLine();
                if ( line.startsWith("#") || line.startsWith(" ") || line.length() < 1 ) goOn=true;
                else goOn = false;
                System.out.println( line );
            }
            // und nun die richtige Zeile mit dem Header lesen
            line = br.readLine();
            br.close();

            StringTokenizer st = new StringTokenizer(line);
            int zahlSpalten = st.countTokens();
            System.out.println( "Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            double cnt=0;
            while (br.ready()) {

                line = br.readLine();
                if ( !line.startsWith("#") ) {

                    line = line.replace(',','.');
                    // System.out.println( "> [" + line + "]" );
                    st = new StringTokenizer(line);
                    int i = 0;
                    while ( st.hasMoreElements() ) {
                        data[i] = st.nextToken();
                        // System.out.println( i + " [" + data[i] + "]" );
                        i++;
                    }
                    // wir zählen ab 1 ....
                    String sy = data[0];
                    Double dx = cnt++;
                    Double dy = Double.parseDouble(sy);
//                    System.out.println( line + " : " + dx + " " + dy );
                mr.addValuePair(dx, dy);
                };

            };
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    };

    public static int limit = 1000000000;

    /**
     * lade x aus Spalte 1 und y aus Spalte 2
     * 
     * @param fn
     * @return
     */
    public Messreihe loadMessreihe( File fn ) {

        Messreihe mr = new Messreihe();

        mr.setLabel( fn.getName() );
        
        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) {
            mr = null;
            return mr;
        }

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        try {
            br = new BufferedReader(new FileReader(fn));
            int limitCounter = 0;
            while (br.ready()) {
                String line = br.readLine();
                if ( !line.startsWith( "#" ) && limitCounter < limit && line.length()>0 ) {
                    StringTokenizer st = new StringTokenizer(line);
                    String sx = (String)st.nextElement();
                    String sy = (String)st.nextElement();
                    Double dx = Double.parseDouble(sx);
                    Double dy = Double.parseDouble(sy);

                    mr.addValuePair(dx, dy);
                    limitCounter++;
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    };

    public Messreihe loadMessreihe_2(File f, int i, int i0, int _limit) {
        limit = _limit;
        System.out.println( ">>> WARNUNG >>>  LIMIT wird gesetzt auf limit=" + _limit);
        return loadMessreihe_2(f, i, i0);
    }

    public Messreihe loadMessreihe_2(File f, int i, int i0, String theDelim) {
        delim = theDelim;
        return loadMessreihe_2(f, i, i0);
    }

    public Messreihe loadMessreihe_zr_SC( File fn, int spalteY , String del ) {

        Messreihe mr = new Messreihe();
        
        System.out.println(">>> File: "+fn.getAbsolutePath() + " lese in Spalte Nr:=" + spalteY );

//        if ( !this.checkFileAccess( fn.getAbsolutePath() ) ) { 
//            mr = null;
//            
//            return mr;
//        };

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            
            br = new BufferedReader(new FileReader(fn));
            
            System.out.println(">>> File: "+fn.getAbsolutePath() + " " + fn.canRead() );
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( ">>> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( del != null ) {
                st = new StringTokenizer(line, del );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( ">>> Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;
            int x = 0;
            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                    line = line.replace(',','.');
                    if ( debug ) System.out.println( "> line=[" + line + "]" );

                    if ( del != null ) {
                        st = new StringTokenizer(line, del );
                    }
                    else {
                        st = new StringTokenizer(line);
                    };

                    int i = 0;
                    while ( st.hasMoreElements() ) {
                    data[i] = st.nextToken();
                    // System.out.println( i + " [" + data[i] + "]" );
                    i++;
                    }

                    x++;
                    // wir zählen ab 1 ....

                    String sy = data[spalteY-1];
                    Double dx = 1.0 * x;
                    Double dy = Double.parseDouble(sy);

                    // System.out.println( line + " : " + dx + " " + dy );
                    mr.addValuePair(dx, dy);
                    
                    StringTokenizer st2 = new StringTokenizer(data[0], "_" );
                    String lA1 = st2.nextToken();
                    String lA2 = st2.nextToken();
                
                    String[] aray = new String[2];
                    aray[0] = lA1;
                    aray[1] = lA2;
                    mr.xLabels.add( aray );
                    limitCounter++;
                } 
                else {
                    System.out.println( "> empty: " + line );
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    }
    
    public Messreihe loadMessreihe_For_LinkStrength( File fn, int spalteLabel1,int spalteLabel2,int spalteValue , String del ) {

        Messreihe mr = new Messreihe();
        
        System.out.println(">>> File: "+fn.getAbsolutePath() + " lese in Spalte Nr:=" + spalteValue );


        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            
            br = new BufferedReader(new FileReader(fn));
            
            System.out.println(">>> File: "+fn.getAbsolutePath() + " " + fn.canRead() );
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( ">>> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( del != null ) {
                st = new StringTokenizer(line, del );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( ">>> Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;
            int x = 0;
            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                    line = line.replace(',','.');
                    //if ( debug ) System.out.println( "> line=[" + line + "]" );

                    if ( del != null ) {
                        st = new StringTokenizer(line, del );
                    }
                    else {
                        st = new StringTokenizer(line);
                    };

                    int i = 0;
                    while ( st.hasMoreElements() ) {
                        data[i] = st.nextToken();
                        //System.out.println( i + " [" + data[i] + "]" );
                        i++;
                    }

                    x++;
                    
                    // wir zählen ab 1 ....
                    String labelA = data[spalteLabel1-1];
                    String labelB = data[spalteLabel2-1];
                    
                    String sy = data[spalteValue-1];

                    Double dy = Double.parseDouble(sy);

                    String[] aray = new String[2];
                    aray[0] = labelA;
                    aray[1] = labelB;
                    mr.addValue( dy , aray );
                    
                    limitCounter++;
                } 
                else {
                    System.out.println( "> empty: " + line );
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    }

    public Messreihe loadMessreihe_For_LinkStrength(File fn, int spalteLabel1, int spalteLabel2, ColumnValueCalculator mapper, String del) {
        Messreihe mr = new Messreihe();
        
        System.out.println(">>> File: "+fn.getAbsolutePath() + " lese mit mapper:=" + mapper.getName() );


        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            
            br = new BufferedReader(new FileReader(fn));
            
            System.out.println(">>> File: "+fn.getAbsolutePath() + " " + fn.canRead() );
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( ">>> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( del != null ) {
                st = new StringTokenizer(line, del );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( ">>> Zahl von Spalten in der Datei ("+ fn +") : " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;
            int x = 0;
            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                    line = line.replace(',','.');
                    if ( x ==0 ) System.out.println( "> line=[" + line + "]" );

                    if ( del != null ) {
                        st = new StringTokenizer(line, del );
                    }
                    else {
                        st = new StringTokenizer(line);
                    };

                    int i = 0;
                    while ( st.hasMoreElements() ) {
                        data[i] = st.nextToken();
                        //System.out.println( i + " [" + data[i] + "]" );
                        i++;
                    }

                    x++;
                    
                    // wir zählen ab 1 ....
                    
                    
                    String labelA = data[spalteLabel1-1];
                    String labelB = data[spalteLabel2-1];
                    Double dy = 0.0;
                    try {
                        dy = mapper.calcLinkStrength( data );
                    }
                    catch( Exception ex ){ 
                        System.out.println( "***>>>> line=[" + line + "]" );
                        ex.printStackTrace();
                    };

                    String[] aray = new String[2];
                    aray[0] = labelA;
                    aray[1] = labelB;
                    
                    mr.addValue( dy , aray );
                    
                    limitCounter++;
                } 
                else {
                    System.out.println( "> empty: " + line );
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    }

    public Messreihe _loadMessreihe_For_FD(File fn, TSValueCalculator mapper, String del, boolean logreturn )  {
        
        Messreihe mr = new Messreihe();

        mr.dateHash = new Hashtable<Date,Double>();
        
        System.out.println(">>> File: "+fn.getAbsolutePath() + " lese mit mapper:=" + mapper.getName() );

        BufferedReader br = null;
        mr.setLabel( fn.getName() );

        int skipped = 0;
        try {
            
            br = new BufferedReader(new FileReader(fn));
            
            System.out.println(">>> File: "+fn.getAbsolutePath() + " " + fn.canRead() );
            String line = br.readLine();
            while( line.startsWith("#") || line.length()<1 ) {
                line = br.readLine();
                skipped++;
            }
            System.out.println( ">>> skipped z=" + skipped + " lines in file: " + fn );
            br.close();

            StringTokenizer st = new StringTokenizer(line);

            if ( del != null ) {
                st = new StringTokenizer(line, del );
            };

            int zahlSpalten = st.countTokens();
            System.out.println( ">>> Zahl von Spalten in der Datei ("+ fn +") " + zahlSpalten );

            String[] data = new String[zahlSpalten];

            br = new BufferedReader(new FileReader(fn));
            int  limitCounter = 0;
            int x = 0;
            while (br.ready()) {
                line = br.readLine();
                if ( !line.startsWith("#") &&  limitCounter < limit && line.length() > 0) {
                    
                   
                    line = line.replace(',','.');
                    if ( x ==0 ) System.out.println( "[" + line + "]" );

                    if ( del != null ) {
                        st = new StringTokenizer(line, del );
                    }
                    else {
                        st = new StringTokenizer(line);
                    };

                    int i = 0;
                    while ( st.hasMoreElements() ) {
                        data[i] = st.nextToken();
                        i++;
                    }

                    x++;
                    
                    // wir zählen ab 1 ....
                    Double dy = 0.0;
                    
                    int mode = 1;
                    try {
                        dy = mapper.getValue( data );
                    }
                    catch( Exception ex ){ 
                        System.out.println( "***>>>> line=[" + line + "]" );
                        ex.printStackTrace();
                    };
                    
                    mr.addValue( dy );
                    mr.hashValueByDate( mapper.getDate( data ), dy );
                    
                    limitCounter++;
                } 
                else {
                    System.out.println( "> empty: " + line );
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(MessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //System.out.print( mr.toString() );
        
        return mr;
    }

}
