package quicklyst;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

//@author A01112707N
public class GoogleIntegration {

    private static final int TIME_LAST_HOUR = 23;
    private static final int TIME_LAST_MINUTE = 59;
    private static final int TIME_LAST_SECOND = 59;

    private static final int RFC3339_END_INDEX_DATE = 10;
    private static final int RFC3339_END_INDEX_MONTH = 7;
    private static final int RFC3339_END_INDEX_YEAR = 4;
    private static final int RFC3339_START_INDEX_DATE = 8;
    private static final int RFC3339_START_INDEX_MONTH = 5;
    private static final int RFC3339_START_INDEX_YEAR = 0;

    private static final String RFC3339_SUFFIX_TIME_ZERO = "T00:00:00Z";

    private static final String RFC3339_DATEFORMAT = "yyyy-MM-dd";

    private static final String LOG_SYNC_EVENT = "syncing event %s with local task";
    private static final String LOG_RETRIEVING_EVENTS = "Retrieving events from Google service";
    private static final String LOG_SYNCING_LOCALLY_DELETED_EVENT = "Deleting locally-deleted event";
    private static final String LOG_SYNCING_LOCAL_EVENTS = "Add local events to Google service";
    private static final String LOG_SYNCING_LOCAL_TASKS = "Add local tasks to Google service";
    private static final String LOG_SYNCING_REMOTELY_DELETED = "Removing local tasks that are deleted on Google service";
    private static final String LOG_SYNC_TASK = "syncing task %s with local task";
    private static final String LOG_CREATE_TASK = "Creating new task %s";
    private static final String LOG_RETRIEVING_TASKS = "Retrieving tasks from Google service";
    private static final String LOG_SYNCING_LOCALLY_DELETED_TASK = "Deleting locally-deleted task";
    private static final String LOG_GOOGLE_TASKS_SYNC = "Syncing with Google Tasks";
    private static final String LOG_GOOGLE_CALENDAR_SYNC = "Syncing with Google Calendar";
    private static final String LOG_INITIALIZE_GOOGLE = "Initializing Google Integration";
    private static final String LOG_EXCEPTION = "%s was thrown";

    private static final int MILLISECONDS_DIFFERENCE_THRESHOLD = 60000;

    private static final String STRING_NEXTLINECHAR = "\n";

    private static final String DEFAULT_TASK_NAME = "(No Title)";

    private static final String FILEDATASTORE_CHILD_DIRECTORY = ".quicklyst";
    private static final String FILEDATASTORE_PARENT_DIRECTORY = System
            .getProperty("user.home");

    private static final String PRIMARY_CALENDAR_ID = "primary";
    private static final String PRIMARY_TASKS_ID = "@default";

    private static final String PREFIX_GOOGLEID_TASKS = "t";
    private static final String PREFIX_GOOGLEID_CALENDAR = "c";
    private static final String USER_ID = "u";

    private static final String STATUS_TASKS_NEEDSACTION = "needsAction";
    private static final String STATUS_TASKS_COMPLETED = "completed";

    private static final int LENGTH_NEXTLINECHAR = 1;

    private static final int OFFSET_MONTH = -1;
    private static final int OFFSET_DUESECOND = -1;
    private static final int OFFSET_DUEDATE = 1;
    private static final int OFFSET_GOOGLEID_PREFIX = 1;

    private final static Logger LOGGER = Logger
            .getLogger(GoogleIntegration.class.getName());

    private static GoogleIntegration _instance;

    private String _userId;
    private boolean _shouldRememberLogin;

    private Credential _cred;
    private GoogleCalConn _googleCalendar;
    private GoogleTaskConn _googleTasks;
    private GoogleLogin _googleLogin;

    private HttpTransport _httpTransport;

    private GoogleIntegration() {
        this(USER_ID, true);
    }

    private GoogleIntegration(String userId, boolean shouldRememberLogin) {
        _userId = userId;
        _shouldRememberLogin = shouldRememberLogin;
    }

