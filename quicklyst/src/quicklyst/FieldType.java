package quicklyst;

public enum FieldType {
	
	TASK_NAME("task name"),
	START_DATE("start date"),
	DUE_DATE("end date"),
	DATE_RANGE("date range"),
	PRIORITY("priority"),
	REMINDER("reminder"),
	DURATION("duration"),
	COMPLETED("completed"),
	OVERDUE("overdue"),
	ALL("all");
	
	private String _name;
	
	private FieldType(String name) {
		_name = name;
	}
	
	public String toString() {
		return _name;
	}
}
