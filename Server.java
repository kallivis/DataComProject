import java.net.*;
import java.io.*;

public class Server {
  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(4444);

      System.out.println("server start listening... ... ...");
    } catch (IOException e) {
      System.err.println("Could not listen on port: 4444.");
      System.exit(1);
    }

    Socket clientSocket = null;
    try {
      clientSocket = serverSocket.accept();
    } catch (IOException e) {
      System.err.println("Accept failed.");
      System.exit(1);
    }
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader in = new BufferedReader(
        new InputStreamReader(
          clientSocket.getInputStream()));
    String inputLine, outputLine;
  
    while ((inputLine = in.readLine()) != null) {
      System.out.println(inputLine);
    /*  File file = new File(inputLine);
      out.write(file);
*/
       FileWriter fw = new FileWriter(inputLine);
              BufferedWriter bufWriter = new BufferedWriter(fw);
               bufWriter.close();
    }
    out.close();
    in.close();
    clientSocket.close();
    serverSocket.close();
  }
}
