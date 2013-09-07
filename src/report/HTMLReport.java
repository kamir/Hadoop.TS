/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class HTMLReport {

    public HTMLReport( String folderO , String name ) {
        folderOUT = folderO;
        title = name;
    };

    private String title = "";
    private String folderOUT;
    private String head = "<html><head><title>RISAnalyse2</title></head><body><table border='1'>";
    private String foot = "</table></body></html>";

    private StringBuffer sb = new StringBuffer();

    public void writeReport() {
        try {
            File f = new File(folderOUT + File.separator + "report.html");
            System.out.println("REPORT: " + f.getAbsolutePath() );
            FileWriter fw = new FileWriter(f);
            fw.write(head + "\n");
            fw.write(sb.toString() + "\n");
            fw.write(foot + "\n");
            fw.flush();
            fw.close();
        }
        catch (IOException ex) {
            Logger.getLogger(HTMLReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createResultlineSVG( String label, String para ) {
        File f = new File(folderOUT+label);
        String fn = "file://" + f.getAbsolutePath();
        String line = "<tr><td width='15%'>" + para + "</td><td><embed src=" + fn + " width='820' height='640' type='image/svg+xml' " +
                "pluginspage='http://www.adobe.com/svg/viewer/install/' /> </td></tr>";
        sb.append(line + "\n");
    };

    public void createResultlinePNG( String label, String para ) {
        File f = new File(folderOUT+label);
        String fn = "file://" + f.getAbsolutePath();
        String line = "<tr><td width='15%'>" + para + "</td><td><img src='" + fn + "' width='640' height='480' /> </td></tr>";
        sb.append(line + "\n");
    };

    public void createResultlinePNG(String label, String[] titles, String para) {
        File f1 = new File(folderOUT+label+"_"+titles[0]+".png");
        File f2 = new File(folderOUT+label+"_"+titles[1]+".png");
        File f3 = new File(folderOUT+label+"_"+titles[2]+".png");
        File f4 = new File(folderOUT+label+"_"+titles[3]+".png");

        String fn1 = "file://" + f1.getAbsolutePath();
        String fn2 = "file://" + f2.getAbsolutePath();
        String fn3 = "file://" + f3.getAbsolutePath();
        String fn4 = "file://" + f4.getAbsolutePath();

        String line = "<tr>"+
                      "<td width='15%'>" + para + "</td>"+
                      "<td><img src='" + fn1 + "' width='320' height='240' /> </td>"+
                      "<td><img src='" + fn2 + "' width='320' height='240' /> </td>"+
                      "<td><img src='" + fn3 + "' width='320' height='240' /> </td>"+
                      "<td><img src='" + fn4 + "' width='320' height='240' /> </td>"+
                      "</tr>";
        sb.append(line + "\n");
    }

}
