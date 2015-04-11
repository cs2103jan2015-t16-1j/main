package quicklyst;

import java.util.LinkedList;

//@author A0102015H
public class AddAction extends Action {

	private Task _newTask;
	private EditAction _editAction;
	private SortAction _defaultSort;

	public AddAction(String taskName, LinkedList<Field> fields) {

		assert !taskName.isEmpty() && taskName != null;

		_feedback = new StringBuilder();
		_type = ActionType.ADD;
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

			_feedback.append(MessageConstants.NOTHING_ADDED);
			_isSuccess = false;
			return;

		} else {

			displayList.add(_newTask);
			masterList.add(_newTask);

			_isSuccess = true;
			_feedback.append(String.format(MessageConstants.TASK_ADDED,
					_newTask.getName()));

			if (_editAction != null) {

				_editAction.execute(displayList, masterList);
				_feedback.append(_editAction.getFeedback().toString());

				if (!_editAction.isSuccess()) {
					LOGGER.info(MessageConstants.SORTING_DISPLAY_LIST);
					_defaultSort.execute(displayList, masterList);
				}

			} else {
				LOGGER.info(MessageConstants.SORTING_DISPLAY_LIST);
				_defaultSort.execute(displayList, masterList);
			}
		}
	}
}
