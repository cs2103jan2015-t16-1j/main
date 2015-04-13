package test;

import static org.junit.Assert.*;

import org.junit.Test;

import quicklyst.CommandTips;
import quicklyst.MessageConstants;

//@author A0112971J
public class CommandTipsTest {
    
    private static final String STRING_EMPTY = " ";
    
    private CommandTips instance = new CommandTips();

    @Test
    public void testShowAllCommandTips() {
        String userInput1 = STRING_EMPTY;
        
        String expectedOutput = "Available commands:\r\nadd (a)\r\ndelete (d)\r\n" +
                                "edit (e)\r\nfind (f)\r\nload (l)\r\nsave (s)\r\n" +
                                "cf\r\nsync\r\nlogout";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = STRING_EMPTY + STRING_EMPTY;
        assertEquals(expectedOutput, instance.getTips(userInput2));
    }
    
    @Test
    public void testShowAddCommandTips() {
        String expectedOutput = "Add a task:\r\nadd\r\n<task name>\\\r\n" + 
                                "[start <date time>]\r\n[due <date time>]\r\n" + 
                                "[priority <low/medium/high>]\r\n";
        
        String userInput1 = "a";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = " a";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = " a ";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "    a    ";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "ad";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "a d";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = " a  d ";
        assertEquals(expectedOutput, instance.getTips(userInput7));
        
        String userInput8 = "add";
        assertEquals(expectedOutput, instance.getTips(userInput8));
        
        String userInput9 = " a   d    d ";
        assertEquals(expectedOutput, instance.getTips(userInput9));
        
        String userInput10 = "     ADd ";
        assertEquals(expectedOutput, instance.getTips(userInput10));
        
        String userInput11 = "     add d";
        assertEquals(expectedOutput, instance.getTips(userInput11));
        
        String userInput12 = "ad d";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput12)); 
    }
    
    @Test
    public void testShowDeleteCommandTips() {
        String expectedOutput = "Delete a task:\r\ndelete\r\n<task number>\r\n";
        
        String userInput1 = "d";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = " d ";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = "de";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "dele";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "DElet";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "delete";
        assertEquals("Delete a task:\r\ndelete\r\n<task number>\r\n",
                     instance.getTips(userInput6));
        
        String userInput7 = "  delete    ";
        assertEquals(expectedOutput, instance.getTips(userInput7)); 
        
        String userInput11 = "deleted";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput11));
        
        String userInput12 = "dl";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput12));
    }
    
    @Test
    public void testShowEditCommandTips() {
        String expectedOutput = "Edit a task:\r\nedit\r\n<task number>\r\n" +
                                "[name <new name> \\]\r\n[start <date time>]\r\n" +
                                "[due <date time>]\r\n[priority <low/medium/high>]\r\n";
        
        String userInput1 = "e";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = "eD";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = "edi";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "edit";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "   edit  ";
        assertEquals(expectedOutput, instance.getTips(userInput5));
    }
    
    @Test
    public void testShowFindCommandTips() {
        String expectedOutput = "Search for tasks:\r\nfind\r\n[name <task name>\\]\r\n" +
                                "[start [on/after/before/between] <date time>]\r\n" +
                                "[due [on/after/before/between] <date time>]\r\n" +
                                "[priority <low/medium/high>]\r\n[overdue <yes/no>]\r\n" +
                                "[completed <yes/no>]\r\n";
        
        String userInput1 = "f";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = " f ";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = "  fi";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "  fin";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "  find ";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "FIND";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = "find a";
        assertEquals(expectedOutput, instance.getTips(userInput7));
        
        String userInput8 = "fi ";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput8));
    }
    
    @Test
    public void testShowLoadCommandTips() {
        String expectedOutput = "Load tasks from a file:\r\nload\r\n<file name>\r\n";
        String userInput1 = "l";
        assertEquals("Possible commands:\r\nload (l)\r\nlogout",
                     instance.getTips(userInput1));
        
        String userInput2 = "lo";
        assertEquals("Possible commands:\r\nload (l)\r\nlogout",
                     instance.getTips(userInput2));
        
        String userInput3 = "loa";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "load";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = " load ";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "load a";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = "  LOAD a";
        assertEquals(expectedOutput, instance.getTips(userInput7));
        
        String userInput8 = "lo ad";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput8));
    }
    
    @Test
    public void testShowSaveCommandTips() {
        String expectedOutput = "Save tasks to a file:\r\nsave\r\n<file name>\r\n";
        
        String userInput1 = "s";
        assertEquals("Possible commands:\r\nsave (s)\r\nsync",
                     instance.getTips(userInput1));
        
        String userInput2 = "sa";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = "sav";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "save";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "  save ";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "sA";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = "saved";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput7));
        

    }
    
    @Test
    public void testShowCfCommandTips() {
        String expectedOutput = "Change file path:\r\ncf\r\n<file name>\r\n";
        
        String userInput1 = "c";
        assertEquals(expectedOutput, instance.getTips(userInput1));
        
        String userInput2 = "cf";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = " cF ";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = " c f ";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput4));
        
        String userInput5 = "c ";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput5));
    }
    
    @Test
    public void testShowSynCommandTips() {
        String expectedOutput = "Synchronise with"
                + " Google services.\r\n" +
                                "A browser will pop out for authentication.\r\n";
        
        String userInput1 = "s";
        assertEquals("Possible commands:\r\nsave (s)\r\nsync",
                     instance.getTips(userInput1));
        
        String userInput2 = "sy";
        assertEquals(expectedOutput, instance.getTips(userInput2));
        
        String userInput3 = "syn";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "sync";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "sync a";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = " sYnc  a";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = " syncd";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput7));
    }
    
    @Test
    public void testShowLogoutCommandTips() {
        String expectedOutput = "Logout from Google services.\r\n";
        String userInput1 = "l";
        assertEquals("Possible commands:\r\nload (l)\r\nlogout",
                     instance.getTips(userInput1));
        
        String userInput2 = "lo";
        assertEquals("Possible commands:\r\nload (l)\r\nlogout",
                     instance.getTips(userInput2));
        
        String userInput3 = "log";
        assertEquals(expectedOutput, instance.getTips(userInput3));
        
        String userInput4 = "logo";
        assertEquals(expectedOutput, instance.getTips(userInput4));
        
        String userInput5 = "logou";
        assertEquals(expectedOutput, instance.getTips(userInput5));
        
        String userInput6 = "logout";
        assertEquals(expectedOutput, instance.getTips(userInput6));
        
        String userInput7 = "logout a";
        assertEquals(expectedOutput, instance.getTips(userInput7));
        
        String userInput8 = "lo ";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput8));
    }
    
    @Test
    public void testShowInvalidCommandTips() {
        String userInput1 = "abcd";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput1));
              
        String userInput2 = "daege";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput2));
        
        String userInput3 = "!@$%";
        assertEquals(MessageConstants.MESSAGE_INVALID_COMMAND, instance.getTips(userInput3));   
        
    }
}
