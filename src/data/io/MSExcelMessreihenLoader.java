package data.io;

import data.series.Messreihe;
import data.series.QualifiedMessreihe;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author kamir
 */
public class MSExcelMessreihenLoader {

    public static Messreihe loadMessreihe(
             File worksheetFile, String tableName,
             int xCol, int yCol,
             int firstRow, int lastRow ) 
             throws FileNotFoundException, IOException {

        Messreihe mr = new Messreihe();

        InputStream input = new FileInputStream(worksheetFile.getAbsolutePath());
        HSSFWorkbook wb = new HSSFWorkbook(input);
        HSSFSheet sheet = wb.getSheet(tableName);


        HSSFRow rowH = sheet.getRow(firstRow-2);

        mr.setLabel_X("min");
        String headY = rowH.getCell(yCol-1).getStringCellValue();

        mr.setLabel_Y(  headY.substring(0,7) );

        mr.setLabel( "[min," + headY.substring(0,7)+"]" );

        for ( int r = firstRow-1 ; r < lastRow; r++ ) {
            HSSFRow row = sheet.getRow(r);
            
        
            double xValue = r-firstRow+1;
            

            double yValue = row.getCell(yCol-1).getNumericCellValue();

            mr.addValuePair(xValue+1, yValue);
        }

        return mr;
    };

    public static QualifiedMessreihe loadQualifiedMessreihe(
             File worksheetFile, String tableName,
             int xCol, int yCol,
             int firstRow, int lastRow )
             throws FileNotFoundException, IOException {

        Hashtable<Integer,Integer> types = new Hashtable<Integer,Integer>();

        QualifiedMessreihe mr = new QualifiedMessreihe();

        InputStream input = new FileInputStream(worksheetFile.getAbsolutePath());
        HSSFWorkbook wb = new HSSFWorkbook(input);
        HSSFSheet sheet = wb.getSheet(tableName);


        HSSFRow rowH = sheet.getRow(firstRow-2);

        mr.setLabel_X("min");
        String headY = rowH.getCell(yCol-1).getStringCellValue();

        mr.setLabel_Y(  headY.substring(0,7) );

        mr.setLabel( "[min," + headY.substring(0,7)+"]" );

        for ( int r = firstRow-1 ; r < lastRow; r++ ) {
            HSSFRow row = sheet.getRow(r);


            double xValue = r-firstRow+1;

            int typ = row.getCell(yCol-1).getCellType();

            Integer k = types.get(typ);
            if ( k == null ) {
                types.put(typ, 1);
            }
            else {
                int v = types.get( typ );
                v = v +1;
                types.put(typ, v);
            }


            Object yValue = null;
            switch( typ ) {
                case 0: yValue = new Double( row.getCell(yCol-1).getNumericCellValue() );
                        mr.addValue(yValue);
                        break;

                case 1: mr.addValue( " " );
                        break;
                        
                case 3: mr.addValue( " " );
                        break;
                default:
                        System.out.println( "TYP: " + row.getCell(yCol-1).getCellType() );

            }


        }

        Enumeration<Integer> en = types.keys();
        while( en.hasMoreElements() ) {
               Integer key = en.nextElement();
               System.out.println( "[" + key + "] " + types.get(key) );
        }

        return mr;
    };


    public static void main( String[] args ) {

        File file = new File( "./data/in/sorted.xls" );
        String tableName = "alle";
        try {
            Messreihe mr = MSExcelMessreihenLoader.loadMessreihe(file, tableName, 1, 2, 1, 100);
            System.out.println(mr);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MSExcelMessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(MSExcelMessreihenLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    };


}
