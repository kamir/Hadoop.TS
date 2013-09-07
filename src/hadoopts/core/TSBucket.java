/**
 * A set of time series files is grouped for efficient bulk processing
 *
 */
package hadoopts.core;

import data.series.Messreihe;
import hadoopts.topics.wikipedia.AccessFileFilter;
import hadoopts.core.SingleRowTSO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;
import statphys.ris.experimental.TSPropertyTester;
import tstool.random.LongTermCorrelationSeriesGenerator;
import tstool.random.TSGenerator;

/**
 *
 * @author kamir
 */
public class TSBucket {

    static String recoderIdMode = "counter"; 
    private static void setRecordMode(String c) {
       recoderIdMode = c;
       resetCounter();
    }

    public Class isProcessedBy = null;
    public boolean isProcessed = false;
    java.util.Vector<Messreihe> bucketData = new java.util.Vector<Messreihe>();
    public int LIMIT = Integer.MAX_VALUE;
    public static int default_LIMIT = 5;
    public String outputDir = ".";
    public String sourcFolder = "/media/esata/wiki/extract/svwiki/";
    int[] ids = null;
    public boolean inMEM = false;

    public static TSBucket createEmptyBucketFull() {
        TSBucket bu = new TSBucket();
        bu.LIMIT = TSBucket.default_LIMIT;
        return bu;
    }

    public static TSBucket createEmptyBucket() {
        TSBucket bu = new TSBucket();
        bu.LIMIT = TSBucket.default_LIMIT;
        return bu;
    }

    // define the limit ...
    public void setLimitTo(int i) {
        LIMIT = i;
    }

    public void setFolder(String in, String out) {
        outputDir = out;
        sourcFolder = in;
        File f = new File(outputDir);
        System.out.println("> bucket tool base: " + f.getName());
        System.out.println("> bucket limit    : " + LIMIT);
    }

    public TSBucket(String in, String out, String s) {
        outputDir = out;
        sourcFolder = in;
        File f = new File(outputDir);
        System.out.println("> bucket tool base: " + f.getName());
        System.out.println("> bucket limit    : " + LIMIT);
    }

    public TSBucket() {
    }

    /**
     * Im sourceFolder wird eine komplette Gruppe gewählt und in einen TS Bucket
     * überführt.
     *
     * ==> ist nur eine SAVE Funktion ...
     *
     * @param groupFolder
     */
    public void createBucketFromLocalFilesInDirectory(String groupFolder, int limit) throws IOException {

        LIMIT = limit;
        String s = groupFolder;

        File f = new File(sourcFolder + s);
        System.out.println("--> load data : " + f.getAbsolutePath());

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(config);

        Path path = new Path(outputDir + "/" + s + ".tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toString());

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
    public static boolean useHDFS = true;

    public void loadFromLocalFS(String fn, java.util.Vector<Integer> ids) throws IOException {

        Configuration config = new Configuration();



        if (useHDFS) {
            System.out.println(">>HDFS<< :: " + fn);
            /**
             * define HDFS
             *
             *
             */
            config.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
            config.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        } else {
            System.out.println("<<LocalFS>> :: " + fn);
        }

        FileSystem fs = FileSystem.get(config);



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

    public java.util.Vector<Messreihe> getBucketData() {
        return bucketData;
    }
    TSOperation tst = null;
    FileWriter fw = null;
    FileWriter fwe = null;
    public static String FN_EXT = "A";

    public void loadAndProcess(String fn, TSOperation t, java.util.Vector<Integer> ids) throws IOException {
        tst = t;
        File f = new File(fn + ".proc." + FN_EXT + ".csv");
        File fe = new File(fn + ".proc.explode." + FN_EXT + ".csv");

        fw = new FileWriter(f);
        fwe = new FileWriter(fe);

        loadFromLocalFS(fn, ids);

        fw.close();
        fwe.close();

        System.out.println(f.getAbsolutePath() + " created ... ");
    }

    public static void resetCounter() { 
        counter = 0;
    }
    static int counter = 0;
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
     * Create LRC time series ...
     *
     * @param s
     * @param z
     * @param EXP
     * @param BETA
     * @throws IOException
     * @throws Exception
     */
    public void createBucketWithRandomTS(String s, int z, int EXP, double BETA) throws IOException, Exception {

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : LRC with beta=" + df.format(BETA));

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(config);

        Path path = new Path(outputDir + "/" + s + "_LRC_beta_" + df.format(BETA) + ".tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toString());

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        System.out.println("--> process bucket : LRC-Generator (" + z + ")");

        int SAMPLES = 0;
        for (int i = 0; i < z; i++) {

            boolean showTESTS = false;

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

    public void createBucketWithRandomTS(String s, int EXP, int ANZ) throws IOException {

        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : uncorrelated TS alpha=0.5");

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(config);

        Path path = new Path(outputDir + "/" + s + "_alpha_0.5_.tsb.vec.seq");
        System.out.println("--> create bucket : " + path.toString());

        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, Text.class, VectorWritable.class);

        System.out.println("--> process bucket : Uniform-Random-Generator ( z=" + ANZ + ", l=" + Math.pow(2, EXP) + ", TestA )");

        int SAMPLES = 0;
        for (int i = 0; i < ANZ; i++) {

            TSData data = new TSData();
            data.dataset = processTESTA( data.getRandomData((int) Math.pow(2, EXP)) );
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

    public void createBucketWithRandomTS_sinus(String s, int ANZ,
            double fMIN, double fMAX, double aMIN, double aMAX, double SR, double time) throws IOException {



        DecimalFormat df = new DecimalFormat("0.000");

        System.out.println("--> create bucket : uncorrelated TS f=[ " + fMIN + ", " + fMAX + "]");

        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(config);

        Path path = new Path(outputDir + "/" + s + "_sinus_.tsb.vec.seq");
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

    /**
     * prepare TestData
     * 
     * @param randomData
     * @return 
     */
    private double[] processTESTA(double[] randomData) {
       
        double sum = 0.0;
        for( int i = 0; i < randomData.length; i++ ) { 
            randomData[i] = randomData[i] * 365 * 24;
            sum = sum + randomData[i];
        }
        System.out.println( sum + "\t" + sum/(double)randomData.length );
        return randomData;
        
    }
}
