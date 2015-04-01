package quicklyst;

import java.util.LinkedList;

public class AddAction extends Action {

	private Task _newTask;
	private EditAction _editAction;

	public AddAction(String taskName, LinkedList<Field> fields) {

		this._feedback = new StringBuilder();
		this._type = ActionType.ADD;

		if (taskName != null) {
			_newTask = new Task(taskName);

		}

		if (fields != null) {
			_editAction = new EditAction(_newTask, fields);
		}
	}

	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (_newTask == null) {
			this._feedback.append("Nothing is added. ");
			this._isSuccess = false;
			return;
		}

		workingList.add(_newTask);
		workingListMaster.add(_newTask);
		
		this._isSuccess = true;
		this._feedback.append("Task: \"" + _newTask.getName() + "\" added. ");

		if (_editAction != null) {
			_editAction.execute();
			this._feedback.append(_editAction.getFeedback().toString());
		}
	}
}
