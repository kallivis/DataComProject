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

public class Client implements Settings {

  //This is the size of the packets being sent and recieved 
  static int base = 0;
  static int  nextSeq = 0;
  static boolean done = false;
  static DatagramPacket[] windowPackets;

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

      DatagramPacket[] windowPackets = new DatagramPacket[WINDOW_SIZE];
      int count = 0;
      int count2 = 0;
      while (!done) {
        //Receives a packet sent from server
        socket.receive(rpacket);

        //Puts the String "ACK" into Bytes
        byte[] cmd = "ACK".getBytes();
        //Creates and sends the ACK packet
        byte[] info = new byte[INT_SIZE];
        byte[] data = new byte[rpacket.getLength() - INT_SIZE];
        System.arraycopy(rpacket.getData(), 0, info, 0, 
            INT_SIZE);
        System.arraycopy(rpacket.getData(), INT_SIZE, data, 0, 
            rpacket.getLength() - INT_SIZE);

        int packNum = ByteConverter.toInt(info, 0);
        if (packNum-(base % WINDOW_SIZE) >= 0)
          windowPackets[packNum-(base % WINDOW_SIZE)] = rpacket; 
        byte[] ackNum = ByteConverter.toBytes(packNum);
     //   System.out.println("ACKNUM "+packNum);
    //    System.out.println("BASE "+base);
        packet = new DatagramPacket(ackNum, ackNum.length, address, 3031);
          socket.send(packet);
          while( windowPackets[0] != null){
            DatagramPacket nextPack = windowPackets[0];

        System.arraycopy(nextPack.getData(), 0, info, 0, 
            INT_SIZE);
      //  System.out.println("WROTE: "+ByteConverter.toInt(info, 0));
       // System.out.println("WROTE#: "+count++);
        System.arraycopy(nextPack.getData(), INT_SIZE, data, 0, 
            nextPack.getLength() - INT_SIZE);
        //System.out.println("Num"+Client.toInt(info, 0));
        //Checks if the packet size is 0.
        //If it is it knows the transfer is complete and client ends.
        if (nextPack.getLength() == 0)
        {
          System.out.println("File transferred");
          done = true;
          break;
        }    
            fos.write(data, 0, data.length);
           count++; 
            DatagramPacket[] temp = new DatagramPacket[windowPackets.length];
              System.arraycopy(windowPackets, 1, temp, 0, windowPackets.length -1);
            windowPackets = temp;
            base++;
            //If the packet has data it writes it into the local file.
            //If this packet is smaller than the agree upon size then it knows
            //that the transfer is complete and client ends.
            if (nextPack.getLength() < PACKET_SIZE) {
              System.out.println("File transferred");
              //System.out.println("TOTAL: " + count);
          done = true;
              break;
            }


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
