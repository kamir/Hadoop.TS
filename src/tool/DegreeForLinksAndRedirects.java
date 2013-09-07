package tool;




import chart.statistic.MyHistogramm2D;
import data.io.IsNotZeroLineSelector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import chart.panels.ChartPanel3;


/*
 * Darstellung der Degree-Distribution als 2D-Histogramm mit Log-Binning.
 * 
 * TODO: 
 * 
 * Matrix ist noch mit der Anzahl der jeweiligen möglichen Werte, die in
 * einem Feld eingeordnet werden können zu normieren.
 */

/**
 *
 * @author Mirko Kämpf
 */
public class DegreeForLinksAndRedirects {
    
     public static void main(String[] args) throws IOException {
        
        String pathoffset = "P:/DATA/";
        System.out.println( " >> Basepath: " + pathoffset );
                
        // wurde mittels Datameer-Plattform erstellt...
        String fileNameJoinedRedirects = "JoinedRedirects_per_PageID";
        String fileNameJoinedLinks = "JoinedLinks_per_PageID";
        String fileNameMasterJoin = "MASTER_JOIN_result.dat";

        /**
         * Einlesen und prüfen der Daten ...
         */
        System.out.println(fileNameJoinedRedirects);
        data.io.datafile.DataFileTool.head( new File( pathoffset + fileNameJoinedRedirects ) , 10 );
        
        System.out.println(fileNameJoinedLinks);
        data.io.datafile.DataFileTool.head( new File( pathoffset + fileNameJoinedLinks ) , 10 );
        
        System.out.println(fileNameMasterJoin);
        data.io.datafile.DataFileTool.head( new File( pathoffset + fileNameMasterJoin ) , 10 );
        
        String offsetOutPath= "P:/DATA/DegreeDistributions/";
        /**
         * 
         * Prepare Output ...
         * 
         */
        String fileNameOUT = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links.dat";
        String fileNameOUT2 = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links_log10.dat";
        
        String fileNameOUT_F2 = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links_F1_col5notZero.dat";
        String fileNameOUT2_F2 = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links_F1_col5notZero_log10.dat";
        
        String fileNameOUT_F1 = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links_F2_col4notZero.dat";
        String fileNameOUT2_F1 = offsetOutPath+"MATRIX_Out_vs_In_DEGREE_Links_F2_col4notZero_log10.dat";
    
        // Filter anlegen ...
        IsNotZeroLineSelector filter1 = new IsNotZeroLineSelector(4);
        IsNotZeroLineSelector filter2 = new IsNotZeroLineSelector(5);
        
        // MyHistogramm2D.limit = 10000;
        
        // BILD 0 ... ohne Filter
        double[][] d0 = MyHistogramm2D.loadHistogram2D( new File( pathoffset + fileNameMasterJoin ), 1, 2, 3, 1.2, 15000.0 );
        ChartPanel3 pan0 = new ChartPanel3( d0 , 6 );
        FileWriter fw = new FileWriter( fileNameOUT );
        pan0.store( fw );
        fw.close();
        fw = new FileWriter( fileNameOUT2 );
        pan0.storeLog( fw );
        fw.close();
        JFrame frame0 = new JFrame("ColorPan -> no Filter " + pathoffset + fileNameJoinedLinks);
        frame0.getContentPane().add( pan0 );
        frame0.setSize(600, 600);
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setVisible(true);
                
        // BILD 1 ... mit Filter1
        double[][] d1 = MyHistogramm2D.loadHistogram2D( new File( pathoffset + fileNameMasterJoin ), 1, 2, 3, 1.2, 15000.0 , filter1 );
        ChartPanel3 pan1 = new ChartPanel3( d1 , 6 );
        fw = new FileWriter( fileNameOUT_F1 );
        pan1.store( fw );
        fw.close();
        fw = new FileWriter( fileNameOUT2_F1 );
        pan1.storeLog( fw );
        fw.close();
        JFrame frame = new JFrame("ColorPan -> F1 " + pathoffset + fileNameJoinedLinks);
        frame.getContentPane().add( pan1 );
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        
        
        // BILD 2 ... mit Filter2
        double[][] d2 = MyHistogramm2D.loadHistogram2D( new File( pathoffset + fileNameMasterJoin ), 1, 2, 3, 1.2, 15000.0 , filter2 );
        ChartPanel3 pan2 = new ChartPanel3( d2 , 6 );
        fw = new FileWriter( fileNameOUT_F2 );
        pan2.store( fw );
        fw.close();
        fw = new FileWriter( fileNameOUT2_F2 );
        pan2.storeLog( fw );
        fw.close();
        JFrame frame2 = new JFrame("ColorPan -> F2 " + pathoffset + fileNameJoinedLinks);
        frame2.getContentPane().add( pan2 );
        frame2.setSize(600, 600);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);     
     
     
        
        
     
     }
     
    
}
