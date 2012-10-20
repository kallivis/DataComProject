import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) throws IOException {

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try 
    {
      socket = new Socket("localhost", 4444);
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

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String fromServer;
    String nextLine;
    String fromUser;
    while ((fromServer = in.readLine()) != null) {
      if(fromServer.equals("start"))
      {
        fromServer = "";

        while(true)
        {
          nextLine = in.readLine();
          if (nextLine.equals("end"))
              break;
          else
              fromServer  = fromServer.concat("\n"+nextLine);
        }
      }
      System.out.println(fromServer);
      if (fromServer.equals("Quit"))
      break;

      fromUser = stdIn.readLine();
      if (fromUser != null) {
        out.println(fromUser);
      }
    }

    out.close();
    in.close();
    stdIn.close();
    socket.close();
  }
}
