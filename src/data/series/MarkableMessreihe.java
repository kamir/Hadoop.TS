package data.series;

import java.util.Vector;

/**
 * In einem Vector werden Marker gespeichert, die dann zur Zerlegung 
 * der Rohdaten genutzt werden können.
 * 
 * @author kamir
 */
public class MarkableMessreihe extends Messreihe {
    
    Vector<String> marker = new Vector<String>();

    public MarkableMessreihe(String l) {
        super( l );
    }

    public MarkableMessreihe() {
        super("-");
    }
    
    public void addValue(double y, String mark) {
        super.addValue(y);
        setMarker(mark);
    }

    public void addValuePair(double x, double y, String mark) {
        super.addValuePair(x, y);
        setMarker(mark);
    }

    public void addValuePair(ValuePair vp, String mark) {
        super.addValuePair(vp);
        setMarker(mark);
    }

    /**
     * Die Daten zur Ausgabe auf der Konsole ausgeben.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        int size = xValues.size();

        sb.append("#\n# M e s s r e i h e \n# Klasse : " + this.getClass() + " \n# Label  : " + this.label + "\n#\n# Anzahl Wertepaare = " + size + "\n#\n");
        sb.append("#\n#" + this.addinfo + "\n#");
        sb.append( getStatisticData("# ") +"\n" );
        for (int i = 0 ; i < size; i++  ) {
            double x = (Double)getXValues().elementAt(i);
            double y = (Double)getYValues().elementAt(i);
            String z = (String)marker.elementAt(i);
            
            sb.append(  decimalFormat_X.format(x) + "\t" + decimalFormat_Y.format(y) + "\t" + z + "\n");
        }
        System.out.println( sb.toString() );
        sb.append("#\n" + sbComments.toString() );
        return sb.toString();
    };


    @Override
    public void addValue(double y) {
        super.addValue(y);
        setMarker("");
    }

    @Override
    public void addValuePair(double x, double y) {
        super.addValuePair(x, y);
        setMarker("");
    }

    @Override
    public void addValuePair(ValuePair vp) {
        super.addValuePair(vp);
        setMarker("");
    }

    @Override
    public void addValues(Messreihe mr) {
        super.addValues(mr);
        setMultiMarker( "", mr.xValues.size() );
    }

    @Override
    public void addValues(Vector<Double> dataRawTIMES) {
        super.addValues(dataRawTIMES);
        setMultiMarker( "", dataRawTIMES.size() );
    }
    
    void setMarker(String mark) {
        marker.add(mark);
    }        
    
    void setMultiMarker(String mark, int nr ) { 
        for( int i = 0; i < nr; i++ ) { 
            setMarker(mark);
        }
    }
    
    
    
}
