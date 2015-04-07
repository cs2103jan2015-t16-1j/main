package quicklyst;

import java.util.Calendar;
import java.util.regex.Pattern;

public class FieldParser {

	private StringBuilder _feedback;
	private String _fieldString;

	private FieldType _fieldType;
	private ActionType _actionType;
	private Object _fieldContent;
	private FieldCriteria _fieldCriteria;
	private boolean _dateParsed;
	private boolean _timeParsed;

	private Field _field;

	public FieldParser(String fieldString) {
		assert !fieldString.equals("");
		_feedback = new StringBuilder();
		_fieldString = fieldString;
	}

	public void setActionType(ActionType a) {
		_actionType = a;
	}

	private void parseField() {
		String[] typeAndContent = _fieldString.split(" ", 2);
		determineType(typeAndContent[0].trim());
		if (typeAndContent.length == 2 && _fieldType != null) {
			determineContent(typeAndContent[1].trim());
		}
	}

	private void determineType(String type) {
		if (type.length() != 1) {
			_feedback.append("Invalid field " + type + ". ");
			return;
		} else {
			switch (type.charAt(0)) {
			case 'd':
				_fieldType = FieldType.DUE_DATE;
				break;
			case 's':
				_fieldType = FieldType.START_DATE;
				break;
			case 'r':
				_fieldType = FieldType.REMINDER;
				break;
			case 'l':
				_fieldType = FieldType.DURATION;
				break;
			case 'p':
				_fieldType = FieldType.PRIORITY;
				break;
			case 'n':
				_fieldType = FieldType.TASK_NAME;
				break;
			case 'c':
				_fieldType = FieldType.COMPLETED;
				break;
			case 'o':
				_fieldType = FieldType.OVERDUE;
				break;
			default:
				break;
			}
		}
	}

	private void determineContent(String content) {
		if (content.isEmpty()) {
			return;
		} else if (_fieldType == FieldType.TASK_NAME) {

			_fieldContent = content.replaceAll(Pattern.quote("\\"), "");

		} else if (_fieldType == FieldType.DUE_DATE
				|| _fieldType == FieldType.START_DATE
				|| _fieldType == FieldType.REMINDER) {

			switch (_actionType) {
			case ADD:
			case EDIT:
				determineDate(content);
				break;
			case FIND:
				determineFindDate(content);
				break;
			case SORT:
				determineCriteria(content);
				break;
			default:
				break;
			}

		} else if (_fieldType == FieldType.PRIORITY) {

			switch (_actionType) {
			case ADD:
			case EDIT:
			case FIND:
				determinePriority(content);
				break;
			case SORT:
				determineCriteria(content);
				break;
			default:
				break;
			}

		} else if (_fieldType == FieldType.COMPLETED
				|| _fieldType == FieldType.OVERDUE
				|| _fieldType == FieldType.DURATION) {
			determineCriteria(content);
		}
	}

	private void determineFindDate(String content) {
		String[] contents = content.split(" ", 2);
		determineCriteria(contents[0].trim());
		if (_fieldCriteria == null) {
			return;
		}
		if (contents.length == 2) {
			if (_fieldCriteria == FieldCriteria.BETWEEN) {
				determineDateRange(contents[1].trim());
			} else {
				determineDate(contents[1]);
			}
		}
	}

	private void determineDate(String content) {
		if (content.equalsIgnoreCase("clr")) {
			_fieldCriteria = FieldCriteria.CLEAR_DATE;
			return;
		}

		DateParser dateParser = new DateParser(content);
		_fieldContent = dateParser.getDateTime();
		_dateParsed = dateParser.isDateParsed();
		_timeParsed = dateParser.isTimeParsed();
		_feedback.append(dateParser.getFeedback());
	}

	private void determineDateRange(String content) {
		String fromAndTo[] = content.split("&", 2);
		if (fromAndTo.length == 1) {
			_feedback.append("Invalid date range. ");
		} else {
			DateParser fromDateParser = new DateParser(fromAndTo[0].trim());
			DateParser toDateParser = new DateParser(fromAndTo[1].trim());

			Calendar fromDate = fromDateParser.getDateTime();
			Calendar toDate = toDateParser.getDateTime();

			_feedback.append(fromDateParser.getFeedback());
			_feedback.append(toDateParser.getFeedback());

			if (fromDate != null && toDate != null) {
				Calendar[] dateRange = { fromDateParser.getDateTime(),
						toDateParser.getDateTime() };
				_fieldContent = dateRange;
			}
		}
	}

	private void determineCriteria(String content) {
		if (content.equalsIgnoreCase("a")
				&& (_fieldType == FieldType.DUE_DATE
						|| _fieldType == FieldType.START_DATE
						|| _fieldType == FieldType.DURATION || _fieldType == FieldType.PRIORITY)) {

			_fieldCriteria = FieldCriteria.ASCEND;

		} else if (content.equalsIgnoreCase("d")
				&& (_fieldType == FieldType.DUE_DATE
						|| _fieldType == FieldType.START_DATE
						|| _fieldType == FieldType.DURATION || _fieldType == FieldType.PRIORITY)) {

			_fieldCriteria = FieldCriteria.DESCEND;

		} else if (content.equalsIgnoreCase("bf")
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.BEFORE;

		} else if (content.equalsIgnoreCase("af")
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.AFTER;

		} else if (content.equalsIgnoreCase("on")
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.ON;

		} else if (content.equalsIgnoreCase("btw")
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.BETWEEN;

		} else if (content.equalsIgnoreCase("y")
				&& (_fieldType == FieldType.OVERDUE || _fieldType == FieldType.COMPLETED)) {

			_fieldCriteria = FieldCriteria.YES;

		} else if (content.equalsIgnoreCase("n")
				&& (_fieldType == FieldType.OVERDUE || _fieldType == FieldType.COMPLETED)) {

			_fieldCriteria = FieldCriteria.NO;

		} else {
			_feedback.append("Invalid field criteria \"" + content + "\". ");
		}
	}

	private void determinePriority(String content) {
		if (content.equalsIgnoreCase("l")) {
			_fieldContent = "L";
		} else if (content.equalsIgnoreCase("m")) {
			_fieldContent = "M";
		} else if (content.equalsIgnoreCase("h")) {
			_fieldContent = "H";
		} else if (content.equalsIgnoreCase("clr")) {
			_fieldCriteria = FieldCriteria.CLEAR_PRIORITY;
		} else {
			_feedback.append("Invalid priority level \"" + content + "\". ");
		}
	}

	public Field getField() {
		parseField();
		if (_fieldType == null) {
			return null;
		} else {
			_field = new Field(_fieldType, _fieldContent, _fieldCriteria);
			_field.setDateParsed(_dateParsed);
			_field.setTimeParsed(_timeParsed);
			return _field;
		}
	}

	public String getFeedback() {
		return _feedback.toString();
	}

}
