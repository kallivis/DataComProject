import java.io.*;
import java.net.Socket;

public class Client2   {

    public static void main(String[] argv) throws IOException {
        Socket sock = new Socket("localhost", 4444);
        InputStream is = null;
        FileOutputStream fos = null;

        byte[] mybytearray = new byte[1024];
        try {
            is = sock.getInputStream();
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
