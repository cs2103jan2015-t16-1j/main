package quicklyst;

import java.util.LinkedList;
import java.util.Scanner;

//@author A0102015H
public class QLLogic {

	public static LinkedList<Task> _displayList; // TODO change back to private
	private static LinkedList<Task> _masterList;
	private static String _filePath;

	private static HistoryManager _historyMgnr;

	/** General methods **/
	public static void setup(StringBuilder feedback) {
		try {
			_filePath = QLSettings.getInstance().getPrefFilePath();
			_masterList = QLStorage.getInstance().loadFile(new LinkedList<Task>(), _filePath);
		} catch (Error e) {
			feedback.append("Preferred task file does not exist. " + "Default task file is used. ");
			_filePath = QLSettings.getInstance().getDefaultFilePath();
			_masterList = QLStorage.getInstance().loadFile(new LinkedList<Task>(), _filePath);
		}
		_displayList = new LinkedList<Task>();
		copyList(_masterList, _displayList);
		_historyMgnr = new HistoryManager(_displayList, _masterList);
	}

	// Stub
	public static void setupStub() {
		_displayList = new LinkedList<Task>();
		_masterList = new LinkedList<Task>();
		_historyMgnr = new HistoryManager(_displayList, _masterList);
	}

	// Stub
	public static void displayStub(StringBuilder feedback) {
		System.out.println("Feedback: " + feedback.toString());
		System.out.println("Name: start date: due date:");
		for (int i = 0; i < _displayList.size(); i++) {
			System.out.print(_displayList.get(i).getName() + " ");
			try {
				System.out
						.print(_displayList.get(i).getStartDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			try {
				System.out.print(_displayList.get(i).getDueDateString() + " ");
			} catch (NullPointerException e) {
				System.out.print("        ");
			}
			if (_displayList.get(i).getPriority() != null) {
				System.out.print(_displayList.get(i).getPriority() + " ");
			}
			System.out.println();
		}

		/*
		 * System.out.println("	workingListMaster: "); for(int i = 0; i <
		 * _workingListMaster.size(); i++) {
		 * System.out.print(_workingListMaster.get(i).getName() + " "); try {
		 * System.out.print(_workingListMaster.get(i).getStartDateString() +
		 * " "); } catch(NullPointerException e) {} try {
		 * System.out.print(_workingListMaster.get(i).getDueDateString() + " ");
		 * } catch(NullPointerException e) {}
		 * if(_workingListMaster.get(i).getPriority() != null) {
		 * System.out.print(_workingListMaster.get(i).getPriority() + " "); }
		 * System.out.println(); }
		 */

		System.out.println();
		feedback.setLength(0);
	}

	public static LinkedList<Task> getDisplayList() {
		return _displayList;
	}

	public static LinkedList<Task> getFullList() {
		return _masterList;
	}

	public static void executeUndo(StringBuilder feedback) {
		_historyMgnr.undo(feedback);
		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		QLStorage.getInstance().saveFile(_masterList, _filePath);
	}

	public static void executeRedo(StringBuilder feedback) {
		_historyMgnr.redo(feedback);
		_displayList = _historyMgnr.getDisplayList();
		_masterList = _historyMgnr.getMasterList();
		QLStorage.getInstance().saveFile(_masterList, _filePath);
	}

	public static void executeCommand(String command, StringBuilder feedback) {

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

	private static void executeChangeDir(String command, StringBuilder feedback) {
		String commandAndPath[] = command.split(" ", 2);
		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {
			feedback.append("No file path entered. ");
		} else {
			String filepath = commandAndPath[1].trim();
			if (QLStorage.getInstance().isValidFile(filepath)) {
				QLSettings.getInstance().updatePrefFilePath(filepath);
				_filePath = filepath;
				_masterList = QLStorage.getInstance().loadFile(new LinkedList<Task>(),
						filepath);
				_displayList = new LinkedList<Task>();
				copyList(_masterList, _displayList);
				_historyMgnr = new HistoryManager(_displayList, _masterList);
				feedback.append("Directory changed. You are editing tasks in file: \"" + filepath + "\".");
			} else {
				feedback.append("Preferred task file does not exist. "  + "Directory is not changed. ");
			}
		}
	}

	private static void executeAction(String command, StringBuilder feedback) {

		CommandParser cp = new CommandParser(command);
		feedback.append(cp.getFeedback().toString());
		Action action = cp.getAction();

		if (action == null) {
			return;
		}

		action.execute(_displayList, _masterList);
		feedback.append(action.getFeedback().toString());

		if (action.isSuccess()) {
			QLStorage.getInstance().saveFile(_masterList, _filePath);
			if (action.getType() != ActionType.PUSH) {
				_historyMgnr.updateUndoStack(_displayList, _masterList);
			}
		}
	}

	private static void executeLoad(String command, StringBuilder feedback) {
		String commandAndPath[] = command.split(" ", 2);
		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {
			feedback.append("No file path entered. ");
		} else {
			String filepath = commandAndPath[1].trim();
			try {
				_displayList = QLStorage.getInstance().loadFile(new LinkedList<Task>(),
						filepath);
				_masterList = new LinkedList<Task>();
				copyList(_displayList, _masterList);
				QLStorage.getInstance().saveFile(_masterList, _filePath);
				_historyMgnr.updateUndoStack(_displayList, _masterList);
				feedback.append("Loaded from: \"" + filepath + "\". ");
			} catch (Error e) {
				feedback.append(e.getMessage());
			}
		}
	}

	private static void executeSave(String command, StringBuilder feedback) {
		String commandAndPath[] = command.split(" ", 2);
		if (commandAndPath.length == 1 || commandAndPath[1].trim().isEmpty()) {
			feedback.append("No file path entered. ");
		} else {
			String filepath = commandAndPath[1].trim();
			try {
				QLStorage.getInstance().saveFile(_masterList, filepath);
				feedback.append("Saved to: \"" + filepath + "\". ");
			} catch (Error e) {
				feedback.append(e.getMessage());
			}
		}
	}

	/** Multi-command methods **/

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

	/** Main method **/
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		setupStub();
		StringBuilder feedback = new StringBuilder();
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter command: ");
			String command = sc.nextLine();
			executeCommand(command, feedback);
			displayStub(feedback);
		}
	}

}
