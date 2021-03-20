import java.util.TreeSet;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/27 16:48
 */
public class Test1 {
    public static void main(String[] args) {
        TreeSet<Integer> is = new TreeSet<>();
        is.add(10);
        is.add(20);
        is.add(1);
        is.add(8);
        System.out.println(is);
        is.pollLast();
        is.pollFirst();
        System.out.println(is);
    }
}
