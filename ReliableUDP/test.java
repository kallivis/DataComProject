import java.nio.ByteBuffer;
import java.nio.IntBuffer;
public class test
{
  public static void main(String[] args)
  {
    ByteBuffer bb = ByteBuffer.wrap(ByteConverter.toBytes(4)); 

    IntBuffer ib = bb.asIntBuffer();
    int int1 = ib.get(0);
        System.out.println(int1);
  }
}
