import java.net.*;
import java.io.*;

public class Server {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(3031);

    File myFile = null;
    FileInputStream input = null;
    OutputStream output = null;

    while (true) {
      Socket clientSocket = serverSocket.accept();
      
      try {
        byte[] mybytearray = new byte[1024];
        output = sock.getOutputStream();

        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          break;
        }

        myFile = new File(inputLine);
        input = new FileInputStream(myFile);
        int count;
        
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
