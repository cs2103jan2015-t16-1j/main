package quicklyst;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

//@author A0102015H
public class CommandParser {

	private static final String COMMAND_LOGOUT_ABBREV = "LG";
	private static final String COMMAND_LOGOUT = "LOGOUT";
	private static final String COMMAND_SYNC_ABBREV = "SG";
	private static final String COMMAND_SYNC = "SYNC";
	private static final String COMMAND_COMPLETE_ABBREV = "C";
	private static final String COMMAND_COMPLETE = "COMPLETE";
	private static final String COMMAND_FIND_ABBREV = "F";
	private static final String COMMAND_FIND = "FIND";
	private static final String COMMAND_DELETE_ABBREV = "D";
	private static final String COMMAND_DELETE = "DELETE";
	private static final String COMMAND_EDIT_ABBREV = "E";
	private static final String COMMAND_EDIT = "EDIT";
	private static final String COMMAND_ADD_ABBREV = "A";
	private static final String COMMAND_ADD = "ADD";
	
	private static final String[][] CONVERSION_TABLE = { { "from", "-s" },
		{ "start", "-s" }, { "to", "-d" }, { "due", "-d" }, { "by", "-d" },
		{ "end", "-d" }, { "priority", "-p" }, { "prio", "-p" },
		{ "overdue", "-o" }, { "completed", "-c" }, { "duration", "-l" },
		{ "ascend", "a" }, { "descend", "d" }, { "high", "h" },
		{ "medium", "m" }, { "low", "l" }, { "yes", "y" }, { "no", "n" },
		{ "before", "bf" }, { "after", "af" }, { "on", "on" },
		{ "between", "btw" }, { "and", "&" }, { "today", "tdy" },
		{ "tomorrow", "tmr" }, { "monday", "mon" }, { "tuesday", "tue" },
		{ "wednesday", "wed" }, { "thursday", "thu" }, { "friday", "fri" },
		{ "saturday", "sat" }, { "sunday", "sun" }, { "clear", "clr" } };
	
	private StringBuilder _feedback;
	private String _taskName;
	private int _taskNumber;
	private ActionType _actionType;
	private LinkedList<Field> _fields;

	private Boolean _completeYesNo;
	private boolean _findAll;

	/* Test variable */
	private String _fieldStringPrim;
	private String _fieldStringClean;

