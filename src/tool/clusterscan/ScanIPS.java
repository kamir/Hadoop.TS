package tool.clusterscan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;


public class ScanIPS {
    public static void main(String[] args) throws Exception {
        int[] bounds = ScanIPS.rangeFromCidr("192.168.3.255/24");

        for (int i = bounds[0]; i <= bounds[1]; i++) {
            String address = InetRange.intToIp(i);
            InetAddress ip = InetAddress.getByName(address);

            if (ip.isReachable(100)) { // Try for one tenth of a second
                System.out.printf("Address %s is reachable\n", ip);
            }
        }
    }

    public static int[] rangeFromCidr(String cidrIp) {
        int maskStub = 1 << 31;
        String[] atoms = cidrIp.split("/");
        int mask = Integer.parseInt(atoms[1]);
        System.out.println(mask);

        int[] result = new int[2];
        result[0] = InetRange.ipToInt(atoms[0]) & (maskStub >> (mask - 1)); // lower bound
        result[1] = InetRange.ipToInt(atoms[0]); // upper bound
        System.out.println(InetRange.intToIp(result[0]));
        System.out.println(InetRange.intToIp(result[1]));

        return result;
    }

    public Vector<InetAddress> scanRange(String text) throws UnknownHostException, IOException {
        
        Vector<InetAddress> v = new Vector<InetAddress>();
        
        int[] bounds = ScanIPS.rangeFromCidr(text);

        for (int i = bounds[0]; i <= bounds[1]; i++) {
            String address = InetRange.intToIp(i);
            
            InetAddress ip = InetAddress.getByName(address);

            if (ip.isReachable(100)) { // Try for one tenth of a second
                System.out.printf("Address %s is reachable\n", ip);
                v.add( ip );
            }
        }
        
        return v;
        
    }

    static class InetRange {
        public static int ipToInt(String ipAddress) {
            try {
                byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
                int octet1 = (bytes[0] & 0xFF) << 24;
                int octet2 = (bytes[1] & 0xFF) << 16;
                int octet3 = (bytes[2] & 0xFF) << 8;
                int octet4 = bytes[3] & 0xFF;
                int address = octet1 | octet2 | octet3 | octet4;

                return address;
            } catch (Exception e) {
                e.printStackTrace();

                return 0;
            }
        }

        public static String intToIp(int ipAddress) {
            int octet1 = (ipAddress & 0xFF000000) >>> 24;
            int octet2 = (ipAddress & 0xFF0000) >>> 16;
            int octet3 = (ipAddress & 0xFF00) >>> 8;
            int octet4 = ipAddress & 0xFF;

            return new StringBuffer().append(octet1).append('.').append(octet2)
                                     .append('.').append(octet3).append('.')
                                     .append(octet4).toString();
        }
    }
}