package quicklyst;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

//@author A0112971J
public class GUI extends JFrame {
    
    private static final int CARET_DEFAULT_POSITION = 0;
    
    private static final int CALENDAR_FIRST_HOUR = 0;
    private static final int CALENDAR_FIRST_MIN = 0;
    private static final int CALENDAR_FIRST_SECOND = 0;
    private static final int CALENDAR_FIRST_MILLISECOND = 0;
    
    private static final int CALENDAR_NEXT_MIN = 1;
    
    private static final int CONSTANT_COUNT_NO_TASK = 0;
    private static final int CONSTANT_CALENDAR_COMPARE = 0;
    
    private static final int COORDINATE_OVERVIEW_LABEL_X = 0;
    private static final int COORDINATE_OVERVIEW_LABEL_Y = 0;
    private static final int COORDINATE_TASK_DETAILS_X = 0;
    private static final int COORDINATE_TASK_DETAILS_Y = 2;
    private static final int COORDINATE_SINGLETASK_PANE_X = 0;
    private static final int COORDINATE_TASK_DETAILS_HEADER_Y = 1;
    private static final int COORDINATE_TASK_DETAILS_HEADER_X = 0;
    
    private static final int DATE_INCREMENT = 1;
    
    private static final int DEFAULT_WINDOW_WIDTH = 800;
    private static final int DEFAULT_WINDOW_HEIGHT = 600;
    
    private static final int FONT_SIZE = 15;
 
    private static final int HEADER_COUNT_INIT = 0;
    
    private static final int MAX_DATE_HEADER_COUNT = 2;
    
    private static final int MIN_WINDOW_WIDTH = 600;
    private static final int MIN_WINDOW_HEIGHT = 450;

    private static final int MARGIN_OVERVIEW = 3;
    
    private static final int MARGIN_TIP_UP = 0;
    private static final int MARGIN_TIP_DOWN = 3;
    private static final int MARGIN_TIP_LEFT = 0;
    private static final int MARGIN_TIP_RIGHT = 0;
    
    private static final int MARGIN_TASKDETAILS_UP = 0;
    private static final int MARGIN_TASKDETAILS_DOWN = 3;
    private static final int MARGIN_TASKDETAILS_LEFT = 3;
    private static final int MARGIN_TASKDETAILS_RIGHT = 3;
    
    private static final int MARGIN_TASKDETAILS_HEADER_UP = 3;
    private static final int MARGIN_TASKDETAILS_HEADER_DOWN = 3;
    private static final int MARGIN_TASKDETAILS_HEADER_LEFT = 0;
    private static final int MARGIN_TASKDETAILS_HEADER_RIGHT = 3;

    private static final int OFFSET_COMMAND_WEST = 10;
    private static final int OFFSET_COMMAND_EAST = -10;
    private static final int OFFSET_COMMAND_SOUTH = -10;
    
    private static final int OFFSET_OVERVIEWPANE_NORTH = 10;
    private static final int OFFSET_OVERVIEWPANE_EAST = -10;
    private static final int OFFSET_OVERVIEWPANE_SOUTH = -10;

    private static final int OFFSET_SINGLETASK = 5;
    
    private static final int OFFSET_STATUS_WEST = 10;
    private static final int OFFSET_STATUS_EAST = -10;
    private static final int OFFSET_STATUS_SOUTH = -10;


    private static final int OFFSET_TASK_POSITION = 1;
    
    private static final int OFFSET_TASKLISTSCROLL_WEST = 10;
    private static final int OFFSET_TASKLISTSCROLL_NORTH = 10;
    private static final int OFFSET_TASKLISTSCROLL_SOUTH = -10;
    private static final int OFFSET_TASKLISTSCROLL_EAST = -10;
    
    private static final int OFFSET_TIPSCROLL_WEST = 10;
    private static final int OFFSET_TIPSCROLL_SOUTH = 0;
    private static final int OFFSET_TIPSCROLL_EAST = -10;
    
