package quicklyst;

import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

public class TaskPanel extends JPanel {
    private static final String TO = " - ";
    private static final String STRING_ONE_SPACE = " ";
    private static final String PREFIX_INDEX = "#";
    private static final String STRING_EMPTY = "";
    private static final String FORMAT_DATE = "dd/MM/yyy";
    private static final String FORMAT_DATE_TIME = "dd/MM/yyy HH:mm";
    
    JPanel priorityColorPane;
    JLabel name;
    JLabel index;
    JLabel date;
    JLabel priority;
    
    public TaskPanel(Task task, int taskIndex) {
        super(new SpringLayout());
        SpringLayout layout = (SpringLayout)this.getLayout();
        
        this.setBorder(new LineBorder(Color.BLACK));

        priorityColorPane = new JPanel();
        name = new JLabel(task.getName());
        index = new JLabel(PREFIX_INDEX + taskIndex);
        date = new JLabel(STRING_ONE_SPACE);
        priority = new JLabel();         
        
        displayPanelColor(task);        
        displayDate(task);
        displayPriority(task);

        addComponents();
        setLayout(layout);        
    }
    
    private void displayPanelColor(Task task) {
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
    }
    
    private void addComponents() {
        this.add(priorityColorPane);
        this.add(name);
        this.add(index);
        this.add(date);
        this.add(priority);
    }
  
    private void setLayout(SpringLayout layout) {
        layout.putConstraint(SpringLayout.SOUTH, this,
                5, SpringLayout.SOUTH, date);

        layout.putConstraint(SpringLayout.WEST,
                priorityColorPane, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH,
                priorityColorPane, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.SOUTH,
                priorityColorPane, -5, SpringLayout.SOUTH, this);

        layout.putConstraint(SpringLayout.WEST, name, 10,
                SpringLayout.EAST, priorityColorPane);
        layout.putConstraint(SpringLayout.NORTH, name, 5,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, name, -5,
                SpringLayout.WEST, index);

        layout.putConstraint(SpringLayout.EAST, index, -10,
                SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, index, 5,
                SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, date, 10,
                SpringLayout.EAST, priorityColorPane);
        layout.putConstraint(SpringLayout.NORTH, date, 5,
                SpringLayout.SOUTH, name);
        layout.putConstraint(SpringLayout.EAST, date, -5,
                SpringLayout.WEST, priority);

        layout.putConstraint(SpringLayout.SOUTH, priority, -5,
                SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, priority, -10,
                SpringLayout.EAST, this);
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
            date.setText(start + TO + due);
        } else if (!start.isEmpty()) {
            date.setText(start);
        } else if (!due.isEmpty()) {
            date.setText(due);
        }
    }
}
