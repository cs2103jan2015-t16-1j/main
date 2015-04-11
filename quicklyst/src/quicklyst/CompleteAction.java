package quicklyst;

import java.util.Calendar;
import java.util.LinkedList;

//@author A0102015H
public class CompleteAction extends Action {

	private int _taskIndex;
	private Boolean _yesNo;

	public CompleteAction(int taskNumber, Boolean yesNO) {
		
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
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		if (isTaskIndexInRange(displayList)) {

			Task taskToComplete = displayList.get(_taskIndex);

			if(_yesNo == null) {
				taskToComplete.toggleCompleted();
			} else if (_yesNo == true) {
				taskToComplete.setIsCompleted(true);
			} else if (_yesNo == false) {
				taskToComplete.setIsCompleted(false);
			}
				
			this._isSuccess = true;
			this._feedback.append("Task #"
					+ (_taskIndex + 1)
					+ " is "
					+ (taskToComplete.getIsCompleted() ? "completed"
							: "not completed") + ". ");
			
			taskToComplete.setLastUpdated(Calendar.getInstance());
		} else {
			this._isSuccess = false;
			this._feedback.append("Task # out of range. ");
			return;
		}
	}

	private boolean isTaskIndexInRange(LinkedList<Task> displayList) {
		if (_taskIndex < 0 || _taskIndex >= displayList.size()) {
			return false;
		} else {
			return true;
		}
	}
}
