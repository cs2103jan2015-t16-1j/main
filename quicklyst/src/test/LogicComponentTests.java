package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddActionTest.class, CommandParserTest.class,
		DateParserTest.class, EditActionTest.class, FieldParserTest.class,
		FindActionTest.class, LogicTest.class, SortActionTest.class })
public class LogicComponentTests {

}
