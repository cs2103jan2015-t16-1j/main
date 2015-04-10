package quicklyst;
import java.util.LinkedList;

import quicklyst.Action;
import quicklyst.ActionType;
import quicklyst.QLGoogleIntegration;
import quicklyst.Task;


public class SyncAction extends Action {
	
	QLGoogleIntegration _googleInt; 

	public SyncAction() {
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.SYNC;
		_googleInt = QLGoogleIntegration.getInstance();
	}
	
	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		try {
			//_googleInt.syncTo(masterList, getDeletedList()); // TODO change to new API
			getFeedback().append("Synced to Google Calendar. ");
			setSuccess(true);
		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}

}
