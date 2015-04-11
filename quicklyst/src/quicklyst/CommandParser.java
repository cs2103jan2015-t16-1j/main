package quicklyst;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

//@author A0102015H
public class CommandParser {

	private static final String STRING_SPACE = " ";
	private static final String STRING_DASH = "-";
	private static final char CHAR_DASH = '-';
	private static final String STRING_EMPTY = "";
	private static final String PRIM_NAME = "-n";
	private static final String STRING_NAME = "name";
	private static final String STRING_NO = "no";
	private static final String STRING_YES = "yes";
	private static final String STRING_N = "n";
	private static final String STRING_Y = "y";
	private static final String STRING_ALL = "all";
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

	private static final char IDENTIFIER_CHAR_BACKSLASH = '\\';
	private static final String IDENTIFIER_BACKSLASH = "\\\\";

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
	private boolean _noTaskName;

	/* Test variable */
	private String _fieldStringPrim;
	private String _fieldStringClean;

	public CommandParser(String command) {
		_feedback = new StringBuilder();
		_fields = new LinkedList<Field>();
		_noTaskName = false;
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
	public int getTaskNumber() {
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

		if (cmdString.trim().equals(STRING_EMPTY)) {
			_feedback.append(MessageConstants.NO_COMMAND);
			return;
		}

		String[] actionAndContents = cmdString.split(STRING_SPACE, 2);

		determineActionType(actionAndContents[0].trim());

		if (_actionType == null) {
			return;
		}

		if (actionAndContents.length == 1
				|| actionAndContents[1].trim().isEmpty()) {

			if (_actionType == ActionType.ADD) {
				_feedback.append(MessageConstants.NO_TASK_NAME);

			}
			return;
		}

		System.out.println(actionAndContents[1].trim());
		determineActionDetails(actionAndContents[1].trim());
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

	private String extractTaskNumber(String fieldsString) {
		String[] numberAndFields = fieldsString.split(STRING_SPACE, 2);
		String taskNumString = numberAndFields[0].trim();

		try {
			_taskNumber = Integer.parseInt(taskNumString);
			fieldsString = fieldsString.replaceFirst(
					String.valueOf(_taskNumber), STRING_EMPTY).trim();
			System.out.println("lala");
		} catch (NumberFormatException e) {
			_taskNumber = 0;
		}
		return fieldsString;
	}

	private String extractTaskName(String fieldsString) {

		int stopIndex = fieldsString.indexOf(IDENTIFIER_CHAR_BACKSLASH);
		if (stopIndex == -1) {
			_feedback.append(MessageConstants.NAME_NO_CLOSE);
		} else if (stopIndex == 0) {
			_feedback.append(MessageConstants.TASK_NAME_BLANK);
		} else {
			String taskName = fieldsString.substring(0, stopIndex).trim();
			if (taskName.isEmpty()) {
				_feedback.append(MessageConstants.TASK_NAME_BLANK);
			} else {
				_taskName = taskName;
				fieldsString = fieldsString
						.substring(
								fieldsString.indexOf(IDENTIFIER_CHAR_BACKSLASH))
						.replaceFirst(IDENTIFIER_BACKSLASH, STRING_EMPTY)
						.trim();
			}
		}
		return fieldsString;
	}

	private String extractFindEditName(String fieldsString) {

		fieldsString = fieldsString.replaceFirst("\\b(?i)" + STRING_NAME
				+ "\\b", PRIM_NAME);
		int initLength = fieldsString.length();
		fieldsString = fieldsString.replaceFirst(PRIM_NAME,
				IDENTIFIER_BACKSLASH);
		int endLength = fieldsString.length();
		if (initLength != endLength) {
			return extractTaskNameWithBackSlash(fieldsString);
		} else {
			return fieldsString;
		}

	}

	private String extractTaskNameWithBackSlash(String fieldsString) {
		int quoteStart = -1, quoteEnd = -1;
		int i, j;
		for (i = 0; i < fieldsString.length(); i++) {
			if (fieldsString.charAt(i) == IDENTIFIER_CHAR_BACKSLASH) {
				quoteStart = i;
				break;
			}
		}

		for (j = fieldsString.length() - 1; j >= 0 && j > i; j--) {
			if (fieldsString.charAt(j) == IDENTIFIER_CHAR_BACKSLASH) {
				quoteEnd = j;
				break;
			}
		}

		if (quoteStart != -1 && quoteEnd == -1) {
			_feedback.append(MessageConstants.NAME_NO_CLOSE);

			return fieldsString.replaceFirst(IDENTIFIER_BACKSLASH, STRING_NAME);

		} else if (quoteStart != -1 && quoteEnd != -1) {

			_taskName = fieldsString.substring(quoteStart + 1, quoteEnd).trim();

			if (_taskName.isEmpty()) {
				_feedback.append(MessageConstants.NO_TASK_NAME);
				_taskName = null;
			}

			String front = fieldsString.substring(0, quoteStart).trim();
			String back;
			if (quoteEnd == fieldsString.length() - 1) {
				back = STRING_EMPTY;
			} else {
				back = fieldsString.substring(quoteEnd + 1).trim();
			}
			return front + STRING_SPACE + back;
		} else {
			return fieldsString;
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

		} else if (actionString.equalsIgnoreCase(COMMAND_SYNC)
				|| actionString.equalsIgnoreCase(COMMAND_SYNC_ABBREV)) {

			_actionType = ActionType.SYNC;

		} else if (actionString.equalsIgnoreCase(COMMAND_LOGOUT)
				|| actionString.equalsIgnoreCase(COMMAND_LOGOUT_ABBREV)) {

			_actionType = ActionType.LOG_OUT;

		} else {
			_feedback.append(MessageConstants.INVALID_ACTION_TYPE);
			return;
		}
	}

	private void determineActionDetails(String fieldsString) {

		fieldsString = determineNonFieldInputs(fieldsString).trim();
		if (_actionType == ActionType.COMPLETE
				|| _actionType == ActionType.DELETE
				|| _actionType == ActionType.SYNC
				|| _actionType == ActionType.LOG_OUT || _noTaskName || _findAll) {
			return;
		} else {
			fieldsString = convertToPrim(fieldsString).trim();
			fieldsString = removeWrongInputs(fieldsString).trim();
			determineFields(fieldsString);
		}
	}

	private void determineFields(String fieldsString) {

		String[] fieldStringArray = fieldsString.split(STRING_DASH);

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

	private String determineNonFieldInputs(String fieldsString) {

		switch (_actionType) {
		case ADD:

			fieldsString = extractTaskName(fieldsString);

			if (_taskName == null) {
				_feedback.append(MessageConstants.NO_TASK_NAME);
				_noTaskName = true;
			} else {
				_noTaskName = false;
			}

			break;

		case EDIT:
		case DELETE:
		case COMPLETE:

			fieldsString = extractTaskNumber(fieldsString);

			if (_actionType == ActionType.EDIT && _taskNumber > 0) {
				fieldsString = extractFindEditName(fieldsString);
			}

			if (_actionType == ActionType.COMPLETE && _taskNumber > 0) {
				if (fieldsString.trim().equalsIgnoreCase(STRING_Y)
						|| fieldsString.trim().equalsIgnoreCase(STRING_YES)) {
					_completeYesNo = true;
				} else if (fieldsString.trim().equalsIgnoreCase(STRING_N)
						|| fieldsString.trim().equalsIgnoreCase(STRING_NO)) {
					_completeYesNo = false;
				} else {
					_completeYesNo = null;
				}
			}

			break;

		case FIND:

			if (fieldsString.trim().equalsIgnoreCase(STRING_ALL)) {
				_findAll = true;
			} else {
				_findAll = false;
			}

			fieldsString = extractFindEditName(fieldsString);
			break;

		default:
			break;
		}

		return fieldsString;
	}

	private String removeWrongInputs(String fieldsString) {

		if (fieldsString.isEmpty()) {
			return fieldsString;
		}

		if (fieldsString.charAt(0) != CHAR_DASH
				&& !fieldsString.equalsIgnoreCase(STRING_ALL)
				&& !fieldsString.equalsIgnoreCase(STRING_Y)
				&& !fieldsString.equalsIgnoreCase(STRING_N)) {

			int indexDash = fieldsString.indexOf(CHAR_DASH);
			String wrongFields;

			if (indexDash != -1) {
				wrongFields = fieldsString.substring(0,
						fieldsString.indexOf(CHAR_DASH));
			} else {
				wrongFields = fieldsString;
			}

			_feedback.append(String.format(
					MessageConstants.INVALID_FIELD_FORMAT_IN,
					wrongFields.trim()));

			fieldsString = fieldsString.replaceFirst(
					Pattern.quote(wrongFields), STRING_EMPTY);
		}

		// For testing purposes
		_fieldStringClean = fieldsString;

		return fieldsString;
	}
}
