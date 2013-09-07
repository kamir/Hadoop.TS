/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.io;

import java.io.*;
import data.series.*;
/**
 *
 * @author napierala
 */
public class SimResultAPI {
    public String mainpath = "/home/sebastian/palaerian/data/sim_UP-CN/";
    public String sim_name = "sim_A";
    public int sim_UM = 1;
    public int sim_CFG = 1;
    public int sim_RR = 10;
    public int sim_ZPERS = 1000;

    /**
     * Liest eine bestimmte Datei
     * (bedeutet die Simulationsläufe einer bestimmten Systemkonfiguration)
     * Gibt in einer Messreihe die Daten zurück
     * Der erste Parameter ist die Spalte, deren Werte in die X-Werte der
     * Messreihe geladen werden, der zweite Parameter sind die Y-Werte
     */
    public Messreihe getDataFromFile(String filename, int XValues, int YValues) {
        String folder = mainpath + sim_name + "_UM=" + sim_UM + "_CFG=" + sim_CFG + "_RR=" + sim_RR +"_ZPERS=" + sim_ZPERS + '/';
        File f = new File(folder+filename);
        Messreihe mrd = MessreihenLoader.getLoader().loadMessreihe_2(f, XValues, YValues);
        return mrd;
    }

    /**
     * Diagramm 1: Evakuierungszeit über der Anzahl der Agenten.
     * Dazu wird zurerst die Evakuierungszeit der einzeln Simulationsläufe
     * einer bestimmten Systemkonfiguration gemittelt
     * Wird die Funktion mit Amax=0 aufgerufen, liefert sie die Werte
     * für alle möglich ZPERS.
     */
    public double[][] getEvacuationTimeByAgents(int Amin, int Ainc, int Amax) {
        Messreihe mrd  = new Messreihe();
        Messreihe mr1  = new Messreihe();

        if (Amax != 0) {
            for (int j=Amin; j<=Amax; j+=Ainc) {
                sim_ZPERS = j;
                mrd = getDataFromFile("Simulation.dat",10,2);
                if (mrd!=null) {
                    mrd.calcAverage();
                    mr1.addValuePair(j, mrd.getAvarage());
                }
            }
        }
        else {
            File dir = new File(mainpath);
            String[] dirList = dir.list();
            System.out.println("Printing "+mainpath);
            if (dirList == null) {
                System.out.println("kein Zugriff möglich");
            }
            else {
                for (int j=0;j<dirList.length;j++) {
                    if (dirList[j].startsWith(sim_name+"_UM="+sim_UM+"_CFG="+sim_CFG+"_RR="+sim_RR)) {
                        String[] dummy = dirList[j].split("=");
                        setSim_ZPERS(Integer.parseInt(dummy[dummy.length-1]));
                        mrd = getDataFromFile("Simulation.dat",10,2);
                        if (mrd!=null) {
                            mrd.calcAverage();
                            mr1.addValuePair(j, mrd.getAvarage());
                        }
                    }
                }
            }
        }
        return mr1.getData();
    }
    
    /**
     * Diagramm 2: Durchschnittliche Evakuierungszeit über der Anzahl der Agenten.
     * Dazu wird zurerst die durchschnittliche Evakuierungszeit der einzeln Simulationsläufe
     * einer bestimmten Systemkonfiguration gemittelt
     */
    public double[][] getMeanEvacuationTimeByAgents(int Amin, int Ainc, int Amax) {
        Messreihe mrd  = new Messreihe();
        Messreihe mr1  = new Messreihe();

        for (int j=Amin; j<=Amax; j+=Ainc) {
            sim_ZPERS = j;
            mrd = getDataFromFile("Simulation.dat",10,7);
            mrd.calcAverage();
            mr1.addValuePair(j, mrd.getAvarage());
        }
        return mr1.getData();
    }


    /**
     * Diagramm 4: Infektionsquote über der Zeit
     * Parameter NoA: Number of Agents
     */
    public double[][] getRatioOfInfectedAgents(int NoA) {
        sim_ZPERS = NoA;
        Messreihe mr1 = new Messreihe();

        // dummys
        Messreihe mrd  = new Messreihe();
        Messreihe mrd2 = new Messreihe();

        // Ermitteln der Anzahl an Simulationsruns je Konfiguration
        // soll später durch Lesen eines Eintrags in cfg-file ersetzt werden
        mrd = getDataFromFile("Simulation.dat",1,1);
        int AnzSimRuns = mrd.getSize()[0];
        mrd.cut(mrd.getSize()[0]);

        // Einlesen der Daten
        int maxs = 0;
        int anz = 0;
        for (int s=1;s<=200;s++) { // steps
            for (int j=1;j<=AnzSimRuns;j++) { // Simulations-Runs
                String dateiname = "Nr_" + (j) + "_System.dat";
                mrd = getDataFromFile(dateiname, 1, 3); // 1: step, 3: Infektionsquote
                anz = mrd.getSize()[0];
                if (s <= anz) {
                    mrd2.addValuePair(j, mrd.getData()[1][s-1]);
                }
                else {
                    // eventuell folgende Zeile rauskommentieren:
                    mrd2.addValuePair(j, mrd.getData()[1][mrd.getSize()[0]-1]);
                    if (mrd.getSize()[0]>maxs) {
                        maxs = mrd.getSize()[0];
                    }
                }
            }
            mrd2.calcAverage();
            mr1.addValuePair(s, mrd2.getAvarage());
            mrd2 = new Messreihe();
        }
        // Bereinigen
        mr1 = mr1.cut(maxs);
        return mr1.getData();
    }

