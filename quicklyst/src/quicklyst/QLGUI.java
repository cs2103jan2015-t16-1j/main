package quicklyst;

import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//@author A0112971J
public class QLGUI extends JFrame {
	private static final String MESSAGE_HEADER_OTHERS = "Others";
	private static final String MESSAGE_HEADER_TOMORROW = "Tomorrow";
	private static final String MESSAGE_HEADER_TODAY = "Today";
	private static final String MESSAGE_HEADER_OVERDUE = "Overdue";
	private static final String MESSAGE_TITLE = "Quicklyst";
	private static final String MESSAGE_HEADER_NO_DUE_DATE = "No due date";
	private static final String MESSAGE_CREATING_GUI = "creating GUI";
	private static final String MESSAGE_CREATING_TASKLIST = "creating tasklist";
	private static final String MESSAGE_CREATING_OVERVIEW = "creating overview panel";
	private static final String MESSAGE_CREATING_TIP = "creating tip";
	private static final String MESSAGE_CREATING_COMMAND_TEXT_FIELD = "creating command text field";
	private static final String MESSAGE_ADDING_COMPONENTS = "adding components to main panel";
	private static final String MESSAGE_SET_CONSTRAINTS = "set constraints for components";
	private static final String MESSAGE_FINALIZING_GUI = "finalizing GUI";
	private static final String MESSAGE_GET_TASK_LIST_FROM_QL_LOGIC = "get taskList from QLLogic";

	private static final String EMPTY_STRING = "";

	private static final String ACTION_UNDO = "Undo";
	private static final String ACTION_REDO = "Redo";

	private static final String HOTKEY_UNDO = "control Z";
	private static final String HOTKEY_REDO = "control Y";

	private static final String FORMAT_OVERVIEW = "<html><u>Overview</u><br>"
			+ "%d due today<br>" + "%d due tomorrow<br>" + "%d overdue<br>"
			+ "%d completed</html>";

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	private static final int STARTING_TASK_POSITION = 1;
	private static final int STARTING_TASK_INDEX = 1;

	private static final int PADDING_OVERVIEW = 3;

	private static final int OFFSET_COMMAND_WEST = 10;
	private static final int OFFSET_COMMAND_EAST = -10;
	private static final int OFFSET_COMMAND_SOUTH = -10;

	private static final int OFFSET_STATUS_WEST = 10;
	private static final int OFFSET_STATUS_EAST = -10;
	private static final int OFFSET_STATUS_SOUTH = -10;

	private static final int OFFSET_TASKLISTSCROLL_WEST = 10;
	private static final int OFFSET_TASKLISTSCROLL_NORTH = 10;
	private static final int OFFSET_TASKLISTSCROLL_SOUTH = -10;
	private static final int OFFSET_TASKLISTSCROLL_EAST = -10;

	private static final int OFFSET_OVERVIEWPANE_NORTH = 10;
	private static final int OFFSET_OVERVIEWPANE_EAST = -10;
	private static final int OFFSET_OVERVIEWPANE_SOUTH = -10;

	private static final int OFFSET_TIPSCROLL_WEST = 10;
	private static final int OFFSET_TIPSCROLL_SOUTH = 0;
	private static final int OFFSET_TIPSCROLL_EAST = -10;

	private static final int OFFSET_SINGLETASK = 5;

	private static final Logger LOGGER = Logger
			.getLogger(QLGUI.class.getName());
	
    private class UpdateUITask extends TimerTask {

        @Override
        public void run() {
            updateUI();
        }
        
    }
    
    private class CommandExecution implements Runnable {
        private String _command;
        
        public CommandExecution(String command) {
            _command = command;
        }
        
        @Override
        public void run() {
            StringBuilder status = new StringBuilder();
            _QLLogic.executeCommand(_command, status);
            afterCommandExecution(status.toString());
        }
        
    }

	private JPanel _taskList;
	private JPanel _overviewPane;
	private JLabel _overview;
	private JLabel _status;
	private JTextArea _taskDetails;
	private JTextArea _tip;
	private JTextField _command;

