package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.joda.time.chrono.AssembledChronology.Fields;

//@author A0102015H
public class FindAction extends Action {

	private LinkedList<Field> _fields;
	private boolean _findAll;
	private int _failCount;
	private String _taskName; 

	public FindAction(LinkedList<Field> fields, boolean findAll, String taskName) {
		this._isSuccess = false;
		this._feedback = new StringBuilder();
		this._type = ActionType.FIND;
		_taskName = taskName;
		_findAll = findAll;
		_fields = fields;
		_failCount = 0;
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		if (_findAll) {
			copyList(masterList, displayList);
			this._isSuccess = true;
			return;
		}

		if (_taskName == null && (_fields == null || _fields.isEmpty())) {
			System.out.println("No field entered. ");
			return;
		}

		LinkedList<Task> bufferList = new LinkedList<Task>();
		copyList(masterList, bufferList);
		
		if(_taskName != null) {
			filterByName(_taskName, bufferList);
			if (bufferList.isEmpty()) {
				this._feedback.append("No matches found. ");
				return;
			}
		}

		for (Field field : _fields) {
			filterdisplayList(field, bufferList);
			if (bufferList.isEmpty()) {
				this._feedback.append("No matches found. ");
				return;
			}
		}
		
		int totalUpdateSize = (_taskName == null) ? _fields.size() : _fields.size() + 1;

		if (_failCount == totalUpdateSize) {
			this._feedback.append("No matches found. ");
			return;
		} else {
			copyList(bufferList, displayList);
			this._isSuccess = true;
			this._feedback.append(displayList.size() + " matches found. ");
			new SortAction().execute(displayList, masterList);
		}
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
			_feedback.append("Invalid field. ");
			return;
		}
	}

	private void filterByName(String taskName, LinkedList<Task> displayList) {
		if (taskName == null || taskName.trim().isEmpty()) {
			this._feedback.append("No task name keywords entered. ");
			_failCount++;
			return;
		}
		String keywords[] = taskName.split(" ");

		LinkedList<Object[]> tasksWithMatchScore = new LinkedList<Object[]>();

		for (Task currTask : displayList) {
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

		copyList(sortFoundTasksByMatchScore(tasksWithMatchScore), displayList);
		System.out.println(displayList.size());
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

		LinkedList<Task> newdisplayList = new LinkedList<Task>();
		for (Object[] taskWithMatchScore : tasksWithMatchScore) {
			newdisplayList.add((Task) taskWithMatchScore[0]);
		}
		return newdisplayList;
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
			LinkedList<Task> displayList) {
		
		if (criteria != FieldCriteria.YES && criteria != FieldCriteria.NO) {
			_feedback.append("Overdue criteria not entered. ");
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
			_feedback.append("Completed criteria not entered. ");
			_failCount++;
			return;
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
			_feedback.append("Priority level not entered. ");
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
			_feedback.append("Date criteria not entered. ");
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
			return;
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
			this._feedback
					.append("Invalid date range. Start date is later than end date. ");
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
					System.out.println(date.getTime().toString());
					System.out.println(currTaskDate.getTime().toString());
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
