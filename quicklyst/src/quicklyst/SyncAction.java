package quicklyst;

import java.util.LinkedList;

import quicklyst.Action;
import quicklyst.ActionType;
import quicklyst.QLGoogleIntegration;
import quicklyst.Task;

//@author A0102015H
public class SyncAction extends Action {

	private SortAction _defaultSort;
	
	private LinkedList<String> _deletedList;

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

			assert _deletedList != null;
			
			QLGoogleIntegration.getInstance()
					.sync(masterList, _deletedList);

			copyList(masterList, displayList);

			getFeedback().append(MessageConstants.SYNCED);

			_isSuccess = true;
			_defaultSort.execute(displayList, masterList);

		} catch (Error e) {
			getFeedback().append(e.getMessage());
		}
	}
	
	public void attachDeletedList(LinkedList<String> list) {
		_deletedList = list;
	}
	
	private <E> void copyList(LinkedList<E> fromList,
			LinkedList<E> toList) {

		toList.clear();
		for (int i = 0; i < fromList.size(); i++)
			toList.add(fromList.get(i));
	}

}
