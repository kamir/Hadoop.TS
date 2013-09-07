/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.io;

/**
 *
 * @author napierala
 */
public class MessreiheTester {

    public static void main (String[] args) {
        
        SimResultAPI api = new SimResultAPI();
        api.setMainpath("/home/sim/DEV/");
        /*try {
            Messreihe mr = api.getDataFromFile("Simulation.dat", 10, 2);
            mr.calcAverage();
            System.out.println(mr.getAvarage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         */
        api.setSim_CFG(1);
        api.setSim_UM(1);
        api.setSim_ZPERS(2235);
        api.setSim_name("sim_GA30");
        java.lang.System.out.println("-Test-");

        for (int i=1;i<6;i+=1) {
            System.out.println(i);
            api.setSim_RR(i);
            api.getRatioOfInfectedAgents2();//.toString();
        }
    }
}
