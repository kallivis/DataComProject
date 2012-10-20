import java.io.*;
import java.net.Socket;

public class Client2   {

    public static void main(String[] argv) throws IOException {
        Socket sock = new Socket("192.168.0.107", 4444);
        InputStream is = null;
        OutputStream fos = null;
        PrintWriter out = null;

        byte[] mybytearray = new byte[1024000];
        try {
            is = sock.getInputStream();
            out = new PrintWriter(sock.getOutputStream(), true);
            out.println("Sheep.jpg"); 
            fos = new FileOutputStream("LocalSheep.jpg");

            int count;
            while ((count = is.read(mybytearray)) >= 0) {
                fos.write(mybytearray, 0, count);
            }
        } finally {
            fos.close();
            is.close();
            sock.close();
        }
    }
}
