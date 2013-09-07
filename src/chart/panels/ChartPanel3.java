/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chart.panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author kamir
 */
public class ChartPanel3 extends JComponent {

    double[][] data = null;
    int width = 0;
    int height = 0;    
    int scale = 1;

    public void store( Writer out ) throws IOException { 
        BufferedWriter bw = new BufferedWriter( out );
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bw.write( data[i][j] + " " );
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    } 
    
    public void storeLog( Writer out ) throws IOException { 
        BufferedWriter bw = new BufferedWriter( out );
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bw.write( Math.log( data[i][j] ) + " " );
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    } 
    
    public ChartPanel3(double[][] _data) {
        data = _data;
        width = _data[0].length;
        height = _data[0].length;
    
        setSize( width * scale , height * scale );        
    }
    
    public ChartPanel3(double[][] _data, int _scale) {
        data = _data;
        width = _data[0].length;
        height = _data[0].length;
        
        scale = _scale;
        System.out.println( width * scale + " " + height * scale );
        setSize( width * scale , height * scale );        
    }

    
    public void paint(Graphics g) {
        
        Graphics2D g2d = (Graphics2D)g;
        
        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB);
        
        AffineTransform transformer = new AffineTransform();
	// transformer.translate(5,5);
	transformer.scale( scale , scale );
	
	g2d.setTransform(transformer);
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {                 
                image.setRGB(i, j, (int)(data[i][j] * 1000.) );
            }             
        }
        
        g2d.drawImage(image, 10, 10, this);
    }
}
