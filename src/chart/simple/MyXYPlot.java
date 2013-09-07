/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chart.simple;

import data.series.Messreihe;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Paint;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MyXYPlot extends javax.swing.JDialog {

    public static String path = "./MyXYPlot.jpg";

    public static boolean doStoreChart = false;
    
    public static void open(Vector<Messreihe> phasenDiagramm, String title, String labelX, String labelY, boolean legende) {
        Messreihe[] r = new Messreihe[ phasenDiagramm.size() ];
        int i = 0;
        for( Messreihe ri : phasenDiagramm ) {
            r[i] = ri;
            i++;
            if ( debug ) System.out.println( ri.toString() );
        }

        MyXYPlot pl = new MyXYPlot(r,title,labelX,labelY,legende);
        pl.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
        pl.setVisible(true);
        pl.setAlwaysOnTop(true);
        
        // pl.store( pl, folder, filename );

        
    }
    
    public static void open(Vector<Messreihe> phasenDiagramm, String title, String labelX, String labelY, boolean legende, File folder, String name) {
        Messreihe[] r = new Messreihe[ phasenDiagramm.size() ];
        int i = 0;
        for( Messreihe ri : phasenDiagramm ) {
            r[i] = ri;
            i++;
            if ( debug ) System.out.println( ri.toString() );
        }

        MyXYPlot pl = new MyXYPlot(r,title,labelX,labelY,legende);
        pl.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
        pl.setVisible(true);
        pl.setAlwaysOnTop(true);
        
        pl.store( pl.chart, folder, name );

        
    }

    public void store(JFreeChart cp, File folder, String filename) {
        if (doStoreChart) {


//            File folder = LogFile.folderFile;
//            String fn = folder.getAbsolutePath() + File.separator + "images/Distribution_";
//            File file = null;
//            fn = fn + GeneralResultRecorder.currentSimulationLabel;


            String fn = filename;
            try {

                final File file1 = new File(folder.getAbsolutePath() + File.separator + fn + ".png");
                System.out.println("\n>>> Save as PNG Image - Filename: " + file1.getAbsolutePath()
                        + "; CP: "+ cp);
                            try {
                final ChartRenderingInfo info = new ChartRenderingInfo
                (new StandardEntityCollection());

                Thread.currentThread().sleep(1000);

                ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);

                Thread.currentThread().sleep(1000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }



//                File file = new File(folder.getAbsolutePath() + File.separator + fn + ".svg");
//                System.out.println(">>> Save as SVG Image - Filename: " + file.getAbsolutePath()
//                        + "; CP: "+ cp);
//
//
//                // Get a DOMImplementation and create an XML document
//                DOMImplementation domImpl =
//                        GenericDOMImplementation.getDOMImplementation();
//                Document document = domImpl.createDocument(null, "svg", null);
//
//                // Create an instance of the SVG Generator
//                SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//
//                // draw the chart in the SVG generator
//                cp.draw(svgGenerator, new Rectangle(800, 600));
//
//                // Write svg file
//                OutputStream outputStream = new FileOutputStream(file);
//                Writer out = new OutputStreamWriter(outputStream, "UTF-8");
//                svgGenerator.stream(out, true /* use css */);
//                outputStream.flush();
//                outputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void open(Messreihe[] phasenDiagramm, String title, String labelX, String labelY, boolean legende) {
        Messreihe[] r = phasenDiagramm;
        int i = 0;
        for( Messreihe ri : phasenDiagramm ) {
            r[i] = ri;
            i++;
        }

        MyXYPlot pl = new MyXYPlot(r,title,labelX,labelY,legende);
        pl.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
        pl.setAlwaysOnTop(true);
        pl.setVisible(true);
        
        
        

    }
        
    public static void open(Messreihe[] phasenDiagramm, String title, String labelX, String labelY, boolean legende, File folder, String name) {
        Messreihe[] r = phasenDiagramm;
        int i = 0;
        for( Messreihe ri : phasenDiagramm ) {
            r[i] = ri;
            i++;
        }

        MyXYPlot pl = new MyXYPlot(r,title,labelX,labelY,legende);
        pl.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE);
        pl.setAlwaysOnTop(true);
        pl.setVisible(true);
        
        pl.store( pl.chart, folder, name );
        

    }

    String xlabel = "x";
    String ylabel = "y";
    String title = "scatterplot";
    boolean legende = false;
    Messreihe mr = null;

    XYSeriesCollection dataset = new XYSeriesCollection();
    static boolean debug = false;

    void addDataSerie( Messreihe mr ) {
        if ( mr != null ) {
            if ( debug ) System.out.println("~ " + mr.getLabel() );
            XYSeries series = mr.getXYSeries();
            dataset.addSeries( series );
        }
    };


    public MyXYPlot(Messreihe mr, String title, String labelX, String labelY) {
        super( new JFrame(), mr.getLabel() );

        this.title = title;
        this.xlabel = labelX;
        this.ylabel = labelY;
        this.mr = mr;
        init();
        addDataSerie(mr);
    }

    public MyXYPlot(Messreihe mr, String labelX, String labelY) {
        super( new JFrame() , mr.getLabel());

        this.xlabel = labelX;
        this.ylabel = labelY;
        this.mr = mr;
        init();
        addDataSerie(mr);
    }

    public MyXYPlot(Messreihe mr, String title, String labelX, String labelY, boolean legende) {
        this(mr, title, labelX, labelY);
        this.legende = legende;
        init();
        addDataSerie(mr);
    }
    
    
    public MyXYPlot(Messreihe[] mr, String title, String labelX, String labelY, boolean legende) {
        super( new JFrame(), title);
        this.title = title;
        this.xlabel = labelX;
        this.ylabel = labelY;
        this.mr = mr[0];  // Master
      
        this.legende = legende;

        init();
        for ( int i=0; i < mr.length ; i++ ) {
            addDataSerie( mr[i] );
        }
         
    }

    boolean inited = false;

    public JFreeChart chart;

    public static double xRangDEFAULT_MIN = 0.0;
    public static double xRangDEFAULT_MAX = 170.0;
    public static double yRangDEFAULT_MAX = 6000.0;
    public static double yRangDEFAULT_MIN = 0.0;
    
    private void init() {
        if ( !inited ) {
            chart = createMyXYChart();
            ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);

            panel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(panel);
            setSize(640, 480);
            
            
            NumberAxis xAxis = (NumberAxis)chart.getXYPlot().getDomainAxis();
            xAxis.setAutoRange(false);
            xAxis.setRange(xRangDEFAULT_MIN, xRangDEFAULT_MAX);
           
            NumberAxis yAxis = (NumberAxis)chart.getXYPlot().getRangeAxis();
            yAxis.setAutoRange(false);
            yAxis.setRange(yRangDEFAULT_MIN, yRangDEFAULT_MAX);

            RefineryUtilities.centerFrameOnScreen(this);
            inited = true;
        }
    }


    public static void main(String[] args) {
        stdlib.StdRandom.initRandomGen((long) 1.0);
        int i = 1500;
        Messreihe r = Messreihe.getGaussianDistribution(i);
        System.err.println( r.toString() );
        MyXYPlot p = new MyXYPlot(r, i + " Zufallszahlen", "nr", "rand(x)");
        p.setYIntervall( -1 , 1);

        p.setVisible(true);
    }

    public static Rectangle defaultSYMBOL = new Rectangle(0,0,2,2);
    
    public static Color rowONEDefualtColor = Color.green;

    public JFreeChart createMyXYChart() {
       
        // addDataSerie(mr);
        //         Generate the graph
        JFreeChart chart = ChartFactory.createScatterPlot(title, // Title
                xlabel, // x-axis Label
                ylabel, // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                legende, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
                );



        XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesShape( 0 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, rowONEDefualtColor );

        renderer.setSeriesShape( 1 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesPaint(1, Color.blue );

        renderer.setSeriesShape( 2 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesPaint(2, Color.orange );

        renderer.setSeriesShape( 3 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(3, false);
        renderer.setSeriesShapesVisible(3, true);
        renderer.setSeriesPaint(3, Color.red );

        renderer.setSeriesShape( 4 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(4, false);
        renderer.setSeriesShapesVisible(4, true);
        renderer.setSeriesPaint(4, Color.gray );

        renderer.setSeriesShape( 5 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(5, false);
        renderer.setSeriesShapesVisible(5, true);
        renderer.setSeriesPaint(5, Color.green );

        renderer.setSeriesShape( 6 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(6, false);
        renderer.setSeriesShapesVisible(6, true);
        renderer.setSeriesPaint(6, Color.cyan );

        renderer.setSeriesShape( 7 , defaultSYMBOL );
        renderer.setSeriesShape( 8 , defaultSYMBOL );
        renderer.setSeriesShape( 9 , defaultSYMBOL );
        renderer.setSeriesLinesVisible(7, false);
        renderer.setSeriesLinesVisible(8, false);
        renderer.setSeriesLinesVisible(9, false);
        


        plot.setBackgroundPaint( Color.white );

        plot.setRenderer(renderer);


//        try {
//            ChartUtilities.saveChartAsJPEG(
//                    new File( path ), chart, 500, 300);
//            System.out.println( path + " plot stored ... " );
//        }
//        catch (IOException e) {
//            System.err.println("Problem occurred creating chart.");
//        }

        return chart;
    }

//    public void store(JFreeChart cp, File folder, String filename) {
//
//
//
////            File folder = LogFile.folderFile;
////            String fn = folder.getAbsolutePath() + File.separator + "images/Distribution_";
////            File file = null;
////            fn = fn + GeneralResultRecorder.currentSimulationLabel;
//
//
//            String fn = filename;
//            try {
//
//                final File file1 = new File(folder.getAbsolutePath() + File.separator + fn + ".png");
//                System.out.println(">>> Save PNG Image\n> Filename: " + file1.getAbsolutePath());
//                try {
//
//                   
//
//                    final ChartRenderingInfo info = new ChartRenderingInfo
//                            (new StandardEntityCollection());
//                    // ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }

//                File file = new File(folder.getAbsolutePath() + File.separator + fn + ".svg");
//                System.out.println(">>> Save as SVG Image - Filename: " + file.getAbsolutePath()
//                        + "; CP: "+ cp);
//
//
//                // Get a DOMImplementation and create an XML document
//                DOMImplementation domImpl =
//                        GenericDOMImplementation.getDOMImplementation();
//                Document document = domImpl.createDocument(null, "svg", null);
//
//                // Create an instance of the SVG Generator
//                SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//
//                // draw the chart in the SVG generator
//                cp.draw(svgGenerator, new Rectangle(800, 600));
//
//                // Write svg file
//                OutputStream outputStream = new FileOutputStream(file);
//                Writer out = new OutputStreamWriter(outputStream, "UTF-8");
//                svgGenerator.stream(out, true /* use css */);
//                outputStream.flush();
//                outputStream.close();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    private void setYIntervall(int i, int i0) {
        ValueAxis ax = this.chart.getXYPlot().getRangeAxis();
        ax.setRange( i, i0);
    }

}






