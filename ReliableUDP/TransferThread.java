import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.HashMap;
import java.util.zip.CRC32;
/*
 * Program Name:    Server.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         3.0 - Nov 30, 2012
 * Purpose:
 * Thread class for Transfering Packets
 */

public  class TransferThread implements Runnable, Settings
{
  private DatagramPacket packet;
  private DatagramSocket socket;
  //The first sequence number in the current current.
  private int base = 0;
  //The next sequence number to send
  private int  nextSeq = 0;
  // Highest sequence number sent so far
  private int  totalSeq = 0;
  private int size = 0;
  private int remainingSize;  
  //An array of all the packets that will be sent
  private DatagramPacket[] windowPackets;
  private int totalPackets;
  private String filename;
  private String user;

  private DatagramPacket pack;
  //HashMap of the Servers window
  //Contains the Sequence Number and the packet
  private HashMap<Integer, packetTimer> timers;
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
    try
    {
      //Runs the client server handshake
      handShake();
      System.out.println("Requested: " + filename);
      //Sets everything needed for the server sliding window and file data.
      setupWindowAndData(filename, user);
      //Starts the SendStream for the given filename
      SendStream(socket);
      //Final check to see if the last packet was sent
      //or if a 0 size packet needs to eb sent.
      finalCheck();
    }
    catch (IOException e) {
      e.printStackTrace();
    } 

  }
  private void setupWindowAndData(String fileName, String User){
    try{
      //Opens a file and a makes a file object from the
      //requested filename for reading.
      if (isNumber(fileName))
      {
        File dir = new File(User);
        File[] files = dir.listFiles();
        if (Integer.parseInt(fileName) >= files.length)
          fileName = "NOTAFILE";
        else
          fileName = User+"/"+files[Integer.parseInt(fileName)].getName();
        System.out.println("File name requested: " + fileName);
      }
      File file = new File(fileName);
      //Check if the file is a file
      if (!file.isFile())
        fileName = "NOTAFILE";

      //Creates a packet for the file ack
      byte[] fdata = new byte[PACKET_SIZE];
      if (fileName.equals("NOTAFILE"))
        fdata = ("NOTAFILE").getBytes();
      else
        fdata = ("FILEACK-"+fileName).getBytes();
      //The packet for the file info 
      DatagramPacket filePacket = new DatagramPacket(fdata, fdata.length,
          packet.getAddress(), 
          packet.getPort());
      //Sends the file info packet
      socket.send(filePacket);

      //Creates a fileInputStream for reading in the file into a buffer
      FileInputStream fis = new FileInputStream(file);

      //Size that will hold the size of the buffer being sent
      size = 0;
      //The remaining size that is left to send of the file
      remainingSize = (int) file.length(); 
      totalPackets = (int) Math.ceil(remainingSize/DATA_SIZE)+1;
      //The buffer that the file will be read into with a max size
      //determined by the PACKET_SIZE constant
      byte[] buffer = new byte[DATA_SIZE];
      System.out.println("Transfer started...");
      //sets the timeout for the server to 5 seconds
      socket.setSoTimeout(5000);
      //Creates a new array with the size of the total number packets
      //that is going to be sent.
      windowPackets = new DatagramPacket[totalPackets];
      //Sets up a crc generator
      CRC32 crc = new CRC32();
      int k = 0;
      //Goes through every packet and puts it into the array of all packet
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
        //the byte array that will be put into the packet
        byte[] sizeBuff = new byte[headerSize];
        //the byte array that will be used to generate the CRC code
        byte[] sizeBuff2 = new byte[headerSize-CHECKSUM_SIZE];

        //Copies the buffer into these new buffer
        System.arraycopy(packInfo,0, sizeBuff, 0,INT_SIZE );
        System.arraycopy(packInfo,0, sizeBuff2, 0,INT_SIZE );
        System.arraycopy(buffer,0, sizeBuff, INT_SIZE, size);
        System.arraycopy(buffer,0, sizeBuff2, INT_SIZE, size);
        //Resets the crc
        crc.reset();
        //Updates the CRC with the new info
        crc.update(sizeBuff2, 0, sizeBuff2.length);
        //Gets the CRC value and puts it into a byte array
        byte[] code = ByteConverter.toBytes((int)crc.getValue());
        //Adds the newly generated CRC code to the data array
        System.arraycopy(code,0, sizeBuff, size+INT_SIZE,CHECKSUM_SIZE);
        //Creates a new packet with the above buffer of data.
        //Uses the initial client packet to get the client's 
        //address and port, and puts it into the array of all packets.
        windowPackets[i] = new DatagramPacket(sizeBuff, headerSize, 
            packet.getAddress(), packet.getPort());
      }
    }
    //Catches a bunch of exception
    catch(FileNotFoundException e){

    }
    catch (SocketException e){

    }
    catch (IOException e){
    }
    catch(NumberFormatException e){
    }
  }
  //This method goes through the clietn server handshake
  private void handShake(){
    try{
      //Creates a String from the packet Message
      String cmd = new String(packet.getData(), 0, 
          packet.getLength());
      //Checks if Client sent the SYNC Command
      if (cmd.substring(0, 4).equals("SYNC"))
      {
        byte[] sdata = new byte[PACKET_SIZE]; 
        user = cmd.substring(4);
        String dirList =FileOperator.getUserFiles(user);
        System.out.println(sdata.length);
        //System.out.println(("SYNACK"+dirList).getBytes().length);
        //Puts the String "SYNACK" into bytes
        sdata = ("SYNACK"+dirList).getBytes();
        //Creates a packet for the SYNACK
        DatagramPacket spacket = new DatagramPacket(sdata, sdata.length,
            packet.getAddress(), packet.getPort());
        System.out.println(spacket.getLength());
        socket.send(spacket);
        //Recieves the Packet with the Filename
        socket.receive(packet);
        //Gets the filename from the packet
        filename = new String(packet.getData(), 0, 
            packet.getLength());

        if (filename.equals("EXIT"))
        {
          System.out.println("Server Closing.");
          System.exit(0);
        }
        if (!isNumber(filename))
          filename = user+"/"+filename;

      }
    }
    //Catches IOException
    catch(IOException e){

    }
  }


  //Check if string is a integer
  public static boolean isNumber(String str)
  {
    try
    {
      int i = Integer.parseInt(str);
    }
    catch(NumberFormatException e)
    {
      return false;
    }
    return true;
  }

  //Method that takes a filename as it's param
  //and sends the requested file in packets
  //to the client that requested it. 
  private void SendStream( DatagramSocket socket) throws IOException
  {

    while ( totalSeq != totalPackets  || !timers.isEmpty())
    {
      while(timers.size() < WINDOW_SIZE && totalSeq < totalPackets )
      {
        //If there is  room in the window send the next packet
        socket.send(windowPackets[totalSeq]);
        //Add that packet to a timer thread with a retry amount of 5
        timers.put(nextSeq, new packetTimer(totalSeq,5)); 
        //Starts the thread
        timers.get(nextSeq).start();
        nextSeq++;
        if (nextSeq == MAX_SEQ)
          nextSeq = 0;
        totalSeq++;
      }
      ///checks for an ack
      getACK();
    }



  }
  private void finalCheck(){
    try{
      //Sends the above packet to the client
      //If the size send was less than PACKET_SIZE then the last
      //packet was sent and Server is done transfering
      if (size  + SAC_SIZE < PACKET_SIZE)
      {
        System.out.println("Transfer finished.");
        socket.setSoTimeout(0);
        return;
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
        return;
      }
    }
    catch (SocketException e){

    }
    catch (IOException e){
    }
  }
  //The fucntion that checks for acks
  private  synchronized void getACK() {
    try{
      byte[] buff = new byte[PACKET_SIZE];
      DatagramPacket packet = new DatagramPacket(buff, buff.length);
      socket.receive(packet);
      byte[] info = packet.getData(); 
      int ackN = ByteConverter.toInt(info,0);
      //If the Server received an ack for a thread that is currently
      //on a timer stops that timer and marks it as acked.
      if(timers.containsKey(ackN)){
        timers.get(ackN).interrupt();
        timers.get(ackN).isACKed = true;
      }
      //If the lower window sequence number expected is received then
      //removes it and all sequence threads that are stopped and 
      //marked as ack
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
  //The timer class thread for server packets waiting fof an ackt
  private  class packetTimer extends Thread{
    int sendNum; 
    int retries = 5; 
    int tries = 0;
    public boolean isACKed = false;
    //Contructs a new packetTimer with a sequence number 
    //and the number of retries
    public packetTimer(int seqNum, int retries) {
      this.sendNum = seqNum;
      this.retries = retries;
      this.tries = 0;
    }
    //Runs the thread
    public void run() {
      //Continues while waiting for an ack
      while(ackWait()) {
        try {
          //Resends the packet if the timer rand out
          socket.send(windowPackets[sendNum]);
          if (tries++ >= retries)
          {
            this.isACKed = true;
            this.interrupt();
          }
        }
        catch(IOException e){
        }

      }
    }
    //Sleeps and waits till interupted
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
}



