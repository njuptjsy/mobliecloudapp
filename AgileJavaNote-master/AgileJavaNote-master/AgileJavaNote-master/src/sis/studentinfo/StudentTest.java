package sis.studentinfo;
//重构加入
import junit.framework.TestCase;
public class StudentTest extends TestCase {
	
	private static final double GRADE_TOLERANCE = 0.05;
	
/*	public void testCreater(){
 	测试方法命名：
 	必须是public void 必须以test开头不能接受任何参数

		//空方法测试一定可以通过
		Student student = new Student("Jane Doe");
		String studentName = student.getName();
		assertEquals("Jane Doe", studentName);//用来证明或者说断言前两个语句的执行结果studentName，是否和预计的一样
		
		Student secondStudent = new Student("Joe Blow");
		String secondStudentName = secondStudent.getName();
		assertEquals("Joe Blow", secondStudentName);
		
		assertEquals("Jane Doe", student.getName());
	}
	
	/*
	 * 重构代码
	 * 1.保证没有重复的代码
	 * 2.保证代码整洁，清晰提现编程意图
	 * 
	
	
	 * 对上面的代码进行重构：
	 * 1.清除不必要的局部变量
	 * public void testCreater(){
		Student student = new Student("Jane Doe");
		
		assertEquals("Jane Doe", firstStudent.getName());
		
		Student secondStudent = new Student("Joe Blow");
		
		assertEquals("Joe Blow", secondStudent.getName);
		
		assertEquals("Jane Doe", student.getName());
	}
	 *
	 *2.代码中的字符串嵌入是不良习惯，因为其意义很难明确
	 * 并且其中Jane Doe字符重复两次，在修改其中之一时，必须要修改第二个
	 * 
	 * public void testCreater(){
	 * 
	 *  final String fristStudentName = "Jane Doe";
		Student firstStudent = new Student(fristStudentName);
		assertEquals(fristStudentName, firstStudent.getName());
		
		final String secondStudentName = "Jane Doe";
		Student secondStudent = new Student(secondStudentName);
		assertEquals(secondStudentName, secondStudent.getName());	
	
	终结下开发的循环：
 * 1.编写测试程序，来断言这些功能是否正确
 * 2.运行测试，修改未通过的测试用例
 * 3.测试都通过开始重构代码，最后测试通过	
*/
	
	public void testCreate(){

		final String fristStudentName = "Jane Doe";
		/*
		 * 在使用了String后，import java.lang.String;语句将隐式的加入每一个java源文件
		 * java中每个类都隐式的集成了java.lang.Object
		 * */
		final Student firstStudent = new Student(fristStudentName);
		assertEquals(fristStudentName, firstStudent.getName());
		
		assertEquals("Jane", firstStudent.getFirstName());
		assertEquals("Doe", firstStudent.getLastName());
		assertEquals("", firstStudent.getMiddleName());
		
		final String secondStudentName = "Blow";
		final Student secondStudent = new Student(secondStudentName);
		assertEquals(secondStudentName, secondStudent.getName());
		assertEquals("", secondStudent.getFirstName());
		assertEquals("Blow", secondStudent.getLastName());
		assertEquals("", secondStudent.getMiddleName());
		
		final String thirdStudentName = "Raymond Douglas Davies";
		final Student thirdStudent = new Student(thirdStudentName);
		assertEquals(thirdStudentName, thirdStudent.getName());
		assertEquals("Raymond", thirdStudent.getFirstName());
		assertEquals("Davies", thirdStudent.getLastName());
		assertEquals("Douglas", thirdStudent.getMiddleName());
	}

	/*public void testFullTime(){
		Student student = new Student("a");
		assertFalse(student.isFullTime());
	}

	public void testCredits(){
		Student student = new Student("a");
		assertEquals(0, student.getCredits());
		student.addCredits(3);
		assertEquals(3, student.getCredits());
		student.addCredits(4);
		assertEquals(7, student.getCredits());
	}合并这两个函数*/
	
