package sis.report;

import sis.studentinfo.*;
import static sis.report.ReportConstant.NEWLINE;

public class RosterReporter {
	//重构使用静态导入，导入常用类常量
	//static final String NEWLINE = System.getProperty("line.separator");
	static final String ROSTER_REPORT_HEADER = "Student" + NEWLINE + "----" + NEWLINE;
	static final String ROSTER_REPORT_FOOTER = NEWLINE + "# students = ";
	private CourseSession session;

	RosterReporter(CourseSession session) {
		this.session = session;
	}
	
	String getReport(){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(ROSTER_REPORT_HEADER);//页眉，ROSTER_REPORT_HEADER是在同一个类中CourseSession.ROSTER_REPORT_HEADER的简写
		
		
		for (Student student: session.getAllStudents())
		{
			buffer.append(student.getName());
			buffer.append(NEWLINE);
		}
		
		buffer.append(ROSTER_REPORT_FOOTER + session.getAllStudents().size() + NEWLINE);//页尾信息
		
		return buffer.toString();
	}
}

