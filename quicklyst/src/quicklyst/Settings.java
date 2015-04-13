package quicklyst;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

//@author A01112707N
public class Settings {
	
    public static Settings _instance;
    
    private String _settingsPath;
    private String _prefFilePath;
    private boolean isLoaded;
    
    public static void testCleanUp() {
        _instance = null;
    }
    
    public static Settings getTestInstance(String filePath) {
        Settings instance = new Settings();
        instance._settingsPath = filePath;
        return instance;
    }
    
    public static Settings getInstance() {
        if (_instance == null) {
            _instance = new Settings();
        }
        return _instance;
    }
    
    private Settings() {
        _settingsPath = MessageConstants.FILEPATH_SETTINGS;
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
        return MessageConstants.FILEPATH_DEFAULT_SAVE;
    }
    
    public void updatePrefFilePath(String filePath) {
    	_prefFilePath = filePath;
    	save();
    }
    
    private void load() {
        try (FileReader f = new FileReader(_settingsPath)) {
            Gson gson = new Gson();
            _prefFilePath = gson.fromJson(f, String.class);
        } catch (FileNotFoundException e) {
            _prefFilePath = null;
        } catch (IOException e) {
            throw new Error(MessageConstants.ERROR_READ_SETTINGS);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new Error(MessageConstants.ERROR_READ_SETTINGS);
        }
    }
    
    private void save() {
    	try (FileWriter f = new FileWriter(_settingsPath)) {
    		Gson gson = new Gson();
    		gson.toJson(_prefFilePath, f);
    	} catch (IOException | JsonIOException e) {
			throw new Error(MessageConstants.ERROR_WRITE_SETTINGS);
		}
    }
}