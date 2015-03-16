package sis.studentinfo;
//重构加入
import junit.framework.TestSuite;;
public class AllTests {//使用AllTests类启动junit将执行下面包含的所有测试
	public static TestSuite suite(){
		junit.framework.TestSuite suite = new junit.framework.TestSuite();
		suite.addTestSuite(StudentTest.class);//StudentTest.class是一个类字面常量，唯一的标示了一个类，可以让类像对象一样被使用
		suite.addTestSuite(CourseSessionTest.class);
		//suite.addTestSuite(RosterReporterTest.class);
		suite.addTestSuite(DateUtilTest.class);
		suite.addTestSuite(BasicGradingStrategyTest.class);
		suite.addTestSuite(HonorsGradingStrategyTest.class);
		return suite;
	}
}
