/**
 * FilteredBucketTool loads a full TSBucket into memory,
 * and uses a list to filter records before they are processed.
 *  * 
 * To process this data set it uses an instance of class derived 
 * from "SingleRowTSO".
 *
 * It works like a local mapper, which processes all records idependently from
 * each other but combined with a map-side join.
 *
 */
package app.experimental;

import chart.simple.MultiChart;
import data.series.MRT;
import data.series.Messreihe;
import hadoopts.core.AbstractTSProcessor;
import hadoopts.buckets.BucketLoader;
import hadoopts.core.SingleRowTSO;
import hadoopts.core.TSBucket;
import hadoopts.data.TSBucketFileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class SimpleFilteredBucketTool extends AbstractTSProcessor {

    public void workOnBucketFolder(File[] files) throws IOException {
        
        // collect some data for output at the end of the run ...
        StringBuffer sb = new StringBuffer();
              
        // process all files ...
        for( File file : files ) {
            System.out.println(">>> file         : " + file.getName() + " " + file.canRead() );
            System.out.println(">>> tool         : " + super.getTSOperation().getClass().getName() );
            
            switch ( procm ) {
                case procm_BULK : {
                    System.out.println(">>>              : BULK" );
                    bl.loadBucket( file.getAbsolutePath() );
                    System.out.println(">>>              : " + bl.getTSBucket() );
                                
                    Vector<Messreihe> data = bl.getBucketData();
                    
                    Vector<Messreihe> dataNorm = new Vector<Messreihe>();

                    int d = 7;
                    int t = 24 * d;

                    for( Messreihe m : data ) {

                        Messreihe mr = MRT.normalizeByPeriodeTrend(m, t);

                        double[] reihe = MRT.calcPeriodeTrend(m, t);
                        Messreihe mr3 = new Messreihe( reihe );

                        dataNorm.add( mr );
                    }

                    sb.append( file.getAbsolutePath() + "   " + data.size() + "\n" );

                    MultiChart.open(dataNorm, file.getName(), "","", true);

                    break;
                }  
                
                case procm_RECORD_STREAM : {
                    
                    System.out.println(">>>              : STREAM" );

                    bl.processBucket( file.getAbsolutePath(), super.getTSOperation() );
                    
                    break;     
                }
                
            }
            
        }    
        System.out.println( "***\n" + sb.toString() + "\n***\n" );
    }
    
    public SimpleFilteredBucketTool() {
        bl = new BucketLoader();
    }
    public BucketLoader bl;
    
    public static final int procm_BULK = 0;
    public static final int procm_RECORD_STREAM = 1;
    
    // default ... work on a stream of time series like in Hadoop but
    //             just within a single JVM ...
    public static int procm = procm_RECORD_STREAM;
        
    public static void main(String[] args) throws Exception {
        
        SimpleFilteredBucketTool tool = new SimpleFilteredBucketTool();
               
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        // default settings 
        File folder = new File("C:/DATA/WIKI");
        tool.procm = procm_BULK;
        tool.bl.setLimit( 50 );
        
        if ( args != null ) folder = new File( args[0] );
        
        System.out.println(">>> input folder : " + folder.getAbsolutePath() + " # exists() => " + folder.exists() );
        System.out.println(">>> mode         : " + procm );

        // get a file-listing of the default input folder 
        // (a folder in the local FS)
        File[] files = folder.listFiles( new TSBucketFileFilter() );
      
        tool.setFilter( "A" );
        tool.initFilter();
        tool.workOnBucketFolder( files );
        
    }
    
    /**
     * get the data as a Vector<Messreihe> ...
     */ 
    public static Vector<Messreihe> loadBucketData(String name) throws IOException {
        BucketLoader bl = new BucketLoader();
        bl.loadBucket( name );
        Vector<Messreihe> data = bl.getBucketData();
        return data;
    }


    
    /**
     * 
     * Example procedure to process some time series ...
     * 
     * @param tsb
     * @param days
     * @return 
     */
    public static Vector<Messreihe> removePeriodicTrend( TSBucket tsb, int days ) {
        Vector<Messreihe> dataNorm = new Vector<Messreihe>();

        int d = days;
        int t = 24 * d;

        int c = 0;
        for( Messreihe m : tsb.getBucketData() ) {
            Messreihe mr = MRT.normalizeByPeriodeTrend(m, t);
            dataNorm.add( mr );
            c++;
        }
        System.out.println(c + " rows in TSB.");
        return dataNorm;
    }

    File filter = null;

    @Override
    public void initFilter() throws IOException {
        Vector<Integer> ids = null;
                
        if ( filter != null ) {
            int col = 1;
            ids = loadIdListe( filter, col );
        }
        else { 
            System.out.println(">>> do not forget to define a filter ... " );
            System.exit(-1);
        }
        System.out.println(">>> filtered : " + ids.size() + " ids.");
    }

    protected Vector<Integer> loadIdListe(File f, int col) throws FileNotFoundException, IOException {
        Vector<Integer> ids = new Vector<Integer>();
        FileReader fr = new FileReader( f );
        BufferedReader br = new BufferedReader( fr );
        while( br.ready() ) {
            String line = br.readLine();
            if ( line.startsWith("#") ) {
                System.out.println( line );
            }
            else {
                StringTokenizer st = new StringTokenizer( line );
                Integer id = Integer.parseInt( st.nextToken() );
                if ( ids.contains(id) ) {
                
                }
                else {
                    ids.add(id);
                }
            }            
        }
        return ids; 
    }

    @Override
    public void setFilter(String fn) throws IOException {
        if ( fn != null ) {
            filter = new File( fn );
            if ( filter.canRead() ) { 
                System.out.println( ">>> use filter-file : " + filter.getAbsolutePath() );
            }  
            else { 
                System.out.println( "!!! filter-file can not be used : " + filter.getAbsolutePath() );
                filter = null;
            }
        }   
    }
    
    
    
    
    
}

