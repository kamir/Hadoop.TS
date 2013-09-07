/**
 * Werte der X-Achse werden zwischen min und max in n Bereiche eingeteilt.
 * Alle in diesem Bereich liegenden Werte werden gemittelt und einem
 * P'(x',y') zugeordnet.
 * 
 * Man kann eine Gerade festlegen und dann noch getrennte Kurven ermitteln,
 * für alle Punkte oberhalb und unterhalb der Gerade.
 * 
 * es gibt dann die Messreihen: 
 *  
 *     - Originaldaten-Messreihe mr
 * (    - X-Errors  )
 * (    - Y-Errors. )
 *  
 * 
 * 
 * 
 */
package data.series;

import java.util.Enumeration;

/**
 *
 * @author kamir
 */
public class AveragedMessreihe {
    
    public Messreihe mrLAST = null;
    public Messreihe mrOriginal = null;
    
    public Messreihe mrBinned[] = new Messreihe[3];
    
    double xMin = 0.0;
    double xMax = 5000.0;
    
    int steps = 150 - 1;
    
    double[][] vX = null;
    double[][] vY = null;
    
    double[][] vX_carrier = null;
    double[][] vY_carrier = null;
    
    double[][] zX = null;
     
    double dx = 0;
    

        
    public AveragedMessreihe( Messreihe mr ) { 
        
        mrOriginal = mr; // darauf bezieht sich auch immer die TRENNUNG Oben / Unten
        
        mrBinned = new Messreihe[3];   
        
        String l = mr.getLabel();
        mrBinned[0] = new Messreihe( l + " all");        
        mrBinned[1] = new Messreihe( l + " o");
        mrBinned[2] = new Messreihe( l + " u");
    };
    
