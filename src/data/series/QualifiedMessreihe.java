/**
 *
 *  Es wird noch nicht in allen Operationen auf die GÜLTIGKEIT 
 *  der Werte geachtet.
 * 
 *  Stand 2.12.2010
 *
 **/

package data.series;

import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class QualifiedMessreihe extends Messreihe {

    Vector<Boolean> correct = new Vector<Boolean>();

    @Override
    public QualifiedMessreihe add(Messreihe mr2) {
        QualifiedMessreihe mrNeu = (QualifiedMessreihe)super.add(mr2);
        checkCorrectnessOfRow( mrNeu );
        return mrNeu;
    }

    /*
     * Nur wenn kein "double Wert" vorliegt, wird geprüft.
     */
    public void addValue(Object y) {

        double vy = 0.0;
        boolean b = true;

        try {
            vy = (Double)y;
        }
        catch(Exception ex) { 
            b = false;
        } 

        if( !b ) { 
            try {
                vy = Double.parseDouble( (String)y );    
                b=true;
            }
            catch(Exception ex) { 
                
            }
        }
        addValue(vy,b);
    }

//    private void addQualifiedValue( double y ) {
//        int x = this.getXValues().size() + 1;
//        this.addQualifiedValuePair( 1.0 * x , y );
//    };
//
//    private void addUnqualifiedValue( double y ) {
//        int x = this.getXValues().size() + 1;
//        this.addUnqualifiedValuePair( 1.0 * x , y );
//    };
//
//    public void addUnqualifiedValuePair(double x, double y) {
//        super.addValuePair(x, y);
//        correct.add(Boolean.FALSE);
//    }
//
//    public void addQualifiedValuePair(double x, double y) {
//        addValuePair(x, y);
//        correct.add(Boolean.TRUE);
//    }


    /**
     * In diesem Fall wird implicit von correctem Wert ausgegangen.
     * @param y
     */
    @Override
    public void addValue(double y) {
        int x = this.getXValues().size() + 1;
        this.addValuePair( 1.0 * x , y );
    }

    private void addValue(double y, boolean b) {
        int x = this.getXValues().size() + 1;
        this.addValuePair( 1.0 * x , y , b);
    }


    private void addValuePair(double x, double y, boolean q) {
        super.addValuePair(x, y);
        correct.add(q);
    }

    @Override
    public void addValuePair(double x, double y) {
        super.addValuePair(x, y);
        correct.add(Boolean.TRUE);
    }

    @Override
    public void calcAverage() {
        int z = this.xValues.size();
        double sum = 0.0;

        int nrOfGood = 0;
        for (int i = 0 ; i < z; i++  ) {
            sum = sum + (Double)getYValues().elementAt(i);
            if ( correct.elementAt(i).booleanValue() ) nrOfGood++;
        }
        av = sum / nrOfGood;
    }

    @Override
    public Messreihe copy() {
        return super.copy();
    }

    @Override
    public Messreihe cut(int nrOfValues) {
        return super.cut(nrOfValues);
    }

    @Override
    public Messreihe diff(Messreihe mr2) {
        return super.diff(mr2);
    }

    @Override
    public void divide_Y_by(double divBy) {
        super.divide_Y_by(divBy);
    }

    @Override
    public QualifiedMessreihe setBinningX_average( int bin ) {

        QualifiedMessreihe mr = new QualifiedMessreihe();
        mr.binning = bin;
        mr.setLabel( this.getLabel() + " BIN=" +bin );
        Enumeration<Double> en = this.yValues.elements();

        int anzahl = this.yValues.size();

        int rest = anzahl % bin;
        int elements = anzahl - rest;
        int blocks = elements / bin;

        // double summeA = this.summeY();

        // System.out.println( "[BINNING] bin=" + bin + "\tRest=" + rest + "\tBlöcke=" + blocks  );
        mr.addStatusInfo( "[BINNING] bin=" + bin + "\tRest=" + rest + "\tBlöcke=" + blocks   );

        int i=0;
        int j=0;
        double v = 0;

        // zählt nur die guten im Block
        int zBlock = 0;
        boolean isBlockGood = false;

        while ( i < elements ) {

            boolean isValueGood = correct.elementAt(i);
            isBlockGood = isBlockGood || isValueGood;

            double vv = en.nextElement();
            if ( isValueGood ) {
                v = v + vv;
                zBlock++;
            }

            if( i % bin == (bin-1) ) {
                
                mr.addValuePair( j , v/zBlock, isBlockGood );
                v = 0.0;
                zBlock = 0;
                isBlockGood = false;
                j++;
            }
            i++;
            System.out.println( i + " " + isBlockGood + " " + " " + zBlock + " " + v  );


        }
        return mr;
    };


    // Kumuliere die Werte einfach
    @Override
    public Messreihe setBinningX_sum(int bin) {
        return super.setBinningX_sum(bin);
    }

    @Override
    public Messreihe shift(int offset) throws Exception {
        return super.shift(offset);
    }

    @Override
    public Messreihe shrinkX(double min, double max) {
        return super.shrinkX(min, max);
    }

    @Override
    public Messreihe[] split(int length, int anzahl) {
        return super.split(length, anzahl);
    }

    @Override
    public double summeY() {
        return super.summeY();
    }

    /**
     * Hier wird die Regel für die Gültigkeitsprüfung angewendet ...
     *
     * @param mrNeu
     */
    private void checkCorrectnessOfRow(QualifiedMessreihe mrNeu) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    private boolean checkCorrectnessOfValue(Object y) {
//
//    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        int size = xValues.size();

        sb.append("#\n# M e s s r e i h e \n# Klasse : " + this.getClass() + " \n# Label  : " + this.label + "\n#\n# Anzahl Wertepaare = " + size + "\n#\n");
        sb.append("#\n#" + this.addinfo + "\n#\n");
        sb.append( getStatisticData("# ") );
        sb.append( getStatusInfo() );
        sb.append("\n");
        for (int i = 0 ; i < size; i++  ) {
            double x = (Double)getXValues().elementAt(i);
            double y = (Double)getYValues().elementAt(i);
            String q = "-";
            if( correct.elementAt( i ) ) q = "+";
            sb.append( "[" + q + "]  \t" + x + "\t " + y + "\n");
        }
        sb.append("#\n" + sbComments.toString() );
        return sb.toString();
    };


    public void checkKonsistenz() {
        int z = 0;
        int y = 0;
        Enumeration<Boolean> en = correct.elements();
        while( en.hasMoreElements() ) {
            if( en.nextElement() ) z++;
            else y++;
        }        
        System.out.println( xValues.size() + " " + yValues.size() + " " + correct.size() + " gute:" + z + " schlechte:" +y );
    };


    
}
