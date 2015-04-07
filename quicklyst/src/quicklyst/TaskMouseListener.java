package quicklyst;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TaskMouseListener implements MouseListener {
    private Task _task;
    private JPanel _panel;
    private JLabel hover;
    
    public TaskMouseListener (Task task, JPanel panel ) {
        this._task = task;
        this._panel = panel; 
    }
    
    public void mouseEntered(MouseEvent m) {
        hover = new JLabel();
        _panel.add(hover, BorderLayout.CENTER);
        hover.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        String title = _task.getName(), description, priority;
        
        description = getDescription();
        
        if (_task.getPriority() != null) {
            priority = _task.getPriority();
        } else {
            priority = "";
        }
        
        String displayTime = getTimeString();
        
        hover.setText(String.format("<html><u>Task Detail</u><br>"
                + "Title: %s<br>" + "Description: %s<br>"
                + "Priority: %s<br>" + "Time: %s<br>", 
                title, description, priority, displayTime));
        hover.setOpaque(true);
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
        BorderLayout layout = (BorderLayout) _panel.getLayout();
        while (layout.getLayoutComponent(BorderLayout.CENTER) != null) {
            _panel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        }
        _panel.revalidate();
        _panel.repaint();
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
