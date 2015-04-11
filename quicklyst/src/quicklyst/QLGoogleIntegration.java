package quicklyst;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.Tasks;

public class QLGoogleIntegration {

    private static final String PRIMARY_CALENDAR_ID = "primary";
    private static final String PRIMARY_TASKS_ID = "@default";

    private static final String PREFIX_GOOGLEID_TASKS = "t";
    private static final String PREFIX_GOOGLEID_CALENDAR = "c";
    private static final String USER_ID = "u";

    private static final String STATUS_TASKS_NEEDSACTION = "needsAction";
    private static final String STATUS_TASKS_COMPLETED = "completed";

    private static final int OFFSET_GOOGLEID_PREFIX = 1;

    private static QLGoogleIntegration instance;

    private String _userId;
    private boolean _shouldRememberLogin;

    private Credential _cred;
    private GoogleCalConn _googleCalendar;
    private GoogleTaskConn _googleTasks;
    private GoogleLogin _googleLogin;

    private HttpTransport _httpTransport;

    private QLGoogleIntegration() {
        this(USER_ID, true);
    }

    private QLGoogleIntegration(String userId, boolean shouldRememberLogin) {
        _userId = userId;
        _shouldRememberLogin = shouldRememberLogin;

        try {
            _httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DataStoreFactory dataStoreFactory = null;
        if (_shouldRememberLogin) {
            File f = new File("googlecred");
            try {
                dataStoreFactory = new FileDataStoreFactory(f);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            dataStoreFactory = new MemoryDataStoreFactory();
        }

        _googleLogin = new GoogleLogin(_userId, _httpTransport,
                dataStoreFactory);
    }

    public static QLGoogleIntegration getInstance() {
        if (instance == null) {
            instance = new QLGoogleIntegration();
        }
        return instance;
    }

    private void init() throws GeneralSecurityException, IOException {

        _cred = _googleLogin.getCredential();

        _googleCalendar = new GoogleCalConn(_cred, _httpTransport);
        _googleTasks = new GoogleTaskConn(_cred, _httpTransport);
    }

    private boolean isInitiated() {
        return (_googleLogin != null && _cred != null
                && _googleCalendar != null && _googleTasks != null);
    }

    public boolean logout() {
        _cred = null;
        _googleCalendar = null;
        _googleTasks = null;
        try {
            return _googleLogin.logout();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void sync(List<Task> taskList, List<String> deletedList) {

        try {
            if (!isInitiated()) {
                init();
            }

            String calId = PRIMARY_CALENDAR_ID;
            String taskListId = PRIMARY_TASKS_ID;

            Map<String, Task> calendarTask = new HashMap<String, Task>();
            Map<String, Task> tasksTask = new HashMap<String, Task>();

            createMapsForSync(taskList, calendarTask, tasksTask);
             
            deletedList.removeAll(calendarTask.keySet());
            deletedList.removeAll(tasksTask.keySet());
            
            deleteFromGoogle(deletedList, _googleCalendar, _googleTasks, calId,
                    taskListId);
            syncWithGoogleCalendar(taskList, _googleCalendar, _googleTasks,
                    calId, taskListId, calendarTask);
            syncWithGoogleTask(taskList, _googleCalendar, _googleTasks, calId,
                    taskListId, tasksTask);

        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void syncWithGoogleTask(List<Task> taskList,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId, Map<String, Task> tasksTask) throws IOException {
        List<com.google.api.services.tasks.model.Task> tasks = googleTasks
                .getTasks(taskListId).getItems();
        
        if (tasks != null) {
            for (com.google.api.services.tasks.model.Task gt : tasks) {
                Task matchingTask = tasksTask.remove(PREFIX_GOOGLEID_TASKS
                        + gt.getId());
                System.out.print(gt.getTitle());
                
                if (matchingTask == null) {
                    if (!isEmptyGoogleTask(gt)) {
                        Task newTask = new Task("");
                        updateTaskWithGoogleTask(newTask, gt);
                        taskList.add(newTask);
                        System.out.println(" new task");
                    }
                } else {
                    long difference = matchingTask.getLastUpdated()
                            .getTimeInMillis() - gt.getUpdated().getValue();
                    if ((-60000 > difference || difference > 60000)
                            && (matchingTask.getLastUpdated().getTimeInMillis() > gt
                                    .getUpdated().getValue())) {
                        if (!isCalendarEvent(matchingTask)) {
                            updateTaskToGoogleTasks(matchingTask,
                                    googleTasks, taskListId);
                            System.out.println(" update tasks");
                        } else {
                            changeGoogleTaskToGoogleCalendar(matchingTask,
                                    googleCalendar, googleTasks, calId, taskListId);
                            System.out.println(" change to calendar");
                        }
                    } else {
                        updateTaskWithGoogleTask(matchingTask, gt);
                        System.out.println(" update task");
                    }
                }
            }
        }

        for (Task t : tasksTask.values()) {
            System.out.println("delete task " + t.getName());
            taskList.remove(t);
        }

        for (Task t : taskList) {
            if (t.getGoogleID() == null || t.getGoogleID().isEmpty()) {
                if (!isCalendarEvent(t)) {
                    createNewTaskToGoogleTasks(t, googleTasks, taskListId);
                    System.out.println("create tasks " + t.getName());
                }
            }
        }
    }

    private void syncWithGoogleCalendar(List<Task> taskList,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId, Map<String, Task> calendarTask)
            throws IOException {
        List<Event> events = googleCalendar.getEvents(calId).getItems();
        if (events != null) {
            for (Event e : events) {
                Task matchingTask = calendarTask.remove(PREFIX_GOOGLEID_CALENDAR
                        + e.getId());
                System.out.print(e.getSummary());
                
    
                if (matchingTask == null) {
                    if (e.getRecurrence() == null) {
                        Task newTask = new Task("");
                        updateTaskWithGoogleEvent(newTask, e);
                        taskList.add(newTask);
                        System.out.println(" new task");
                    }
                } else {
                    long difference = matchingTask.getLastUpdated()
                            .getTimeInMillis() - e.getUpdated().getValue();
                    if ((-60000 > difference || difference > 60000)
                            && (matchingTask.getLastUpdated().getTimeInMillis() > e
                                    .getUpdated().getValue())) {
                        if (isCalendarEvent(matchingTask)) {
                            updateEventToGoogleCalendar(matchingTask,
                                    googleCalendar, calId);
                            System.out.println(" update event");
                        } else {
                            changeGoogleCalendarToGoogleTasks(matchingTask,
                                    googleCalendar, googleTasks, calId, taskListId);
                            System.out.println(" change to task");
                        }
                    } else {
                        updateTaskWithGoogleEvent(matchingTask, e);
                        System.out.println(" update task");
                    }
                }
            }
        }

        for (Task t : calendarTask.values()) {
            System.out.println("delete task " + t.getName());
            taskList.remove(t);
        }

        for (Task t : taskList) {
            if (t.getGoogleID() == null || t.getGoogleID().isEmpty()) {
                if (isCalendarEvent(t)) {
                    createNewEventToGoogleCalendar(t, googleCalendar, calId);
                    System.out.println("create event " + t.getName());
                }
            }
        }
    }

    private void deleteFromGoogle(List<String> deletedList,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) {
        Iterator<String> iter = deletedList.iterator();
        while (iter.hasNext()) {
            String id = iter.next();
            try {
                if (id.startsWith(PREFIX_GOOGLEID_CALENDAR)) {
                    googleCalendar.deleteEvent(calId,
                            id.substring(OFFSET_GOOGLEID_PREFIX));
                    iter.remove();

                } else if (id.startsWith(PREFIX_GOOGLEID_TASKS)) {
                    googleTasks.deleteTask(taskListId,
                            id.substring(OFFSET_GOOGLEID_PREFIX));
                    iter.remove();
                }
            } catch (IOException e) {

            }
        }
    }

    public List<Task> syncFrom(List<Task> taskList) {
        try {

            if (!isInitiated()) {
                init();
            }

            String calId = PRIMARY_CALENDAR_ID;
            String taskListId = PRIMARY_TASKS_ID;

            syncGoogleToTaskList(taskList, _googleCalendar, _googleTasks,
                    calId, taskListId);

            return taskList;

        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void syncTo(List<Task> taskList) {
        try {

            if (!isInitiated()) {
                init();
            }

            String calId = PRIMARY_CALENDAR_ID;
            String taskListId = PRIMARY_TASKS_ID;

            syncTaskListToGoogle(taskList, _googleCalendar, _googleTasks,
                    calId, taskListId);

        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void syncGoogleToTaskList(List<Task> taskList,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {

        Map<String, Task> calendarTask = new HashMap<String, Task>();
        Map<String, Task> tasksTask = new HashMap<String, Task>();

        createMapsForSync(taskList, calendarTask, tasksTask);

        syncGoogleCalendarToTaskList(taskList, googleCalendar, calId,
                calendarTask);

        syncGoogleTasksToTaskList(taskList, googleTasks, taskListId, tasksTask);
    }

    private void syncGoogleTasksToTaskList(List<Task> taskList,
            GoogleTaskConn googleTasks, String taskListId,
            Map<String, Task> tasksTask) throws IOException {
        if (taskListId.isEmpty()) {
            return;
        }
        List<com.google.api.services.tasks.model.Task> tasks = googleTasks
                .getTasks(taskListId).getItems();
        if (tasks != null) {
            for (com.google.api.services.tasks.model.Task t : tasks) {
                Task matchingTask = tasksTask.remove(PREFIX_GOOGLEID_TASKS
                        + t.getId());
                if (matchingTask == null) {
                    if (!isEmptyGoogleTask(t)) {
                        Task newTask = new Task("");
                        updateTaskWithGoogleTask(newTask, t);
                        taskList.add(newTask);
                    }
                } else {
                    updateTaskWithGoogleTask(matchingTask, t);
                }
            }
        }
        for (Task t : tasksTask.values()) {
            taskList.remove(t);
        }
    }

    private void syncGoogleCalendarToTaskList(List<Task> taskList,
            GoogleCalConn googleCalendar, String calId,
            Map<String, Task> calendarTask) throws IOException {
        if (calId.isEmpty())
            return;
        List<Event> events = googleCalendar.getEvents(calId).getItems();
        if (events != null) {
            for (Event e : events) {
                Task matchingTask = calendarTask
                        .remove(PREFIX_GOOGLEID_CALENDAR + e.getId());
                if (e.getRecurrence() != null) {
                    continue;
                }
                if (matchingTask == null) {
                    Task newTask = new Task("");
                    updateTaskWithGoogleEvent(newTask, e);
                    taskList.add(newTask);
                } else {
                    updateTaskWithGoogleEvent(matchingTask, e);
                }
            }
        }

        for (Task t : calendarTask.values()) {
            taskList.remove(t);
        }

    }

    private void createMapsForSync(List<Task> taskList,
            Map<String, Task> calendarTask, Map<String, Task> tasksTask) {
        for (Task t : taskList) {
            if ((t.getGoogleID() != null) && (!t.getGoogleID().isEmpty())) {
                if (t.getGoogleID().startsWith(PREFIX_GOOGLEID_CALENDAR)) {
                    calendarTask.put(t.getGoogleID(), t);
                } else {
                    tasksTask.put(t.getGoogleID(), t);
                }
            }
        }
    }

    private String createNewTaskListIfNotExist(String listName,
            GoogleTaskConn googleTasks, String taskListId) throws IOException {
        if (taskListId.equals("")) {
            taskListId = googleTasks.createTaskList(
                    new TaskList().setTitle(listName)).getId();
        }
        return taskListId;
    }

    private String createNewCalendarIfNotExist(String listName,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        if (calId.equals("")) {
            calId = googleCalendar.createCalendar(
                    new Calendar().setSummary(listName)).getId();
        }
        return calId;
    }

    private void syncTaskListToGoogle(List<Task> taskList,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {
        Events events = googleCalendar.getEvents(calId);
        Tasks tasks = googleTasks.getTasks(taskListId);
        Set<String> existingIds = new HashSet<String>();
        Set<String> currentEvents = new HashSet<String>();
        Set<String> currentTasks = new HashSet<String>();
        if ((events != null) && (events.getItems() != null)) {
            for (Event e : events.getItems()) {
                currentEvents.add(e.getId());
            }
        }
        if ((tasks != null) && (tasks.getItems() != null)) {
            for (com.google.api.services.tasks.model.Task t : tasks.getItems()) {
                currentTasks.add(t.getId());
            }
        }

        for (Task t : taskList) {
            if ((t.getGoogleID() != null) && (!t.getGoogleID().isEmpty())) {
                existingIds.add(t.getGoogleID().substring(
                        OFFSET_GOOGLEID_PREFIX));
            }
            if (isCalendarEvent(t)) {
                syncTaskToGoogleCalendar(googleCalendar, googleTasks, calId,
                        taskListId, t, currentEvents);
            } else {
                syncTaskToGoogleTasks(googleCalendar, googleTasks, calId,
                        taskListId, t, currentTasks);
            }
        }

        currentEvents.removeAll(existingIds);
        currentTasks.removeAll(existingIds);

        for (String id : currentEvents) {
            googleCalendar.deleteEvent(calId, id);
        }
        for (String id : currentTasks) {
            googleTasks.deleteTask(taskListId, id);
        }
    }

    private void syncTaskToGoogleCalendar(GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId,
            Task t, Set<String> currentEvents) throws IOException {
        if ((t.getGoogleID() != null) && (!t.getGoogleID().isEmpty())) {
            if (t.getGoogleID().startsWith(PREFIX_GOOGLEID_CALENDAR)) {
                if (currentEvents.contains(t.getGoogleID().substring(
                        OFFSET_GOOGLEID_PREFIX))) {
                    updateEventToGoogleCalendar(t, googleCalendar, calId);
                } else {
                    createNewEventToGoogleCalendar(t, googleCalendar, calId);
                }
            } else {
                changeGoogleTaskToGoogleCalendar(t, googleCalendar,
                        googleTasks, calId, taskListId);
            }
        } else {
            createNewEventToGoogleCalendar(t, googleCalendar, calId);
        }
    }

    private void updateEventToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        String id = t.getGoogleID().substring(OFFSET_GOOGLEID_PREFIX);
        Event e = googleCalendar.getEvent(calId, id);
        e = updateGoogleEventWithTask(e, t);
        googleCalendar.updateEvent(calId, id, e);
    }

    private void createNewEventToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        Event e = new Event();
        e = updateGoogleEventWithTask(e, t);
        t.setGoogleID(PREFIX_GOOGLEID_CALENDAR
                + googleCalendar.createEvent(calId, e).getId());
    }

    private void changeGoogleTaskToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {
        googleTasks.deleteTask(taskListId,
                t.getGoogleID().substring(OFFSET_GOOGLEID_PREFIX));
        t.setGoogleID(null);
        createNewEventToGoogleCalendar(t, googleCalendar, calId);
    }

    private void syncTaskToGoogleTasks(GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId,
            Task t, Set<String> currentTasks) throws IOException {
        if ((t.getGoogleID() != null) && (!t.getGoogleID().isEmpty())) {
            if (t.getGoogleID().startsWith(PREFIX_GOOGLEID_TASKS)) {
                if (currentTasks.contains(t.getGoogleID().substring(
                        OFFSET_GOOGLEID_PREFIX))) {
                    updateTaskToGoogleTasks(t, googleTasks, taskListId);
                } else {
                    createNewTaskToGoogleTasks(t, googleTasks, taskListId);
                }
            } else {
                changeGoogleCalendarToGoogleTasks(t, googleCalendar,
                        googleTasks, calId, taskListId);
            }
        } else {
            createNewTaskToGoogleTasks(t, googleTasks, taskListId);
        }
    }

    private void updateTaskToGoogleTasks(Task t, GoogleTaskConn googleTasks,
            String taskListId) throws IOException {
        String id = t.getGoogleID().substring(OFFSET_GOOGLEID_PREFIX);
        com.google.api.services.tasks.model.Task gt = googleTasks.getTask(
                taskListId, id);
        gt = updateGoogleTaskWithTask(gt, t);
        googleTasks.updateTask(taskListId, id, gt);
    }

    private void createNewTaskToGoogleTasks(Task t, GoogleTaskConn googleTasks,
            String taskListId) throws IOException {
        com.google.api.services.tasks.model.Task gt = new com.google.api.services.tasks.model.Task();
        gt = updateGoogleTaskWithTask(gt, t);
        t.setGoogleID(PREFIX_GOOGLEID_TASKS
                + googleTasks.createTask(taskListId, gt).getId());
    }

    private void changeGoogleCalendarToGoogleTasks(Task t,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {
        googleCalendar.deleteEvent(calId,
                t.getGoogleID().substring(OFFSET_GOOGLEID_PREFIX));
        t.setGoogleID(null);
        createNewTaskToGoogleTasks(t, googleTasks, taskListId);
    }

    private String getCalendarIdByName(String calendarName,
            GoogleCalConn googleCalendar) throws IOException {
        List<CalendarListEntry> calendars = googleCalendar.getCalendars()
                .getItems();
        String id = "";
        for (CalendarListEntry c : calendars) {
            if (c.getSummary().equals(calendarName)) {
                id = c.getId();
                break;
            }
        }
        return id;
    }

    private String getTaskListIdByName(String taskListName,
            GoogleTaskConn googleTasks) throws IOException {
        List<TaskList> taskLists = googleTasks.getTaskLists().getItems();
        String id = "";
        for (TaskList t : taskLists) {
            if (t.getTitle().equals(taskListName)) {
                id = t.getId();
                break;
            }
        }
        return id;
    }

    private void updateTaskWithGoogleEvent(Task t, Event e) {
        t.setName(correctEmptySummaryToProperName(e.getSummary()));
        t.setDescription(e.getDescription());
        t.setGoogleID(PREFIX_GOOGLEID_CALENDAR + e.getId());
        if (e.getStart().getDateTime() != null) {
            java.util.Calendar cal = dateTimeToCalendar(e.getStart()
                    .getDateTime());
            t.setStartDate(cal);
            t.setHasStartTime(true);
        } else if (e.getStart().getDate() != null) {
            java.util.Calendar cal = dateToCalendar(e.getStart().getDate());
            t.setStartDate(cal);
            t.setHasStartTime(false);
        }
        if (e.getEnd().getDateTime() != null) {
            java.util.Calendar cal = dateTimeToCalendar(e.getEnd()
                    .getDateTime());
            t.setDueDate(cal);
            t.setHasDueTime(true);
        } else if (e.getEnd().getDate() != null) {
            java.util.Calendar cal = dateToCalendar(e.getEnd().getDate());
            cal.add(java.util.Calendar.SECOND, -1);
            t.setDueDate(cal);
            t.setHasDueTime(false);
        }
        t.setLastUpdated(dateTimeToCalendar(e.getUpdated()));
    }

    private void updateTaskWithGoogleTask(Task t,
            com.google.api.services.tasks.model.Task gt) {
        t.setName(correctEmptySummaryToProperName(gt.getTitle()));
        t.setDescription(gt.getNotes());
        t.setGoogleID(PREFIX_GOOGLEID_TASKS + gt.getId());
        if (gt.getDue() != null) {
            java.util.Calendar cal = dateToCalendar(gt.getDue());
            cal.add(java.util.Calendar.DATE, 1);
            cal.add(java.util.Calendar.SECOND, -1);
            t.setDueDate(cal);
            t.setHasDueTime(false);
        }
        if (gt.getStatus().equals(STATUS_TASKS_COMPLETED)) {
            t.setIsCompleted(true);
        } else {
            t.setIsCompleted(false);
        }
        t.setLastUpdated(dateTimeToCalendar(gt.getUpdated()));
    }

    private Event updateGoogleEventWithTask(Event e, Task t) {
        e.setSummary(t.getName());
        e.setDescription(t.getDescription());

        if ((e.getStart() != null) && (e.getStart().getDateTime() != null)) {
            DateTime startDT = calendarToDateTime(t.getStartDate());
            e.setStart(new EventDateTime().setDateTime(startDT));

            DateTime endDT = calendarToDateTime(t.getDueDate());
            e.setEnd(new EventDateTime().setDateTime(endDT));
        } else if (t.getHasDueTime() != t.getHasStartTime()) {
            DateTime startDT = calendarToDateTime(t.getStartDate());
            e.setStart(new EventDateTime().setDateTime(startDT));

            DateTime endDT = calendarToDateTime(t.getDueDate());
            e.setEnd(new EventDateTime().setDateTime(endDT));
        } else if (t.getHasStartTime()) {
            DateTime startDT = calendarToDateTime(t.getStartDate());
            e.setStart(new EventDateTime().setDateTime(startDT));

            DateTime endDT = calendarToDateTime(t.getDueDate());
            e.setEnd(new EventDateTime().setDateTime(endDT));
        } else {
            DateTime startDT = calendarToDate(t.getStartDate());
            e.setStart(new EventDateTime().setDate(startDT));

            java.util.Calendar dueCal = (java.util.Calendar) t.getDueDate()
                    .clone();
            dueCal.add(java.util.Calendar.DATE, 1);
            DateTime endDT = calendarToDate(dueCal);
            e.setEnd(new EventDateTime().setDate(endDT));
        }

        return e;
    }

    private com.google.api.services.tasks.model.Task updateGoogleTaskWithTask(
            com.google.api.services.tasks.model.Task gt, Task t) {
        gt.setTitle(t.getName());
        gt.setNotes(t.getDescription());
        if (t.getDueDate() != null) {
            DateTime dt = calendarToDateTime(t.getDueDate());
            gt.setDue(dt);
        }
        if ((t.getIsCompleted()) && (gt.getCompleted() == null)) {
            DateTime dt = new DateTime(new Date());
            gt.setCompleted(dt);
            gt.setStatus(STATUS_TASKS_COMPLETED);
        } else if (!t.getIsCompleted()) {
            gt.setCompleted(null);
            gt.setStatus(STATUS_TASKS_NEEDSACTION);
        }
        return gt;
    }

    private boolean isCalendarEvent(Task t) {
        return ((t.getStartDate() != null) && (t.getDueDate() != null));
    }

    private boolean isEmptyGoogleTask(com.google.api.services.tasks.model.Task t) {
        if ((t.getTitle().isEmpty()) && (t.getDue() == null)
                && (t.getNotes() == null)) {
            return true;
        }
        // Fixes an issue where empty tasks is added
        // because google calendar web interface is used on the browser Opera
        // 12.14
        if ((t.getTitle().length() == 1) && (t.getTitle().equals("\n"))) {
            return true;
        }
        return false;
    }

    private String correctEmptySummaryToProperName(String s) {
        if ((s != null) && (!s.equals(""))) {
            return s;
        }
        return "(No Title)";
    }

    private DateTime calendarToDateTime(java.util.Calendar c) {
        return new DateTime(false, c.getTimeInMillis(), null);
    }

    private DateTime calendarToDate(java.util.Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        return DateTime.parseRfc3339(sdf.format(c.getTime()));
    }

    private java.util.Calendar dateTimeToCalendar(DateTime dt) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(dt.getValue());
        return cal;
    }

    private java.util.Calendar dateToCalendar(DateTime dt) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(dt.getValue() - cal.getTimeZone().getRawOffset());
        return cal;
    }

}
