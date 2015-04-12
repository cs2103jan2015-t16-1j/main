package quicklyst;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

//@author A0112971J
public class TaskMouseListener implements MouseListener {
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
        details.append(String.format("Title:\r\n%s\r\n", title));
        
        if (!displayTime.isEmpty()) {
            details.append(String.format("Time:\r\n%s\r\n", displayTime));
        }
        
        if (!priority.isEmpty()) {
            details.append(String.format("Priority:\r\n%s\r\n", priority));
        }
        
        if (!description.isEmpty()) {
            details.append(String.format("Description:\r\n%s\r\n", description));
        }
        
        _taskDetails.setText(details.toString());
    }

    private String getPriority() {
        String priority;
        if (_task.getPriority() != null) {
            switch (_task.getPriority()) {
            case "H":
                priority = "HIGH";
                break;
            case "M":
                priority = "MEDIUM";
                break;
            case "L":
                priority = "LOW";
                break;
            default:
                priority = "";
            }
             
        } else {
            priority = "";
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
        String displayTime = "";
        if (!start.isEmpty() && !due.isEmpty()) {
            displayTime = (start + " - " + due);
        } else if (!start.isEmpty()) {
            displayTime = "starts " + start;
        } else if (!due.isEmpty()) {
            displayTime = "due " + due;                
        } else {
            displayTime = "";
        }
        return displayTime;
    }

    private String getDueTimeString() {
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String due = "";
        if (_task.getDueDate() != null && _task.getHasDueTime()) {
            due = dateAndTime.format(_task.getDueDate().getTime());
        } else if (_task.getDueDate() != null) {
            due = date.format(_task.getDueDate().getTime());
        }
        return due;
    }

    private String getStartTimeString() {
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String start = "";
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
            description = "";
        }
        return description;
    }
    
    @Override
    public void mouseExited(MouseEvent arg0) {
        //_taskDetails.setText("");
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
