package quicklyst;

//import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
//import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
//import javax.swing.InputMap;
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

//import com.sun.glass.events.KeyEvent;







import java.util.logging.Logger;



public class QLGUI extends JFrame implements Observer { 
    private static final String MESSAGE_TITLE = "Quicklyst";
    private static final String MESSAGE_NO_DUE_DATE = "No due date";
    private static final String MESSAGE_CREATING_GUI = "creating GUI";
    private static final String MESSAGE_CREATING_TASKLIST = "creating tasklist";
    private static final String MESSAGE_CREATING_OVERVIEW = "creating overview panel";
    private static final String MESSAGE_CREATING_FEEDBACK = "creating feedback";
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
    
    private static final int PADDING_OVERVIEWPANE = 1;
    private static final int PADDING_OVERVIEW = 3;
    
    private static final int OFFSET_COMMAND_WEST = 10;
    private static final int OFFSET_COMMAND_EAST = -10;
    private static final int OFFSET_COMMAND_SOUTH = -10;
    
    private static final int OFFSET_TASKLISTSCROLL_WEST = 10;
    private static final int OFFSET_TASKLISTSCROLL_NORTH = 10;
    private static final int OFFSET_TASKLISTSCROLL_SOUTH = -10;
    
    private static final int OFFSET_OVERVIEWPANE_WEST = 10;
    private static final int OFFSET_OVERVIEWPANE_NORTH = 10;
    private static final int OFFSET_OVERVIEWPANE_EAST = -10;

    private static final int OFFSET_FEEDBACKSCROLL_WEST = 10;
    private static final int OFFSET_FEEDBACKSCROLL_NORTH = 10;
    private static final int OFFSET_FEEDBACKSCROLL_SOUTH = 0;
    private static final int OFFSET_FEEDBACKSCROLL_EAST = -10;
    
    private static final int OFFSET_SINGLETASK = 5;
    
    private static final Logger LOGGER = Logger
            .getLogger(QLGUI.class.getName());

    private JPanel _taskList;
    private JPanel _overviewPane;
    private JLabel _overview;
    private JTextArea _feedback;
    private JTextField _command;
       
    class KeyPressed implements KeyListener {
    
        Stack<String> _commands;
        Stack<String> _prevCmd;
        Stack<String> _nextCmd;
        
        public KeyPressed(Stack<String> commands) {
            this._commands = commands;
            _nextCmd = new Stack<String>();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub
            int keyCode = e.getKeyCode();
            switch (keyCode) {
            case 38:
                _feedback.append("Up button" + "\r\n");
                _prevCmd = _commands;
                if (_prevCmd.size() > 0) {
                    _feedback.append(_prevCmd.peek());
                    _command.setText(_prevCmd.peek());
                    _feedback.append("here" + "\r\n");
                    _nextCmd.add(_prevCmd.remove(_prevCmd.size()-1));
                    _feedback.append("there" + "\r\n");
                }
                break;
            case 40:
                _feedback.append("Down button" + "\r\n");
                if (_nextCmd.size() > 0) {
                    _feedback.append(_nextCmd.peek());
                    _command.setText(_nextCmd.peek());
                    _nextCmd.remove(_nextCmd.size()-1);
                }
                break;
            default:
                break;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }
        

    }
    
    public QLGUI() {
        super(MESSAGE_TITLE);

        LOGGER.info(MESSAGE_CREATING_GUI);

        Container contentPane = this.getContentPane();
        SpringLayout layout = new SpringLayout();

        contentPane.setLayout(layout);

        LOGGER.info(MESSAGE_CREATING_TASKLIST);
        JScrollPane taskListScroll = setupTaskListPanel();

        LOGGER.info(MESSAGE_CREATING_OVERVIEW);
        setupOverviewPanel();

        LOGGER.info(MESSAGE_CREATING_FEEDBACK);
        JScrollPane feedbackScroll = setupFeedback();

        LOGGER.info(MESSAGE_CREATING_COMMAND_TEXT_FIELD);
        //setupCommand();
        Stack<String> commandlist = setupCommand();

        setupHotkeys();
        
        _command.addKeyListener(new KeyPressed(commandlist));
        
        LOGGER.info(MESSAGE_ADDING_COMPONENTS);
        addComponents(taskListScroll, feedbackScroll, _command, _overviewPane);

        LOGGER.info(MESSAGE_SET_CONSTRAINTS);
        setConstraintsForMainFrame(layout, contentPane, taskListScroll,
                _overviewPane, feedbackScroll, _command);

        LOGGER.info(MESSAGE_FINALIZING_GUI);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        LOGGER.info(MESSAGE_GET_TASK_LIST_FROM_QL_LOGIC);
        QLLogic.setup(new StringBuilder());
        updateUI();

    }

