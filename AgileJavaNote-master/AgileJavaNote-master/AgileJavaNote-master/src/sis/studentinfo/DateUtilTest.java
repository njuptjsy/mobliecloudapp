package sis.studentinfo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class DateUtilTest extends TestCase {
	public void testCreatDate()
	{
		//重构Date date = new DateUtil().createDate(2000,0,1);
		Date date = DateUtil.createDate(2000,0,1);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		assertEquals(2000, calendar.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
		assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
	}
}
