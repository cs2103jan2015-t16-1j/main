package quicklyst;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

//@author A0102015H
public class Logic {

	private static final String SPACE = " ";
	private static final String COMMAND_LOAD_ABBREV = "l";
	private static final String COMMAND_CHANGE_FILE = "cf";
	private static final String COMMAND_LOAD = "load";
	private static final String COMMAND_SAVE_ABBREV = "s";
	private static final String COMMAND_SAVE = "save";
	private static final String COMMAND_REDO_ABBREV = "r";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_UNO_ABBREV = "u";
	private static final String COMMAND_UNDO = "undo";

	private LinkedList<Task> _displayList;
	private LinkedList<Task> _masterList;
	private LinkedList<String> _deletedList;

	private String _filePath;

	private boolean _shouldShowAllCompleted;

	private HistoryManager _historyMgnr;

	private Storage _qLStorage;

	private Settings _qLSettings;

	private static Logic _instance;

	public static Logic getInstance() {
		if (_instance == null) {
			_instance = new Logic();
		}
		return _instance;
	}

	/** General methods **/
	public void setup(StringBuilder feedback) {

		_qLStorage = Storage.getInstance();
		_qLSettings = Settings.getInstance();
		_masterList = new LinkedList<Task>();
        _deletedList = new LinkedList<String>();
		try {
            _filePath = _qLSettings.getPrefFilePath();
        } catch (Error e) {
            feedback.append(MessageConstants.ERROR_READING_SETTINGS);
        }
        if (_filePath != null) {
            try {
                _qLStorage.loadFile(_masterList, _deletedList, _filePath);
    
            } catch (Error e) {
    
                feedback.append(MessageConstants.ERROR_READING_PREFFERED_TASK_FILE);
                _filePath = _qLSettings.getDefaultFilePath();
                _masterList = new LinkedList<Task>();
                _deletedList = new LinkedList<String>();
                try {
                    _qLStorage.loadFile(_masterList, _deletedList, _filePath);
                    feedback.append(MessageConstants.DEFAULT_TASK_FILE_USED);
                } catch (Error err) {
                    feedback.append(MessageConstants.ERROR_READING_DEFAULT_TASK_FILE);
                }
            }
        } else {
            _filePath = _qLSettings.getDefaultFilePath();
            try {
                _qLStorage.loadFile(_masterList, _deletedList, _filePath);
            } catch (Error err) {
                feedback.append(MessageConstants.ERROR_READING_DEFAULT_TASK_FILE);
            }
        }

		_displayList = new LinkedList<Task>();

		copyList(_masterList, _displayList);

		_historyMgnr = new HistoryManager(_displayList, _masterList,
				_deletedList, _shouldShowAllCompleted);
	}

	/* For testing */
	public void setupStub() {

		_filePath = "test.json";
		_shouldShowAllCompleted = false;
		_qLStorage = Storage.getInstance();
		_qLSettings = Settings.getInstance();
		_displayList = new LinkedList<Task>();
		_masterList = new LinkedList<Task>();
		_deletedList = new LinkedList<String>();
		_historyMgnr = new HistoryManager(_displayList, _masterList,
				_deletedList, _shouldShowAllCompleted);
	}

	public LinkedList<Task> getDisplayList() {
		if (!_shouldShowAllCompleted) {
			showStreamlinedList();
		}
		return _displayList;
	}

	private void showStreamlinedList() {

		Calendar now = Calendar.getInstance();
		Iterator<Task> iter = _displayList.listIterator();

		while (iter.hasNext()) {

			Task task = iter.next();

			if (task.getIsCompleted()) {

				if ((task.getDueDate() == null)
						|| (task.getDueDate().compareTo(now) < 0)) {
					iter.remove();
				}
			}
		}
	}

	public LinkedList<Task> getMasterList() {
		return _masterList;
	}

	public void executeUndo(StringBuilder feedback) {

		_historyMgnr.undo(feedback);

		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		_shouldShowAllCompleted = _historyMgnr.getShouldShowAllCompleted();
		_deletedList = _historyMgnr.getDeletedList();

		try {
            _qLStorage.saveFile(_masterList, _deletedList, _filePath);
        } catch (Error e) {
            feedback.append(e.getMessage());
        }
	}

	public void executeRedo(StringBuilder feedback) {

		_historyMgnr.redo(feedback);

		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		_shouldShowAllCompleted = _historyMgnr.getShouldShowAllCompleted();
		_deletedList = _historyMgnr.getDeletedList();

		try {
            _qLStorage.saveFile(_masterList, _deletedList, _filePath);
        } catch (Error e) {
            feedback.append(e.getMessage());
        }
	}