    private JScrollPane setupTaskListPanel() {
        _taskList = new JPanel(new GridBagLayout());
        JPanel taskListBorderPane = new JPanel(new BorderLayout());
        taskListBorderPane.add(_taskList, BorderLayout.NORTH);
        JScrollPane taskListScroll = new JScrollPane(taskListBorderPane);
        return taskListScroll;
    }

    private void setupOverviewPanel() {
        _overviewPane = new JPanel(new BorderLayout());
        _overviewPane.setBorder(BorderFactory.createMatteBorder(
                PADDING_OVERVIEWPANE, PADDING_OVERVIEWPANE,
                PADDING_OVERVIEWPANE, PADDING_OVERVIEWPANE, Color.BLACK));

        _overview = new JLabel();
        _overview.setBorder(BorderFactory.createEmptyBorder(PADDING_OVERVIEW,
                PADDING_OVERVIEW, PADDING_OVERVIEW, PADDING_OVERVIEW));
        _overviewPane.add(_overview, BorderLayout.NORTH);
    }

    private JScrollPane setupFeedback() {
        _feedback = new JTextArea();
        _feedback.setEditable(false);
        _feedback.setLineWrap(true);
        _feedback.setWrapStyleWord(true);
        JPanel feedbackBorderPane = new JPanel(new BorderLayout());
        feedbackBorderPane.setBackground(_feedback.getBackground());
        feedbackBorderPane.add(_feedback, BorderLayout.SOUTH);
        JScrollPane feedbackScroll = new JScrollPane(feedbackBorderPane);
        return feedbackScroll;
    }

    /*private void setupCommand() {
        _command = new JTextField();
        LOGGER.info("adding actionListener to command text field");
        _command.addActionListener(new CommandListener(_command, this));
    }*/
    
