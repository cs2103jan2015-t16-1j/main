package quicklyst;

import java.util.LinkedList;
import java.util.Stack;

//@author A0102015H
public class HistoryManager {

	private LinkedList<Task> _displayList;
	private LinkedList<Task> _masterList;

	private LinkedList<String> _deletedList;

	private boolean _shouldShowAllCompleted;

	private Stack<LinkedList<Task>> _undoMainStack;
	private Stack<LinkedList<Task>> _redoMainStack;

	private Stack<Boolean> _undoShowAllStack;
	private Stack<Boolean> _redoShowAllStack;

	private Stack<LinkedList<String>> _undoDeletedListStack;
	private Stack<LinkedList<String>> _redoDeletedListStack;

	public HistoryManager(LinkedList<Task> displayList,
			LinkedList<Task> masterList, LinkedList<String> deletedList,
			boolean shouldShowAllCompleted) {

		_undoMainStack = new Stack<LinkedList<Task>>();
		_redoMainStack = new Stack<LinkedList<Task>>();

		LinkedList<Task> displayListInit = new LinkedList<Task>();
		LinkedList<Task> masterListInit = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, displayListInit,
				masterListInit);

		_undoMainStack.push(masterListInit);
		_undoMainStack.push(displayListInit);

		_displayList = displayListInit;
		_masterList = masterListInit;

		_shouldShowAllCompleted = shouldShowAllCompleted;

		_undoShowAllStack = new Stack<Boolean>();
		_redoShowAllStack = new Stack<Boolean>();

		_undoShowAllStack.push(_shouldShowAllCompleted);

		_deletedList = new LinkedList<String>(deletedList);

		_undoDeletedListStack = new Stack<LinkedList<String>>();
		_redoDeletedListStack = new Stack<LinkedList<String>>();

		_undoDeletedListStack.push(_deletedList);
	}

	public LinkedList<Task> getDisplayList() {
		return _displayList;
	}

	public LinkedList<Task> getMasterList() {
		return _masterList;
	}

	public boolean getShouldShowAllCompleted() {
		return _shouldShowAllCompleted;
	}

	public LinkedList<String> getDeletedList() {
		return _deletedList;
	}

	public void updateUndoStack(LinkedList<Task> displayList,
			LinkedList<Task> masterList, LinkedList<String> deletedList,
			boolean shouldShowAllCompleted) {

		LinkedList<Task> workingListMaster = new LinkedList<Task>();
		LinkedList<Task> workingList = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, workingList,
				workingListMaster);

		_undoMainStack.push(workingListMaster);
		_undoMainStack.push(workingList);
		_redoMainStack.clear();

		_undoShowAllStack.push(shouldShowAllCompleted);
		_redoShowAllStack.clear();

		_undoDeletedListStack.push(new LinkedList<String>(deletedList));
		_redoDeletedListStack.clear();
	}

	public void undo(StringBuilder feedback) {

		if (_undoMainStack.size() == 2) {
			feedback.append(MessageConstants.NOTHING_TO_UNDO);
			return;
		}

		_redoMainStack.push(_undoMainStack.pop());
		_redoMainStack.push(_undoMainStack.pop());

		LinkedList<Task> displayList = _undoMainStack.pop();
		LinkedList<Task> masterList = _undoMainStack.pop();

		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, updatedWL, updatedWLM);

		_undoMainStack.push(masterList);
		_undoMainStack.push(displayList);

		_displayList = updatedWL;
		_masterList = updatedWLM;

		_redoShowAllStack.push(_undoShowAllStack.pop());
		_shouldShowAllCompleted = _undoShowAllStack.pop();
		_undoShowAllStack.push(_shouldShowAllCompleted);

		_redoDeletedListStack.push(_undoDeletedListStack.pop());
		LinkedList<String> deletedList = _undoDeletedListStack.pop();
		LinkedList<String> updatedDL = new LinkedList<String>(deletedList);
		_undoDeletedListStack.push(updatedDL);
		_deletedList = updatedDL;
	}

	public void redo(StringBuilder feedback) {

		if (_redoMainStack.isEmpty()) {
			feedback.append(MessageConstants.NOTHING_TO_REDO);
			return;
		}

		LinkedList<Task> masterList = _redoMainStack.pop();
		LinkedList<Task> displayList = _redoMainStack.pop();

		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, updatedWL, updatedWLM);

		_undoMainStack.push(masterList);
		_undoMainStack.push(displayList);

		_displayList = updatedWL;
		_masterList = updatedWLM;

		_shouldShowAllCompleted = _redoShowAllStack.pop();
		_undoShowAllStack.push(_shouldShowAllCompleted);

		LinkedList<String> deletedList = _redoDeletedListStack.pop();
		LinkedList<String> updatedDL = new LinkedList<String>(deletedList);
		_undoDeletedListStack.push(deletedList);
		_deletedList = updatedDL;
	}

	private void copyListWithClone(LinkedList<Task> subList,
			LinkedList<Task> masterList, LinkedList<Task> subListNew,
			LinkedList<Task> masterListNew) {

		LinkedList<Integer> indexesInMasterListForRepeatTask = new LinkedList<Integer>();

		for (int i = 0; i < subList.size(); i++) {
			indexesInMasterListForRepeatTask.add(masterList.indexOf(subList
					.get(i)));
		}

		for (int i = 0; i < masterList.size(); i++) {
			masterListNew.add(masterList.get(i).clone());
		}

		for (int i = 0; i < indexesInMasterListForRepeatTask.size(); i++) {
			subListNew.add(masterListNew.get(indexesInMasterListForRepeatTask
					.get(i)));
		}
	}

}