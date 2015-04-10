package quicklyst;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

//@author A0102015H
public class Task {

	private static final String FORMAT_DATE_NO_TIME = "dd/MM/yyyy";
	private static final String FORMAT_DATE_WITH_TIME = "dd/MM/yyyy HHmm'H'";
	private static final String STRING_PRIORITY_HIGH = "H";
	private static final String STRING_PRIORITY_MEDIUM = "M";
	private static final String STRING_PRIORITY_LOW = "L";

	private static final int NUM_PRIORITY_HIGH = 3;
	private static final int NUM_PRIORITY_MEDIUM = 2;
	private static final int NUM_PRIORITY_LOW = 1;

	private String _name;
	private String _description;
	private String _priority;
	private String _googleID;
	private Calendar _startDate;
	private Calendar _dueDate;
	private Calendar _lastUpdated;
	private boolean _isCompleted;
	private boolean _isOverdue;
	private boolean _shouldSync;
	private boolean _hasStartTime;
	private boolean _hasDueTime;

	/* Constructors */
	public Task(String name) {
		_name = new String(name);
		_isCompleted = false;
		_hasDueTime = false;
		_hasStartTime = false;
		_lastUpdated = Calendar.getInstance();
	}

	/* Mutators */
	public void setName(String name) {
		_name = name;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setPriority(String priority) {
		_priority = priority;
	}

	public void setGoogleID(String ID) {
		_googleID = ID;
	}

	public void setStartDate(Calendar startDate) {
		_startDate = startDate;
	}

	public void setDueDate(Calendar dueDate) {
		_dueDate = dueDate;
		updateIsOverdue();
	}

	public void setHasStartTime(boolean hasTime) {
		_hasStartTime = hasTime;
	}

	public void setHasDueTime(boolean hasTime) {
		_hasDueTime = hasTime;
	}

	public void setIsCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}
	
	public void setLastUpdated(Calendar lastUpdated) {
		_lastUpdated = lastUpdated;
	}

	public void toggleCompleted() {
		if (_isCompleted) {
			_isCompleted = false;
		} else {
			_isCompleted = true;
		}
	}

	public void updateIsOverdue() {
		Calendar today = new GregorianCalendar();

		if (_dueDate == null || _isCompleted == true
				|| _dueDate.compareTo(today) > 0) {
			_isOverdue = false;
		} else {
			_isOverdue = true;
		}
	}

	public void setShouldSync() {
		_shouldSync = true;
	}

	public void setShouldNotSync() {
		_shouldSync = false;
	}

	public void setIsShouldSync(boolean shouldSync) {
		_shouldSync = shouldSync;
	}

	/* Accessors */
	public String getName() {
		return _name;
	}

	public String getDescription() {
		return _description;
	}

	public String getPriority() {
		if (_priority == null) {
			return null;
		} else if (_priority.equalsIgnoreCase(STRING_PRIORITY_LOW)) {
			return STRING_PRIORITY_LOW;
		} else if (_priority.equalsIgnoreCase(STRING_PRIORITY_MEDIUM)) {
			return STRING_PRIORITY_MEDIUM;
		} else if (_priority.equalsIgnoreCase(STRING_PRIORITY_HIGH)) {
			return STRING_PRIORITY_HIGH;
		} else {
			return null;
		}
	}

	public String getGoogleID() {
		return _googleID;
	}

	public int getPriorityInt() {
		if (_priority == null) {
			return 0;
		}
		if (_priority.equalsIgnoreCase(STRING_PRIORITY_LOW)) {
			return NUM_PRIORITY_LOW;
		}
		if (_priority.equalsIgnoreCase(STRING_PRIORITY_MEDIUM)) {
			return NUM_PRIORITY_MEDIUM;
		}
		if (_priority.equalsIgnoreCase(STRING_PRIORITY_HIGH)) {
			return NUM_PRIORITY_HIGH;
		}
		return 0;
	}

	public Calendar getStartDate() {
		return _startDate;
	}

	public Calendar getDueDate() {
		return _dueDate;
	}

	public String getStartDateString() {
		SimpleDateFormat sdf;
		if (_startDate == null) {
			return "no start date";
		}
		
		if(_hasStartTime) {
			sdf = new SimpleDateFormat(FORMAT_DATE_WITH_TIME);
			return sdf.format(_startDate.getTime());
		} else {
			sdf = new SimpleDateFormat(FORMAT_DATE_NO_TIME);
			return sdf.format(_startDate.getTime());
		}
	}

	public String getDueDateString() {
		SimpleDateFormat sdf;
		if (_dueDate == null) {
			return "no due date";
		}
		
		if(_hasDueTime) {
			sdf = new SimpleDateFormat(FORMAT_DATE_WITH_TIME);
			return sdf.format(_dueDate.getTime());
		} else {
			sdf = new SimpleDateFormat(FORMAT_DATE_NO_TIME);
			return sdf.format(_dueDate.getTime());
		}
	}

	public boolean getHasStartTime() {
		return _hasStartTime;
	}

	public boolean getHasDueTime() {
		return _hasDueTime;
	}

	public boolean getIsCompleted() {
		return _isCompleted;
	}

	public boolean getIsOverdue() {
		updateIsOverdue();
		return _isOverdue;
	}

	public boolean getShouldSync() {
		return _shouldSync;
	}

	public int getDuration() {
		if (_dueDate == null || _startDate == null
				|| _dueDate.compareTo(_startDate) < 0) {
			return -1;
		}
		Calendar _dueDateDummy = new GregorianCalendar(
				_dueDate.get(Calendar.YEAR), _dueDate.get(Calendar.MONTH),
				_dueDate.get(Calendar.DAY_OF_MONTH));
		Calendar _startDateDummy = new GregorianCalendar(
				_startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH),
				_startDate.get(Calendar.DAY_OF_MONTH));
		int duration = 0;
		while (!_dueDateDummy.equals(_startDateDummy)) {
			_startDateDummy.add(Calendar.DAY_OF_MONTH, 1);
			duration++;
		}
		return duration;
	}
	
	public Calendar getLastUpdated() {
		return _lastUpdated;
	}

	/* Other methods */

	public Task clone() {
		Task clonedTask = new Task(_name);
		if (_startDate == null) {
			clonedTask.setStartDate((Calendar) null);
		} else {
			Calendar startdate = new GregorianCalendar(
					_startDate.get(Calendar.YEAR),
					_startDate.get(Calendar.MONTH),
					_startDate.get(Calendar.DAY_OF_MONTH),
					_startDate.get(Calendar.HOUR_OF_DAY),
					_startDate.get(Calendar.MINUTE),
					_startDate.get(Calendar.SECOND));
			clonedTask.setStartDate(startdate);
		}
		if (_dueDate == null) {
			clonedTask.setStartDate((Calendar) null);
		} else {
			Calendar dueDate = new GregorianCalendar(
					_dueDate.get(Calendar.YEAR), _dueDate.get(Calendar.MONTH),
					_dueDate.get(Calendar.DAY_OF_MONTH),
					_dueDate.get(Calendar.HOUR_OF_DAY),
					_dueDate.get(Calendar.MINUTE),
					_dueDate.get(Calendar.SECOND));
			clonedTask.setDueDate(dueDate);
		}
		clonedTask.setDescription(_description);
		clonedTask.setIsCompleted(_isCompleted);
		clonedTask.setPriority(_priority);
		clonedTask.setIsShouldSync(_shouldSync);
		clonedTask.setHasDueTime(_hasDueTime);
		clonedTask.setHasStartTime(_hasStartTime);
		return clonedTask;
	}
}
