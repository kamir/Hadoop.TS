package hadoopts.core;

import data.series.Messreihe;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.io.Writable;

/**
 * The data, used in Messreihe ...
 * 
 * @author Mirko Kämpf
 */
public class TSData implements Writable {
    
    long t0 = -1;
    double dt = 1000.0;  // seconds 
    double[] dataset = null;
    String label = null;

    /**
     * 
     * @param mr
     * @return 
     */
    static TSData convertMessreihe(Messreihe mr) {
        
        TSData data = new TSData();
        
        data.t0 = mr.getTime_t0();
        data.label = mr.getLabel();
        data.dt = mr.getSamplingRate();
        // we assume to have äquidistant values ...
        data.dataset = mr.getYData();
        
        return data;
    }

    public TSData() {
        t0 = System.currentTimeMillis();
        label = "random data";
        dt = 1000.0;
        dataset = getRandomData();
    };

    /**
     * load data from a file ...
     */ 
    TSData(File f) {
        this();
        loadDataFromFile(f);
    }
    
    public void setData( long t_0, double d_t, double[] d, String l ) {
        label = l;
        dataset = d;
        dt = d_t;
        t0 = t_0;
    }
    public double[] getData() { 
        return dataset;
    }

    public void write(DataOutput d) throws IOException {
        d.writeLong( t0 );
        d.writeDouble( dt);
        d.writeUTF( label );
        d.writeInt( dataset.length );
        for( double v : dataset ) d.writeDouble( v );
    }

    public void readFields(DataInput d) throws IOException {
        t0 = d.readLong();
        dt = d.readDouble();
        label = d.readUTF();
        int z = d.readInt();
        dataset = new double[z];
        for( int i = 0; i < z ; i++ ) dataset[i] = d.readDouble();
    }

    /**
     * Just for internal Tests.
     * 
     * @return 
     */
    public double[] getRandomData() {
        return getRandomData(7200);
    }
    
    /**
     * @param z
     * @return 
     */
    static public double[] getRandomData( int z) {
        double[] d = new double[z];
        for(int i = 0 ; i < z; i++ ) {
            // uniform
//            d[i] = Math.random();
            // gaussian
//            d[i] = stdlib.StdRandom.gaussian();
            d[i] = stdlib.StdRandom.poisson(1.0);
        }
        return d;
    }

    
    
    private void loadDataFromFile( File f ) {

        double d[] = null;
        BufferedReader br = null;
        Vector<String> lines = new Vector<String>();
        try {
            System.out.println(f.getAbsolutePath());
            br = new BufferedReader(new FileReader(f));
            while( br.ready() ) {
                String line = br.readLine();
                lines.add( line );
            }
            d = new double[lines.size()];
            int i = 0;
            for( String l : lines ) {
                d[i] = Double.parseDouble( l );
                i++;
            }
        }
        catch (Exception ex) {
            Logger.getLogger(TSData.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ex) {
                Logger.getLogger(TSData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    Messreihe getMessreihe() {
        Messreihe mr = new Messreihe();
        for( double y : dataset) { 
            mr.addValue(y);
        }    
        return mr;
    }

}
