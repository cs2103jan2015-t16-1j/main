package quicklyst;

import java.util.LinkedList;

public abstract class SyncAction extends Action {

	protected String _userName;
	
	public void setUserName(String userName) {
		_userName = userName;	
	}
	
	public String getUserName() {
		return _userName;
	}

}
