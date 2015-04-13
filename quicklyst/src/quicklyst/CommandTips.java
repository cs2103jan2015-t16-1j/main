package quicklyst;

import java.util.ArrayList;

//@author A0112971J
public class CommandTips {
    
    private static final int INDEX_START = 0;
    
    private static final int COMMANDTYPE_INDEX_TIP = 2;
    private static final int COMMANDTYPE_INDEX_SHORTCOMMAND = 1;
    private static final int COMMANDTYPE_INDEX_FULLCOMMAND = 0;
    
    private static final int MATCH_SIZE_ONE = 1;
    private static final int MATCH_INDEX_FIRST = 0;
    
    private static final int SPLIT_SIZE_NO_TOKEN = 0;
    private static final int SPLIT_INDEX_START = 0;

    private static final String STRING_SPACE = " ";
    static final String NEXTLINE = "\r\n";
    
    private static final String[][] COMMANDTYPES = {
            {"add", "a", "Add a task:\r\n" +
                         "add\r\n" +
                         "<task name>\\\r\n" +
                         "[start <date time>]\r\n" + 
                         "[due <date time>]\r\n" +
                         "[priority <low/medium/high>]\r\n"},            
            {"delete", "d", "Delete a task:\r\n" +
                            "delete\r\n" +
                            "<task number>\r\n"},
            {"edit", "e", "Edit a task:\r\n" +
                          "edit\r\n" +
                          "<task number>\r\n" +
                          "[name <new name> \\]\r\n" +
                          "[start <date time>]\r\n" +
                          "[due <date time>]\r\n" +
                          "[priority <low/medium/high>]\r\n"},
            {"find", "f", "Search for tasks:\r\n" +
                          "find\r\n" +
                          "[name <task name>\\]\r\n" +
                          "[start [on/after/before/between] <date time>]\r\n" +
                          "[due [on/after/before/between] <date time>]\r\n" +
                          "[priority <low/medium/high>]\r\n" +
                          "[overdue <yes/no>]\r\n" +
                         "[completed <yes/no>]\r\n"},
            {"load", "l", "Load tasks from a file:\r\n" +
                          "load\r\n" +
                          "<file name>\r\n"},
            {"save", "s", "Save tasks to a file:\r\n" +
                          "save\r\n" +
                          "<file name>\r\n"},
            {"cf", null, "Change file path:\r\n" +
                         "cf\r\n" +
                         "<file name>\r\n"},
            {"sync", null, "Synchronise with Google services.\r\n" +
                           "A browser will pop out for authentication.\r\n"},
            {"logout", null, "Logout from Google services.\r\n"},
        };
    
    
    public CommandTips() {
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
            return GlobalConstants.MESSAGE_INVALID_COMMAND;
        } else if (match.size() == MATCH_SIZE_ONE) {
            return COMMANDTYPES[match.get(MATCH_INDEX_FIRST)][COMMANDTYPE_INDEX_TIP];
        } else {
            return getAllMatchedCommands(match);
        }
    }

    private String getAllMatchedCommands(ArrayList<Integer> match) {
        StringBuilder commandList = new StringBuilder(GlobalConstants.MESSAGE_POSSIBLE_COMMANDS);
        for (int i: match) {
            commandList.append(NEXTLINE);
            commandList.append(COMMANDTYPES[i][COMMANDTYPE_INDEX_FULLCOMMAND]);
            if (COMMANDTYPES[i][COMMANDTYPE_INDEX_SHORTCOMMAND] != null) {
                commandList.append(String.format(GlobalConstants.MESSAGE_COMMAND_BODY,
                                   COMMANDTYPES[i][COMMANDTYPE_INDEX_SHORTCOMMAND]));
            }
        }
        return commandList.toString();
    }

    private ArrayList<Integer> findCommandPartialMatch(String userInput) {
        ArrayList<Integer> match = new ArrayList<Integer>();
        for (int i = INDEX_START; i < COMMANDTYPES.length; ++i) {
            String[] commands = COMMANDTYPES[i];
            if (isMatch(commands[COMMANDTYPE_INDEX_FULLCOMMAND], userInput)) {
                match.add(i);
            } else if ((commands[COMMANDTYPE_INDEX_SHORTCOMMAND] != null) &&
                       (isMatch(commands[COMMANDTYPE_INDEX_SHORTCOMMAND], userInput))) {
                match.add(i);
            }
        }
        return match;
    }
    
    private boolean isMatch(String actualCommand, String userInput) {
        userInput = userInput.toLowerCase();
        
        String userInputTrimmed = userInput.trim();
        String[] userInputSplit = userInputTrimmed.split(STRING_SPACE);
        if (userInputSplit.length == SPLIT_SIZE_NO_TOKEN) {
            return true;
        }
        String userInputCommand = userInputSplit[SPLIT_INDEX_START];
        if (actualCommand.startsWith(userInputCommand)) {
            int commandPosition = userInput.indexOf(userInputCommand);
            int nextCharacterPosition = commandPosition + userInputCommand.length();
            if (userInput.length() > nextCharacterPosition) {
                if (userInputCommand.equals(actualCommand)) {
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
        StringBuilder commandList = new StringBuilder(GlobalConstants.MESSAGE_AVAILABLE_COMMANDS);
        for (String[] commands: COMMANDTYPES) {
            commandList.append(NEXTLINE);
            commandList.append(commands[COMMANDTYPE_INDEX_FULLCOMMAND]);
            if (commands[COMMANDTYPE_INDEX_SHORTCOMMAND] != null) {
                commandList.append(String.format(GlobalConstants.MESSAGE_COMMAND_BODY, commands[COMMANDTYPE_INDEX_SHORTCOMMAND]));
            }
        }
        return commandList.toString();
    }

}
