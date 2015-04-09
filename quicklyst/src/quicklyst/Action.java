package quicklyst;

import java.util.LinkedList;

//@author A0102015H
public abstract class Action {
	
	protected ActionType _type;
	protected StringBuilder _feedback;
	protected boolean _isSuccess;
	
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
}
