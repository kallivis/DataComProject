public interface Settings {
  public final static int PACKET_SIZE = 10024;
  public final static int WINDOW_SIZE = 8;
  public final static int INT_SIZE = 4;
  public final static int THREAD_TIME = 100;
  public final static int FIELD_SIZE = (int)( Math.log(WINDOW_SIZE)/Math.log(2));
}


