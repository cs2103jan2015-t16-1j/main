package quicklyst;

import java.util.LinkedList;

public class LogOutAction extends Action {
	
	QLGoogleIntegration _googleInt; 
	
	public LogOutAction() {
		
		setSuccess(false);
		_feedback = new StringBuilder();
		_type = ActionType.LOG_OUT;
		_googleInt = QLGoogleIntegration.getInstance(); 
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		
		try {
			
			if (_googleInt.logout()) {
				_feedback.append("Logged out from Google. ");
			} else {
				_feedback.append("Not logged in to Google. ");
			}
			
			setSuccess(true);
			
		} catch (Error e) {
			_feedback.append(e.getMessage());
		}
	}
}
