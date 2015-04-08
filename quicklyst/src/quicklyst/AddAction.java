package quicklyst;

import java.util.LinkedList;

public class AddAction extends Action {

	private Task _newTask;
	private EditAction _editAction;
	private SortAction _defaultSort;

	public AddAction(String taskName, LinkedList<Field> fields) {

		this._feedback = new StringBuilder();
		this._type = ActionType.ADD;
		_defaultSort = new SortAction();

		if (taskName != null) {
			_newTask = new Task(taskName);
		}

		if (fields != null) {
			_editAction = new EditAction(_newTask, fields);
		}
	}

	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		if (_newTask == null) {
			this._feedback.append("Nothing is added. ");
			this._isSuccess = false;
			return;
		} else {
			displayList.add(_newTask);
			masterList.add(_newTask);

			this._isSuccess = true;
			this._feedback.append("Task: \"" + _newTask.getName()
					+ "\" added. ");

			if (_editAction != null) {
				_editAction.execute(displayList, masterList);
				this._feedback.append(_editAction.getFeedback().toString());
				if (!_editAction.isSuccess()) {
					_defaultSort.execute(displayList, masterList);
				}
			} else {
				_defaultSort.execute(displayList, masterList);
			}
		}
	}
}
