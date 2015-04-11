package quicklyst;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

//@author A0102015H
public class QLLogic {

	private LinkedList<Task> _displayList;
	private LinkedList<Task> _masterList;
	private LinkedList<String> _deletedList;

	private String _filePath;

	private boolean _shouldShowAllCompleted;

	private HistoryManager _historyMgnr;

	private QLStorage _QLStorage;

	private QLSettings _QLSettings;

	private static QLLogic _instance;

	public static QLLogic getInstance() {
		if (_instance == null) {
			_instance = new QLLogic();
		}
		return _instance;
	}

	/** General methods **/
	public void setup(StringBuilder feedback) {

		_QLStorage = QLStorage.getInstance();
		_QLSettings = QLSettings.getInstance();

		try {

			_filePath = _QLSettings.getPrefFilePath();
			_masterList = new LinkedList<Task>();
			_deletedList = new LinkedList<String>();
			_QLStorage.loadFile(_masterList, _deletedList, _filePath);

		} catch (Error e) {

			feedback.append("Preferred task file does not exist. "
					+ "Default task file is used. ");
			_filePath = _QLSettings.getDefaultFilePath();
			_masterList = new LinkedList<Task>();
			_deletedList = new LinkedList<String>();
			_QLStorage.loadFile(_masterList, _deletedList, _filePath);
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
		_QLStorage = QLStorage.getInstance();
		_QLSettings = QLSettings.getInstance();
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

	public LinkedList<Task> getFullList() {
		return _masterList;
	}

	public void executeUndo(StringBuilder feedback) {

		_historyMgnr.undo(feedback);

		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		_shouldShowAllCompleted = _historyMgnr.getShouldShowAll();
		_deletedList = _historyMgnr.getDeletedList();

		_QLStorage.saveFile(_masterList, _deletedList, _filePath);
	}

	public void executeRedo(StringBuilder feedback) {

		_historyMgnr.redo(feedback);

		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		_shouldShowAllCompleted = _historyMgnr.getShouldShowAll();
		_deletedList = _historyMgnr.getDeletedList();

		_QLStorage.saveFile(_masterList, _deletedList, _filePath);
	}

	public void executeCommand(String command, StringBuilder feedback) {

		if (command.trim().equalsIgnoreCase("undo")
				|| command.trim().equalsIgnoreCase("u")) {
			executeUndo(feedback);
			return;
		}

		if (command.trim().equalsIgnoreCase("redo")
				|| command.trim().equalsIgnoreCase("r")) {
			executeRedo(feedback);
			return;
		}

		if (command.split(" ", 2)[0].equalsIgnoreCase("save")
				|| command.split(" ", 2)[0].equalsIgnoreCase("s")) {
			executeSave(command, feedback);
			return;
		}

		if (command.split(" ", 2)[0].equalsIgnoreCase("load")
				|| command.split(" ", 2)[0].equalsIgnoreCase("l")) {
			executeLoad(command, feedback);
			return;
		}

		if (command.split(" ", 2)[0].equalsIgnoreCase("cd")
				|| command.split(" ", 2)[0].equalsIgnoreCase("s")) {
			executeChangeDir(command, feedback);
			return;
		}

		executeAction(command, feedback);
	}

	public boolean shouldShowAllCompleted() {
		return _shouldShowAllCompleted;
	}

	private void executeChangeDir(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(" ", 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append("No file path entered. ");

		} else {

			String filepath = commandAndPath[1].trim();

			if (_QLStorage.isValidFile(filepath)) {

				_filePath = filepath;

				_QLSettings.updatePrefFilePath(filepath);

				_masterList = new LinkedList<Task>();
				_deletedList = new LinkedList<String>();

				_QLStorage.loadFile(_masterList, _deletedList, filepath);

				copyList(_masterList, _displayList);

				_historyMgnr = new HistoryManager(_displayList, _masterList,
						_deletedList, _shouldShowAllCompleted);

				feedback.append("Directory changed. You are editing tasks in file: \""
						+ filepath + "\".");

			} else {

				feedback.append("Preferred task file does not exist. "
						+ "Directory is not changed. ");
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

			_QLStorage.saveFile(_masterList, _deletedList, _filePath);

			if (action.getType() != ActionType.PUSH) {

				_historyMgnr.updateUndoStack(_displayList, _masterList,
						_deletedList, _shouldShowAllCompleted);
			}
		}
	}

	private void executeLoad(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(" ", 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append("No file path entered. ");

		} else {

			String filepath = commandAndPath[1].trim();

			try {

				_displayList = new LinkedList<Task>();
				_deletedList = new LinkedList<String>();

				_QLStorage.loadFile(_displayList, _deletedList, filepath);

				_masterList = new LinkedList<Task>();

				copyList(_displayList, _masterList);

				_QLStorage.saveFile(_masterList, _deletedList, _filePath);

				_historyMgnr.updateUndoStack(_displayList, _masterList,
						_deletedList, _shouldShowAllCompleted);

				feedback.append("Loaded from: \"" + filepath + "\". ");

			} catch (Error e) {
				feedback.append(e.getMessage());
			}
		}
	}

	private void executeSave(String command, StringBuilder feedback) {

		String commandAndPath[] = command.split(" ", 2);

		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {

			feedback.append("No file path entered. ");

		} else {

			String filepath = commandAndPath[1].trim();

			try {

				_QLStorage.saveFile(_masterList, _deletedList, filepath);
				feedback.append("Saved to: \"" + filepath + "\". ");

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
