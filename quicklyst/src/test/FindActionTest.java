package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.Action;
import quicklyst.CommandParser;
import quicklyst.MessageConstants;
import quicklyst.Task;

public class FindActionTest {

	private static final String DEFAULT_TASK_ADD_COMMAND_1 = "add one\\ start 11/12/2012 7:30 due 12/12/2012 7:30 priority L";
	private static final String DEFAULT_TASK_ADD_COMMAND_2 = "add task two\\ start 10/12/2012 7:30 due 13/12/2012 7:30 priority M";
	private static final String DEFAULT_TASK_ADD_COMMAND_3 = "add task three\\ start 9/12/2012 7:30 due 14/12/2012 7:30 priority H";

	private Action add;
	private Action findTest;
	private CommandParser cp;

	private LinkedList<Task> displayList;
	private LinkedList<Task> masterList;

	@Before
	public void setUp() throws Exception {
		reset();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindName() {

		/* Full word */
		cp = new CommandParser("find name one\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		/* Partial word */
		cp = new CommandParser("find name on\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find name ask\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		/* Multiple keywords */
		cp = new CommandParser("find name one two\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals("task two", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find name ask t\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		/* No match */
		cp = new CommandParser("find name four\\");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(2, displayList.size());
		assertEquals(MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

		reset();
	}

	@Test
	public void testFindDate() {

		/* On date */
		cp = new CommandParser("find due on 12/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find start on 11/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find start on 7/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(1, displayList.size());
		assertEquals(MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

		/* After date */
		cp = new CommandParser("find start after 10/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals("task two", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find due after 13/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find start after 14/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(2, displayList.size());
		assertEquals(MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

		/* Before date */
		cp = new CommandParser("find start before 10/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find due before 13/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals("task two", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find start before 7/12/2012");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(2, displayList.size());
		assertEquals(MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

		/* Between date */
		cp = new CommandParser("find start between 8/12/12 and 10/12/12");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find due between 10/12/12 and 13/12/12");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals("task two", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find start between 1/12/12 and 7/12/12");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(2, displayList.size());
		assertEquals(MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

		reset();
	}

	@Test
	public void testFindPriority() {

		cp = new CommandParser("find priority high");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task three", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find priority medium");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find priority low");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find priority extreme");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals(1, displayList.size());
		assertEquals(MessageConstants.NO_PRIORITY_LEVEL
				+ MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());
	}

	@Test
	public void testFindCompleted() {
		displayList.getFirst().setIsCompleted(true);

		cp = new CommandParser("find completed yes");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("one", displayList.getFirst().getName());
		assertEquals(1, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 1),
				findTest.getFeedback().toString());

		cp = new CommandParser("find completed no");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(String.format(MessageConstants.MATCHES_FOUND, 2),
				findTest.getFeedback().toString());

		cp = new CommandParser("find completed");
		findTest = cp.getAction();
		findTest.execute(displayList, masterList);
		assertEquals("task two", displayList.getFirst().getName());
		assertEquals("task three", displayList.getLast().getName());
		assertEquals(2, displayList.size());
		assertEquals(MessageConstants.NO_COMPLETE_CRITERIA
				+ MessageConstants.NO_MATCHES_FOUND, findTest
				.getFeedback().toString());

	}

	/* Reset lists for next test */
	private void reset() {
		displayList = new LinkedList<Task>();
		masterList = new LinkedList<Task>();
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_1);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_2);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_3);
		add = cp.getAction();
		add.execute(displayList, masterList);
	}

}
