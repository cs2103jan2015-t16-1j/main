package quicklyst;

import java.util.LinkedList;

//@author A0102015H
public abstract class Action {
	
	protected ActionType _type;
	protected StringBuilder _feedback;
	protected boolean _isSuccess;
	private String _deletedTaskID;
	private LinkedList<String> _deletedList;
	
	public abstract void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList);
	
	public void attachFeedback(StringBuilder feedback) {
		_feedback = feedback;
	}
	
	public StringBuilder getFeedback() {
		return _feedback;
	}
	
	public boolean isSuccess() {
		return _isSuccess;
	}
	
	public void setSuccess(boolean isSuccess) {
		_isSuccess = isSuccess;
	}
	
	public ActionType getType() {
		return _type;
	}
	
	public void setType(ActionType type) {
		_type = type;
	}
	
	public boolean shouldShowAllCompleted() {
		return false;
	}
	
	public void setDeletedTaskID(String ID) {
		_deletedTaskID = ID;
	}
	
	public String getDeletedTaskID() {
		return _deletedTaskID;
	}
	
	public void attachDeletedList(LinkedList<String> list) {
		_deletedList = list;
	}
	
	public LinkedList<String> getDeletedList() {
		return _deletedList;
	}
}
