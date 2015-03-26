package quicklyst;

import java.util.LinkedList;

public class CompleteAction extends Action {

	private int _taskIndex;
	private FieldCriteria _yesNo;

	public CompleteAction(int taskNumber, FieldCriteria yesNO) {
		this._feedback = new StringBuilder();
		this._type = ActionType.COMPLETE;
		_yesNo = yesNO;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (isTaskIndexInRange(workingList)) {

			Task taskToComplete = workingList.get(_taskIndex);

			if (_yesNo == FieldCriteria.YES) {
				taskToComplete.setIsCompleted(true);
			} else if (_yesNo == FieldCriteria.NO) {
				taskToComplete.setIsCompleted(false);
			} else if (_yesNo == null) {
				taskToComplete.toggleCompleted();
			}

			this._feedback.append("Task #"
					+ (_taskIndex + 1)
					+ " is "
					+ (taskToComplete.getIsCompleted() ? "completed"
							: "not completed") + ". ");
		} else {
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
