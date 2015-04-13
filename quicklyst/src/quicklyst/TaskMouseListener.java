package quicklyst;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

//@author A0112971J
public class TaskMouseListener implements MouseListener {
    
    private static final String MESSAGE_TO = " - ";
    private static final String MESSAGE_START = "starts ";
    private static final String MESSAGE_DUE = "due ";

    private static final String PRIORITY_NONE = "";
    private static final String PRIORITY_LOW = "LOW";
    private static final String PRIORITY_MEDIUM = "MEDIUM";
    private static final String PRIORITY_HIGH = "HIGH";
    private static final String PRIORITY_LOW_ABBREVIATION = "L";
    private static final String PRIORITY_MEDIUM_ABBREVIATION = "M";
    private static final String PRIORITY_HIGH_ABBREVIATION = "H";
    
    private static final String MESSAGE_DESCRIPTION = "Description:\r\n%s\r\n";
    private static final String MESSAGE_PRIORITY = "Priority:\r\n%s\r\n";
    private static final String MESSAGE_TIME = "Time:\r\n%s\r\n";
    private static final String MESSAGE_TITLE = "Title:\r\n%s\r\n";
    
    private static final String FORMAT_DATE = "dd/MM/yyyy";
    private static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm";
    
    private Task _task;
    private JTextArea _taskDetails;
    
    public TaskMouseListener (Task task, JTextArea taskDetails) {
        _task = task;
        _taskDetails = taskDetails;
    }
    
    public void mouseEntered(MouseEvent m) {
        String title = _task.getName();
            
        String displayTime = getTimeString();
        
        String priority = getPriority();
        
        String description = getDescription();
        
        StringBuilder details = new StringBuilder();
        details.append(String.format(MESSAGE_TITLE, title));
        
        if (!displayTime.isEmpty()) {
            details.append(String.format(MESSAGE_TIME, displayTime));
        }
        
        if (!priority.isEmpty()) {
            details.append(String.format(MESSAGE_PRIORITY, priority));
        }
        
        if (!description.isEmpty()) {
            details.append(String.format(MESSAGE_DESCRIPTION, description));
        }
        
        _taskDetails.setText(details.toString());
    }

    private String getPriority() {
        String priority;
        if (_task.getPriority() != null) {
            switch (_task.getPriority()) {
            case PRIORITY_HIGH_ABBREVIATION:
                priority = PRIORITY_HIGH;
                break;
            case PRIORITY_MEDIUM_ABBREVIATION:
                priority = PRIORITY_MEDIUM;
                break;
            case PRIORITY_LOW_ABBREVIATION:
                priority = PRIORITY_LOW;
                break;
            default:
                priority = PRIORITY_NONE;
            }
             
        } else {
            priority = PRIORITY_NONE;
        }
        return priority;
    }

    private String getTimeString() {
        String start = getStartTimeString();
        String due = getDueTimeString();
        String displayTime = compoundStartAndDueTime(start, due);
        return displayTime;
    }

    private String compoundStartAndDueTime(String start, String due) {
        String displayTime = PRIORITY_NONE;
        if (!start.isEmpty() && !due.isEmpty()) {
            displayTime = (start + MESSAGE_TO + due);
        } else if (!start.isEmpty()) {
            displayTime = MESSAGE_START + start;
        } else if (!due.isEmpty()) {
            displayTime = MESSAGE_DUE + due;                
        } else {
            displayTime = PRIORITY_NONE;
        }
        return displayTime;
    }

    private String getDueTimeString() {
        SimpleDateFormat date = new SimpleDateFormat(FORMAT_DATE);
        SimpleDateFormat dateAndTime = new SimpleDateFormat(FORMAT_DATE_TIME);
        String due = PRIORITY_NONE;
        if (_task.getDueDate() != null && _task.getHasDueTime()) {
            due = dateAndTime.format(_task.getDueDate().getTime());
        } else if (_task.getDueDate() != null) {
            due = date.format(_task.getDueDate().getTime());
        }
        return due;
    }

    private String getStartTimeString() {
        SimpleDateFormat date = new SimpleDateFormat(FORMAT_DATE);
        SimpleDateFormat dateAndTime = new SimpleDateFormat(FORMAT_DATE_TIME);
        String start = PRIORITY_NONE;
        if (_task.getStartDate() != null && _task.getHasStartTime()) {
            start = dateAndTime.format(_task.getStartDate().getTime());
        } else if (_task.getStartDate() != null) {
            start = date.format(_task.getStartDate().getTime());
        }
        return start;
    }

    private String getDescription() {
        String description;
        if (_task.getDescription() != null) {
            description = _task.getDescription();
        } else {
            description = PRIORITY_NONE;
        }
        return description;
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) { 
    }  
}