    public void initSegements( int anz , Messreihe toSplit ) { 
                        
//        this.xMin = 0; // mrOriginal.getMinX();
//        this.xMax = 200; // mrOriginal.getMaxX();
        
        this.steps = anz - 1;
        
        vX = new double[ steps ][3];        
        vY = new double[ steps ][3];
        
        vX_carrier = new double[ steps ][3];        
        vY_carrier = new double[ steps ][3];
        
        zX = new double[ steps ][3]; 
        dx = xMax / steps;        
        
        double offset = dx / 2.0; 
              
        int ouIndex = 0;
        int i = 0;
        
        double xc = 0.0;
        double yc = 0.0;
            
        Enumeration en = mrOriginal.xValues.elements();
        while( en.hasMoreElements() ) { 
            
            double x = (Double)en.nextElement();
            double y = (Double)mrOriginal.yValues.get(i);
            
            xc = (Double)toSplit.xValues.elementAt(i);
            yc = (Double)toSplit.yValues.elementAt(i);
            
            // Container ermitteln ...
            int j = (int)( x / dx );
            
            if ( isOben(x, y) ) {
                ouIndex = 1;
            }
            if ( isUnten(x, y) ) {
                ouIndex = 2;
            }
//            
//            if ( j < vX.length ) {
            try {
                // vX[j][ouIndex] = vX[j][ouIndex] + x;
                vY[j][ouIndex] = vY[j][ouIndex] + yc;            
                zX[j][ouIndex] = zX[j][ouIndex] + 1;
                
                // vX[j][0] = vX[j][0] + x;
                vY[j][0] = vY[j][0] + yc;            
                zX[j][0] = zX[j][0] + 1;
            }
            catch( Exception ex) { 
                System.out.println( "a" );
                //ex.printStackTrace();
            }
//            }
            i++;
        }
        double y = 0;
        
        for( int k = 0; k < steps;k++ ) { 
           
           y = vY[k][0];
           mrBinned[0].addValuePair( (k + 0.5) * dx , ( y / zX[k][0])   );    
           
           y = vY[k][1];
           mrBinned[1].addValuePair( (k + 0.5) * dx , ( y / zX[k][1])   );
           
           y = vY[k][2];
           mrBinned[2].addValuePair( (k + 0.5) * dx , ( y / zX[k][2])   );
                
        }
        
    };
    
    

//    public void ___initSegementsO(int anz) {
//        
////        this.xMin = mrOriginal.getMinX();
////        this.xMax = mrOriginal.getMaxX();
//        
//        this.steps = anz - 1;
//        
//        vX = new double[ steps ][3];        
//        vY = new double[ steps ];
//        
//        zX = new double[ steps ]; 
//        dx = xMax / steps;    
//        
//        double offset = dx / 2.0; 
//        
//        int i = 0;
//        Enumeration en = mrOriginal.xValues.elements();
//        while( en.hasMoreElements() ) { 
//            double x = (Double)en.nextElement();
//            double y = (Double)mrOriginal.yValues.get(i);
//            
//            if ( isOben( x,y ) ) {
//                // Container ermitteln ...
//                int j = (int)( x / dx );
//
//                if ( j < vX.length ) {
//                    vX[j] = vX[j] + x;
//                    vY[j] = vY[j] + y;
//
//                    zX[j] = zX[j] + 1;
//
//                    i++;
//                };    
//            }
//        }
//        for( int k = 0; k < steps;k++ ) { 
////            mrBinned.addValuePair( offset + vX[k] / zX[k], (vY[k] / zX[k])   );        
//            mrBinned.addValuePair( (k + 0.5) * dx , (vY[k] / zX[k])   );        
//        }
//         
//    }
//    
//    public void ___initSegementsO(int anz, Messreihe flowDens ) {
//        
////        this.xMin = mrOriginal.getMinX();
////        this.xMax = mrOriginal.getMaxX();
//        
//        this.steps = anz - 1;
//        
//        vX = new double[ steps ];        
//        vY = new double[ steps ];
//        
//        zX = new double[ steps ]; 
//        dx = xMax / steps;    
//        
//        double offset = dx / 2.0; 
//        
//        int i = 0;
//        Enumeration en = mrOriginal.xValues.elements();
//        while( en.hasMoreElements() ) { 
//            double x = (Double)en.nextElement();
//            double y = (Double)mrOriginal.yValues.get(i);
//
//            double checkY = (Double)flowDens.yValues.get(i);
//            
//            if ( isOben( x,checkY ) ) {
//                // Container ermitteln ...
//                int j = (int)( x / dx );
//
//                if ( j < vX.length ) {
//                    vX[j] = vX[j] + x;
//                    vY[j] = vY[j] + y;
//
//                    zX[j] = zX[j] + 1;
//
//                    i++;
//                };    
//            }
//        }
//        for( int k = 0; k < steps;k++ ) { 
//            
//            // mrBinned.addValuePair( offset + vX[k] / zX[k], (vY[k] / zX[k])   );        
//            mrBinned.addValuePair( (k + 0.5) * dx , (vY[k] / zX[k])   );        
//        
//        }
//         
//    }
//
//    public void ___initSegementsU(int anz) {
//        
////        this.xMin = mrOriginal.getMinX();
////        this.xMax = mrOriginal.getMaxX();
//        this.steps = anz - 1;
//        
//        vX = new double[ steps ];        
//        vY = new double[ steps ];
//        
//        zX = new double[ steps ]; 
//        dx = xMax / steps; 
//        
//        double offset = dx / 2.0; 
//        
//        int i = 0;
//        Enumeration en = mrOriginal.xValues.elements();
//        while( en.hasMoreElements() ) { 
//            double x = (Double)en.nextElement();
//            double y = (Double)mrOriginal.yValues.get(i);
//            
//            if ( isUnten( x,y ) ) {
//
//                // Container ermitteln ...
//                int j = (int)( x / dx );
//
//                if ( j < vX.length ) {
//                    vX[j] = vX[j] + x;
//                    vY[j] = vY[j] + y;
//
//                    zX[j] = zX[j] + 1;
//
//                    i++;
//                }
//            }     
//        }
//        for( int k = 0; k < steps;k++ ) { 
////            mrBinned.addValuePair( offset + vX[k] / zX[k] , (vY[k] / zX[k])   );     
//            mrBinned.addValuePair( (k + 0.5) * dx , (vY[k] / zX[k])   );        
//        }
//         
//    }
//    
//    public void ___initSegementsU(int anz, Messreihe flowDens ) {
//        
////        this.xMin = mrOriginal.getMinX();
////        this.xMax = mrOriginal.getMaxX();
//        
//        this.steps = anz - 1;
//        
//        vX = new double[ steps ];        
//        vY = new double[ steps ];
//        
//        zX = new double[ steps ]; 
//        dx = xMax / steps;    
//        
//        double offset = dx / 2.0; 
//        
//        int i = 0;
//        Enumeration en = mrOriginal.xValues.elements();
//        while( en.hasMoreElements() ) { 
//            double x = (Double)en.nextElement();
//            double y = (Double)mrOriginal.yValues.get(i);
//
//            double checkY = (Double)flowDens.yValues.get(i);
//            
//            if ( isUnten( x,checkY ) ) {
//                // Container ermitteln ...
//                int j = (int)( x / dx );
//
//                if ( j < vX.length ) {
//                    vX[j] = vX[j] + x;
//                    vY[j] = vY[j] + y;
//
//                    zX[j] = zX[j] + 1;
//
//                    i++;
//                };    
//            }
//        }
//        for( int k = 0; k < steps;k++ ) { 
////            mrBinned.addValuePair( offset + vX[k] / zX[k], (vY[k] / zX[k])   );        
//            mrBinned.addValuePair( (k + 0.5) * dx , (vY[k] / zX[k])   );        
//        }
//         
//    }

    private boolean isUnten(double x, double y) {
        double ySoll = yX_double( (double)x );
        if ( y >= ySoll ) return true;
        else return false;
    }

    private boolean isOben(double x, double y) {
        double ySoll = yX_double( (double)x );
        if ( y < ySoll ) return true;
        else return false;
    }    
    
    public static double yX_double(double X) {
        return (m * X + (1.0*n) );
    }
    
    static int n = 0;
    static double m = 0;
    public static void _setGerade( int x1, int y1,int x2,int y2) { 
        n = y1;
        m = (1.0*(y2-y1))/(1.0*(x2-x1));
    };
    public static int yX(int X) {
        return (int) (m * X + n);
    }

    public Messreihe[] getNrOfValuesPerBin() {
        
        Messreihe[] mr = new Messreihe[3];
        
        mr[0] = new Messreihe();
        mr[1] = new Messreihe();
        mr[2] = new Messreihe();
        
        for( int k = 0; k < steps;k++ ) { 
            for( int k1 = 0; k1 < 3; k1++ ) {
               double z = (double)zX[k][k1];
               mr[k1].addValuePair( (k + 0.5) * dx , z );        
            }
        }    
            
        
        return mr;
         
    }
    




   
    
}
