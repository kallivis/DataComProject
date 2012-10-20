import java.net.*;
import java.io.*;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket servsock = new ServerSocket(4444);
        File myFile = null;
        FileInputStream fis = null;
        OutputStream os = null;
        while (true) {
            Socket sock = servsock.accept();
            try {
            byte[] mybytearray = new byte[1024];
            os = sock.getOutputStream();

    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
        break;
    }
    myFile = new File(inputLine);
            fis = new FileInputStream(myFile);
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
