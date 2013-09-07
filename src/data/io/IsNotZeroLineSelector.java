package data.io;

import java.util.StringTokenizer;

/**
 *
 * Vorverarbeitung einer Eingabezeile einer Datei.
 *
 * Hier werden z.B. fehlende Stellen aufgefüllt, oder Zeichen ersetzt.
 *
 */


/**
 *
 * @author kamir
 */
public class IsNotZeroLineSelector {
    
    public IsNotZeroLineSelector( int _col ) { 
        col = _col;
    };

    int col = 3; 
    
    public boolean doSelectLine(String line, String delim) {
        boolean v = true;
        //System.out.println( line );
        
        StringTokenizer st = new StringTokenizer(line, delim);
        int i = 0;
        while ( st.hasMoreTokens() ) { 
            String s = st.nextToken();
            
            if ( col == (i+1) ) {
                Integer in = Integer.parseInt( s  );
                if ( in == 0 ) { 
                    v = false;
                }
                //System.out.print( i + "," + in + "=" + v + "\t" );
                return v;
            }    
            
            i=i+1;
        }
        //System.out.println();
        return v;
    }
}
