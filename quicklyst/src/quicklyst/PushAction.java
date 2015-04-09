package quicklyst;

import java.util.LinkedList;

public class PushAction extends Action {

	public PushAction() {
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.PUSH;
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		try {
			QLGoogleIntegration.getInstance().syncTo(masterList);
			getFeedback().append("Synced to Google Calendar. ");
			setSuccess(true);
		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}

}
