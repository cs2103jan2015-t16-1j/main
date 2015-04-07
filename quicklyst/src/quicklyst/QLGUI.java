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
    private static final String TITLE = "Quicklyst";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    private static final String UNDO = "Undo action key";
    private static final String REDO = "Redo action key";

    private final static Logger LOGGER = Logger
            .getLogger(QLGUI.class.getName());

    private JPanel _taskList;
    private JLabel _overview;
    private JPanel overviewPane;
    private JTextArea _feedback;
    private JTextField _command;

    public class TaskMouseListener implements MouseListener {
        private Task task;
        private JLabel hover;
        
        public TaskMouseListener (Task task) {
            this.task = task;
        }
        
        public void mouseEntered(MouseEvent m) {
            hover = new JLabel();
            overviewPane.add(hover, BorderLayout.CENTER);
            hover.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            
            String title = task.getName(), description, priority;
            String start = "", due = "", displayTime = "";
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            if (task.getDescription() != null) {
                description = task.getDescription();
            } else {
                description = "";
            }
            
            if (task.getPriority() != null) {
                priority = task.getPriority();
            } else {
                priority = "";
            }
            
            if (task.getStartDate() != null && task.getHasStartTime()) {
                start = dateAndTime.format(task.getStartDate().getTime());
            } else if (task.getStartDate() != null) {
                start = date.format(task.getStartDate().getTime());
            }

            if (task.getDueDate() != null && task.getHasDueTime()) {
                due = dateAndTime.format(task.getDueDate().getTime());
            } else if (task.getDueDate() != null) {
                due = date.format(task.getDueDate().getTime());
            }
            
            if (!start.isEmpty() && !due.isEmpty()) {
                displayTime = (start + " - " + due);
            } else if (!start.isEmpty()) {
                displayTime = "starts " + start;
            } else if (!due.isEmpty()) {
                displayTime = "due " + due;                
            } else {
                displayTime = "";
            }
            
            hover.setText(String.format("<html><u>Task Detail</u><br>"
                    + "Title: %s<br>" + "Description: %s<br>"
                    + "Priority: %s<br>" + "Time: %s<br>", 
                    title, description, priority, displayTime));
            hover.setOpaque(true);
        }
        
        @Override
        public void mouseExited(MouseEvent arg0) {
            hover.setVisible(false);
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }


        @Override
        public void mousePressed(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }  
    }
    
    public class commandListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            LOGGER.info(String.format("user entered: %s",
                    _command.getText()));
            StringBuilder fb = new StringBuilder();
            QLLogic.executeCommand(_command.getText(), fb);

            if (!fb.toString().isEmpty()) {
                _feedback.append(fb.toString() + "\r\n");
            }
            updateUI();
            _command.setText("");
        }
    }
    
    public class hotKeysAction extends AbstractAction {
        private String keyPressed;
       
        public hotKeysAction (String keyPressed) {
            this.keyPressed = keyPressed;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            LOGGER.info(keyPressed);
            StringBuilder fb = new StringBuilder();
            if (keyPressed.equalsIgnoreCase("undo")) {
                QLLogic.executeCommand("undo", fb);
            } else {
                QLLogic.executeCommand("redo", fb);                
            }

            if (!fb.toString().isEmpty()) {
                _feedback.append(fb.toString() + "\r\n");
            }
            updateUI();
        }
        
    }
    
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
        overviewPane = new JPanel(new BorderLayout());
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
        _command.addActionListener(new commandListener());

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

        /*Action undoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                LOGGER.info("Undo.");
                StringBuilder fb = new StringBuilder();
                QLLogic.executeCommand("undo", fb);

                if (!fb.toString().isEmpty()) {
                    _feedback.append(fb.toString() + "\r\n");
                }
                updateUI();
            }
        };*/
        Action undoAction = new hotKeysAction("Undo");
        Action redoAction = new hotKeysAction("Redo");
        
        
        /*Action redoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            LOGGER.info("Redo.");
            StringBuilder fb = new StringBuilder();
            QLLogic.executeCommand("redo", fb);

            if (!fb.toString().isEmpty()) {
                _feedback.append(fb.toString() + "\r\n");
            }
            updateUI();
          }
        };*/

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
        QLLogic.setup(new StringBuilder());
        updateUI();

    }

    private void updateUI() {
        _taskList.removeAll();
        int i = 1, taskIndex = 1;
        String curDate = "";
        String prevDate = "";
        String display = "";

        List<Task> tasks = QLLogic.getDisplayList();
        for (Task task : tasks) {
            SpringLayout singleTaskLayout = new SpringLayout();
            JPanel singleTaskPane = new JPanel(singleTaskLayout);

            JPanel priorityColorPane = new JPanel();
            JLabel name = new JLabel(task.getName());
            JLabel index = new JLabel("#" + taskIndex);
            JLabel date = new JLabel(" ");
            JLabel priority = new JLabel();
            
            SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyy"); 
            SimpleDateFormat dateAndTime = new SimpleDateFormat("dd/MM/yyy HH:mm");            
            
            display = task.getName();
            singleTaskPane.setBorder(new LineBorder(Color.BLACK));
            
            if (task.getIsCompleted()) {
                singleTaskPane.setBackground(Color.CYAN);
            } else if (task.getIsOverdue()) {
                singleTaskPane.setBackground(Color.PINK);
            }
            
            String start = "", due = "";
            
            if (task.getStartDate() != null && task.getHasStartTime()) {
                start = dateAndTime.format(task.getStartDate().getTime());
            } else if (task.getStartDate() != null) {
                start = dateOnly.format(task.getStartDate().getTime());
            }
            
            if (task.getDueDate() != null && task.getHasDueTime()) {
                due = dateAndTime.format(task.getDueDate().getTime());
            } else if (task.getDueDate() != null) {
                due = dateOnly.format(task.getDueDate().getTime());
            }
            
            if ((!start.isEmpty()) && (!due.isEmpty())) {
                date.setText(start + " - " + due);
            } else if (!start.isEmpty()) {
                date.setText(start);
            } else if (!due.isEmpty()) {
                date.setText(due);
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
            singleTaskPane.setToolTipText(display);
            singleTaskPane.addMouseListener(new TaskMouseListener(task));
            
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
