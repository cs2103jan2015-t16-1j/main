package quicklyst;

import java.util.LinkedList;

public class SortAction extends Action {

	private LinkedList<Field> _fields;

	/** default sort action- due date descending **/
	public SortAction() {
		this._isSuccess = true;
		this._type = ActionType.SORT;
		_fields = new LinkedList<Field>();
		_fields.add(new Field(FieldType.DUE_DATE, FieldCriteria.ASCEND));
		_fields.add(new Field(FieldType.PRIORITY, FieldCriteria.DESCEND));
	}

	public SortAction(LinkedList<Field> fields) {

		this._isSuccess = true;
		this._feedback = new StringBuilder();
		this._type = ActionType.SORT;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		execute(displayList);
		if (this._isSuccess) {
			execute(masterList);
		}
	}

	private void execute(LinkedList<Task> displayList) {

		if (_fields == null || _fields.isEmpty()) {
			System.out.println("No field entered. ");
			this._isSuccess = false;
			return;
		}

		for (int i = _fields.size() - 1; i >= 0; i--) {

			Field field = _fields.get(i);
			FieldType fieldType = field.getFieldType();

			if (fieldType != FieldType.DUE_DATE
					&& fieldType != FieldType.PRIORITY
					&& fieldType != FieldType.DURATION) {
				this._isSuccess = false;
				this._feedback.append("Invalid field type. ");
				return;
			}

			FieldCriteria order = field.getCriteria();

			if (order != FieldCriteria.ASCEND && order != FieldCriteria.DESCEND) {
				this._isSuccess = false;
				this._feedback.append("Invalid order. ");
				return;
			}

			switch (field.getFieldType()) {
			case DUE_DATE:
				sortByDueDate(order, displayList);
				break;
			case DURATION:
				sortByDuration(order, displayList);
				break;
			case PRIORITY:
				sortByPriority(order, displayList);
				break;
			default:
				break;
			}
		}
	}

	private void sortByDuration(FieldCriteria order,
			LinkedList<Task> displayList) {
		LinkedList<Task> tasksWithNoDuration = new LinkedList<Task>();
		for (int i = 0; i < displayList.size(); i++) {
			if (displayList.get(i).getDuration() == -1) {
				Task removedTask = displayList.remove(i);
				tasksWithNoDuration.add(removedTask);
				i--;
			}
		}

		for (int i = displayList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = displayList.get(j);
				Task taskRight = displayList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getDuration() > taskRight.getDuration()) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getDuration() < taskRight.getDuration()) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;

				default:
					break;
				}
			}
			if (isSorted) {
				break;
			}
		}
		displayList.addAll(tasksWithNoDuration);
	}

	private void sortByPriority(FieldCriteria order,
			LinkedList<Task> displayList) {

		LinkedList<Task> tasksWithNoPriority = new LinkedList<Task>();
		for (int i = 0; i < displayList.size(); i++) {
			if (displayList.get(i).getPriorityInt() == 0) {
				Task removedTask = displayList.remove(i);
				tasksWithNoPriority.add(removedTask);
				i--;
			}
		}

		for (int i = displayList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = displayList.get(j);
				Task taskRight = displayList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getPriorityInt() > taskRight.getPriorityInt()) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getPriorityInt() < taskRight.getPriorityInt()) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;

				default:
					break;
				}

			}
			if (isSorted) {
				break;
			}
		}
		displayList.addAll(tasksWithNoPriority);
	}

	private void sortByDueDate(FieldCriteria order, LinkedList<Task> displayList) {

		LinkedList<Task> tasksWithNoDueDate = new LinkedList<Task>();
		for (int i = 0; i < displayList.size(); i++) {
			if (displayList.get(i).getDueDate() == null) {
				Task removedTask = displayList.remove(i);
				tasksWithNoDueDate.add(removedTask);
				i--;
			}
		}

		for (int i = displayList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = displayList.get(j);
				Task taskRight = displayList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getDueDate().compareTo(taskRight.getDueDate()) > 0) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getDueDate().compareTo(taskRight.getDueDate()) < 0) {
						displayList.set(j + 1, taskLeft);
						displayList.set(j, taskRight);
						isSorted = false;
					}
					break;
				default:
					break;
				}
			}
			if (isSorted) {
				break;
			}
		}
		displayList.addAll(tasksWithNoDueDate);
	}
}