	private CommandTips _commandTips;
	private CommandHistory _commandHistory;
	private UpdateUITask _updateUITask;
	private Timer _timer;
	private Thread _executionThread;
	
	private QLLogic _QLLogic;
	
	public QLGUI() {
		super(MESSAGE_TITLE);

		LOGGER.info(MESSAGE_CREATING_GUI);

		Container contentPane = this.getContentPane();
		SpringLayout layout = new SpringLayout();

		contentPane.setLayout(layout);

		LOGGER.info(MESSAGE_CREATING_TASKLIST);
		JComponent taskListScroll = setupTaskListPanel();

		LOGGER.info(MESSAGE_CREATING_OVERVIEW);
		JComponent overview = setupOverviewPanel();

		LOGGER.info(MESSAGE_CREATING_TIP);
		JComponent tipScroll = setupTip();

		LOGGER.info(MESSAGE_CREATING_COMMAND_TEXT_FIELD);
		setupCommand();

		setupHotkeys();
		_commandTips = CommandTips.getInstance();
		_command.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				showTips();
			}

			public void removeUpdate(DocumentEvent e) {
				showTips();
			}

			public void insertUpdate(DocumentEvent e) {
				showTips();
			}

		});

		showTips();

		_commandHistory = CommandHistory.getInstance();
		_command.addKeyListener(new CommandKeyListener(_commandHistory,
				_command));

		_status = new JLabel("Welcome...");

		LOGGER.info(MESSAGE_ADDING_COMPONENTS);
		addComponents(taskListScroll, tipScroll, _command, overview, _status);

		LOGGER.info(MESSAGE_SET_CONSTRAINTS);
		setConstraintsForMainFrame(layout, contentPane, taskListScroll,
				overview, tipScroll, _command, _status);

		LOGGER.info(MESSAGE_FINALIZING_GUI);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(600, 450));
		setVisible(true);
		
		_timer = new Timer(true);

		LOGGER.info(MESSAGE_GET_TASK_LIST_FROM_QL_LOGIC);
		_QLLogic = QLLogic.getInstance();
		_QLLogic.setup(new StringBuilder());
		updateUI();

	}

	private JComponent setupTaskListPanel() {
		_taskList = new JPanel(new GridBagLayout());
		JPanel taskListBorderPane = new JPanel(new BorderLayout());
		taskListBorderPane.add(_taskList, BorderLayout.NORTH);
		JScrollPane taskListScroll = new JScrollPane(taskListBorderPane);
		return taskListScroll;
	}

	private JComponent setupOverviewPanel() {
		_overviewPane = new JPanel(new GridBagLayout());
		// _overviewPane.setBorder(new LineBorder(Color.BLACK));
		GridBagConstraints c = new GridBagConstraints();

		_overview = new JLabel();
		_overview.setBorder(BorderFactory.createEmptyBorder(PADDING_OVERVIEW,
				PADDING_OVERVIEW, PADDING_OVERVIEW, PADDING_OVERVIEW));
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		_overviewPane.add(_overview, c);

		JLabel taskDetailsHeader = new JLabel(
				"<html><u>Task Details</u></html>");
		taskDetailsHeader.setBorder(BorderFactory.createEmptyBorder(
				PADDING_OVERVIEW, PADDING_OVERVIEW, 0, PADDING_OVERVIEW));
		GridBagConstraints c2 = new GridBagConstraints();
		c2.anchor = GridBagConstraints.NORTHWEST;
		c2.gridx = 0;
		c2.gridy = 1;
		_overviewPane.add(taskDetailsHeader, c2);

		GridBagConstraints c3 = new GridBagConstraints();
		_taskDetails = new JTextArea(0, 17);
		_taskDetails.setBorder(BorderFactory.createEmptyBorder(0,
				PADDING_OVERVIEW, PADDING_OVERVIEW, PADDING_OVERVIEW));
		_taskDetails.setBackground(_overviewPane.getBackground());
		_taskDetails.setWrapStyleWord(true);
		_taskDetails.setLineWrap(true);
		_taskDetails.setEditable(false);
		_taskDetails.setEnabled(false);
		_taskDetails.setFont(_overview.getFont());
		_taskDetails.setDisabledTextColor(_overview.getForeground());
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridx = 0;
		c3.gridy = 2;
		c3.weighty = 1;
		c3.weightx = 1;
		// overviewScroll.setBorder(new LineBorder(Color.BLACK));
		_overviewPane.add(_taskDetails, c3);

		// _overviewPane.add(detailsScroll, c2);
		JScrollPane overviewScroll = new JScrollPane(_overviewPane);

		return overviewScroll;
	}

	private JComponent setupTip() {
		_tip = new JTextArea();
		_tip.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		_tip.setEditable(false);
		_tip.setLineWrap(true);
		_tip.setWrapStyleWord(true);
		JScrollPane tipScroll = new JScrollPane(_tip);
		return tipScroll;
	}

	private void setupCommand() {
		_command = new JTextField();
		Font f = new Font("Dialog", Font.PLAIN, 15);

		_command.setFont(f);
		LOGGER.info("adding actionListener to command text field");
		CommandActionListener c = new CommandActionListener(_command, this);
		_command.addActionListener(c);
	}

	private void setupHotkeys() {
		setupActionMap();
		setupInputMap();
	}

	private void setupActionMap() {
		Action undoAction = new HotkeysAction(ACTION_UNDO, this);
		Action redoAction = new HotkeysAction(ACTION_REDO, this);
		_command.getActionMap().put(ACTION_UNDO, undoAction);
		_command.getActionMap().put(ACTION_REDO, redoAction);
	}

	private void setupInputMap() {
		_command.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(HOTKEY_UNDO), ACTION_UNDO);
		_command.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(HOTKEY_UNDO), ACTION_UNDO);
		_command.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(HOTKEY_UNDO), ACTION_UNDO);

		_command.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(HOTKEY_REDO), ACTION_REDO);
		_command.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(HOTKEY_REDO), ACTION_REDO);
		_command.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(HOTKEY_REDO), ACTION_REDO);
	}

	private void addComponents(JComponent taskListScroll, JComponent tipScroll,
			JComponent command, JComponent overview, JComponent status) {
		add(command);
		add(taskListScroll);
		add(tipScroll);
		add(overview);
		add(status);
	}

	private void setConstraintsForMainFrame(SpringLayout layout,
			Container contentPane, JComponent taskListScroll,
			JComponent overviewScroll, JComponent tipScroll,
			JComponent commandTextField, JComponent status) {

		setConstraintsForCommandTextField(layout, contentPane,
				commandTextField, status);
		setConstraintsForTaskListScroll(layout, contentPane, taskListScroll,
				commandTextField, overviewScroll);
		setConstraintsForOverviewPane(layout, contentPane, taskListScroll,
				overviewScroll, tipScroll);
		setConstraintsForTipScroll(layout, contentPane, taskListScroll,
				overviewScroll, tipScroll);
		setConstraintsForStatus(layout, contentPane, status);
	}

	private void setConstraintsForCommandTextField(SpringLayout layout,
			Container contentPane, JComponent commandTextField,
			JComponent status) {
		layout.putConstraint(SpringLayout.WEST, commandTextField,
				OFFSET_COMMAND_WEST, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, commandTextField,
				OFFSET_COMMAND_EAST, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, commandTextField,
				OFFSET_COMMAND_SOUTH, SpringLayout.NORTH, status);
	}

	private void setConstraintsForStatus(SpringLayout layout,
			Container contentPane, JComponent status) {
		layout.putConstraint(SpringLayout.WEST, status, OFFSET_STATUS_WEST,
				SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, status, OFFSET_STATUS_EAST,
				SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, status, OFFSET_STATUS_SOUTH,
				SpringLayout.SOUTH, contentPane);
	}

	private void setConstraintsForTaskListScroll(SpringLayout layout,
			Container contentPane, JComponent taskListScroll,
			JComponent commandTextField, JComponent overviewScroll) {
		layout.putConstraint(SpringLayout.WEST, taskListScroll,
				OFFSET_TASKLISTSCROLL_WEST, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, taskListScroll,
				OFFSET_TASKLISTSCROLL_NORTH, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, taskListScroll,
				OFFSET_TASKLISTSCROLL_SOUTH, SpringLayout.NORTH,
				commandTextField);
		layout.putConstraint(SpringLayout.EAST, taskListScroll,
				OFFSET_TASKLISTSCROLL_EAST, SpringLayout.WEST, overviewScroll);
		layout.getConstraints(taskListScroll).setWidth(
				Spring.max(Spring.constant(220),
						layout.getConstraints(taskListScroll).getWidth()));
	}

	private void setConstraintsForOverviewPane(SpringLayout layout,
			Container contentPane, JComponent taskListScroll,
			JComponent overviewScroll, JComponent tipScroll) {
		layout.putConstraint(SpringLayout.NORTH, overviewScroll,
				OFFSET_OVERVIEWPANE_NORTH, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.EAST, overviewScroll,
				OFFSET_OVERVIEWPANE_EAST, SpringLayout.EAST, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, overviewScroll,
				OFFSET_OVERVIEWPANE_SOUTH, SpringLayout.NORTH, tipScroll);
		layout.getConstraints(overviewScroll).setWidth(Spring.constant(220));
	}

	private void setConstraintsForTipScroll(SpringLayout layout,
			Container contentPane, JComponent taskListScroll,
			JComponent overviewPane, JComponent tipScroll) {
		layout.putConstraint(SpringLayout.WEST, tipScroll,
				OFFSET_TIPSCROLL_WEST, SpringLayout.EAST, taskListScroll);
		layout.putConstraint(SpringLayout.SOUTH, tipScroll,
				OFFSET_TIPSCROLL_SOUTH, SpringLayout.SOUTH, taskListScroll);
		layout.putConstraint(SpringLayout.EAST, tipScroll,
				OFFSET_TIPSCROLL_EAST, SpringLayout.EAST, contentPane);
		layout.getConstraints(tipScroll).setHeight(Spring.constant(180));
	}

	private void updateUI() {
	    List<Task> displayList = _QLLogic.getDisplayList();
		updateTaskList(displayList);
		updateOverview();
		clearTaskDetails();
		scheduleNextUpdate(displayList);
	}

	private void scheduleNextUpdate(List<Task> displayList) {
        Calendar now = Calendar.getInstance();
        Calendar nextUpdate = null;
	    for (Task task : displayList) {
            if (task.getDueDate() != null && task.getDueDate().compareTo(now) > 0) {
                nextUpdate = (Calendar)task.getDueDate().clone();
                nextUpdate.add(Calendar.MINUTE, 1);
                nextUpdate.set(Calendar.SECOND, 0);
                nextUpdate.set(Calendar.MILLISECOND, 0);
                break;
            }
        }
	    
	    if (_updateUITask != null) {
	        _updateUITask.cancel();
	        _updateUITask = null;
	    }
	    
	    _timer.purge();
        if (nextUpdate != null) {
            _updateUITask = new UpdateUITask();
            _timer.schedule(_updateUITask, nextUpdate.getTime());
        }
    }

    private void clearTaskDetails() {
		// _taskDetails.setText("");
		_taskDetails.setText("Mouse over task to show more...");
	}

	private void updateTaskList(List<Task> tasks) {
		_taskList.removeAll();
		int taskPosition = STARTING_TASK_POSITION, taskIndex = STARTING_TASK_INDEX;
		int headerCount = 0;
		String currentHeader = EMPTY_STRING;
		String previousHeader = EMPTY_STRING;
		Calendar now = Calendar.getInstance();

		for (Task task : tasks) {
			TaskPanel singleTaskPane = new TaskPanel(task, taskIndex);

			GridBagConstraints con = new GridBagConstraints();
			con.insets = new Insets(OFFSET_SINGLETASK, OFFSET_SINGLETASK,
					OFFSET_SINGLETASK, OFFSET_SINGLETASK);
			con.weightx = 1;
			con.anchor = GridBagConstraints.NORTHEAST;
			con.fill = GridBagConstraints.HORIZONTAL;
			con.gridx = 0;
			con.gridy = taskPosition - 1;

			if (task.getDueDate() != null) {
				if (task.getDueDate().compareTo(now) < 0) {
					currentHeader = MESSAGE_HEADER_OVERDUE;
				} else {
					if (headerCount < 3) {
						currentHeader = calendarToString(task.getDueDate());
						if (!currentHeader.equals(previousHeader)) {
							headerCount++;
						}
					}
					if (headerCount > 2) {
						currentHeader = MESSAGE_HEADER_OTHERS;
					}
				}
			} else {
				currentHeader = MESSAGE_HEADER_NO_DUE_DATE;
			}

			if (!currentHeader.equals(previousHeader)) {
				JLabel dateLabel = new JLabel(currentHeader);
				_taskList.add(dateLabel, con);
				taskPosition++;
				con.gridy = taskPosition - 1;
				previousHeader = currentHeader;
			}

			_taskList.add(singleTaskPane, con);
			singleTaskPane.addMouseListener(new TaskMouseListener(task,
					_taskDetails));

			taskPosition++;
			taskIndex++;
		}

		_taskList.revalidate();
		_taskList.repaint();
	}

	private String calendarToString(Calendar c) {
		Calendar today = getCalendarToday();
		Calendar tomorrow = getCalendarOneDayAfter(today);
		Calendar twoDaysAfter = getCalendarOneDayAfter(tomorrow);
		if ((c.compareTo(today) >= 0) && (c.compareTo(tomorrow) < 0)) {
			return MESSAGE_HEADER_TODAY;
		} else if (c.compareTo(twoDaysAfter) < 0) {
			return MESSAGE_HEADER_TOMORROW;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(c.getTime());
		}
	}

	private void updateOverview() {
		Calendar now = Calendar.getInstance();
		Calendar today = getCalendarToday();
		Calendar tomorrow = getCalendarOneDayAfter(today);
		Calendar twoDaysAfter = getCalendarOneDayAfter(tomorrow);

		int dueToday = 0, dueTomorrow = 0, overdue = 0, completed = 0;
		List<Task> allTasks = _QLLogic.getMasterList();
		for (int j = 0; j < allTasks.size(); ++j) {
			if (allTasks.get(j).getIsCompleted()) {
				completed++;
				continue;
			}

			Calendar due = allTasks.get(j).getDueDate();
			if (due == null) {
				continue;
			}

			if ((due.compareTo(today) >= 0) && (due.compareTo(tomorrow) < 0)) {
				dueToday++;
			} else if ((due.compareTo(tomorrow) >= 0)
					&& (due.compareTo(twoDaysAfter) < 0)) {
				dueTomorrow++;
			}

			if (due.compareTo(now) < 0) {
				overdue++;
			}
		}

		_overview.setText(String.format(FORMAT_OVERVIEW, dueToday, dueTomorrow,
				overdue, completed));
	}

	private Calendar getCalendarToday() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today;
	}

	private Calendar getCalendarOneDayAfter(Calendar day) {
		Calendar tomorrow = (Calendar) day.clone();
		tomorrow.add(Calendar.DATE, 1);
		return tomorrow;
	}
	
	// Method for actionListener
	public boolean executeCommand(String command) {
	    if ((_executionThread == null) && (!command.trim().isEmpty())) {
    	    CommandExecution exec = new CommandExecution(command);
    	    _executionThread = new Thread(exec);
    	    _executionThread.start();
    	    setStatus("Processing... Please wait...");
    	    return true;
	    }
	    return false;
	}
	
	public void afterCommandExecution(String status) {
	    if (!status.isEmpty()) {
            setStatus(status.toString());
        } else {
            setStatus(" ");
        }
        updateUI();
        _executionThread = null;
	}

    private void setStatus(String status) {
        _status.setText(status);
    }

	public void addCommandToCommandHistory(String command) {
		// TODO Auto-generated method stub
		_commandHistory.addCommand(command);
	}

	public void showTips() {
		String tips = _commandTips.getTips(_command.getText());
		setTips(tips);
	}

    private void setTips(String tips) {
        if (!_tip.getText().equals(tips)) {
			_tip.setText(tips);
			_tip.setCaretPosition(0);
		}
    }

	public static void main(String[] args) {
		QLGUI g = new QLGUI();
	}

}
