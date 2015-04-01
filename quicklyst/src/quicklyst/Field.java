package quicklyst;

import java.util.Calendar;

public class Field {

	private FieldType _fieldType;

	private String _taskName;
	private Calendar _date;
	private String _priority;
	private Calendar[] _dateRange;

	private FieldCriteria _fieldCriteria;

	private boolean _dateParsed = false;
	private boolean _timeParsed = false;

	public Field(FieldType fieldType) {
		_fieldType = fieldType;
	}

	public Field(FieldType fieldType, Object fieldContent) {

		_fieldType = fieldType;
		updateFieldContent(fieldContent);
	}

	public Field(FieldType fieldType, Object fieldContent,
			FieldCriteria fieldCriteria) {

		_fieldType = fieldType;
		updateFieldContent(fieldContent);
		updateFieldCriteria(fieldCriteria);
	}

	public Field(FieldType fieldType, FieldCriteria fieldCriteria) {

		_fieldType = fieldType;
		updateFieldCriteria(fieldCriteria);
	}

	private void updateFieldContent(Object content) {
		switch (_fieldType) {
		case TASK_NAME:
			_taskName = (String) content;
			break;
		case START_DATE:
		case DUE_DATE:
		case REMINDER:
			try {
				_date = (Calendar) content;
			} catch (ClassCastException e) {
				_dateRange = (Calendar[]) content;
			}
			break;
		case DATE_RANGE:
			_dateRange = (Calendar[]) content;
			break;
		case PRIORITY:
			_priority = (String) content;
			break;
		case COMPLETED:
		case OVERDUE:
			break;
		default:
			break;
		}
	}

	private void updateFieldCriteria(FieldCriteria fieldCriteria) {
		_fieldCriteria = fieldCriteria;
	}
	
	public void setDateParsed(boolean yesNo) {
		_dateParsed = yesNo;
	}
	
	public void setTimeParsed(boolean yesNo) {
		_timeParsed = yesNo;
	}

	public FieldType getFieldType() {
		return _fieldType;
	}

	public String getTaskName() {
		return _taskName;
	}

	public Calendar getDate() {
		return _date;
	}

	public Calendar[] getDateRange() {
		return _dateRange;
	}

	public String getPriority() {
		return _priority;
	}

	public FieldCriteria getCriteria() {
		return _fieldCriteria;
	}
	
	public boolean isDateParsed() {
		return _dateParsed;
	}
	
	public boolean isTimeParsed() {
		return _timeParsed;
	}

	public boolean shouldClearDate() {
		if (_fieldCriteria == FieldCriteria.CLEAR_DATE) {
			return true;
		} else {
			return false;
		}
	}
}
