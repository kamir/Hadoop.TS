package app;

import java.io.File;

/**
 *
 * @author kamir
 */
public class ProjectContext {

    public static String basePathIN = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT = "/home/kamir/ANALYSIS/"; 
    
    public static String basePathOUT_dashboard = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_rawTS = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_corpus = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_listfile = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_cclog = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_eslog = "/home/kamir/ANALYSIS/"; 
    
    public static String basePathOUT_networks = "/home/kamir/ANALYSIS/"; 
    public static String basePathOUT_LRI = "LRI/"; 
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
    }
    
    private static String projectName;

    public static void initProject( String name ) { 
        projectName = name;
    }
    
    public static String getProjektPath_CALC_LRI() {
        String n = basePathOUT + projectName + "/";
        
        boolean goOn = false;
        
        File f = new File(n);
        if ( f.exists() ) { 
            if ( f.canWrite() ) { 
                goOn = true;
            }
        }
        else { 
            f.mkdirs();
            goOn = true;
        }
        
        if ( !goOn ) { 
            System.err.print( "Can not create file: " + n );
            System.exit( -2 );
        }
        
        return n; 
    }
 
}
