package tool;


import data.io.MessreihenLoader;
import data.series.Messreihe;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kamir
 */
public class LogBinnedLoaderTest {
    
    public static void main( String[] args ) throws IOException { 
        
        String fileName = "C:/users/kamir/Documents/Ergebnis2.dat";
        File f = new File( fileName );
                
        if(! f.canRead() ) {
            System.exit(0);
        }
                
        Messreihe mr42 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe(  f , 1, 2, 1.2, 200);
        
        System.out.println( mr42 );

        FileWriter fw = new FileWriter( "C:/users/kamir/Documents/Ergebnis2_logBinned.dat" );
        fw.write( mr42.toString() );
        fw.flush();
        
       
    }
    
    
}
