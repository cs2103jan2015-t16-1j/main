package quicklyst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

//@author A01112707N
public class Storage {

    private static final int INDEX_OFFSET_FOUND = 1;
    private static final int INDEX_NOT_FOUND = -1;
    private static final int INDEX_PATH_START = 0;

    private static final String LOG_READING_FROM_FILE = "Reading %s";
    private static final String LOG_EXCEPTION = "%s was thrown";
    private static final String LOG_CREATED_DIRECTORY = "Directory %s created";
    private static final String LOG_CREATE_DIRECTORY = "Creating %s";
    private static final String LOG_INVALID_DIRECTORY = "%s is a file";
    private static final String LOG_ENCODING_TASKLIST = "Encoding taskList into file";
    private static final String LOG_ADDING_TASKS = "Adding loaded tasks into taskList";
    private static final String LOG_DECODING_FILE = "Decoding taskList from file";
    private static final String LOG_WRITING_TO_FILE = "Writing %s";
    private static final String LOG_UNABLE_WRITE_FILE = "%s cannot be writen";
    private static final String LOG_UNABLE_READ_FILE = "%s cannot be read";
    private static final String LOG_DIRECTORY_FILEPATH = "%s points to a directory";
    private static final String LOG_NO_SUCH_FILE = "%s does not exist";
    private static final String LOG_INVALID_FILEPATH = "%s is invalid";

    private static final String JSON_OBJECT_DELETEDIDS = "deletedIds";
    private static final String JSON_OBJECT_TASKS = "tasks";
    private static final String JSON_OBJECT_TASKNAME = "_name";

    private static final String DEFAULT_TASK_NAME = "(No Title)";

    private static final String REGEX_WINDOWS_INVALID_CHAR = ".*[<>\"].*";

    private final static Logger LOGGER = Logger.getLogger(Storage.class
            .getName());

    private class TasksWrapper {
        private ArrayList<Task> tasks;
        private ArrayList<String> deletedIds;

        public TasksWrapper(List<Task> t, List<String> d) {
            tasks = new ArrayList<Task>(t);
            deletedIds = new ArrayList<String>(d);
        }
    }

    private class TaskWrapperDeserializer implements
            JsonDeserializer<TasksWrapper> {

        @Override
        public TasksWrapper deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {

            JsonObject taskObj = json.getAsJsonObject();
            Type stringListType = new TypeToken<List<String>>() {
            }.getType();
            Type taskListType = new TypeToken<List<Task>>() {
            }.getType();
            Gson g = new GsonBuilder()
                    .registerTypeAdapter(stringListType,
                            new StringListDeserializer())
                    .registerTypeAdapter(taskListType,
                            new TaskListDeserializer()).create();

            List<Task> tasks;
            List<String> deletedIds;

            if ((taskObj.has(JSON_OBJECT_TASKS))
                    && (!taskObj.get(JSON_OBJECT_TASKS).isJsonNull())) {
                tasks = g
                        .fromJson(taskObj.get(JSON_OBJECT_TASKS), taskListType);
            } else {
                tasks = new ArrayList<Task>();
            }
            if ((taskObj.has(JSON_OBJECT_DELETEDIDS))
                    && (!taskObj.get(JSON_OBJECT_DELETEDIDS).isJsonNull())) {
                deletedIds = g.fromJson(taskObj.get(JSON_OBJECT_DELETEDIDS),
                        stringListType);
            } else {
                deletedIds = new ArrayList<String>();
            }

            return new TasksWrapper(tasks, deletedIds);
        }
    }

    private class StringListDeserializer implements
            JsonDeserializer<List<String>> {
        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {

            List<String> list = new ArrayList<String>();
            Gson g = new Gson();

            for (JsonElement e : json.getAsJsonArray()) {
                if (!e.isJsonNull()) {
                    list.add(g.fromJson(e, String.class));
                }
            }
            return list;
        }
    }

    private class TaskListDeserializer implements JsonDeserializer<List<Task>> {
        @Override
        public List<Task> deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {

            List<Task> list = new ArrayList<Task>();
            Gson g = new GsonBuilder().registerTypeAdapter(Task.class,
                    new TaskDeserializer()).create();

            for (JsonElement e : json.getAsJsonArray()) {
                if (!e.isJsonNull()) {
                    list.add(g.fromJson(e, Task.class));
                }
            }
            return list;
        }
    }

    private class TaskDeserializer implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {

