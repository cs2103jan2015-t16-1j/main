package quicklyst;

import java.util.LinkedList;

public class DeleteAction extends Action {

	private int _taskIndex;

	public DeleteAction(int taskNumber) {
		this._feedback = new StringBuilder();
		this._type = ActionType.DELETE;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}
	}

	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (isTaskIndexInRange(workingList)) {
			Task taskToDel = workingList.get(_taskIndex);
			workingList.remove(taskToDel);
			workingListMaster.remove(taskToDel);
			this._isSuccess = true;
			this._feedback.append("Task #" + 
					(_taskIndex + 1) +
					" deleted. ");
		} else {
			this._isSuccess = false;
			this._feedback.append("Task # out of range. ");
			return;
		}
	}

	private boolean isTaskIndexInRange(LinkedList<Task> workingList) {
		if (_taskIndex < 0 || _taskIndex >= workingList.size()) {
			return false;
		} else {
			return true;
		}
	}
}
