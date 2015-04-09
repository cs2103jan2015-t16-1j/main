package quicklyst;

import java.util.LinkedList;

//@author A0102015H
public class PullAction extends Action {
	
	private SortAction _defaultSort;

	public PullAction() {
		setSuccess(false);
		this._feedback = new StringBuilder();
		this._type = ActionType.PULL;
		_defaultSort = new SortAction();
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {
		try {
			QLGoogleIntegration.getInstance().syncFrom(masterList);
			copyList(masterList, displayList);
			getFeedback().append("Synced from Google Calendar. ");
			setSuccess(true);
			_defaultSort.execute(displayList, masterList);
		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}

	private static <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {
		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}
}
