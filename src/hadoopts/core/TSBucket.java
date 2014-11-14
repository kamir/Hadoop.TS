/**
 * A set of time series is grouped for efficient bulk processing
 * in TSBucket.
 * 
 * The default implementation works with a SequenceFile. An alternative
 * representation of the TSBucket are AVRO and PARQUET files.
 * 
 * The data is stored as a (k,v) pair from:
 *   
 *    key:     Text
 *    value:   VectorWritable;
 *
 *   EXAMPLE:
 *   
 *     NamedVector nv = new NamedVector(new DenseVector(data.getData()), data.label);
 *     VectorWritable vec = new VectorWritable();
 *     vec.set(nv);
 *     writer.append(new Text(nv.getName()), vec);
 *   
 */
package hadoopts.core;

import data.series.Messreihe;
import hadoopts.topics.wikipedia.AccessFileFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

import statphys.ris.experimental.TSPropertyTester;
import tstool.random.LongTermCorrelationSeriesGenerator;
import tstool.random.TSGenerator;

/**
 * @author Mirko Kaempf
 */
public class TSBucket {

    public static boolean useHDFS = false;
    public boolean inMEM = false;

    public static int default_LIMIT = 5;
    public int LIMIT = Integer.MAX_VALUE;

    public boolean isProcessed = false;
    
    int[] ids = null;
    java.util.Vector<Messreihe> bucketData = new java.util.Vector<Messreihe>();
    // record mode is a domain specific feature from WIKIPEDIA analysis.
    static String recoderIdMode = "counter"; 

    public String sourcFolder = "/media/esata/wiki/extract/svwiki/";
        
    FileWriter fw = null;
    FileWriter fwe = null;
    public static String FN_EXT = "A";
    
    static int counter = 0;
  
    /**
     * Create an empty TSBucket and set LIMIT to Integer.MAX_VALUE.
     * 
     * @return a bucket
     */
    public static TSBucket createEmptyBucketFull() {
        TSBucket bu = new TSBucket();
        bu.LIMIT = Integer.MAX_VALUE;
        return bu;
    }

    /**
     * Create an empty TSBucket and set LIMIT to Integer.MAX_VALUE.
     * 
     * @return a bucket
     */
    public static TSBucket createEmptyBucket() {
        TSBucket bu = new TSBucket();
        bu.LIMIT = TSBucket.default_LIMIT;
        return bu;
    }

    // define the limit ...
    public void setLimitTo(int i) {
        LIMIT = i;
    }

    private static void setRecordMode(String c) {
        recoderIdMode = c;
        resetCounter();
     }

    /**
     * Where should the time series be loaded from?
     * 
     * Within the source folder, there is a "group folder",
     * from which data can be loaded.
     * 
     * @param in 
     */
    public void setSourceDataFolder(String in) {
        sourcFolder = in;
        System.out.println("> bucket source base : " + in);
        System.out.println("> bucket limit       : " + LIMIT);
    }

    public TSBucket(String in, String out, String s) {
    	setSourceDataFolder( in );
    }

    public TSBucket() {
    }

    public TSBucket(Configuration conf) {
 		setConfig(conf);
	}
    
    /**
     * Processing results are stored in CSV files.
     * 
     * This processing mode is just for testing. 
     * Large scale processing is done via Crunch.TS
     * and scalable pipelines.
     * 
     * @param fn
     * @param t
     * @param ids
     * @throws IOException
     */
    public void loadAndProcess(String fn, TSOperation t, java.util.Vector<Integer> ids) throws IOException {
        
    	File f = new File(fn + ".proc." + FN_EXT + ".csv");
        File fe = new File(fn + ".proc.explode." + FN_EXT + ".csv");

        fw = new FileWriter(f);
        fwe = new FileWriter(fe);

        loadFromSequenceFile(fn, ids, t);

        fw.close();
        fwe.close();

        System.out.println(f.getAbsolutePath() + " created ... ");
    }

