package data.io.datafile;

import data.io.LineFilter;
import java.io.*;

/**
 *
 * @author kamir
 */
public class DataFileTool {
 
    
    public static void head(File f, int i) throws FileNotFoundException, IOException {
        System.err.println( ">>>[HEAD #lines="+i+"] " +  f.getAbsolutePath() );
        System.err.flush();
        BufferedReader br = new BufferedReader( new FileReader( f ));
        boolean stop = false;
        int j = 0;
        while ( br.ready() && !stop ) { 
            String line = br.readLine();
            System.out.println( "["+j+"] " +  line );
            j = j + 1;
            if ( j == i ) stop = true;
        }
        System.out.flush();
    }

    public static void head(File f, int i, LineFilter lineFilter) throws FileNotFoundException, IOException {
        System.err.println( ">>>[HEAD #lines="+i+"] " +  f.getAbsolutePath() );
        System.err.println( ">>>[Filter:] " +  lineFilter.getClass().getName() );
        System.err.flush();
        BufferedReader br = new BufferedReader( new FileReader( f ));
        boolean stop = false;
        int j = 0;
        while ( br.ready() && !stop ) { 
            String lineA = br.readLine();
            String line = lineFilter.doFilterLine( lineA );
            System.out.println( "["+j+"] " + lineA + " \t {neu} " + line );
            j = j + 1;
            if ( j == i ) stop = true;
        }
        System.out.flush();
    }
        
}
