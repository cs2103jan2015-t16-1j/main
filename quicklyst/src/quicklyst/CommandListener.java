package quicklyst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class CommandListener implements ActionListener {
    private static final String STRING_EMPTY = "";
    private final static Logger LOGGER = Logger
            .getLogger(CommandListener.class.getName());
    
    private JTextField _command;
    private QLGUI _guiInstance;
    private Stack<String> _commandList;
    
    public CommandListener(JTextField command, QLGUI gui) {
        _command = command;
        _guiInstance = gui;
        _commandList = new Stack<String>();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        LOGGER.info(String.format("user entered: %s",
                _command.getText()));
        _guiInstance.executeCommand(_command.getText());
        saveCommands(_command.getText());
        _command.setText(STRING_EMPTY);
    }
    
    public void saveCommands(String command) {
        _commandList.add(command);
    }
    
    public Stack<String> getCommandList() {
        return _commandList;
    }
}
