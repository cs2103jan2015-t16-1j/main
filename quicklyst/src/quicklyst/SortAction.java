package quicklyst;

import java.util.LinkedList;

public class SortAction extends Action {

	private LinkedList<Field> _fields;

	public SortAction(LinkedList<Field> fields) {
		
		this._isSuccess = true;
		this._feedback = new StringBuilder();
		this._type = ActionType.SORT;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {
		execute(workingList);
	}

	private void execute(LinkedList<Task> workingList) {

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
				sortByDueDate(order, workingList);
				break;
			case DURATION:
				sortByDuration(order, workingList);
				break;
			case PRIORITY:
				sortByPriority(order, workingList);
				break;
			default:
				break;
			}
		}
	}

	private void sortByDuration(FieldCriteria order,
			LinkedList<Task> workingList) {
		LinkedList<Task> tasksWithNoDuration = new LinkedList<Task>();
		for (int i = 0; i < workingList.size(); i++) {
			if (workingList.get(i).getDuration() == -1) {
				Task removedTask = workingList.remove(i);
				tasksWithNoDuration.add(removedTask);
				i--;
			}
		}

		for (int i = workingList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = workingList.get(j);
				Task taskRight = workingList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getDuration() > taskRight.getDuration()) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getDuration() < taskRight.getDuration()) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
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
		tasksWithNoDuration.addAll(workingList);
		copyList(tasksWithNoDuration, workingList);
	}

	private void sortByPriority(FieldCriteria order,
			LinkedList<Task> workingList) {
		
		LinkedList<Task> tasksWithNoPriority = new LinkedList<Task>();
		for (int i = 0; i < workingList.size(); i++) {
			if (workingList.get(i).getPriorityInt() == 0) {
				Task removedTask = workingList.remove(i);
				tasksWithNoPriority.add(removedTask);
				i--;
			}
		}

		for (int i = workingList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = workingList.get(j);
				Task taskRight = workingList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getPriorityInt() > taskRight.getPriorityInt()) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getPriorityInt() < taskRight.getPriorityInt()) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
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
		tasksWithNoPriority.addAll(workingList);
		copyList(tasksWithNoPriority, workingList);
	}

	private void sortByDueDate(FieldCriteria order, LinkedList<Task> workingList) {

		LinkedList<Task> tasksWithNoDueDate = new LinkedList<Task>();
		for (int i = 0; i < workingList.size(); i++) {
			if (workingList.get(i).getDueDate() == null) {
				Task removedTask = workingList.remove(i);
				tasksWithNoDueDate.add(removedTask);
				i--;
			}
		}

		for (int i = workingList.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Task taskLeft = workingList.get(j);
				Task taskRight = workingList.get(j + 1);
				switch (order) {
				case ASCEND:
					if (taskLeft.getDueDate().compareTo(taskRight.getDueDate()) > 0) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
						isSorted = false;
					}
					break;

				case DESCEND:
					if (taskLeft.getDueDate().compareTo(taskRight.getDueDate()) < 0) {
						workingList.set(j + 1, taskLeft);
						workingList.set(j, taskRight);
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
		tasksWithNoDueDate.addAll(workingList);
		copyList(tasksWithNoDueDate, workingList);
	}

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}
}
