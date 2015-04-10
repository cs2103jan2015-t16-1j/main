package test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.ActionType;
import quicklyst.AddAction;
import quicklyst.Field;
import quicklyst.FieldParser;
import quicklyst.MessageConstants;
import quicklyst.Task;

//@author A0102015H
/*
 * AddAction make use of EditAction to update fields of a task.
 * Different Field cases are not tested extensively as it is covered in EditActionTest Class
 * 
 */
public class AddActionTest {

	private AddAction add;
	private LinkedList<Field> fields;
	private FieldParser fp;

	private LinkedList<Task> displayList;
	private LinkedList<Task> masterList;

	@Before
	public void setUp() throws Exception {
		displayList = new LinkedList<Task>();
		masterList = new LinkedList<Task>();
		fields = new LinkedList<Field>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/*
	 * Blank name not tested ad it is not supposed to happen in AddAction as
	 * CommandParser Class already rejects empty name.
	 */
	public void testAddNoField() {

		/* Null name */
		add = new AddAction(null, null);
		add.execute(displayList, masterList);
		assertEquals(0, displayList.size());
		assertEquals(0, masterList.size());
		assertEquals(MessageConstants.MESSAGE_NOTHING_ADDED, add.getFeedback()
				.toString());

		/* One word name */
		add = new AddAction("task", null);
		add.execute(displayList, masterList);
		assertEquals("task", displayList.getFirst().getName());
		assertEquals("task", masterList.getFirst().getName());
		assertEquals(
				String.format(MessageConstants.MESSAGE_ADD_SUCCESS, "task"),
				add.getFeedback().toString());
		displayList.clear();
		masterList.clear();

		/* Multiple word name */
		add = new AddAction("task one two", null);
		add.execute(displayList, masterList);
		assertEquals("task one two", displayList.getFirst().getName());
		assertEquals("task one two", masterList.getFirst().getName());
		assertEquals(String.format(MessageConstants.MESSAGE_ADD_SUCCESS,
				"task one two"), add.getFeedback().toString());
		displayList.clear();
		masterList.clear();

	}

	@Test
	public void testAddHasField() {

		/* Not null name */
		fp = new FieldParser("d 12/12/12 7:30");
		fp.setActionType(ActionType.ADD);
		fields.add(fp.getField());
		add = new AddAction("task one two", fields);
		add.execute(displayList, masterList);
		assertEquals(String.format(MessageConstants.MESSAGE_ADD_SUCCESS
				+ MessageConstants.MESSAGE_DUE_DATE_SET, "task one two",
				"12/12/2012 0730H"), add.getFeedback().toString());
		displayList.clear();
		masterList.clear();
		fields.clear();

		fp = new FieldParser("s 12/12/12");
		fp.setActionType(ActionType.ADD);
		fields.add(fp.getField());
		add = new AddAction("task one two", fields);
		add.execute(displayList, masterList);
		assertEquals(String.format(MessageConstants.MESSAGE_ADD_SUCCESS
				+ MessageConstants.MESSAGE_START_DATE_SET, "task one two",
				"12/12/2012"), add.getFeedback().toString());
		displayList.clear();
		masterList.clear();
		fields.clear();

		fp = new FieldParser("p H");
		fp.setActionType(ActionType.ADD);
		fields.add(fp.getField());
		add = new AddAction("task one two", fields);
		add.execute(displayList, masterList);
		assertEquals(String.format(MessageConstants.MESSAGE_ADD_SUCCESS
				+ MessageConstants.MESSAGE_PRIORITY_SET, "task one two", "H"),
				add.getFeedback().toString());
		displayList.clear();
		masterList.clear();
		fields.clear();

		/* Null name */
		fp = new FieldParser("d 12/12/12 7:30");
		fp.setActionType(ActionType.ADD);
		fields.add(fp.getField());
		add = new AddAction(null, fields);
		add.execute(displayList, masterList);
		assertEquals(0, displayList.size());
		assertEquals(0, masterList.size());
		assertEquals(MessageConstants.MESSAGE_NOTHING_ADDED, add.getFeedback()
				.toString());

	}
}