	public CommandParser(String command) {
		_feedback = new StringBuilder();
		_fields = new LinkedList<Field>();
		command = command.trim();
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
			return new EditAction(_taskNumber, _fields, _taskName);
		case SORT:
			return new SortAction(_fields);
		case FIND:
			return new FindAction(_fields, _findAll, _taskName);
		case COMPLETE:
			return new CompleteAction(_taskNumber, _completeYesNo);
		case PUSH:
			return new PushAction();
		case PULL:
			return new PullAction();
		case SYNC:
			return new SyncAction();
		case LOG_OUT:
		    return new LogOutAction();
		default:
			return null;
		}
	}

	/* Test method */
	public int getTastNumber() {
		return _taskNumber;
	}

	/* Test method */
	public String getTaskName() {
		return _taskName;
	}

	/* Test method */
	public String getFieldStringPrim() {
		return _fieldStringPrim;
	}

	/* Test method */
	public boolean getFindAll() {
		return _findAll;
	}

	/* Test method */
	public boolean getCompleteYesNo() {
		return _completeYesNo;
	}

	/* Test method */
	public String getFieldStringClean() {
		return _fieldStringClean;
	}
	
	/* Test method */
	public LinkedList<Field> getFields() {
		return _fields;
	}

	private void processCmdString(String cmdString) {

		if (cmdString.trim().equals("")) {
			_feedback.append("Please enter a command. ");
			return;
		}

		String[] actionAndContents = cmdString.split(" ", 2);

		determineActionType(actionAndContents[0].trim());

		if (_actionType == null) {
			return;
		}

		switch (_actionType) {
		case ADD:

			if (actionAndContents.length == 1) {
				_feedback.append("No task name detected. ");
				return;
			}
			
			extractTaskName(actionAndContents[1]);
			
			if (_taskName == null) {
				_feedback.append("No task name detected. ");
				return;
			}
			
			actionAndContents[1] = actionAndContents[1]
					.substring(actionAndContents[1].indexOf(92))
					.replaceFirst(Pattern.quote("\\"), "").trim();
			
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

			if (_actionType == ActionType.EDIT) {
				String commandWithNoName = extractFindEditName(actionAndContents[1]);
				if (commandWithNoName != null) {
					actionAndContents[1] = commandWithNoName;
				}
			}
			break;

		case FIND:

			if (actionAndContents.length == 1) {
				return;
			}
			String commandWithNoName = extractFindEditName(actionAndContents[1]);
			if (commandWithNoName != null) {
				actionAndContents[1] = commandWithNoName;
			}
			break;

		case PUSH:
		case PULL:
		case SYNC:
		case LOG_OUT:

			return;

		default:
			break;
		}

		if (actionAndContents.length == 1
				|| actionAndContents[1].trim().isEmpty()) {
			return;
		}

		determineActionDetails(convertToPrim(actionAndContents[1]).trim());
	}

	private String convertToPrim(String cmdString) {
		for (String[] conversion : CONVERSION_TABLE) {
			String natForm = conversion[0];
			String primForm = conversion[1];
			cmdString = cmdString.replaceAll("\\b" + "(?i)" + natForm + "\\b",
					primForm);
		}

		// for testing purpose;
		_fieldStringPrim = cmdString;

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
					.append("Please denote end of task name with the \"\\\" character. Unexpected error may occur. ");
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

	private String extractFindEditName(String content) {
		content = content.replaceFirst("\\b(?i)name\\b", "-n");
		int initLength = content.length();
		content = content.replaceFirst("-n", "\\\\");
		int endLength = content.length();
		if (initLength != endLength) {
			return extractTaskNameWithBackSlash(content);
		} else {
			return null;
		}

	}

	private String extractTaskNameWithBackSlash(String fieldsString) {
		int quoteStart = -1, quoteEnd = -1;
		int i, j;
		for (i = 0; i < fieldsString.length(); i++) {
			if (fieldsString.charAt(i) == 92) {
				quoteStart = i;
				break;
			}
		}

		for (j = fieldsString.length() - 1; j >= 0 && j > i; j--) {
			if (fieldsString.charAt(j) == 92) {
				quoteEnd = j;
				break;
			}
		}

		if (quoteStart != -1 && quoteEnd == -1) {
			_feedback
					.append("Please denote end of task name with the \"\\\" character. Unexpected error may occur. ");
			return null;
		} else if (quoteStart != -1 && quoteEnd != -1) {

			_taskName = fieldsString.substring(quoteStart + 1, quoteEnd).trim();

			if (_taskName.isEmpty()) {
				_feedback.append("Task name is empty. ");
				_taskName = null;
			}

			String front = fieldsString.substring(0, quoteStart).trim();
			String back;
			if (quoteEnd == fieldsString.length() - 1) {
				back = "";
			} else {
				back = fieldsString.substring(quoteEnd + 1).trim();
			}
			String contentWithoutName = front + " " + back;
			return contentWithoutName;
		} else {
			return null;
		}
	}

	private void determineActionType(String actionString) {
		if (actionString.equalsIgnoreCase(COMMAND_ADD)
				|| actionString.equalsIgnoreCase(COMMAND_ADD_ABBREV)) {

			_actionType = ActionType.ADD;

		} else if (actionString.equalsIgnoreCase(COMMAND_EDIT)
				|| actionString.equalsIgnoreCase(COMMAND_EDIT_ABBREV)) {

			_actionType = ActionType.EDIT;

		} else if (actionString.equalsIgnoreCase(COMMAND_DELETE)
				|| actionString.equalsIgnoreCase(COMMAND_DELETE_ABBREV)) {

			_actionType = ActionType.DELETE;

		} else if (actionString.equalsIgnoreCase(COMMAND_FIND)
				|| actionString.equalsIgnoreCase(COMMAND_FIND_ABBREV)) {

			_actionType = ActionType.FIND;

		} else if (actionString.equalsIgnoreCase(COMMAND_COMPLETE)
				|| actionString.equalsIgnoreCase(COMMAND_COMPLETE_ABBREV)) {

			_actionType = ActionType.COMPLETE;

		} else if (actionString.equalsIgnoreCase("PUSH")
				|| actionString.equalsIgnoreCase("PS")) {

			_actionType = ActionType.PUSH;

		} else if (actionString.equalsIgnoreCase("PULL")
				|| actionString.equalsIgnoreCase("PL")) {

			_actionType = ActionType.PULL;

		} else if (actionString.equalsIgnoreCase(COMMAND_SYNC)
				|| actionString.equalsIgnoreCase(COMMAND_SYNC_ABBREV)) {

			_actionType = ActionType.SYNC;

		} else if (actionString.equalsIgnoreCase(COMMAND_LOGOUT)
				|| actionString.equalsIgnoreCase(COMMAND_LOGOUT_ABBREV)) {

			_actionType = ActionType.LOG_OUT;

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
				_findAll = true;
			} else {
				_findAll = false;
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

			_feedback.append(String.format(
					MessageConstants.INVALID_FIELD_FORMAT_IN,
					wrongFields.trim()));

			fieldsString = fieldsString.replaceFirst(
					Pattern.quote(wrongFields), "");
		}

		// For testing purposes
		_fieldStringClean = fieldsString;

		return fieldsString;
	}
}
