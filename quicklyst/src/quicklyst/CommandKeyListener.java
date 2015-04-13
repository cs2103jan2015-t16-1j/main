package quicklyst;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

//@author A0112971J
public class CommandKeyListener implements KeyListener {
    
    private static final int KEY_UP = 38;
    private static final int KEY_DOWN = 40;

    private CommandHistory _commandHistory;
    private JTextField _command;
    
    public CommandKeyListener(CommandHistory commandHistory, JTextField command) {
        _commandHistory = commandHistory;
        _command = command;
    }
        
    @Override
    public void keyPressed(KeyEvent e) {
        
        int keyCode = e.getKeyCode();
        
        if (keyCode == KEY_UP) {
            _command.setText(_commandHistory.getPreviousCommand());
        } else if (keyCode == KEY_DOWN) {
            _command.setText(_commandHistory.getNextCommand());
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {     
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
