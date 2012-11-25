public interface Settings {
  public final static int PACKET_SIZE = 10024;
  public final static int WINDOW_SIZE = 8;
  public final static int INT_SIZE = 4;
  public final static int CHECKSUM_SIZE = 4;
  public final static int SAC_SIZE = INT_SIZE+CHECKSUM_SIZE;
  public final static int THREAD_TIME = 100;
  public final static int FIELD_SIZE = (int)( Math.log(WINDOW_SIZE)/Math.log(2));
  public final static int DATA_SIZE = PACKET_SIZE-(INT_SIZE * 2);
}


