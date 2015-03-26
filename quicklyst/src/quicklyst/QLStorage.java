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
    private static final String ERROR_CHECK_FILE = "Error checking file %s";
    private static final String ERROR_UNABLE_READ_FILE = "Unable to read file %s";
    private static final String ERROR_DIRECTORY = "%s is a directory";
    
    private final static Logger LOGGER = Logger.getLogger(QLStorage.class.getName()); 
    
    private static class TasksWrapper {
        private ArrayList<Task> tasks;
        public TasksWrapper(List<Task> t) {
            tasks = new ArrayList<Task>(t);
        }
    }
    
    private static boolean checkFile(String filePath) {
        File file = null;
        file = new File(filePath);
        if (!file.exists()) {
            LOGGER.info(String.format("%s does not exist", filePath));
            return false;
        }
        if (file.isDirectory()) {
            LOGGER.warning(String.format("%s points to a directory", filePath));
            throw new Error(String.format(ERROR_DIRECTORY, filePath));
        } else if (!file.canRead()) {
            LOGGER.warning(String.format("%s cannot be read", filePath));
            throw new Error(String.format(ERROR_UNABLE_READ_FILE, filePath));
        }
        return true;
    }
    
    public static <T extends List<Task>> T loadFile(T taskList, String filePath) {
        assert taskList != null;
        assert taskList.isEmpty();
        assert filePath != null;
        
        boolean doesFileExist = checkFile(filePath);
        
        if (!doesFileExist) {
            return taskList;
        }
        
        LOGGER.info(String.format("Reading %s", filePath));
        
        try (FileReader f = new FileReader(filePath))
        {
            LOGGER.info(String.format("Decoding %s", filePath));
            Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Calendar.class, new CalendarDeserializer())
                            .create();
            
            assert gson != null;
            
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
    
    public static void saveFile(List<Task> taskList, String filePath) {
        assert taskList != null;
        assert filePath != null;
        
        LOGGER.info(String.format("Writing %s", filePath));
        
        try (FileWriter f = new FileWriter(filePath))
        {
            TasksWrapper wrapper = new TasksWrapper(taskList);
            
            Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .create();
            
            assert gson != null;
            
            LOGGER.info("Encoding taskList");
            gson.toJson(wrapper, f);
        } catch (IOException e) {
            LOGGER.severe("IOException was thrown");
            throw new Error(String.format(ERROR_WRITE_FILE, filePath));
        } 
    }
    
    private static class CalendarDeserializer implements JsonDeserializer<Calendar>
    {
        public Calendar deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject = json.getAsJsonObject(); 
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(0);
            c.set(jobject.get("year").getAsInt(), 
                  jobject.get("month").getAsInt(), 
                  jobject.get("dayOfMonth").getAsInt(), 
                  jobject.get("hourOfDay").getAsInt(),
                  jobject.get("minute").getAsInt(), 
                  jobject.get("second").getAsInt());
            c.get(Calendar.YEAR);
            return c;
        }
    }
}