	public void testStudentStatus(){
		Student student = new Student("a");
		assertEquals(0, student.getCredits());
		assertFalse(student.isFullTime());
	
		student.addCredits(3);
		assertEquals(3, student.getCredits());
		assertFalse(student.isFullTime());
		
		student.addCredits(4);
		assertEquals(7, student.getCredits());
		assertFalse(student.isFullTime());
		
		student.addCredits(5);
		assertEquals(Student.CREDITS_REQUIRED_FOR＿FULL_TIME, student.getCredits());
		assertTrue(student.isFullTime());
	}

	public void testInState(){
		Student student = new Student("a");
		assertFalse(student.isInState());
		student.setState(Student.IN_STATE);
		assertTrue(student.isInState());
		student.setState("MD");
		assertFalse(student.isInState());
	}

	/*重构：public void testCalculateGpa(){
		Student student = new Student("a");
		assertEquals(0.0, student.getGpa(), GRADE_TOLERANCE);
		//在比较浮点数的时候JUnit提供了第三个参数，该参数表示可以接受的误差范围
		//规则是误差不超过最下进度的一半
		student.addGrade("A");
		assertEquals(4.0, student.getGpa(), GRADE_TOLERANCE);
		student.addGrade("B");
		assertEquals(3.5, student.getGpa(), GRADE_TOLERANCE);
		student.addGrade("C");
		assertEquals(3.0, student.getGpa(), GRADE_TOLERANCE);
		student.addGrade("D");
		assertEquals(2.5, student.getGpa(), GRADE_TOLERANCE);
		student.addGrade("F");
		assertEquals(2.0, student.getGpa(), GRADE_TOLERANCE);
	}*/
	
	public void testCalculateGpa(){
		Student student = new Student("a");
		assertGpa(student, 0.0);
		student.addGrade(Student.Grade.A);
		assertGpa(student, 4.0);
		student.addGrade(Student.Grade.B);
		assertGpa(student, 3.5);
		student.addGrade(Student.Grade.C);
		assertGpa(student, 3.0);
		student.addGrade(Student.Grade.D);
		assertGpa(student, 2.5);
		student.addGrade(Student.Grade.F);
		assertGpa(student, 2.0);
	}
	
	private void assertGpa(Student student, double expectedGpa) {
		assertEquals(expectedGpa, student.getGpa(), GRADE_TOLERANCE);
	}

	public void testCaclulateHonorsStudentGpa() {
		assertGpa(createHonorsStudent(), 0.0);
		assertGpa(createHonorsStudent(Student.Grade.A), 5.0);
		assertGpa(createHonorsStudent(Student.Grade.B), 4.0);
		assertGpa(createHonorsStudent(Student.Grade.C), 3.0);
		assertGpa(createHonorsStudent(Student.Grade.D), 2.0);
		assertGpa(createHonorsStudent(Student.Grade.F), 0.0);
	}
	
	private Student createHonorsStudent(Student.Grade grade) {
		Student student = createHonorsStudent();
		student.addGrade(grade);
		return student;
	}
	
	private Student  createHonorsStudent() {//创建一个基本学生类型，在设置其成绩策略就可以根据不同的成绩策略创建不同的学生
		Student student = new Student("a");
		student.setGradingStrategy(new HonorsGradingStrategy());
		return student;
	}
	
	public void testEndTrim(){
		assertEquals("", endTrim(""));
		assertEquals("  x", endTrim("  x  "));
		assertEquals("xyz", endTrim("xyz"));
		assertEquals("", endTrim("   "));
		assertEquals("xxx", endTrim("xxx   "));
	}

	private String endTrim(String source) {
		int i = source.length();
		if (i == 0) {
			return "";
		}
		else {
			while ( i > 0) {
				if (source.charAt(i-1) != ' ') {
					break;
				}
			i--;	
			}
			return source.substring(0, i);
		}

	}

}
