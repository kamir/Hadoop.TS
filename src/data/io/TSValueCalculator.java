/*
 *
 */
package data.io;

import java.util.Date;

/**
 *
 * @author kamir
 */
public interface TSValueCalculator {
    
    public void setTimeIntervall( Date begin, Date end );
   
    public Date getDate( String[] line );
            
    public double getValue( String[] line );
    public String getName();
 
    
}
