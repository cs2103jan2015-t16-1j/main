package quicklyst;

import java.util.LinkedList;
import java.util.Stack;

public class HistoryManager {

	private Stack<LinkedList<Task>> _undoStack;
	private Stack<LinkedList<Task>> _redoStack;
	private LinkedList<Task> _displayList;
	private LinkedList<Task> _masterList;

	public HistoryManager(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		_undoStack = new Stack<LinkedList<Task>>();
		_redoStack = new Stack<LinkedList<Task>>();
		LinkedList<Task> displayListInit = new LinkedList<Task>();
		LinkedList<Task> masterListInit = new LinkedList<Task>();
		copyListWithClone(displayList, masterList, displayListInit,
				masterListInit);
		_undoStack.push(masterListInit);
		_undoStack.push(displayListInit);
		_displayList = displayListInit;
		_masterList = masterListInit;
	}

	public LinkedList<Task> getDisplayList() {
		return _displayList;
	}

	public LinkedList<Task> getMasterList() {
		return _masterList;
	}

	private static void printStack(Stack<LinkedList<Task>> stack) {
		Stack<LinkedList<Task>> buffer = new Stack<LinkedList<Task>>();
		int stackCount = 0;
		while (!stack.isEmpty()) {
			stackCount++;
			buffer.push(stack.pop());
			LinkedList<Task> list = buffer.peek();
			if (stackCount % 2 != 0) {
				System.out.println("Stack " + stackCount);
				for (Task task : list) {
					System.out.println(task.getName());
				}
			}
		}

		while (!buffer.isEmpty()) {
			stack.push(buffer.pop());
		}
	}

	public void updateUndoStack(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		LinkedList<Task> workingListMaster = new LinkedList<Task>();
		LinkedList<Task> workingList = new LinkedList<Task>();
		copyListWithClone(displayList, masterList, workingList,
				workingListMaster);

		_undoStack.push(workingListMaster);
		_undoStack.push(workingList);
		_redoStack.clear();
	}

	public void undo(StringBuilder feedback) {
		if (_undoStack.size() == 2) {
			feedback.append("Nothing to undo. ");
			return;
		}
		_redoStack.push(_undoStack.pop());
		_redoStack.push(_undoStack.pop());

		LinkedList<Task> displayList = _undoStack.pop();
		LinkedList<Task> masterList = _undoStack.pop();

		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, updatedWL, updatedWLM);

		_undoStack.push(masterList);
		_undoStack.push(displayList);

		_displayList = updatedWL;
		_masterList = updatedWLM;
		
	}

	public void redo(StringBuilder feedback) {
		if (_redoStack.isEmpty()) {
			feedback.append("Nothing to redo. ");
			return;
		}

		LinkedList<Task> masterList = _redoStack.pop();
		LinkedList<Task> displayList = _redoStack.pop();

		LinkedList<Task> updatedWL = new LinkedList<Task>();
		LinkedList<Task> updatedWLM = new LinkedList<Task>();

		copyListWithClone(displayList, masterList, updatedWL, updatedWLM);

		_undoStack.push(masterList);
		_undoStack.push(displayList);

		_displayList = updatedWL;
		_masterList = updatedWLM;

	}

	private void copyListWithClone(LinkedList<Task> subList,
			LinkedList<Task> masterList, LinkedList<Task> subListNew,
			LinkedList<Task> masterListNew) {

		LinkedList<Integer> indexesInListMasterForRepeatTask = new LinkedList<Integer>();
		for (int i = 0; i < subList.size(); i++) {
			indexesInListMasterForRepeatTask.add(masterList.indexOf(subList
					.get(i)));
		}
		for (int i = 0; i < masterList.size(); i++) {
			masterListNew.add(masterList.get(i).clone());
		}
		for (int i = 0; i < indexesInListMasterForRepeatTask.size(); i++) {
			subListNew.add(masterListNew.get(indexesInListMasterForRepeatTask
					.get(i)));
		}
	}

}