    private static final int STARTING_TASK_POSITION = 1;
    private static final int STARTING_TASK_INDEX = 1;
    
    private static final int TASK_LIST_FIRST_TASK = 0;
    
    private static final int TASK_DETAILS_LABEL_HEIGHT = 17;
    private static final int TASK_DETAILS_LABEL_WIDTH = 0;
    
    private static final int WEIGHT_SINGLETASK_PANE_X = 1;

    private static final int WEIGHT_TASK_DETAILS_X = 1;
    private static final int WEIGHT_TASK_DETAILS_Y = 1;

    private static final int WEIGHT_OVERVIEW_LABEL_X = 1;
    
    private static final String ACTION_UNDO = "Undo";
    private static final String ACTION_REDO = "Redo";

    private static final String FONT_STYLE = "Dialog";
    
    private static final String FORMAT_DATE = "dd/MM/yyyy";
        
    private static final String HOTKEY_UNDO = "control Z";
    private static final String HOTKEY_REDO = "control Y";
    
    private static final String LOG_MESSAGE_CREATING_GUI = "creating GUI";
    private static final String LOG_MESSAGE_CREATING_TASKLIST = "creating tasklist";
    private static final String LOG_MESSAGE_CREATING_OVERVIEW = "creating overview panel";
    private static final String LOG_MESSAGE_CREATING_TIP = "creating tip";
    private static final String LOG_MESSAGE_CREATING_COMMAND_TEXT_FIELD = "creating command text field";
    private static final String LOG_MESSAGE_ADDING_ACTION_LISTENER = "adding actionListener to command text field";
    private static final String LOG_MESSAGE_ADDING_COMPONENTS = "adding components to main panel";
    private static final String LOG_MESSAGE_SET_CONSTRAINTS = "set constraints for components";
    private static final String LOG_MESSAGE_FINALIZING_GUI = "finalizing GUI";
    private static final String LOG_MESSAGE_GET_TASK_LIST_FROM_QL_LOGIC = "get taskList from QLLogic";

    private static final String STRING_EMPTY = "";
    private static final String STATUS_NONE = " ";
    
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
    
    
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
    
