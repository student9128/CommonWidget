package com.kevin.commonwidgetlib;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;


/**
 * Created by Kevin on 2021/12/4<br/>
 * <p>
 * Describe:<br/>
 */
public class MyUnitTest {
    private String time = "2021-12-04 15:36:00";
    private long stamp = 1638603360000l;
    private Date mDate;
    @Before
    public void setUp() throws Exception {
        System.out.println("测试开始");
        mDate = new Date();
        mDate.setTime(stamp);
    }
    @After
    public void tearDown() throws Exception {
        System.out.println("测试结束");
    }
    @Test
    public void dateToStampTest() throws Exception {
        System.out.println("stamp="+DateUtil.dateToStamp(time));
        Assert.assertNotEquals(4,DateUtil.dateToStamp(time));
    }
    @Test
    public void stampToDateTest() throws Exception {
        Assert.assertEquals(time,DateUtil.stampToDate(stamp));
    }
    @Test
    public void testAssertThat() throws Exception {
        assertThat("Hello",startsWith("H"));
    }
}
