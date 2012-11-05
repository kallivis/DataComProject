/*
 * Program Name:    Client.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829
 * Version:         2.0 - Nov 2, 2012
 * Purpose:
 * Can recieve a file sent from Server.java (using UDP).
 */

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args)
    {
        if (args.length <= 0)
        {
            System.out.println("No file specified!");
            return;
        }
        String filename = args[0];
        String message = filename;
        try
        {

            File file = new File("local_" + message);
            FileOutputStream fos = new FileOutputStream(file);

            InetAddress address = InetAddress.getByName("localhost");

            byte[] sdata = new byte[512]; 
            byte[] rData = new byte[512];
            sdata = message.getBytes();

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            DatagramPacket packet = new DatagramPacket(sdata, sdata.length,
                    InetAddress.getByName("localhost"), 3031);

            socket.send(packet);

            if (message.equals("EXIT"))
            {
                System.out.println("Exit request sent.");
                return;
            }

            DatagramPacket rpacket = new DatagramPacket(rData, 
                rData.length);

            System.out.println("Transfer started.");
            while (true) {
                socket.receive(rpacket);
                fos.write(rpacket.getData(), 0, rpacket.getLength());
                if (rpacket.getLength() < 512) {
                    System.out.println("File transferred");
                    break;
                }
            }

            System.out.println("File length - " + file.length());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
