package test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.DateParser;


//@author A0102015H

/*
 * Testing of day of week, today and tomorrow cannot be automated, as the result
 * changes depending on the day the test is carried out. Such tests are done manually 
 * 
 */
public class DateParserTest {

	private static final String DATE_TIME_FORMAT = "dd/MM/yy HH:mm";
	private static final String DATE_FORMAT = "dd/MM/yy";
	private static final String TIME_FORMAT = "HH:mm";

	private SimpleDateFormat formatDateTime;
	private SimpleDateFormat formatDate;
	private SimpleDateFormat formatTime;

	private Calendar testDate;
	private DateParser testDateParser;
	private String testDateString;
	private String feedback;

	@Before
	public void setUp() throws Exception {
		formatDateTime = new SimpleDateFormat(DATE_TIME_FORMAT);
		formatDate = new SimpleDateFormat(DATE_FORMAT);
		formatTime = new SimpleDateFormat(TIME_FORMAT);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/** Test valid date and time **/
	public void testDateTimeValid() {

		/* Full date and time formats */
		testDateString = "12/12/2012 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/12/12 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "12/12/12 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/12/12 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01/01/01 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01/01/01 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1/1/01 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01/01/01 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		/* Half date and full time formats */
		testDateString = "12/12 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/12/15 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01/01 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01/01/15 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1/1 12:12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01/01/15 12:12",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		/* Varying date and half time formats */
		testDateString = "12/12/12 09:30";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/12/12 09:30",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "12/12 9:30";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/12/15 09:30",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1/12 9:9";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01/12/15 09:09",
				formatDateTime.format(testDate.getTime()));
		assertEquals("", feedback);
	}

	@Test
	/** Test valid date**/
	public void testDateValid() {

		/* Full date formats */
		testDateString = "12/12/2012";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("12/12/12", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "12/12/12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("12/12/12", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		/* Half date formats */
		testDateString = "12/12";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("12/12/15", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01/01";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("01/01/15", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1/01";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("01/01/15", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01/1";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("01/01/15", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1/1";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("01/01/15", formatDate.format(testDate.getTime()));
		assertEquals("", feedback);
	}

	@Test
	/** Test valid time**/
	public void testTimeValid() {

		testDateString = "23:59";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("23:59", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "00:00";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("00:00", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "00:0";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		feedback = testDateParser.getFeedback().toString();
		assertEquals("00:00", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "0:00";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("00:00", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "0:0";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("00:00", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01:01";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01:01", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1:01";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01:01", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "01:1";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01:01", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);

		testDateString = "1:1";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("01:01", formatTime.format(testDate.getTime()));
		assertEquals("", feedback);
	}

	@Test
	/** Test invalid date and time**/
	public void testDateTimeInvalid() {

		/* Invalid date and time formats */
		testDateString = "12032015 1600";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertNull(testDate);

		testDateString = "12/032015 1:600";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/11/15 11:00",
				formatDateTime.format(testDate.getTime()));

		testDateString = "120320/15 160:0";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("08/08/15 16:00",
				formatDateTime.format(testDate.getTime()));

		/* Invalid date and valid time format */
		testDateString = "12032015 12:30";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertNull(testDate);

		testDateString = "12/032015 12:30";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("12/11/15 12:30",
				formatDateTime.format(testDate.getTime()));

		testDateString = "120320/15  12:30";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("02/08/15 12:30",
				formatDateTime.format(testDate.getTime()));

		/* Invalid time and valid date format */
		testDateString = "31/12/15 1600";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertEquals("31/12/15 00:00",
				formatDateTime.format(testDate.getTime()));

		testDateString = "31/12/15 1:600";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("31/12/15 11:00",
				formatDateTime.format(testDate.getTime()));

		testDateString = "31/12/15 160:0";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertTrue(testDateParser.isDateParsed());
		assertTrue(testDateParser.isTimeParsed());
		assertEquals("06/01/16 16:00",
				formatDateTime.format(testDate.getTime()));

		/* Spaces in between numbers */
		testDateString = "31 / 12 / 15 16 : 00";
		testDateParser = new DateParser(testDateString);
		testDate = testDateParser.getDateTime();
		feedback = testDateParser.getFeedback().toString();
		assertFalse(testDateParser.isDateParsed());
		assertFalse(testDateParser.isTimeParsed());
		assertNull(testDate);
	}
}
