package quicklyst;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TaskMouseListener implements MouseListener {
    private Task _task;
    private JLabel _taskDetailsLabel;
    private JPanel _overviewPanel;
    
    public TaskMouseListener (Task task, JLabel taskDetailsLabel, JPanel overviewPanel) {
        _task = task;
        _taskDetailsLabel = taskDetailsLabel;
        _overviewPanel = overviewPanel;
    }
    
    public void mouseEntered(MouseEvent m) {
        String title = _task.getName(), description, priority;
            
        String displayTime = getTimeString();
        
        if (_task.getPriority() != null) {
            priority = _task.getPriority();
        } else {
            priority = "";
        }
        
        description = getDescription();
        
        _taskDetailsLabel.setText(String.format("<html><u>Task Detail</u><br>"
                + "Title: %s<br>" + "Time: %s<br>"
                + "Priority: %s<br>" + "Description: %s<br>", 
                title, displayTime, priority, description));
        
        _overviewPanel.setLayout(null);
        _overviewPanel.add(_taskDetailsLabel);
        _taskDetailsLabel.setLocation(0, -20);
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
        _taskDetailsLabel.setText("");
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
