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

public class EditActionTest {

	private static final String DEFAULT_TASK_ADD_COMMAND = "add one\\ start 11/12/2012 7:30 due 12/12/2012 7:30 priority M";

	private Action edit;
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
	public void testInvalidTaskNumber() {

		/* Task # too big */
		cp = new CommandParser("edit 2 priority L");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("Task # out of range. ", edit.getFeedback().toString());

		/* Task # too small */
		cp = new CommandParser("edit 0 priority L");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("Task # out of range. ", edit.getFeedback().toString());

		/* Task # negative */
		cp = new CommandParser("edit -1 priority L");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("Task # out of range. ", edit.getFeedback().toString());

	}

	@Test
	public void testClearDate() {
		/* Clear due date valid */
		cp = new CommandParser("edit 1 due clear");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertNull(displayList.getFirst().getDueDate());
		assertNull(masterList.getFirst().getDueDate());
		assertEquals(MessageConstants.MESSAGE_DUE_DATE_CLEARED, edit
				.getFeedback().toString());

		/* Clear due date valid */
		cp = new CommandParser("edit 1 start clear");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertNull(displayList.getFirst().getStartDate());
		assertNull(masterList.getFirst().getStartDate());
		assertEquals(MessageConstants.MESSAGE_START_DATE_CLEARED, edit
				.getFeedback().toString());

		reset();
	}

	@Test
	public void testEditDueDate() {

		/* Change with both date and time valid */
		cp = new CommandParser("edit 1 due 12/12/12 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0830H", displayList.getFirst()
				.getDueDateString());
		assertEquals("12/12/2012 0830H", masterList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"12/12/2012 0830H"), edit.getFeedback().toString());

		/* Change with both date and time too small */
		cp = new CommandParser("edit 1 due 11/12/12 6:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0830H", displayList.getFirst()
				.getDueDateString());
		assertEquals(MessageConstants.MESSAGE_DUE_SMALLER_THAN_START, edit
				.getFeedback().toString());

