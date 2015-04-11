package quicklyst;

import java.util.LinkedList;

import quicklyst.Action;
import quicklyst.ActionType;
import quicklyst.QLGoogleIntegration;
import quicklyst.Task;

public class SyncAction extends Action {

	private SortAction _defaultSort;

	public SyncAction() {
		setSuccess(false);
		_feedback = new StringBuilder();
		_type = ActionType.SYNC;
		_defaultSort = new SortAction();
	}

	@Override
	public void execute(LinkedList<Task> displayList,
			LinkedList<Task> masterList) {

		try {

			QLGoogleIntegration.getInstance()
					.sync(masterList, getDeletedList());

			copyList(masterList, displayList);

			getFeedback().append(MessageConstants.SYNCED);

			_isSuccess = true;
			_defaultSort.execute(displayList, masterList);

		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}

	private <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {

		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

}
