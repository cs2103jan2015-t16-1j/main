package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class EditAction extends Action {

	private int _taskIndex;
	private LinkedList<Field> _fields;
	private Task _task;

	public EditAction(int taskNumber, LinkedList<Field> fields) {
		
		this._isSuccess = false;
		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}

		_fields = fields;
	}

	public EditAction(Task task, LinkedList<Field> fields) {

		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;
		_task = task;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (isTaskIndexInRange(workingList)) {
			_task = workingList.get(_taskIndex);
			execute();
		} else {
			this._feedback.append("Task # out of range. ");
		}
	}

	public void execute() {
		for (Field field : _fields) {
			FieldType fieldType = field.getFieldType();
			switch (fieldType) {
			case TASK_NAME:
				_task.setName(field.getTaskName());
				this._feedback.append("Task name set to \""
						+ field.getTaskName() + "\". ");
				break;
			case START_DATE:

				if (field.shouldClearDate()) {
					_task.setStartDate((Calendar) null);
					this._feedback.append("Start date cleared. ");
					break;
				}

				if (field.getDate() == null) {
					break;
				}

				if (_task.getStartDate() != null) {
					updateStartDate(field);
				} else {
					addStartDate(field);
				}
				break;

			case DUE_DATE:

				if (field.shouldClearDate()) {
					_task.setDueDate((Calendar) null);
					this._feedback.append("Due date cleared. ");
					break;
				}

				if (field.getDate() == null) {
					break;
				}

				if (_task.getDueDate() != null) {
					updateDueDate(field);
				} else {
					addDueDate(field);
				}
				break;

			case REMINDER:
				/*
				 * not implemented yet _task.setReminder(field.getDate());
				 * this._feedback.append("Reminder set to " +
				 * _task.getReminderDateString() + ". ");
				 */
				break;
			case PRIORITY:
				if (field.getPriority() == null) {
					break;
				} else if (field.getPriority().equalsIgnoreCase("clr")) {
					_task.setPriority((String) null);
					this._feedback.append("Priority cleared. ");
					break;
				} else {
					_task.setPriority(field.getPriority());
					this._feedback.append("Priority set to \""
							+ field.getPriority() + "\". ");
					break;
				}
			default:
				break;
			}
		}
	}

	private void addDueDate(Field field) {
		if (_task.getStartDate() != null
				&& field.getDate().compareTo(_task.getStartDate()) < 0) {
			this._feedback
					.append("Due date/time entered is bigger than start date/time of task. ");
			return;
		}

		_task.setDueDate(field.getDate());
		_task.setHasDueTime(true);

		if (!field.isTimeParsed()) {
			_task.getDueDate().set(Calendar.HOUR_OF_DAY, 23);
			_task.getDueDate().set(Calendar.MINUTE, 59);
			_task.getDueDate().set(Calendar.SECOND, 59);
			_task.setHasDueTime(false);
		}
		
		this._isSuccess = true;
		this._feedback.append("Due date set to " + _task.getDueDateTimeString()
				+ ". ");
	}

	private void updateDueDate(Field field) {
		Calendar newDate = new GregorianCalendar();

		if (field.isDateParsed() && field.isTimeParsed()) {
			addDueDate(field);
			return;
		} else if (field.isDateParsed()) {

			newDate.set(Calendar.YEAR, field.getDate().get(Calendar.YEAR));
			newDate.set(Calendar.MONTH, field.getDate().get(Calendar.MONTH));
			newDate.set(Calendar.DAY_OF_MONTH,
					field.getDate().get(Calendar.DAY_OF_MONTH));

			newDate.set(Calendar.HOUR_OF_DAY,
					_task.getDueDate().get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE, _task.getDueDate()
					.get(Calendar.MINUTE));

		} else if (field.isTimeParsed()) {
			newDate.set(Calendar.HOUR_OF_DAY,
					field.getDate().get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE, field.getDate().get(Calendar.MINUTE));

			newDate.set(Calendar.YEAR, _task.getDueDate().get(Calendar.YEAR));
			newDate.set(Calendar.MONTH, _task.getDueDate().get(Calendar.MONTH));
			newDate.set(Calendar.DAY_OF_MONTH,
					_task.getDueDate().get(Calendar.DAY_OF_MONTH));
		} else {
			return;
		}

		if (_task.getStartDate() != null
				&& newDate.compareTo(_task.getStartDate()) < 0) {
			this._feedback
					.append("Due date/time entered is bigger than start date/time of task. ");
		} else {
			_task.setDueDate(newDate);
			if(field.isDateParsed()) {
				_task.setHasDueTime(true);
			}
			this._isSuccess = true;
			this._feedback.append("Due date set to "
					+ _task.getDueDateTimeString() + ". ");

		}

	}

	private void addStartDate(Field field) {
		if (_task.getDueDate() != null
				&& field.getDate().compareTo(_task.getDueDate()) > 0) {
			this._feedback
					.append("Start date/time entered is bigger than due date/time of task. ");
			return;
		}

		_task.setStartDate(field.getDate());
		_task.setHasStartTime(true);

		if (!field.isTimeParsed()) {
			_task.getStartDate().set(Calendar.HOUR_OF_DAY, 0);
			_task.getStartDate().set(Calendar.MINUTE, 0);
			_task.getStartDate().set(Calendar.SECOND, 0);
			_task.setHasStartTime(false);
		}
		
		this._isSuccess = true;
		this._feedback.append("Start date set to " + _task.getStartDateTimeString()
				+ ". ");
	}

	private void updateStartDate(Field field) {
		Calendar newDate = new GregorianCalendar();

		if (field.isDateParsed() && field.isTimeParsed()) {
			addStartDate(field);
			return;
		} else if (field.isDateParsed()) {

			newDate.set(Calendar.YEAR, field.getDate().get(Calendar.YEAR));
			newDate.set(Calendar.MONTH, field.getDate().get(Calendar.MONTH));
			newDate.set(Calendar.DAY_OF_MONTH,
					field.getDate().get(Calendar.DAY_OF_MONTH));

			newDate.set(Calendar.HOUR_OF_DAY,
					_task.getStartDate().get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE,
					_task.getStartDate().get(Calendar.MINUTE));

		} else if (field.isTimeParsed()) {
			newDate.set(Calendar.HOUR_OF_DAY,
					field.getDate().get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE, field.getDate().get(Calendar.MINUTE));

			newDate.set(Calendar.YEAR, _task.getStartDate().get(Calendar.YEAR));
			newDate.set(Calendar.MONTH, _task.getStartDate()
					.get(Calendar.MONTH));
			newDate.set(Calendar.DAY_OF_MONTH,
					_task.getStartDate().get(Calendar.DAY_OF_MONTH));

		} else {
			return;
		}

		if (_task.getDueDate() != null
				&& newDate.compareTo(_task.getDueDate()) > 0) {
			this._feedback
					.append("Start date/time entered is bigger than due date/time of task. ");
		} else {
			_task.setStartDate(newDate);
			if(field.isTimeParsed()) {
				_task.setHasDueTime(true);
			}
			this._isSuccess = true;
			this._feedback.append("Start date set to "
					+ _task.getStartDateTimeString() + ". ");
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
