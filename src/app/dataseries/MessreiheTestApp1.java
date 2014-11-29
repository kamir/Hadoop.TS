package app.dataseries;

import data.io.MSExcelMessreihenLoader;
import data.io.MessreihenLoader;
import data.series.MessIntervall;
import data.series.Messreihe;
import data.series.QualifiedMessreihe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MLU Halle-Wittenberg
 *
 * @author Mirko Kämpf
 */
public class MessreiheTestApp1 {

    public static final String version =  "Version 1.0.0";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println( version );

        Messreihe mr = new Messreihe();
        mr.addValuePair(1.0, 5.0);
        mr.addValuePair(2.0, 22.0);
        mr.addValuePair(3.0, 22.0);
        mr.addValuePair(4.0, 22.0);
        mr.addValuePair(5.0, 22.0);
        mr.addValuePair(11.0, 220.0);
        mr.addValuePair(121.0, 223.0);
        mr.addValuePair(141.0, 221.0);
        mr.addValuePair(1221.0, 222.0);

        
        
        mr.calcAverage();


        MessIntervall mi = new MessIntervall(mr, 2, 5);

        mr.show();
        mi.show();

        // teste die SHRINK Funktion
        Messreihe mr2 = new Messreihe();
        for( int i = 10; i < 50; i+=2 ) {
            mr2.addValuePair(i, i*i);
        }
        mr2.setLabel("PARABEL");

        Messreihe kleiner = mr2.shrinkX(21, 29);
        System.out.println( kleiner );

        String reihe = "1 2 x x 3 4 5 6 x x x 10"; //" 11 12 1 1 3 3 4 4 5 5 5 x x x x x x x x x x 7 7 9 1 1";
        QualifiedMessreihe qmr = new QualifiedMessreihe();
        qmr.setLabel("Testreihe");
        StringTokenizer st = new StringTokenizer(reihe);
        while( st.hasMoreTokens() ) {
            Object o = st.nextToken();
            qmr.addValue(o);
        };
        qmr.addValue(8);
        qmr.addValue(108);
        qmr.addValue(9);

        System.out.println( qmr );
        qmr.checkKonsistenz();

        QualifiedMessreihe qmr2 = qmr.setBinningX_average(2);
        System.out.println( qmr2 );
        qmr2.checkKonsistenz();


        QualifiedMessreihe qmr4 = qmr.setBinningX_average(4);
        System.out.println( qmr4 );
        qmr4.checkKonsistenz();



        File file = new File( "./data/in/SPEED_B.xls" );
        String tableName = "A";
        try {
            QualifiedMessreihe mr3 = MSExcelMessreihenLoader.loadQualifiedMessreihe(file, tableName, 1, 2, 11, 10090);
            
            //System.out.println(mr3);
            mr3.checkKonsistenz();

            Messreihe[] reihen = mr3.splitInto(1028);
            System.out.println( reihen.length );
            System.out.println( reihen[ reihen.length-1] );

        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MSExcelMessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(MSExcelMessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println( mr.getYValueForX2(121, 5) );
        
        
        
//        String fn = "D:\\WIKI\\count_links\\out_degree_hist.dat";
//        File f = new File( fn );
//        
//        Messreihe mr42 = MessreihenLoader.getLoader()._loadLogBinnedMessreihe_DIV_BY_BINWIDTH( f , 1, 2, 1.2, 1000000);
//        
//        Messreihe mr43 = mr42.calcLogLog();

    }

}
