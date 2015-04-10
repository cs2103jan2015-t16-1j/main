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
			//_googleInt.logOut(); // TODO change to new API
			getFeedback().append("Logged out from Google Calendar. ");
			setSuccess(true);
		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}
}
