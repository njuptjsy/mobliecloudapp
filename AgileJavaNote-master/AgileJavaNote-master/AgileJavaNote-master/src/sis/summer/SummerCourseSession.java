package sis.summer;

import java.util.Date;
import sis.studentinfo.Session;

public class SummerCourseSession extends Session{
	public static SummerCourseSession create(String department, String number, Date startDate){
		return new SummerCourseSession(department,number,startDate);
	}
	
	private SummerCourseSession(String department, String number, Date startDate) {
		super(department,number,startDate);
	}
	
	/* public Date getEndDate(){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(getStartDate());
		//int sessionLength = 8;
		int daysInWeek = 7;
		int daysFromFridayToMonday = 3;
		int numberOfDays = getSessionLength() * daysInWeek - daysFromFridayToMonday;
		calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
		return calendar.getTime();
	}重构删除*/
	
	 
	 //重构：重载父类方法
	 @Override
	 protected int getSessionLength(){
		 return 8;
	 }
}
