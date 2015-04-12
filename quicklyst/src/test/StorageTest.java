package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.QLStorage;
import quicklyst.Task;

//@author A01112707N
public class StorageTest {
    
    private QLStorage _storage;
    private List<Task> _tasks;
    private List<String> _deletedIDs;
    private String _filename;

    @Before
    public void setUp() throws Exception {
        _storage = QLStorage.getInstance();
        _tasks = new LinkedList<Task>();
        _deletedIDs = new LinkedList<String>(); 
    }

    @After
    public void tearDown() throws Exception {
        if (_filename != null) {
            File file = new File(_filename);
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    @Test
    public void testSaveAndLoad() {
        Calendar now = Calendar.getInstance();
        _tasks.add(instantiateTask("0",
                                   "0",
                                   "L",
                                   "0",
                                   now,
                                   oneMinuteAfter(now), 
                                   now, 
                                   false,
                                   true,
                                   false, 
                                   true));
        _deletedIDs.add("0");
        for (int i = 1; i < 10; ++i) {
            Task prev = _tasks.get(i-1);
            Task newTask = instantiateTask(prev.getName() + i,
                                           prev.getDescription() + i,
                                           nextPriority(prev.getPriority()),
                                           prev.getGoogleId() + i,
                                           oneMinuteAfter(prev.getStartDate()),
                                           oneMinuteAfter(prev.getDueDate()), 
                                           oneMinuteAfter(prev.getLastUpdated()), 
                                           !prev.getIsCompleted(),
                                           !prev.getShouldSync(),
                                           !prev.getHasStartTime(), 
                                           !prev.getHasDueTime());
            _tasks.add(newTask);
            _deletedIDs.add(_deletedIDs.get(i-1) + i);
        }
        _filename = "testSaveAndLoad.json";
        _storage.saveFile(_tasks, _deletedIDs, _filename);
        
        List<Task> newTasks = new LinkedList<Task>();
        List<String> newDeletedIDs = new LinkedList<String>();
        _storage.loadFile(newTasks, newDeletedIDs, _filename);
        assertEquals(_tasks.size(), newTasks.size());
        assertEquals(_deletedIDs.size(), newDeletedIDs.size());
        for (int i = 0; i < _tasks.size(); ++i) {
            assertTaskEquals(_tasks.get(i), newTasks.get(i));
        }
    }
    
    @Test
    public void testSavingEmpty() {
        _tasks.clear();
        _deletedIDs.clear();
        _filename = "testSavingEmpty.json";
        _storage.saveFile(_tasks, _deletedIDs, _filename);
        
        List<Task> newTasks = new LinkedList<Task>();
        List<String> newDeletedIDs = new LinkedList<String>();
        _storage.loadFile(newTasks, newDeletedIDs, _filename);
        assertEquals(_tasks.size(), newTasks.size());
        assertEquals(_deletedIDs.size(), newDeletedIDs.size());
    }
    
    @Test
    public void testSavingSpecialCharacters() {
        _tasks.add(instantiateTask("[](){}\":,=>\t\r\n",
                                   "[](){}\":,=>\t\r\n",
                                   "L",
                                   "[](){}\":,=>\t\r\n",
                                   Calendar.getInstance(),
                                   Calendar.getInstance(), 
                                   Calendar.getInstance(), 
                                   false,
                                   true,
                                   false, 
                                   true));
        _deletedIDs.add("[](){}\":,=>\t\r\n");
        _filename = "testSavingSpecialCharacters.json";
        _storage.saveFile(_tasks, _deletedIDs, _filename);
        
        List<Task> newTasks = new LinkedList<Task>();
        List<String> newDeletedIDs = new LinkedList<String>();
        _storage.loadFile(newTasks, newDeletedIDs, _filename);
        assertEquals(_tasks.size(), newTasks.size());
        assertEquals(_deletedIDs.size(), newDeletedIDs.size());
        for (int i = 0; i < _tasks.size(); ++i) {
            assertTaskEquals(_tasks.get(i), newTasks.get(i));
        }
    }
    
    @Test
    public void testSavingIntoOpenedFiles() throws IOException {
        _filename = "openedfile.json";
        (new File(_filename)).createNewFile(); 
        try (RandomAccessFile raf = new RandomAccessFile(_filename, "rw");
                FileChannel channel = raf.getChannel()) {
            FileLock lock = channel.lock();
            _tasks.clear();
            _deletedIDs.clear();
            Error caught = null;
            try {
                _storage.saveFile(_tasks, _deletedIDs, _filename);
            } catch (Error e) {
                caught = e;
            }
            assertNotNull(caught);
            lock.release();
        }
    }
    
    @Test
    public void testLoadingNonexistingFile() {
        _filename = "nonexistent.json";
        File file = new File(_filename);
        while (file.exists()) {
            _filename = _filename + "1";
            file = new File(_filename);
        }
        try {
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            fail("Error caught unexpectedly");
        }
        assertEquals(0, _tasks.size());
        assertEquals(0, _deletedIDs.size());
    }
    
    @Test
    public void testSavingValidFilepath() {
        Error caught = null;
        try {
            _filename = "legal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath1() {
        Error caught = null;
        try {
            _filename = "ille:gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath2() {
        Error caught = null;
        try {
            _filename = "ille*gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath3() {
        Error caught = null;
        try {
            _filename = "ille?gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath4() {
        Error caught = null;
        try {
            _filename = "ille\"gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath5() {
        Error caught = null;
        try {
            _filename = "ille<gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath6() {
        Error caught = null;
        try {
            _filename = "ille>gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testSavingInvalidFilepath7() {
        Error caught = null;
        try {
            _filename = "ille|gal.json";
            _storage.saveFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingValidFilepath() {
        Error caught = null;
        try {
            _filename = "legal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath1() {
        Error caught = null;
        try {
            _filename = "ille:gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath2() {
        Error caught = null;
        try {
            _filename = "ille*gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath3() {
        Error caught = null;
        try {
            _filename = "ille?gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath4() {
        Error caught = null;
        try {
            _filename = "ille\"gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath5() {
        Error caught = null;
        try {
            _filename = "ille<gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath6() {
        Error caught = null;
        try {
            _filename = "ille>gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    @Test
    public void testLoadingInvalidFilepath7() {
        Error caught = null;
        try {
            _filename = "ille|gal.json";
            _storage.loadFile(_tasks, _deletedIDs, _filename);
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
    }
    
    private String nextPriority(String priority) {
        switch (priority) {
        case "L":
            return "M";
        case "M":
            return "H";
        case "H":
            return "L";
        default:
            return "";
        }
    }
    
    private Calendar oneMinuteAfter(Calendar instance) {
        Calendar nextMinute = (Calendar) instance.clone();
        nextMinute.add(Calendar.MINUTE, 1);
        return nextMinute;
    }
    
    private Task instantiateTask(String name, String description, String priority, 
            String googleId, Calendar start, Calendar due, Calendar lastUpdated, 
            boolean isCompleted, boolean shouldSync, boolean hasStartTime, 
            boolean hasDueTime) {
        Task task = new Task(name);
        task.setDescription(description);
        task.setPriority(priority);
        task.setGoogleId(googleId);
        task.setStartDate(start);
        task.setDueDate(due);
        task.setLastUpdated(lastUpdated);
        task.setIsCompleted(isCompleted);
        task.setShouldSync(shouldSync);
        task.setHasStartTime(hasStartTime);
        task.setHasDueTime(hasDueTime);
        return task;
    }
    
    private void assertTaskEquals(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getGoogleId(), actual.getGoogleId());
        assertCalendarEquals(expected.getStartDate(), actual.getStartDate());
        assertCalendarEquals(expected.getDueDate(), actual.getDueDate());
        assertCalendarEquals(expected.getLastUpdated(), actual.getLastUpdated());
        assertEquals(expected.getIsCompleted(), actual.getIsCompleted());
        assertEquals(expected.getIsOverdue(), actual.getIsOverdue());
        assertEquals(expected.getShouldSync(), actual.getShouldSync());
        assertEquals(expected.getHasStartTime(), actual.getHasStartTime());
        assertEquals(expected.getHasDueTime(), actual.getHasDueTime());
    }
    
    private void assertCalendarEquals(Calendar expected, Calendar actual) {
        assertEquals(expected == null, actual == null);
        if (expected != null) {
            assertEquals(expected.getTimeInMillis()/1000, actual.getTimeInMillis()/1000);
        }
    }
}

