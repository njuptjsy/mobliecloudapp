package sis.studentinfo;

import sis.studentinfo.Student.Grade;
/*
 * abstract public class BasicGradingStrategy implements GradingStrategy{
	abstract public int getGradePointsFor(Student.Grade grade);
 * 重构，在BasicGradingStrategy类中实现getGradePointsFor方法进一步消除代码的重复
 * */
 public class BasicGradingStrategy implements GradingStrategy{
	/*public int getGradePointsFor(Grade grade) {
		return basicGradePointsFor(grade);
	}
	
	int basicGradePointsFor(Grade grade) {
		if (grade == Grade.A) return 4;
		if (grade == Grade.B) return 3;
		if (grade == Grade.C) return 2;
	    if (grade == Grade.D) return 1;
		return 0;使用switch语句重构
		switch (grade) {
		case A:return 4;
		case B:return 3;
		case C:return 2;
		case D:return 1;
		default: return 0;
		}
	}重构：basicGradePointsFor方法*/
	 public int getGradePointsFor(Grade grade) {
		 /*switch (grade) {
		 case A:return 4;
		 case B:return 3;
		 case C:return 2;
		 case D:return 1;
		 default: return 0;
		 }使用加强型枚举之后 重构*/
		 return grade.getPoints();
	 }
}
