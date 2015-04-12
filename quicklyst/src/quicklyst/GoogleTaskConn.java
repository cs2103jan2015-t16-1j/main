package quicklyst;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;

//@author A01112707N
public class GoogleTaskConn {

    private static final String APPLICATION_NAME = "Quicklyst";
    private static final JsonFactory JSON_FACTORY = GsonFactory
            .getDefaultInstance();

    private Credential _credential;
    private HttpTransport _httpTransport;

    private com.google.api.services.tasks.Tasks getService() {

        return new com.google.api.services.tasks.Tasks.Builder(_httpTransport,
                JSON_FACTORY, _credential).setApplicationName(APPLICATION_NAME)
                .build();
    }

    public GoogleTaskConn(Credential credential, HttpTransport httpTransport) {
        _credential = credential;
        _httpTransport = httpTransport;
    }

    public TaskLists getTaskLists() throws IOException {
        return getService().tasklists().list().execute();
    }
    
    public Tasks getTasks(String taskListId) throws IOException {
        return getService().tasks().list(taskListId).execute();
    }

    public TaskList getTaskList(String taskListId) throws IOException {
        return getService().tasklists().get(taskListId).execute();
    }

    public Task getTask(String taskListId, String taskId) throws IOException {
        return getService().tasks().get(taskListId, taskId).execute();
    }

    public TaskList createTaskList(TaskList taskList) throws IOException {
        return getService().tasklists().insert(taskList).execute();
    }

    public Task createTask(String taskListId, Task task) throws IOException {
        return getService().tasks().insert(taskListId, task).execute();
    }

    public TaskList updateTaskList(String taskListId, TaskList newTaskList)
            throws IOException {
        return getService().tasklists().update(taskListId, newTaskList)
                .execute();
    }

    public Task updateTask(String taskListId, String taskId, Task newTask)
            throws IOException {
        return getService().tasks().update(taskListId, taskId, newTask)
                .execute();
    }

    public void deleteTaskList(String taskListId) throws IOException {
        getService().tasklists().delete(taskListId).execute();
    }

    public void deleteTask(String taskListId, String taskId) throws IOException {
        getService().tasks().delete(taskListId, taskId).execute();
    }

}
