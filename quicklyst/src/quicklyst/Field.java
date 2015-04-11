package quicklyst;

import java.util.Calendar;

//@author A0102015H
public class Field {

	private FieldType _fieldType;

	private String _taskName;
	private String _priority;
	
	private Calendar _date;
	private Calendar[] _dateRange;

	private FieldCriteria _fieldCriteria;

	private boolean _isDateParsed = false;
	private boolean _isTimeParsed = false;

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
			
		case START_DATE:
		case DUE_DATE:
			
			try {
				_date = (Calendar) content;
			} catch (ClassCastException e) {
				_dateRange = (Calendar[]) content;
			}
			break;
			
		case PRIORITY:
			
			_priority = (String) content;
			break;

		default:
			break;
		}
	}

	private void updateFieldCriteria(FieldCriteria fieldCriteria) {
		_fieldCriteria = fieldCriteria;
	}

	public void setDateParsed(boolean yesNo) {
		_isDateParsed = yesNo;
	}

	public void setTimeParsed(boolean yesNo) {
		_isTimeParsed = yesNo;
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
		return _isDateParsed;
	}

	public boolean isTimeParsed() {
		return _isTimeParsed;
	}

	public boolean shouldClearDate() {
		if (_fieldCriteria == FieldCriteria.CLEAR_DATE) {
			return true;
		} else {
			return false;
		}
	}

	public boolean shouldClearPriority() {
		if (_fieldCriteria == FieldCriteria.CLEAR_PRIORITY) {
			return true;
		} else {
			return false;
		}
	}
}
