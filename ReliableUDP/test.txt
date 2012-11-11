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

    //This is the size of the packets being sent and recieved 
    private final static int PACKET_SIZE = 10;

    public static void main(String[] args)
    {
        // Checks if no command line args were given by usej
        if (args.length <= 0)
        {
            System.out.println("No file specified!");
            return;
        }
        //Sets the filename equal to the command line argument
        String filename = args[0];
        String message = filename;

        try
        {
            //Creates an InetAddress using localhost  
            InetAddress address = InetAddress.getByName("localhost");
            //The byte buffer used for data the client is sending
            byte[] sdata = new byte[PACKET_SIZE]; 
            //The byte buffer used for data the client is receiving
            byte[] rData = new byte[PACKET_SIZE];
            //Puts the message into the sending byte buffer
            sdata = message.getBytes();

            DatagramSocket socket = new DatagramSocket();
            // Socket timesout after 5 seconds to prevent infinite wait
            socket.setSoTimeout(5000);

            //Creates the packet and puts in the message 
            DatagramPacket packet = new DatagramPacket(sdata, sdata.length,
                    address, 3031);

            //Sends packet with the message to the server 
            socket.send(packet);

            //Checks if the message is Exit. If so the client exits as well
            if (message.equals("EXIT"))
            {
                System.out.println("Exit request sent.");
                return;
            }
            //Creates a local file to put the data recieved from the server in.
            File file = new File("local_" + message);
            //Opens a FileOutputStream to use to write the data in the above file.
            FileOutputStream fos = new FileOutputStream(file);
            //Creates the receiving packet for data comming form the server
            DatagramPacket rpacket = new DatagramPacket(rData, 
                    rData.length);

            System.out.println("Transfer started.");
            //The loop to received the packets of requested data.
            while (true) {
                //Receives a packet sent from server
                socket.receive(rpacket);
                //Checks if the packet size is 0.
                //If it is it knows the transfer is complete and client ends.
                if  (rpacket.getLength() == 0)
                {
                    System.out.println("File transferred");
                    break;
                }
                //If the packet has data it writes it into the local file.
                fos.write(rpacket.getData(), 0, rpacket.getLength());
                //If this packet is smaller than the agree upon size then it knows
                //that the transfer is complete and client ends.
                if (rpacket.getLength() < PACKET_SIZE) {
                    System.out.println("File transferred");
                    break;
                }
            }
            System.out.println("File length - " + file.length());
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (UnknownHostException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
