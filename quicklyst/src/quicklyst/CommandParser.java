package quicklyst;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandParser {

	private StringBuilder _feedback;

	private String _taskName;
	private String _userName;
	private int _taskNumber;
	private ActionType _actionType;
	private LinkedList<Field> _fields;

	private Boolean _completeYesNo;
	private boolean _all;

	private static final String[][] CONVERSION_TABLE = { { "name", "-n" },
			{ "from", "-s" }, { "start", "-s" }, { "to", "-d" },
			{ "due", "-d" }, { "by", "-d" }, { "end", "-d" },
			{ "priority", "-p" }, { "prio", "-p" }, { "remind", "-r" },
			{ "overdue", "-o" }, { "completed", "-c" }, { "duration", "-l" },
			{ "ascend", "a" }, { "descend", "d" }, { "high", "h" },
			{ "medium", "m" }, { "low", "l" }, { "yes", "y" }, { "no", "n" },
			{ "before", "bf" }, { "after", "af" }, { "on", "on" },
			{ "between", "btw" }, { "and", "&" }, { "today", "tdy" },
			{ "tomorrow", "tmr" } };

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
			return new FindAction(_fields, _all);
		case COMPLETE:
			return new CompleteAction(_taskNumber, _completeYesNo);
		case PUSH:
			return new PushAction();
		case PULL:
			return new PullAction();
		default:
			return null;
		}
	}

	public LinkedList<Field> getFields() {
		return _fields;
	}

	private void processCmdString(String cmdString) {

		if (cmdString.trim().equals("")) {
			_feedback.append("Please enter a command. ");
			return;
		}

		String[] actionAndContents = cmdString.split(" ", 2);

		String actionString = actionAndContents[0].trim();
		determineActionType(actionString);
		if (_actionType == null) {
			return;
		}

		switch (_actionType) {
		case ADD:
			if (actionAndContents.length == 1) {
				_feedback.append("No task name deteced. ");
				return;
			}
			System.out.println(actionAndContents[1]);
			extractTaskName(actionAndContents[1]);
			if (_taskName == null) {
				_feedback.append("No task name deteced. ");
				return;
			}
			actionAndContents[1] = actionAndContents[1]
					.substring(actionAndContents[1].indexOf(92))
					.replaceFirst(Pattern.quote("\\"), "").trim();
			System.out.println(actionAndContents[1]);
			break;
		case EDIT:
		case DELETE:
		case COMPLETE:
			if (actionAndContents.length == 1) {
				return;
			}
			extractTaskNumber(actionAndContents[1].trim());

			actionAndContents[1] = actionAndContents[1].trim()
					.replaceFirst(String.valueOf(_taskNumber), "").trim();
			
			break;
		default:
			break;
		}

		if (actionAndContents.length == 1
				|| actionAndContents[1].trim().isEmpty()) {
			return;
		}

		String fieldsString = convertToPrim(actionAndContents[1]).trim();
		System.out.println(fieldsString);
		determineActionDetails(fieldsString);
	}

	private String extractFindEditName(String content) {
		content = content.replaceFirst(
				"\\b(?i)name\\b", "-n");
		content = content.replaceFirst(
				"-n ", "\\\\ ");
		int start = -1;
		int end = -1;
		for(int i = 0; i < content.length(); i++) {
			if(content.charAt(i) == 92 && start == -1) {
				start = i;
			} else if(content.charAt(i) == 92 && start != -1) {
				end = i;
				break;
			}
		}
		if(start != -1 && end == -1) {
			_feedback.append("Please denote end of task name with the \"\\\" character. ");
			return content.trim();
		} else if (start != -1 && end != -1) {
			_taskName = content.substring(start + 1, end).trim();
			content = content.replaceAll("\\\\", "");
			/*
			String front = content.substring(0, start);
			String back = content.substring(end);
			content = front + " " + back;
			*/
			content = content.replaceFirst(Pattern.quote(_taskName), "");
			return content.trim();
		} else {
			System.out.println("nothing");
			return content.trim();
		}
	}

	private String convertToPrim(String cmdString) {
		for (String[] conversion : CONVERSION_TABLE) {
			String natForm = conversion[0];
			String primForm = conversion[1];
			cmdString = cmdString.replaceFirst(
					"\\b" + "(?i)" + natForm + "\\b", primForm);
		}
		return cmdString;
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
		// check for '\' character - ascii 92
		int stopIndex = fieldsString.indexOf(92);
		if (stopIndex == -1) {
			_feedback
					.append("Please denote end of task name with the \"\\\" character");
			return;
		} else if (stopIndex == 0) {
			_feedback.append("Task name entered is blank. ");
			return;
		} else {
			String taskName = fieldsString.substring(0, stopIndex).trim();
			if (taskName.isEmpty()) {
				_feedback.append("Task name entered is blank. ");
			} else {
				_taskName = taskName;
			}
		}
	}

	private void extractTaskNameWithQuotes(String fieldsString) {
		int quoteStart = -1, quoteEnd = -1;
		int i, j;
		for (i = 0; i < fieldsString.length(); i++) {
			if (fieldsString.charAt(i) == 34) {
				quoteStart = i;
				break;
			}
		}

		for (j = fieldsString.length() - 1; j >= 0 && j > i; j--) {
			if (fieldsString.charAt(j) == 34) {
				quoteEnd = j;
				break;
			}
		}

		System.out.println(quoteStart + " " + quoteEnd);

		if (quoteStart == -1 || quoteEnd == -1) {
			_feedback.append("Please enclose task name with \" \". ");
			return;
		} else {
			_taskName = fieldsString.substring(quoteStart + 1, quoteEnd);
		}
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

		}

		/* sort function removed */
		/*
		 * else if (actionString.equalsIgnoreCase("SORT") ||
		 * actionString.equalsIgnoreCase("S")) {
		 * 
		 * _actionType = ActionType.SORT;
		 * 
		 * }
		 */

		else if (actionString.equalsIgnoreCase("COMPLETE")
				|| actionString.equalsIgnoreCase("C")) {

			_actionType = ActionType.COMPLETE;

		} else if (actionString.equalsIgnoreCase("PUSH")
				|| actionString.equalsIgnoreCase("PS")) {

			_actionType = ActionType.PUSH;

		} else if (actionString.equalsIgnoreCase("PULL")
				|| actionString.equalsIgnoreCase("PL")) {

			_actionType = ActionType.PULL;

		} else {
			_feedback.append("Invalid action type. ");
			return;
		}
	}

	private void determineActionDetails(String fieldsString) {

		determineNonFieldInputs(fieldsString);
		fieldsString = removeWrongInputs(fieldsString);
		determineFields(fieldsString);
	}

	private void determineFields(String fieldsString) {
		if (fieldsString.equalsIgnoreCase("all")
				|| fieldsString.equalsIgnoreCase("y")
				|| fieldsString.equalsIgnoreCase("n")
				|| _actionType == ActionType.PUSH
				|| _actionType == ActionType.PULL) {
			return;
		}

		String[] fieldStringArray = fieldsString.split("-");

		for (String fieldString : fieldStringArray) {
			fieldString = fieldString.trim();
			if (!fieldString.isEmpty()) {
				FieldParser fp = new FieldParser(fieldString);
				fp.setActionType(_actionType);
				Field field = fp.getField();
				_feedback.append(fp.getFeedback());
				if (field != null) {
					_fields.add(field);
				}
			}
		}
	}

	private void determineNonFieldInputs(String fieldsString) {
		switch (_actionType) {
		case FIND:
		case DELETE:
			if (fieldsString.trim().equalsIgnoreCase("all")) {
				_all = true;
			} else {
				_all = false;
			}
			break;

		case COMPLETE:
			if (fieldsString.trim().equalsIgnoreCase("y")) {
				_completeYesNo = true;
			} else if (fieldsString.trim().equalsIgnoreCase("n")) {
				_completeYesNo = false;
			} else {
				_completeYesNo = null;
			}
			break;

		default:
			break;
		}
	}

	private String removeWrongInputs(String fieldsString) {
		if (fieldsString.charAt(0) != '-'
				&& !fieldsString.equalsIgnoreCase("all")
				&& !fieldsString.equalsIgnoreCase("y")
				&& !fieldsString.equalsIgnoreCase("n")
				&& _actionType != ActionType.PUSH
				&& _actionType != ActionType.PULL) {
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
			fieldsString = fieldsString.replaceFirst(
					Pattern.quote(wrongFields), "");
		}
		return fieldsString;
	}

	/*
	 * old implementation private Field parseField(String fieldString) {
	 * 
	 * assert !fieldString.equals("");
	 * 
	 * // empty field will not be added to field list if (fieldString.length()
	 * == 1) { return null; }
	 * 
	 * char fieldTypeChar = fieldString.charAt(0); char spaceAftFieldType =
	 * fieldString.charAt(1);
	 * 
	 * if (spaceAftFieldType != ' ') { _feedback.append("Invalid field type \""
	 * + fieldString.split(" ", 2)[0].trim() + "\". "); return null; }
	 * 
	 * String fieldContentString = fieldString.substring(1).trim();
	 * 
	 * if (fieldContentString.equals("")) { return null; }
	 * 
	 * FieldType fieldType = null; Object fieldContent = null; FieldCriteria
	 * fieldCriteria = null;
	 * 
	 * switch (fieldTypeChar) { case 'd': case 's': case 'r':
	 * 
	 * switch (fieldTypeChar) { case 'd': fieldType = FieldType.DUE_DATE; break;
	 * case 's': fieldType = FieldType.START_DATE; break; case 'r': fieldType =
	 * FieldType.REMINDER; break; default: fieldType = null; break; }
	 * 
	 * if (_actionType == ActionType.FIND) { String criteriaAndDate[] =
	 * fieldContentString.split(" ", 2);
	 * 
	 * String dateString; String criteriaString;
	 * 
	 * if (criteriaAndDate.length == 2) { dateString =
	 * criteriaAndDate[1].trim(); criteriaString = criteriaAndDate[0].trim();
	 * fieldCriteria = determineFieldCriteria(criteriaString); } else {
	 * dateString = criteriaAndDate[0].trim(); }
	 * 
	 * DateParser dateParser = new DateParser(dateString); fieldContent =
	 * dateParser.getDateTime(); _feedback.append(dateParser.getFeedback());
	 * 
	 * if (fieldCriteria == FieldCriteria.BETWEEN) {
	 * System.out.println(dateString); String fromAndTo[] =
	 * dateString.split("&", 2); if (fromAndTo.length == 1) {
	 * _feedback.append("Date range not valid"); fieldContent = null; } else {
	 * System.out.println(fromAndTo[0].trim());
	 * System.out.println(fromAndTo[1].trim()); DateParser fromDateParser = new
	 * DateParser( fromAndTo[0].trim()); DateParser toDateParser = new
	 * DateParser( fromAndTo[1].trim()); Calendar fromDate =
	 * fromDateParser.getDateTime(); Calendar toDate =
	 * toDateParser.getDateTime();
	 * _feedback.append(fromDateParser.getFeedback());
	 * _feedback.append(toDateParser.getFeedback());
	 * 
	 * if (fromDate != null && toDate != null) { Calendar[] dateRange = {
	 * fromDateParser.getDateTime(), toDateParser.getDateTime() };
	 * System.out.println(dateRange[0].getTime());
	 * System.out.println(dateRange[1].getTime()); fieldContent = dateRange; } }
	 * } break; } else { String dateString = fieldContentString; if
	 * (dateString.equalsIgnoreCase("clr")) { fieldCriteria =
	 * FieldCriteria.CLEAR_DATE; } else if (dateString.equalsIgnoreCase("a") ||
	 * dateString.equalsIgnoreCase("d")) { fieldCriteria =
	 * determineFieldCriteria(dateString); } else { DateParser dateParser = new
	 * DateParser(dateString); fieldContent = dateParser.getDateTime();
	 * _dateParsed = dateParser.isDateParsed(); _timeParsed =
	 * dateParser.isTimeParsed(); _feedback.append(dateParser.getFeedback()); }
	 * break; }
	 * 
	 * case 'l':
	 * 
	 * fieldType = FieldType.DURATION; fieldCriteria =
	 * determineFieldCriteria(fieldContentString); break;
	 * 
	 * case 'p':
	 * 
	 * fieldType = FieldType.PRIORITY;
	 * 
	 * if (_actionType == ActionType.SORT) { fieldCriteria =
	 * determineFieldCriteria(fieldContentString); } if (_actionType ==
	 * ActionType.ADD || _actionType == ActionType.FIND || _actionType ==
	 * ActionType.EDIT) { fieldContent = determinePriority(fieldContentString);
	 * } break;
	 * 
	 * case 'n':
	 * 
	 * fieldType = FieldType.TASK_NAME; fieldContent = fieldContentString;
	 * break;
	 * 
	 * case 'c':
	 * 
	 * fieldType = FieldType.COMPLETED; fieldCriteria =
	 * determineFieldCriteria(fieldContentString); break;
	 * 
	 * case 'o':
	 * 
	 * fieldType = FieldType.OVERDUE; fieldCriteria =
	 * determineFieldCriteria(fieldContentString); break;
	 * 
	 * default:
	 * 
	 * _feedback.append("Invalid field type \"" + fieldTypeChar + "\". "); //
	 * null type will not be added to field list return null; }
	 * 
	 * // field always has valid field type Field field = new Field(fieldType,
	 * fieldContent, fieldCriteria); field.setDateParsed(_dateParsed);
	 * field.setTimeParsed(_timeParsed); return field; }
	 * 
	 * private String determinePriority(String fieldContentString) { if
	 * (fieldContentString.equalsIgnoreCase("l")) { return "L"; } else if
	 * (fieldContentString.equalsIgnoreCase("m")) { return "M"; } else if
	 * (fieldContentString.equalsIgnoreCase("H")) { return "H"; } else if
	 * (fieldContentString.equalsIgnoreCase("clr")) { return "CLR"; } else {
	 * _feedback.append("Invalid priority level \"" + fieldContentString +
	 * "\". "); return null; } }
	 * 
	 * 
	 * 
	 * private FieldCriteria determineFieldCriteria(String criteriaString) { if
	 * (criteriaString.equalsIgnoreCase("a")) { return FieldCriteria.ASCEND; }
	 * else if (criteriaString.equalsIgnoreCase("d")) { return
	 * FieldCriteria.DESCEND; } else if (criteriaString.equalsIgnoreCase("bf"))
	 * { return FieldCriteria.BEFORE; } else if
	 * (criteriaString.equalsIgnoreCase("af")) { return FieldCriteria.AFTER; }
	 * else if (criteriaString.equalsIgnoreCase("on")) { return
	 * FieldCriteria.ON; } else if (criteriaString.equalsIgnoreCase("btw")) {
	 * return FieldCriteria.BETWEEN; } else if
	 * (criteriaString.equalsIgnoreCase("y")) { return FieldCriteria.YES; } else
	 * if (criteriaString.equalsIgnoreCase("n")) { return FieldCriteria.NO; }
	 * else { _feedback.append("Invalid criteria \"" + criteriaString + "\". ");
	 * return null; } }
	 */

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);

		while (true) {
			String cmd = sc.nextLine();
			CommandParser cp = new CommandParser(cmd);
			System.out.println("feedback: " + cp.getFeedback());
			for (Field field : cp.getFields()) {
				System.out.println(field);
			}

		}
	}
}
