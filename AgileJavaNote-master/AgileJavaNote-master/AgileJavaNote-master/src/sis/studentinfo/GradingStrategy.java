package sis.studentinfo;

public interface GradingStrategy {//�ӿ������еķ�������Ҫ��ʵ�����н���ʵ�֣����нӿ����еķ���Ĭ�϶���public��abstract������Ҫ����
	int getGradePointsFor(Student.Grade grade);
}
