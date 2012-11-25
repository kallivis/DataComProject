/*
 * Program Name:    Server.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         2.0 - Nov 2, 2012
 * Purpose:
 * Can send any requested file (using UDP) from the directory that Server.java is run from
 * to the directory Client.java is run from.
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.HashMap;
import java.util.zip.CRC32;

public class Server implements Settings {

  //This is the size of the packets being sent and recieved 
  static int base = 0;
  static int  nextSeq = 0;
  static int  totalSeq = 0;
  public static HashMap<Integer, packetTimer> timers = new HashMap<Integer, packetTimer>();
  static DatagramPacket[] windowPackets;
  static DatagramSocket socket;

  public static void main(String[] args) throws Exception
  {
    //The buffer for the initial message received from the Client
    byte[] buff = new byte[PACKET_SIZE];

    //Creates the Packet to receive the request from the Client
    DatagramPacket packet = new DatagramPacket(buff, buff.length);
    //Opens a Datagram Socket that the server is running on
    //Opens it on port 3031
    socket = new DatagramSocket(3031);


    byte[] sdata = new byte[PACKET_SIZE]; 
    System.out.println("Server started at 3031 ...");

    //While server is running, Constantly listens for client requests
    while (true) {
      //Receives the request packet from the client
      socket.receive(packet);
      //Creates a String from the packet Message
      String cmd = new String(packet.getData(), 0, 
          packet.getLength());
      //Checks if Client sent the SYNC Command
      if (cmd.equals("SYNC"))
      {
        //Puts the String "SYNACK" into bytes
        sdata = "SYNACK".getBytes();
        //Creates a packet for the SYNACK
        DatagramPacket spacket = new DatagramPacket(sdata, sdata.length,
            packet.getAddress(), packet.getPort());
        socket.send(spacket);
        //Recieves the Packet with the Filename
        socket.receive(packet);
        String filename = new String(packet.getData(), 0, 
            packet.getLength());
        if (filename.equals("EXIT"))
        {
          System.out.println("Server Closing.");
          System.exit(0);
        }
        //starts a new thread to send the packets of file
        //requested by the client.
        new Server.TransferThread(socket, packet).run();
      }

    }
  }
  //Thread class for Transfering Packets
  public static class TransferThread implements Runnable
  {
    //Sets up a packet and Socket
    private DatagramPacket packet;
    private DatagramSocket socket;

    //Constructer for the thread Class
    //Takes a Socket and Packet as parameters
    public TransferThread(DatagramSocket socket, DatagramPacket packet) 
    {
      this.packet = packet;
      this.socket = socket;
      base = 0;
      nextSeq = 0;
      totalSeq = 0;
      timers = new HashMap<Integer, packetTimer>();


    }

    //The run method of the thread used to check on the request
    //and if a file is requested starts the sendStream 
    public void run()
    {
      //Uses the received packet data from the client to 
      //setup a string with the requested filename
      String filename = new String(packet.getData(), 0, 
          packet.getLength());
      System.out.println("Requested: " + filename);
      try
      {
        //Starts the SendStream for the given filename
        SendStream(filename, socket);
      }
      catch (IOException e) {
        e.printStackTrace();
      } 

    }

    //Method that takes a filename as it's param
    //and sends the requested file in packets
    //to the client that requested it. 
    private void SendStream(String fileName, DatagramSocket socket) throws IOException
    {
      //Opens a file and a makes a file object from the
      //requested filename for reading.
      File file = new File(fileName);

      //Creates a fileInputStream for reading in the file into a buffer
      FileInputStream fis = new FileInputStream(file);
      //Creates a DatagramPacket packet that will be used to send 
      //data to the client
      DatagramPacket pack;

      //Size that will hold the size of the buffer being sent
      int size = 0;
      // The remaining size that is left to send of the file
      int remainingSize = (int) file.length(); 
      int totalPackets = (int) Math.ceil(remainingSize/DATA_SIZE)+1;
      //The buffer that the file will be read into with a max size
      //determined by the PACKET_SIZE constant
      byte[] buffer = new byte[DATA_SIZE];
      System.out.println("Transfer started...");

      socket.setSoTimeout(5000);
      windowPackets = new DatagramPacket[totalPackets];
      CRC32 crc = new CRC32();
      while (true)
      {
        //System.out.println("START OF WHILE TRUE");
        int k = 0;
        for (int i = 0; i < totalPackets; i++)
        {
          byte[] packInfo = ByteConverter.toBytes(k);
          k ++;
          if (k == MAX_SEQ)
            k = 0;

          //Reads part of the file into the buffer and sets the size of
          //the amount read
          size = fis.read(buffer);
          //Updates the remaining size by the amount read above
          remainingSize -= size;

          //Creates a new buffer the size of the amount read
          //This is in case the size is less than the PACKET_SIZE 
          //constant, so that the buffer is of exact size of the
          //data that we are sending
          int headerSize = size + SAC_SIZE;
          byte[] sizeBuff = new byte[headerSize];
          byte[] sizeBuff2 = new byte[headerSize-CHECKSUM_SIZE];

          //  System.out.println("CRC BYTES "+ByteConverter.longToBytes(crc.getValue()));
          // System.out.println("CRC BYTES LENGTH "+ByteConverter.longToBytes(crc.getValue()).length);

          //Copies the buffer into this new buffer
          System.arraycopy(packInfo,0, sizeBuff, 0,INT_SIZE );
          System.arraycopy(packInfo,0, sizeBuff2, 0,INT_SIZE );
          System.arraycopy(buffer,0, sizeBuff, INT_SIZE, size);
          System.arraycopy(buffer,0, sizeBuff2, INT_SIZE, size);
          crc.reset();
          crc.update(sizeBuff2, 0, sizeBuff2.length);
          byte[] code = ByteConverter.toBytes((int)crc.getValue());
          System.arraycopy(code,0, sizeBuff, size+INT_SIZE,CHECKSUM_SIZE);
          //Creates a new packet with the above buffer of data.
          //Uses the initial client packet to get the client's 
          //address and port.

          windowPackets[i] = new DatagramPacket(sizeBuff, headerSize, packet.getAddress(), packet.getPort());
        }
        while ( totalSeq != totalPackets  || !timers.isEmpty())
        {
          while(timers.size() < WINDOW_SIZE && totalSeq < totalPackets )
          {
            socket.send(windowPackets[totalSeq]);
            timers.put(nextSeq, new packetTimer(totalSeq)); 
            timers.get(nextSeq).start();
            nextSeq++;
            if (nextSeq == MAX_SEQ)
              nextSeq = 0;
            totalSeq++;
          }
          getACK();
        }
        //Sends the above packet to the client
        //If the size send was less than PACKET_SIZE then the last
        //packet was sent and Server is done transfering
        if (size  + SAC_SIZE < PACKET_SIZE)
        {
          System.out.println("Transfer finished.");
          socket.setSoTimeout(0);
          break;
        }
        //If data was a multiple of PACKET_SIZE then the last packet
        //when remaining size is equal to 0.
        //An empty packet is then sent to the client to signal
        //that the transfer is complete.
        if (remainingSize == 0)  
        {
          //Creates the empty packet to send to the client to signal
          //that the transfer is complete
          pack = new DatagramPacket(new byte[0] , 0, 
              packet.getAddress(),packet.getPort());
          //Sends the empty packet
          socket.send(pack);
          System.out.println("Transfer finished.");
          socket.setSoTimeout(0);
          break;
        }


      }
    }
  }
  private static class packetTimer extends Thread{
    int sendNum; 
    public boolean isACKed = false;
    public packetTimer(int seqNum) {
      sendNum = seqNum;
    }
    public void run() {
      while(ackWait()) {
        try {
          socket.send(windowPackets[sendNum]);
        }
        catch(IOException e){
        }

      }
    }
    private boolean ackWait(){
      if(this.interrupted()){
        return false;

      }
      try{
        Thread.sleep(THREAD_TIME);
      }
      catch(InterruptedException e){
        return false;
      }
      return true;
    }
  }
  private static synchronized void getACK() {
    try{
      byte[] buff = new byte[PACKET_SIZE];
      DatagramPacket packet = new DatagramPacket(buff, buff.length);
      socket.receive(packet);
      byte[] info = packet.getData(); 
      int ackN = ByteConverter.toInt(info,0);
      if(timers.containsKey(ackN)){
        timers.get(ackN).interrupt();
        timers.get(ackN).isACKed = true;
      }
      if(base == ackN){
        while(timers.containsKey(base) && timers.get(base).isACKed){
          timers.remove(base);
          base++;
          if (base == MAX_SEQ)
            base = 0;
        }
      }
      Thread.yield();
    }
    catch(IOException e){

    }

  }



}
