package quicklyst;

public enum FieldCriteria {
	
	//start date, due date
	//duration
	//priority
	ASCEND,
	DESCEND,
	
	//start date, due date
	BEFORE,
	ON,
	AFTER,
	BETWEEN,
	
	//completed
	//overdue
	YES,
	NO,
	
	//start date, due date
	CLEAR_DATE,
	
	//priority
	CLEAR_PRIORITY,
	
	//sync
	TO,
	FROM;
}
