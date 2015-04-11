package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.QLLogic;

/**
 * Testing of Storage, Settings and GoogleIntegration Classes are omitted in
 * this test. Testing of History Manager, getDisplayList and getFullList is
 * done in this test.
 * 
 */
public class QLLogicTest {

	private static final String DEFAULT_TASK_ADD_COMMAND_1 = "add task one\\ start 11/12/2012 7:30 due 12/12/2012 7:30 priority L";
	private static final String DEFAULT_TASK_ADD_COMMAND_2 = "add task two\\ start 10/12/2012 7:30 due 13/12/2012 7:30 priority M";
	private static final String DEFAULT_TASK_ADD_COMMAND_3 = "add task three\\ start 8/12/2017 7:30 due 14/12/2017 7:30 priority H";
	private static final String DEFAULT_TASK_ADD_COMMAND_4 = "add task four\\ start 9/12/2017 7:30 due 14/12/2017 7:30";

	private static final String COMPELTE_COMMAND_1 = "c 1";
	private static final String COMPELTE_COMMAND_4 = "c 4";

	private static final String DELETE_COMMAND_1 = "c 1";

	private static final String UNDO_COMMAND = "u";
	private static final String REDO_COMMAND = "r";

	private static final String[] TASK_NAMES = { "task one", "task two",
			"task three", "task four" };

	private QLLogic logicTest;
	private StringBuilder feedback;

	@Before
	public void setUp() throws Exception {
		logicTest = QLLogic.getInstance();
		logicTest.setupStub();
		feedback = new StringBuilder();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetFullList() {

		/* No complete task */
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_2,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_1,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_4,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_3,
				new StringBuilder());
		assertEquals(4, logicTest.getFullList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getFullList().get(i)
					.getName());
		}

		/* Complete overdue task */
		logicTest.executeCommand(COMPELTE_COMMAND_1, new StringBuilder());
		assertEquals(4, logicTest.getFullList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getFullList().get(i)
					.getName());
		}

		/* Complete not overdue task */
		logicTest.executeCommand(COMPELTE_COMMAND_4, new StringBuilder());
		assertEquals(4, logicTest.getFullList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getFullList().get(i)
					.getName());
		}

		/* Complete not overdue task */
		logicTest.executeCommand(COMPELTE_COMMAND_4, new StringBuilder());
		assertEquals(4, logicTest.getFullList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getFullList().get(i)
					.getName());
		}

		/* Delete task */
		logicTest.executeCommand(DELETE_COMMAND_1, new StringBuilder());
		assertEquals(4, logicTest.getFullList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getFullList().get(i)
					.getName());
		}
	}

	@Test
	public void testGetDisplayList() {

		/* No complete task */
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_2,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_1,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_4,
				new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_3,
				new StringBuilder());
		assertEquals(4, logicTest.getDisplayList().size());
		for (int i = 0; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getDisplayList().get(i)
					.getName());
		}

		/* Complete overdue task */
		logicTest.executeCommand(COMPELTE_COMMAND_1, new StringBuilder());
		assertEquals(3, logicTest.getDisplayList().size());
		for (int i = 1; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getDisplayList().get(i - 1)
					.getName());
		}

		/* Find overdue completed task */
		logicTest.executeCommand("f -c y", new StringBuilder());
		assertEquals(1, logicTest.getDisplayList().size());
		assertEquals(TASK_NAMES[0], logicTest.getDisplayList().getFirst()
				.getName());
		logicTest.executeCommand(UNDO_COMMAND, new StringBuilder());

		/* Complete not overdue task */
		logicTest.executeCommand(COMPELTE_COMMAND_4, new StringBuilder());
		assertEquals(3, logicTest.getDisplayList().size());
		for (int i = 1; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getDisplayList().get(i - 1)
					.getName());
		}

		/* Delete task */
		logicTest.executeCommand(DELETE_COMMAND_1, new StringBuilder());
		assertEquals(2, logicTest.getDisplayList().size());
		for (int i = 2; i < 4; i++) {
			assertEquals(TASK_NAMES[i], logicTest.getDisplayList().get(i - 2)
					.getName());
		}
		assertEquals(4, logicTest.getFullList().size());
	}

	@Test
	public void testUndoRedo() {

		/* Test undo */
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_1,
				new StringBuilder());
		logicTest.executeCommand(UNDO_COMMAND, feedback);
		assertTrue(logicTest.getDisplayList().isEmpty());
		assertEquals("", feedback.toString());

		logicTest.executeCommand(UNDO_COMMAND, feedback);
		assertEquals("Nothing to undo. ", feedback.toString());

		feedback = new StringBuilder();

		/* Test redo */
		logicTest.executeCommand(REDO_COMMAND, feedback);
		assertEquals(TASK_NAMES[0], logicTest.getDisplayList().getFirst()
				.getName());
		assertEquals(1, logicTest.getDisplayList().size());
		assertEquals("", feedback.toString());

		logicTest.executeCommand(REDO_COMMAND, feedback);
		assertEquals("Nothing to redo. ", feedback.toString());

		feedback = new StringBuilder();

		/* Test redo after execute after undo */
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_1,
				new StringBuilder());
		logicTest.executeCommand(UNDO_COMMAND, new StringBuilder());
		logicTest.executeCommand(DEFAULT_TASK_ADD_COMMAND_2,
				new StringBuilder());
		logicTest.executeCommand(REDO_COMMAND, feedback);
		assertEquals("Nothing to redo. ", feedback.toString());

		feedback = new StringBuilder();
	}
}
