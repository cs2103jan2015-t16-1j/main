package test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import quicklyst.QLGoogleIntegration;
import quicklyst.Task;

//@author A01112707N
public class GoogleIntegrationTest {
    
    private QLGoogleIntegration _googleIntegration;
    private String _googleId;
    private Calendar _now;
    private Calendar _later;
    @BeforeClass
    public static void setUpBeforeClass() {
        QLGoogleIntegration.getInstance().logout();
        QLGoogleIntegration.getInstance().sync(new LinkedList<Task>(), new LinkedList<String>());
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        QLGoogleIntegration.getInstance().logout();
    }
    
    @Before
    public void setUp() throws Exception {
        _googleIntegration = QLGoogleIntegration.getInstance();
        _googleId = null;
        _now = Calendar.getInstance();
        _later = (Calendar) _now.clone();
        _later.add(Calendar.DATE, 1);
    }
    
    @After
    public void tearDown() throws Exception {
        if ((_googleId != null) && (!_googleId.isEmpty())) {
            List<String> delete = new LinkedList<String>();
            delete.add(_googleId);
            _googleIntegration.sync(new LinkedList<Task>(), delete);
        }
    }
    
    @Test
    public void testSync() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Error caught = null;
        try {
            _googleIntegration.sync(tasks, deleted);
        } catch (Error e) {
            caught = e;
        }
        assertNull(caught);
    }
    
    @Test
    public void testSyncWithNullsInList1() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        tasks.add(null);
        Error caught = null;
        try {
            _googleIntegration.sync(tasks, deleted);
        } catch (Error e) {
            caught = e;
        }
        assertNull(caught);
    }

    @Test
    public void testSyncWithNullsInList2() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        deleted.add(null);
        Error caught = null;
        try {
            _googleIntegration.sync(tasks, deleted);
        } catch (Error e) {
            caught = e;
        }
        assertNull(caught);
    }

    @Test
    public void testAddToGoogleTasks1() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        tasks.add(t);
        assertTrue(t.getGoogleId() == null || t.getGoogleId().isEmpty());
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        assertTrue(tasks.contains(t));
        assertTrue(t.getGoogleId() != null && !t.getGoogleId().isEmpty());
        assertTrue(t.getGoogleId().startsWith("t"));
    }
    
    @Test
    public void testAddToGoogleTasks2() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        t.setDueDate(_later);
        tasks.add(t);
        assertTrue(t.getGoogleId() == null || t.getGoogleId().isEmpty());
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        assertTrue(tasks.contains(t));
        assertTrue(t.getGoogleId() != null && !t.getGoogleId().isEmpty());
        assertTrue(t.getGoogleId().startsWith("t"));
    }
    
    @Test
    public void testAddToGoogleCalendar() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        t.setStartDate(_now);
        t.setDueDate(_later);
        tasks.add(t);
        assertTrue(t.getGoogleId() == null || t.getGoogleId().isEmpty());
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        assertTrue(tasks.contains(t));
        assertTrue(t.getGoogleId() != null && !t.getGoogleId().isEmpty());
        assertTrue(t.getGoogleId().startsWith("c"));
    }
    
    @Test
    public void testDeleteFromGoogleTasks1() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertTrue(hasTask(tasks2, t));
        
        tasks.remove(t);
        deleted.add(_googleId);
        _googleIntegration.sync(tasks, deleted);
        
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertFalse(hasTask(tasks2, t));
        _googleId = null;
    }
    
    @Test
    public void testDeleteFromGoogleTasks2() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        t.setDueDate(_later);
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertTrue(hasTask(tasks2, t));
        
        tasks.remove(t);
        deleted.add(_googleId);
        _googleIntegration.sync(tasks, deleted);
        
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertFalse(hasTask(tasks2, t));
        _googleId = null;
    }
    
    @Test
    public void testDeleteFromGoogleCalendar() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        t.setStartDate(_now);
        t.setDueDate(_later);
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertTrue(hasTask(tasks2, t));
        
        tasks.remove(t);
        deleted.add(_googleId);
        _googleIntegration.sync(tasks, deleted);
        
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        assertFalse(hasTask(tasks2, t));
        _googleId = null;
    }
    
    @Test
    public void testEditGoogleTask1() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        Task match = getTask(tasks2, t);
        assertNotNull(match);
        assertEquals(t.getName(), match.getName());
        
        t.setName("Something new");
        t.setLastUpdated(_later);
        _googleIntegration.sync(tasks, deleted);
        
        tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        match = getTask(tasks2, t);
        assertNotNull(match);
        assertEquals(t.getName(), match.getName());
    }
    
    @Test
    public void testEditGoogleTask2() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        _now.set(Calendar.HOUR_OF_DAY, 23);
        _now.set(Calendar.MINUTE, 59);
        _now.set(Calendar.SECOND, 59);
        t.setDueDate(_now);
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        Task match = getTask(tasks2, t);
        assertNotNull(match);
        assertDateEquals(t.getDueDate(), match.getDueDate());
        
        _now.add(Calendar.DATE, 1);
        t.setDueDate((Calendar) _now.clone());
        t.setLastUpdated(_later);
        _googleIntegration.sync(tasks, deleted);
        
        tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        match = getTask(tasks2, t);
        assertNotNull(match);
        assertDateEquals(t.getDueDate(), match.getDueDate());
    }
    
    @Test
    public void testEditGoogleCalendar() {
        LinkedList<Task> tasks = new LinkedList<Task>();
        LinkedList<String> deleted = new LinkedList<String>();
        Task t = new Task("Google Testing");
        t.setStartDate(_now);
        t.setHasStartTime(true);
        t.setDueDate(_later);
        t.setHasDueTime(true);
        tasks.add(t);
        _googleIntegration.sync(tasks, deleted);
        _googleId = t.getGoogleId();
        
        LinkedList<Task> tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        Task match = getTask(tasks2, t);
        assertNotNull(match);
        assertCalendarEquals(t.getStartDate(), match.getStartDate());
        assertCalendarEquals(t.getDueDate(), match.getDueDate());
        
        _later.add(Calendar.DATE, 1);
        t.setDueDate((Calendar) _later.clone());
        t.setLastUpdated(_later);
        _googleIntegration.sync(tasks, deleted);
        
        tasks2 = new LinkedList<Task>();
        _googleIntegration.sync(tasks2, new LinkedList<String>());
        match = getTask(tasks2, t);
        assertNotNull(match);
        assertCalendarEquals(t.getStartDate(), match.getStartDate());
        assertCalendarEquals(t.getDueDate(), match.getDueDate());
    }
    
    public boolean hasTask(List<Task> tasks, Task task) {
        for (Task t : tasks) {
            if (t.getGoogleId().equals(task.getGoogleId())) {
                return true;
            }
        }
        return false;
    }
    
    public Task getTask(List<Task> tasks, Task task) {
        for (Task t : tasks) {
            if (t.getGoogleId().equals(task.getGoogleId())) {
                return t;
            }
        }
        return null;
    }
    
    private void assertCalendarEquals(Calendar expected, Calendar actual) {
        assertEquals(expected == null, actual == null);
        if (expected != null) {
            assertEquals(expected.getTimeInMillis()/1000, actual.getTimeInMillis()/1000);
        }
    }
    
    private void assertDateEquals(Calendar expected, Calendar actual) {
        assertEquals(expected == null, actual == null);
        if (expected != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            assertEquals(sdf.format(expected.getTime()), sdf.format(actual.getTime()));
        }
    }
}
