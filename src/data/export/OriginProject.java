/*
 *  Ein Daten-Container, in dem Messreihen abgelegt werden,
 *  die dann für den Origin-Import optimiert gespeichert werden.
 * 
 */
package data.export;

import data.series.Messreihe;
import data.series.MesswertTabelle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JFileChooser;

/**
 *
 * @author kamir
 */
public class OriginProject {
    
    /**
     * absoluter Name im File-System
     */
    public String folderName = null;
    
    public void initFolder(String folder) { 
        if ( folder == null ) {
            javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();  
            //File p = f.getParentFile();
            folderName = f.getAbsolutePath();
        }
        else { 
            folderName = folder;
        }
        System.out.println(">>> Origin-Project: " +
                           "    Folder: " + folderName );
    }

    public void addMessreihen( Messreihe[][] mrv2, String pre) {
        int i = mrv2[0].length;
        int j = mrv2.length;
        
        for( int a=0; a<j; a++ ) { 
            Vector<Messreihe> r = new Vector<Messreihe>();
            for( int b=0; b<i; b++ ) {
                r.add( mrv2[a][b]);
            }    
            addMessreihen(r, pre+"_"+a+"_", true);
        }    
    }

        
    public void addMessreihen( Messreihe[] mrv2, String pre) {
        Vector<Messreihe> rows = new Vector<Messreihe>();
        for( Messreihe mr : mrv2) {
            rows.add(mr);
        }
        addMessreihen(rows, pre, true);
     }
    
     public void addMessreihen(Vector<Messreihe> mrv2, String prefix, boolean writeTab ) {
        if ( writeTab) { 
            MesswertTabelle tab = new MesswertTabelle();
            tab.setLabel(folderName + File.separator + "tab_"+prefix+".dat");
            tab.setMessReihen(mrv2);
            tab.writeToFile();
        }    
            
        for( Messreihe mr : mrv2) {
            File f = new File( folderName + File.separator + prefix + "_" + mr.getFileName() );
            mr.writeToFile( f , '.' );            
        }
    }

    Hashtable<String, FileWriter> logger = new Hashtable<String, FileWriter>();
    public void createLogFile(String s) throws IOException {
        File f = new File(this.folderName + File.separator + s);
        
        if ( !f.getParentFile().exists() ) {
            f.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter( f.getAbsolutePath() );
        logger.put(s, fw);
    }
    
    public void logLine( String s, String line ) throws IOException { 
        FileWriter fw = logger.get(s);
        fw.write(line);
    };
    
    public void closeAllWriter() throws IOException { 
        for( FileWriter fw: logger.values() ) {
            fw.flush();
            fw.close();
        } 
    }

    public void storeMesswertTabelle(MesswertTabelle mwt) {
        File f = new File(this.folderName + File.separator + mwt.getLabel() );
        mwt.writeToFile( f );
    }
    
    
}
