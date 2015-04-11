package quicklyst;

//@author A0102015H
public enum FieldType {
	
	START_DATE("start date"),
	DUE_DATE("due date"),
	PRIORITY("priority"),
	COMPLETED("completed"),
	OVERDUE("overdue");
	
	private String _name;
	
	private FieldType(String name) {
		_name = name;
	}
	
	public String toString() {
		return _name;
	}
}
