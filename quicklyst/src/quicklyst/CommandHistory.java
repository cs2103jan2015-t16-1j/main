package quicklyst;

import java.util.ArrayList;
import java.util.List;

//@author A0112971J
public class CommandHistory {
    
    private static final int INDEX_START = 0;

    private static final String STRING_EMPTY = "";
    
    private List<String> _commands;
    private int _currentIndex;
    
    public CommandHistory() {
        _commands = new ArrayList<String>();
        _currentIndex = INDEX_START;
    }
    
    public String getPreviousCommand() {
        if (_currentIndex == INDEX_START) {
            if (_commands.isEmpty()) {
                return STRING_EMPTY;
            } else {
                return _commands.get(_currentIndex);
            }
        } else {
            _currentIndex--;
            return _commands.get(_currentIndex);
        }
    }
    
    public String getNextCommand() {
        if (_currentIndex < _commands.size()) { 
            _currentIndex++;
        }
        
        if (_currentIndex == _commands.size()) {
            return STRING_EMPTY;
        } else {
            return _commands.get(_currentIndex);
        }
    }
    
    public void addCommand(String command) {
        if (_currentIndex == _commands.size()) {
            if (!command.trim().isEmpty()) {
                _commands.add(command);
            }
        } else if (_currentIndex < _commands.size() &&
                  !_commands.get(_currentIndex).equals(command)) {
            if (!command.trim().isEmpty()) {
                _commands.add(command);
            }
        }
        _currentIndex = _commands.size();
    }
}
