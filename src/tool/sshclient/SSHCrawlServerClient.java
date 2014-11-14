/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 package tool.sshclient;
  
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
 
import org.apache.sshd.common.AbstractFactoryManager;
import org.apache.sshd.common.util.NoCloseInputStream;
import org.apache.sshd.common.util.NoCloseOutputStream;

/**
 * Entry point for the client side of the SSH protocol.
 *
 * The default configured client can be created using
 * the {@link #setUpDefaultClient()}.  The next step is to
 * start the client using the {@link #start()} method.
 *
 * Sessions can then be created using on of the
 * {@link #connect(String, int)} or {@link #connect(java.net.SocketAddress)}
 * methods.
 *
 * The client can be stopped at anytime using the {@link #stop()} method.
 *
 * Following is an example of using the SshClient:
 * <pre>
 *    SshClient client = SshClient.setUpDefaultClient();
 *    client.start();
 *    try {
 *        ClientSession session = client.connect(host, port);
 *
 *        int ret = ClientSession.WAIT_AUTH;
 *        while ((ret & ClientSession.WAIT_AUTH) != 0) {
 *            System.out.print("Password:");
 *            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
 *            String password = r.readLine();
 *            session.authPassword(login, password);
 *            ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
 *        }
 *        if ((ret & ClientSession.CLOSED) != 0) {
 *            System.err.println("error");
 *            System.exit(-1);
 *        }
 *        ClientChannel channel = session.createChannel("shell");
 *        channel.setIn(new NoCloseInputStream(System.in));
 *        channel.setOut(new NoCloseOutputStream(System.out));
 *        channel.setErr(new NoCloseOutputStream(System.err));
 *        channel.open();
 *        channel.waitFor(ClientChannel.CLOSED, 0);
 *        session.close();
 *    } finally {
 *        client.stop();
 *    }
 * </pre>
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class SSHCrawlServerClient extends AbstractFactoryManager {

    /*=================================
          Main class implementation
     *=================================*/

    public static void main(String[] args) throws Exception {
        
        int port = 22;
        String host = null;
        String login = null;
        List<String> command = null;
        int logLevel = 0;
        boolean error = false;

        args = new String[6];
        args[0] = "-v";
        args[1] = "-l";
        args[2] = "kamir";
        args[3] = "-p";
        args[4] = "22";
        args[5] = "192.168.3.171";
        
        
        for (int i = 0; i < args.length; i++) {
            if (command == null && "-p".equals(args[i])) {
                if (i + 1 >= args.length) {
                    System.err.println("option requires an argument: " + args[i]);
                    error = true;
                    break;
                }
                port = Integer.parseInt(args[++i]);
            } else if (command == null && "-l".equals(args[i])) {
                if (i + 1 >= args.length) {
                    System.err.println("option requires an argument: " + args[i]);
                    error = true;
                    break;
                }
                login = args[++i];
            } else if (command == null && "-v".equals(args[i])) {
                logLevel = 1;
            } else if (command == null && "-vv".equals(args[i])) {
                logLevel = 2;
            } else if (command == null && "-vvv".equals(args[i])) {
                logLevel = 3;
            } else if (command == null && args[i].startsWith("-")) {
                System.err.println("illegal option: " + args[i]);
                error = true;
                break;
            } else {
                if (host == null) {
                    host = args[i];
                } else {
                    if (command == null) {
                        command = new ArrayList<String>();
                    }
                    command.add(args[i]);
                }
            }
        }
        if (host == null) {
            System.err.println("hostname required");
            error = true;
        }
        if (error) {
            System.err.println("usage: ssh [-v[v][v]] [-l login] [-p port] hostname [command]");
            System.exit(-1);
        }

        // TODO: handle log level

        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try {
            ClientSession session = client.connect(host, port).await().getSession();

            int ret = ClientSession.WAIT_AUTH;
            while ((ret & ClientSession.WAIT_AUTH) != 0) {
                System.out.print("Password:");
                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                String password = r.readLine();
                session.authPassword(login, password);
                ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
            }
            if ((ret & ClientSession.CLOSED) != 0) {
                System.err.println("error");
                System.exit(-1);
            }
            ClientChannel channel;
            if (command == null) {
                channel = session.createChannel(ClientChannel.CHANNEL_SHELL);
                channel.setIn(new NoCloseInputStream(System.in));
            } else {
                System.out.println( "> CMD: " );
                System.out.println( "* " + command );
                channel = session.createChannel(ClientChannel.CHANNEL_EXEC);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer w = new OutputStreamWriter(baos);
                for (String cmd : command) {
                    w.append(cmd).append(" ");
                }
                w.append("\n");
                w.flush();
                w.close();
                channel.setIn(new ByteArrayInputStream(baos.toByteArray()));
            }
            channel.setOut(new NoCloseOutputStream(System.out));
            channel.setErr(new NoCloseOutputStream(System.err));
            channel.open().await();
            
            channel.waitFor(ClientChannel.CLOSED, 0);
            session.close(false);
        } finally {
            client.stop();
        }
    }

    
    void sendCommend(String text) throws Exception {
        System.out.println( "> " + text );
        
        
      
        
        List<String> command = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(text);
        while( st.hasMoreTokens() ) {
            String t = st.nextToken();
            System.out.println( t );
            command.add(t);
        }
        
        ClientChannel channel;
            if (command == null) {
                channel = session.createChannel(ClientChannel.CHANNEL_SHELL);
                channel.setIn(new NoCloseInputStream(System.in));
            } else {
                channel = session.createChannel(ClientChannel.CHANNEL_EXEC);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer w = new OutputStreamWriter(baos);
                for (String cmd : command) {
                    w.append(cmd).append(" ");
                }
                w.append("\n");
                w.close();
                channel.setIn(new ByteArrayInputStream(baos.toByteArray()));
                
                channel.setIn(new ByteArrayInputStream(baos.toByteArray()));
            channel.setOut(new NoCloseOutputStream(System.out));
            channel.setErr(new NoCloseOutputStream(System.err));
            channel.open().await();
            channel.waitFor(ClientChannel.CLOSED, 0);
            session.close(false);
            }

            System.out.println( "***");
                 
            
        
        
    }

    ClientSession session = null;
    
    void login() throws Exception {
        
        System.out.println( "LOGIN now ...");        
        
        int port = 22;
        String host = null;
        String login = System.getProperty("user.name");
        List<String> command = null;
        int logLevel = 0;
        boolean error = false;

        String[] args = new String[6];
        args[0] = "-v";
        args[1] = "-l";
        args[2] = "root";
        args[3] = "-p";
        args[4] = "22";
        args[5] = "master2";
        
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < args.length; i++) {
            if (command == null && "-p".equals(args[i])) {
                if (i + 1 >= args.length) {
                    System.err.println("option requires an argument: " + args[i]);
                    error = true;
                    break;
                }
                port = Integer.parseInt(args[++i]);
            } else if (command == null && "-l".equals(args[i])) {
                if (i + 1 >= args.length) {
                    System.err.println("option requires an argument: " + args[i]);
                    error = true;
                    break;
                }
                login = args[++i];
            } else if (command == null && "-v".equals(args[i])) {
                logLevel = 1;
            } else if (command == null && "-vv".equals(args[i])) {
                logLevel = 2;
            } else if (command == null && "-vvv".equals(args[i])) {
                logLevel = 3;
            } else if (command == null && args[i].startsWith("-")) {
                System.err.println("illegal option: " + args[i]);
                error = true;
                break;
            } else {
                if (host == null) {
                    host = args[i];
                } else {
                    if (command == null) {
                        command = new ArrayList<String>();
                    }
                    command.add(args[i]);
                }
            }
        }
        if (host == null) {
            System.err.println("hostname required");
            error = true;
        }
        if (error) {
            System.err.println("usage: ssh [-v[v][v]] [-l login] [-p port] hostname [command]");
            System.exit(-1);
        }

        // TODO: handle log level

        SshClient client = SshClient.setUpDefaultClient();
        client.start();
 
        
            session = client.connect(host, port).await().getSession();

            int ret = ClientSession.WAIT_AUTH;
            while ((ret & ClientSession.WAIT_AUTH) != 0) {
                
                String password = getPassword();
                
                session.authPassword(login, password);
                
               
                
                ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
            }
            if ((ret & ClientSession.CLOSED) != 0) {
                System.err.println("error");
                System.exit(-1);
            }
            
             System.out.println( "***");
            
            // now we can operate on the cahnnel ...
   
    }

    private String getPassword() {
        
//                System.out.print("Password:");
//                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
//                String password = r.readLine();
        return "cloudera";
                
    }
       
        

}
