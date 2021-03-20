import org.apache.flink.util.MathUtils;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 15:55
 */
public class MMHashDemo {
    public static void main(String[] args) {
        System.out.println(MathUtils.murmurHash("奇数".hashCode()) % 128);
        System.out.println(MathUtils.murmurHash("偶数".hashCode()) % 128);
    }
}
