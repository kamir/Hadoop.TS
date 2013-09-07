/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.series;

/**
 *
 * @author kamir
 */
public class ValuePair implements Comparable<Object>{

    static public boolean sortByX = false;

    public double x = 0.0;
    public double y = 0.0;

    public ValuePair(double dX, double dY) {
        x = dX;
        y = dY;
    }

    ValuePair() {
        
    }

    public int compareTo(Object o) {
        ValuePair v = (ValuePair)o;
        double d1, d2;
        if ( sortByX ) {
            d1 = v.x;
            d2 = this.x;
        }
        else {
            d1 = v.y;
            d2 = this.y;
        }

        int r = 0;
        if ( d1 > d2 ) return 1;
        else if(d1 < d2) return -1;
        else if(d1 == d2 ) return 0;
        
        return r;
    }

    @Override
    public String toString() { 
        return "(x,y) = (" + x +","+y+")";
    };

}
