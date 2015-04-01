package quicklyst;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class DateParser {

	private static final String DATE_TIME_FORMAT_1 = "dd/MM/yy HH:mm";
	private static final String DATE_TIME_FORMAT_2 = "dd/MM HH:mm";
	private static final String DATE_FORMAT_1 = "dd/MM/yy";
	private static final String DATE_FORMAT_2 = "dd/MM";
	private static final String TIME_FORMAT = "HH:mm";

	private Calendar _dateTime;
	private StringBuilder _feedback;

	private boolean _dateParsed;
	private boolean _timeParsed;

	public DateParser(String dateTimeStr) {
		_feedback = new StringBuilder();
		_dateTime = new GregorianCalendar();
		_dateParsed = false;
		_timeParsed = false;
		parseDateTime(dateTimeStr);
	}

	private void parseDateTime(String dateTimeStr) {

		if (dateTimeStr.contains(":") && dateTimeStr.contains("/")) {

			try {
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
						DATE_TIME_FORMAT_1);
				_dateTime.setTime(dateTimeFormat.parse(dateTimeStr));
				_dateParsed = true;
				_timeParsed = true;
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
				_dateTime = null;
			} catch (ParseException e) {

				try {
					SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
							DATE_TIME_FORMAT_2);
					_dateTime.setTime(dateTimeFormat.parse(dateTimeStr));
					_dateTime.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					_dateParsed = true;
					_timeParsed = true;
				} catch (ParseException e2) {
					_feedback.append("Invalid date and time format. ");
					_dateTime = null;
					System.out.println(e2.getMessage());
				}
			}

		} else if (dateTimeStr.contains(":") && dateTimeStr.contains(" ")) {

			String[] dayAndTime = dateTimeStr.split(" ", 2);
			String day = dayAndTime[0];
			String time = dayAndTime[1];
			parseDay(day);
			parseTime(time);

		} else if (dateTimeStr.contains(":")) {

			parseTime(dateTimeStr);

		} else if (dateTimeStr.contains("/")) {

			parseDate(dateTimeStr);

		} else {

			parseDay(dateTimeStr);
		}
	}

	private void parseDate(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
		try {
			_dateTime.setTime(dateFormat.parse(dateStr));
			_dateParsed = true;
		} catch (NullPointerException e) {
			_dateTime = null;
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			try {
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						DATE_FORMAT_2);
				_dateTime.setTime(timeFormat.parse(dateStr));
				_dateTime.set(Calendar.YEAR,
						Calendar.getInstance().get(Calendar.YEAR));
				_dateParsed = true;
			} catch (ParseException e2) {
				_feedback.append("Invalid date format. ");
				_dateTime = null;
				System.out.println(e2.getMessage());
			}
		}
	}

	private void parseTime(String timeStr) {

		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

		Calendar timeOfDay = new GregorianCalendar();

		try {
			timeOfDay.setTime(timeFormat.parse(timeStr));
			_timeParsed = true;
		} catch (NullPointerException e) {
			_dateTime = null;
			return;
		} catch (ParseException e) {
			_dateTime = null;
			_feedback.append("Invalid time \"" + timeStr + "\" entered. ");
			return;
		}

		_dateTime
				.set(Calendar.HOUR_OF_DAY, timeOfDay.get(Calendar.HOUR_OF_DAY));
		_dateTime.set(Calendar.MINUTE, timeOfDay.get(Calendar.MINUTE));

		if (!_dateParsed) {
			Calendar now = new GregorianCalendar();
			if (_dateTime.getTimeInMillis() < now.getTimeInMillis()) {
				_dateTime.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
	}

	private void parseDay(String dayStr) {

		if (dayStr.equalsIgnoreCase("tmr") || dayStr.equalsIgnoreCase("tdy")) {
			if (dayStr.equalsIgnoreCase("tmr")) {
				_dateTime.add(Calendar.DAY_OF_MONTH, 1);
			}
			_dateParsed = true;
		} else {

			SimpleDateFormat format = new SimpleDateFormat("EEE");
			Calendar dayOfWeek = new GregorianCalendar();

			try {
				dayOfWeek.setTime(format.parse(dayStr));
				_dateParsed = true;
			} catch (NullPointerException e) {
				_dateTime = null;
				return;
			} catch (ParseException e) {
				_dateTime = null;
				_feedback.append("Invalid day \"" + dayStr + "\" entered. ");
				return;
			}
			;

			int dayOfWeekInt = dayOfWeek.get(Calendar.DAY_OF_WEEK);

			while (dayOfWeekInt != _dateTime.get(Calendar.DAY_OF_WEEK)) {
				_dateTime.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
	}

	public Calendar getDateTime() {
		return _dateTime;
	}

	public StringBuilder getFeedback() {
		return _feedback;
	}

	public boolean isDateParsed() {
		return _dateParsed;
	}

	public boolean isTimeParsed() {
		return _timeParsed;
	}

	public static void main(String args[]) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy, HH:mm");
		Scanner sc = new Scanner(System.in);
		while (true) {
			String datetimeStr = sc.nextLine();
			DateParser dp = new DateParser(datetimeStr);
			Calendar cal = dp.getDateTime();
			try {
				System.out.println(format.format(cal.getTime()) + " "
						+ dp.getFeedback());
			} catch (NullPointerException e) {
				System.out.println("       " + dp.getFeedback());
			}
		}
	}
}
