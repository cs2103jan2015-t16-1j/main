package quicklyst;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

//@author A0112971J
public class HotkeysAction extends AbstractAction {
    
    private static final String UNDO = "Undo";
    private static final String REDO = "Redo";    
    
    private static final Logger LOGGER = Logger.getLogger(HotkeysAction
                                                          .class.getName());

    private String _keyPressed;
    private GUI _instance;
   
    public HotkeysAction (String keyPressed, GUI gui) {
        this._keyPressed = keyPressed;
        _instance = gui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        LOGGER.info(_keyPressed);
        
        if (_keyPressed.equalsIgnoreCase(UNDO)) {
            _instance.executeCommand(UNDO);
        } else {
            _instance.executeCommand(REDO);                
        }
    }    
}