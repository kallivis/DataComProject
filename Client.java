/*
 * Program Name:    Client.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy, 
 * Version:         1.0 - Oct 22, 2012
 * Purpose:
 * Can recieve a file sent from Server.java.
 */

import java.io.*;
import java.net.Socket;

public class Client   {

  public static void main(String[] argv) throws IOException {

    Socket socket = new Socket("192.168.0.107", 4444);
    InputStream input = null;
    OutputStream output = null;
    PrintWriter out = null;

    byte[] mybytearray = new byte[1024000];

    //Establish connection
    try {

      input = socket.getInputStream();
      out = new PrintWriter(socket.getOutputStream(), true);
      String filename = argv[0];

      //Request file
      out.println(filename); 
      output = new FileOutputStream("LocalSheep.jpg");

      int count;

      //Save file
      while ((count = input.read(mybytearray)) >= 0) {
        output.write(mybytearray, 0, count);
      }
    }
    finally {
      output.close();
      input.close();
      socket.close();
    }
  }
}
