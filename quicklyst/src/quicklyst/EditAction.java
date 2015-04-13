package quicklyst;

import java.util.Calendar;
import java.util.LinkedList;

//@author A0102015H
public class EditAction extends Action {

	private static final String STRING_L = "L";
	private static final String STRING_M = "M";
	private static final String STRING_H = "H";
	
	private int _taskIndex;
	private LinkedList<Field> _fields;
	private Task _task;
	private SortAction _defaultSort;
	private String _taskName;

	public EditAction(int taskNumber, LinkedList<Field> fields, String taskName) {

		_isSuccess = false;
		_feedback = new StringBuilder();
		_type = ActionType.EDIT;
		_taskName = taskName;
		_defaultSort = new SortAction();

		if (taskNumber != 0) {
			_taskIndex = taskNumber - 1;
		} else {
			_taskIndex = -1;
		}

		_fields = fields;
	}

	/* For add action */
	public EditAction(Task task, LinkedList<Field> fields) {

		_isSuccess = false;
		_feedback = new StringBuilder();
		_type = ActionType.EDIT;
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
			_feedback.append(GlobalConstants.TASK_NO_OUT_OF_RANGE);
		}

		if (_isSuccess) {
			LOGGER.info(GlobalConstants.SORTING_DISPLAY_LIST);
			_task.setLastUpdated(Calendar.getInstance());
			_defaultSort.execute(displayList, masterList);
		}
	}

	private void execute() {
		
		if (_taskName != null) {
			updateTaskName();
		}

		for (Field field : _fields) {
			FieldType fieldType = field.getFieldType();

			switch (fieldType) {
			case START_DATE:
				updateStartDate(field);
				break;
			case DUE_DATE:
				updateDueDate(field);
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
		
		assert field.getPriority().equalsIgnoreCase(STRING_H)
				|| field.getPriority().equalsIgnoreCase(STRING_M)
				|| field.getPriority().equalsIgnoreCase(STRING_L);

		if (field.shouldClearPriority()) {

			_task.setPriority((String) null);
			_isSuccess = true;
			_feedback.append(GlobalConstants.PRIORITY_CLEARED);

		} else if (field.getPriority() == null) {

			_isSuccess = false;

		} else {

			_task.setPriority(field.getPriority());
			_isSuccess = true;
			_feedback.append(String.format(GlobalConstants.PRIORITY_SET,
					field.getPriority()));
		}
	}

	private void updateTaskName() {
		
		assert !_taskName.isEmpty();
		_task.setName(_taskName);
		_feedback.append(String.format(GlobalConstants.TASK_NAME_SET,
				_taskName));
		_isSuccess = true;
	}

	private void updateDueDate(Field field) {
		
		if (field.shouldClearDate()) {
			_task.setDueDate((Calendar) null);
			_task.setHasDueTime(false);
			_isSuccess = true;
			_feedback.append(GlobalConstants.DUE_DATE_CLEARED);
		} else if (field.getDate() == null) {
			return;
		} else {
			Calendar newDate = (Calendar) field.getDate().clone();
			boolean hasDueTime = calibrateDueDate(field, newDate);
			setDueDate(newDate, hasDueTime, field.isTimeParsed());
		}
	}

	private void setDueDate(Calendar newDate, boolean hasDueTime,
			boolean isTimeParsed) {
		
		if (_task.getStartDate() != null
				&& newDate.compareTo(_task.getStartDate()) < 0) {
			_feedback.append(GlobalConstants.DUE_SMALLER_THAN_START);
		} else {

			_task.setDueDate(newDate);
			_task.setHasDueTime(hasDueTime);
			_isSuccess = true;
			_feedback.append(String.format(GlobalConstants.DUE_DATE_SET,
					_task.getDueDateString()));
		}
	}

	private boolean calibrateDueDate(Field field, Calendar newDate) {
		boolean hasDueTime;

		if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getDueDate() != null) {

			matchTaskDueDate(newDate);
			hasDueTime = true;

		} else if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getStartDate() != null) {

			matchTaskStartDate(newDate);
			hasDueTime = true;

			if (!field.isDateParsed() && _task.getStartDate() != null
					&& newDate.compareTo(_task.getStartDate()) < 0) {
				newDate.add(Calendar.DAY_OF_MONTH, 1);
			}

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
			_isSuccess = true;
			_feedback.append(GlobalConstants.START_DATE_CLEARED);
			
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
			
			_feedback.append(GlobalConstants.START_BIGGER_THAN_DUE);
			
		} else {

			_task.setStartDate(newDate);
			_task.setHasStartTime(hasStartTime);
			_isSuccess = true;
			_feedback
					.append(String.format(GlobalConstants.START_DATE_SET,
							_task.getStartDateString()));
		}
	}

	private boolean calibrateStartDate(Field field, Calendar newDate) {
		boolean hasStartTime;

		if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getStartDate() != null) {

			matchTaskStartDate(newDate);
			hasStartTime = true;

		} else if (!field.isDateParsed() && field.isTimeParsed()
				&& _task.getDueDate() != null) {

			matchTaskDueDate(newDate);
			hasStartTime = true;

			if (!field.isDateParsed() && _task.getDueDate() != null
					&& newDate.compareTo(_task.getDueDate()) > 0) {
				newDate.add(Calendar.DAY_OF_MONTH, -1);
			}

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
}
