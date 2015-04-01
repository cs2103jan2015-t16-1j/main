package quicklyst;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class CommandParser {

	private StringBuilder _feedback;

	private String _taskName;
	private int _taskNumber;
	private ActionType _actionType;
	private LinkedList<Field> _fields;
	private FieldCriteria _yesNo;
	
	private boolean _dateParsed;
	private boolean _timeParsed;

	public CommandParser(String command) {
		_feedback = new StringBuilder();
		_fields = new LinkedList<Field>();
		processCmdString(command);
	}

	public StringBuilder getFeedback() {
		return _feedback;
	}

	public Action getAction() {
		if (_actionType == null) {
			return null;
		}
		switch (_actionType) {
		case ADD:
			return new AddAction(_taskName, _fields);
		case DELETE:
			return new DeleteAction(_taskNumber);
		case EDIT:
			return new EditAction(_taskNumber, _fields);
		case SORT:
			return new SortAction(_fields);
		case FIND:
			return new FindAction(_fields);
		case COMPLETE:
			return new CompleteAction(_taskNumber, _yesNo);
		default:
			return null;
		}
	}

	private void processCmdString(String cmdString) {

		if (cmdString.trim().equals("")) {
			_feedback.append("Please enter a command. ");
			return;
		}

		String[] actionAndFields = cmdString.split(" ", 2);

		String actionString = actionAndFields[0].trim();

		determineActionType(actionString);

		if (_actionType == null) {
			_feedback.append("Invalid action type. ");
			return;
		}

		String fieldsString;

		switch (_actionType) {
		case ADD:
			if (actionAndFields.length == 1) {
				_feedback.append("No task name entered. ");
				return;
			}
			extractTaskName(actionAndFields[1].trim());
			if (_taskName == null) {
				_feedback.append("No task name entered. ");
				return;
			}
			actionAndFields[1] = actionAndFields[1].trim()
					.replaceFirst(Pattern.quote(_taskName), "").trim();
			break;
		case EDIT:
		case DELETE:
		case COMPLETE:
			if (actionAndFields.length == 1) {
				return;
			}
			extractTaskNumber(actionAndFields[1].trim());
			actionAndFields[1] = actionAndFields[1].trim()
					.replaceFirst(String.valueOf(_taskNumber), "").trim();
			break;
		default:
			break;
		}

		if (actionAndFields.length == 1 || actionAndFields[1].trim().isEmpty()) {
			/*
			 * unnecessary _feedback.append("No fields entered. ");
			 */
			return;
		}

		fieldsString = actionAndFields[1].trim();
		
		if (fieldsString.charAt(0) != '-' && fieldsString.charAt(0) != ' '
				&& !fieldsString.equalsIgnoreCase("all")
				&& !fieldsString.equalsIgnoreCase("y")
				&& !fieldsString.equalsIgnoreCase("n")) {
			int indexDash = fieldsString.indexOf('-');
			String wrongFields;
			if (indexDash != -1) {
				wrongFields = fieldsString.substring(0,
						fieldsString.indexOf('-'));
			} else {
				wrongFields = fieldsString;
			}
			_feedback.append("Invalid field format in \"" + wrongFields
					+ "\". ");
			fieldsString = fieldsString.replaceFirst(wrongFields, "");
		}

		fieldsString = " " + fieldsString;

		determineFieldsPrim(fieldsString);
	}

	private void extractTaskNumber(String fieldsString) {
		String[] numberAndFields = fieldsString.split(" ", 2);
		String taskNumString = numberAndFields[0].trim();
		if (taskNumString.isEmpty() || taskNumString.charAt(0) == '-'
				|| taskNumString.charAt(0) == '0') {
			_taskNumber = 0;
			return;
		}
		try {
			_taskNumber = Integer.parseInt(taskNumString);
		} catch (NumberFormatException e) {
			_taskNumber = 0;
		}
	}

	private void extractTaskName(String fieldsString) {
		int indexOfDash = fieldsString.indexOf(" -");
		String taskName;
		if (indexOfDash == -1) {
			taskName = fieldsString;
		} else {
			taskName = fieldsString.substring(0, indexOfDash).trim();
		}
		if (taskName.equals("") || taskName == null) {
			return;
		}
		_taskName = taskName;
	}

	private void determineActionType(String actionString) {
		if (actionString.equalsIgnoreCase("ADD")
				|| actionString.equalsIgnoreCase("A")) {

			_actionType = ActionType.ADD;

		} else if (actionString.equalsIgnoreCase("EDIT")
				|| actionString.equalsIgnoreCase("E")) {

			_actionType = ActionType.EDIT;

		} else if (actionString.equalsIgnoreCase("DELETE")
				|| actionString.equalsIgnoreCase("DEL")
				|| actionString.equalsIgnoreCase("D")) {

			_actionType = ActionType.DELETE;

		} else if (actionString.equalsIgnoreCase("FIND")
				|| actionString.equalsIgnoreCase("F")) {

			_actionType = ActionType.FIND;

		} else if (actionString.equalsIgnoreCase("SORT")
				|| actionString.equalsIgnoreCase("S")) {

			_actionType = ActionType.SORT;

		} else if (actionString.equalsIgnoreCase("COMPLETE")
				|| actionString.equalsIgnoreCase("C")) {

			_actionType = ActionType.COMPLETE;
		} else {
			return;
		}
	}

	private void determineFieldsPrim(String fieldsString) {

		if (fieldsString.trim().equalsIgnoreCase("all")) {
			_fields.add(new Field(FieldType.ALL));
			return;
		}

		if (fieldsString.trim().equalsIgnoreCase("y")) {
			_yesNo = FieldCriteria.YES;
			return;
		}

		if (fieldsString.trim().equalsIgnoreCase("n")) {
			_yesNo = FieldCriteria.NO;
			return;
		}

		String[] fieldStringArray = fieldsString.split(" -");

		for (String fieldString : fieldStringArray) {
			fieldString = fieldString.trim();
			if (!fieldString.equals("")) {
				Field field = parseField(fieldString);
				if (field != null) {
					_fields.add(field);
				}
			}
		}
	}

	private Field parseField(String fieldString) {
		/* Assertion */
		assert !fieldString.equals("");

		// empty field will not be added to field list
		if (fieldString.length() == 1) {
			return null;
		}

		char fieldTypeChar = fieldString.charAt(0);
		char spaceAftFieldType = fieldString.charAt(1);

		if (spaceAftFieldType != ' ') {
			_feedback.append("Invalid field type \""
					+ fieldString.split(" ", 2)[0].trim() + "\". ");
			return null;
		}

		String fieldContentString = fieldString.substring(1).trim();

		if (fieldContentString.equals("")) {
			return null;
		}

		FieldType fieldType = null;
		Object fieldContent = null;
		FieldCriteria fieldCriteria = null;

		switch (fieldTypeChar) {
		case 'd':
		case 's':
		case 'r':

			switch (fieldTypeChar) {
			case 'd':
				fieldType = FieldType.DUE_DATE;
				break;
			case 's':
				fieldType = FieldType.START_DATE;
				break;
			case 'r':
				fieldType = FieldType.REMINDER;
				break;
			default:
				fieldType = null;
				break;
			}

			if (_actionType == ActionType.FIND) {
				String criteriaAndDate[] = fieldContentString.split(" ", 2);

				String dateString;
				String criteriaString;

				if (criteriaAndDate.length == 2) {
					dateString = criteriaAndDate[1].trim();
					criteriaString = criteriaAndDate[0].trim();
					fieldCriteria = determineFieldCriteria(criteriaString);
				} else {
					dateString = criteriaAndDate[0].trim();
				}

				if (fieldCriteria == FieldCriteria.BETWEEN) {
					String fromAndTo[] = dateString.split("&", 2);
					if (fromAndTo.length == 1) {
						_feedback.append("Date range not valid");
					} else {

						DateParser fromDateParser = new DateParser(
								fromAndTo[0].trim());
						DateParser toDateParser = new DateParser(
								fromAndTo[1].trim());
						Calendar fromDate = fromDateParser.getDateTime();
						Calendar toDate = toDateParser.getDateTime();

						_feedback.append(fromDateParser.getFeedback());
						_feedback.append(toDateParser.getFeedback());

						if (fromDate != null && toDate != null) {
							Calendar[] dateRange = {
									fromDateParser.getDateTime(),
									toDateParser.getDateTime() };
							fieldContent = dateRange;
						}
					}
				}
				break;
			}
				
			String dateString = fieldContentString;

			if (dateString.equalsIgnoreCase("clr")) {
				fieldCriteria = FieldCriteria.CLEAR_DATE;
			} else if (dateString.equalsIgnoreCase("a")
					|| dateString.equalsIgnoreCase("d")) {
				fieldCriteria = determineFieldCriteria(dateString);
			} else {
				DateParser dateParser = new DateParser(dateString);
				Calendar dateTime = dateParser.getDateTime();
				_dateParsed = dateParser.isDateParsed();
				_timeParsed = dateParser.isTimeParsed();
				_feedback.append(dateParser.getFeedback());
				if (dateTime != null) {
					fieldContent = dateTime;
				}
			}
			break;

		case 'l':

			fieldType = FieldType.DURATION;
			fieldCriteria = determineFieldCriteria(fieldContentString);
			break;

		case 'p':

			fieldType = FieldType.PRIORITY;

			if (_actionType == ActionType.SORT) {
				fieldCriteria = determineFieldCriteria(fieldContentString);
			}
			if (_actionType == ActionType.ADD || _actionType == ActionType.FIND
					|| _actionType == ActionType.EDIT) {
				fieldContent = determinePriority(fieldContentString);
			}
			break;

		case 'n':

			fieldType = FieldType.TASK_NAME;
			fieldContent = fieldContentString;
			break;

		case 'c':

			fieldType = FieldType.COMPLETED;
			fieldCriteria = determineFieldCriteria(fieldContentString);
			break;

		case 'o':

			fieldType = FieldType.OVERDUE;
			fieldCriteria = determineFieldCriteria(fieldContentString);
			break;

		default:

			_feedback.append("Invalid field type \"" + fieldTypeChar + "\". ");
			// null type will not be added to field list
			return null;
		}

		// field always has valid field type
		Field field = new Field(fieldType, fieldContent, fieldCriteria);
		field.setDateParsed(_dateParsed);
		field.setTimeParsed(_timeParsed);
		return field;
	}

	private String determinePriority(String fieldContentString) {
		if (fieldContentString.equalsIgnoreCase("l")) {
			return "L";
		} else if (fieldContentString.equalsIgnoreCase("m")) {
			return "M";
		} else if (fieldContentString.equalsIgnoreCase("H")) {
			return "H";
		} else if (fieldContentString.equalsIgnoreCase("clr")) {
			return "CLR";
		} else {
			_feedback.append("Invalid priority level \"" + fieldContentString
					+ "\". ");
			return null;
		}
	}

	private FieldCriteria determineFieldCriteria(String criteriaString) {
		if (criteriaString.equalsIgnoreCase("a")) {
			return FieldCriteria.ASCEND;
		} else if (criteriaString.equalsIgnoreCase("d")) {
			return FieldCriteria.DESCEND;
		} else if (criteriaString.equalsIgnoreCase("bf")) {
			return FieldCriteria.BEFORE;
		} else if (criteriaString.equalsIgnoreCase("af")) {
			return FieldCriteria.AFTER;
		} else if (criteriaString.equalsIgnoreCase("on")) {
			return FieldCriteria.ON;
		} else if (criteriaString.equalsIgnoreCase("btw")) {
			return FieldCriteria.BETWEEN;
		} else if (criteriaString.equalsIgnoreCase("y")) {
			return FieldCriteria.YES;
		} else if (criteriaString.equalsIgnoreCase("n")) {
			return FieldCriteria.NO;
		} else {
			_feedback.append("Invalid criteria \"" + criteriaString + "\". ");
			return null;
		}
	}

	public static void main(String args[]) {
	}
}
