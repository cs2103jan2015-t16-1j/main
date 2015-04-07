package quicklyst;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
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

import java.util.logging.Logger;

public class QLGUI extends JFrame implements Observer {
    private static final String HOTKEY_REDO = "control Y";
    private static final String HOTKEY_UNDO = "control Z";
    private static final String TITLE = "Quicklyst";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    private static final String UNDO = "Undo";
    private static final String REDO = "Redo";

    private static final Logger LOGGER = Logger
            .getLogger(QLGUI.class.getName());

    private JPanel _taskList;
    private JPanel _overviewPane;
    private JLabel _overview;
    private JTextArea _feedback;
    private JTextField _command;
      
    public QLGUI() {
        super(TITLE);

        LOGGER.info("creating GUI");


        Container contentPane = this.getContentPane();
        SpringLayout layout = new SpringLayout();

        contentPane.setLayout(layout);

        LOGGER.info("creating tasklist");
        JScrollPane taskListScroll = setupTaskListPanel();

        LOGGER.info("creating overview panel");
        setupOverviewPanel();

        LOGGER.info("creating feedback");
        JScrollPane feedbackScroll = setupFeedback();

        LOGGER.info("creating command text field");
        setupCommand();

        setupHotkeys();
        
        LOGGER.info("adding components to main panel");
        addComponents(taskListScroll, feedbackScroll,
                      _command, _overviewPane);

        LOGGER.info("set constraints for components");
        setConstraintsForMainFrame(layout, contentPane, taskListScroll,
                                   _overviewPane, feedbackScroll, _command);

        LOGGER.info("finalizing GUI");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        LOGGER.info("get taskList from QLLogic");
        QLLogic.setup(new StringBuilder());
        updateUI();

    }

    private void setupHotkeys() {
        setupActionMap();
        setupInputMap();
    }

    private void setupInputMap() {
        _command.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(HOTKEY_UNDO), UNDO);
        _command.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(HOTKEY_UNDO), UNDO);
        _command.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(HOTKEY_UNDO), UNDO);
        
        _command.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(HOTKEY_REDO), REDO);
        _command.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(HOTKEY_REDO), REDO);
        _command.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(HOTKEY_REDO), REDO);
    }

    private void setupActionMap() {
        Action undoAction = new HotkeysAction("Undo", this);
        Action redoAction = new HotkeysAction("Redo", this);

        _command.getActionMap().put(UNDO, undoAction);
        _command.getActionMap().put(REDO, redoAction);
    }

    private void addComponents(JScrollPane taskListScroll, JScrollPane feedbackScroll,
                               JTextField command, JPanel overview) {
        add(_command);
        add(taskListScroll);
        add(feedbackScroll);
        add(_overviewPane);
    }

    private void setupCommand() {
        _command = new JTextField();
        LOGGER.info("adding actionListener to command text field");
        _command.addActionListener(new CommandListener(_command, this));
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

    private void setupOverviewPanel() {
        _overviewPane = new JPanel(new BorderLayout());
        _overviewPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                Color.BLACK));

        _overview = new JLabel();
        _overview.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        _overviewPane.add(_overview, BorderLayout.NORTH);
    }

    private JScrollPane setupTaskListPanel() {
        _taskList = new JPanel(new GridBagLayout());
        JPanel taskListBorderPane = new JPanel(new BorderLayout());
        taskListBorderPane.add(_taskList, BorderLayout.NORTH);
        JScrollPane taskListScroll = new JScrollPane(taskListBorderPane);
        return taskListScroll;
    }

    private void updateUI() {
        _taskList.removeAll();
        int i = 1, taskIndex = 1;
        String curDate = "";
        String prevDate = "";

        List<Task> tasks = QLLogic.getDisplayList();
        for (Task task : tasks) {
            
            TaskPanel singleTaskPane = new TaskPanel(task, taskIndex);
            
            GridBagConstraints con = new GridBagConstraints();
            con.insets = new Insets(5, 5, 5, 5);
            con.weightx = 1;
            con.anchor = GridBagConstraints.NORTHEAST;
            con.fill = GridBagConstraints.HORIZONTAL;
            con.gridx = 0;
            con.gridy = i - 1;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
            if (task.getDueDate() != null) {
                curDate = sdf.format(task.getDueDate().getTime());
            } else {
                curDate = "No due date";
            }

            if (!curDate.equals(prevDate)) {

                JLabel dateLabel = new JLabel(curDate);
                _taskList.add(dateLabel, con);
                i++;
                con.gridy = i - 1;
                
            }

            _taskList.add(singleTaskPane, con);
            singleTaskPane.addMouseListener(new TaskMouseListener(task, _overviewPane));
            
            i++;
            taskIndex++;
            prevDate = curDate;
        }

        _taskList.revalidate();
        _taskList.repaint();

        // update the overview based on dates
        updateOverview();
    }

    private void updateOverview() {
        int dueToday = 0, dueTomorrow = 0, overdue = 0, completed = 0;
        Calendar now = Calendar.getInstance();
        Calendar today = (Calendar) now.clone();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Calendar twoDaysAfter = (Calendar) tomorrow.clone();
        twoDaysAfter.add(Calendar.DAY_OF_MONTH, 1);
        
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
        
        _overview.setText(String.format("<html><u>Overview</u><br>"
                + "%d due today<br>" + "%d due tomorrow<br>" + "%d overdue<br>"
                + "%d completed</html>", dueToday, dueTomorrow, overdue,
                completed));
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
        layout.putConstraint(SpringLayout.WEST, commandTextField, 10,
                SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.EAST, commandTextField, -10,
                SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, commandTextField, -10,
                SpringLayout.SOUTH, contentPane);
    }
    
    private void setConstraintsForTaskListScroll(SpringLayout layout,
                 Container contentPane, JComponent taskListScroll,
                 JComponent commandTextField) {
        layout.putConstraint(SpringLayout.WEST, taskListScroll, 10,
                SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, taskListScroll, 10,
                SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, taskListScroll, -10,
                SpringLayout.NORTH, commandTextField);
        layout.getConstraints(taskListScroll).setWidth(Spring.constant(385));
    }
    
    private void setConstraintsForOverviewPane(SpringLayout layout,
                 Container contentPane, JComponent taskListScroll,
                 JComponent overviewPane) {
        layout.putConstraint(SpringLayout.WEST, overviewPane, 10,
                SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, overviewPane, 10,
                SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, overviewPane, -10,
                SpringLayout.EAST, contentPane);
        layout.getConstraints(overviewPane).setHeight(Spring.constant(220));
    }
    
    private void setConstraintsForFeedbackScroll(SpringLayout layout,
                 Container contentPane, JComponent taskListScroll,
                 JComponent overviewPane, JComponent feedbackScroll) {
        layout.putConstraint(SpringLayout.WEST, feedbackScroll, 10,
                SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, feedbackScroll, 10,
                SpringLayout.SOUTH, overviewPane);
        layout.putConstraint(SpringLayout.SOUTH, feedbackScroll, 0,
                SpringLayout.SOUTH, taskListScroll);
        layout.putConstraint(SpringLayout.EAST, feedbackScroll, -10,
                SpringLayout.EAST, contentPane);
    }
    
    public void executeCommand(String command) {
        StringBuilder fb = new StringBuilder();
        QLLogic.executeCommand(command, fb);
    
        if (!fb.toString().isEmpty()) {
            _feedback.append(fb.toString() + "\r\n");
        }
        updateUI();
    }
    
    public void update (Observable o, Object arg) {
        
    }

    public static void main(String[] args) {
        QLGUI g = new QLGUI();
    }

}