    private Stack<String> setupCommand() {
        _command = new JTextField();
        LOGGER.info("adding actionListener to command text field");
        CommandListener c = new CommandListener(_command, this);
        _command.addActionListener(c);
        
        return c.getCommandList();
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

    private void addComponents(JScrollPane taskListScroll,
            JScrollPane feedbackScroll, JTextField command, JPanel overview) {
        add(_command);
        add(taskListScroll);
        add(feedbackScroll);
        add(_overviewPane);
    }

    private void setConstraintsForMainFrame(SpringLayout layout,
            Container contentPane, JComponent taskListScroll,
            JComponent overviewPane, JComponent feedbackScroll,
            JComponent commandTextField) {

        setConstraintsForCommandTextField(layout, contentPane, commandTextField);
        setConstraintsForTaskListScroll(layout, contentPane, taskListScroll,
                commandTextField);
        setConstraintsForOverviewPane(layout, contentPane, taskListScroll,
                overviewPane);
        setConstraintsForFeedbackScroll(layout, contentPane, taskListScroll,
                overviewPane, feedbackScroll);
    }

    private void setConstraintsForCommandTextField(SpringLayout layout,
            Container contentPane, JComponent commandTextField) {
        layout.putConstraint(SpringLayout.WEST, commandTextField, OFFSET_COMMAND_WEST,
                SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.EAST, commandTextField, OFFSET_COMMAND_EAST,
                SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, commandTextField, OFFSET_COMMAND_SOUTH,
                SpringLayout.SOUTH, contentPane);
    }

    private void setConstraintsForTaskListScroll(SpringLayout layout,
            Container contentPane, JComponent taskListScroll,
            JComponent commandTextField) {
        layout.putConstraint(SpringLayout.WEST, taskListScroll,
                OFFSET_TASKLISTSCROLL_WEST, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, taskListScroll,
                OFFSET_TASKLISTSCROLL_NORTH, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, taskListScroll,
                OFFSET_TASKLISTSCROLL_SOUTH, SpringLayout.NORTH,
                commandTextField);
        layout.getConstraints(taskListScroll).setWidth(Spring.constant(385));
    }

    private void setConstraintsForOverviewPane(SpringLayout layout,
            Container contentPane, JComponent taskListScroll,
            JComponent overviewPane) {
        layout.putConstraint(SpringLayout.WEST, overviewPane,
                OFFSET_OVERVIEWPANE_WEST, SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, overviewPane,
                OFFSET_OVERVIEWPANE_NORTH, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, overviewPane,
                OFFSET_OVERVIEWPANE_EAST, SpringLayout.EAST, contentPane);
        layout.getConstraints(overviewPane).setHeight(Spring.constant(220));
    }

    private void setConstraintsForFeedbackScroll(SpringLayout layout,
            Container contentPane, JComponent taskListScroll,
            JComponent overviewPane, JComponent feedbackScroll) {
        layout.putConstraint(SpringLayout.WEST, feedbackScroll, OFFSET_FEEDBACKSCROLL_WEST,
                SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, feedbackScroll, OFFSET_FEEDBACKSCROLL_NORTH,
                SpringLayout.SOUTH, overviewPane);
        layout.putConstraint(SpringLayout.SOUTH, feedbackScroll, OFFSET_FEEDBACKSCROLL_SOUTH,
                SpringLayout.SOUTH, taskListScroll);
        layout.putConstraint(SpringLayout.EAST, feedbackScroll, OFFSET_FEEDBACKSCROLL_EAST,
                SpringLayout.EAST, contentPane);
    }
    
    private void updateUI() {
        _taskList.removeAll();
        int taskPosition = STARTING_TASK_POSITION, taskIndex = STARTING_TASK_INDEX;
        String curDate = EMPTY_STRING;
        String prevDate = EMPTY_STRING;

        List<Task> tasks = QLLogic.getDisplayList();
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

            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
            if (task.getDueDate() != null) {
                curDate = sdf.format(task.getDueDate().getTime());
            } else {
                curDate = MESSAGE_NO_DUE_DATE;
            }

            if (!curDate.equals(prevDate)) {

                JLabel dateLabel = new JLabel(curDate);
                _taskList.add(dateLabel, con);
                taskPosition++;
                con.gridy = taskPosition - 1;
            }

            _taskList.add(singleTaskPane, con);
            singleTaskPane.addMouseListener(new TaskMouseListener(task,
                    _overviewPane));

            taskPosition++;
            taskIndex++;
            prevDate = curDate;
        }

        _taskList.revalidate();
        _taskList.repaint();

        // update the overview based on dates
        updateOverview();
    }

    private void updateOverview() {
        Calendar now = Calendar.getInstance();
        Calendar today = setCalendarToday(now);
        Calendar tomorrow = setCalendarTomorrow(today); 
        Calendar twoDaysAfter = setCalendarTomorrow(tomorrow);

        int dueToday = 0, dueTomorrow = 0, overdue = 0, completed = 0;
        List<Task> allTasks = QLLogic.getFullList();
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

        _overview.setText(String.format(FORMAT_OVERVIEW, dueToday,
                                        dueTomorrow, overdue, completed));
    }

    private Calendar setCalendarToday(Calendar now) {
        Calendar today = (Calendar) now.clone();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }
    
    private Calendar setCalendarTomorrow(Calendar today) {
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        return tomorrow;
    }

    // Method for actionListener
    public void executeCommand(String command) {
        StringBuilder fb = new StringBuilder();
        QLLogic.executeCommand(command, fb);

        if (!fb.toString().isEmpty()) {
            _feedback.append(fb.toString() + "\r\n");
        }
        updateUI();
    }

    public void update(Observable o, Object arg) {

    }

    public static void main(String[] args) {
        QLGUI g = new QLGUI();
    }


}
