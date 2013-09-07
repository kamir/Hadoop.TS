package data.series;

import java.util.Vector;

/**
 * author Mirko Kämpf
 */
public interface IMessreihe {

        /**
         * Ein String zur Benennung der Datenreihe in einem Diagramm oder in
         * einer Datei.
         *
         * @param label
         */
        public void setLabel( String label);
        public String getLabel();

        /**
         * Ein Vector mit den geordneten X-Werten.
         *
         * @return data
         */
        Vector getXValues();
        /**
         * Ein Vector mit den geordneten Y-Werten.
         *
         * @return data
         */
        Vector getYValues();

        /**
         * Ein 2D Array mit den Wertepaaren.
         *
         * @return data
         */
         public double[][] getData();

    /**
     * Ein Wertepaar wird hinzugefügt.
     *
     * @param x
     * @param y
     */
    void addValuePair(double x, double y);

    /**
     * Die Daten zur Ausgabe auf der Konsole ausgeben.
     *
     * @return
     */
    @Override
    String toString();


    /*
     * die statistischen Kennzahlen der Messreihe werden als String
     * ausgegeben. Dabei beginnt jede Zeile mit "pre".
     */
    String getStatisticData(String pre);

    void setDecimalFomrmatX(String format);

    void setDecimalFomrmatY(String format);

}
