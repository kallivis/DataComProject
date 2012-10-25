/*
 * Program Name:    Server.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         1.0 - Oct 22, 2012
 * Purpose:
 * Can send any requested file from the directory that Server.java is run from
 * to the directory Client.java is run from.
 */

import java.net.*;
import java.io.*;

public class Server {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(3031);

    File myFile = null;
    FileInputStream input = null;
    OutputStream output = null;

    //Listen for connectiona
    while (true) {
      Socket clientSocket = serverSocket.accept();

      try {
        byte[] mybytearray = new byte[1024];
        output = clientSocket.getOutputStream();

        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())); String inputLine;

        while ((inputLine = in.readLine()) != null) {
          break;
        }

        //Find file
        myFile = new File(inputLine);
        input = new FileInputStream(myFile);
        int count;

        //Send file
        while ((count = input.read(mybytearray)) >= 0) {
          output.write(mybytearray, 0, count);
        }

        output.flush();
      } 
      finally {
        input.close();
        output.close();
        clientSocket.close();

        System.out.println("Socket closed");
      }
    }
  }
}
