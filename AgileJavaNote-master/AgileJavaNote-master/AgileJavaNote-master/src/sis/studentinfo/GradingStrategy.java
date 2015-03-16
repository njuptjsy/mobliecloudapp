package sis.studentinfo;

public interface GradingStrategy {//接口中所有的方法都需要在实现类中进行实现，所有接口所有的方法默认都是public和abstract，不需要声明
	int getGradePointsFor(Student.Grade grade);
}
