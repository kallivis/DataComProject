import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.nio.charset.StandardCharsets;
import java.nio.ByteOrder;
public class test
{
  public static void main(String[] args)
  {
    //ByteBuffer bb = ByteBuffer.putInt(4);
    //BitSet bs = BitSet.valueOf(ByteConverter.toBytes2(4)); 
    ByteBuffer bb = ByteBuffer.wrap("hello".getBytes(StandardCharsets.US_ASCII)).order(ByteOrder.BIG_ENDIAN);
    System.out.println(bb.hashCode());
    //BitSet bs = BitSet.valueOf("hello".getBytes(StandardCharsets.US_ASCII));
    BitSet bs = BitSet.valueOf(bb);
    
    

        System.out.println(bs.toString());
               System.out.println(bs.get(39));
               System.out.println(bs.get(38));
        System.out.println(bs.get(37));
        System.out.println(bs.get(36));
        System.out.println(bs.get(35));
        System.out.println(bs.get(34));
        System.out.println(bs.get(33));
        System.out.println(bs.get(32));
        System.out.println(bs.get(31));
        System.out.println(bs.get(30));
        System.out.println(bs.get(29));
        System.out.println(bs.get(28));
        System.out.println(bs.get(27));
        System.out.println(bs.get(26));
        System.out.println(bs.get(25));
        System.out.println(bs.get(24));
        System.out.println(bs.get(23));
        System.out.println(bs.get(22));
        System.out.println(bs.get(21));
        System.out.println(bs.get(20));




        System.out.println(bs.size());
  }
}
