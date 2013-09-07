package data.io;

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
public class LineFilter {

    public static String doFilterLine(String line) {
        
        while (line.contains(",,")) {
            line = line.replaceAll(",,", ",0,");
        }
        
        line = line.replaceAll( "," , " " );
        
        return line;
    }
}
