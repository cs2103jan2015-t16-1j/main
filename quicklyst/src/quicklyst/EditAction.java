package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class EditAction extends Action {

	private int _taskIndex;
	private LinkedList<Field> _fields;
	private Task _task;
	private SortAction _defaultSort;

	public EditAction(int taskNumber, LinkedList<Field> fields) {

		this._isSuccess = false;
		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;
		_defaultSort = new SortAction();

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}

		_fields = fields;
	}

	public EditAction(Task task, LinkedList<Field> fields) {

		this._isSuccess = false;
		this._feedback = new StringBuilder();
		this._type = ActionType.EDIT;
		_task = task;
		_fields = fields;
		_defaultSort = new SortAction();
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		if (_task != null) {
			execute();
		} else if (isTaskIndexInRange(displayList)) {
			_task = displayList.get(_taskIndex);
			execute();
		} else {
			this._feedback.append("Task # out of range. ");
		}
		
		System.out.println(this._isSuccess);

		if (this._isSuccess) {
			_defaultSort.execute(displayList, masterList);
			System.out.println("sorted");
		}
	}

	private void execute() {
		for (Field field : _fields) {
			FieldType fieldType = field.getFieldType();
			switch (fieldType) {
			case TASK_NAME:
				updateTaskName(field);
				break;
			case START_DATE:
				updateStartDate(field);
				/*
				 * if (_task.getStartDate() != null) { updateStartDate(field); }
				 * else { addStartDate(field); }
				 */
				break;

			case DUE_DATE:
				updateDueDate(field);
				/*
				 * if (_task.getDueDate() != null) { updateDueDate(field); }
				 * else { addDueDate(field); }
				 */
				break;

			case REMINDER:
				/*
				 * not implemented yet _task.setReminder(field.getDate());
				 * this._feedback.append("Reminder set to " +
				 * _task.getReminderDateString() + ". ");
				 */
				break;
			case PRIORITY:
				updatePriority(field);
				break;
			default:
				break;
			}
		}
	}

	private void updatePriority(Field field) {
		if (field.shouldClearPriority()) {
			_task.setPriority((String) null);
			this._isSuccess = true;
			this._feedback.append("Priority cleared. ");
		} else {
			_task.setPriority(field.getPriority());
			this._isSuccess = true;
			this._feedback.append("Priority set to \"" + field.getPriority()
					+ "\". ");
		}
	}

	private void updateTaskName(Field field) {
		_task.setName(field.getTaskName());
		this._feedback.append("Task name set to \"" + field.getTaskName()
				+ "\". ");
		this._isSuccess = true;
	}

	private void updateDueDate(Field field) {
		if (field.shouldClearDate()) {
			_task.setDueDate((Calendar) null);
			_task.setHasDueTime(false);
			this._isSuccess = true;
			this._feedback.append("Due date cleared. ");
		} else if (field.getDate() == null) {
			return;
		} else {
			Calendar newDate = (Calendar) field.getDate().clone();
			boolean hasDueTime = calibrateDueDate(field, newDate);
			setDueDate(newDate, hasDueTime);
		}
	}

	private void setDueDate(Calendar newDate, boolean hasDueTime) {
		if (_task.getStartDate() != null
				&& newDate.compareTo(_task.getStartDate()) < 0) {
			this._feedback
					.append("Due date/time entered is smaller than start date/time of task. ");
		} else {

			_task.setDueDate(newDate);
			_task.setHasDueTime(hasDueTime);
			this._isSuccess = true;
			this._feedback.append("Due date set to " + _task.getDueDateString()
					+ ". ");
		}
	}

	private boolean calibrateDueDate(Field field, Calendar newDate) {
		boolean hasDueTime;
		if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getStartDate() != null) {

			matchTaskStartDate(newDate);
			hasDueTime = true;

		} else if(field.isTimeParsed() && _task.getDueDate()!= null) {
			
			matchTaskDueDate(newDate);
			hasDueTime = true;
			
		} else if (!field.isTimeParsed()) {

			configureToMaxTime(newDate);
			hasDueTime = false;

		} else {

			hasDueTime = true;

		}
		return hasDueTime;
	}

	private void updateStartDate(Field field) {
		if (field.shouldClearDate()) {
			_task.setStartDate((Calendar) null);
			_task.setHasStartTime(false);
			this._isSuccess = true;
			this._feedback.append("Start date cleared. ");
		} else if (field.getDate() == null) {
			return;
		} else {
			Calendar newDate = (Calendar) field.getDate().clone();
			boolean hasStartTime = calibrateStartDate(field, newDate);
			setStartDate(newDate, hasStartTime);
		}
	}

	private void setStartDate(Calendar newDate, boolean hasStartTime) {
		if (_task.getDueDate() != null
				&& newDate.compareTo(_task.getDueDate()) > 0) {
			this._feedback
					.append("Start date/time entered is bigger than due date/time of task. ");
		} else {

			_task.setStartDate(newDate);
			_task.setHasStartTime(hasStartTime);
			this._isSuccess = true;
			this._feedback.append("Start date set to "
					+ _task.getStartDateString() + ". ");
		}
	}

	private boolean calibrateStartDate(Field field, Calendar newDate) {
		boolean hasStartTime;
		if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getDueDate() != null) {

			matchTaskDueDate(newDate);
			hasStartTime = true;

		} else if(field.isTimeParsed() && _task.getStartDate()!= null) {
			
			matchTaskStartDate(newDate);
			hasStartTime = true;
			
		} else if (!field.isTimeParsed()) {

			configureToMinTime(newDate);
			hasStartTime = false;

		} else {

			hasStartTime = true;

		}
		return hasStartTime;
	}

	private boolean isTaskIndexInRange(LinkedList<Task> displayList) {
		if (_taskIndex < 0 || _taskIndex >= displayList.size()) {
			return false;
		} else {
			return true;
		}
	}

	private void configureToMinTime(Calendar newDate) {
		newDate.set(Calendar.HOUR_OF_DAY, 0);
		newDate.set(Calendar.MINUTE, 0);
		newDate.set(Calendar.SECOND, 0);
	}

	private void configureToMaxTime(Calendar newDate) {
		newDate.set(Calendar.HOUR_OF_DAY, 23);
		newDate.set(Calendar.MINUTE, 59);
		newDate.set(Calendar.SECOND, 59);
	}

	private void matchTaskDueDate(Calendar newDate) {
		newDate.set(Calendar.YEAR, _task.getDueDate().get(Calendar.YEAR));
		newDate.set(Calendar.MONTH, _task.getDueDate().get(Calendar.MONTH));
		newDate.set(Calendar.DAY_OF_MONTH,
				_task.getDueDate().get(Calendar.DAY_OF_MONTH));
	}

	private void matchTaskStartDate(Calendar newDate) {
		newDate.set(Calendar.YEAR, _task.getStartDate().get(Calendar.YEAR));
		newDate.set(Calendar.MONTH, _task.getStartDate().get(Calendar.MONTH));
		newDate.set(Calendar.DAY_OF_MONTH,
				_task.getStartDate().get(Calendar.DAY_OF_MONTH));
	}

	/*
	 * old implementation private void updateStartDate(Field field) { Calendar
	 * newDate = new GregorianCalendar();
	 * 
	 * if (field.isDateParsed() && field.isTimeParsed()) { addStartDate(field);
	 * return; } else if (field.isDateParsed()) {
	 * 
	 * newDate.set(Calendar.YEAR, field.getDate().get(Calendar.YEAR));
	 * newDate.set(Calendar.MONTH, field.getDate().get(Calendar.MONTH));
	 * newDate.set(Calendar.DAY_OF_MONTH,
	 * field.getDate().get(Calendar.DAY_OF_MONTH));
	 * 
	 * newDate.set(Calendar.HOUR_OF_DAY,
	 * _task.getStartDate().get(Calendar.HOUR_OF_DAY));
	 * newDate.set(Calendar.MINUTE, _task.getStartDate().get(Calendar.MINUTE));
	 * 
	 * } else if (field.isTimeParsed()) { newDate.set(Calendar.HOUR_OF_DAY,
	 * field.getDate().get(Calendar.HOUR_OF_DAY)); newDate.set(Calendar.MINUTE,
	 * field.getDate().get(Calendar.MINUTE));
	 * 
	 * matchTaskStartDate(newDate);
	 * 
	 * } else { return; }
	 * 
	 * if (_task.getDueDate() != null && newDate.compareTo(_task.getDueDate()) >
	 * 0) { this._feedback
	 * .append("Start date/time entered is bigger than due date/time of task. "
	 * ); } else { _task.setStartDate(newDate); if (field.isTimeParsed()) {
	 * _task.setHasStartTime(true); } this._isSuccess = true;
	 * this._feedback.append("Start date set to " + _task.getStartDateString() +
	 * ". "); } }
	 */

	/*
	 * older implementation
	 * 
	 * private void updateDueDate(Field field) {
	 * 
	 * Calendar newDate = (Calendar) field.getDate().clone();
	 * 
	 * if (field.isDateParsed() && field.isTimeParsed()) {
	 * 
	 * addDueDate(field); return;
	 * 
	 * } else if (!field.isDateParsed() && field.isTimeParsed() &&
	 * _task.getStartDate() != null) {
	 * 
	 * 
	 * 
	 * }
	 * 
	 * else if (field.isDateParsed()) {
	 * 
	 * } else if (field.isTimeParsed()) { newDate.set(Calendar.HOUR_OF_DAY,
	 * field.getDate().get(Calendar.HOUR_OF_DAY)); newDate.set(Calendar.MINUTE,
	 * field.getDate().get(Calendar.MINUTE));
	 * 
	 * newDate.set(Calendar.YEAR, _task.getDueDate().get(Calendar.YEAR));
	 * newDate.set(Calendar.MONTH, _task.getDueDate().get(Calendar.MONTH));
	 * newDate.set(Calendar.DAY_OF_MONTH,
	 * _task.getDueDate().get(Calendar.DAY_OF_MONTH)); } else { return; }
	 * 
	 * if (_task.getStartDate() != null &&
	 * newDate.compareTo(_task.getStartDate()) < 0) { this._feedback
	 * .append("Due date/time entered is smaller than start date/time of task. "
	 * ); } else { _task.setDueDate(newDate); if (field.isTimeParsed()) {
	 * _task.setHasDueTime(true); } this._isSuccess = true;
	 * this._feedback.append("Due date set to " + _task.getDueDateString() +
	 * ". ");
	 * 
	 * }
	 * 
	 * }
	 */

}
