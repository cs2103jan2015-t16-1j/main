package quicklyst;

import java.util.ArrayList;

//@author A0112971J
public class CommandTips {
    private static final String NEXTLINE = System.lineSeparator();
    private static final int COMMANDTYPE_INDEX_TIP = 2;
    private static final int COMMANDTYPE_INDEX_SHORTCOMMAND = 1;
    private static final int COMMANDTYPE_INDEX_FULLCOMMAND = 0;
    private static final String[][] commandTypes = {
            {"add", "a", "Add a task:NEXTLINEadd\r\n<task name>\\\r\n[start <date time>]\r\n[due <date time>]\r\n[priority <low/medium/high>]\r\n"},            
            {"delete", "d", "Delete a task:\r\ndelete\r\n<task number>\r\n"},
            {"edit", "e", "Edit a task:\r\nedit\r\n<task number>\r\n[name <new name> \\]\r\n[start <date time>\\\r\n[due <date time>\r\n[priority <low/medium/high>\r\n"},
            {"find", "f", "Search for tasks:\r\nfind\r\n[name <task name>\\]\r\n[start [on/after/before/between] <date time>]\r\n[due [on/after/before/between] <date time>\r\n[priority <low/medium/high>\r\n[overdue <yes/no>]\r\n[completed <yes/no>\r\n"},
            {"load", "l", "Load tasks from a file:\r\nload\r\n<file name>\r\n"},
            {"save", "s", "Save tasks to a file:\r\nsave\r\n<file name>\r\n"},
            {"cd", null, "Change directory:\r\ncd\r\n<file name>\r\n"},
            {"push", null, "push"},
            {"pull", null, "pull"},
        };
    
    private static CommandTips _instance;
    
    private CommandTips() {
    }
    
    public static CommandTips getInstance() {
        if (_instance == null) {
            _instance = new CommandTips();
        }
        return _instance;
    }
    
    public String getTips(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return allCommands();
        } else {
            ArrayList<Integer> match = findCommandPartialMatch(userInput);
            return getMatchedCommandTips(match);
        }
    }

    private String getMatchedCommandTips(ArrayList<Integer> match) {
        if (match.isEmpty()) {
            return "Invalid command";
        } else if (match.size() == 1) {
            return commandTypes[match.get(0)][COMMANDTYPE_INDEX_TIP];
        } else {
            return getAllMatchedCommands(match);
        }
    }

    private String getAllMatchedCommands(ArrayList<Integer> match) {
        StringBuilder commandList = new StringBuilder("Possible commands:");
        for (int i: match) {
            commandList.append(NEXTLINE);
            commandList.append(commandTypes[i][COMMANDTYPE_INDEX_FULLCOMMAND]);
            if (commandTypes[i][COMMANDTYPE_INDEX_SHORTCOMMAND] != null) {
                commandList.append(String.format(" (%s)", commandTypes[i][COMMANDTYPE_INDEX_SHORTCOMMAND]));
            }
        }
        return commandList.toString();
    }

    private ArrayList<Integer> findCommandPartialMatch(String userInput) {
        ArrayList<Integer> match = new ArrayList<Integer>();
        for (int i = 0; i < commandTypes.length; ++i) {
            String[] s = commandTypes[i];
            if (isMatch(s[COMMANDTYPE_INDEX_FULLCOMMAND], userInput)) {
                match.add(i);
            } else if ((s[COMMANDTYPE_INDEX_SHORTCOMMAND] != null) && (isMatch(s[COMMANDTYPE_INDEX_SHORTCOMMAND], userInput))) {
                match.add(i);
            }
        }
        return match;
    }
    
    private boolean isMatch(String actual, String userInput) {
        String userInputTrimmed = userInput.trim();
        String[] userInputSplit = userInputTrimmed.split(" ");
        if (userInputSplit.length == 0) {
            return true;
        }
        String userInputCommand = userInputSplit[0];
        if (actual.startsWith(userInputCommand)) {
            int commandPosition = userInput.indexOf(userInputCommand);
            int nextCharacterPosition = commandPosition + userInputCommand.length();
            if (userInput.length() > nextCharacterPosition) {
                if (userInputCommand.equals(actual)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private String allCommands() {
        StringBuilder commandList = new StringBuilder("Available commands:");
        for (String[] s: commandTypes) {
            commandList.append(NEXTLINE);
            commandList.append(s[COMMANDTYPE_INDEX_FULLCOMMAND]);
            if (s[COMMANDTYPE_INDEX_SHORTCOMMAND] != null) {
                commandList.append(String.format(" (%s)", s[COMMANDTYPE_INDEX_SHORTCOMMAND]));
            }
        }
        return commandList.toString();
    }

}
