/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chart.simple;

import data.series.Messreihe;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author root
 */
public class MultiPointsChart extends MultiChart {

    public static void open(JFrame f, Messreihe[] mrs, String string, String string0, String string1, boolean b) {
        cont = f;
        open(mrs, string, string0, string1, b);
    }

    private MultiPointsChart(JFrame jf, boolean b) {
        super(jf,b);
    }

    @Override
    public ChartPanel _createChartPanel() {

        JFreeChart chart2 = ChartFactory.createScatterPlot(
                chartTitle,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                useLegende, // legende
                true,
                true);

        XYPlot plot = chart2.getXYPlot();
        plot.setBackgroundPaint(Color.white);

      	plot.getRenderer().setSeriesPaint(0, Color.green);
      	plot.getRenderer().setSeriesPaint(1, Color.blue);
      	plot.getRenderer().setSeriesPaint(2, Color.orange);
      	plot.getRenderer().setSeriesPaint(3, Color.RED);
      	plot.getRenderer().setSeriesPaint(4, Color.gray);


        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        BasicStroke s = new BasicStroke(
                    2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0.0f);

        renderer.setSeriesStroke( 0, s );
        renderer.setSeriesStroke( 1, s );
        renderer.setSeriesStroke( 2, s );
        renderer.setSeriesStroke( 3, s );
        renderer.setSeriesStroke( 4, s );

        ChartPanel chartPanel = new ChartPanel(chart2);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chart = chart2;
        return chartPanel;
    }


    static Container cont = new JFrame();

    public static void open(final Vector<Messreihe> mrs, final String string, final String x, final String y, final boolean b , final String comment) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
                final MultiPointsChart dialog = new MultiPointsChart( (JFrame) cont, false);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.setVisible(false);
                    }
                });
                Enumeration<Messreihe> en = mrs.elements();
                while (en.hasMoreElements()) {
                    Messreihe mr = en.nextElement();
                    dialog.initMessreihe( mr );
                    //System.out.println( mr.getStatisticData("> ") );
                }
                dialog.chartTitle = string;
                dialog.xLabel = x;
                dialog.yLabel = y;
                dialog.useLegende = b;
                dialog.getStatisticTextField().setText(comment);
                dialog.initChart();
                dialog.setTitle(string);
                dialog.setVisible(true);
                dialog.setAlwaysOnTop(true);
                RefineryUtilities.centerFrameOnScreen(dialog);
//            }
//        });
    }

}
