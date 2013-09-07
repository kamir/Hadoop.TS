package chart.gnuplot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author kamir
 */
public class GnuPlotChart {

    String foldername = "./charts";
    String ext = ".cmd";

    public static String command = "gnuplot ";


    String cmdFilename = null;

    String dataFilename = null;
    String dataFoldername = null;
    String _t_cmdFilename = null;

    public GnuPlotChart( String cmdFN , String dataFN, String basePATH ) {
        cmdFilename = foldername + File.separator + cmdFN +ext;
        //dataFoldername = LogFile.folderFile.getAbsolutePath();
        dataFoldername = basePATH;

        _t_cmdFilename = dataFoldername + File.separator + cmdFN + ext;

        String line1 = "file=\""+dataFN+"\"\n";
        String line2 = "cd \"" + dataFoldername + "\"\n";

        try {
            BufferedReader br = new BufferedReader(new FileReader(cmdFilename));
            BufferedWriter bw = new BufferedWriter(new FileWriter(_t_cmdFilename));
            bw.write( line1 );
            bw.write( line2 ); 
            br.readLine();
            br.readLine();

            while( br.ready() ) {
                bw.write( br.readLine() + "\n" );
            };
            bw.flush();
            bw.close();
            br.close();
            Thread.sleep(50);
            String cmd = command + _t_cmdFilename;
            System.out.println("> Commando : " + cmd );
            Runtime.getRuntime().exec( cmd );
        }
        catch (Exception ex) {
            System.err.println( ex.getMessage() );
        }
    }

}