	/**
     * Im sourceFolder wird eine komplette Gruppe gewählt und in einen TS Bucket
     * überführt.
     *
     * ==> ist nur eine SAVE Funktion ...
     *
     * @param groupFolder
     * @param limit
     * 
     */
    public void createBucketFromLocalFilesInDirectory(String groupFolder, int limit) throws IOException {

        LIMIT = limit;
        String s = groupFolder;

        File f = new File(sourcFolder + s);
        
        System.out.println("--> load data from ($source/$group) : " + f.getAbsolutePath());

        Configuration config = initConfig();
        FileSystem fs = initFileSystem();

        Path path = new Path(s + ".tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toUri().toString());

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        File[] liste = f.listFiles(new AccessFileFilter());
        System.out.println(liste.length);
        System.out.println("--> process bucket : " + f.getAbsolutePath() + " (" + liste.length + ")");

        int c = 0;
        for (File file : liste) {
            c++;
            if (c < LIMIT) {
                TSData data = new TSData(file);
                System.out.println("(" + c + ")");
                NamedVector nv = new NamedVector(new DenseVector(data.getData()), data.label);
                VectorWritable vec = new VectorWritable();
                vec.set(nv);

                writer.append(new Text(nv.getName()), vec);
            }
            if (c % 10000 == 0) {
                System.out.println(c);
            }
        }
        writer.close();
    }

    
    /**
     * Reload a TSBucket from a Sequence-File and do an implicit processing, 
     * if the TSOperation object is not null. 
     *
     * @param fn
     * @param ids
     * 
     * @throws IOException
     */
    public void loadFromSequenceFile(String fn, java.util.Vector<Integer> ids, TSOperation tst) throws IOException {

    	if ( tst != null ) isProcessed = true;
    	
        FileSystem fs = initFileSystem();
        Configuration config = initConfig();

        Path path = new Path(fn);

        // write a SequenceFile form a Vector
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, config);

        System.out.println("--> process bucket    : " + fn);
        System.out.println("--> compression-codes : " + reader.getCompressionCodec());
        System.out.println("--> key-classename    : " + reader.getKeyClassName());
        System.out.println("--> value-classname   : " + reader.getValueClassName());

        // TSBucket.setRecordMode( "page-id" );
        TSBucket.setRecordMode( "counter" );
        
        boolean goOn = true;
        int i = 1;
        while (goOn && i <= LIMIT) {

            Text key = new Text();

            VectorWritable vec = new VectorWritable();
            goOn = reader.next(key);

            int pageID = TSBucket.getRecordID(key.toString());

            boolean skipped = false;

            // bearbeite nur die, die im FILTER file stehen ...
            if (ids != null) {
                if (ids.contains(pageID)) {
                    skipped = false;
                } else {
                    skipped = true;
                }
            }

            reader.getCurrentValue(vec);

            Messreihe mr = new Messreihe();
            mr.setDescription(i + " ) " + fn + "_[" + key.toString() + "]");
            mr.setLabel(pageID + " ");

            int c = 0;
            NamedVector vector = (NamedVector) vec.get();
            while (c < vector.size()) {
                double value = vector.get(c);
                //System.out.println( c + "\t" + value  );

                mr.addValue(value);

                c++;
            }
            try {
                Messreihe m = null;
                if (tst != null) {
                    m = tst.processReihe(fw, mr, fwe);
                }
            } catch (Exception ex) {
                Logger.getLogger(TSBucket.class.getName()).log(Level.SEVERE, null, ex);
            }

            i = i + 1;

            int code = 1;
            if (skipped) {
                code = 0;
            }

            if (inMEM) {
                bucketData.add(mr);
            }

            //System.out.println( code + "\t" + mr.getLabel() + "\t" + mr.xValues.size() + "\t" + mr.summeY() );

        }
        System.out.println("--> nr of records     : " + (i - 1));
    }
    
    Configuration config = null;
    
    public void setConfig( Configuration conf ) { 
    	config = conf;
    }
    
    private Configuration initConfig() { 
    	return config;
    }

    private FileSystem initFileSystem() throws IOException {

    	FileSystem fs = null;
    	try  {
    		fs = FileSystem.get( new Configuration() );
    		System.out.println( fs );
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();            
    	}
    	
        return fs;
	}

	public java.util.Vector<Messreihe> getBucketData() {
        return bucketData;
    }

    public static void resetCounter() { 
        counter = 0;
    }

    public static int getRecordID(String t) {

        if ( recoderIdMode.equals("counter") ) {
                counter++;    
                return counter;
                
        }
        else { 
            
            if (t.length() > 0) {

                int i1 = t.indexOf("PageID_");

                String a = t.substring(i1 + 7);

                int i2 = a.indexOf(".nrv.h.dat");

                String b = a.substring(0, i2);
                // System.out.append( b + " " + t );
                return Integer.parseInt(b);

            } 
            else {
                return 0;
            }
            
        }
    }

