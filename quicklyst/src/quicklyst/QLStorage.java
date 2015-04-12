package quicklyst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
public class QLStorage {
	
	private static final String ERROR_INVALID_FILEPATH = "Invalid filepath";

    private class TasksWrapper {
		private ArrayList<Task> tasks;
		private ArrayList<String> deletedIDs;

		public TasksWrapper(List<Task> t, List<String> d) {
			tasks = new ArrayList<Task>(t);
			deletedIDs = new ArrayList<String>(d);
		}
	}
    
    private class TaskWrapperDeserializer implements JsonDeserializer<TasksWrapper> {
        @Override
        public TasksWrapper deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            
            System.out.println("taskwrapper deserialize");
            
            JsonObject taskObj = json.getAsJsonObject();
            Type stringListType = new TypeToken<List<String>>(){}.getType();
            Type taskListType = new TypeToken<List<Task>>(){}.getType();
            Gson g = new GsonBuilder()
                         .registerTypeAdapter(stringListType, new StringListDeserializer())
                         .registerTypeAdapter(taskListType, new TaskListDeserializer())
                         .create();
            
            List<Task> tasks;
            List<String> deletedIDs;
            
            if ((taskObj.has("tasks")) && (!taskObj.get("tasks").isJsonNull())) {
                tasks = g.fromJson(taskObj.get("tasks"), taskListType);
            } else {
                tasks = new ArrayList<Task>();
            }
            if ((taskObj.has("deletedIDs")) && (!taskObj.get("deletedIDs").isJsonNull())) {
                deletedIDs = g.fromJson(taskObj.get("deletedIDs"), stringListType);
            } else {
                deletedIDs = new ArrayList<String>();
            }