    public static GoogleIntegration getInstance() {
        if (_instance == null) {
            _instance = new GoogleIntegration();
        }
        return _instance;
    }

    private void init() throws GeneralSecurityException, IOException {

        _httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        DataStoreFactory dataStoreFactory = null;
        if (_shouldRememberLogin) {
            File f = new File(FILEDATASTORE_PARENT_DIRECTORY,
                    FILEDATASTORE_CHILD_DIRECTORY);
            try {
                dataStoreFactory = new FileDataStoreFactory(f);
            } catch (IOException e) {
                LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass()
                        .getName()));
                dataStoreFactory = new MemoryDataStoreFactory();
            }
        } else {
            dataStoreFactory = new MemoryDataStoreFactory();
        }

        _googleLogin = new GoogleLogin(_userId, _httpTransport,
                dataStoreFactory);

        _cred = _googleLogin.getCredential();

        _googleCalendar = new GoogleCalConn(_cred, _httpTransport);
        _googleTasks = new GoogleTaskConn(_cred, _httpTransport);
    }

    private boolean isInitiated() {
        return (_googleLogin != null && _cred != null
                && _googleCalendar != null && _googleTasks != null);
    }

    public boolean logout() {
        try {
            GoogleLogin temp = _googleLogin;
            _cred = null;
            _googleCalendar = null;
            _googleTasks = null;
            _googleLogin = null;
            if (temp != null) {
                return temp.logout();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void sync(List<Task> taskList, List<String> deletedIds) {
        assert taskList != null;
        assert deletedIds != null;

        try {
            if (!isInitiated()) {
                LOGGER.info(LOG_INITIALIZE_GOOGLE);
                init();
            }

            String calId = PRIMARY_CALENDAR_ID;
            String taskListId = PRIMARY_TASKS_ID;

            LOGGER.info(LOG_GOOGLE_CALENDAR_SYNC);
            syncWithGoogleCalendar(taskList, deletedIds, _googleCalendar,
                    _googleTasks, calId, taskListId);

            LOGGER.info(LOG_GOOGLE_TASKS_SYNC);
            syncWithGoogleTask(taskList, deletedIds, _googleCalendar,
                    _googleTasks, calId, taskListId);

        } catch (GeneralSecurityException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(
                    GlobalConstants.ERROR_SECURE_CONNECTION_UNAVAILABLE);
        } catch (IOException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(GlobalConstants.ERROR_SYNC);
        }

    }

    private void syncWithGoogleTask(List<Task> taskList,
            List<String> deletedIds, GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId)
            throws IOException {

        Map<String, Task> tasksTask = new HashMap<String, Task>();

        generateGoogleTasksMap(taskList, tasksTask);

        removeExistingTaskFromDeletedList(deletedIds, tasksTask);

        LOGGER.info(LOG_SYNCING_LOCALLY_DELETED_TASK);
        deleteTasksFromGoogleServices(deletedIds, googleTasks, taskListId);

        LOGGER.info(LOG_RETRIEVING_TASKS);
        List<com.google.api.services.tasks.model.Task> tasks = getTasksFromGoogleServices(
                googleTasks, taskListId);

        if (tasks != null) {
            for (com.google.api.services.tasks.model.Task gt : tasks) {
                Task matchingTask = tasksTask.remove(PREFIX_GOOGLEID_TASKS
                        + gt.getId());

                if (matchingTask == null) {
                    LOGGER.info(String.format(LOG_CREATE_TASK, gt.getTitle()));
                    createNewTaskFromGoogleTasks(taskList, gt);
                } else {
                    LOGGER.info(String.format(LOG_SYNC_TASK, gt.getTitle()));
                    syncGoogleTaskWithLocalTask(googleCalendar, googleTasks,
                            calId, taskListId, gt, matchingTask);
                }
            }
        }

        LOGGER.info(LOG_SYNCING_REMOTELY_DELETED);
        removeLocalTasks(taskList, tasksTask);

        LOGGER.info(LOG_SYNCING_LOCAL_TASKS);
        addTasksToGoogleServices(taskList, googleTasks, taskListId);
    }

    private void createNewTaskFromGoogleTasks(List<Task> taskList,
            com.google.api.services.tasks.model.Task gt) {
        if (!isEmptyGoogleTask(gt)) {
            Task newTask = new Task(DEFAULT_TASK_NAME);
            updateTaskWithGoogleTask(newTask, gt);
            taskList.add(newTask);
        }
    }

    private void syncGoogleTaskWithLocalTask(GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId,
            com.google.api.services.tasks.model.Task gt, Task matchingTask)
            throws IOException {
        long difference = matchingTask.getLastUpdated().getTimeInMillis()
                - gt.getUpdated().getValue();
        if (((-MILLISECONDS_DIFFERENCE_THRESHOLD > difference) || 
             (difference > MILLISECONDS_DIFFERENCE_THRESHOLD)) && 
            (matchingTask.getLastUpdated().getTimeInMillis() > gt.getUpdated().getValue())) {
            if (!isCalendarEvent(matchingTask)) {
                updateTaskToGoogleTasks(matchingTask, googleTasks, taskListId);
            } else {
                changeGoogleTaskToGoogleCalendar(matchingTask, googleCalendar,
                        googleTasks, calId, taskListId);
            }
        } else {
            updateTaskWithGoogleTask(matchingTask, gt);
        }
    }

    private void addTasksToGoogleServices(List<Task> taskList,
            GoogleTaskConn googleTasks, String taskListId) throws IOException {
        for (Task t : taskList) {
            if ((t != null)
                    && ((t.getGoogleId() == null) || (t.getGoogleId().isEmpty()))) {
                if (!isCalendarEvent(t)) {
                    createNewTaskToGoogleTasks(t, googleTasks, taskListId);
                }
            }
        }
    }

    private void removeLocalTasks(List<Task> taskList,
            Map<String, Task> remainingMap) {
        for (Task t : remainingMap.values()) {
            taskList.remove(t);
        }
    }

    private List<com.google.api.services.tasks.model.Task> getTasksFromGoogleServices(
            GoogleTaskConn googleTasks, String taskListId) throws IOException {
        List<com.google.api.services.tasks.model.Task> tasks = googleTasks
                .getTasks(taskListId).getItems();
        return tasks;
    }

    private void deleteTasksFromGoogleServices(List<String> deletedIds,
            GoogleTaskConn googleTasks, String taskListId) throws IOException {
        for (String id : deletedIds) {
            if ((id != null) && (id.startsWith(PREFIX_GOOGLEID_TASKS))) {
                googleTasks.deleteTask(taskListId,
                        id.substring(OFFSET_GOOGLEID_PREFIX));
            }
        }
    }

    private void removeExistingTaskFromDeletedList(List<String> deletedIds,
            Map<String, Task> existingMap) {
        deletedIds.removeAll(existingMap.keySet());
    }

    private void generateGoogleTasksMap(List<Task> taskList,
            Map<String, Task> tasksTask) {
        for (Task t : taskList) {
            if ((t != null) && (t.getGoogleId() != null)
                    && (!t.getGoogleId().isEmpty())) {
                if (t.getGoogleId().startsWith(PREFIX_GOOGLEID_TASKS)) {
                    tasksTask.put(t.getGoogleId(), t);
                }
            }
        }
    }

    private void syncWithGoogleCalendar(List<Task> taskList,
            List<String> deletedIds, GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId)
            throws IOException {

        Map<String, Task> calendarTask = new HashMap<String, Task>();

        generateGoogleEventsMap(taskList, calendarTask);

        removeExistingTaskFromDeletedList(deletedIds, calendarTask);

        LOGGER.info(LOG_SYNCING_LOCALLY_DELETED_EVENT);
        deleteEventsFromGoogleServices(deletedIds, googleCalendar, calId);

        LOGGER.info(LOG_RETRIEVING_EVENTS);
        List<Event> events = getEventsFromGoogleServices(googleCalendar, calId);

        if (events != null) {
            for (Event e : events) {
                Task matchingTask = calendarTask
                        .remove(PREFIX_GOOGLEID_CALENDAR + e.getId());

                if (matchingTask == null) {
                    LOGGER.info(String.format(LOG_CREATE_TASK, e.getSummary()));
                    createNewTaskFromGoogleEvents(taskList, e);
                } else {
                    LOGGER.info(String.format(LOG_SYNC_EVENT, e.getSummary()));
                    syncGoogleEventWithLocalTask(googleCalendar, googleTasks,
                            calId, taskListId, e, matchingTask);
                }
            }
        }

        LOGGER.info(LOG_SYNCING_REMOTELY_DELETED);
        removeLocalTasks(taskList, calendarTask);

        LOGGER.info(LOG_SYNCING_LOCAL_EVENTS);
        addEventsToGoogleServices(taskList, googleCalendar, calId);
    }

    private void addEventsToGoogleServices(List<Task> taskList,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        for (Task t : taskList) {
            if ((t != null)
                    && ((t.getGoogleId() == null) || (t.getGoogleId().isEmpty()))) {
                if (isCalendarEvent(t)) {
                    createNewEventToGoogleCalendar(t, googleCalendar, calId);
                }
            }
        }
    }

    private void syncGoogleEventWithLocalTask(GoogleCalConn googleCalendar,
            GoogleTaskConn googleTasks, String calId, String taskListId,
            Event e, Task matchingTask) throws IOException {
        long difference = matchingTask.getLastUpdated().getTimeInMillis()
                - e.getUpdated().getValue();
        if (((-MILLISECONDS_DIFFERENCE_THRESHOLD > difference) || 
             (difference > MILLISECONDS_DIFFERENCE_THRESHOLD)) && 
            (matchingTask.getLastUpdated().getTimeInMillis() > e.getUpdated().getValue())) {
            if (isCalendarEvent(matchingTask)) {
                updateEventToGoogleCalendar(matchingTask, googleCalendar, calId);
            } else {
                changeGoogleCalendarToGoogleTasks(matchingTask, googleCalendar,
                        googleTasks, calId, taskListId);
            }
        } else {
            updateTaskWithGoogleEvent(matchingTask, e);
        }
    }

    private void createNewTaskFromGoogleEvents(List<Task> taskList, Event e) {
        if (e.getRecurrence() == null) {
            Task newTask = new Task(DEFAULT_TASK_NAME);
            updateTaskWithGoogleEvent(newTask, e);
            taskList.add(newTask);
        }
    }

    private List<Event> getEventsFromGoogleServices(
            GoogleCalConn googleCalendar, String calId) throws IOException {
        return googleCalendar.getEvents(calId).getItems();
    }

    private void deleteEventsFromGoogleServices(List<String> deletedIds,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        for (String id : deletedIds) {
            if ((id != null) && (id.startsWith(PREFIX_GOOGLEID_CALENDAR))) {
                googleCalendar.deleteEvent(calId,
                        id.substring(OFFSET_GOOGLEID_PREFIX));
            }
        }
    }

    private void generateGoogleEventsMap(List<Task> taskList,
            Map<String, Task> calendarTask) {
        for (Task t : taskList) {
            if ((t != null) && (t.getGoogleId() != null)
                    && (!t.getGoogleId().isEmpty())) {
                if (t.getGoogleId().startsWith(PREFIX_GOOGLEID_CALENDAR)) {
                    calendarTask.put(t.getGoogleId(), t);
                }
            }
        }
    }

    private void updateEventToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        String id = t.getGoogleId().substring(OFFSET_GOOGLEID_PREFIX);
        Event e = googleCalendar.getEvent(calId, id);
        e = updateGoogleEventWithTask(e, t);
        googleCalendar.updateEvent(calId, id, e);
    }

    private void createNewEventToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, String calId) throws IOException {
        Event e = new Event();
        e = updateGoogleEventWithTask(e, t);
        t.setGoogleId(PREFIX_GOOGLEID_CALENDAR
                + googleCalendar.createEvent(calId, e).getId());
    }

    private void changeGoogleTaskToGoogleCalendar(Task t,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {
        googleTasks.deleteTask(taskListId,
                t.getGoogleId().substring(OFFSET_GOOGLEID_PREFIX));
        t.setGoogleId(null);
        createNewEventToGoogleCalendar(t, googleCalendar, calId);
    }

    private void updateTaskToGoogleTasks(Task t, GoogleTaskConn googleTasks,
            String taskListId) throws IOException {
        String id = t.getGoogleId().substring(OFFSET_GOOGLEID_PREFIX);
        com.google.api.services.tasks.model.Task gt = googleTasks.getTask(
                taskListId, id);
        gt = updateGoogleTaskWithTask(gt, t);
        googleTasks.updateTask(taskListId, id, gt);
    }

    private void createNewTaskToGoogleTasks(Task t, GoogleTaskConn googleTasks,
            String taskListId) throws IOException {
        com.google.api.services.tasks.model.Task gt = 
                new com.google.api.services.tasks.model.Task();
        gt = updateGoogleTaskWithTask(gt, t);
        t.setGoogleId(PREFIX_GOOGLEID_TASKS
                + googleTasks.createTask(taskListId, gt).getId());
    }

    private void changeGoogleCalendarToGoogleTasks(Task t,
            GoogleCalConn googleCalendar, GoogleTaskConn googleTasks,
            String calId, String taskListId) throws IOException {
        googleCalendar.deleteEvent(calId,
                t.getGoogleId().substring(OFFSET_GOOGLEID_PREFIX));
        t.setGoogleId(null);
        createNewTaskToGoogleTasks(t, googleTasks, taskListId);
    }

    private void updateTaskWithGoogleEvent(Task t, Event e) {
        t.setName(correctEmptySummaryToProperName(e.getSummary()));
        t.setDescription(e.getDescription());
        t.setGoogleId(PREFIX_GOOGLEID_CALENDAR + e.getId());
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
            cal.add(java.util.Calendar.SECOND, OFFSET_DUESECOND);
            t.setDueDate(cal);
            t.setHasDueTime(false);
        }
        t.setLastUpdated(dateTimeToCalendar(e.getUpdated()));
    }

    private void updateTaskWithGoogleTask(Task t,
            com.google.api.services.tasks.model.Task gt) {
        t.setName(correctEmptySummaryToProperName(gt.getTitle()));
        t.setDescription(gt.getNotes());
        t.setGoogleId(PREFIX_GOOGLEID_TASKS + gt.getId());
        if (gt.getDue() != null) {
            java.util.Calendar cal = tasksDateTimeToCalendar(gt.getDue());
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
            dueCal.add(java.util.Calendar.DATE, OFFSET_DUEDATE);
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
            DateTime dt = tasksCalendarToDateTime(t.getDueDate());
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
        if ((t.getTitle().length() == LENGTH_NEXTLINECHAR)
                && (t.getTitle().equals(STRING_NEXTLINECHAR))) {
            return true;
        }
        return false;
    }

    private String correctEmptySummaryToProperName(String s) {
        if ((s != null) && (!s.isEmpty())) {
            return s;
        }
        return DEFAULT_TASK_NAME;
    }

    private DateTime calendarToDateTime(java.util.Calendar c) {
        return new DateTime(false, c.getTimeInMillis(), null);
    }

    private DateTime calendarToDate(java.util.Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat(RFC3339_DATEFORMAT);
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

    private DateTime tasksCalendarToDateTime(java.util.Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat(RFC3339_DATEFORMAT);
        return DateTime.parseRfc3339(sdf.format(c.getTime())
                + RFC3339_SUFFIX_TIME_ZERO);
    }

    private Calendar tasksDateTimeToCalendar(DateTime dt) {
        try {
            String rfc3339 = dt.toStringRfc3339();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(rfc3339.substring(
                    RFC3339_START_INDEX_YEAR, RFC3339_END_INDEX_YEAR)));
            cal.set(Calendar.MONTH,
                    Integer.parseInt(rfc3339.substring(
                            RFC3339_START_INDEX_MONTH, RFC3339_END_INDEX_MONTH))
                            + OFFSET_MONTH);
            cal.set(Calendar.DATE, Integer.parseInt(rfc3339.substring(
                    RFC3339_START_INDEX_DATE, RFC3339_END_INDEX_DATE)));
            cal.set(Calendar.HOUR_OF_DAY, TIME_LAST_HOUR);
            cal.set(Calendar.MINUTE, TIME_LAST_MINUTE);
            cal.set(Calendar.SECOND, TIME_LAST_SECOND);
            return cal;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // @author A0112707N-unused
    // push, pull changed to a more user-friendly sync
    /*
     * public void syncFrom(List<Task> taskList) { assert taskList != null; try
     * {
     * 
     * if (!isInitiated()) { init(); }
     * 
     * String calId = PRIMARY_CALENDAR_ID; String taskListId = PRIMARY_TASKS_ID;
     * 
     * syncGoogleToTaskList(taskList, _googleCalendar, _googleTasks, calId,
     * taskListId);
     * 
     * } catch (GeneralSecurityException e) {
     * LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
     * throw new Error( MessageConstants.ERROR_SECURE_CONNECTION_UNAVAILABLE); }
     * catch (IOException e) { LOGGER.severe(String.format(LOG_EXCEPTION,
     * e.getClass().getName())); throw new Error(MessageConstants.ERROR_SYNC); }
     * }
     * 
     * public void syncTo(List<Task> taskList) { assert taskList != null; try {
     * 
     * if (!isInitiated()) { init(); }
     * 
     * String calId = PRIMARY_CALENDAR_ID; String taskListId = PRIMARY_TASKS_ID;
     * 
     * syncTaskListToGoogle(taskList, _googleCalendar, _googleTasks, calId,
     * taskListId);
     * 
     * } catch (GeneralSecurityException e) {
     * LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
     * throw new Error( MessageConstants.ERROR_SECURE_CONNECTION_UNAVAILABLE); }
     * catch (IOException e) { LOGGER.severe(String.format(LOG_EXCEPTION,
     * e.getClass().getName())); throw new Error(MessageConstants.ERROR_SYNC); }
     * }
     * 
     * private void syncGoogleToTaskList(List<Task> taskList, GoogleCalConn
     * googleCalendar, GoogleTaskConn googleTasks, String calId, String
     * taskListId) throws IOException {
     * 
     * Map<String, Task> calendarTask = new HashMap<String, Task>(); Map<String,
     * Task> tasksTask = new HashMap<String, Task>();
     * 
     * createMapsForSync(taskList, calendarTask, tasksTask);
     * 
     * syncGoogleCalendarToTaskList(taskList, googleCalendar, calId,
     * calendarTask);
     * 
     * syncGoogleTasksToTaskList(taskList, googleTasks, taskListId, tasksTask);
     * }
     * 
     * private void syncGoogleTasksToTaskList(List<Task> taskList,
     * GoogleTaskConn googleTasks, String taskListId, Map<String, Task>
     * tasksTask) throws IOException { if (taskListId.isEmpty()) { return; }
     * List<com.google.api.services.tasks.model.Task> tasks = googleTasks
     * .getTasks(taskListId).getItems(); if (tasks != null) { for
     * (com.google.api.services.tasks.model.Task t : tasks) { Task matchingTask
     * = tasksTask.remove(PREFIX_GOOGLEID_TASKS + t.getId()); if (matchingTask
     * == null) { if (!isEmptyGoogleTask(t)) { Task newTask = new Task("");
     * updateTaskWithGoogleTask(newTask, t); taskList.add(newTask); } } else {
     * updateTaskWithGoogleTask(matchingTask, t); } } } for (Task t :
     * tasksTask.values()) { taskList.remove(t); } }
     * 
     * private void syncGoogleCalendarToTaskList(List<Task> taskList,
     * GoogleCalConn googleCalendar, String calId, Map<String, Task>
     * calendarTask) throws IOException { if (calId.isEmpty()) return;
     * List<Event> events = googleCalendar.getEvents(calId).getItems(); if
     * (events != null) { for (Event e : events) { Task matchingTask =
     * calendarTask .remove(PREFIX_GOOGLEID_CALENDAR + e.getId()); if
     * (e.getRecurrence() != null) { continue; } if (matchingTask == null) {
     * Task newTask = new Task(""); updateTaskWithGoogleEvent(newTask, e);
     * taskList.add(newTask); } else { updateTaskWithGoogleEvent(matchingTask,
     * e); } } }
     * 
     * for (Task t : calendarTask.values()) { taskList.remove(t); }
     * 
     * }
     * 
     * private String createNewTaskListIfNotExist(String listName,
     * GoogleTaskConn googleTasks, String taskListId) throws IOException { if
     * (taskListId.equals("")) { taskListId = googleTasks.createTaskList( new
     * TaskList().setTitle(listName)).getId(); } return taskListId; }
     * 
     * private String createNewCalendarIfNotExist(String listName, GoogleCalConn
     * googleCalendar, String calId) throws IOException { if (calId.equals(""))
     * { calId = googleCalendar.createCalendar( new
     * com.google.api.services.calendar.model.Calendar()
     * .setSummary(listName)).getId(); } return calId; }
     * 
     * private void syncTaskListToGoogle(List<Task> taskList, GoogleCalConn
     * googleCalendar, GoogleTaskConn googleTasks, String calId, String
     * taskListId) throws IOException { Events events =
     * googleCalendar.getEvents(calId); Tasks tasks =
     * googleTasks.getTasks(taskListId); Set<String> existingIds = new
     * HashSet<String>(); Set<String> currentEvents = new HashSet<String>();
     * Set<String> currentTasks = new HashSet<String>(); if ((events != null) &&
     * (events.getItems() != null)) { for (Event e : events.getItems()) {
     * currentEvents.add(e.getId()); } } if ((tasks != null) &&
     * (tasks.getItems() != null)) { for
     * (com.google.api.services.tasks.model.Task t : tasks.getItems()) {
     * currentTasks.add(t.getId()); } }
     * 
     * for (Task t : taskList) { if ((t.getGoogleId() != null) &&
     * (!t.getGoogleId().isEmpty())) {
     * existingIds.add(t.getGoogleId().substring( OFFSET_GOOGLEID_PREFIX)); } if
     * (isCalendarEvent(t)) { syncTaskToGoogleCalendar(googleCalendar,
     * googleTasks, calId, taskListId, t, currentEvents); } else {
     * syncTaskToGoogleTasks(googleCalendar, googleTasks, calId, taskListId, t,
     * currentTasks); } }
     * 
     * currentEvents.removeAll(existingIds);
     * currentTasks.removeAll(existingIds);
     * 
     * for (String id : currentEvents) { googleCalendar.deleteEvent(calId, id);
     * } for (String id : currentTasks) { googleTasks.deleteTask(taskListId,
     * id); } }
     * 
     * private void syncTaskToGoogleTasks(GoogleCalConn googleCalendar,
     * GoogleTaskConn googleTasks, String calId, String taskListId, Task t,
     * Set<String> currentTasks) throws IOException { if ((t.getGoogleId() !=
     * null) && (!t.getGoogleId().isEmpty())) { if
     * (t.getGoogleId().startsWith(PREFIX_GOOGLEID_TASKS)) { if
     * (currentTasks.contains(t.getGoogleId().substring(
     * OFFSET_GOOGLEID_PREFIX))) { updateTaskToGoogleTasks(t, googleTasks,
     * taskListId); } else { createNewTaskToGoogleTasks(t, googleTasks,
     * taskListId); } } else { changeGoogleCalendarToGoogleTasks(t,
     * googleCalendar, googleTasks, calId, taskListId); } } else {
     * createNewTaskToGoogleTasks(t, googleTasks, taskListId); } }
     * 
     * private void syncTaskToGoogleCalendar(GoogleCalConn googleCalendar,
     * GoogleTaskConn googleTasks, String calId, String taskListId, Task t,
     * Set<String> currentEvents) throws IOException { if ((t.getGoogleId() !=
     * null) && (!t.getGoogleId().isEmpty())) { if
     * (t.getGoogleId().startsWith(PREFIX_GOOGLEID_CALENDAR)) { if
     * (currentEvents.contains(t.getGoogleId().substring(
     * OFFSET_GOOGLEID_PREFIX))) { updateEventToGoogleCalendar(t,
     * googleCalendar, calId); } else { createNewEventToGoogleCalendar(t,
     * googleCalendar, calId); } } else { changeGoogleTaskToGoogleCalendar(t,
     * googleCalendar, googleTasks, calId, taskListId); } } else {
     * createNewEventToGoogleCalendar(t, googleCalendar, calId); } }
     * 
     * private String getCalendarIdByName(String calendarName, GoogleCalConn
     * googleCalendar) throws IOException { List<CalendarListEntry> calendars =
     * googleCalendar.getCalendars() .getItems(); String id = ""; for
     * (CalendarListEntry c : calendars) { if
     * (c.getSummary().equals(calendarName)) { id = c.getId(); break; } } return
     * id; }
     * 
     * private String getTaskListIdByName(String taskListName, GoogleTaskConn
     * googleTasks) throws IOException { List<TaskList> taskLists =
     * googleTasks.getTaskLists().getItems(); String id = ""; for (TaskList t :
     * taskLists) { if (t.getTitle().equals(taskListName)) { id = t.getId();
     * break; } } return id; }
     * 
     * private void deleteFromGoogle(List<String> deletedIds, GoogleCalConn
     * googleCalendar, GoogleTaskConn googleTasks, String calId, String
     * taskListId) { Iterator<String> iter = deletedIds.iterator(); while
     * (iter.hasNext()) { String id = iter.next(); try { if
     * (id.startsWith(PREFIX_GOOGLEID_CALENDAR)) {
     * googleCalendar.deleteEvent(calId, id.substring(OFFSET_GOOGLEID_PREFIX));
     * iter.remove();
     * 
     * } else if (id.startsWith(PREFIX_GOOGLEID_TASKS)) {
     * googleTasks.deleteTask(taskListId, id.substring(OFFSET_GOOGLEID_PREFIX));
     * iter.remove(); } } catch (IOException e) {
     * 
     * } } }
     * 
     * private void createMapsForSync(List<Task> taskList, Map<String, Task>
     * calendarTask, Map<String, Task> tasksTask) { for (Task t : taskList) { if
     * ((t.getGoogleId() != null) && (!t.getGoogleId().isEmpty())) { if
     * (t.getGoogleId().startsWith(PREFIX_GOOGLEID_CALENDAR)) {
     * calendarTask.put(t.getGoogleId(), t); } else {
     * tasksTask.put(t.getGoogleId(), t); } } } }
     */
}
