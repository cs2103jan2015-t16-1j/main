package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import quicklyst.CommandHistory;

//@author A0112971J
public class CommandHistoryTest {
    private CommandHistory _instance;

    @Before
    public void setUp() throws Exception {
        _instance = new CommandHistory();
    }
    
    @Test
    public void testGetPreviousCommand() {
        _instance.addCommand("a");
        _instance.addCommand("ab");
        _instance.addCommand("abc");
        _instance.addCommand("abcd");
        
        assertEquals("abcd", _instance.getPreviousCommand());
        assertEquals("abc", _instance.getPreviousCommand());
        assertEquals("ab", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());                
    }
    
    @Test
    public void testGetNextCommand() {
        _instance.addCommand("a");
        _instance.addCommand("ab");
        _instance.addCommand("abc");
        _instance.addCommand("abcd");
                
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        
        assertEquals("ab", _instance.getNextCommand());
        assertEquals("abc", _instance.getNextCommand());
        assertEquals("abcd", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
    }
    
    @Test
    public void testGetPreviousCommandRandomCharacters() {
        _instance.addCommand("abcdeft");
        _instance.addCommand("a@#R%");
        _instance.addCommand("ab %");
        _instance.addCommand("  cde t");
        _instance.addCommand("azZZEcdeft");
        _instance.addCommand("acdeIEFft");
        
        assertEquals("acdeIEFft", _instance.getPreviousCommand());        
        assertEquals("azZZEcdeft", _instance.getPreviousCommand());
        assertEquals("  cde t", _instance.getPreviousCommand());
        assertEquals("ab %", _instance.getPreviousCommand());
        assertEquals("a@#R%", _instance.getPreviousCommand());
        assertEquals("abcdeft", _instance.getPreviousCommand());
    }
    
    @Test
    public void testGetNextCommandRandomCharacters() {
        _instance.addCommand("abcdeft");
        _instance.addCommand("a@#R%");
        _instance.addCommand("ab %");
        _instance.addCommand("  cde t");
        _instance.addCommand("azZZEcdeft");
        _instance.addCommand("acdeIEFft");
        
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        
        assertEquals("a@#R%", _instance.getNextCommand());
        assertEquals("ab %", _instance.getNextCommand());
        assertEquals("  cde t", _instance.getNextCommand());
        assertEquals("azZZEcdeft", _instance.getNextCommand());
        assertEquals("acdeIEFft", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());         
    }
    
    @Test
    public void testEmptyHistory() {
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getPreviousCommand());
        assertEquals("", _instance.getPreviousCommand());
    }
    
    @Test
    public void testRepeatedCommand() {
        _instance.addCommand("add");
        _instance.addCommand("delete");
        _instance.addCommand("find");
        _instance.addCommand("edit");
        _instance.addCommand("sync");
        
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        
        _instance.addCommand("edit");
        assertEquals("sync", _instance.getPreviousCommand()); 
    }
    
    @Test
    public void testAddingEmptyCommand() {
        _instance.addCommand("add");
        _instance.addCommand("a");
        _instance.addCommand("      ");
        _instance.addCommand(" ");
        _instance.addCommand("");
        
        assertEquals("a",_instance.getPreviousCommand());
    }
    
    @Test
    public void testNonRepeatedCommand() {
        _instance.addCommand("add");
        _instance.addCommand("delete");
        _instance.addCommand("find");
        _instance.addCommand("edit");
        _instance.addCommand("sync");
        
        _instance.getPreviousCommand();
        _instance.getPreviousCommand();
        
        _instance.addCommand("logout");
        assertEquals("logout", _instance.getPreviousCommand());
    }
    
    @Test
    public void testLimit() {
        _instance.addCommand("a");
        _instance.addCommand("ab");
        _instance.addCommand("abc");
        _instance.addCommand("abcd");
        
        assertEquals("abcd", _instance.getPreviousCommand());
        assertEquals("abc", _instance.getPreviousCommand());
        assertEquals("ab", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());
        
        assertEquals("ab", _instance.getNextCommand());
        assertEquals("abc", _instance.getNextCommand());
        assertEquals("abcd", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
        assertEquals("", _instance.getNextCommand());
        
        assertEquals("abcd", _instance.getPreviousCommand());
        assertEquals("abc", _instance.getPreviousCommand());
    }
    
    @Test
    public void testAddReset() {
        _instance.addCommand("a");
        _instance.getPreviousCommand();
        _instance.addCommand("ab");
        assertEquals("ab", _instance.getPreviousCommand());
        _instance.addCommand("abc");
        assertEquals("abc", _instance.getPreviousCommand());
        assertEquals("ab", _instance.getPreviousCommand());
        _instance.addCommand("abcd");
        assertEquals("abcd", _instance.getPreviousCommand());
        assertEquals("abc", _instance.getPreviousCommand());
        assertEquals("ab", _instance.getPreviousCommand());
        assertEquals("a", _instance.getPreviousCommand());
    }

}
