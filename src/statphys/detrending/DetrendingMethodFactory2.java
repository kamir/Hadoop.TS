

package statphys.detrending;

import statphys.detrending.methods.DFA;
import statphys.detrending.methods.DFAParameter; 
import statphys.detrending.methods.DFAmulti;
import statphys.detrending.methods.IDetrendingMethod;

/**
 * Derzeit nur DFA , aber mit verschiedenen Polynomen.
 *
 * @author kamir
 */
public class DetrendingMethodFactory2 {

    public static final String DFA1 = "DFA_1";
    public static final String DFA2 = "DFA_2";
    public static final String DFAn = "DFA_n";

    private DetrendingMethodFactory2() { };

    public static IDetrendingMethod getDetrendingMethod( String key ) {

        DFA m = new DFA();
        
        DFAParameter para = (DFAParameter) m.initParameter();

        if ( key.equals( DFA1 ) )
            para.setGradeOfPolynom( 1 );

        else if ( key.equals( DFA2 ) )
            para.setGradeOfPolynom( 2 );

        else { //Grad ist danach festzulegen
            String input = javax.swing.JOptionPane.showInputDialog("Grade of Polynomial fit: ");
            int grade = Integer.parseInt( input );
            para.setGradeOfPolynom( grade );
        }
        m.setPara(para);
        return (IDetrendingMethod)m;
    };

    public static IDetrendingMethod getDetrendingMethod( int grad ) {
        DFAmulti m = new DFAmulti();
        DFAParameter para = (DFAParameter) m.initParameter();
        para.setGradeOfPolynom( grad );
        m.setPara(para);
        return (IDetrendingMethod)m;
    };
}
