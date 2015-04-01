package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class FindAction extends Action {

	private LinkedList<Field> _fields;

	public FindAction(LinkedList<Field> fields) {
		
		this._isSuccess = false;
		this._feedback = new StringBuilder();
		this._type = ActionType.FIND;
		_fields = fields;
	}

	@Override
	public void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster) {

		if (_fields == null || _fields.isEmpty()) {
			System.out.println("no field");
			return;
		}

		if (_fields.getFirst().getFieldType() == FieldType.ALL) {
			copyList(workingListMaster, workingList);
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();
		copyList(workingListMaster, bufferList);

		for (Field field : _fields) {

			filterWorkingList(field, bufferList);

			if (bufferList.isEmpty()) {
				this._feedback.append("No matches found. ");
				return;
			}
		}

		copyList(bufferList, workingList);
		this._isSuccess = true;
		this._feedback.append(workingList.size() + " matches found. ");
	}

	private void filterWorkingList(Field field, LinkedList<Task> workingList) {

		FieldType fieldType = field.getFieldType();

		if (fieldType == null) {
			return;
		}

		switch (fieldType) {
		case DUE_DATE:
		case START_DATE:
			filterByDate(field, workingList);
			break;
		case PRIORITY:
			String priority = field.getPriority();
			if (priority == null) {
				return;
			}
			filterByPriority(priority, workingList);
			break;
		case COMPLETED:
			FieldCriteria yesNoC = field.getCriteria();
			if (yesNoC == null) {
				return;
			}
			filterByCompleteStatus(yesNoC, workingList);
			break;
		case OVERDUE:
			FieldCriteria yesNoO = field.getCriteria();
			if (yesNoO == null) {
				return;
			}
			filterByOverdueStatus(yesNoO, workingList);
			break;
		case TASK_NAME:
			String taskName = field.getTaskName();
			if (taskName == null || taskName.trim().isEmpty()) {
				return;
			}
			filterByName(taskName, workingList);
			break;
		default:
			return;
		}
	}

	private void filterByName(String taskName, LinkedList<Task> workingList) {
		if (taskName == null) {
			this._feedback.append("No task name keywords entered. ");
			return;
		}
		String keywords[] = taskName.split(" ");

		LinkedList<Object[]> tasksWithMatchScore = new LinkedList<Object[]>();

		for (Task currTask : workingList) {
			int matchScore = 0;
			for (String keyword : keywords) {
				keyword = keyword.trim();
				matchScore = matchKeywordScore(currTask, keyword);
				System.out.println(currTask.getName() + " " + keyword + " "
						+ matchScore);
			}
			if (matchScore != 0) {
				tasksWithMatchScore.add(new Object[] { currTask,
						Integer.valueOf(matchScore) });
			}
		}

		copyList(sortFoundTasksByMatchScore(tasksWithMatchScore), workingList);
		System.out.println(workingList.size());
		System.out.println(sortFoundTasksByMatchScore(tasksWithMatchScore)
				.size());
	}

	private LinkedList<Task> sortFoundTasksByMatchScore(
			LinkedList<Object[]> tasksWithMatchScore) {

		for (int i = tasksWithMatchScore.size() - 1; i >= 0; i--) {
			boolean isSorted = true;
			for (int j = 0; j < i; j++) {
				Object[] taskLeft = tasksWithMatchScore.get(j);
				Object[] taskRight = tasksWithMatchScore.get(j + 1);
				if ((int) taskLeft[1] < (int) taskRight[1]) {
					tasksWithMatchScore.set(j + 1, taskLeft);
					tasksWithMatchScore.set(j, taskRight);
					isSorted = false;
				}
			}
			if (isSorted) {
				break;
			}
		}

		LinkedList<Task> newWorkingList = new LinkedList<Task>();
		for (Object[] taskWithMatchScore : tasksWithMatchScore) {
			newWorkingList.add((Task) taskWithMatchScore[0]);
		}
		return newWorkingList;
	}

	private int matchKeywordScore(Task currTask, String keyword) {
		String[] taskNameWords = currTask.getName().split(" ");
		int totalScore = 0;
		for (String currWord : taskNameWords) {
			currWord = currWord.trim();
			if (currWord.contains(keyword)) {
				totalScore++;
			}
			if (currWord.equals(keyword)) {
				totalScore++;
			}
		}
		return totalScore;
	}

	private void filterByOverdueStatus(FieldCriteria criteria,
			LinkedList<Task> workingList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if ((currTask.getIsOverdue() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsOverdue() && criteria == FieldCriteria.NO)) {
				bufferList.add(currTask);
			}
		}
		copyList(bufferList, workingList);
		;
	}

	private void filterByCompleteStatus(FieldCriteria criteria,
			LinkedList<Task> workingList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if ((currTask.getIsCompleted() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsCompleted() && criteria == FieldCriteria.NO)) {
				bufferList.add(currTask);
			}
		}
		copyList(bufferList, workingList);
	}

	private void filterByPriority(String priority, LinkedList<Task> workingList) {
		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : workingList) {
			if (currTask.getPriority() != null
					&& currTask.getPriority().equalsIgnoreCase(priority)) {
				bufferList.add(currTask);
			}
		}
		copyList(bufferList, workingList);
		;
	}

	private void filterByDate(Field field, LinkedList<Task> workingList) {

		FieldType fieldType = field.getFieldType();
		FieldCriteria criteria = field.getCriteria();

		if (criteria == null) {
			_feedback.append("Date criteria not entered. ");
			return;
		}

		switch (criteria) {
		case BEFORE:
		case AFTER:
		case ON:
			Calendar date = field.getDate();
			filterBySingleDate(date, fieldType, criteria, workingList);
			break;
		case BETWEEN:
			Calendar[] dateRange = field.getDateRange();
			filterByDateRange(dateRange, fieldType, criteria, workingList);
			break;
		default:
			return;
		}
	}

	private void filterByDateRange(Calendar[] dateRange, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> workingList) {

		if (fieldType == null || criteria == null || dateRange == null) {
			return;
		}

		Calendar fromDate = dateRange[0];
		Calendar toDate = dateRange[1];

		fromDate.set(Calendar.HOUR, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR, 23);
		toDate.set(Calendar.MINUTE, 59);
		toDate.set(Calendar.SECOND, 59);

		if (fromDate.compareTo(toDate) > 0) {
			this._feedback
					.append("Invalid date range. Start date is later than end date. ");
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : workingList) {

			Calendar currTaskDate;

			switch (fieldType) {
			case START_DATE:
				currTaskDate = currTask.getStartDate();
				break;
			case DUE_DATE:
				currTaskDate = currTask.getDueDate();
				break;
			default:
				currTaskDate = null;
				break;
			}

			if (currTaskDate != null && currTaskDate.compareTo(fromDate) >= 0
					&& currTaskDate.compareTo(toDate) <= 0) {
				bufferList.add(currTask);
			}
		}

		copyList(bufferList, workingList);
	}

	private void filterBySingleDate(Calendar date, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> workingList) {

		if (fieldType == null || criteria == null) {
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : workingList) {

			Calendar currTaskDate;

			switch (fieldType) {
			case START_DATE:
				currTaskDate = currTask.getStartDate();
				break;
			case DUE_DATE:
				currTaskDate = currTask.getDueDate();
				break;
			default:
				currTaskDate = null;
				break;
			}

			if (currTaskDate != null) {
				switch (criteria) {
				case BEFORE:
					date.set(Calendar.HOUR_OF_DAY, 23);
					date.set(Calendar.MINUTE, 59);
					date.set(Calendar.SECOND, 59);
					if (currTaskDate.compareTo(date) <= 0) {
						bufferList.add(currTask);
					}
					break;
				case AFTER:
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					if (currTaskDate.compareTo(date) >= 0) {
						bufferList.add(currTask);
					}
					break;
				case ON:
					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					currTaskDate = new GregorianCalendar(
							currTaskDate.get(Calendar.YEAR),
							currTaskDate.get(Calendar.MONTH),
							currTaskDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					if (currTaskDate.equals(date)) {
						bufferList.add(currTask);
					}
					break;
				default:
					break;
				}
			}
		}

		copyList(bufferList, workingList);
	}

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

}
