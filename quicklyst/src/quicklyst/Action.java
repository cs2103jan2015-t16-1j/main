package quicklyst;

import java.util.LinkedList;
import java.util.logging.Logger;

//@author A0102015H
public abstract class Action {

	protected static final Logger LOGGER = Logger.getLogger(Action.class
			.getName());

	protected ActionType _type;
	protected StringBuilder _feedback;
	protected boolean _isSuccess;
	//protected String _deletedTaskID;
	//protected LinkedList<String> _deletedList;

	public abstract void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList);

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

	public boolean shouldShowAllCompleted() {
		return false;
	}

	public String getDeletedTaskID() {
		return null;
	}

	public void attachDeletedList(LinkedList<String> list) {
		return;
	}
}
