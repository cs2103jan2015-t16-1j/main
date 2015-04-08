package quicklyst;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class CommandKeyListener implements KeyListener {
    private CommandHistory _commandHistory;
    private JTextField _command;
    
    public CommandKeyListener(CommandHistory commandHistory, JTextField command) {
        _commandHistory = commandHistory;
        _command = command;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        int keyCode = e.getKeyCode();
        if (keyCode == 38) {
            _command.setText(_commandHistory.getPreviousCommand());
        } else if (keyCode == 40) {
            _command.setText(_commandHistory.getNextCommand());
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}
