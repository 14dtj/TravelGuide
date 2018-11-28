package cn.edu.sjtu.travelguide;

import org.junit.Test;

import cn.edu.sjtu.travelguide.service.HttpHelper;
import cn.edu.sjtu.travelguide.service.RMPService;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMultiThread() {
        HttpHelper httpHelper = new HttpHelper();
        System.out.println(httpHelper.sendGet("http://119.23.241.119:8080/Entity/U18494e8f6fa80/travel/User"));
    }

    @Test
    public void testLogin() {
        RMPService service = RMPService.getInstance();
        assertTrue(service.login("tj", "tj"));
    }
}