package app;

import java.io.File;

/**
 *
 * @author kamir
 */
public class ProjectContext {

    public static String basePathIN = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    
    public static String basePathOUT_dashboard = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT_rawTS = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT_corpus = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT_listfile = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT_cclog = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    public static String basePathOUT_eslog = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
    
    public static String basePathOUT_networks = "/Volumes/MyExternalDrive/CALCULATIONS/"; 
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
