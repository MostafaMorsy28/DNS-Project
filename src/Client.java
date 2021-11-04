
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            System.out.println("Client is active");
            Scanner in = new Scanner(System.in);
            int port = 50001;
            Boolean nullCheck = false;
            InetAddress serverIP = InetAddress.getByName("localhost");
            byte[] res = new byte[4096];
            byte[] req = new byte[4096];

            while (true) {
                System.out.println("Enter website name / Enter 'quit' to exit");
                System.out.println("Client: ");
                String input = in.nextLine();
                input = input.toLowerCase();
                req = new byte[4096];
                req = input.getBytes();
                DatagramPacket myClientPacket = new DatagramPacket(req, req.length, serverIP, port);
                clientSocket.send(myClientPacket);

                if (input.toLowerCase().equals("quit")) {
                    System.out.println("Socket is Closed");
                    clientSocket.close();
                    break;
                }

                /*Need more Dynamic way*/
                //System.out.println(System.getProperty("user.dir"));
                /////////////////////////////////////////////////
                // URL //////////////////////////////////
                DatagramPacket serverPacket = new DatagramPacket(res, res.length);
                clientSocket.receive(serverPacket);
                String urlOutput = new String(serverPacket.getData()).trim();
                /////////////////////////////////////////////////
                // Ip Address ///////////////////////////////////
                res = new byte[4096];
                serverPacket = new DatagramPacket(res, res.length);
                clientSocket.receive(serverPacket);
                String ipAddressOutput = new String(serverPacket.getData()).trim();
                /////////////////////////////////////////////////
                // Query ////////////////////////////////////////
                res = new byte[4096];
                serverPacket = new DatagramPacket(res, res.length);
                clientSocket.receive(serverPacket);
                String queryOutput = new String(serverPacket.getData()).trim();
                /////////////////////////////////////////////////
                // cName ////////////////////////////////////////
                res = new byte[4096];
                serverPacket = new DatagramPacket(res, res.length);
                clientSocket.receive(serverPacket);
                String cNameOutput = new String(serverPacket.getData()).trim();
                /////////////////////////////////////////////////
                // Server Name //////////////////////////////////
                res = new byte[4096];
                serverPacket = new DatagramPacket(res, res.length);
                clientSocket.receive(serverPacket);
                String serverNameoutPut = new String(serverPacket.getData()).trim();
                /////////////////////////////////////////////////
                if (urlOutput.equals("NULL")){
                    System.out.println("URL Not Found!");
                    System.out.println();
                    continue;
                }
                System.out.print("Reply from DNS_Server is: " + "URL: " + urlOutput);
                System.out.print(" IP Address: " + ipAddressOutput);
                System.out.println(" Query type: " + queryOutput);
                System.out.println("Server Name: " + serverNameoutPut);
                if (!cNameOutput.equals("NULL")){
                    System.out.println("Canonical name: " + cNameOutput);
                    System.out.println("Aliases: " + input);
                }
                if (serverNameoutPut.equals("Authoritative DNS")){
                    res = new byte[4096];
                    serverPacket = new DatagramPacket(res, res.length);
                    clientSocket.receive(serverPacket);
                    String authorIPoutput = new String(serverPacket.getData()).trim();

                    System.out.println("Authoritative answer:");
                    System.out.println("Name: authoritative_dns_table.txt");
                    System.out.println("IP= "+ authorIPoutput);

                }
                System.out.println();
                res = new byte[4096];
                req = new byte[4096];
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
