import java.io.*;
import java.net.Socket;

public class Client   {

  public static void main(String[] argv) throws IOException {

    Socket socket = new Socket("192.168.0.107", 4444);
    InputStream input = null;
    OutputStream output = null;
    PrintWriter out = null;

    byte[] mybytearray = new byte[1024000];

    try {

      input = socket.getInputStream();
      out = new PrintWriter(socket.getOutputStream(), true);
      String filename = argv[0];
      out.println(filename); 
      output = new FileOutputStream("LocalSheep.jpg");

      int count;

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
