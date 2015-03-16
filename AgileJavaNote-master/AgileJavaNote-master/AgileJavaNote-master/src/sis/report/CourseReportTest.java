package sis.report;

import java.util.ArrayList;
import java.util.*;
import junit.framework.*;
import sis.studentinfo.CourseSession;
import static sis.report.ReportConstant.NEWLINE;

public class CourseReportTest extends TestCase{

	
	public void testReport() {
	final Date date = new Date();
	CourseReport report = new CourseReport();
	report.add(CourseSession.create("ENGL", "101", date));
	report.add(CourseSession.create("CZEC", "200", date));
	report.add(CourseSession.create("ITAL", "410", date));
	report.add(CourseSession.create("CZEC", "220", date));
	report.add(CourseSession.create("ITAL", "330", date));
	
	assertEquals("CZEC 200" + NEWLINE + 
			"CZEC 220" + NEWLINE + "ENGL 101" + NEWLINE + 
			"ITAL 330" + NEWLINE+ "ITAL 410" + NEWLINE, report.text());
	}
	
	public void testSortStringInplace(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("Heller");
		list.add("kafka");
		list.add("Camus");
		list.add("Boyle");
		Collections.sort(list);//测试Collections.sort(list);语句
		//sort使用的是合并排序的算法
		assertEquals("Boyle", list.get(0));
		assertEquals("Camus", list.get(1));
		assertEquals("Heller", list.get(2));
		assertEquals("kafka", list.get(3));
	}
	
	public void testSortInNewList() {//生成新的ArrayList排序，这样就可以不改变原有arrayList的顺序
		ArrayList<String> list = new ArrayList<String>();
		list.add("Heller");
		list.add("kafka");
		list.add("Camus");
		list.add("Boyle");
		ArrayList<String> sortedList = new ArrayList<String>(list);//利用list的元素生成新的sortedList
		Collections.sort(sortedList);
		
		assertEquals("Boyle", sortedList.get(0));
		assertEquals("Camus", sortedList.get(1));
		assertEquals("Heller", sortedList.get(2));
		assertEquals("kafka", sortedList.get(3));
		
		assertEquals("Heller", list.get(0));
		assertEquals("kafka", list.get(1));
		assertEquals("Camus", list.get(2));
		assertEquals("Boyle", list.get(3));
		
	}

	public void testStringCompareTo(){
		assertTrue("A".compareTo("B") < 0);
		assertEquals(0, "A".compareTo("A"));
		assertTrue("B".compareTo("A") > 0);
	}
	
	public void testComparable(){
		final Date date = new Date();
		CourseSession sessionA = CourseSession.create("CMSC", "101", date);
		CourseSession sessionB = CourseSession.create("ENGL", "101", date);
		assertTrue(sessionA.compareTo(sessionB) < 0);
		assertTrue(sessionB.compareTo(sessionA) > 0);
		
		CourseSession sessionC = CourseSession.create("CMSC", "101", date);
		assertEquals(0, sessionA.compareTo(sessionC));
	}
}
