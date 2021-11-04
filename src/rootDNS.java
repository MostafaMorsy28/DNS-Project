import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class rootDNS {
    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket serverSocket = new DatagramSocket(50002);
            System.out.println("Server is connected");
            rootDNS.threading clientConnection = new rootDNS.threading(serverSocket);
            clientConnection.start();
        } catch (IOException ex) {
            Logger.getLogger(rootDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    static class threading extends Thread {
        DatagramSocket serverSocket;

        threading(DatagramSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        public void run() {
            byte[] URLBytes = new byte[4096];
            byte[] URLSentBytes = new byte[4096];
            byte[] IPSentBytes = new byte[4096];
            byte[] CNameSentBytes = new byte[4096];
            byte[] QueryTypeBytes = new byte[4096];
            Boolean IPCheck = false, CnameCheck = false;
            Boolean found = false;
            String clientURL;
            /* SENDER*///
            while (true) {
                DatagramPacket clientPacket = new DatagramPacket(URLBytes, URLBytes.length);
                try {
                    serverSocket.receive(clientPacket);
                    clientURL = new String(clientPacket.getData()).trim();
                    //System.out.println(System.getProperty("user.dir"));
                    System.out.println("Client requested: " + ": " + clientURL);
                    //System.out.println("URL: " + ": " + clientURL);
                    String queryType = "", cName = "";
                    /*/*Searching file****/
                    File dns1 = new File("C:\\Users\\David\\Desktop\\DNSProject\\src\\root_dns_table.txt");
                    FileReader fr = new FileReader(dns1);
                    BufferedReader br = new BufferedReader(fr);
                    String[] URLFile = null;
                    int wordCount = 0;
                    String s;
                    InetAddress clientIP = clientPacket.getAddress();
                    int port = clientPacket.getPort();
                    while ((s = br.readLine()) != null) {
                        URLFile = s.split(" ");
                        for (String word : URLFile) {
                            if (word.equals(clientURL)) {
                                found = true;
                                /////////////////////////////////////////////////////////////
                                // URL //////////////////////////////////////////////////////
                                String url = URLFile[0];
                                byte[] res = new byte[4096];
                                res = url.getBytes();
                                DatagramPacket urlPacket = new DatagramPacket(res, res.length, clientIP, port);
                                /////////////////////////////////////////////////////////////
                                // Server Name //////////////////////////////////////////////
                                String serverName = "root DNS";
                                res = new byte[4096];
                                res = serverName.getBytes();
                                DatagramPacket serverNamePacket = new DatagramPacket(res, res.length, clientIP, port);
                                //serverSocket.send(serverNamePacket);
                                /////////////////////////////////////////////////////////////
                                // IP Address ///////////////////////////////////////////////
                                String ipAddress = URLFile[1];
                                res = new byte[4096];
                                res = ipAddress.getBytes();
                                DatagramPacket ipAddressPacket = new DatagramPacket(res, res.length, clientIP, port);
                                //serverSocket.send(myServerPacket);
                                /////////////////////////////////////////////////////////////
                                // Query type and cName /////////////////////////////////////
                                int size = URLFile.length;
                                if (size == 2){
                                    queryType = "A";
                                    cName = "NULL";
                                }else if (size == 3){
                                    queryType = "A, CNAME";
                                    cName = URLFile[2];
                                }
                                res = new byte[4096];
                                res = queryType.getBytes();
                                DatagramPacket queryPacket = new DatagramPacket(res, res.length, clientIP, port);
                                res = new byte[4096];
                                res = cName.getBytes();
                                DatagramPacket cNamePacket = new DatagramPacket(res, res.length, clientIP, port);
                                ///////////////////////////////////////////////////////////////
                                // Outputs ////////////////////////////////////////////////////
                                System.out.println("URL:: " + url);
                                System.out.println("Query type= " + queryType);
                                System.out.println("IP Address:: " + ipAddress);
                                if (!cName.equals("NULL")) {
                                    System.out.println("Canonical name: " + cName);
                                    System.out.println("Aliases: " + url);
                                }
                                ///////////////////////////////////////////////////////////////
                                // Send informations to the client ////////////////////////////
                                serverSocket.send(urlPacket);
                                serverSocket.send(ipAddressPacket);
                                serverSocket.send(queryPacket);
                                serverSocket.send(cNamePacket);
                                serverSocket.send(serverNamePacket);
                                break;
                            }
                        }
                        if (found){
                            break;
                        }
                    }
                    if (!found) {
                        int serverport = 50003;
                        InetAddress serverIP = InetAddress.getByName("localhost");
                        byte[] res = new byte[4096];
                        byte[] req = new byte[4096];
                        req = clientURL.getBytes();
                        DatagramPacket server2serverpacket = new DatagramPacket(req, req.length, serverIP, serverport);
                        serverSocket.send(server2serverpacket);
                        // get ServerName from server 2
                        ///////////////////////////////////////////////////////////////
                        // URL ////////////////////////////////////////////////////////
                        res = new byte[4096];
                        req = new byte[4096];
                        server2serverpacket = new DatagramPacket(res, res.length);
                        serverSocket.receive(server2serverpacket);
                        String URL = new String(server2serverpacket.getData()).trim();
                        req = URL.getBytes();
                        DatagramPacket urlPacket = new DatagramPacket(req, req.length, clientIP, port);
                        ///////////////////////////////////////////////////////////////
                        // IP Address /////////////////////////////////////////////////
                        res = new byte[4096];
                        req = new byte[4096];
                        server2serverpacket = new DatagramPacket(res, res.length);
                        serverSocket.receive(server2serverpacket);
                        String ipAddress = new String(server2serverpacket.getData()).trim();
                        req = ipAddress.getBytes();
                        DatagramPacket ipAddressPacket = new DatagramPacket(req, req.length, clientIP, port);
                        ///////////////////////////////////////////////////////////////
                        // Query Type /////////////////////////////////////////////////
                        res = new byte[4096];
                        req = new byte[4096];
                        server2serverpacket = new DatagramPacket(res, res.length);
                        serverSocket.receive(server2serverpacket);
                        queryType = new String(server2serverpacket.getData()).trim();
                        req = queryType.getBytes();
                        DatagramPacket queryTypePacket = new DatagramPacket(req, req.length, clientIP, port);
                        ///////////////////////////////////////////////////////////////
                        // Canonical Name /////////////////////////////////////////////
                        res = new byte[4096];
                        req = new byte[4096];
                        server2serverpacket = new DatagramPacket(res, res.length);
                        serverSocket.receive(server2serverpacket);
                        cName = new String(server2serverpacket.getData()).trim();
                        req = cName.getBytes();
                        DatagramPacket cNamePacket = new DatagramPacket(req, req.length, clientIP, port);
                        ///////////////////////////////////////////////////////////////
                        // Server Name ////////////////////////////////////////////////
                        res = new byte[4096];
                        req = new byte[4096];
                        server2serverpacket = new DatagramPacket(res, res.length);
                        serverSocket.receive(server2serverpacket);
                        String serverName = new String(server2serverpacket.getData()).trim();
                        req = serverName.getBytes();
                        DatagramPacket serverNamePacket = new DatagramPacket(req, req.length, clientIP, port);
                        ////////////////////////////////////////////////////////////////

                        ////////////////////////////////////////////////////////////////
                        serverSocket.send(urlPacket);
                        serverSocket.send(ipAddressPacket);
                        serverSocket.send(queryTypePacket);
                        serverSocket.send(cNamePacket);
                        serverSocket.send(serverNamePacket);
                        if (serverName.equals("Authoritative DNS")){
                            ////////////////////////////////////////////////////////////////
                            res = new byte[4096];
                            req = new byte[4096];
                            server2serverpacket = new DatagramPacket(res, res.length);
                            serverSocket.receive(server2serverpacket);
                            String authorIP = new String(server2serverpacket.getData()).trim();
                            req = authorIP.getBytes();
                            DatagramPacket authorIPpacket = new DatagramPacket(req, req.length, clientIP, port);
                            serverSocket.send(authorIPpacket);
                            ////////////////////////////////////////////////////////////////
                        }
                    }
                    URLBytes = new byte[4096];
                    URLSentBytes = new byte[4096];
                    IPSentBytes = new byte[4096];
                    CNameSentBytes = new byte[4096];
                    found=false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}