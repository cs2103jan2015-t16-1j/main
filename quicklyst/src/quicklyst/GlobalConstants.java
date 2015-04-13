package quicklyst;

public class GlobalConstants {

	public static final String ADD_SUCCESS = "Task: \"%s\" added. ";
	public static final String DEFAULT_TASK_FILE_USED = "Default task file is used. ";
	public static final String DUE_DATE_CLEARED = "Due date cleared. ";
	public static final String DUE_DATE_SET = "Due date set to %s. ";
	public static final String DUE_SMALLER_THAN_START = "Due date/time entered is smaller than start date/time of task. ";
	public static final String ERROR_READING_SETTINGS = "Error reading settings file. Using default settings. ";
	public static final String ERROR_READING_DEFAULT_TASK_FILE = "Error reading default task file. ";
    public static final String ERROR_READING_PREFFERED_TASK_FILE = "Error reading preferred task file. ";
	public static final String FILE_CHANGED = "Filepath changed. You are editing tasks in file: \"%s\". ";
	public static final String INVALID_ACTION_TYPE = "Invalid action type. ";
	public static final String INVALID_DATE_AND_TIME_FORMAT = "Invalid date and time format. ";
	public static final String INVALID_DATE_FORMAT = "Invalid date format. ";
	public static final String INVALID_DATE_RANGE = "Invalid date range. ";
	public static final String INVALID_DAY = "Invalid day \"%s\" entered. ";
	public static final String INVALID_DAY_CRITERIA = "Invalid day criteria \"%s\". ";
	public static final String INVALID_FIELD = "Invalid field %s. ";
	public static final String INVALID_FIELD_CRITERIA = "Invalid field criteria \"%s\". ";
	public static final String INVALID_FIELD_FORMAT_IN = "Invalid field format in \"%s\". ";
	public static final String INVALID_FIELD_TYPE = "Invalid field type. ";
	public static final String INVALID_FILEPATH = "Filepath is invalid. Filepath is not changed. ";
	public static final String INVALID_PRIORITY_LEVEL = "Invalid priority level \"%s\". ";
	public static final String INVALID_TIME = "Invalid time \"%s\" entered. ";
	public static final String LOADED_FROM = "Loaded from: \"%s\". ";
	public static final String LOGGED_OUT = "Logged out from Google. ";
	public static final String MATCHES_FOUND = "%d matches found. ";
	public static final String NAME_NO_CLOSE = "Please denote end of task name with the \"\\\" character. Unexpected error may occur. ";
	public static final String NO_COMMAND = "Please enter a command. ";
	public static final String NO_COMPLETE_CRITERIA = "Completed criteria not entered. ";
	public static final String NO_DATE_CRITERIA = "Date criteria not entered. ";
	public static final String NO_FIELD = "No field entered. ";
	public static final String NO_FILEPATH = "No file path entered. ";
	public static final String NO_KEYWORDS = "No task name keywords entered. ";
	public static final String NO_MATCHES_FOUND = "No matches found. ";
	public static final String NO_OVERDUE_CRITERIA = "Overdue criteria not entered. ";
	public static final String NO_PRIORITY_LEVEL = "Priority level not entered. ";
	public static final String NO_TASK_NAME = "No task name detected. ";
	public static final String NOT_LOGGED_IN = "Not logged in to Google. ";
	public static final String NOTHING_ADDED = "Nothing is added. ";
	public static final String NOTHING_TO_REDO = "Nothing to redo. ";
	public static final String NOTHING_TO_UNDO = "Nothing to undo. ";
	public static final String PRIORITY_CLEARED = "Priority cleared. ";
	public static final String PRIORITY_SET = "Priority set to \"%s\". ";
	public static final String SAVED_TO = "Saved to: \"%s\". ";
	public static final String SORTING_DISPLAY_LIST = "Sorting display list. ";
	public static final String START_BIGGER_THAN_DUE = "Start date/time entered is bigger than due date/time of task. ";
	public static final String START_DATE_CLEARED = "Start date cleared. ";
	public static final String START_DATE_SET = "Start date set to %s. ";
	public static final String SYNCED = "Synced with Google Calendar. ";
	public static final String TASK_ADDED = "Task: \"%s\" added. ";
	public static final String TASK_NAME_BLANK = "Task name entered is blank. ";
	public static final String TASK_NAME_SET = "Task name set to \"%s\". ";
	public static final String TASK_NO_IS = "Task # %d is %s. ";
	public static final String TASK_NO_OUT_OF_RANGE = "Task # out of range. ";
	
	//Storage
	public static final String ERROR_INVALID_FILEPATH = "Invalid filepath. ";
	public static final String ERROR_WRITE_FILE = "Error writing file %s. ";
	public static final String ERROR_READ_FILE = "Error reading file %s. ";
	public static final String ERROR_UNABLE_READ_FILE = "Unable to read file %s. ";
	public static final String ERROR_UNABLE_WRITE_FILE = "Unable to write file %s. ";
	public static final String ERROR_DIRECTORY = "%s is a directory. ";
	public static final String ERROR_UNABLE_MAKE_DIRECTORY = "Unable to make directory %s. ";
	public static final String ERROR_DIRECTORY_FILE = "%s is a file. ";
	
	//Google Integration
	public static final String GOOGLESERVICES_APPLICATION_NAME = "Quicklyst";
	public static final String ERROR_SYNC = "Unable to sync with Google services. ";
	public static final String ERROR_SECURE_CONNECTION_UNAVAILABLE = "Unable to initialise secure connection. ";
	
	//Settings
	public static final String ERROR_READ_SETTINGS = "Error reading settings. ";
	public static final String ERROR_WRITE_SETTINGS = "Error writing settings. ";
	
	public static final String FILEPATH_SETTINGS = "settings.json";
	public static final String FILEPATH_DEFAULT_SAVE = "save.json";
	
	//CommandTips
	public static final String MESSAGE_COMMAND_BODY = " (%s)";
	public static final String MESSAGE_POSSIBLE_COMMANDS = "Possible commands:";
	public static final String MESSAGE_AVAILABLE_COMMANDS = "Available commands:";
	public static final String MESSAGE_INVALID_COMMAND = "Invalid command";
	
    //GUI
	public static final String MESSAGE_HOVER_TASK_TITLE = "<html><u>Task Details</u></html>";
	public static final String MESSAGE_HOVER_DISPLAY = "Mouse over task to show more...";
	public static final String MESSAGE_TITLE = "Quicklyst";
	public static final String MESSAGE_APPLICATION_START = "Welcome...";
	public static final String MESSAGE_STATUS_PROCESSING = "Processing... Please wait...";
    
	public static final String MESSAGE_HEADER_OTHERS = "Others";
	public static final String MESSAGE_HEADER_TOMORROW = "Tomorrow";
	public static final String MESSAGE_HEADER_TODAY = "Today";
	public static final String MESSAGE_HEADER_OVERDUE = "Overdue";
	public static final String MESSAGE_HEADER_NO_DUE_DATE = "No due date";
    public static final String MESSAGE_OVERVIEW = "<html><u>Overview</u><br>" +
                                                  "%d due today<br>" + "%d due tomorrow<br>" + 
                                                  "%d overdue<br>" + "%d completed</html>";
}
