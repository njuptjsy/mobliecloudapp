package sis.studentinfo;
/*在商业程序中每个类都应该被包含在某个包中而不是放在默认包中*/
/*import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
 */
import java.util.*;

public class CourseSession extends Session {
	static final String NEWLINE = System.getProperty("line.separator");
	static final String ROSTER_REPORT_HEADER = "Student" + NEWLINE + "----" + NEWLINE;
	static final String ROSTER_REPORT_FOOTER = NEWLINE + "# students = ";
	private String department;
	private String number;
	//重构删除 private int numberOfStudents = 0;//成员变量在构造函数执行之前被初始化
	//显示的初始化成员变量有助于理解代码意图
	private List<Student> students = new ArrayList<Student>();
	protected Date startDate;//protected表示只有该变量所在类和其子类可以访问这个变量,但是要注意同一包中的非子类也可以访问protected元素
	private int numberOfCredits;
	private static int count;
	/* 删除这个为了测试的短暂的构造函数
	CourseSession(String department, String number) {
		this.department = department;
		this.number = number;
	}*/


	public static CourseSession create(String department, String number, Date startDate) {
		//重构删除incrementCount();
		//在静态方法中调用的方法必须也是static的
		return new CourseSession(department, number, startDate);//就近原则
	}

	/*protected CourseSession(String department, String number,Date startDate) {
		this.department = department;
		this.number = number;
		this.startDate = startDate;
		//重构：禁止直接访问类变量CourseSession.count = CourseSession.count + 1;
		//重构2：将CourseSession.incrementCount();移动到create方法
		//incrementCount();虽然这样调用类方法也是可以的，但是为了避免混淆应该避免这么做
	}重构*/

	protected CourseSession(String department, String number,Date startDate) {
		super(department,number,startDate);
		CourseSession.incrementCount();
	}
	private static void incrementCount() {
		//count = count + 1;
		//起作用等价于
		count ++;
	}

	static void resetCount() {
		count = 0;
	}

	static int getCount() {
		return count;
	}

	//重构：增加
	protected int getSessionLength() {
		return 16;
	}

	/*
	 * 封装可以使，客户不依赖于students容器，可以更加方便的更改这个存储结构
	 * 用户无法操作这个students容器
	 * */

	public Date getEndDate(){//当代吗超过12行左右就太长了，需要思考重构
		//如果你发现很难为这个方法找到名字，你也应用思考重构
		GregorianCalendar calendar =new GregorianCalendar();
		calendar.setTime(getStartDate());//存入表示课程开始的对象
		//重构：int numberOfDays = 16 * 7 - 3;//在开始日期基础上需要增加的天数，减三因为课程最后一天是周五
		//这个语句语义不明确，需要加注释，重构如下：
		//重构删除：final int sessionLength = 16;
		final int daysInWeek = 7;
		final int daysFromFridayToMonday = 3;
		int numberOfDays = getSessionLength() * daysInWeek - daysFromFridayToMonday;

		calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
		//第一个参数表示你要加的是什么类型，第二个参数是要加的数量
		Date endDate = calendar.getTime();
		return endDate;
	}

	/*重构这个函数，将其变成三个函数
	 String getReport()
	{
		StringBuilder buffer = new StringBuilder();//StringBuilder比StringBuffer具有更好的性能，不需要用支持多线程应用

		buffer.append(ROSTER_REPORT_HEADER);//页眉，ROSTER_REPORT_HEADER是在同一个类中CourseSession.ROSTER_REPORT_HEADER的简写

//		Student student = students.get(0);
//		buffer.append(student.getName());
//		buffer.append(NEWLINE);
//		
//		student = students.get(1);
//		buffer.append(student.getName());
//		buffer.append(NEWLINE);
//		使用循环遍历来适应任何数量的学生，遍历学生列表


		for (Student student: students)
		{
			buffer.append(student.getName());
			buffer.append(NEWLINE);
		}

		buffer.append(ROSTER_REPORT_FOOTER + students.size() + NEWLINE);//页尾信息

		return buffer.toString();
	}*/

	String getReport()
	{
		StringBuilder buffer = new StringBuilder();
		writeHeader(buffer);
		writeBody(buffer);
		writeFooter(buffer);

		return buffer.toString();
	}

	void writeHeader(StringBuilder buffer)
	{
		buffer.append(ROSTER_REPORT_HEADER);
	}

	void writeBody(StringBuilder buffer)
	{
		for (Student student: students)
		{
			buffer.append(student.getName());
			buffer.append(NEWLINE);
		}
	}

	void writeFooter(StringBuilder buffer)
	{
		buffer.append(ROSTER_REPORT_FOOTER + students.size() + NEWLINE);
	}

}

/**javadoc
 * 注释以/**开头
 * 其中的关键字是@param描述参数
 * @return年数返回值等等
 * */