    private class commandDocumentListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            showTips();
        }

        public void removeUpdate(DocumentEvent e) {
            showTips();
        }

        public void insertUpdate(DocumentEvent e) {
            showTips();
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
    
    private Logic _QLLogic;
    
    public GUI() {
        super(MessageConstants.MESSAGE_TITLE);

        LOGGER.info(LOG_MESSAGE_CREATING_GUI);

        Container contentPane = this.getContentPane();
        SpringLayout layout = new SpringLayout();

        contentPane.setLayout(layout);

        LOGGER.info(LOG_MESSAGE_CREATING_TASKLIST);
        JComponent taskListScroll = setupTaskListPanel();

        LOGGER.info(LOG_MESSAGE_CREATING_OVERVIEW);
        JComponent overview = setupOverviewPanel();

        LOGGER.info(LOG_MESSAGE_CREATING_TIP);
        JComponent tipScroll = setupTip();

        LOGGER.info(LOG_MESSAGE_CREATING_COMMAND_TEXT_FIELD);
        setupCommand();

        setupHotkeys();
        
        setupCommandTips();
        
        attachDocumentListenerToCommand();

        _commandHistory = new CommandHistory();
        _command.addKeyListener(new CommandKeyListener(_commandHistory,
                _command));

        _status = new JLabel(MessageConstants.MESSAGE_APPLICATION_START);

        LOGGER.info(LOG_MESSAGE_ADDING_COMPONENTS);
        addComponents(taskListScroll, tipScroll, _command, overview, _status);

        LOGGER.info(LOG_MESSAGE_SET_CONSTRAINTS);
        setConstraintsForMainFrame(layout, contentPane, taskListScroll,
                overview, tipScroll, _command, _status);

        LOGGER.info(LOG_MESSAGE_FINALIZING_GUI);
        setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        setVisible(true);
        
        _timer = new Timer(true);

        LOGGER.info(LOG_MESSAGE_GET_TASK_LIST_FROM_QL_LOGIC);
        _QLLogic = Logic.getInstance();
        StringBuilder status = new StringBuilder();
        _QLLogic.setup(status);
        if (!status.toString().isEmpty()) {
            setStatus(status.toString());
        }
        updateUI();
        showTips();
    }

    private void attachDocumentListenerToCommand() {
        _command.getDocument().addDocumentListener(new commandDocumentListener());
        
    }

    private void setupCommandTips() {
        _commandTips = new CommandTips();
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

        GridBagConstraints c = new GridBagConstraints();

        setupOverviewLabel(c);
        _overviewPane.add(_overview, c);

        JLabel taskDetailsHeader = new JLabel(MessageConstants.MESSAGE_HOVER_TASK_TITLE);
        taskDetailsHeader.setBorder(BorderFactory
                                    .createEmptyBorder(MARGIN_TASKDETAILS_HEADER_UP,
                                                       MARGIN_TASKDETAILS_HEADER_DOWN,
                                                       MARGIN_TASKDETAILS_HEADER_LEFT,
                                                       MARGIN_TASKDETAILS_HEADER_RIGHT));
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.gridx = COORDINATE_TASK_DETAILS_HEADER_X;
        c2.gridy = COORDINATE_TASK_DETAILS_HEADER_Y;
        _overviewPane.add(taskDetailsHeader, c2);

        GridBagConstraints c3 = new GridBagConstraints();
        _taskDetails = new JTextArea(TASK_DETAILS_LABEL_WIDTH, TASK_DETAILS_LABEL_HEIGHT);
        _taskDetails.setBorder(BorderFactory
                               .createEmptyBorder(MARGIN_TASKDETAILS_UP, MARGIN_TASKDETAILS_DOWN,
                                                  MARGIN_TASKDETAILS_LEFT, MARGIN_TASKDETAILS_RIGHT));
        _taskDetails.setBackground(_overviewPane.getBackground());
        _taskDetails.setWrapStyleWord(true);
        _taskDetails.setLineWrap(true);
        _taskDetails.setEditable(false);
        _taskDetails.setEnabled(false);
        _taskDetails.setFont(_overview.getFont());
        _taskDetails.setDisabledTextColor(_overview.getForeground());
        c3.anchor = GridBagConstraints.NORTHWEST;
        c3.gridx = COORDINATE_TASK_DETAILS_X;
        c3.gridy = COORDINATE_TASK_DETAILS_Y;
        c3.weightx = WEIGHT_TASK_DETAILS_X;
        c3.weighty = WEIGHT_TASK_DETAILS_Y;
        
        _overviewPane.add(_taskDetails, c3);

        JScrollPane overviewScroll = new JScrollPane(_overviewPane);

        return overviewScroll;
    }

    private void setupOverviewLabel(GridBagConstraints con) {
        _overview = new JLabel();
        _overview.setBorder(BorderFactory.createEmptyBorder(MARGIN_OVERVIEW,
                MARGIN_OVERVIEW, MARGIN_OVERVIEW, MARGIN_OVERVIEW));
        con.anchor = GridBagConstraints.NORTHWEST;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.gridx = COORDINATE_OVERVIEW_LABEL_X;
        con.gridy = COORDINATE_OVERVIEW_LABEL_Y;
        con.weightx = WEIGHT_OVERVIEW_LABEL_X;
    }

    private JComponent setupTip() {
        _tip = new JTextArea();
        _tip.setBorder(BorderFactory.createEmptyBorder(MARGIN_TIP_UP, MARGIN_TIP_DOWN,
                                                       MARGIN_TIP_LEFT, MARGIN_TIP_RIGHT));
        _tip.setEditable(false);
        _tip.setLineWrap(true);
        _tip.setWrapStyleWord(true);
        JScrollPane tipScroll = new JScrollPane(_tip);
        return tipScroll;
    }

    private void setupCommand() {
        _command = new JTextField();
        Font font = new Font(FONT_STYLE, Font.PLAIN, FONT_SIZE);

        _command.setFont(font);
        LOGGER.info(LOG_MESSAGE_ADDING_ACTION_LISTENER);
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
        Calendar nextUpdate = findNextDueDate(displayList);
        
        scheduleUpdateUI(nextUpdate);
    }

    private void scheduleUpdateUI(Calendar nextUpdate) {
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

    private Calendar findNextDueDate(List<Task> displayList) {
        Calendar now = Calendar.getInstance();
        Calendar nextUpdate = null;
        for (Task task : displayList) {
            if (task.getDueDate() != null && task.getDueDate().compareTo(now) > CONSTANT_CALENDAR_COMPARE) {
                nextUpdate = (Calendar)task.getDueDate().clone();
                nextUpdate.add(Calendar.MINUTE, CALENDAR_NEXT_MIN);
                nextUpdate.set(Calendar.SECOND, CALENDAR_FIRST_SECOND);
                nextUpdate.set(Calendar.MILLISECOND, CALENDAR_FIRST_MILLISECOND);
                break;
            }
        }
        return nextUpdate;
    }

    private void clearTaskDetails() {
        _taskDetails.setText(MessageConstants.MESSAGE_HOVER_DISPLAY);
    }

    private void updateTaskList(List<Task> tasks) {
        _taskList.removeAll();
        int taskPosition = STARTING_TASK_POSITION, taskIndex = STARTING_TASK_INDEX;
        int headerCount = HEADER_COUNT_INIT;
        String currentHeader = STRING_EMPTY;
        String previousHeader = STRING_EMPTY;
        Calendar now = Calendar.getInstance();

        for (Task task : tasks) {
            GridBagConstraints con = new GridBagConstraints();
            con.insets = new Insets(OFFSET_SINGLETASK, OFFSET_SINGLETASK,
                    OFFSET_SINGLETASK, OFFSET_SINGLETASK);
            con.weightx = WEIGHT_SINGLETASK_PANE_X;
            con.anchor = GridBagConstraints.NORTHEAST;
            con.fill = GridBagConstraints.HORIZONTAL;
            con.gridx = COORDINATE_SINGLETASK_PANE_X;
            con.gridy = taskPosition - OFFSET_TASK_POSITION;

            if (task.getDueDate() != null) {
                if (task.getDueDate().compareTo(now) < CONSTANT_CALENDAR_COMPARE) {
                    currentHeader = MessageConstants.MESSAGE_HEADER_OVERDUE;
                } else {
                    currentHeader = calendarToString(task.getDueDate());
                }
            } else {
                currentHeader = MessageConstants.MESSAGE_HEADER_NO_DUE_DATE;
            }
            
            if ((!currentHeader.equals(MessageConstants.MESSAGE_HEADER_OVERDUE)) &&
                (!currentHeader.equals(previousHeader))) {
                headerCount++;
            }
            
            if ((headerCount > MAX_DATE_HEADER_COUNT) &&
                (!currentHeader.equals(MessageConstants.MESSAGE_HEADER_NO_DUE_DATE))) {
                currentHeader = MessageConstants.MESSAGE_HEADER_OTHERS;
            }

            if (!currentHeader.equals(previousHeader)) {
                JLabel dateLabel = new JLabel(currentHeader);
                _taskList.add(dateLabel, con);
                taskPosition++;
                con.gridy = taskPosition - OFFSET_TASK_POSITION;
                previousHeader = currentHeader;
            }
            
            TaskPanel singleTaskPane = new TaskPanel(task, taskIndex);

            _taskList.add(singleTaskPane, con);
            singleTaskPane.addMouseListener(new TaskMouseListener(task,
                    _taskDetails));

            taskPosition++;
            taskIndex++;
        }

        _taskList.revalidate();
        _taskList.repaint();
    }

    private String calendarToString(Calendar calendar) {
        Calendar today = getCalendarToday();
        Calendar tomorrow = getCalendarOneDayAfter(today);
        Calendar twoDaysAfter = getCalendarOneDayAfter(tomorrow);
        if ((calendar.compareTo(today) >= CONSTANT_CALENDAR_COMPARE) &&
            (calendar.compareTo(tomorrow) < CONSTANT_CALENDAR_COMPARE)) {
            return MessageConstants.MESSAGE_HEADER_TODAY;
        } else if (calendar.compareTo(twoDaysAfter) < CONSTANT_CALENDAR_COMPARE) {
            return MessageConstants.MESSAGE_HEADER_TOMORROW;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
            return sdf.format(calendar.getTime());
        }
    }

    private void updateOverview() {
        Calendar now = Calendar.getInstance();
        Calendar today = getCalendarToday();
        Calendar tomorrow = getCalendarOneDayAfter(today);
        Calendar twoDaysAfter = getCalendarOneDayAfter(tomorrow);

        int dueToday = CONSTANT_COUNT_NO_TASK, dueTomorrow = CONSTANT_COUNT_NO_TASK,
            overdue = CONSTANT_COUNT_NO_TASK, completed = CONSTANT_COUNT_NO_TASK;
        
        List<Task> allTasks = _QLLogic.getMasterList();
        for (int j = TASK_LIST_FIRST_TASK; j < allTasks.size(); ++j) {
            if (allTasks.get(j).getIsCompleted()) {
                completed++;
                continue;
            }

            Calendar due = allTasks.get(j).getDueDate();
            if (due == null) {
                continue;
            }

            if ((due.compareTo(today) >= CONSTANT_CALENDAR_COMPARE) &&
                (due.compareTo(tomorrow) < CONSTANT_CALENDAR_COMPARE)) {
                dueToday++;
            } else if ((due.compareTo(tomorrow) >= CONSTANT_CALENDAR_COMPARE) &&
                       (due.compareTo(twoDaysAfter) < CONSTANT_CALENDAR_COMPARE)) {
                dueTomorrow++;
            }

            if (due.compareTo(now) < CONSTANT_CALENDAR_COMPARE) {
                overdue++;
            }
        }

        _overview.setText(String.format(MessageConstants.MESSAGE_OVERVIEW, 
                                        dueToday, dueTomorrow,
                                        overdue, completed));
    }

    private Calendar getCalendarToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, CALENDAR_FIRST_HOUR);
        today.set(Calendar.MINUTE, CALENDAR_FIRST_MIN);
        today.set(Calendar.SECOND, CALENDAR_FIRST_SECOND);
        today.set(Calendar.MILLISECOND, CALENDAR_FIRST_MILLISECOND);
        return today;
    }

    private Calendar getCalendarOneDayAfter(Calendar day) {
        Calendar tomorrow = (Calendar) day.clone();
        tomorrow.add(Calendar.DATE, DATE_INCREMENT);
        return tomorrow;
    }
    
    // Method for actionListener
    public boolean executeCommand(String command) {
        if ((_executionThread == null) && (!command.trim().isEmpty())) {
            CommandExecution exec = new CommandExecution(command);
            _executionThread = new Thread(exec);
            _executionThread.start();
            setStatus(MessageConstants.MESSAGE_STATUS_PROCESSING);
            return true;
        }
        return false;
    }
    
    public void afterCommandExecution(String status) {
        if (!status.isEmpty()) {
            setStatus(status.toString());
        } else {
            setStatus(STATUS_NONE);
        }
        updateUI();
        _executionThread = null;
    }

    private void setStatus(String status) {
        _status.setText(status);
    }

    public void addCommandToCommandHistory(String command) {
        _commandHistory.addCommand(command);
    }

    public void showTips() {
        String tips = _commandTips.getTips(_command.getText());
        setTips(tips);
    }

    private void setTips(String tips) {
        if (!_tip.getText().equals(tips)) {
            _tip.setText(tips);
            _tip.setCaretPosition(CARET_DEFAULT_POSITION);
        }
    }

    public static void main(String[] args) {
        GUI g = new GUI();
    }

}
