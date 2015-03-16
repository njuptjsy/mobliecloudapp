package sis.studentinfo;

import java.util.*;

import javax.swing.plaf.basic.BasicBorders.SplitPaneBorder;

import org.omg.CORBA.StringHolder;

public class Student implements Comparable<Student>{
	//String myName;
	//改成更加专业的名字
	
	//String name;
	/*
	 * 不要讲成员变量直接暴露给其他类，可以签字面向对象和封装
	 * 当然会有至少两个理由让我们不将成员变量指定为private
	 * */
	private String name;//一般容易犯得错误就是将对象变量声明成类变量
	private int credits;
	public static final int CREDITS_REQUIRED_FOR＿FULL_TIME = 12;
	static final String IN_STATE = "CO";
	private String state = "";//必须初始化为空，这里隐形的将字符串成员变量初始化成null
	private List<Grade> grades = new ArrayList<Grade>();
	public enum Grade {//增强的枚举
		A(4),
		B(3),
		C(2),
		D(1),
		F(0);//定义了每个枚举对应的数
		
		private int points;//枚举的成员变量
		
		Grade (int points){//构造函数
			this.points = points;
		}
		
		int getPoints(){
			return points;
		}
	}
	
	//重构删除private boolean isHonors = false;
	private GradingStrategy gradingStrategy = new BasicGradingStrategy();
	private String firstName = "";
	private String middleName = "";//这两个变量并不一定会被赋值，所以必须初始化
	private String lastName;
	
	public Student (String fullName)
	{
		this.name = fullName;
		/*
		 * 这里必须使用this.name,name = name;语句会让编译器以为这只是将name的值赋给其自身
		消除歧义的方法有两种：1.给成员变量name换个名字一般起名anName,aName或者_name2.在成员变量前面加上this
		*/
		credits = 0;
		List<String> nameParts = split(fullName);
		setName(nameParts);
	}
	
	public String getName()
	{
		return this.name;
	}

	boolean isFullTime() {
		return credits >= CREDITS_REQUIRED_FOR＿FULL_TIME;
		//重构将12学分这个标准抽取成静态变量 return credits >= 12;
	}

	int getCredits() {
		return credits;
	}
	
	void addCredits(int credits){
		this.credits += credits;
	}

	void setState(String state){
		this.state = state.toUpperCase();
	}
	
	public boolean isInState() {
		return state.equals(Student.IN_STATE);
	}

	void addGrade(Grade grade){
		grades.add(grade);
	}
	
/*	double getGpa(){
		if (grades.isEmpty()) {
			return 0.0;
		}//根据某个特定条件判断是否返回的if语句被称为防卫语句
		double total = 0.0;
		for (String grade : grades) {
			if (grade.equals("A")) {
				total += 4;
			}
			else if (grade.equals("B")) {
				total += 3;
			}
			else if (grade.equals("C")) {
				total += 2;
			}
			else if (grade.equals("D")) {
				total += 1;
			}
		}	
		return total/grades.size();
	}
	将对getGpa()方法进行重构，新建一个新的方法
	*/
	
	double getGpa(){
		if (grades.isEmpty()) {
			return 0.0;
		}//根据某个特定条件判断是否返回的if语句被称为防卫语句
		double total = 0.0;
		for (Grade grade : grades) {
			//重构：total += gradePointsFor(grade);将gradePointsFor方法并入getGpa
			total += gradingStrategy.getGradePointsFor(grade);
		}	
		return total/grades.size();
	}
	
	/*int gradePointsFor(String grade){
		if (grade.equals("A")) {
			return 4;
		}
		else if (grade.equals("B")) {
			return 3;
		}
		else if (grade.equals("C")) {
			return 2;
		}
		else if (grade.equals("D")) {
			return 1;
		}
		return 0;
	}
	对这个函数再重构去掉else语句*/
	
	/*int gradePointsFor(Grade grade){
		/*if (grade == Grade.A) return 4;
		if (grade == Grade.B) return 3;
		if (grade == Grade.C) return 2;
	    if (grade == Grade.D) return 1;//通常将if语句主体和条件放在不同的行，但是这边由于为了清晰放在一行
		return 0;第一次重构：加入荣誉学生
		int points = basicGradePointsFor(grade);
		if (isHonors) {
			if (points > 0) {
				points += 1;
			}
		}
		return points;第二次重构：使用接口将各种学生计算分数的策略分离成一个个单独的类
		return gradingStrategy.getGradePointsFor(grade);
	}第三次重构：删除代码*/
	

	

	@Override
	public int compareTo(Student o) {
		
		return 0;
	}

	/*void setHonors(){
		isHonors = true;
	}
	
	private int basicGradePointsFor(Grade grade) {
		if (grade == Grade.A) return 4;
		if (grade == Grade.B) return 3;
		if (grade == Grade.C) return 2;
	    if (grade == Grade.D) return 1;//通常将if语句主体和条件放在不同的行，但是这边由于为了清晰放在一行
		return 0;
	}重构删除*/
	
	/*
	 * 这里使用了多态的知识
	 * 通过setGradingStrategy方法传入实现了GradingStrategy接口的各种实现类
	 * 再在getGpa方法中向gradingStrategy传递getGradePointsFor(grade)消息
	 * 这样就可以得到不同学生的不同成绩策略在相应的成绩下返回的绩点，从而实现了通过增加GradingStrategy接口的实现类增加新的成绩策略的目的
	 * “用增加来拥抱变化而不是用改变去适应变化”
	 * */
	void setGradingStrategy(GradingStrategy gradingStrategy){
		this.gradingStrategy = gradingStrategy;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getMiddleName(){
		return middleName;
	}
	
	public String getLastName(){
		return lastName;
	}

	/*private void setName(List<String> nameParts) {
		if (nameParts.size() == 1) {
			this.lastName = nameParts.get(0);
		}
		else if (nameParts.size() == 2) {
			this.firstName = nameParts.get(0);
			this.lastName = nameParts.get(1);
		}
		else if (nameParts.size() == 3) {
			this.firstName = nameParts.get(0);
			this.middleName = nameParts.get(1);
			this.lastName = nameParts.get(2);
		} 
	}重构*/
	
	private void setName(List<String> nameParts) {
		this.lastName = removeLast(nameParts);
		String name = removeLast(nameParts);
		if (nameParts.isEmpty()) {
			this.firstName = name;
		}
		else {
			this.middleName = name;
			this.firstName = removeLast(nameParts);
		}
	}
	
	private String removeLast(List<String> list) {
		if (list.isEmpty()) {
			return "";
		}
		return list.remove(list.size() - 1);//删除相应位置的元素，并防护被删除的元素
	}
	
	private List<String> split(String name) {
		List<String> results = new ArrayList<String>();
		
		StringBuffer word = new StringBuffer();
		for (int index = 0; index < name.length(); index++){
			char ch = name.charAt(index);
			if (ch != ' ') {//java中的字符用单引号而不是双引号
				word.append(ch);
			}
			else {
				if (word.length() > 0){
					results.add(word.toString());
					word = new StringBuffer();
				}
			}
		}
		if (word.length() > 0) {//将最后一个空格后面的单词加入list
			results.add(word.toString());
		}
		return results;
	}
	
}

/*
 *成员变量一般使用名词命名，方法使用动词，并且他们都遵循驼峰原则
 * 类命名使用大写的驼峰原则，类使用名词，并且是单数名词，劲量使用单数名词集合代替复数，
 * 如：students可以用StudentDirectory
 * 一个类一般只处理一件事，下划线一般只出现与成员变量和类常量
 * 避免缩写，劲量使你的命名一目了然	
 * 代码的版面设计：
 */
