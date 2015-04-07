package quicklyst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JTextField;

public class CommandListener implements ActionListener {
    private static final String STRING_EMPTY = "";
    private final static Logger LOGGER = Logger
            .getLogger(CommandListener.class.getName());
    
    private JTextField _command;
    private QLGUI guiInstance;
    
    public CommandListener(JTextField command, QLGUI gui) {
        _command = command;
        guiInstance = gui;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        LOGGER.info(String.format("user entered: %s",
                _command.getText()));
        guiInstance.executeCommand(_command.getText());
        _command.setText(STRING_EMPTY);
    }
}
