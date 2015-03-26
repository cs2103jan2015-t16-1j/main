package quicklyst;

import java.util.LinkedList;


public class OldCommandParser {
	
	public static final char CHAR_DASH = '-';
	public static final String MESSAGE_INVALID_YES_NO = "Invalid yes/no field. Please use Y for yes and N for no.";
	public static final String MESSAGE_NO_TASK_SATISFY_CRITERIA = "No task satisfies criteria entered.";
	public static final String MESSAGE_NO_DATE_ENTERED = "No date entered.";
	public static final String MESSAGE_INVALID_PRIORITY_LEVEL = "Invalid priority level.";
	public static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\".";
	public static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed.";
	public static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. Nothing is executed.";
	public static final String MESSAGE_TASK_NUMBER_OUT_OF_RANGE = "Task number entered out of range. Nothing is executed.";
	public static final String MESSAGE_INVALID_TASK_NUMBER = "Invalid task number entered. Nothing is executed.";
	
	public static final int INDEX_ACTION = 0;
	public static final int INDEX_FIELDS = 1;
	public static final int INDEX_FIELD_CONTENT_START = 1;
	public static final int INDEX_FIELD_TYPE = 0;
	public static final int INDEX_PRIORITY_LEVEL = 0;
	
	public static final int NUM_SPLIT_TWO = 2;
	public static final int NUM_INVALID = -1;
	public static final int NUM_0_SEC = 0;
	public static final int NUM_0_MIN = 0;
	public static final int NUM_0_HOUR = 0;
	public static final int NUM_59_SEC = 59;
	public static final int NUM_59_MIN = 59;
	public static final int NUM_23_HOUR = 23;
	
	public static final int OFFSET_TASK_NUMBER_TO_INDEX = -1;
	
	public static final String COMMAND_ADD_ABBREV = "a";
	public static final String COMMAND_ADD = "add";
	public static final String COMMAND_EDIT_ABBREV = "e";
	public static final String COMMAND_EDIT = "edit";
	public static final String COMMAND_DELETE_ABBREV = "d";
	public static final String COMMAND_DELETE = "delete";
	public static final String COMMAND_COMPLETE_ABBREV = "c";
	public static final String COMMAND_COMPLETE = "complete";
	public static final String COMMAND_LIST_ABBREV = "l";
	public static final String COMMAND_LIST = "list";
	
	public static final String STRING_NO_CHAR = "";
	public static final String STRING_BLANK_SPACE = " ";
	public static final String STRING_DASH = "-";
	public static final String STRING_NO = "N";
	public static final String STRING_YES = "Y";
	
	public static final char CHAR_NO_PRIORITY_LEVEL = 'N';
	
	/* CommandProcessor Class start */
	public static String[] splitActionAndFields(String command) {
		String[] splittedInstruction = command.split(STRING_BLANK_SPACE, NUM_SPLIT_TWO);
		if(splittedInstruction.length == 1) {
			String action = splittedInstruction[INDEX_ACTION];
			splittedInstruction = new String[2];
			splittedInstruction[INDEX_ACTION] = action;
			splittedInstruction[INDEX_FIELDS] = "";
		}
		return splittedInstruction;
	}
	
	/**
	 * Split field line into individual fields
	 * 
	 * @param fieldLine may consists of multiple fields
	 * @return LinkedList of fields, LinkedList may be empty, fields always contain something trimmed
	 */
	public static LinkedList<String> processFieldLine(String fieldLine) {
		String[] fields_array = fieldLine.split(STRING_DASH);
		
		LinkedList<String> fields_linkedList = new LinkedList<String>();
		for(int i = 0; i < fields_array.length; i++) {
			String field = fields_array[i].trim();
			if(!field.equals(STRING_NO_CHAR)) {
				fields_linkedList.add(field);
			}
		}
		return fields_linkedList;
	}
	
	public static String extractTaskName(String fieldLine) {
		int taskNameEndIndex = fieldLine.length();
		for(int i = 0; i < fieldLine.length(); i++) {
			if(fieldLine.charAt(i) == '-') {
				taskNameEndIndex = i;
				break;
			}
		}
		return fieldLine.substring(0, taskNameEndIndex).trim();
	}
	
	public static String extractTaskNumberString(String fieldLine) {
		int taskNumberEndIndex = fieldLine.length();
		for(int i = 0; i < fieldLine.length(); i++) {
			if(fieldLine.charAt(i) == ' ') {
				taskNumberEndIndex = i;
				break;
			}
		}
		return fieldLine.substring(0, taskNumberEndIndex).trim();
	}
	
	public static boolean isValidTaskNumber(String taskNumberString, StringBuilder feedback, int workListSize) {
		try {
			if(taskNumberString.equals(STRING_NO_CHAR)) {
				feedback.append(MESSAGE_INVALID_TASK_NUMBER);
				return false;
			} 
			
			if(Integer.parseInt(taskNumberString) > workListSize || Integer.parseInt(taskNumberString) < 1) {
				feedback.append(MESSAGE_TASK_NUMBER_OUT_OF_RANGE);
				return false;
			}
			return true;
		} catch(NumberFormatException e) {
			feedback.append(MESSAGE_INVALID_TASK_NUMBER);
			return false;
		}
	}
	
	public static boolean isValidPriorityLevel(String priorityLevelString, StringBuilder feedback) {
		if(priorityLevelString.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
			return false;
		}
		if(priorityLevelString.equalsIgnoreCase("H") ||
				priorityLevelString.equalsIgnoreCase("M") ||
				priorityLevelString.equalsIgnoreCase("L")) {
			return true;
		} 
		else {
			feedback.append(MESSAGE_INVALID_PRIORITY_LEVEL);
			return false;
		}
	}

	public static boolean isValidYesNo(String yesNoString, StringBuilder feedback) {
		if(yesNoString.equalsIgnoreCase(STRING_YES) || yesNoString.equalsIgnoreCase(STRING_NO)) {
			return true;
		}
		else {
			feedback.append(MESSAGE_INVALID_YES_NO);
			return false;
		}
	}
	
	public static boolean isValidTaskName(String taskName, StringBuilder feedback) {
		if(taskName.equals(STRING_NO_CHAR)) {
			feedback.append(MESSAGE_INVALID_TASK_NAME);
			return false;
		} 
		else {
			return true;
		}
	}
	
	public static LinkedList<char[]> getSortingCriteria(LinkedList<String> fields) {
		LinkedList<char[]> sortingCriteria = new LinkedList<char[]>();
		for(int i = 0; i < fields.size(); i++) {
			String criterion = fields.get(i);
			char criterionType = criterion.charAt(INDEX_FIELD_TYPE);
			String criterionOrderString = criterion.substring(INDEX_FIELD_CONTENT_START).trim();
			char criteriaOrder = criterionOrderString.charAt(0);
			sortingCriteria.add(new char[]{criterionType, criteriaOrder});
		}
		return sortingCriteria;
	}
}
