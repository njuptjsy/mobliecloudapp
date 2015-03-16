package sis.report;

import junit.framework.TestCase;
import sis.studentinfo.Student;


public class ReportCardTest extends TestCase{
	public void testMessage(){
		ReportCard card = new ReportCard();
		assertEquals(ReportCard.A_MESSAGE,card.getMessage(Student.Grade.A));
		assertEquals(ReportCard.A_MESSAGE,card.getMessage(Student.Grade.B));
		assertEquals(ReportCard.A_MESSAGE,card.getMessage(Student.Grade.C));
		assertEquals(ReportCard.A_MESSAGE,card.getMessage(Student.Grade.D));
		assertEquals(ReportCard.A_MESSAGE,card.getMessage(Student.Grade.F));
	}
}
