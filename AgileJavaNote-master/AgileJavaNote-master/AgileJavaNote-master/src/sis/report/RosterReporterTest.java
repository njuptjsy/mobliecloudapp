package sis.report;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.TestCase;
import sis.studentinfo.*;
//静态导入：这样在这个类中可以直接使用ReportConstant类中的NEWLINE类常量
import static sis.report.ReportConstant.NEWLINE;
public class RosterReporterTest extends TestCase {
	public void testRosterReport(){
		CourseSession session = CourseSession.create("ENGL", "101",DateUtil.createDate(2001,1,6));
		
		session.enroll(new Student("A"));
		session.enroll(new Student("B"));
		
		String rosterReport = new RosterReporter(session).getReport();
//System.out.println(rosterReport);//将这行代码和页面左端对其，表示这是一行临时代码，方便删除
assertEquals(RosterReporter.ROSTER_REPORT_HEADER + "A" + NEWLINE +
				"B" + NEWLINE + 
				RosterReporter.ROSTER_REPORT_FOOTER + "2" + NEWLINE, rosterReport);
	
	}
	
	/*
	 *重构后弃用
	 * Date createDate(int year,int month,int data) {
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.clear();
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,month-1);
		calendar.set(Calendar.DAY_OF_MONTH, data);
		return calendar.getTime();
	}*/
}
