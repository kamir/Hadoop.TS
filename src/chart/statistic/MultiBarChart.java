/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chart.statistic;


import data.series.Messreihe;
import java.awt.Color;
import java.awt.Dimension;

import java.util.Enumeration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author kamir
 */
public class MultiBarChart extends ApplicationFrame {
    
    
    
 
 

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public MultiBarChart(String title) {

        super(title);

    }
    
    public void init() {
        
        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);

    }

    
    public Messreihe[][] rows = null;
    
    public String[] serie; // Was wurde gemessen ?
    


    int nrCat = 5; // i, Phase
    int nrSer = 3; // j, Messwert-Sorte
    
    
    /**
     * Returns a sample dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        
        String[] cat= new String[nrCat];
        
        for( int i= 0; i < nrCat; i++ ) { 
            cat[i] = "phase: " + i;
        }

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for( int i= 0; i < 5; i++ ) { 
            for( int j=0; j<3;j++) {
                
                Messreihe mr = rows[i][j];
                
                Enumeration en = mr.yValues.elements();
                
                while( en.hasMoreElements() ) {
                    double v = (Double)en.nextElement();
                    dataset.addValue( v, serie[j], cat[i] ); 
                }
            }
        }
        
        return dataset;
        
    }
    

    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "Bar Chart Demo 8",       // chart title
            "Category",               // domain axis label
            "Value",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.15);
        
        // disable bar outlines...
        CategoryItemRenderer renderer = plot.getRenderer();        
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
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
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        MultiBarChart demo = new MultiBarChart("Bar Chart Demo 8");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

 
}
