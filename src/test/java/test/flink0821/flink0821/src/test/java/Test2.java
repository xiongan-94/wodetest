import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/29 10:10
 */
public class Test2 {
    public static void main(String[] args) {
        long ts = 1611886277L;


        LocalDateTime today = LocalDateTime.ofEpochSecond(ts, 0, ZoneOffset.ofHours(8));
        LocalDateTime tomorrow = LocalDateTime
            .of(today.toLocalDate().plusDays(1), LocalTime.of(0, 0, 0));
        System.out.println(tomorrow.toEpochSecond(ZoneOffset.ofHours(8)));
    }
}
