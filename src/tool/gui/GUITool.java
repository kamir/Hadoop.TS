package tool.gui;

import java.awt.Toolkit;
import javax.swing.JFrame;
 
/**
 *
 * @author kamir
 */
public class GUITool {
    
    public static void fullScreen( Object o ) { 
        Toolkit tk = Toolkit.getDefaultToolkit();  
        int xSize = ((int) tk.getScreenSize().getWidth());  
        int ySize = ((int) tk.getScreenSize().getHeight());  
        JFrame frame = (JFrame)o;
        frame.setSize(xSize,ySize);  
    }
    
}
