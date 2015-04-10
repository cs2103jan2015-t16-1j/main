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

public class CommandActionListener implements ActionListener {
    private static final String STRING_EMPTY = "";
    private final static Logger LOGGER = Logger
            .getLogger(CommandActionListener.class.getName());
    
    private JTextField _command;
    private QLGUI _guiInstance;

    public CommandActionListener(JTextField command, QLGUI gui) {
        _command = command;
        _guiInstance = gui;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        LOGGER.info(String.format("user entered: %s",
                _command.getText()));
        _guiInstance.addCommandToCommandHistory(_command.getText());
        if (_guiInstance.executeCommand(_command.getText())) {
            _command.setText(STRING_EMPTY);
        }
    }
}
