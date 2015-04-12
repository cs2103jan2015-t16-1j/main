package quicklyst;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

//@author A01112707N
public class QLSettings {
	
    private static final String FILEPATH_SETTINGS = "settings.json";
	private static final String FILEPATH_DEFAULT_SAVE = "save.json";
	
    public static QLSettings _instance;
    
    private String _prefFilePath;
    private boolean isLoaded;
    
    public static QLSettings getInstance() {
        if (_instance == null) {
            _instance = new QLSettings();
        }
        return _instance;
    }
    
    private QLSettings() {
        _prefFilePath = null;
        isLoaded = false;
    }
    
    public String getPrefFilePath() {
        if (!isLoaded) {
            load();
        }
    	return _prefFilePath;
    }
    
    public String getDefaultFilePath() {
        return FILEPATH_DEFAULT_SAVE;
    }
    
    public void updatePrefFilePath(String filePath) {
    	_prefFilePath = filePath;
    	save();
    }
    
    private void load() {
        try (FileReader f = new FileReader(FILEPATH_SETTINGS)) {
            Gson gson = new Gson();
            _prefFilePath = gson.fromJson(f, String.class);
        } catch (FileNotFoundException e) {
            _prefFilePath = null;
        } catch (IOException e) {
            throw new Error(ERROR_READ_SETTINGS);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new Error(ERROR_READ_SETTINGS);
        }
    }
    
    private void save() {
    	try (FileWriter f = new FileWriter(FILEPATH_SETTINGS)) {
    		Gson gson = new Gson();
    		gson.toJson(_prefFilePath, f);
    	} catch (IOException | JsonIOException e) {
			throw new Error(ERROR_WRITE_SETTINGS)
		}
    }
}