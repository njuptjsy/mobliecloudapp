package sis.studentinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

abstract public class Session implements Comparable<Session>,Iterable<Student>  {
	private String department;
	private String number;
	public List<Student> students = new ArrayList<Student>();
	protected Date startDate;//protected表示只有该变量所在类和其子类可以访问这个变量,但是要注意同一包中的非子类也可以访问protected元素
	private int numberOfCredits;
	private static int count;
	
	protected Session(String department, String number ,Date startDate){
		this.department = department;
		this.number = number;
		this.startDate = startDate;
	}
	
	public int compareTo(Session that) {
		int compare = this.getDepartment().compareTo(that.getDepartment());
		if (compare == 0) {
			compare = this.getNumber().compareTo(that.getNumber());
		}
		return compare;
	}

	public void setNumberOfCredits(int numberOfCredits) {
		this.numberOfCredits = numberOfCredits;
	}

	public String getDepartment(){
		return department;
	}
	
	public String getNumber(){
		return number;
	}

	public int getNumberOfStudents() {//int -2147483648~2147483647,java中的数字不是对象	
		return students.size();
	}
	
	public void enroll (Student student)
	{
		student.addCredits(numberOfCredits);
		students.add(student);
	}
	
	Student get(int index){
		return students.get(index);
	}
	
	protected Date getStartDate() {
		return startDate;
	}

	public List<Student> getAllStudents()
	{
		return students;
	}

	abstract protected int getSessionLength();
	
	public Date getEndDate(){
		GregorianCalendar calendar =new GregorianCalendar();
		calendar.setTime(getStartDate());//存入表示课程开始的对象
		final int daysInWeek = 7;
		final int daysFromFridayToMonday = 3;
		int numberOfDays = getSessionLength() * daysInWeek - daysFromFridayToMonday;	
		calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);	
		Date endDate = calendar.getTime();
		return endDate;
	}

	double averageGpaForPartTimeStudents(){
		double total = 0.0;
		int count = 0;
		for (Student student: students) {//使用迭代器的方式Iterator<Student> it = students.iterator();it.hasNext();
			if (student.isFullTime()) {
				continue;
			}
			count++;
			total += student.getGpa();
		}
		if (count == 0) {
			return 0.0;
		}
		return total/count;
	}
	
	public Iterator<Student> iterator(){
		return students.iterator();
	}
}
