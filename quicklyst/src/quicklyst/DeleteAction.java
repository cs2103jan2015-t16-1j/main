package quicklyst;

import java.util.LinkedList;

//@author A0102015H
public class DeleteAction extends Action {

	private static final String STRING_DELETED = "deleted";

	private int _taskIndex;

	private String _deletedTaskID;

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

			_deletedTaskID = taskToDel.getGoogleId();

			this._isSuccess = true;
			this._feedback.append(String.format(GlobalConstants.TASK_NO_IS,
					_taskIndex + 1, STRING_DELETED));

		} else {

			this._isSuccess = false;
			this._feedback.append(GlobalConstants.TASK_NO_OUT_OF_RANGE);
			return;
		}
	}

	public String getDeletedTaskID() {
		return _deletedTaskID;
	}

	private boolean isTaskIndexInRange(LinkedList<Task> workingList) {

		if (_taskIndex < 0 || _taskIndex >= workingList.size()) {
			return false;
		} else {
			return true;
		}
	}
}
