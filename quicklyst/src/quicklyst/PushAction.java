package quicklyst;

import java.util.LinkedList;

public class PushAction extends SyncAction {

	public PushAction(String userName) {
		setUserName(userName);
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.PUSH;
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		String userName = getUserName();
		if (userName == null || userName.isEmpty()) {
			getFeedback().append("User name is blank. ");
			return;
		} else {
			try {
			    QLGoogleIntegration.getInstance().syncTo(masterList);
				getFeedback().append("Synced to Google Calendar. ");
				setSuccess(true);
			} catch (Error e) {
				getFeedback().append(e.getMessage());
			}
		}
	}
	

}
