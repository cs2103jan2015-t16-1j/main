package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.ActionType;
import quicklyst.CommandParser;
import quicklyst.MessageConstants;

public class CommandParserTest {

	private CommandParser testCommandParser;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInvalidActionTypes() {

		testCommandParser = new CommandParser("xxx task 1\\");
		assertNull(testCommandParser.getAction());
		assertEquals(MessageConstants.INVALID_ACTION_TYPE, testCommandParser
				.getFeedback().toString());

		testCommandParser = new CommandParser("adds task 1\\");
		assertNull(testCommandParser.getAction());
		assertEquals(MessageConstants.INVALID_ACTION_TYPE, testCommandParser
				.getFeedback().toString());

		testCommandParser = new CommandParser("ad task 1\\");
		assertNull(testCommandParser.getAction());
		assertEquals(MessageConstants.INVALID_ACTION_TYPE, testCommandParser
				.getFeedback().toString());
	}

	@Test
	public void testAddActionTypes() {

		testCommandParser = new CommandParser("add task 1\\");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("aDD task 1\\");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("a task 1\\");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("add task 1");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertNull(testCommandParser.getTaskName());
		assertEquals(MessageConstants.NAME_NO_CLOSE
				+ MessageConstants.NO_TASK_NAME, testCommandParser
				.getFeedback().toString());

		testCommandParser = new CommandParser("add \\");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertNull(testCommandParser.getTaskName());
		assertEquals(MessageConstants.TASK_NAME_BLANK
				+ MessageConstants.NO_TASK_NAME, testCommandParser
				.getFeedback().toString());

		testCommandParser = new CommandParser("A ");
		assertEquals(ActionType.ADD, testCommandParser.getAction().getType());
		assertNull(testCommandParser.getTaskName());
		assertEquals(MessageConstants.NO_TASK_NAME, testCommandParser
				.getFeedback().toString());
	}

	@Test
	public void testEditActionTypes() {

		testCommandParser = new CommandParser("edit 1");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals(1, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("eDIt 0");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals(0, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("e -1");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals(-1, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("E a");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals(0, testCommandParser.getTaskNumber());
		assertEquals(
				String.format(MessageConstants.INVALID_FIELD_FORMAT_IN, "a"),
				testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("e ");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals(0, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());
	}

	@Test
	public void testFindActionTypes() {

		/* Valid fields */
		testCommandParser = new CommandParser("find");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertNull(testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("f -s af tdy -d bf tmr -p h");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s af tdy -d bf tmr -p h",
				testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"find start after today end before tomorrow priority high");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s af tdy -d bf tmr -p h",
				testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"find from on today to between tomorrow and 12/12 priority medium");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s on tdy -d btw tmr & 12/12 -p m",
				testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"find from on today to between tomorrow and 12/12 priority low");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s on tdy -d btw tmr & 12/12 -p l",
				testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("find all");
		assertTrue(testCommandParser.getFields().isEmpty());
		assertTrue(testCommandParser.getFindAll());
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertNull(testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());

		/* Invalid fields */
		testCommandParser = new CommandParser("find start today due tomorrow");
		assertFalse(testCommandParser.getFields().isEmpty());
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s tdy -d tmr", testCommandParser.getFieldStringPrim());
		assertEquals(
				String.format(MessageConstants.INVALID_FIELD_CRITERIA, "tdy")
						+ String.format(
								MessageConstants.INVALID_FIELD_CRITERIA, "tmr"),
				testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("find start before due after");
		assertFalse(testCommandParser.getFields().isEmpty());
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-s bf -d af", testCommandParser.getFieldStringPrim());
		assertEquals("", testCommandParser.getFeedback().toString());
	}

	@Test
	public void testNameField() {

		/* Valid task name */
		testCommandParser = new CommandParser("edit 1 name task 1\\");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals("", testCommandParser.getFieldStringPrim());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"edit 1 name task 1\\ due tmr priority H");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals("-d tmr -p H", testCommandParser.getFieldStringPrim());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"edit 1 due tmr name task 1\\ priority H");
		assertEquals(ActionType.EDIT, testCommandParser.getAction().getType());
		assertEquals("-d tmr -p H", testCommandParser.getFieldStringPrim());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("find name task 1\\");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("", testCommandParser.getFieldStringPrim());	
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser(
				"find due on tmr start between 12/12 and 12/12 name task 1\\");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-d on tmr -s btw 12/12 & 12/12",
				testCommandParser.getFieldStringPrim());
		assertEquals("task 1", testCommandParser.getTaskName());
		assertEquals("", testCommandParser.getFeedback().toString());

		/* Invalid task name */
		testCommandParser = new CommandParser(
				"find name task 1 due on tmr start between 12/12 and 12/12");
		assertEquals(ActionType.FIND, testCommandParser.getAction().getType());
		assertEquals("-d on tmr -s btw 12/12 & 12/12",
				testCommandParser.getFieldStringClean());
		assertNull(testCommandParser.getTaskName());
		assertEquals(
				MessageConstants.NAME_NO_CLOSE
						+ String.format(
								MessageConstants.INVALID_FIELD_FORMAT_IN,
								"name task 1"), testCommandParser.getFeedback()
						.toString());
	}

	@Test
	public void testCompleteDeleteActionType() {

		/* Valid task number */
		testCommandParser = new CommandParser("delete 1");
		assertEquals(ActionType.DELETE, testCommandParser.getAction().getType());
		assertEquals(1, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("d 1");
		assertEquals(ActionType.DELETE, testCommandParser.getAction().getType());
		assertEquals(1, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("complete 1 y");
		assertEquals(ActionType.COMPLETE, testCommandParser.getAction()
				.getType());
		assertEquals(1, testCommandParser.getTaskNumber());
		assertTrue(testCommandParser.getCompleteYesNo());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("c 1 n");
		assertEquals(ActionType.COMPLETE, testCommandParser.getAction()
				.getType());
		assertEquals(1, testCommandParser.getTaskNumber());
		assertFalse(testCommandParser.getCompleteYesNo());
		assertEquals("", testCommandParser.getFeedback().toString());

		/* Invalid task number */
		testCommandParser = new CommandParser("complete 0");
		assertEquals(ActionType.COMPLETE, testCommandParser.getAction()
				.getType());
		assertEquals(0, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("complete 0 y");
		assertEquals(ActionType.COMPLETE, testCommandParser.getAction()
				.getType());
		assertEquals(0, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("complete -1 n");
		assertEquals(ActionType.COMPLETE, testCommandParser.getAction()
				.getType());
		assertEquals(-1, testCommandParser.getTaskNumber());
		assertEquals("", testCommandParser.getFeedback().toString());
	}

	@Test
	public void testNonFieldContent() {

		testCommandParser = new CommandParser("find xxx xxx due on tmr");
		assertEquals("-d on tmr", testCommandParser.getFieldStringClean());
		assertEquals(String.format(MessageConstants.INVALID_FIELD_FORMAT_IN,
				"xxx xxx"), testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("e tdy due tmr start");
		assertEquals("-d tmr -s", testCommandParser.getFieldStringClean());
		assertEquals(
				String.format(MessageConstants.INVALID_FIELD_FORMAT_IN, "tdy"),
				testCommandParser.getFeedback().toString());

		testCommandParser = new CommandParser("find low priority due tmr");
		assertEquals("-p -d tmr", testCommandParser.getFieldStringClean());
		assertEquals(
				String.format(MessageConstants.INVALID_FIELD_FORMAT_IN, "l")
						+ String.format(
								MessageConstants.INVALID_FIELD_CRITERIA, "tmr"),
				testCommandParser.getFeedback().toString());

	}

}
