/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.series;

import data.series.Messreihe;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class MesswertTabelle {

    String header = "#\n# unknown PARAMETER set \n#\n";
    String label = null;
    public boolean singleX = true;

    public MesswertTabelle() {
    }

    public MesswertTabelle(String tesT) {
        label = tesT;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    Vector<Messreihe> messReihen = null;

    public Vector<Messreihe> getMessReihen() {
        return messReihen;
    }

    public void setMessReihen(Vector<Messreihe> _messReihen) {
        this.messReihen = _messReihen;
    }

    public void setMessReihen(Messreihe[] rows) {
        Vector<Messreihe> mrv = new Vector<Messreihe>();
        for (Messreihe r : rows) {
            mrv.add(r);
        }
        this.setMessReihen(mrv);
    }

    public void writeToFile() {
        File f = new File(this.getLabel());
        writeToFile(f);
    }

    public void createParrentFile(File f) {
        System.out.println("file: " + f);
        File p = f.getParentFile();
        System.out.println("parent: " + p);

        if (p == null || !p.exists()) {
//            p.mkdirs();
//            System.out.println( f.getAbsolutePath() + " was created ... " );
        } else {
            System.out.println(" nothing to create ");
        }
    }

    ;

    public void writeToFile(File f) {

        System.out.println("a) MWT=>" + f.getAbsolutePath());

        createParrentFile(f);

        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write(this.toString());
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Messreihe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public double fill_UP_VALUE = -5.0;

    public static String getCommentLine(String comment) {
        return "#\n# " + comment + "\n#\n";
    }

    /**
     * Die Daten zur Ausgabe auf der Konsole ausgeben.
     *
     * @return
     */
    public String toSignificanzString() {

        Hashtable keyedHash = new Hashtable<String, Messreihe>();
        for (Messreihe m : this.messReihen) {
            if (m == null) {
                m = new Messreihe("empty");
            }
            keyedHash.put(new Integer(m.getLabel()), m);
        }

        Enumeration<Messreihe> en1 = this.messReihen.elements();
        int maxLength = 0;
        while (en1.hasMoreElements()) {
            Messreihe mr = en1.nextElement();
            int l = mr.yValues.size();
            if (l > maxLength) {
                maxLength = l;
            }
        }

        StringBuffer sb = new StringBuffer();

        int size = this.messReihen.size();
        Enumeration<Messreihe> en = this.messReihen.elements();

        String headline = "";

        Messreihe mr = en.nextElement();
        // sb.append("\n# " + mr.getLabel() + " [" + mr.xValues.size() +","+ mr.yValues.size() +"] Werte" );
        //headline = headline.concat( mr.getLabel_X() +"\t" + mr.getLabel_Y() +"\t" );


        Set<Integer> s = keyedHash.keySet();
        List l = new ArrayList();
        for (Integer lab : s) {
            l.add(lab);
        };

        System.out.println(l);
        Collections.sort(l);
        System.out.println(l);

        Iterator it = l.iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            mr = (Messreihe) keyedHash.get(key);
            System.err.println("k=" + key);
            sb.append("\n# " + mr.getLabel() + " [" + mr.xValues.size() + "," + mr.yValues.size() + "] Werte");
            if (singleX) {
                headline = headline.concat(mr.getLabel_Y() + "\t");
            } else {
                headline = headline.concat(mr.getLabel_X() + "\t" + mr.getLabel_Y() + "\t");
            }

            // size = mr.yValues.size();
        }

        sb.append("\n#\n#\n");
        sb.append(headline + "\n");

        // DecimalFormat df = new DecimalFormat("0.00000E00");
        DecimalFormat df = new DecimalFormat("0,00");

        Messreihe longest = getLogestRow(this.messReihen);
        int j = 0;
        for (int i = 0; i < maxLength; i++) {
            it = l.iterator();
            while (it.hasNext()) {
                mr = (Messreihe) keyedHash.get((Integer) it.next());
                double x = 0.0;
                double y = 0.0;

                try {
                    x = (Double) longest.getXValues().elementAt(i);
                } catch (Exception ex) {
                    // x = 0.0;
                }

                try {
                    y = (Double) mr.getYValues().elementAt(i);
                } catch (Exception ex) {
                    y = fill_UP_VALUE;
                }

                String linePart;
                if (singleX && j > 0) {
                    linePart = df.format(y) + getSymbol(y) + "\t";
                } else {
                    linePart = x + "\t" + df.format(y) + getSymbol(y) + "\t";
                }

                sb.append(linePart);
                j++;
            }
            sb.append("\n");
        }
        sb.append("# * p<0.03; + p<0.01; # p<0.001\n#");
        return sb.toString();
    }

    ;
    
    /**
     * Die Daten zur Ausgabe auf der Konsole ausgeben.
     *
     * @return
     */
    public String toString() {

        Hashtable keyedHash = new Hashtable<Integer, Messreihe>();
        int zi = 1;
        for (Messreihe m : this.messReihen) {
            if (m == null) {
                m = new Messreihe("empty");
            }
            Integer key = calcKey(m, zi);
            zi++;
            keyedHash.put(key, m);
        }

        int maxLength = 0;

        // max length ermitteln zum auffüllen ...
        Enumeration<Messreihe> en1 = this.messReihen.elements();
        while (en1.hasMoreElements()) {
            Messreihe mr = en1.nextElement();
            int l = mr.yValues.size();
            if (l > maxLength) {
                maxLength = l;
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(this.header);

        sb.append("#\n# M e s s w e r t t a b e l l e  der Reihen:\n#");

        int size = this.messReihen.size();
        Enumeration<Messreihe> en = this.messReihen.elements();

        String headline = "";

        Messreihe mr = en.nextElement();
        //sb.append("\n# " + mr.getLabel() + " [" + mr.xValues.size() +","+ mr.yValues.size() +"] Werte" );
        //headline = headline.concat( mr.getLabel_X() +"\t" + mr.getLabel_Y() +"\t" );

        Set<Integer> s = keyedHash.keySet();
        List l = new ArrayList();
        for (Integer lab : s) {
            l.add(lab);
        };

        System.out.println(l);
        Collections.sort(l);
        System.out.println(l);

        Iterator it = l.iterator();
        int col = 0;
        while (it.hasNext()) {
            mr = (Messreihe) keyedHash.get((Integer) it.next());
            sb.append("\n# " + mr.getLabel() + " [" + mr.xValues.size() + "," + mr.yValues.size() + "] Werte");
            if (singleX || col > 1) {
                headline = headline.concat(mr.getLabel_Y() + "\t");
            } else {
                headline = headline.concat(mr.getLabel_X() + "\t" + mr.getLabel_Y() + "\t");
            }
            col++;
            // size = mr.yValues.size();
        };

        sb.append("\n#\n#\n");
        sb.append(headline + "\n");

        Messreihe longest = getLogestRow(this.messReihen);

        DecimalFormat dfX = new DecimalFormat("0.0000");
        DecimalFormat dfY = new DecimalFormat("0.0000");

        int j = 0;
        for (int i = 0; i < maxLength; i++) {
            it = l.iterator();
            while (it.hasNext()) {
                mr = (Messreihe) keyedHash.get((Integer) it.next());
                double x = 0.0;
                double y = 0.0;

                try {
                    x = (Double) longest.getXValues().elementAt(i);
                } catch (Exception ex) {
                    // x = 0.0;
                };

                try {
                    y = (Double) mr.getYValues().elementAt(i);
                } catch (Exception ex) {
                    y = fill_UP_VALUE;
                };

                String linePart;
                if (singleX && j > 0) {
                    linePart = dfY.format(y) + "\t";
                } else {
                    linePart = dfX.format(x) + "\t" + dfY.format(y) + "\t";
                }

                sb.append(linePart);
                j++;
            }
            j=0;
            sb.append("\n");
        };
        return sb.toString();
    }

    ;

    public void setHeader(String parameterSet) {
        header = parameterSet;
    }

    private Messreihe getLogestRow(Vector<Messreihe> messReihen) {
        Messreihe l = null;
        int max = 0;
        int c = 0;
        int index = 0;
        for (Messreihe m : messReihen) {
            int s = m.getSize()[0];
            if (s > max) {
                max = s;
                index = c;
            }
            c++;
        }
        l = messReihen.elementAt(index);
        return l;
    }

    public void addMessreihe(Messreihe mr) {
        if (messReihen == null) {
            messReihen = new Vector<Messreihe>();
        }
        messReihen.add(mr);
    }

    private String getSymbol(double y) {
        String s = "\t~\t";
        if (y < 0.03) {
            s = "\t*\t";
        }
        if (y < 0.01) {
            s = "\t+\t";
        }
        if (y < 0.001) {
            s = "\t#\t";
        }
        return s;
    }

    public String[] getHeaders() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public double[][] getDataArray() {
        return null;
    }

    public int getNrLines() {
        return 8;
    }

    public int getNrCols() {
        return 7;
    }

    private Integer calcKey(Messreihe m, int lastKey) {
        Integer key = lastKey;
//        try {
//            Integer i = new Integer( m.getLabel() );
//            key = i;
//        } 
//        catch(Exception ex) { 
//        
//        }    
        return key;
    }
}