		/* Change with both date and time same as start */
		cp = new CommandParser("edit 1 due 11/12/12 7:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0730H", displayList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"11/12/2012 0730H"), edit.getFeedback().toString());

		/* Change with only date valid */
		cp = new CommandParser("edit 1 due 13/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"13/12/2012"), edit.getFeedback().toString());

		/* Change with only date too small valid */
		cp = new CommandParser("edit 1 due 10/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("13/12/2012", displayList.getFirst().getDueDateString());
		assertEquals(MessageConstants.MESSAGE_DUE_SMALLER_THAN_START, edit
				.getFeedback().toString());

		/* Change with only date same as start date */
		cp = new CommandParser("edit 1 due 12/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012", displayList.getFirst().getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"12/12/2012"), edit.getFeedback().toString());

		reset();
	}

	@Test
	public void testStartDate() {

		/* Change with both date and time valid */
		cp = new CommandParser("edit 1 start 11/12/12 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0830H", displayList.getFirst()
				.getStartDateString());
		assertEquals("11/12/2012 0830H", masterList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"11/12/2012 0830H"), edit.getFeedback().toString());

		/* Change with both date and time too big */
		cp = new CommandParser("edit 1 start 12/12/12 9:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0830H", displayList.getFirst()
				.getStartDateString());
		assertEquals(MessageConstants.MESSAGE_START_BIGGER_THAN_DUE, edit
				.getFeedback().toString());

		/* Change with both date and time same as due */
		cp = new CommandParser("edit 1 start 12/12/12 7:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0730H", displayList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"12/12/2012 0730H"), edit.getFeedback().toString());

		/* Change with only date valid */
		cp = new CommandParser("edit 1 start 10/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"10/12/2012"), edit.getFeedback().toString());

		/* Change with only date too big valid */
		cp = new CommandParser("edit 1 start 13/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("10/12/2012", displayList.getFirst().getStartDateString());
		assertEquals(MessageConstants.MESSAGE_START_BIGGER_THAN_DUE, edit
				.getFeedback().toString());

		/* Change with only date same as start date */
		cp = new CommandParser("edit 1 start 12/12/12");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012", displayList.getFirst().getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"12/12/2012"), edit.getFeedback().toString());

		reset();
	}

	@Test
	public void testEditDueTime() {

		/* Change with due date present valid */
		// due time bigger than start time
		cp = new CommandParser("edit 1 due 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0830H", displayList.getFirst()
				.getDueDateString());
		assertEquals("12/12/2012 0830H", masterList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"12/12/2012 0830H"), edit.getFeedback().toString());

		// due time smaller than start time
		cp = new CommandParser("edit 1 due 6:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0630H", displayList.getFirst()
				.getDueDateString());
		assertEquals("12/12/2012 0630H", masterList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"12/12/2012 0630H"), edit.getFeedback().toString());

		/* Change with due date not present valid */
		// due time bigger than start time
		displayList.getFirst().setDueDate(null);
		masterList.getFirst().setDueDate(null);
		cp = new CommandParser("edit 1 due 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0830H", displayList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"11/12/2012 0830H"), edit.getFeedback().toString());

		// due time smaller than start time
		displayList.getFirst().setDueDate(null);
		masterList.getFirst().setDueDate(null);
		cp = new CommandParser("edit 1 due 6:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0630H", displayList.getFirst()
				.getDueDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
				"12/12/2012 0630H"), edit.getFeedback().toString());

		reset();
	}

	@Test
	public void testEditStartTime() {

		/* Change with start date present valid */
		// start time smaller than due time
		cp = new CommandParser("edit 1 start 6:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0630H", displayList.getFirst()
				.getStartDateString());
		assertEquals("11/12/2012 0630H", masterList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"11/12/2012 0630H"), edit.getFeedback().toString());

		// start time bigger than due time
		cp = new CommandParser("edit 1 start 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0830H", displayList.getFirst()
				.getStartDateString());
		assertEquals("11/12/2012 0830H", masterList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"11/12/2012 0830H"), edit.getFeedback().toString());

		/* Change with start date not present valid */
		// start time smaller than due time
		displayList.getFirst().setStartDate(null);
		masterList.getFirst().setStartDate(null);
		cp = new CommandParser("edit 1 start 6:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("12/12/2012 0630H", displayList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"12/12/2012 0630H"), edit.getFeedback().toString());

		// start time bigger than due time
		displayList.getFirst().setStartDate(null);
		masterList.getFirst().setStartDate(null);
		cp = new CommandParser("edit 1 start 8:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0830H", displayList.getFirst()
				.getStartDateString());
		assertEquals(String.format(MessageConstants.MESSAGE_START_DATE_SET,
				"11/12/2012 0830H"), edit.getFeedback().toString());

		reset();
	}

	@Test
	public void testEditStartDueTime() {

		/* Start date present */
		// start time smaller than end time
		displayList.getFirst().setStartDate(null);
		masterList.getFirst().setStartDate(null);
		displayList.getFirst().setDueDate(null);
		masterList.getFirst().setDueDate(null);
		cp = new CommandParser("edit 1 start 11/12/12 6:30 end 7:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0630H", displayList.getFirst()
				.getStartDateString());
		assertEquals("11/12/2012 0730H", displayList.getFirst()
				.getDueDateString());
		assertEquals(
				String.format(MessageConstants.MESSAGE_START_DATE_SET,
						"11/12/2012 0630H")
						+ String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
								"11/12/2012 0730H"), edit.getFeedback()
						.toString());

		// start time smaller than end time
		displayList.getFirst().setStartDate(null);
		masterList.getFirst().setStartDate(null);
		displayList.getFirst().setDueDate(null);
		masterList.getFirst().setDueDate(null);
		cp = new CommandParser("edit 1 start 11/12/12 6:30 end 5:30");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("11/12/2012 0630H", displayList.getFirst()
				.getStartDateString());
		assertEquals("12/12/2012 0530H", displayList.getFirst()
				.getDueDateString());
		assertEquals(
				String.format(MessageConstants.MESSAGE_START_DATE_SET,
						"11/12/2012 0630H")
						+ String.format(MessageConstants.MESSAGE_DUE_DATE_SET,
								"12/12/2012 0530H"), edit.getFeedback()
						.toString());

		reset();
	}

	@Test
	public void testEditPriority() {

		/* Change priority valid */
		cp = new CommandParser("edit 1 priority H");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("H", displayList.getFirst().getPriority());
		assertEquals("H", masterList.getFirst().getPriority());
		assertEquals(String.format(MessageConstants.MESSAGE_PRIORITY_SET, "H"),
				edit.getFeedback().toString());

		cp = new CommandParser("edit 1 priority m");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("M", displayList.getFirst().getPriority());
		assertEquals(String.format(MessageConstants.MESSAGE_PRIORITY_SET, "M"),
				edit.getFeedback().toString());

		cp = new CommandParser("edit 1 priority Low");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("L", displayList.getFirst().getPriority());
		assertEquals(String.format(MessageConstants.MESSAGE_PRIORITY_SET, "L"),
				edit.getFeedback().toString());

		/* Change priority invalid */
		cp = new CommandParser("edit 1 priority HI");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("L", displayList.getFirst().getPriority());
		assertEquals("L", masterList.getFirst().getPriority());
		assertEquals("", edit.getFeedback().toString());

		cp = new CommandParser("edit 1 priority higher");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("L", displayList.getFirst().getPriority());
		assertEquals("L", masterList.getFirst().getPriority());
		assertEquals("", edit.getFeedback().toString());

		/* Clear priority */
		cp = new CommandParser("edit 1 priority clear");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertNull(displayList.getFirst().getPriority());
		assertEquals(MessageConstants.MESSAGE_PRIORITY_CLEARED, edit
				.getFeedback().toString());

		reset();
	}

	@Test
	public void testEditName() {

		/* Change name valid */
		cp = new CommandParser("edit 1 name tasky\\");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("tasky", displayList.getFirst().getName());
		assertEquals("tasky", masterList.getFirst().getName());
		assertEquals(
				String.format(MessageConstants.MESSAGE_TASK_NAME_SET, "tasky"),
				edit.getFeedback().toString());

		cp = new CommandParser("edit 1 name tasky one\\");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("tasky one", displayList.getFirst().getName());
		assertEquals("tasky one", masterList.getFirst().getName());
		assertEquals(String.format(MessageConstants.MESSAGE_TASK_NAME_SET,
				"tasky one"), edit.getFeedback().toString());

		/* Change empty name invalid */
		cp = new CommandParser("edit 1 name \\");
		edit = cp.getAction();
		edit.execute(displayList, masterList);
		assertEquals("tasky one", displayList.getFirst().getName());
		assertEquals("tasky one", masterList.getFirst().getName());
		assertEquals("", edit.getFeedback().toString());

		reset();
	}

	/* Reset lists for next test */
	private void reset() {
		displayList = new LinkedList<Task>();
		masterList = new LinkedList<Task>();
		cp = new CommandParser(DEFAULT_TASK_ADD_COMMAND);
		Action add = cp.getAction();
		add.execute(displayList, masterList);
	}

}
