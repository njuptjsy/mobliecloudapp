package sis.studentinfo;
import java.util.*;//重构将原来的四个import语句变成一个*通配语句

import javax.swing.RowFilter;

/*
 * 包的用途：
 * 1.将一组类打包，未开发提供便利
 * 2.方便发布，对子模块进行重用
 * 3.包中每个类命名唯一
 * */
//重构加入
import junit.framework.TestCase;
public class CourseSessionTest extends SessionTest {
	private CourseSession session;
	private Date startDate;
	private static final int CREDITS = 3;
	
	/*public void setUp()
	{
		
		//int year = 103;
		//int month = 0;
		//int date = 6;
		
		startDate = new Date(year,month,date);//第一个参数表示年数减去1990，这里指的是2003
		//第二个是0~11表示1~12月
		//第三个参数1~31表示一个月中的1~31号
		
		startDate =  DateUtil.createDate(2003, 1, 6);
		session = createCourseSession();
		//JUnit在执行每个测试用例之前，默认执行setUp方法
		//在测试类中用构造函数对公共变量进行初始化是很差的实践
	}重构删除：在父类中测试*/
	
	/*重构后弃用
	 * //为了消除Date参数的歧义使用下列方法
	Date createDate(int year,int month,int data) {
		//为了消除Date警告，再重构
		//return new Date(year -1990,month - 1,data);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.clear();
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,month-1);
		calendar.set(Calendar.DAY_OF_MONTH, data);
		return calendar.getTime();
	}*/
	
	/*public void	testCreate(){//对象创建测试
		
		*重构原因：可以将局部对象和简单变量标记为final
		*提供额外的保护性
		* 注意作者认为只要遵循以下原则就可以不要将可以将局部对象和简单变量标记为final：永远不要给参数赋值
		* 而且尽量少为局部对象引用重新赋值
		* static final用来声明类产量，类常量用大小字母命名，单词之间用下划线分隔，在内存仅仅保存一个备份
		

		assertEquals("ENGL", session.getDepartment());
		assertEquals("101", session.getNumber());
		assertEquals(0, session.getNumberOfStudents());
		assertEquals(startDate, session.getStartDate());
	}重构删除：在父类中测试*/
	
	/*public void testEnrollStudents(){重构原因：
		代码长，断言太多,将这整个学生列表都暴露给了客户
	
		//重构删除：CourseSession session = new CourseSession("ENGL", "101");
		*上面的对象被创建了两次，需要消除冗余
		 * 使用JUnit提供的setUp方法
		 * 
		
		Student student1 = new Student("Cain DiVoe");
		session.enroll(student1);
		assertEquals(1, session.getNumberOfStudents());
		assertEquals(CREDITS, student1.getCredits());
		//重构删除：ArrayList<Student> allStudents = session.getAllStudents();
		//ArrayList<Student>参数化类型
		//重构删除：assertEquals(1, allStudents.size());
		//重构修改assertEquals(student1,allStudents.get(0));
		assertEquals(student1, session.get(0));
		
		Student student2 = new Student("coralee Devaughn");
		session.enroll(student2);
		assertEquals(CREDITS, student2.getCredits());
		assertEquals(2, session.getNumberOfStudents());
		//重构删除：assertEquals(2, allStudents.size());
		//重构修改assertEquals(student1, allStudents.get(0));
		//重构修改assertEquals(student2, allStudents.get(1));
		assertEquals(student1, session.get(0));
		assertEquals(student2, session.get(1));
	}重构删除：在父类中测试*/

	public void textCourseDates()
	{
		Date startDate = DateUtil.createDate(2003, 1, 6);
		Session session = createSession("ENGL", "200", startDate);
		Date sixteenWeeksOut = DateUtil.createDate(2003, 4, 25);
		assertEquals(sixteenWeeksOut, session.getEndDate());
	}
	
	/*public void testReport()
	{	Session session = createSession("ENGL", "200", startDate);
		session.enroll(new Student("A"));
		session.enroll(new Student("B"));
		
		String rosterReport = session.getReport();
		//重构：assertEquals(CourseSession.ROSTER_REPORT_HEADER + "A\nB\n" + CourseSession.ROSTER_REPORT_FOOTER + "2\n", rosterReport);
		//将在不同系统中不同的换行符用System.getProperty("line.separator")代替
		assertEquals(CourseSession.ROSTER_REPORT_HEADER + "A"
				+ CourseSession.NEWLINE + "B" + CourseSession.NEWLINE + CourseSession.ROSTER_REPORT_FOOTER + "2"
				+ CourseSession.NEWLINE, rosterReport);
	}重构删除*/

	public void testCount(){
		//重构：不允许直接操作类变量和对象属性CourseSession.count = 0;
		CourseSession.resetCount();
		createSession("", "", new Date());
		assertEquals(1, CourseSession.getCount());
		createSession("", "", new Date());
		assertEquals(2, CourseSession.getCount());
	}
	
	/*private CourseSession createCourseSession(){//这种创建对象的方法可以通过方法名来向程序员传递更多信息
		session =CourseSession.create("ENGL", "101", startDate);
		session.setNumberOfCredits(CourseSessionTest.CREDITS);
		return session;
	}重构删除*/

	public void testComparable(){
		final Date date = new Date();
		CourseSession sessionA = CourseSession.create("CMSC", "101", date);
		CourseSession sessionB = CourseSession.create("ENGL", "101", date);
		assertTrue(sessionA.compareTo(sessionB) < 0);
		assertTrue(sessionB.compareTo(sessionA) > 0);
		
		CourseSession sessionC = CourseSession.create("CMSC", "101", date);
		assertEquals(0, sessionA.compareTo(sessionC));
		
		CourseSession sessionD = CourseSession.create("CMSC", "210", date);
		assertTrue(sessionC.compareTo(sessionD) < 0);
		assertTrue(sessionD.compareTo(sessionC) > 0);
	}

	@Override
	protected Session createSession(String department, String number,Date startDate) {
		return CourseSession.create(department, number, startDate);
	}

	public void testLabeledBreak() {
		List<List<String>> table = new ArrayList<List<String>>();
		
		List<String> row1 = new ArrayList<String>();
		row1.add("5");
		row1.add("2");
		List<String> row2 = new ArrayList<String>();
		row2.add("3");
		row2.add("4");
		
		table.add(row1);
		table.add(row2);
		assertTrue(found(table,"3"));
		assertFalse(found(table,"8"));
	}
	
	private boolean found(List<List<String>> table, String target){
		boolean found = false;
		search://使用标签后，java遇到像break或者continue这样的跳转语句，就会直接将控制流转到标签所在处
			for(List<String> row : table){
				for(String value: row){
					if (value.equals(target)) {
						found = true;
						break search;
					}
				}
			}
		return found;
	}
}
