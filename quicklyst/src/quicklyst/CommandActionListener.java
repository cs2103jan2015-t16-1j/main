package quicklyst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

//@author A0112971J
public class CommandActionListener implements ActionListener {
    
    private static final String STRING_EMPTY = "";
    
    private static final String USER_ENTERED_COMMAND = "user entered: %s";
    
    private static final Logger LOGGER = Logger.getLogger(CommandActionListener
                                                          .class.getName());
    
    private JTextField _command;
    private GUI _gui;

    public CommandActionListener(JTextField command, GUI gui) {
        _command = command;
        _gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String command = _command.getText();
        
        LOGGER.info(String.format(USER_ENTERED_COMMAND,command));
        
        _gui.addCommandToCommandHistory(command);
        
        if (_gui.executeCommand(command)) {
            _command.setText(STRING_EMPTY);
        }
    }
}
