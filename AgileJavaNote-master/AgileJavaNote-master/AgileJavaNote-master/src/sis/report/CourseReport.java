package sis.report;

import java.util.*;
import java.util.Collections;

import static sis.report.ReportConstant.NEWLINE;
import sis.studentinfo.CourseSession;

public class CourseReport {
	private List<CourseSession> sessions = new ArrayList<CourseSession>();
	
	public void add(CourseSession session)
	{
		sessions.add(session);
	}
	
	public String text(){
		//Collections.sort(sessions);编译错误：sort只能对java.lang.comparable类型进行排序
		StringBuilder builder = new StringBuilder();
		Collections.sort(sessions);
		for (CourseSession session:sessions)
		{
			builder.append(session.getDepartment() + " " + session.getNumber() + NEWLINE);
		}
		return builder.toString();
	}
}