            return new TasksWrapper(tasks, deletedIDs);
        }
    }
    
    private class StringListDeserializer implements JsonDeserializer<List<String>> {
        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            
            System.out.println("string list deserialize");
            
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
            
            System.out.println("task list deserialize");
            
            List<Task> list = new ArrayList<Task>();
            Gson g = new GsonBuilder()
                         .registerTypeAdapter(Task.class, new TaskDeserializer())
                         .create();
            
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
	        
	        System.out.println("task deserialize");
	        
			Gson g = new Gson();
			Task t = g.fromJson(json, Task.class);
			JsonObject taskObj = json.getAsJsonObject();
			if (!taskObj.has("_name") || taskObj.get("_name").isJsonNull()) {
				t.setName("(No Title)");
			}
			if (t.getLastUpdated() == null) {
			    t.setLastUpdated(Calendar.getInstance());
			}
			return t;
		}
	}

	private static final String ERROR_WRITE_FILE = "Error writing file %s";
	private static final String ERROR_READ_FILE = "Error reading file %s";
	private static final String ERROR_UNABLE_READ_FILE = "Unable to read file %s";
	private static final String ERROR_UNABLE_WRITE_FILE = "Unable to write file %s";
	private static final String ERROR_DIRECTORY = "%s is a directory";
	private static final String ERROR_UNABLE_MAKE_DIRECTORY = "unable to make directory %s";
	private static final String ERROR_DIRECTORY_FILE = "%s is a file";
	
	private static final String REGEX_INVALID_CHAR = "^.*[" + Pattern.quote(":*?/\"<>|") + "].*$";
	private static final String REGEX_DRIVE = "^[A-Z]:$";

	private final static Logger LOGGER = Logger.getLogger(QLStorage.class
			.getName());

	private static QLStorage _instance;

	private QLStorage() {
	}

	public static QLStorage getInstance() {
		if (_instance == null) {
			_instance = new QLStorage();
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

	public void loadFile(List<Task> taskList, List<String> deletedIDs, String filePath) {
		assert taskList != null;
		assert taskList.isEmpty();
		assert taskList != null;
        assert deletedIDs.isEmpty();
		assert filePath != null;
		
		if (!isValidFilepath(filePath)) {
            LOGGER.warning(String.format("%s is invalid", filePath));
            throw new Error(ERROR_INVALID_FILEPATH);
        }

		if (!hasFile(filePath)) {
			LOGGER.info(String.format("%s does not exist", filePath));
			return;
		}

		if (isDirectory(filePath)) {
			LOGGER.warning(String.format("%s points to a directory", filePath));
			throw new Error(ERROR_DIRECTORY);
		}

		if (!isReadable(filePath)) {
			LOGGER.warning(String.format("%s cannot be read", filePath));
			throw new Error(ERROR_UNABLE_READ_FILE);
		}

		LOGGER.info(String.format("Reading %s", filePath));
		readListFromFile(taskList, deletedIDs, filePath);

	}

	public void saveFile(List<Task> taskList, List<String> deletedIDs, String filePath) {
		assert taskList != null;
		assert deletedIDs != null;
		assert filePath != null;
		
		if (!isValidFilepath(filePath)) {
		    LOGGER.warning(String.format("%s is invalid", filePath));
		    throw new Error(ERROR_INVALID_FILEPATH);
		}

		if ((hasFile(filePath)) && (!isWritable(filePath))) {
			LOGGER.warning(String.format("%s cannot be writen", filePath));
			throw new Error(ERROR_UNABLE_WRITE_FILE);
		}

		createNecessaryDirectories(filePath);

		LOGGER.info(String.format("Writing %s", filePath));

		writeListToFile(taskList, deletedIDs, filePath);
	}

	private void readListFromFile(List<Task> taskList, List<String> deletedIDs,
			String filePath) {
		try (FileReader f = new FileReader(filePath)) {
		    
			Gson gson = new GsonBuilder()
			                .registerTypeAdapter(TasksWrapper.class, new TaskWrapperDeserializer())
			                .create();

			LOGGER.info(String.format("Decoding taskList from file", filePath));
			TasksWrapper wrapper = gson.fromJson(f, TasksWrapper.class);

			LOGGER.info("Adding loaded tasks into taskList");
			if (wrapper != null) {
			    if (wrapper.tasks != null) {
			        taskList.addAll(wrapper.tasks);
			    }
			    if (wrapper.deletedIDs != null) {
			        deletedIDs.addAll(wrapper.deletedIDs);
			    }
			}

		} catch (FileNotFoundException e) {
			LOGGER.severe("FileNotFoundException was thrown");
			throw new Error(String.format(ERROR_READ_FILE, filePath));
		} catch (IOException e) {
			LOGGER.severe("IOException was thrown");
			throw new Error(String.format(ERROR_READ_FILE, filePath));
		} catch (JsonSyntaxException | JsonIOException e) {
		    LOGGER.severe("JsonException was thrown");
            throw new Error(String.format(ERROR_READ_FILE, filePath));
		}
	}

	private void writeListToFile(List<Task> taskList, List<String> deletedIDs, String filePath) {
		try (FileWriter f = new FileWriter(filePath)) {
			TasksWrapper wrapper = new TasksWrapper(taskList, deletedIDs);

			Gson gson = new GsonBuilder()
			                .serializeNulls()
			                .setPrettyPrinting()
			                .create();
			
			LOGGER.info("Encoding taskList into file");
			gson.toJson(wrapper, f);
		} catch (IOException e) {
			LOGGER.severe("IOException was thrown");
			throw new Error(String.format(ERROR_WRITE_FILE, filePath));
		}
	}

	private void createNecessaryDirectories(String filePath) {
		int pathSeperatorIndex = 0;
		while (filePath.indexOf(File.separator, pathSeperatorIndex) != -1) {
			pathSeperatorIndex = filePath.indexOf(File.separator,
					pathSeperatorIndex) + 1;
			File directory = new File(filePath.substring(0, pathSeperatorIndex));

			if (directory.exists()) {
				if (!directory.isDirectory()) {
					LOGGER.warning(String.format("%s is a file",
							directory.getPath()));
					throw new Error(String.format(ERROR_DIRECTORY_FILE,
							directory.getPath()));
				}
			} else {
				LOGGER.info(String.format("creating %s", directory.getPath()));
				if (!directory.mkdir()) {
					throw new Error(String.format(ERROR_UNABLE_MAKE_DIRECTORY,
							directory.getPath()));
				}
				LOGGER.info(String.format("directory %s created",
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
	    //windows specific
	    if (filePath.isEmpty()) {
	        return false;
	    }
	    if (filePath.length() > 2) {
	        if (filePath.substring(2).matches(REGEX_INVALID_CHAR)) {
	            return false;
	        } else if (filePath.substring(0, 2).matches(REGEX_DRIVE)) {
	            return true;
	        } else if (filePath.substring(0, 2).matches(REGEX_INVALID_CHAR)) {
	            return false;
	        } else {
	            return true;
	        }
	    } else if (filePath.matches(REGEX_INVALID_CHAR)) {
	        return false;
	    }
	    return true;
	}
}
