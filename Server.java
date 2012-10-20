import java.net.*;
import java.io.*;

public class Server {
  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = null;
    Board board = new Board();
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
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(
          clientSocket.getInputStream()));
    String inputLine, outputLine;
    while ((inputLine = in.readLine()) != null) {
      out.println(outputLine);
      if (outputLine.equals("quit")|| winner != null)
        break;
    }
    out.close();
    in.close();
    clientSocket.close();
    serverSocket.close();
  }
}
