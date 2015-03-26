package quicklyst;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class QLLogic {
    
    private static final String MESSAGE_CANNOT_CLEAR_NAME = "Cannot clear the name of a task. ";
    private static final String MESSAGE_NOTHING_TO_REDO = "Nothing to redo. ";
    private static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo. ";
    private static final String MESSAGE_INVALID_DATE_RANGE = "Invalid date range entered. ";
    private static final String MESSAGE_NO_MATCHES_FOUND = "No matches found for criteria entered. ";
    private static final String MESSAGE_NO_TASK_MATCHES_KEYWORD = "No task matches keyword. ";
    private static final String MESSAGE_INVALID_SORTING_CRITERIA_TYPE = "Invalid sorting criteria type \"%1$s\" ";
    private static final String MESSAGE_INVALID_SORTING_ORDER = "Invalid sorting order \"%1$s\". ";
    private static final String MESSAGE_NO_DATE_ENTERED = "No date entered. ";
    private static final String MESSAGE_NO_NAME_ENTERED = "No task date entered. ";
    private static final String MESSAGE_INVALID_FIELD_TYPE = "Invalid field type \"%1$s\". ";
    private static final String MESSAGE_INVALID_COMMAND = "Invalid command. No command executed. ";
    private static final String MESSAGE_INVALID_TASK_NAME = "Invalid task name entered. Nothing is executed. ";
    
    private static final int INDEX_COMMAND = 0;
    private static final int INDEX_FIELDS = 1;
    private static final int INDEX_FIELD_CONTENT_START = 1;
    private static final int INDEX_FIELD_TYPE = 0;
    private static final int INDEX_PRIORITY_LEVEL = 0;
    
    private static final int NUM_0_SEC = 0;
    private static final int NUM_0_MIN = 0;
    private static final int NUM_0_HOUR = 0;
    private static final int NUM_59_SEC = 59;
    private static final int NUM_59_MIN = 59;
    private static final int NUM_23_HOUR = 23;
    
    private static final int OFFSET_TASK_NUMBER_TO_INDEX = -1;
    
    private static final String COMMAND_REDO_ABBREV = "r";
    private static final String COMMAND_REDO = "redo";
    private static final String COMMAND_UNDO_ABBREV = "u";
    private static final String COMMAND_UNDO = "undo";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_FIND_ABBREV = "f";
    private static final String COMMAND_SORT = "s";
    private static final String COMMAND_SORT_ABBREV = "sort";
    private static final String COMMAND_ADD_ABBREV = "a";
    private static final String COMMAND_ADD = "add";
    private static final String COMMAND_EDIT_ABBREV = "e";
    private static final String COMMAND_EDIT = "edit";
    private static final String COMMAND_DELETE_ABBREV = "d";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_COMPLETE_ABBREV = "c";
    private static final String COMMAND_COMPLETE = "complete";
    
    private static final String STRING_NO_CHAR = "";
    private static final String STRING_BLANK_SPACE = " ";
    private static final String STRING_NO = "N";
    private static final String STRING_YES = "Y";
    private static final String STRING_NEW_LINE = "\n";
    private static final String STRING_CLEAR = "CLR";
    
    private static final char CHAR_DESCENDING_LOWERCASE = 'd';
    private static final char CHAR_ASCENDING_LOWERCASE = 'a';
    private static final char CHAR_DESCENDING_UPPERCASE = 'D';
    private static final char CHAR_ASCENDING_UPPERCASE = 'A';

    
    public static LinkedList<Task> _workingList;    //TODO change back to private
    private static LinkedList<Task> _workingListMaster;
    private static Stack<LinkedList<Task>> _undoStack;
    private static Stack<LinkedList<Task>> _redoStack;
    private static String _fileName;
    
    /** General methods **/
    public static LinkedList<Task> setup(String fileName) {
        _fileName = fileName; 
        _undoStack = new Stack<LinkedList<Task>>();
        _redoStack = new Stack<LinkedList<Task>>();
        _workingList = QLStorage.loadFile(fileName);
        _workingListMaster = new LinkedList<Task>();
        copyList(_workingList, _workingListMaster);
        _undoStack.push(_workingListMaster);
        _undoStack.push(_workingList);
        return _workingList;
    }
    
    private static void recover() {
        copyList(_workingListMaster, _workingList);
    }
    
    // Stub
    public static void setupStub() {
        _undoStack = new Stack<LinkedList<Task>>();
        _redoStack = new Stack<LinkedList<Task>>();
        _workingList = new LinkedList<Task>();
        _workingListMaster = new LinkedList<Task>();
        _undoStack.push(new LinkedList<Task>());
        _undoStack.push(new LinkedList<Task>());
    }
    
    // Stub
    public static void displayStub(StringBuilder feedback) {
        System.out.println("Feedback: " + feedback.toString());
        System.out.println("Name: start date: due date:");
        for(int i = 0; i < _workingList.size(); i++) {
            System.out.print(_workingList.get(i).getName() + " ");
            try {
                System.out.print(_workingList.get(i).getStartDateString() + " ");
            } catch(NullPointerException e) {System.out.print("        ");}
            try {
                System.out.print(_workingList.get(i).getDueDateString() + " ");
            } catch(NullPointerException e) {System.out.print("        ");}
            if(_workingList.get(i).getPriority() != null) {
                System.out.print(_workingList.get(i).getPriority() + " ");
            } 
            System.out.println();
        }
        
        /*
        System.out.println("    workingListMaster: ");
        for(int i = 0; i < _workingListMaster.size(); i++) {
            System.out.print(_workingListMaster.get(i).getName() + " ");
            try {
                System.out.print(_workingListMaster.get(i).getStartDateString() + " ");
            } catch(NullPointerException e) {}
            try {
                System.out.print(_workingListMaster.get(i).getDueDateString() + " ");
            } catch(NullPointerException e) {}
            if(_workingListMaster.get(i).getPriority() != null) {
                System.out.print(_workingListMaster.get(i).getPriority() + " ");
            } 
            System.out.println();
        }
        */
        
        System.out.println();
        feedback.setLength(0);
    }

    public static LinkedList<Task> executeCommand(String instruction, StringBuilder feedback) {
        String[] splittedInstruction = CommandParser.splitCommandAndFields(instruction);
        
        String command = splittedInstruction[INDEX_COMMAND].trim();
        String fieldLine = splittedInstruction[INDEX_FIELDS].trim();
                
        if(command.equalsIgnoreCase(COMMAND_ADD) || command.equalsIgnoreCase(COMMAND_ADD_ABBREV)) {
            return executeAdd(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_EDIT) || command.equalsIgnoreCase(COMMAND_EDIT_ABBREV)) {
            return executeEdit(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_DELETE) || command.equalsIgnoreCase(COMMAND_DELETE_ABBREV)) {
            return executeDelete(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_COMPLETE) || command.equalsIgnoreCase(COMMAND_COMPLETE_ABBREV)) {
            return executeComplete(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_SORT) || command.equalsIgnoreCase(COMMAND_SORT_ABBREV)) {
            return executeSort(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_FIND) || command.equalsIgnoreCase(COMMAND_FIND_ABBREV)) {
            return executeFind(fieldLine, feedback);
        } else if(command.equalsIgnoreCase(COMMAND_UNDO) || command.equalsIgnoreCase(COMMAND_UNDO_ABBREV)) {
            undo(feedback);
            return _workingList;
        } else if(command.equalsIgnoreCase(COMMAND_REDO) || command.equalsIgnoreCase(COMMAND_REDO_ABBREV)) {
            redo(feedback);
            return _workingList;
        } else {
            feedback.append(MESSAGE_INVALID_COMMAND);
            return _workingList;
        }
    }

    /** Multi-command methods **/ 
    //TODO change to private
    public static void clearWorkingList() {
        _workingList = new LinkedList<Task>();
    }
    
    private static <E> void copyList(LinkedList<E> fromList, LinkedList<E> toList) {
        toList.clear();
        for(int i = 0; i < fromList.size(); i++)
            toList.add(fromList.get(i));
    }
    
    private static void copyListsForUndoStack(LinkedList<Task> list, LinkedList<Task> listMaster,
            LinkedList<Task> listNew, LinkedList<Task> listMasterNew) {
        LinkedList<Integer> indexesInListMasterForRepeatTask = new LinkedList<Integer>();
        for(int i = 0; i < list.size(); i++) {
            indexesInListMasterForRepeatTask.add(listMaster.indexOf(list.get(i)));
        }
        for(int i = 0; i < listMaster.size(); i++) {
            listMasterNew.add(listMaster.get(i).clone());
        }
        for(int i = 0; i < indexesInListMasterForRepeatTask.size(); i++) {
            listNew.add(listMasterNew.get(indexesInListMasterForRepeatTask.get(i)));
        }
    }
    
    private static void updateUndoStack() {
        LinkedList<Task> workingListMaster = new LinkedList<Task>();
        LinkedList<Task> workingList = new LinkedList<Task>();
        copyListsForUndoStack(_workingList, _workingListMaster,
                workingList, workingListMaster);
        
        _undoStack.push(workingListMaster);
        _undoStack.push(workingList);
        _redoStack.clear();
    }


    private static void undo(StringBuilder feedback) {
        if(_undoStack.size() == 2) {
            feedback.append(MESSAGE_NOTHING_TO_UNDO);
            return;
        }
        _redoStack.push(_undoStack.pop());
        _redoStack.push(_undoStack.pop());
        
        _workingList = _undoStack.pop();
        _workingListMaster = _undoStack.pop();
        
        _undoStack.push(_workingListMaster);
        _undoStack.push(_workingList);
        
        QLStorage.saveFile(_workingListMaster, _fileName);
    }
    
    private static void redo(StringBuilder feedback) {
        if(_redoStack.isEmpty()) {
            feedback.append(MESSAGE_NOTHING_TO_REDO);
            return;
        }
        
        _undoStack.push(_redoStack.pop());
        _undoStack.push(_redoStack.pop());
        
        _workingList = _undoStack.pop();
        _workingListMaster = _undoStack.pop();
        
        _undoStack.push(_workingListMaster);
        _undoStack.push(_workingList);
        
        QLStorage.saveFile(_workingListMaster, _fileName);
    }
    
    
    /** Update methods **/
    private static void updateField(String field, Task task, StringBuilder feedback) {
        char fieldType = field.charAt(INDEX_FIELD_TYPE);
        String fieldContent = field.substring(INDEX_FIELD_CONTENT_START).trim();
            
        switch(fieldType) {
        case 'd':       
            updateDueDate(task, feedback, fieldContent);
            break;
        
        case 's':
            updateStartDate(task, feedback, fieldContent);
            break;
            
        case 'p':
            updatePriority(task, feedback, fieldContent);
            break;
                
        case 'n':
            updateName(task, feedback, fieldContent);
            break;
                
        default: 
            feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append(STRING_NEW_LINE);
            return;
        }
    }
    
    private static void updateName(Task task, StringBuilder feedback, String fieldContent) {
        if(fieldContent.equals(STRING_NO_CHAR)) {
            feedback.append(MESSAGE_INVALID_TASK_NAME);
            return;
        }
        if(fieldContent.equalsIgnoreCase(STRING_CLEAR)) {
            feedback.append(MESSAGE_CANNOT_CLEAR_NAME);
            return;
        }
        task.setName(fieldContent);
        feedback.append("Task name updated. ");
    }

    private static void updatePriority(Task task, StringBuilder feedback, String fieldContent) {
        if(fieldContent.equalsIgnoreCase(STRING_CLEAR)) {
            task.setPriority(null);
            feedback.append("Priority level cleared. ");
            return;
        }
        if(CommandParser.isValidPriorityLevel(fieldContent, feedback)) {
            task.setPriority(fieldContent);
            feedback.append("Priority level updated. ");
        }
    }
    
    public static void updateOverdue() {
        for(int i = 0; i < _workingList.size(); i++) {
            _workingList.get(i).updateIsOverdue();
        }
    }
    
    private static void updateDueDate(Task task, StringBuilder feedback, String fieldContent) {
        if(fieldContent.equalsIgnoreCase(STRING_CLEAR)) {
            task.setDueDate((Calendar)null);
            feedback.append("Due date cleared. ");
            return;
        }
        if(!DateHandler.isValidDateFormat(fieldContent, feedback)) {
            return;
        }
        task.setDueDate(fieldContent);
        feedback.append("Due date updated. ");
    }
    
    private static void updateStartDate(Task task, StringBuilder feedback, String fieldContent) {
        if(fieldContent.equalsIgnoreCase(STRING_CLEAR)) {
            task.setStartDate((Calendar)null);
            feedback.append("Start date cleared. ");
            return;
        }
        if(!DateHandler.isValidDateFormat(fieldContent, feedback)) {
            return;
        }
        task.setStartDate(fieldContent);
        feedback.append("Start date updated. ");
    }
    
    /** Add methods **/
    private static LinkedList<Task> executeAdd(String fieldLineWithName, StringBuilder feedback) {
        String taskName = CommandParser.extractTaskName(fieldLineWithName);
        if(!CommandParser.isValidTaskName(taskName, feedback)) {
            return _workingList;
        }
        
        String fieldLine= fieldLineWithName.replaceFirst(taskName, STRING_NO_CHAR).trim();
        LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
        
        Task newTask = new Task(taskName);
        feedback.append("\"" + taskName + "\" added. ");
        
        for(int i = 0; i < fields.size(); i++) {
            updateField(fields.get(i), newTask, feedback);
        }
        
        _workingList.add(newTask);
        _workingListMaster.add(newTask);
        
        QLStorage.saveFile(_workingListMaster, _fileName);
        updateUndoStack();
        return _workingList;
    }

    /** Edit methods **/
    private static LinkedList<Task> executeEdit(String fieldLineWithTaskNumber, StringBuilder feedback) {
        int taskNumber;
        String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
        if(!CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
            return _workingList;
        } else {
            taskNumber = Integer.parseInt(taskNumberString);
        }
        
        String fieldLine= fieldLineWithTaskNumber.replaceFirst(String.valueOf(taskNumber), STRING_NO_CHAR).trim();
        LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
        
        Task taskToEdit = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
        
        feedback.append("Edit \"" + taskToEdit.getName() + "\": ");
        
        for(int i = 0; i < fields.size(); i++) {
            updateField(fields.get(i), taskToEdit, feedback);
        }
                
        QLStorage.saveFile(_workingListMaster, _fileName);
        updateUndoStack();
        return _workingList;
    }

    /** Delete methods **/
    private static LinkedList<Task> executeDelete(String fieldLineWithTaskNumber, StringBuilder feedback) {
        int taskNumber;
        String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
        if(CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
            taskNumber = Integer.parseInt(taskNumberString);
        } else {
            return _workingList;
        }
        
        Task taskToDelete = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
        deleteTask(taskToDelete);
        feedback.append("\"" + taskToDelete.getName() + "\" deleted. ");
        
        QLStorage.saveFile(_workingListMaster, _fileName);
        updateUndoStack();
        return _workingList;
    }

    private static void deleteTask(Task taskToDelete) {
        _workingList.remove(taskToDelete);
        _workingListMaster.remove(taskToDelete);
    }

    /** Complete methods **/
    private static LinkedList<Task> executeComplete(String fieldLineWithTaskNumber, StringBuilder feedback) {
        int taskNumber;
        String taskNumberString = CommandParser.extractTaskNumberString(fieldLineWithTaskNumber);
        if(CommandParser.isValidTaskNumber(taskNumberString, feedback, _workingList.size())) {
            taskNumber = Integer.parseInt(taskNumberString);
        } else {
            return _workingList;
        }
        
        Task taskToComplete = _workingList.get(taskNumber + OFFSET_TASK_NUMBER_TO_INDEX);
        completeTask(taskToComplete);
        
        QLStorage.saveFile(_workingListMaster, _fileName);
        updateUndoStack();
        return _workingList;
    }
    
    private static void completeTask(Task taskToComplete) {
        if(taskToComplete.getIsCompleted()) {
            taskToComplete.setNotCompleted();
        } 
        else {
            taskToComplete.setCompleted();
        }
    }
    
    /** Find methods **/
    private static LinkedList<Task> executeFind(String fieldLine, StringBuilder feedback) {
        LinkedList<Task> workingListBackUp = new LinkedList<Task>();
        copyList(_workingList, workingListBackUp);
        
        recover();
        
        LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
        for(int i = 0; i < fields.size(); i++) {
            filterWorkingListByCriteria(fields.get(i), feedback);
        }
        if(_workingList.isEmpty() || fields.isEmpty()) {
            feedback.append(MESSAGE_NO_MATCHES_FOUND);
            _workingList = workingListBackUp;
            return _workingList;
        }
        updateUndoStack();
        return _workingList;
    }

    private static void filterWorkingListByCriteria(String field, StringBuilder feedback) {
        if(field.equals(STRING_BLANK_SPACE)) {
            return;
        } 
        
        if(field.equalsIgnoreCase("ALL")) {
            recover();
            return;
        }
        
        char fieldType = field.charAt(INDEX_FIELD_TYPE);
        String fieldCriteria = field.substring(INDEX_FIELD_CONTENT_START).trim();
        switch(fieldType) {
        case 'd':   
        case 's':
            filterByDate(fieldCriteria, feedback, fieldType);
            break;

        case 'p':
            filterByPriority(fieldCriteria, feedback);
            break;
                
        case 'c':
            filterByCompleteStatus(fieldCriteria, feedback);
            break;
            
        case 'o':
            filterByOverdueStatus(fieldCriteria, feedback);
            break;
        
        case 'n':
            filterByName(fieldCriteria, feedback);
            break;
                
        default: 
            feedback.append(String.format(MESSAGE_INVALID_FIELD_TYPE, fieldType)).append(STRING_NEW_LINE);
            break;
        }
    }

    private static void filterByName(String fieldCriteria, StringBuilder feedback) {
        if(fieldCriteria.equals(STRING_NO_CHAR)) {
            feedback.append(MESSAGE_NO_NAME_ENTERED);
            return;
        }
        String keywords[] = fieldCriteria.split(STRING_BLANK_SPACE);
        findTasksMatchKeywords(keywords, feedback);
    }

    private static void filterByOverdueStatus(String fieldCriteria, StringBuilder feedback) {
        if(CommandParser.isValidYesNo(fieldCriteria, feedback)) {
            LinkedList<Task> bufferList = new LinkedList<Task>();
            for(int i = 0; i < _workingList.size(); i++) {
                Task currentTask = _workingList.get(i);
                if((currentTask.getIsOverdue() && fieldCriteria.equalsIgnoreCase(STRING_YES))
                        || (!currentTask.getIsOverdue() && fieldCriteria.equalsIgnoreCase(STRING_NO))) {
                    bufferList.add(currentTask);
                } 
            }
            copyList(bufferList, _workingList);
        } else {
            _workingList.clear();
        }
    }
    
    private static void filterByCompleteStatus(String fieldCriteria, StringBuilder feedback) {
        if(CommandParser.isValidYesNo(fieldCriteria, feedback)) {
            LinkedList<Task> bufferList = new LinkedList<Task>();
            for(int i = 0; i < _workingList.size(); i++) {
                Task currentTask = _workingList.get(i);
                if((currentTask.getIsCompleted() && fieldCriteria.equalsIgnoreCase(STRING_YES))
                        || (!currentTask.getIsCompleted() && fieldCriteria.equalsIgnoreCase(STRING_NO))) {
                    bufferList.add(currentTask);
                } 
            }
            copyList(bufferList, _workingList);
        } else {
            _workingList.clear();
        }
    }
    
    private static void filterByPriority(String fieldCriteria, StringBuilder feedback) {
        if(CommandParser.isValidPriorityLevel(fieldCriteria, feedback)) {
            String priorityLevel = fieldCriteria.substring(INDEX_PRIORITY_LEVEL, INDEX_PRIORITY_LEVEL + 1);
            LinkedList<Task> bufferList = new LinkedList<Task>();
            for(int i = 0; i < _workingList.size(); i++) {
                Task currentTask = _workingList.get(i);
                if(currentTask.getPriority() != null) {
                    if(currentTask.getPriority().equalsIgnoreCase(priorityLevel)) {
                        bufferList.add(currentTask);
                    } 
                }
            }
            copyList(bufferList, _workingList);
        } else {
            _workingList.clear();
        }
    }
    
    /*
    private static void filterByDuration(String fieldCriteria, StringBuilder feedback) {
        if(fieldCriteria.equals(STRING_NO_CHAR)) {
            feedback.append(MESSAGE_NO_DATE_ENTERED);
            return;
        }
        String[] periodRange = fieldCriteria.split(":");
        if(periodRange.length != 2) {
            feedback.append(MESSAGE_INVALID_DATE_RANGE);
            return;
        }
    
        Calendar startDate = DateHandler.changeFromDateStringToDateCalendar(periodRange[0], feedback);
        if(startDate == null) {
            return;
        }
        Calendar endDate = DateHandler.changeFromDateStringToDateCalendar(periodRange[1], feedback);
        if(endDate == null) {
            return;
        }
        startDate.set(Calendar.HOUR, NUM_0_HOUR);
        startDate.set(Calendar.MINUTE, NUM_0_MIN);
        startDate.set(Calendar.SECOND, NUM_0_SEC);
        
        endDate.set(Calendar.HOUR, NUM_23_HOUR);
        endDate.set(Calendar.MINUTE, NUM_59_MIN);
        endDate.set(Calendar.SECOND, NUM_59_SEC);
        
        LinkedList<Task> bufferList = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            Calendar currentTaskStartDate = currentTask.getStartDate();
            Calendar currentTaskDueDate = currentTask.getDueDate();
            if(currentTaskStartDate != null && currentTaskDueDate != null) {
                if(currentTaskStartDate.compareTo(startDate) >= 0 && currentTaskDueDate.compareTo(endDate) <= 0) {
                    bufferList.add(currentTask);
                }
            }
        }
        copyList(bufferList, _workingList);
    }
    */
    
    private static void filterByDate(String fieldCriteria, StringBuilder feedback, char startOrDueDate) {
        if(fieldCriteria.equals(STRING_NO_CHAR)) {
            feedback.append(MESSAGE_NO_DATE_ENTERED);
            _workingList.clear();
            return;
        }
        
        String[] dateCriteria = fieldCriteria.split(STRING_BLANK_SPACE, 2);
        
        if(dateCriteria.length == 1) {
            feedback.append("Invalid date criteria entered. ");
            _workingList.clear();
            return;
        }
        
        String criteriaQualifier = dateCriteria[0];
        String criteriaDates = dateCriteria[1];
        
        if(criteriaQualifier.equalsIgnoreCase("bf")) {
            filterByDateBefore(criteriaDates, feedback, startOrDueDate);
        } else if(criteriaQualifier.equalsIgnoreCase("af")) {
            filterByDateAfter(criteriaDates, feedback, startOrDueDate);
        } else if(criteriaQualifier.equalsIgnoreCase("on")) {
            filterBySingleDate(criteriaDates, feedback, startOrDueDate);
        } else if(criteriaQualifier.equalsIgnoreCase("btw")) {
            filterByDateRange(criteriaDates, feedback, startOrDueDate);
        } else {
            feedback.append("Invalid date criteria entered. ");
            _workingList.clear();
            return;
        }
    }

    private static void filterByDateRange(String criteriaDates, StringBuilder feedback, char startOrDueDate) {
        String[] startEndDates = criteriaDates.split(":");
        
        if(startEndDates.length == 1) {
            feedback.append("Invalid date criteria entered. ");
            _workingList.clear();
            return;
        }
        
        Calendar startDate = DateHandler.changeFromDateStringToDateCalendar(startEndDates[0], feedback);
        if(startDate == null) {
            _workingList.clear();
            return;
        }
        Calendar endDate = DateHandler.changeFromDateStringToDateCalendar(startEndDates[1], feedback);
        if(endDate == null) {
            _workingList.clear();
            return;
        }
        startDate.set(Calendar.HOUR, NUM_0_HOUR);
        startDate.set(Calendar.MINUTE, NUM_0_MIN);
        startDate.set(Calendar.SECOND, NUM_0_SEC);
        
        endDate.set(Calendar.HOUR, NUM_23_HOUR);
        endDate.set(Calendar.MINUTE, NUM_59_MIN);
        endDate.set(Calendar.SECOND, NUM_59_SEC);
        
        LinkedList<Task> bufferList = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            
            Calendar currentTaskDate;
            if(startOrDueDate == 's') {
                currentTaskDate = currentTask.getStartDate();
            } else if(startOrDueDate == 'd') {
                currentTaskDate = currentTask.getDueDate();
            } else {
                currentTaskDate = null;
            }
            
            if(currentTaskDate != null) {
                if(currentTaskDate.compareTo(startDate) >= 0 && currentTaskDate.compareTo(endDate) <= 0) {
                    bufferList.add(currentTask);
                }
            }
        }
        copyList(bufferList, _workingList);
    }

    private static void filterBySingleDate(String singleDateCriteria, StringBuilder feedback, char startOrDueDate) {
        Calendar dateCriteria = DateHandler.changeFromDateStringToDateCalendar(singleDateCriteria, feedback);
        if(dateCriteria == null) {
            _workingList.clear();
            return;
        }
        
        LinkedList<Task> bufferList = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            
            Calendar currentTaskDate;
            if(startOrDueDate == 's') {
                currentTaskDate = currentTask.getStartDate();
            } else if(startOrDueDate == 'd') {
                currentTaskDate = currentTask.getDueDate();
            } else {
                currentTaskDate = null;
            }
            
            if(currentTaskDate != null) {
                int currentTaskDay = currentTaskDate.get(Calendar.DAY_OF_MONTH);
                int currentTaskMonth = currentTaskDate.get(Calendar.MONTH);
                int currentTaskYear = currentTaskDate.get(Calendar.YEAR); 
                Calendar currentTaskDateNoTime = new GregorianCalendar(currentTaskYear, currentTaskMonth, currentTaskDay);
                
                if(currentTaskDateNoTime.equals(dateCriteria)) {
                    bufferList.add(currentTask);
                } 
            }       
        }
        copyList(bufferList, _workingList);
    }
    
    private static void filterByDateBefore(String criteriaDates, StringBuilder feedback, char startOrDueDate) {
        Calendar dateCriteria = DateHandler.changeFromDateStringToDateCalendar(criteriaDates, feedback);
        if(dateCriteria == null) {
            _workingList.clear();
            return;
        }
        dateCriteria.set(Calendar.HOUR_OF_DAY, NUM_23_HOUR);
        dateCriteria.set(Calendar.MINUTE, NUM_59_MIN);
        dateCriteria.set(Calendar.SECOND, NUM_59_SEC);
        
        LinkedList<Task> bufferList = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            
            Calendar currentTaskDate;
            if(startOrDueDate == 's') {
                currentTaskDate = currentTask.getStartDate();
            } else if(startOrDueDate == 'd') {
                currentTaskDate = currentTask.getDueDate();
            } else {
                currentTaskDate = null;
            }
            
            if(currentTaskDate != null) {
                Calendar currentTaskDateMinTime = new GregorianCalendar(currentTaskDate.get(Calendar.YEAR),
                        currentTaskDate.get(Calendar.MONTH),
                        currentTaskDate.get(Calendar.DAY_OF_MONTH));
                if(currentTaskDateMinTime.compareTo(dateCriteria) <= 0) {
                    bufferList.add(currentTask);
                } 
            }       
        }
        copyList(bufferList, _workingList);
    }
    
    private static void filterByDateAfter(String criteriaDates, StringBuilder feedback, char startOrDueDate) {
        Calendar dateCriteria = DateHandler.changeFromDateStringToDateCalendar(criteriaDates, feedback);
        if(dateCriteria == null) {
            _workingList.clear();
            return;
        }
        
        LinkedList<Task> bufferList = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            
            Calendar currentTaskDate;
            if(startOrDueDate == 's') {
                currentTaskDate = currentTask.getStartDate();
            } else if(startOrDueDate == 'd') {
                currentTaskDate = currentTask.getDueDate();
            } else {
                currentTaskDate = null;
            }
            
            if(currentTaskDate != null) {
                Calendar currentTaskDateMinTime = new GregorianCalendar(currentTaskDate.get(Calendar.YEAR),
                        currentTaskDate.get(Calendar.MONTH),
                        currentTaskDate.get(Calendar.DAY_OF_MONTH));
                if(currentTaskDateMinTime.compareTo(dateCriteria) >= 0) {
                    bufferList.add(currentTask);
                } 
            }       
        }
        copyList(bufferList, _workingList);
    }


    private static void findTasksMatchKeywords(String[] keywords, StringBuilder feedback) {
        LinkedList<Object[]> foundTasksWithMatchScore = new LinkedList<Object[]>();
        for(int i = 0; i < _workingList.size(); i++) {
            Task currentTask = _workingList.get(i);
            int matchScore = 0;
            for(int j = 0; j < keywords.length; j++) {
                String currentKeyword = keywords[j].trim();
                matchScore = matchKeywordScore(currentTask, currentKeyword);
            }
            if(matchScore != 0) {
                foundTasksWithMatchScore.add(new Object[]{currentTask, Integer.valueOf(matchScore)});
            }
        }
        if(foundTasksWithMatchScore.isEmpty()) {
            feedback.append(MESSAGE_NO_TASK_MATCHES_KEYWORD);
            _workingList.clear();
            return;
        }
        _workingList = sortFoundTasksByMatchScore(foundTasksWithMatchScore);
    }

    private static LinkedList<Task> sortFoundTasksByMatchScore(LinkedList<Object[]> foundTasksWithMatchScore) {
        for(int i = foundTasksWithMatchScore.size() - 1; i >= 0; i--) {
            boolean isSorted = true;
            for(int j = 0; j < i; j++) {
                Object[] taskLeft = foundTasksWithMatchScore.get(j);
                Object[] taskRight = foundTasksWithMatchScore.get(j + 1);
                if((int)taskLeft[1] < (int)taskRight[1]) {
                    foundTasksWithMatchScore.set(j + 1, taskLeft);
                    foundTasksWithMatchScore.set(j, taskRight);
                    isSorted = false;
                }
            }
            if(isSorted) {
                break;
            }
        }
        LinkedList<Task> newWorkingList = new LinkedList<Task>();
        for(int i = 0; i < foundTasksWithMatchScore.size(); i++) {
            Task taskToAdd = (Task)foundTasksWithMatchScore.get(i)[0];
            newWorkingList.add(taskToAdd);
        }
        return newWorkingList;
    }

    private static int matchKeywordScore(Task currentTask, String currentKeyword) {
        String[] taskNameWords = currentTask.getName().split(STRING_BLANK_SPACE);
        int totalScore = 0;
        for(int i = 0; i < taskNameWords.length; i++) {
            String currentTaskNameWord = taskNameWords[i].trim();
            if(currentTaskNameWord.contains(currentKeyword)) {
                totalScore++;
            }
            if(currentTaskNameWord.equals(currentKeyword)) {
                totalScore ++;
            }
        }
        return totalScore;
    }
    
    /** Sort methods **/
    private static LinkedList<Task> executeSort(String fieldLine, StringBuilder feedback) {
        LinkedList<String> fields = CommandParser.processFieldLine(fieldLine);
        if(fields.size() == 0) {
            feedback.append("No field entered.");
            return _workingList;
        }   
        LinkedList<char[]> sortingCriteria = CommandParser.getSortingCriteria(fields);
        sortByCriteria(sortingCriteria, feedback);
        updateUndoStack();
        return _workingList;
    }
    
    private static void sortByCriteria(LinkedList<char[]> sortingCriteria, StringBuilder feedback) {
        for(int i = sortingCriteria.size() - 1; i >= 0; i--) {
            char criterionType = sortingCriteria.get(i)[0];
            char criterionOrder = sortingCriteria.get(i)[1];
            if(criterionOrder == CHAR_ASCENDING_LOWERCASE || 
                    criterionOrder == CHAR_ASCENDING_UPPERCASE || 
                    criterionOrder == CHAR_DESCENDING_LOWERCASE || 
                    criterionOrder == CHAR_DESCENDING_UPPERCASE) {
                switch(criterionType) {
                case 'd':
                    sortByDueDate(criterionOrder, feedback);
                    break;
                    
                case 'p':
                    sortByPriority(criterionOrder, feedback);
                    break;
                
                case 'l':
                    sortByDurationLength(criterionOrder, feedback);
                    break;
                    
                default:
                    feedback.append(String.format(MESSAGE_INVALID_SORTING_CRITERIA_TYPE, criterionType)).append(STRING_NEW_LINE);
                    break;
                }
            } else {
                feedback.append(String.format(MESSAGE_INVALID_SORTING_ORDER, criterionOrder)).append(STRING_NEW_LINE);
            }
        }
    }

    private static void sortByDurationLength(char order, StringBuilder feedback) {
        LinkedList<Task> tasksWithNoDuration = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++){
            if(_workingList.get(i).getDuration() == -1) {
                Task removedTask = _workingList.remove(i); 
                tasksWithNoDuration.add(removedTask);
                i--;
            }
        }
        
        for(int i = _workingList.size() - 1; i >= 0; i--) {
            boolean isSorted = true;
            for(int j = 0; j < i; j++) {
                Task taskLeft = _workingList.get(j);
                Task taskRight = _workingList.get(j + 1);
                switch (order) {
                case CHAR_ASCENDING_LOWERCASE:
                case CHAR_ASCENDING_UPPERCASE:
                    if(taskLeft.getDuration() > taskRight.getDuration()) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                
                case CHAR_DESCENDING_LOWERCASE:
                case CHAR_DESCENDING_UPPERCASE:
                    if(taskLeft.getDuration() < taskRight.getDuration()) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                }
            }
            if(isSorted) {
                break;
            }
        }
        tasksWithNoDuration.addAll(_workingList);
        _workingList = tasksWithNoDuration;
        
    }

    private static void sortByPriority(char order, StringBuilder feedback) {
        LinkedList<Task> tasksWithNoPriority = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++){
            if(_workingList.get(i).getPriorityInt() == 0) {
                Task removedTask = _workingList.remove(i); 
                tasksWithNoPriority.add(removedTask);
                i--;
            }
        }
        
        for(int i = _workingList.size() - 1; i >= 0; i--) {
            boolean isSorted = true;
            for(int j = 0; j < i; j++) {
                Task taskLeft = _workingList.get(j);
                Task taskRight = _workingList.get(j + 1);
                switch (order) {
                case CHAR_ASCENDING_LOWERCASE:
                case CHAR_ASCENDING_UPPERCASE:
                    if(taskLeft.getPriorityInt() > taskRight.getPriorityInt()) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                
                case CHAR_DESCENDING_LOWERCASE:
                case CHAR_DESCENDING_UPPERCASE:
                    if(taskLeft.getPriorityInt() < taskRight.getPriorityInt()) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                }
            }
            if(isSorted) {
                break;
            }
        }
        tasksWithNoPriority.addAll(_workingList);
        _workingList = tasksWithNoPriority;
    }
    
    private static void sortByDueDate(char order, StringBuilder feedback) {
        LinkedList<Task> tasksWithNoDueDate = new LinkedList<Task>();
        for(int i = 0; i < _workingList.size(); i++){
            if(_workingList.get(i).getDueDate() == null) {
                Task removedTask = _workingList.remove(i); 
                tasksWithNoDueDate.add(removedTask);
                i--;
            }
        }
                
        for(int i = _workingList.size() - 1; i >= 0; i--) {
            boolean isSorted = true;
            for(int j = 0; j < i; j++) {
                Task taskLeft = _workingList.get(j);
                Task taskRight = _workingList.get(j + 1);
                switch (order) {
                case CHAR_ASCENDING_LOWERCASE:
                case CHAR_ASCENDING_UPPERCASE:
                    if(taskLeft.getDueDate().compareTo(taskRight.getDueDate()) > 0 ) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                
                case CHAR_DESCENDING_LOWERCASE:
                case CHAR_DESCENDING_UPPERCASE:
                    if(taskLeft.getDueDate().compareTo(taskRight.getDueDate()) < 0) {
                        _workingList.set(j + 1, taskLeft);
                        _workingList.set(j, taskRight);
                        isSorted = false;
                    }
                    break;
                }
            }
            if(isSorted) {
                break;
            }
        }
        tasksWithNoDueDate.addAll(_workingList);
        _workingList = tasksWithNoDueDate;
    }

    /** Main method **/
    public static void main(String args[]) {
        setupStub();
        StringBuilder feedback =  new StringBuilder();
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Enter command: ");
            String command = sc.nextLine();
            executeCommand(command, feedback);
            displayStub(feedback);
        }
    }
    
}
