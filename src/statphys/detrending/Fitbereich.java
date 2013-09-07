/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package statphys.detrending;

/**
 *
 * @author root
 */
public class Fitbereich {

    double u;
    double o;

    Fitbereich(double vu, double vo) {
        u = Math.pow(10.0, vu);
        o = Math.pow(10.0, vo);
    }

    public double getO() {
        return o;
    }

    public void setO(double o) {
        this.o = o;
    }

    public double getU() {
        return u;
    }

    public void setU(double u) {
        this.u = u;
    }

    public double getULog10() {
        return Math.log10(u);
    }

    public double getOLog10() {
        return Math.log10(o);
    }





}
