package cn.edu.sjtu.travelguide;

import org.junit.Test;

import cn.edu.sjtu.travelguide.service.PoiService;

import static org.junit.Assert.assertEquals;

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
    public void testGetPoi() {
        PoiService.getInstance().addSearchRecord("火锅");
    }
}