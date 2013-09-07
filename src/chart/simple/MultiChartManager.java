/*
 * Controllerklasse für Chartgruppen ...
 */

package chart.simple;

import java.util.Hashtable;
import java.util.Vector;


public class MultiChartManager {

    public static MultiChartManager mcm = null;
    public static MultiChartManager getMultiChartManager() {
        if ( mcm == null ) {
            mcm = new MultiChartManager();
        }
        return mcm;
    };

    Hashtable<String,Vector<MultiChart>> groups = new Hashtable<String,Vector<MultiChart>>();

    public void createNewChartGroup( String label ) {
        if ( getChartGroup(label) == null ) {
            Vector<MultiChart> group = new Vector<MultiChart>();
            groups.put(label, group);
        }
    }

    public Vector<MultiChart> getChartGroup( String label ) {
        return (Vector<MultiChart>)groups.get(label);
    }

    public MultiChart createNewMultiChart( String group, String label ) {
        MultiChart mc = new MultiChart( label );
        Vector<MultiChart> g = getChartGroup( group );
        g.add(mc);
        return mc;
    };

}
