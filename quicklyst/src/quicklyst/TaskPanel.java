package quicklyst;

import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

//@author A0112971J
public class TaskPanel extends JPanel {
    
    private static final int OFFSET_COMMON_PADDING = 5;
    private static final int OFFSET_INNER_PADDING = 10;
    
    private static final String MESSAGE_STARTS = "starts ";
    private static final String MESSAGE_DUE = "due ";

    private static final String PRIORITY_HIGH = "H";
    private static final String PRIORITY_MEDIUM = "M";
    private static final String PRIORITY_LOW = "L";

    private static final String STRING_EMPTY = "";
    private static final String STRING_DASH = " - ";
    private static final String STRING_SPACE = " ";
    
    private static final String PREFIX_INDEX = "#";

    private static final String FORMAT_DATE = "dd/MM/yyy";
    private static final String FORMAT_DATE_TIME = "dd/MM/yyy HH:mm";
    
    private JPanel priorityColorPane;
    private JLabel name;
    private JLabel index;
    private JLabel date;
    private JLabel priority;
    
    public TaskPanel(Task task, int taskIndex) {
        super(new SpringLayout());
        
        SpringLayout layout = (SpringLayout)this.getLayout();
        
        this.setBorder(new LineBorder(Color.BLACK));

        priorityColorPane = new JPanel();
        name = new JLabel(task.getName());
        index = new JLabel(PREFIX_INDEX + taskIndex);
        date = new JLabel(STRING_SPACE);
        priority = new JLabel();         
        
        displayColoredPanel(task);        
        displayDate(task);
        displayPriority(task);

        addComponents();
        setLayout(layout);        
    }
    
    private void displayColoredPanel(Task task) {
        if (task.getIsCompleted()) {
            this.setBackground(Color.CYAN);
        } else if (task.getIsOverdue()) {
            this.setBackground(Color.PINK);
        }
    }
    
    private void displayDate(Task task) {
        String start = getStartDate(task);
        String due = getDueDate(task);
        
        setDisplayDate(start, due);
    }

    private void displayPriority(Task task) {
        if (task.getPriority() != null) {
            priority.setText(task.getPriority());
            setPriorityColor(task);
        }
    }

    private void setPriorityColor(Task task) {
        switch (task.getPriority()) {
        case PRIORITY_HIGH:
            priorityColorPane.setBackground(Color.RED);
            break;
        case PRIORITY_MEDIUM:
            priorityColorPane.setBackground(Color.ORANGE);
            break;
        case PRIORITY_LOW:
            priorityColorPane.setBackground(Color.YELLOW);
            break;
        default:
            break;
        }
    }
    
    private void addComponents() {
        this.add(priorityColorPane);
        this.add(name);
        this.add(index);
        this.add(date);
        this.add(priority);
    }
  
    private void setLayout(SpringLayout layout) {
        
        setupTaskPaneConstraint(layout);

        setupPriorityColorPaneConstraint(layout);

        setupNameLabelConstraint(layout);

        setupTaskIndexLabelConstraint(layout);

        setupDateLabelConstraint(layout);

        setupPriorityLabelConstraint(layout);
    }

    private void setupTaskPaneConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.SOUTH, this, OFFSET_COMMON_PADDING,
                SpringLayout.SOUTH, date);
    }

    private void setupPriorityLabelConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.SOUTH, priority,
                -OFFSET_COMMON_PADDING, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, priority,
                -OFFSET_INNER_PADDING, SpringLayout.EAST, this);
    }

    private void setupDateLabelConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.WEST, date, OFFSET_INNER_PADDING,
                SpringLayout.EAST, priorityColorPane);
        layout.putConstraint(SpringLayout.NORTH, date, OFFSET_COMMON_PADDING,
                SpringLayout.SOUTH, name);
        layout.putConstraint(SpringLayout.EAST, date, -OFFSET_COMMON_PADDING,
                SpringLayout.WEST, priority);
    }

    private void setupTaskIndexLabelConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.EAST, index, -OFFSET_INNER_PADDING,
                SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, index, OFFSET_COMMON_PADDING,
                SpringLayout.NORTH, this);
    }

    private void setupNameLabelConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.WEST, name, OFFSET_INNER_PADDING,
                SpringLayout.EAST, priorityColorPane);
        layout.putConstraint(SpringLayout.NORTH, name, OFFSET_COMMON_PADDING,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, name, -OFFSET_COMMON_PADDING,
                SpringLayout.WEST, index);
    }

    private void setupPriorityColorPaneConstraint(SpringLayout layout) {
        layout.putConstraint(SpringLayout.WEST, priorityColorPane,
                OFFSET_COMMON_PADDING, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, priorityColorPane,
                OFFSET_COMMON_PADDING, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH, priorityColorPane,
                -OFFSET_COMMON_PADDING, SpringLayout.SOUTH, this);
    }
    
    private String getStartDate(Task task) {
        SimpleDateFormat dateOnly = new SimpleDateFormat(FORMAT_DATE); 
        SimpleDateFormat dateAndTime = new SimpleDateFormat(FORMAT_DATE_TIME); 
        
        String start = STRING_EMPTY;
        
        if (task.getStartDate() != null && task.getHasStartTime()) {
            start = dateAndTime.format(task.getStartDate().getTime());
        } else if (task.getStartDate() != null) {
            start = dateOnly.format(task.getStartDate().getTime());
        }
        return start;
    }
    
    private String getDueDate(Task task) {
        SimpleDateFormat dateOnly = new SimpleDateFormat(FORMAT_DATE); 
        SimpleDateFormat dateAndTime = new SimpleDateFormat(FORMAT_DATE_TIME); 
        
        String due = STRING_EMPTY;
        
        if (task.getDueDate() != null && task.getHasDueTime()) {
            due = dateAndTime.format(task.getDueDate().getTime());
        } else if (task.getDueDate() != null) {
            due = dateOnly.format(task.getDueDate().getTime());
        }
        return due;
    }
    
    private void setDisplayDate(String start, String due) {
        if ((!start.isEmpty()) && (!due.isEmpty())) {
            date.setText(start + STRING_DASH + due);
        } else if (!start.isEmpty()) {
            date.setText(MESSAGE_STARTS + start);
        } else if (!due.isEmpty()) {
            date.setText(MESSAGE_DUE + due);
        }
    }
}
