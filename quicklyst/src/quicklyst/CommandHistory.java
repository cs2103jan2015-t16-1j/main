package quicklyst;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
    private List<String> _commands;
    private int _currentIndex;
    
    public CommandHistory() {
        _commands = new ArrayList<String>();
        _currentIndex = 0;
    }
    
    public String getPreviousCommand() {
        if (_currentIndex == 0) {
            if (_commands.isEmpty()) {
                return "";
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
            return "";
        } else {
            return _commands.get(_currentIndex);
        }
    }
    
    public void addCommand(String command) {
        if (_currentIndex == _commands.size()) {
            _commands.add(command);
        } else if (_currentIndex < _commands.size() && !_commands.get(_currentIndex).equals(command)) {
            _commands.add(command);
        }
        _currentIndex = _commands.size();
    }
}
