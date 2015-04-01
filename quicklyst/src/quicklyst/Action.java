package quicklyst;

import java.util.LinkedList;

public abstract class Action {
	
	protected ActionType _type;
	protected StringBuilder _feedback;
	protected boolean _isSuccess;
	
	public abstract void execute(LinkedList<Task> workingList,
			LinkedList<Task> workingListMaster);
	
	public void attachFeedback(StringBuilder feedback) {
		_feedback = feedback;
	}
	
	public StringBuilder getFeedback() {
		return _feedback;
	}
	
	public boolean isSuccess() {
		return _isSuccess;
	}
}
