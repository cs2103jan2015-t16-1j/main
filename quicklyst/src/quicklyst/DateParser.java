package quicklyst;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

//@author A0102015H
public class DateParser {

	private static final String PRIM_TODAY = "tdy";
	private static final String PRIM_TOMORROW = "tmr";
	private static final String STRING_NEXT = "next";
	private static final String SPACE = " ";
	private static final String BACK_SLASH = "/";
	private static final String COLON = ":";

	private static final String DATE_TIME_FORMAT_1 = "dd/MM/yy HH:mm";
	private static final String DATE_TIME_FORMAT_2 = "dd/MM HH:mm";
	private static final String DATE_FORMAT_1 = "dd/MM/yy";
	private static final String DATE_FORMAT_2 = "dd/MM";
	private static final String TIME_FORMAT = "HH:mm";
	private static final String DAY_FORMAT = "EEE";

	private Calendar _dateTime;
	private StringBuilder _feedback;

	private boolean _dateParsed;
	private boolean _timeParsed;

	public DateParser(String dateTimeStr) {
		_feedback = new StringBuilder();
		_dateTime = new GregorianCalendar();
		_dateParsed = false;
		_timeParsed = false;
		parse(dateTimeStr);
	}

	private void parse(String dateTimeStr) {

		if (dateTimeStr.contains(COLON) && dateTimeStr.contains(BACK_SLASH)) {

			parseDateTime(dateTimeStr);

		} else if (dateTimeStr.contains(COLON) && dateTimeStr.contains(SPACE)) {

			parseDayTime(dateTimeStr);

		} else if (dateTimeStr.contains(COLON)) {

			parseTime(dateTimeStr);

		} else if (dateTimeStr.contains(BACK_SLASH)) {

			parseDate(dateTimeStr);

		} else {

			determineDay(dateTimeStr);
		}
	}

	private void determineDay(String dateTimeStr) {
		String nextAndDay[] = dateTimeStr.split(SPACE, 2);
		String day;

		if (nextAndDay.length == 2
				&& nextAndDay[0].trim().equalsIgnoreCase(STRING_NEXT)) {

			day = nextAndDay[1];
			parseDay(day, true);

		} else if (nextAndDay.length == 2
				&& !nextAndDay[0].trim().equalsIgnoreCase(STRING_NEXT)) {

			_dateTime = null;
			_feedback
					.append(String.format(
							GlobalConstants.INVALID_DAY_CRITERIA,
							nextAndDay[0].trim()));

		} else if (nextAndDay.length == 1) {

			day = nextAndDay[0];
			parseDay(day, false);
		}
	}

	private void parseDayTime(String dateTimeStr) {
		String[] dayAndTime = dateTimeStr.split(SPACE, 3);
		String day;
		String time;

		if (dayAndTime.length == 3) {

			if (dayAndTime[0].trim().equalsIgnoreCase(STRING_NEXT)) {

				day = dayAndTime[1];
				time = dayAndTime[2];
				parseDay(day, true);
				parseTime(time);

			} else {

				_dateTime = null;
				_feedback.append(String.format(
						GlobalConstants.INVALID_DAY_CRITERIA,
						dayAndTime[0].trim()));

			}

		} else if (dayAndTime.length == 2) {

			day = dayAndTime[0];
			time = dayAndTime[1];
			parseDay(day, false);
			parseTime(time);
		}
	}

	private void parseDateTime(String dateTimeStr) {
		try {

			SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
					DATE_TIME_FORMAT_1);
			_dateTime.setTime(dateTimeFormat.parse(dateTimeStr));
			_dateParsed = true;
			_timeParsed = true;

		} catch (NullPointerException e) {

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

				_feedback.append(GlobalConstants.INVALID_DATE_AND_TIME_FORMAT);
				_dateTime = null;
			}
		}
	}

	private void parseDate(String dateStr) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);

		try {

			_dateTime.setTime(dateFormat.parse(dateStr));
			_dateParsed = true;

		} catch (NullPointerException e) {

			_dateTime = null;

		} catch (ParseException e) {

			try {

				SimpleDateFormat timeFormat = new SimpleDateFormat(
						DATE_FORMAT_2);
				_dateTime.setTime(timeFormat.parse(dateStr));
				_dateTime.set(Calendar.YEAR,
						Calendar.getInstance().get(Calendar.YEAR));
				_dateParsed = true;

			} catch (ParseException e2) {

				_feedback.append(GlobalConstants.INVALID_DATE_FORMAT);
				_dateTime = null;
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
			_feedback.append(String.format(GlobalConstants.INVALID_TIME,
					timeStr));
			return;
		}

		if (_dateTime != null) {

			_dateTime.set(Calendar.HOUR_OF_DAY,
					timeOfDay.get(Calendar.HOUR_OF_DAY));
			_dateTime.set(Calendar.MINUTE, timeOfDay.get(Calendar.MINUTE));

			if (!_dateParsed) {

				Calendar now = new GregorianCalendar();

				if (_dateTime.compareTo(now) < 0) {
					_dateTime.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
		}
	}

	private void parseDay(String dayStr, boolean isNextWeek) {

		if (dayStr.equalsIgnoreCase(PRIM_TOMORROW)
				|| dayStr.equalsIgnoreCase(PRIM_TODAY)) {

			if (dayStr.equalsIgnoreCase(PRIM_TOMORROW)) {
				_dateTime.add(Calendar.DAY_OF_MONTH, 1);
			}
			_dateParsed = true;

		} else {

			SimpleDateFormat format = new SimpleDateFormat(DAY_FORMAT);
			Calendar dayOfWeek = new GregorianCalendar();

			try {

				dayOfWeek.setTime(format.parse(dayStr));
				_dateParsed = true;
				determineDateFromDay(isNextWeek, dayOfWeek);

			} catch (NullPointerException e) {

				_dateTime = null;

			} catch (ParseException e) {

				_dateTime = null;
				_feedback.append(String.format(GlobalConstants.INVALID_DAY,
						dayStr));
			}
		}
	}

	private void determineDateFromDay(boolean isNextWeek, Calendar dayOfWeek) {
		int dayOfWeekInt = dayOfWeek.get(Calendar.DAY_OF_WEEK);

		if (isNextWeek) {

			boolean weekCrossed = false;
			int prevDay = _dateTime.get(Calendar.DAY_OF_WEEK);

			while (dayOfWeekInt != _dateTime.get(Calendar.DAY_OF_WEEK)
					|| !weekCrossed) {

				_dateTime.add(Calendar.DAY_OF_MONTH, 1);

				if (!weekCrossed
						&& _dateTime.get(Calendar.DAY_OF_WEEK) <= prevDay) {

					weekCrossed = true;
				}
			}

		} else {

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
}
