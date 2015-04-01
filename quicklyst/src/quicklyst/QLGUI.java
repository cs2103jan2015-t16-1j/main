package quicklyst;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
    private static final String TITLE = "Quicklyst";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    private static final String UNDO = "Undo action key";
    private static final String REDO = "Redo action key";

    private final static Logger LOGGER = Logger
            .getLogger(QLGUI.class.getName());

    private JPanel _taskList;
    private JLabel _overview;
    private JTextArea _feedback;
    private JTextField _command;

    public QLGUI() {
        super(TITLE);

        LOGGER.info("creating GUI");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = this.getContentPane();
        SpringLayout layout = new SpringLayout();

        contentPane.setLayout(layout);

        LOGGER.info("creating tasklist");
        _taskList = new JPanel(new GridBagLayout());
        JPanel taskListBorderPane = new JPanel(new BorderLayout());
        taskListBorderPane.add(_taskList, BorderLayout.NORTH);
        JScrollPane taskListScroll = new JScrollPane(taskListBorderPane);

        LOGGER.info("creating overview panel");
        JPanel overviewPane = new JPanel(new BorderLayout());
        overviewPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                Color.BLACK));

        _overview = new JLabel();
        _overview.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        overviewPane.add(_overview, BorderLayout.NORTH);

        LOGGER.info("creating feedback");
        _feedback = new JTextArea();
        _feedback.setEditable(false);
        _feedback.setLineWrap(true);
        _feedback.setWrapStyleWord(true);
        JPanel feedbackBorderPane = new JPanel(new BorderLayout());
        feedbackBorderPane.setBackground(_feedback.getBackground());
        feedbackBorderPane.add(_feedback, BorderLayout.SOUTH);
        JScrollPane feedbackScroll = new JScrollPane(feedbackBorderPane);

        LOGGER.info("creating command text field");
        _command = new JTextField();
        LOGGER.info("adding actionListener to command text field");
        _command.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LOGGER.info(String.format("user entered: %s",
                        _command.getText()));
                StringBuilder fb = new StringBuilder();
                List<Task> tasks = QLLogic.executeCommand(_command.getText(),
                        fb);
                assert tasks != null;

                if (!fb.toString().isEmpty()) {
                    _feedback.append(fb.toString() + "\r\n");
                }
                updateUIWithTaskList(tasks);
                _command.setText("");
            }
        });

        LOGGER.info("adding components to main panel");
        add(_command);
        add(taskListScroll);
        add(feedbackScroll);
        add(overviewPane);

        LOGGER.info("set constraints for components");
        setConstraintsForMainFrame(layout, contentPane, taskListScroll,
                overviewPane, feedbackScroll, _command);

        LOGGER.info("finalizing GUI");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(true);

        Action undoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // undo();
                _feedback.append(UNDO + "\r\n");
            }
        };
        Action redoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // redo();
                _feedback.append(REDO + "\r\n");
            }
        };

        _command.getActionMap().put(UNDO, undoAction);
        _command.getActionMap().put(REDO, redoAction);

        InputMap[] inputMaps = new InputMap[] {
                _command.getInputMap(JComponent.WHEN_FOCUSED),
                _command.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                _command.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), };
        for (InputMap i : inputMaps) {
            i.put(KeyStroke.getKeyStroke("control Z"), UNDO);
            i.put(KeyStroke.getKeyStroke("control Y"), REDO);
        }

        LOGGER.info("get taskList from QLLogic");
        List<Task> t = QLLogic.setup("save.json");
        assert t != null;
        updateUIWithTaskList(t);

    }

    private void updateUIWithTaskList(List<Task> tasks) {
        _taskList.removeAll();
        int i = 1, taskIndex = 1;
        String curDate = "";
        String prevDate = "";

        for (Task task : tasks) {
            SpringLayout singleTaskLayout = new SpringLayout();
            JPanel singleTaskPane = new JPanel(singleTaskLayout);

            JPanel priorityColorPane = new JPanel();
            JLabel name = new JLabel(task.getName());
            JLabel index = new JLabel("#" + taskIndex);
            JLabel date = new JLabel(" ");
            JLabel priority = new JLabel();

            singleTaskPane.setBorder(new LineBorder(Color.BLACK));
            if (task.getIsCompleted()) {
                singleTaskPane.setBackground(Color.CYAN);
            } else if (task.getIsOverdue()) {
                singleTaskPane.setBackground(Color.PINK);
            }

            if (task.getDueDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                date.setText("due " + sdf.format(task.getDueDate().getTime()));
            }

            if (task.getPriority() != null) {

                priority.setText(task.getPriority());

                switch (task.getPriority()) {
                case "H":
                    priorityColorPane.setBackground(Color.RED);
                    break;
                case "M":
                    priorityColorPane.setBackground(Color.ORANGE);
                    break;
                case "L":
                    priorityColorPane.setBackground(Color.YELLOW);
                    break;
                default:
                    break;
                }
            }

            singleTaskPane.add(priorityColorPane);
            singleTaskPane.add(name);
            singleTaskPane.add(index);
            singleTaskPane.add(date);
            singleTaskPane.add(priority);

            singleTaskLayout.putConstraint(SpringLayout.SOUTH, singleTaskPane,
                    5, SpringLayout.SOUTH, date);

            singleTaskLayout.putConstraint(SpringLayout.WEST,
                    priorityColorPane, 5, SpringLayout.WEST, singleTaskPane);
            singleTaskLayout.putConstraint(SpringLayout.NORTH,
                    priorityColorPane, 5, SpringLayout.NORTH, singleTaskPane);
            singleTaskLayout.putConstraint(SpringLayout.SOUTH,
                    priorityColorPane, -5, SpringLayout.SOUTH, singleTaskPane);

            singleTaskLayout.putConstraint(SpringLayout.WEST, name, 10,
                    SpringLayout.EAST, priorityColorPane);
            singleTaskLayout.putConstraint(SpringLayout.NORTH, name, 5,
                    SpringLayout.NORTH, singleTaskPane);
            singleTaskLayout.putConstraint(SpringLayout.EAST, name, -5,
                    SpringLayout.WEST, index);

            singleTaskLayout.putConstraint(SpringLayout.EAST, index, -10,
                    SpringLayout.EAST, singleTaskPane);
            singleTaskLayout.putConstraint(SpringLayout.NORTH, index, 5,
                    SpringLayout.NORTH, singleTaskPane);

            singleTaskLayout.putConstraint(SpringLayout.WEST, date, 10,
                    SpringLayout.EAST, priorityColorPane);
            singleTaskLayout.putConstraint(SpringLayout.NORTH, date, 5,
                    SpringLayout.SOUTH, name);
            singleTaskLayout.putConstraint(SpringLayout.EAST, date, -5,
                    SpringLayout.WEST, priority);

            singleTaskLayout.putConstraint(SpringLayout.SOUTH, priority, -5,
                    SpringLayout.SOUTH, singleTaskPane);
            singleTaskLayout.putConstraint(SpringLayout.EAST, priority, -10,
                    SpringLayout.EAST, singleTaskPane);

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

            i++;
            taskIndex++;
            prevDate = curDate;
        }

        _taskList.revalidate();
        _taskList.repaint();

        // update the overview based on dates
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

        for (int j = 0; j < tasks.size(); ++j) {
            if (tasks.get(j).getIsCompleted()) {
                completed++;
                continue;
            }
            Calendar due = tasks.get(j).getDueDate();
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

        layout.putConstraint(SpringLayout.WEST, commandTextField, 10,
                SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.EAST, commandTextField, -10,
                SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, commandTextField, -10,
                SpringLayout.SOUTH, contentPane);

        layout.putConstraint(SpringLayout.WEST, taskListScroll, 10,
                SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, taskListScroll, 10,
                SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.SOUTH, taskListScroll, -10,
                SpringLayout.NORTH, commandTextField);
        layout.getConstraints(taskListScroll).setWidth(Spring.constant(385));

        layout.putConstraint(SpringLayout.WEST, overviewPane, 10,
                SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, overviewPane, 10,
                SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.EAST, overviewPane, -10,
                SpringLayout.EAST, contentPane);
        layout.getConstraints(overviewPane).setHeight(Spring.constant(220));

        layout.putConstraint(SpringLayout.WEST, feedbackScroll, 10,
                SpringLayout.EAST, taskListScroll);
        layout.putConstraint(SpringLayout.NORTH, feedbackScroll, 10,
                SpringLayout.SOUTH, overviewPane);
        layout.putConstraint(SpringLayout.SOUTH, feedbackScroll, 0,
                SpringLayout.SOUTH, taskListScroll);
        layout.putConstraint(SpringLayout.EAST, feedbackScroll, -10,
                SpringLayout.EAST, contentPane);
    }
    
    public void update (Observable o, Object arg) {
        
    }

    public static void main(String[] args) {
        QLGUI g = new QLGUI();

    }
}