	public void executeCommand(String command, StringBuilder feedback) {

		if (command.trim().equalsIgnoreCase(COMMAND_UNDO)
				|| command.trim().equalsIgnoreCase(COMMAND_UNO_ABBREV)) {
			executeUndo(feedback);
			return;
		}

		if (command.trim().equalsIgnoreCase(COMMAND_REDO)
				|| command.trim().equalsIgnoreCase(COMMAND_REDO_ABBREV)) {
			executeRedo(feedback);
			return;
		}

		if (command.split(SPACE, 2)[0].equalsIgnoreCase(COMMAND_SAVE)
				|| command.split(SPACE, 2)[0]
						.equalsIgnoreCase(COMMAND_SAVE_ABBREV)) {
			executeSave(command, feedback);
			return;
		}

		if (command.split(SPACE, 2)[0].equalsIgnoreCase(COMMAND_LOAD)
				|| command.split(SPACE, 2)[0]
						.equalsIgnoreCase(COMMAND_LOAD_ABBREV)) {
			executeLoad(command, feedback);
			return;
		}

		if (command.split(SPACE, 2)[0].equalsIgnoreCase(COMMAND_CHANGE_FILE)) {
			executeChangeFile(command, feedback);
			return;
		}

		executeAction(command, feedback);
	}

	public boolean shouldShowAllCompleted() {
		return _shouldShowAllCompleted;
	}

	private void executeChangeFile(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(SPACE, 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append(MessageConstants.NO_FILEPATH);

		} else {

			String filepath = commandAndPath[1].trim();

			if (_qLStorage.isValidFile(filepath)) {
			    try {
    
			        LinkedList<Task> tempMasterList = new LinkedList<Task>();
			        LinkedList<String> tempDeletedList = new LinkedList<String>();
    				
    				
    				_qLStorage.loadFile(tempMasterList, tempDeletedList, filepath);
    				
    				_qLSettings.updatePrefFilePath(filepath);
    				
    				_masterList = tempMasterList;
    				_deletedList = tempDeletedList;
    				
    				_filePath = filepath;
    
    				copyList(_masterList, _displayList);
    
    				_historyMgnr = new HistoryManager(_displayList, _masterList,
    						_deletedList, _shouldShowAllCompleted);
    
    				feedback.append(String.format(MessageConstants.FILE_CHANGED,
    						filepath));
			    } catch (Error e) {
                    feedback.append(e.getMessage());
                }
			} else {

				feedback.append(MessageConstants.INVALID_FILEPATH);
			}
		}
	}

	private void executeAction(String command, StringBuilder feedback) {

		CommandParser cp = new CommandParser(command);

		feedback.append(cp.getFeedback().toString());

		Action action = cp.getAction();

		if (action == null) {
			return;
		}

		if (action.getType() == ActionType.SYNC) {
			action.attachDeletedList(_deletedList);
		}

		action.execute(_displayList, _masterList);

		feedback.append(action.getFeedback().toString());

		if (action.getType() == ActionType.FIND) {
			_shouldShowAllCompleted = action.shouldShowAllCompleted();
		}

		if (action.isSuccess()) {

			if (action.getType() == ActionType.DELETE
					&& action.getDeletedTaskID() != null) {

				_deletedList.add(action.getDeletedTaskID());
			}
			try {
			    _qLStorage.saveFile(_masterList, _deletedList, _filePath);
			} catch (Error e) {
			    feedback.append(e.getMessage());
			}

			if (action.getType() != ActionType.LOG_OUT) {

				_historyMgnr.updateUndoStack(_displayList, _masterList,
						_deletedList, _shouldShowAllCompleted);
			}
		}
	}

	private void executeLoad(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(SPACE, 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append(MessageConstants.NO_FILEPATH);

		} else {

			String filepath = commandAndPath[1].trim();

			try {

				_displayList = new LinkedList<Task>();
				_deletedList = new LinkedList<String>();

				_qLStorage.loadFile(_displayList, _deletedList, filepath);

				_masterList = new LinkedList<Task>();

				copyList(_displayList, _masterList);

				try {
	                _qLStorage.saveFile(_masterList, _deletedList, _filePath);
	            } catch (Error e) {
	                feedback.append(e.getMessage());
	            }

				_historyMgnr.updateUndoStack(_displayList, _masterList,
						_deletedList, _shouldShowAllCompleted);

				feedback.append(String.format(MessageConstants.LOADED_FROM,
						filepath));

			} catch (Error e) {
				feedback.append(e.getMessage());
			}
		}
	}

	private void executeSave(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(SPACE, 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append(MessageConstants.NO_FILEPATH);

		} else {

			String filepath = commandAndPath[1].trim();

			try {

				_qLStorage.saveFile(_masterList, _deletedList, filepath);
				feedback.append(String.format(MessageConstants.SAVED_TO,
						filepath));

			} catch (Error e) {
				feedback.append(e.getMessage());
			}
		}
	}

	private <E> void copyList(LinkedList<E> fromList, LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}
}
