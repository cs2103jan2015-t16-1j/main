package quicklyst;

import java.util.LinkedList;

public class LogOutAction extends Action {
	
	QLGoogleIntegration _googleInt; 
	
	public LogOutAction() {
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.LOG_OUT;
		_googleInt = QLGoogleIntegration.getInstance(); 
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		
		try {
			if (_googleInt.logout()) {
			    getFeedback().append("Logged out from Google. ");
			} else {
			    getFeedback().append("Not logged in to Google. ");
			}
			setSuccess(true);
		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}
}