    /**
     * Diagramm 4: Infektionsquote über der Zeit
     * Parameter NoA: Number of Agents
     * Version 2 der Funktion
     * bei dieser werden die Ergebnisdateien im
     * Array mrd2[] zwischengespeichert
     */
    public double[][] getRatioOfInfectedAgents2() {
        Messreihe mr1 = new Messreihe();

        // dummys
        Messreihe mrd  = new Messreihe();

        // Ermitteln der Anzahl an Simulationsruns je Konfiguration
        // soll später durch Lesen eines Eintrags in cfg-file ersetzt werden
        mrd = getDataFromFile("Simulation.dat",1,1);

        int anzSimRuns = mrd.getSize()[0];

        Messreihe[] mrd2 = new Messreihe[anzSimRuns];

        // Einlesen der Daten
        int maxs = 0; // Maximale Anzahl an Schritten in den verschiedenen Wdhl.
        int anz = 0;

        for (int j=1; j<=anzSimRuns; j++) {
            String dateiname = "Nr_" + (j) + "_System.dat";
            mrd = getDataFromFile(dateiname, 1, 3); // 1: step, 3: Infektionsquote
            mrd2[j-1] = mrd;
            if (mrd.getSize()[0] > maxs) {
                maxs = mrd.getSize()[0];
            }
        }

        double summe;   // Zum Zusammenzählen der Infektionsquote in einem
                        // Zeitschritt über alle Simulationsläufe zum Zwecke
                        // der Mittelwerbildung

        for (int s=1; s<=maxs; s++) {   // alle Zeitschritte durchgehen
            summe = 0;
            for (int j=1; j<=anzSimRuns; j++) { // über alle Runs zusammenzählen
                if (s <= mrd2[j-1].getSize()[0])
                    summe += mrd2[j-1].getData()[1][s-1];
                else
                    summe += mrd2[j-1].getData()[1][mrd2[j-1].getSize()[0]-1];
            }
            mr1.addValuePair(s, summe/anzSimRuns);  // Mittelwert für jeden
                                                    // Zeitschritt ablegen
        }
        return mr1.getData();
    }

    public int getNumberOfFloors() throws Exception {
        int result = Integer.parseInt(getSimCFG("AnzEtagen"));
        return result;
    }

    /*
     * Bezieht sich auf Daten in der Building.dat oberhalb der Daten abgelegt
     * sind.
     * Im Einzelnen (mögliche Parameter für diese Funktion):
     * - MODE
     * - WH
     * - AnzEtagen
     * - AnzTreppen
     * - AnzPersonen
     * - FOLDER
     * - ZAHL_Felder_pro_Flur
     * - ZAHL_Treppenstufen
     * - I
     * - R
     * - P
     * - ID
     * - Path_Resultfile
     * - LOOPID
     * - infectionMode
     * - updateMode
     */
    public String getSimCFG(String property) throws Exception {
        String folder = mainpath + sim_name + "_UM=" + sim_UM + "_CFG=" + sim_CFG + "_RR=" + sim_RR +"_ZPERS=" + sim_ZPERS + '/';
        File f = new File(folder+"Building.dat");
        if (!f.canRead()) {
            return null;
        }
        BufferedReader br = null;
        String result;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (!line.startsWith("# "+property)) {
                line = br.readLine();
            }
            result = line.split(" = ")[1];
        }
        finally {
            br.close();
        }
        return result;
    }

    public String getMainpath() {
        return mainpath;
    }

    public void setMainpath(String mainpath) {
        this.mainpath = mainpath;
    }

    public int getSim_CFG() {
        return sim_CFG;
    }

    public void setSim_CFG(int sim_CFG) {
        this.sim_CFG = sim_CFG;
    }

    public int getSim_RR() {
        return sim_RR;
    }

    public void setSim_RR(int sim_RR) {
        this.sim_RR = sim_RR;
    }

    public int getSim_UM() {
        return sim_UM;
    }

    public void setSim_UM(int sim_UM) {
        this.sim_UM = sim_UM;
    }

    public int getSim_ZPERS() {
        return sim_ZPERS;
    }

    public void setSim_ZPERS(int sim_ZPERS) {
        this.sim_ZPERS = sim_ZPERS;
    }

    public String getSim_name() {
        return sim_name;
    }

    public void setSim_name(String sim_name) {
        this.sim_name = sim_name;
    }
}
