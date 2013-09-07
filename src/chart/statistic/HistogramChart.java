/**
 *   Quelle : http://www.koders.com/java/fid0CC0EF466A91FD577D941296B7C42D62367292B3.aspx
 */

package chart.statistic;

/**
 *
 * @author kamir
 */
/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------
 * HistogramDemo2.java
 * -------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HistogramDemo2.java,v 1.1 2005/04/28 16:29:15 harrym_nu Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

import data.series.Messreihe;
import data.series.MesswertTabelle;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo of the {@link HistogramDataset} class.
 */
public class HistogramChart extends ApplicationFrame {

   
    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public HistogramChart(String title) {
        super(title);
    }

    /**
     * Creates a sample {@link HistogramDataset}.
     *
     * @return the dataset.
     */
//    private IntervalXYDataset createDataset() {
//        HistogramDataset dataset = new HistogramDataset();
//        double[] values = {1.0, 20.0, 38.0, 40.0, 45.0, 46.0, 47.0, 48.0, 9.0, 10.0};
//        dataset.addSeries("Reihe 1", values, 10, 0.0, 100.0);
//        return dataset;
//    }

    HistogramDataset dataset = null;
    Messreihe mr = null;

    public void addSerieWithBinning(Messreihe _mr, int bins, double min, double max) {


        if ( mr == null ) dataset = new HistogramDataset();

        this.mr = _mr;
        
        double[] values = mr.getYData();

        dataset.addSeries(mr.getLabel(), values, bins, min, max);


    }

    public void addSerie(Messreihe _mr) {
        
        if ( mr == null ) dataset = new HistogramDataset();

        this.mr = _mr;
        
        double[] values = mr.getYData();

        dataset.addSeries(mr.getLabel(), values, 10, 0, 10.0);
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    JFreeChart chart = null;

    static int widthD = 778;
    static int heightD = 467;

    public boolean useLegend = false;
    public ChartPanel createChartPanel() {
                      
        chart = ChartFactory.createHistogram(
            mr.getLabel(),
            null,
            null,
            dataset,
            PlotOrientation.VERTICAL,
            useLegend, // legende
            false,
            false
        );
        chart.getXYPlot().setForegroundAlpha(0.75f);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension( widthD, heightD));
        
        return chartPanel;
    }

        public JFreeChart createChart() {

        JFreeChart chart = ChartFactory.createHistogram(
            mr.getLabel(),
            null,
            null,
            dataset,
            PlotOrientation.VERTICAL,
            false, // legende
            false,
            false
        );
        chart.getXYPlot().setForegroundAlpha(0.75f);
        return chart;
        }

    /**
     * The starting point for the demo.
     *
     * @param args  ignored.
     *
     * @throws IOException  if there is a problem saving the file.
     */
    public static void main(String[] args) throws IOException {

        stdlib.StdRandom.initRandomGen((long)1.0);
        HistogramChart demo = new HistogramChart("Mein Histogram" );

        Messreihe mr1 = Messreihe.getGaussianDistribution(150);

        Messreihe mr2 = Messreihe.getGaussianDistribution(550);

        //System.out.println( mr );

        demo.addSerie( mr1 );
        demo.addSerie( mr2 );


        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

        demo.store(".", "HIST_demo" );


    }

    boolean doStoreChart = true;
    
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


    public void store( String folder, String filename ) {
        store( chart, new File(folder), filename);

        MesswertTabelle tab = new MesswertTabelle();
        File f = new File( folder + "/" + "TAB_" + filename + ".dat" );
        Vector<Messreihe> mrs = new Vector<Messreihe>();
        mrs.add( mr );
        tab.setMessReihen( mrs );
        tab.writeToFile( f );
    }


}
