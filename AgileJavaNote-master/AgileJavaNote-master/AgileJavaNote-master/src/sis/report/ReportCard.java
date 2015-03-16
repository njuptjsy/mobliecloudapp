package sis.report;

import java.util.EnumMap;
import java.util.Map;

import sis.studentinfo.Student;

public class ReportCard {
	static final String A_MESSAGE = "Exellent";
	static final String B_MESSAGE = "Very good";
	static final String C_MESSAGE = "Hmmm...";
	static final String D_MESSAGE = "You're not trying";
	static final String F_MESSAGE = "Loser";
	
	private Map<Student.Grade, String> messages = null;
	
	public String getMessage(Student.Grade grade) {
		return getMessage().get(grade);
	}
	
	private Map<Student.Grade, String> getMessage() {
		if (messages == null) {
			loadMessage();
		}
		return messages;
	}
	
	private void loadMessage() {//延迟初始化，在使用某个成员变量时才对其进行初始化
		messages = new EnumMap<Student.Grade, String>(Student.Grade.class);//Creates an empty enum map with the specified key type
		messages.put(Student.Grade.A, A_MESSAGE);
		messages.put(Student.Grade.A, B_MESSAGE);
		messages.put(Student.Grade.A, C_MESSAGE);
		messages.put(Student.Grade.A, D_MESSAGE);
		messages.put(Student.Grade.A, F_MESSAGE);
	}
}
