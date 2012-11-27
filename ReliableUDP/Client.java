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
import java.util.zip.CRC32;
import java.util.ArrayList;
import java.util.Scanner;

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
      System.out.println("No user specified!");
      return;
    }
    //Sets the filename equal to the command line argument
    //String filename = args[0];
    String filename ="";
    String message = args[0];

    try
    {
      //Creates an InetAddress using localhost  
      InetAddress address = InetAddress.getByName("localhost");
      //The byte buffer used for data the client is sending
      byte[] sdata = new byte[PACKET_SIZE]; 
      //The byte buffer used for data the client is receiving
      byte[] rData = new byte[PACKET_SIZE];

      //Puts "Sync" into the send Data
      sdata = ("SYNC"+message).getBytes();            

      DatagramSocket socket = new DatagramSocket();
      // Socket timesout after 5 seconds to prevent infinite wait
      socket.setSoTimeout(1000);

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
      if (cmd1.substring(0,6).equals("SYNACK"))
      {
        String dirList = cmd1.substring(6);
        System.out.println(dirList);
        System.out.flush();
        System.out.println("Please type the file you wish to receive, or type " +
                           "EXIT to close the server");
        Scanner scan = new Scanner(System.in); 
        filename = scan.next();
        //Puts the file named into the Send Data
        sdata = filename.getBytes();
        //Creates a Packet with the Filename
        packet = new DatagramPacket(sdata, sdata.length,
            address, 3031);
        socket.send(packet);
      }
      else
        return;
      //Checks if the filename is Exit. If so the client exits as well
      if (filename.equals("EXIT"))
      {
        System.out.println("Exit request sent.");
        return;
      }
      //Creates a local file to put the data recieved from the server in.
      if (isNumber(filename))
      {
          System.out.println("What would you like to name this file?");
          Scanner scan = new Scanner(System.in);
          filename = scan.next();
          System.out.println("File name requested: " + filename);
      }
      File file = new File("local_" + filename);
      //Opens a FileOutputStream to use to write the data in the above file.
      FileOutputStream fos = new FileOutputStream(file);
      //Creates the receiving packet for data comming form the server

      System.out.println("Transfer started.");
      //The loop to received the packets of requested data.

      windowPackets = new DatagramPacket[MAX_SEQ];
      CRC32 crc = new CRC32();
      while (!done) {
        //Receives a packet sent from server
        socket.receive(rpacket);

        //Puts the String "ACK" into Bytes
        //Creates and sends the ACK packet
        byte[] info = new byte[INT_SIZE];
        byte[] code = new byte[CHECKSUM_SIZE];
        byte[] data = new byte[rpacket.getLength() - SAC_SIZE];
        byte[] data2 = new byte[rpacket.getLength() - CHECKSUM_SIZE];
        System.arraycopy(rpacket.getData(), 0, info, 0, 
            INT_SIZE);
        System.arraycopy(rpacket.getData(), INT_SIZE, data, 0, 
            rpacket.getLength() - SAC_SIZE);
        System.arraycopy(rpacket.getData(), 0, data2, 0, 
            rpacket.getLength() - CHECKSUM_SIZE);
        System.arraycopy( rpacket.getData(),rpacket.getLength() - CHECKSUM_SIZE , code, 0, 
            CHECKSUM_SIZE);
        int packNum2 = ByteConverter.toInt(info, 0);
        int sCode = ByteConverter.toInt(code,0);
        int packNum = ByteConverter.toInt(info, 0);
        crc.reset();
        crc.update(data2, 0, data2.length);
        int cCode = (int)crc.getValue();

        byte[] ackNum = ByteConverter.toBytes(packNum);
        ArrayList<Integer> expecting  = new ArrayList<Integer>();
        if (cCode == sCode)
        {
          for (int i = 0; i < WINDOW_SIZE; i++)
          {
            if (base + i >= MAX_SEQ)
              expecting.add((base  + i ) - MAX_SEQ);
            else
              expecting.add(base+i);
          }
          if (packNum == base)
          {

            ackNum = ByteConverter.toBytes(packNum);
            packet = new DatagramPacket(ackNum, ackNum.length, address, 3031);
            {

              socket.send(packet);
            }

            if (rpacket.getLength() == 0)
            {
              done = true;
              break;
            }    
            fos.write(data, 0, data.length);
            base++;
            if (base == MAX_SEQ)
              base = 0;
            for (int i = 0; i < WINDOW_SIZE; i++)
            {
              if (base + i >= MAX_SEQ)
                expecting.add((base  + i ) - MAX_SEQ);
              else
                expecting.add(base+i);
            }


            //If the packet has data it writes it into the local file.
            //If this packet is smaller than the agree upon size then it knows
            //that the transfer is complete and client ends.
            if (rpacket.getLength() < PACKET_SIZE) {
              System.out.println("File transferred");
              done = true;
              break;
            }

            while(windowPackets[base] != null)
            {
              DatagramPacket  nextPacket = windowPackets[base];
              windowPackets[base] = null;

              data = new byte[nextPacket.getLength() - SAC_SIZE];
              System.arraycopy(nextPacket.getData(), INT_SIZE, data, 0, 
                  nextPacket.getLength() - SAC_SIZE);
              System.arraycopy(nextPacket.getData(), 0, info, 0, 
                  INT_SIZE);

              packNum = ByteConverter.toInt(info,0); 


              if (nextPacket.getLength() == 0)
              {
                System.out.println("File transferred");
                done = true;
                break;
              }    
              fos.write(data, 0, data.length);
              base++;
              if (base == MAX_SEQ)
                base = 0;
              expecting.clear();
              for (int i = 0; i < WINDOW_SIZE; i++)
              {
                if (base + i >= MAX_SEQ)
                  expecting.add((base  + i ) - MAX_SEQ);
                else
                  expecting.add(base+i);
              }

              //If the packet has data it writes it into the local file.
              //If this packet is smaller than the agree upon size then it knows
              //that the transfer is complete and client ends.
              if (nextPacket.getLength() < PACKET_SIZE) {
                System.out.println("File transferred");
                done = true;
                break;
              }

            }

          }
          else if (expecting.contains(packNum))
          {
            windowPackets[packNum] = rpacket  ;
            System.arraycopy(rpacket.getData(), 0, info, 0, 
                INT_SIZE);
            packNum = ByteConverter.toInt(info,0); 
            ackNum = ByteConverter.toBytes(packNum);
            packet = new DatagramPacket(ackNum, ackNum.length, address, 3031);
            socket.send(packet);
          }
          else  
          {
            System.arraycopy(rpacket.getData(), 0, info, 0, 
                INT_SIZE);
            packNum = ByteConverter.toInt(info,0); 
            ackNum = ByteConverter.toBytes(packNum);
            packet = new DatagramPacket(ackNum, ackNum.length, address, 3031);
            socket.send(packet);
          }


        }
        else 
          System.out.println("ERROR");


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
    
  //Check if integer is a string
  public static boolean isNumber(String str)
  {
      try
      {
          int i = Integer.parseInt(str);
      }
      catch(NumberFormatException e)
      {
          return false;
      }
      return true;
  }

}
