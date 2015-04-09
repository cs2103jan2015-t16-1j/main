package test;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.ActionType;
import quicklyst.DateParser;
import quicklyst.Field;
import quicklyst.FieldCriteria;
import quicklyst.FieldParser;
import quicklyst.FieldType;

//@author A0102015H
/*
 * Dates are not tested extensively as it is covered in DateParserTest Class
 * 
 */
public class FieldParserTest {

	private FieldParser fieldParser;
	private Field field;
	private Object content;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEditDateField() {

		/* Valid due date and time */
		fieldParser = new FieldParser("d 12/12/12 12:12");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		content = new DateParser("12/12/12 12:12").getDateTime();
		assertEquals((Calendar) content, field.getDate());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Clear due date and time */
		fieldParser = new FieldParser("d clr");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.CLEAR_DATE, field.getCriteria());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Invalid due date and time */
		/* For detailed date parsing tests, use DateParserTest Class */
		fieldParser = new FieldParser("d xxx xxx");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getDate());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals("Invalid day criteria \"xxx\". ",
				fieldParser.getFeedback());

		fieldParser = new FieldParser("d xxx");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getDate());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals("Invalid day \"xxx\" entered. ", fieldParser.getFeedback());

		/* Valid start date and time */
		fieldParser = new FieldParser("s 12/12/12 12:12");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		content = new DateParser("12/12/12 12:12").getDateTime();
		assertEquals((Calendar) content, field.getDate());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Clear start date and time */
		fieldParser = new FieldParser("s clr");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.CLEAR_DATE, field.getCriteria());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Invalid start date and time */
		/* For detailed date parsing tests, use DateParserTest Class */
		fieldParser = new FieldParser("s xxx xxx");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getDate());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals("Invalid day criteria \"xxx\". ",
				fieldParser.getFeedback());

		fieldParser = new FieldParser("s xxx");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getDate());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals("Invalid day \"xxx\" entered. ", fieldParser.getFeedback());
	}

	@Test
	public void testFindDateField() {

		/* Valid "on" criteria */
		fieldParser = new FieldParser("d on 12/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		content = new DateParser("12/12/12").getDateTime();
		assertEquals((Calendar) content, field.getDate());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals(FieldCriteria.ON, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());

		/* Valid "before" criteria */
		fieldParser = new FieldParser("s bf 12/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		content = new DateParser("12/12/12").getDateTime();
		assertEquals((Calendar) content, field.getDate());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BEFORE, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());

		/* Valid "after" criteria */
		fieldParser = new FieldParser("d af 12/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		content = new DateParser("12/12/12").getDateTime();
		assertEquals((Calendar) content, field.getDate());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals(FieldCriteria.AFTER, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());

		/* Valid "between" criteria */
		Calendar dateRange[];
		fieldParser = new FieldParser("s btw 12/12/12 & 13/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertEquals((Calendar[]) content, field.getDateRange());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());

		/* Invalid "between" criteria */
		fieldParser = new FieldParser("s btw 12/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals("Invalid date range. ", fieldParser.getFeedback());

		fieldParser = new FieldParser("s btw 12/12/12 & xxx");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals("Invalid day \"xxx\" entered. ", fieldParser.getFeedback());

		fieldParser = new FieldParser("d btw xxx & 12/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals("Invalid day \"xxx\" entered. ", fieldParser.getFeedback());

		fieldParser = new FieldParser("d btw xxx & yyy");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals(
				"Invalid day \"xxx\" entered. Invalid day \"yyy\" entered. ",
				fieldParser.getFeedback());

		fieldParser = new FieldParser("d btw 12/12/12 to 13/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertEquals(FieldCriteria.BETWEEN, field.getCriteria());
		assertEquals("Invalid date range. ", fieldParser.getFeedback());
	}

	@Test
	public void testPriorityField() {

		/*
		 * Test valid priority levels
		 * 
		 * Full words form like "high", "low", etc are omitted as they are
		 * processed into single letter form in CommandParser Class
		 */
		fieldParser = new FieldParser("p h");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals("H", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p m");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals("M", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p l");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals("L", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p H");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals("H", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p M");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertEquals("M", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p L");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals("L", field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Test invalid priority level */
		fieldParser = new FieldParser("p n");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("Invalid priority level \"n\". ",
				fieldParser.getFeedback());

		fieldParser = new FieldParser("p n");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getPriority());
		assertEquals(FieldType.PRIORITY, field.getFieldType());
		assertEquals("Invalid priority level \"n\". ",
				fieldParser.getFeedback());

		/* Test valid priority clear */
		fieldParser = new FieldParser("p CLR");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getPriority());
		assertEquals(FieldCriteria.CLEAR_PRIORITY, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("p clr");
		fieldParser.setActionType(ActionType.EDIT);
		field = fieldParser.getField();
		assertNull(field.getPriority());
		assertEquals(FieldCriteria.CLEAR_PRIORITY, field.getCriteria());
		assertEquals("", fieldParser.getFeedback());
	}

	@Test
	public void testOverdueField() {

		/*
		 * Test valid overdue criteria
		 * 
		 * Full words form "yes" and "no" are omitted as they are processed into
		 * single letter form in CommandParser Class
		 */
		fieldParser = new FieldParser("o y");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.YES, field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("o n");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.NO, field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("o N");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.NO, field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("o Y");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.YES, field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Test invalid overdue criteria */
		fieldParser = new FieldParser("o m");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("Invalid field criteria \"m\". ",
				fieldParser.getFeedback());

	}

	@Test
	public void testCompletedField() {

		/*
		 * Test valid completed criteria
		 * 
		 * Full words form "yes" and "no" are omitted as they are processed into
		 * single letter form in CommandParser Class
		 */
		fieldParser = new FieldParser("c y");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.YES, field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("c n");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.NO, field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("c N");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.NO, field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		fieldParser = new FieldParser("c Y");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertEquals(FieldCriteria.YES, field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("", fieldParser.getFeedback());

		/* Test invalid completed criteria */
		fieldParser = new FieldParser("c m");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("Invalid field criteria \"m\". ",
				fieldParser.getFeedback());
	}

	@Test
	public void testInvalidFieldCriteriaCombinations() {

		Calendar[] dateRange;
		/* Start and due dates for FIND */
		fieldParser = new FieldParser("d y 12/12/12 & 13/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.DUE_DATE, field.getFieldType());
		assertNull(field.getCriteria());
		assertEquals("Invalid field criteria \"y\". ",
				fieldParser.getFeedback());
		
		fieldParser = new FieldParser("s clr 12/12/12 & 13/12/12");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		dateRange = new Calendar[] { new DateParser("12/12/12").getDateTime(),
				new DateParser("13/12/12").getDateTime() };
		content = dateRange;
		assertNull(field.getDateRange());
		assertEquals(FieldType.START_DATE, field.getFieldType());
		assertNull(field.getCriteria());
		assertEquals("Invalid field criteria \"clr\". ",
				fieldParser.getFeedback());
		
		/* Overdue status for FIND */
		fieldParser = new FieldParser("o on");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("Invalid field criteria \"on\". ", fieldParser.getFeedback());
		
		fieldParser = new FieldParser("o af");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.OVERDUE, field.getFieldType());
		assertEquals("Invalid field criteria \"af\". ", fieldParser.getFeedback());
		
		/* Completed status for FIND */
		fieldParser = new FieldParser("c btw");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("Invalid field criteria \"btw\". ",
				fieldParser.getFeedback());
		
		fieldParser = new FieldParser("c bf");
		fieldParser.setActionType(ActionType.FIND);
		field = fieldParser.getField();
		assertNull(field.getCriteria());
		assertEquals(FieldType.COMPLETED, field.getFieldType());
		assertEquals("Invalid field criteria \"bf\". ",
				fieldParser.getFeedback());
	}
}