    /**
     * Create Long Term Correlated time series ...
     *
     * @param s
     * @param z
     * @param EXP
     * @param BETA
     * @throws IOException
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
	public void createBucketWithRandomTS(String s, int z, int EXP, double BETA) throws IOException, Exception {

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : LRC with beta=" + df.format(BETA));

        FileSystem fs = initFileSystem();

        Path path = new Path( s + "_LRC_beta_" + df.format(BETA) + ".tsb.vec.seq");
        System.out.println("--> create bucket : (" + path.toString() + ") " + fs);

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        System.out.println("--> process bucket : LRC-Generator (" + z + ")");

        int SAMPLES = 0;
        for (int i = 0; i < z; i++) {

            boolean showTESTS = true;

            Messreihe mr = LongTermCorrelationSeriesGenerator.getRandomRow((int) Math.pow(2, EXP), BETA, showTESTS, false);
            if ( SAMPLES < TSPropertyTester.zSAMPLES) TSPropertyTester.addSample( mr );
            SAMPLES++;
                        
            TSData data = TSData.convertMessreihe(mr);

            System.out.println("(" + i + ")");
            NamedVector nv = new NamedVector(new DenseVector(data.getData()), data.label);
            VectorWritable vec = new VectorWritable();
            vec.set(nv);

            writer.append(new Text(nv.getName()), vec);
        }

        writer.close();
        System.out.println("### DONE : " + path.toString());
    }


    /**
     * Create Uncorrelated time series ...
     *
     * @param s
     * @param EXP
     * @param ANZ
     * @throws IOException
     */
    public void createBucketWithRandomTS(String s, int EXP, int ANZ) throws IOException {

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : uncorrelated TS alpha=0.5");

        Configuration config = initConfig();
        FileSystem fs = initFileSystem();

        Path path = new Path( s + "_alpha_0.5_.tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toString());

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        System.out.println("--> process bucket : Uniform-Random-Generator ( z=" + ANZ + ", l=" + Math.pow(2, EXP) + ", TestA )");

        int SAMPLES = 0;
        for (int i = 0; i < ANZ; i++) {

            TSData data = new TSData();
            data.dataset = rescaleRandomData( data.getRandomData((int) Math.pow(2, EXP)) , 24.0 );
            Messreihe mr = data.getMessreihe();
            if ( SAMPLES < TSPropertyTester.zSAMPLES) TSPropertyTester.addSample( mr );
            SAMPLES++;
            /**
             * 
             * Here we lose the METADATA of each row!!!
             * 
             */
            System.out.print("  (" + i + ")");
            NamedVector nv = new NamedVector(new DenseVector(data.getData()), data.label);
            VectorWritable vec = new VectorWritable();
            vec.set(nv);

            writer.append(new Text(nv.getName()), vec);
        }

        writer.close();
        System.out.println("### DONE : " + path.toString());
    }
    
    /**
     * a set of random numbers is multiplied by a factor
     * 
     * @param randomData, factor
     * 
     * @return rescaled data 
     */
    private double[] rescaleRandomData(double[] randomData, double factor) {
       
        double sum = 0.0;
        for( int i = 0; i < randomData.length; i++ ) { 
            randomData[i] = randomData[i] * factor;
            sum = sum + randomData[i];
        }
        System.out.println( sum + "\t" + sum/(double)randomData.length );
        return randomData;        
    }

    /**
     * Generate a sinus wave.
     * 
     * @param s
     * @param ANZ
     * @param fMIN
     * @param fMAX
     * @param aMIN
     * @param aMAX
     * @param SR
     * @param time
     * @throws IOException
     */
    public void createBucketWithRandomTS_sinus(String s, int ANZ,
            double fMIN, double fMAX, double aMIN, double aMAX, double SR, double time) throws IOException {

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : uncorrelated TS f=[ " + fMIN + ", " + fMAX + "]");

        Configuration config = initConfig();
        FileSystem fs = initFileSystem();

        Path path = new Path( s + "_sinus_.tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toString());

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        System.out.println("--> process bucket : Sinus-Generator ( z=" + ANZ + ", length=" + (time * SR) + ")");

        for (int i = 0; i < ANZ; i++) {

            double fre = stdlib.StdRandom.uniform(fMIN, fMAX);
            double ampl = stdlib.StdRandom.uniform(aMIN, aMAX);
        
            Messreihe mr = TSGenerator.getSinusWave(fre, time, SR, ampl);
            TSData data = TSData.convertMessreihe(mr);

            System.out.print("   (" + i + ")\t");
            NamedVector nv = new NamedVector(new DenseVector(data.getData()), data.label);
            VectorWritable vec = new VectorWritable();
            vec.set(nv);

            writer.append(new Text(nv.getName()), vec);
        }

        writer.close();
        System.out.println("### DONE : " + path.toString());
    }


}
