import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) throws IOException {

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try 
    {
      socket = new Socket("192.168.66.128", 4444);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } 
    catch (UnknownHostException e) 
    {
      System.err.println("Don't know about host.");
      System.exit(1);
    } 
    catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to Host.");
      System.exit(1);
    }

    //Input file name
    String filename = args[0];
    System.out.println(filename);

    //Write file to out.println(args[1])
    out.println(filename);

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String inputline;
    while ((inputline = in.readLine()) != null) {
    System.out.println(inputline);
    }

    out.close();
    in.close();
    stdIn.close();
    socket.close();

  }
}