            Gson g = new Gson();
            Task t = g.fromJson(json, Task.class);
            JsonObject taskObj = json.getAsJsonObject();
            if (!taskObj.has(JSON_OBJECT_TASKNAME)
                    || taskObj.get(JSON_OBJECT_TASKNAME).isJsonNull()) {
                t.setName(DEFAULT_TASK_NAME);
            }
            if (t.getLastUpdated() == null) {
                t.setLastUpdated(Calendar.getInstance());
            }
            return t;
        }
    }

    private static Storage _instance;

    private Storage() {
    }

    public static Storage getInstance() {
        if (_instance == null) {
            _instance = new Storage();
        }
        return _instance;
    }

    public boolean isValidFile(String filePath) {
        assert filePath != null;

        if (!isValidFilepath(filePath)) {
            return false;
        }

        if (!hasFile(filePath)) {
            return true;
        }
        if ((hasFile(filePath)) && (!isDirectory(filePath))
                && (isReadable(filePath)) && (isWritable(filePath))) {
            return true;
        }
        return false;
    }

    public void loadFile(List<Task> taskList, List<String> deletedIds,
            String filePath) {
        assert taskList != null;
        assert taskList.isEmpty();
        assert taskList != null;
        assert deletedIds.isEmpty();
        assert filePath != null;

        if (!isValidFilepath(filePath)) {
            LOGGER.warning(String.format(LOG_INVALID_FILEPATH, filePath));
            throw new Error(GlobalConstants.ERROR_INVALID_FILEPATH);
        }

        if (!hasFile(filePath)) {
            LOGGER.info(String.format(LOG_NO_SUCH_FILE, filePath));
            return;
        }

        if (isDirectory(filePath)) {
            LOGGER.warning(String.format(LOG_DIRECTORY_FILEPATH, filePath));
            throw new Error(GlobalConstants.ERROR_DIRECTORY);
        }

        if (!isReadable(filePath)) {
            LOGGER.warning(String.format(LOG_UNABLE_READ_FILE, filePath));
            throw new Error(GlobalConstants.ERROR_UNABLE_READ_FILE);
        }

        LOGGER.info(String.format(LOG_READING_FROM_FILE, filePath));
        readListFromFile(taskList, deletedIds, filePath);

    }

    public void saveFile(List<Task> taskList, List<String> deletedIds,
            String filePath) {
        assert taskList != null;
        assert deletedIds != null;
        assert filePath != null;

        if (!isValidFilepath(filePath)) {
            LOGGER.warning(String.format(LOG_INVALID_FILEPATH, filePath));
            throw new Error(GlobalConstants.ERROR_INVALID_FILEPATH);
        }

        if ((hasFile(filePath)) && (!isWritable(filePath))) {
            LOGGER.warning(String.format(LOG_UNABLE_WRITE_FILE, filePath));
            throw new Error(GlobalConstants.ERROR_UNABLE_WRITE_FILE);
        }

        createNecessaryDirectories(filePath);

        LOGGER.info(String.format(LOG_WRITING_TO_FILE, filePath));

        writeListToFile(taskList, deletedIds, filePath);
    }

    private void readListFromFile(List<Task> taskList, List<String> deletedIds,
            String filePath) {
        try (FileReader f = new FileReader(filePath)) {

            Gson gson = new GsonBuilder().registerTypeAdapter(
                    TasksWrapper.class, new TaskWrapperDeserializer()).create();

            LOGGER.info(String.format(LOG_DECODING_FILE, filePath));
            TasksWrapper wrapper = gson.fromJson(f, TasksWrapper.class);

            LOGGER.info(LOG_ADDING_TASKS);
            if (wrapper != null) {
                if (wrapper.tasks != null) {
                    taskList.addAll(wrapper.tasks);
                }
                if (wrapper.deletedIds != null) {
                    deletedIds.addAll(wrapper.deletedIds);
                }
            }

        } catch (FileNotFoundException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(String.format(GlobalConstants.ERROR_READ_FILE,
                    filePath));
        } catch (IOException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(String.format(GlobalConstants.ERROR_READ_FILE,
                    filePath));
        } catch (JsonSyntaxException | JsonIOException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(String.format(GlobalConstants.ERROR_READ_FILE,
                    filePath));
        }
    }

    private void writeListToFile(List<Task> taskList, List<String> deletedIds,
            String filePath) {
        try (FileWriter f = new FileWriter(filePath)) {
            TasksWrapper wrapper = new TasksWrapper(taskList, deletedIds);

            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting()
                    .create();

            LOGGER.info(LOG_ENCODING_TASKLIST);
            gson.toJson(wrapper, f);
        } catch (IOException e) {
            LOGGER.severe(String.format(LOG_EXCEPTION, e.getClass().getName()));
            throw new Error(String.format(GlobalConstants.ERROR_WRITE_FILE,
                    filePath));
        }
    }

    private void createNecessaryDirectories(String filePath) {
        int pathSeperatorIndex = INDEX_PATH_START;
        while (filePath.indexOf(File.separator, pathSeperatorIndex) != INDEX_NOT_FOUND) {
            pathSeperatorIndex = filePath.indexOf(File.separator,
                    pathSeperatorIndex) + INDEX_OFFSET_FOUND;
            File directory = new File(filePath.substring(INDEX_PATH_START,
                    pathSeperatorIndex));

            if (directory.exists()) {
                if (!directory.isDirectory()) {
                    LOGGER.warning(String.format(LOG_INVALID_DIRECTORY,
                            directory.getPath()));
                    throw new Error(String.format(
                            GlobalConstants.ERROR_DIRECTORY_FILE,
                            directory.getPath()));
                }
            } else {
                LOGGER.info(String.format(LOG_CREATE_DIRECTORY,
                        directory.getPath()));
                if (!directory.mkdir()) {
                    throw new Error(String.format(
                            GlobalConstants.ERROR_UNABLE_MAKE_DIRECTORY,
                            directory.getPath()));
                }
                LOGGER.info(String.format(LOG_CREATED_DIRECTORY,
                        directory.getPath()));
            }
        }
    }

    private boolean hasFile(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private boolean isDirectory(String filePath) {
        File file = new File(filePath);
        return file.isDirectory();
    }

    private boolean isReadable(String filePath) {
        File file = new File(filePath);
        return file.canRead();
    }

    private boolean isWritable(String filePath) {
        File file = new File(filePath);
        return file.canWrite();
    }

    private boolean isValidFilepath(String filePath) {
        if (filePath.endsWith(File.separator)) {
            return false;
        }
        try {
            File f = new File(filePath);
            f.getCanonicalPath();
            if (filePath.matches(REGEX_WINDOWS_INVALID_CHAR)) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
