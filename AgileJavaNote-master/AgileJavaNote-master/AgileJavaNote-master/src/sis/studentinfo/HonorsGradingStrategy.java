package sis.studentinfo;

import sis.studentinfo.BasicGradingStrategy;

public class HonorsGradingStrategy extends BasicGradingStrategy {
	
	public int getGradePointsFor(Student.Grade grade) {
		int points = super.getGradePointsFor(grade);
		if (points > 0) {
			points += 1;	
			}
		return points;
	}
}
