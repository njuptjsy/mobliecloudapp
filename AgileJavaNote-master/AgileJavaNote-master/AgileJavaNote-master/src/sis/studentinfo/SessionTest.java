package sis.studentinfo;

import static sis.studentinfo.DateUtil.createDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;


abstract public class SessionTest extends TestCase {
	private Session session;
	private Date startDate;
	public static final int CREDITS = 3;

	public void setUp() {
		startDate = createDate(2003, 1 , 6);
		session = createSession("ENGL", "101", startDate);
		session.setNumberOfCredits(CREDITS);
	}

	abstract protected Session createSession(String department,String number, Date startDate); 

	public void	testCreate(){
		assertEquals("ENGL", session.getDepartment());
		assertEquals("101", session.getNumber());
		assertEquals(0, session.getNumberOfStudents());
		assertEquals(startDate, session.getStartDate());
	}
	
	public void testEnrollStudents(){
		Student student1 = new Student("Cain DiVoe");
		session.enroll(student1);
		assertEquals(1, session.getNumberOfStudents());
		assertEquals(CREDITS, student1.getCredits());
		assertEquals(student1, session.get(0));		
		
		Student student2 = new Student("coralee Devaughn");
		session.enroll(student2);
		assertEquals(CREDITS, student2.getCredits());
		assertEquals(2, session.getNumberOfStudents());
		assertEquals(student1, session.get(0));
		assertEquals(student2, session.get(1));
	}

	public void testComparable(){
		final Date date = new Date();
		Session sessionA = createSession("CMSC", "101", date);
		Session sessionB = createSession("ENGL", "101", date);
		assertTrue(sessionA.compareTo(sessionB) < 0);
		assertTrue(sessionB.compareTo(sessionA) > 0);	
		Session sessionC = createSession("CMSC", "101", date);
		assertEquals(0, sessionA.compareTo(sessionC));
		Session sessionD = createSession("CMSC", "210", date);
		assertTrue(sessionC.compareTo(sessionD) < 0);
		assertTrue(sessionD.compareTo(sessionC) > 0);
	}

//	public void testSessionLength() {
//		Session session = createSession(" "," ",new Date());
//		assertTrue(session.getSessionLength() > 0);
//	}
	public void testAverageGpaForPartTimeStudents(){
		session.students.clear();
		session.enroll(createFullTimeStudent());
		
		Student partTimer1 = new Student("1");
		partTimer1.addGrade(Student.Grade.A);
		session.enroll(partTimer1);
		
		session.enroll(createFullTimeStudent());
		
		Student partTimer2 = new Student("2");
		partTimer2.addGrade(Student.Grade.B);
		session.enroll(partTimer2);
		
		assertEquals(3.5, session.averageGpaForPartTimeStudents(),0.05);
	}

	private Student createFullTimeStudent() {
		Student student = new Student("a");
		student.addCredits(Student.CREDITS_REQUIRED_FOR£ßFULL_TIME);
		return student;
	}

	public void testIterate(){
		enrollStudent(session);
		
		List<Student> results = new ArrayList<Student>();
		for (Student student : session)
			results.add(student);
		
		assertEquals(session.getAllStudents(), results);
	}
	
	private void enrollStudent(Session session){
		session.enroll(new Student("1"));
		session.enroll(new Student("2"));
		session.enroll(new Student("3"));
	}
}
