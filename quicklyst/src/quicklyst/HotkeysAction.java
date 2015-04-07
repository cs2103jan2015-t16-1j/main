package quicklyst;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

public class HotkeysAction extends AbstractAction {
    private static final Logger LOGGER = Logger
            .getLogger(HotkeysAction.class.getName());
    private static final String UNDO = "Undo";
    private static final String REDO = "Redo";
    
    private String keyPressed;
    private QLGUI guiInstance;
   
    public HotkeysAction (String keyPressed, QLGUI gui) {
        this.keyPressed = keyPressed;
        guiInstance = gui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        LOGGER.info(keyPressed);
        if (keyPressed.equalsIgnoreCase(UNDO)) {
            guiInstance.executeCommand(UNDO);
        } else {
            guiInstance.executeCommand(REDO);                
        }
    }    
}