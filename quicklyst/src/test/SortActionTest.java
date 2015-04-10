package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.Action;
import quicklyst.CommandParser;
import quicklyst.SortAction;
import quicklyst.Task;

public class SortActionTest {
	
	private static final String DEFAULT_TASK_ADD_COMMAND_0 = "add 1\\ start 11/12/2012 7:30";
	private static final String DEFAULT_TASK_ADD_COMMAND_1 = "add 2\\ start 11/12/2012 7:30 due 13/12/2012 7:30 priority L";
	private static final String DEFAULT_TASK_ADD_COMMAND_2 = "add 3\\ start 10/12/2012 7:30 due 12/12/2012 7:30 priority M";
	private static final String DEFAULT_TASK_ADD_COMMAND_3 = "add 4\\ start 9/12/2012 7:30 due 12/12/2012 7:30 priority H";
	private static final String DEFAULT_TASK_ADD_COMMAND_4 = "add 5\\ start 9/12/2012 7:30 due 10/12/2012 7:30";
	
	private static final String[] sortedTaskNames = {"5", "4", "3", "2", "1"};
	
	private Action add;
	private Action sortTest;
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
	public void testExecute() {
		sortTest = new SortAction();
		sortTest.execute(displayList, masterList);
		for(int i = 0; i < 5; i++) {
			assertEquals(sortedTaskNames[i], displayList.get(i).getName());
		}
	}
	
	private void reset() {
		displayList = new LinkedList<Task>();
		masterList = new LinkedList<Task>();
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_0);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_1);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_2);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_3);
		add = cp.getAction();
		add.execute(displayList, masterList);
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND_4);
		add = cp.getAction();
		add.execute(displayList, masterList);
	}
}
