import java.net.*;
import java.io.*;

public class Server2 {

    public static void main(String[] args) throws IOException {
        ServerSocket servsock = new ServerSocket(4444);
        File myFile = new File("Sheep.jpg");
        FileInputStream fis = null;
        OutputStream os = null;
        while (true) {
            Socket sock = servsock.accept();
            try {
            byte[] mybytearray = new byte[1024];
            fis = new FileInputStream(myFile);
            os = sock.getOutputStream();

            int count;
            while ((count = fis.read(mybytearray)) >= 0) {
                os.write(mybytearray, 0, count);

            }
            os.flush();
            } finally {
            fis.close();
            os.close();
            sock.close();

            System.out.println("Socket closed");
            }
        }
    }
}
