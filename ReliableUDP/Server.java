/* * Program Name:    Server.java * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         3.0 - Nov 30, 2012
 * Purpose:
 * Can send any requested file (using UDP) from the directory that 
 * Server.java is run from to the directory Client.java is run from.
 */

import java.io.*;
import java.net.*;
import java.nio.*;

public class Server implements Settings  {



  public static void main(String[] args) throws Exception
  {
    //The buffer for the initial message received from the Client
    byte[] buff = new byte[PACKET_SIZE];

    //Creates the Packet to receive the request from the Client
    DatagramPacket packet = new DatagramPacket(buff, buff.length);
    //Opens a Datagram Socket that the server is running on
    //Opens it on port 3031
    DatagramSocket socket = new DatagramSocket(3031);

    System.out.println("Server started at 3031 ...");

    //While server is running, Constantly listens for client requests
    while (true) {
      socket.receive(packet);
      //Receives the request packet from the client
      //starts a new thread to send the packets of file
      //requested by the client.
      new TransferThread(socket, packet).run();
    }

  }
}
