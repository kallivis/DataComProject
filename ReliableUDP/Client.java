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
    private final static int PACKET_SIZE = 512;
    private final static int WINDOW_SIZE = 8;

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

            //Puts "Sync" into the send Data
            sdata = "SYNC".getBytes();            

            DatagramSocket socket = new DatagramSocket();
            // Socket timesout after 5 seconds to prevent infinite wait
            socket.setSoTimeout(5000);

            //Creates the packet and puts in the message 
            DatagramPacket packet = new DatagramPacket(sdata, sdata.length,
                    address, 3031);

            //Sends packet with the message to the server 
            socket.send(packet);
            
            //Creates the recieve packet
            DatagramPacket rpacket = new DatagramPacket(rData, 
                    rData.length);

            socket.receive(rpacket);
            //Pulls the string out of the recieved packet
            String cmd1 = new String(rpacket.getData(), 0, 
                    rpacket.getLength());
            //Checks if the server sent SYNACK
            if (cmd1.equals("SYNACK"))
            {
                //Puts the file named into the Send Data
                sdata = filename.getBytes();
                //Creates a Packet with the Filename
                packet = new DatagramPacket(sdata, sdata.length,
                    address, 3031);
                socket.send(packet);
            }
            else
                return;
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

            System.out.println("Transfer started.");
            //The loop to received the packets of requested data.
            while (true) {
                //Receives a packet sent from server
                socket.receive(rpacket);
                
                //Puts the String "ACK" into Bytes
                byte[] cmd = "ACK".getBytes();
                //Creates and sends the ACK packet
                packet = new DatagramPacket(cmd, cmd.length, address, 3031);
                socket.send(packet);
                byte[] info = new byte[4];
                byte[] data = new byte[rpacket.getData().length - 4];

                System.arraycopy(rpacket.getData(), 0, info, 0, 
                        4);
                System.arraycopy(rpacket.getData(), 4, data, 0, 
                        rpacket.getData().length - 4);
                System.out.println("Num"+Client.toInt(info, 0));
                //Checks if the packet size is 0.
                //If it is it knows the transfer is complete and client ends.
                if  (rpacket.getLength() == 0)
                {
                    System.out.println("File transferred");
                    break;
                }
                //If the packet has data it writes it into the local file.
                fos.write(data, 0, data.length);
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
   static byte[] toBytes(int i)
        {
            byte[] result = new byte[4];

            result[0] = (byte) (i >> 24);
            result[1] = (byte) (i >> 16);
            result[2] = (byte) (i >> 8);
            result[3] = (byte) (i /*>> 0*/);

            return result;
        }
       static int toInt(byte[] bytes, int offset) {
            int ret = 0;
            for (int i=0; i<4 && i+offset<bytes.length; i++) {
                ret <<= 8;
                ret |= (int)bytes[i] & 0xFF;
            }
            return ret;

        }
 
}
