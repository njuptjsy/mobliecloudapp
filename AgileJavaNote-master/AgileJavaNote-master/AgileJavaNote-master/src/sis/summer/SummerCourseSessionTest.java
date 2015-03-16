package sis.summer;

import java.util.Date;

import sis.studentinfo.CourseSession;
import sis.studentinfo.DateUtil;
import sis.studentinfo.Session;
import sis.studentinfo.SessionTest;
import junit.framework.TestCase;

public class SummerCourseSessionTest extends SessionTest{
	public void testEndDate() {
		Date startDate = DateUtil.createDate(2003, 6, 10);
		Session session = createSession("ENGL","200",startDate);
		Date eightWeeksOut = DateUtil.createDate(2003, 8, 1);
		assertEquals(eightWeeksOut, session.getEndDate());
	}
	
	protected Session createSession(String department, String number, Date date){
		return SummerCourseSession.create(department, number, date);
	}
}
