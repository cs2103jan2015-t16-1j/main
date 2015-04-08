package quicklyst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class QLStorage {
    
	private static final String ERROR_WRITE_FILE = "Error writing file %s";
    private static final String ERROR_READ_FILE = "Error reading file %s";
    private static final String ERROR_UNABLE_READ_FILE = "Unable to read file %s";
    private static final String ERROR_UNABLE_WRITE_FILE = "Unable to write file %s";
    private static final String ERROR_DIRECTORY = "%s is a directory";
    private static final String ERROR_UNABLE_MAKE_DIRECTORY = "unable to make directory %s";
	private static final String ERROR_DIRECTORY_FILE = "%s is a file";
    
    private final static Logger LOGGER = Logger.getLogger(QLStorage.class.getName());
    
    private static QLStorage instance;
    
    private QLStorage() {
    }
    
    public static QLStorage getInstance() {
    	if (instance == null) {
    		instance = new QLStorage(); 
    	}
    	return instance;
    }
    
    
    private class TasksWrapper {
        private ArrayList<Task> tasks;
        public TasksWrapper(List<Task> t) {
            tasks = new ArrayList<Task>(t);
        }
    }
    
    public <T extends List<Task>> T loadFile(T taskList, String filePath) {
        assert taskList != null;
        assert taskList.isEmpty();
        assert filePath != null;
        
        if (!hasFile(filePath)) {
            LOGGER.info(String.format("%s does not exist", filePath));
            return taskList;
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
        
        try (FileReader f = new FileReader(filePath))
        {
            LOGGER.info(String.format("Decoding %s", filePath));
            Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Task.class, new TaskDeserializer())
                            .create();
            
            TasksWrapper wrapper = gson.fromJson(f, TasksWrapper.class);
            
            LOGGER.info("Adding loaded tasks into taskList");
            taskList.addAll(wrapper.tasks);
            
            return taskList;
        } catch (FileNotFoundException e) {
            LOGGER.severe("FileNotFoundException was thrown");
            throw new Error(String.format(ERROR_READ_FILE, filePath));
        } catch (IOException e) {
            LOGGER.severe("IOException was thrown");
            throw new Error(String.format(ERROR_READ_FILE, filePath));
        }
    }
    
    public void saveFile(List<Task> taskList, String filePath) {
        assert taskList != null;
        assert filePath != null;
        
        if ((hasFile(filePath)) && (!isWritable(filePath))) {
        	LOGGER.warning(String.format("%s cannot be writen", filePath));
        	throw new Error(ERROR_UNABLE_WRITE_FILE); 
        }
        
        createNecessaryDirectories(filePath);
        
        LOGGER.info(String.format("Writing %s", filePath));
        
        try (FileWriter f = new FileWriter(filePath))
        {
            TasksWrapper wrapper = new TasksWrapper(taskList);
            
            Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .create();
            
            LOGGER.info("Encoding taskList");
            gson.toJson(wrapper, f);
        } catch (IOException e) {
            LOGGER.severe("IOException was thrown");
            throw new Error(String.format(ERROR_WRITE_FILE, filePath));
        }
    }
    
    public void createNecessaryDirectories(String filePath) {
    	int pathSeperatorIndex = 0;
    	while (filePath.indexOf(File.separator, pathSeperatorIndex) != -1) {
    		pathSeperatorIndex = filePath.indexOf(File.separator, pathSeperatorIndex) +1;
    		File directory = new File(filePath.substring(0, pathSeperatorIndex));
    		
    		if (directory.exists()) {
				if (!directory.isDirectory()) {
					LOGGER.warning(String.format("%s is a file", directory.getPath()));
					throw new Error(String.format(ERROR_DIRECTORY_FILE, directory.getPath()));
				}
    		} else {
    			LOGGER.info(String.format("creating %s", directory.getPath()));
    			if (!directory.mkdir()) {
    				throw new Error(String.format(ERROR_UNABLE_MAKE_DIRECTORY, directory.getPath()));
    			}
    			LOGGER.info(String.format("directory %s created", directory.getPath()));
    		}
    	}
    }
    
    public boolean isValidFile(String filePath) {
    	if (!hasFile(filePath)) {
    		return true;
    	}
    	if ((hasFile(filePath)) && (!isDirectory(filePath)) && (isReadable(filePath)) && (isWritable(filePath))) {
    		return true;
    	}
        return false;
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
    
    private class TaskDeserializer implements JsonDeserializer<Task>
    {
        public Task deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Gson g = new Gson();
            Task t = g.fromJson(json, Task.class);
            JsonObject taskObj = json.getAsJsonObject();
            if (taskObj.get("_name").isJsonNull()) {
                t.setName("(No Title)");
            }
            return t;
        }
    }
}
