package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

//@author A0102015H
public class FindAction extends Action {

	private static final String SPACE = " ";
	private LinkedList<Field> _fields;

	private boolean _findAll;
	private boolean _shouldShowAllCompleted;

	private int _failCount;

	private String _taskName;

	private SortAction _defaultSort;

	public FindAction(LinkedList<Field> fields, boolean findAll, String taskName) {

		_isSuccess = false;
		_feedback = new StringBuilder();
		_type = ActionType.FIND;
		_taskName = taskName;
		_findAll = findAll;
		_fields = fields;
		_failCount = 0;
		_shouldShowAllCompleted = false;
		_defaultSort = new SortAction();
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		if (_findAll) {
			copyList(masterList, displayList);
			_isSuccess = true;
			return;
		}

		if (_taskName == null && (_fields == null || _fields.isEmpty())) {
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();
		copyList(masterList, bufferList);

		if (_taskName != null) {

			filterByName(_taskName, bufferList);

			if (bufferList.isEmpty()) {
				_feedback.append(GlobalConstants.NO_MATCHES_FOUND);
				return;
			}
		}

		for (Field field : _fields) {

			filterdisplayList(field, bufferList);

			if (bufferList.isEmpty()) {
				_feedback.append(GlobalConstants.NO_MATCHES_FOUND);
				return;
			}
		}

		int totalUpdateSize = (_taskName == null) ? _fields.size() : _fields
				.size() + 1;

		if (_failCount == totalUpdateSize) {

			_feedback.append((GlobalConstants.NO_MATCHES_FOUND));
			return;

		} else {

			copyList(bufferList, displayList);

			_isSuccess = true;
			_feedback.append(String.format(GlobalConstants.MATCHES_FOUND,
					displayList.size()));

			LOGGER.info(GlobalConstants.SORTING_DISPLAY_LIST);
			_defaultSort.execute(displayList, masterList);
		}
	}

	public boolean shouldShowAllCompleted() {
		return _shouldShowAllCompleted;
	}

	private void filterdisplayList(Field field, LinkedList<Task> displayList) {

		FieldType fieldType = field.getFieldType();

		if (fieldType == null) {
			_failCount++;
			return;
		}

		switch (fieldType) {
		case DUE_DATE:
		case START_DATE:
			filterByDate(field, displayList);
			break;
		case PRIORITY:
			String priority = field.getPriority();
			filterByPriority(priority, displayList);
			break;
		case COMPLETED:
			FieldCriteria yesNoC = field.getCriteria();
			filterByCompleteStatus(yesNoC, displayList);
			break;
		case OVERDUE:
			FieldCriteria yesNoO = field.getCriteria();
			filterByOverdueStatus(yesNoO, displayList);
			break;
		default:
			_feedback.append(GlobalConstants.INVALID_FIELD);
			return;
		}
	}

	private void filterByName(String taskName, LinkedList<Task> displayList) {

		if (taskName == null || taskName.trim().isEmpty()) {

			_feedback.append(GlobalConstants.NO_KEYWORDS);
			_failCount++;
			return;
		}

		String keywords[] = taskName.split(SPACE);

		LinkedList<Object[]> tasksWithMatchScore = new LinkedList<Object[]>();

		for (Task currTask : displayList) {

			int matchScore = 0;

			for (String keyword : keywords) {

				keyword = keyword.trim();
				matchScore += matchKeywordScore(currTask, keyword);
			}

			if (matchScore != 0) {

				tasksWithMatchScore.add(new Object[] { currTask,
						Integer.valueOf(matchScore) });
			}
		}

		copyList(sortFoundTasksByMatchScore(tasksWithMatchScore), displayList);
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

		LinkedList<Task> newdisplayList = new LinkedList<Task>();

		for (Object[] taskWithMatchScore : tasksWithMatchScore) {
			newdisplayList.add((Task) taskWithMatchScore[0]);
		}

		return newdisplayList;
	}

	private int matchKeywordScore(Task currTask, String keyword) {

		keyword = keyword.toLowerCase();

		String[] taskNameWords = currTask.getName().split(SPACE);

		int totalScore = 0;

		for (String currWord : taskNameWords) {

			currWord = currWord.trim().toLowerCase();

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
			LinkedList<Task> displayList) {

		if (criteria != FieldCriteria.YES && criteria != FieldCriteria.NO) {
			_feedback.append(GlobalConstants.NO_OVERDUE_CRITERIA);
			_failCount++;
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : displayList) {

			if ((currTask.getIsOverdue() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsOverdue() && criteria == FieldCriteria.NO)) {

				bufferList.add(currTask);
			}
		}

		copyList(bufferList, displayList);
	}

	private void filterByCompleteStatus(FieldCriteria criteria,
			LinkedList<Task> displayList) {

		if (criteria != FieldCriteria.YES && criteria != FieldCriteria.NO) {
			_feedback.append(GlobalConstants.NO_COMPLETE_CRITERIA);
			_failCount++;
			return;
		}

		if (criteria == FieldCriteria.YES) {
			_shouldShowAllCompleted = true;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : displayList) {

			if ((currTask.getIsCompleted() && criteria == FieldCriteria.YES)
					|| (!currTask.getIsCompleted() && criteria == FieldCriteria.NO)) {

				bufferList.add(currTask);
			}
		}

		copyList(bufferList, displayList);
	}

	private void filterByPriority(String priority, LinkedList<Task> displayList) {

		if (priority == null) {
			_feedback.append(GlobalConstants.NO_PRIORITY_LEVEL);
			_failCount++;
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();
		for (Task currTask : displayList) {

			if (currTask.getPriority() != null
					&& currTask.getPriority().equalsIgnoreCase(priority)) {

				bufferList.add(currTask);
			}
		}

		copyList(bufferList, displayList);
	}

	private void filterByDate(Field field, LinkedList<Task> displayList) {

		FieldType fieldType = field.getFieldType();
		FieldCriteria criteria = field.getCriteria();

		if (criteria == null) {
			_feedback.append(GlobalConstants.NO_DATE_CRITERIA);
			_failCount++;
			return;
		}

		switch (criteria) {
		case BEFORE:
		case AFTER:
		case ON:
			Calendar date = field.getDate();
			filterBySingleDate(date, fieldType, criteria, displayList);
			break;
		case BETWEEN:
			Calendar[] dateRange = field.getDateRange();
			filterByDateRange(dateRange, fieldType, criteria, displayList);
			break;
		default:
			break;
		}
	}

	private void filterByDateRange(Calendar[] dateRange, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> displayList) {

		if (dateRange == null) {
			_failCount++;
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
			_feedback.append(GlobalConstants.INVALID_DATE_RANGE);
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : displayList) {

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

		copyList(bufferList, displayList);
	}

	private void filterBySingleDate(Calendar date, FieldType fieldType,
			FieldCriteria criteria, LinkedList<Task> displayList) {

		LinkedList<Task> bufferList = new LinkedList<Task>();

		for (Task currTask : displayList) {

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
					date.set(Calendar.MILLISECOND, 999);

					if (currTaskDate.compareTo(date) <= 0) {
						bufferList.add(currTask);
					}

					break;

				case AFTER:

					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					date.set(Calendar.MILLISECOND, 0);

					if (currTaskDate.compareTo(date) >= 0) {
						bufferList.add(currTask);
					}

					break;

				case ON:

					date.set(Calendar.HOUR_OF_DAY, 0);
					date.set(Calendar.MINUTE, 0);
					date.set(Calendar.SECOND, 0);
					date.set(Calendar.MILLISECOND, 0);

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

		copyList(bufferList, displayList);
	}

	private <E> void copyList(LinkedList<E> fromList, LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

}
