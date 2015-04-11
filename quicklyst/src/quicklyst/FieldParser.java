package quicklyst;

import java.util.Calendar;

//@author A0102015H
public class FieldParser {

	private static final String PRIM_NO = "n";
	private static final String PRIM_YES = "y";
	private static final String PRIM_BETWEEN = "btw";
	private static final String PRIM_ON = "on";
	private static final String PRIM_AFTER = "af";
	private static final String PRIM_BEFORE = "bf";
	private static final String PRIM_DESCEND = "d";
	private static final String PRIM_ASCEND = "a";
	private static final String STRING_H = "H";
	private static final String STRING_M = "M";
	private static final String STRING_L = "L";
	private static final String PRIM_AND = "&";
	private static final String PRIM_CLEAR = "clr";
	private static final String SPACE = " ";

	private StringBuilder _feedback;
	
	private String _fieldString;

	private ActionType _actionType;
	private FieldType _fieldType;
	private Object _fieldContent;
	private FieldCriteria _fieldCriteria;
	
	private boolean _dateParsed;
	private boolean _timeParsed;

	private Field _field;

	public FieldParser(String fieldString) {
		
		assert !fieldString.isEmpty();
	
		_feedback = new StringBuilder();
		_fieldString = fieldString;
	}

	public void setActionType(ActionType a) {
		_actionType = a;
	}

	private void parseField() {

		String[] typeAndContent = _fieldString.split(SPACE, 2);

		determineType(typeAndContent[0].trim());

		if (typeAndContent.length == 2 && _fieldType != null) {
			determineContent(typeAndContent[1].trim());
		}
	}

	private void determineType(String type) {

		if (type.length() != 1) {

			_feedback.append(String
					.format(MessageConstants.INVALID_FIELD, type));
			return;

		} else {

			switch (type.charAt(0)) {
			case 'd':
				_fieldType = FieldType.DUE_DATE;
				break;
			case 's':
				_fieldType = FieldType.START_DATE;
				break;
			case 'p':
				_fieldType = FieldType.PRIORITY;
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

		} else if (_fieldType == FieldType.DUE_DATE
				|| _fieldType == FieldType.START_DATE) {

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
				|| _fieldType == FieldType.OVERDUE) {

			determineCriteria(content);
		}
	}

	private void determineFindDate(String content) {

		String[] contents = content.split(SPACE, 2);

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

		if (content.equalsIgnoreCase(PRIM_CLEAR)) {
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

		String fromAndTo[] = content.split(PRIM_AND, 2);

		if (fromAndTo.length == 1) {

			_feedback.append(MessageConstants.INVALID_DATE_RANGE);

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

		if (content.equalsIgnoreCase(PRIM_ASCEND)
				&& (_fieldType == FieldType.DUE_DATE
						|| _fieldType == FieldType.START_DATE || _fieldType == FieldType.PRIORITY)) {

			_fieldCriteria = FieldCriteria.ASCEND;

		} else if (content.equalsIgnoreCase(PRIM_DESCEND)
				&& (_fieldType == FieldType.DUE_DATE
						|| _fieldType == FieldType.START_DATE || _fieldType == FieldType.PRIORITY)) {

			_fieldCriteria = FieldCriteria.DESCEND;

		} else if (content.equalsIgnoreCase(PRIM_BEFORE)
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.BEFORE;

		} else if (content.equalsIgnoreCase(PRIM_AFTER)
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.AFTER;

		} else if (content.equalsIgnoreCase(PRIM_ON)
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.ON;

		} else if (content.equalsIgnoreCase(PRIM_BETWEEN)
				&& (_fieldType == FieldType.DUE_DATE || _fieldType == FieldType.START_DATE)) {

			_fieldCriteria = FieldCriteria.BETWEEN;

		} else if (content.equalsIgnoreCase(PRIM_YES)
				&& (_fieldType == FieldType.OVERDUE || _fieldType == FieldType.COMPLETED)) {

			_fieldCriteria = FieldCriteria.YES;

		} else if (content.equalsIgnoreCase(PRIM_NO)
				&& (_fieldType == FieldType.OVERDUE || _fieldType == FieldType.COMPLETED)) {

			_fieldCriteria = FieldCriteria.NO;

		} else {

			_feedback.append(String.format(
					MessageConstants.INVALID_FIELD_CRITERIA, content));
		}
	}

	private void determinePriority(String content) {

		if (content.equalsIgnoreCase(STRING_L)) {
			_fieldContent = STRING_L;
		} else if (content.equalsIgnoreCase(STRING_M)) {
			_fieldContent = STRING_M;
		} else if (content.equalsIgnoreCase(STRING_H)) {
			_fieldContent = STRING_H;
		} else if (content.equalsIgnoreCase(PRIM_CLEAR)) {
			_fieldCriteria = FieldCriteria.CLEAR_PRIORITY;
		} else {
			_feedback.append(String.format(
					MessageConstants.INVALID_PRIORITY_LEVEL, content));
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
