package quicklyst;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class QLSettings {
	
	private static final String FILEPATH_SETTINGS = "settings.json";
	private static final String FILEPATH_DEFAULT_SAVE = "save.json";
	
    public static QLSettings _instance;
    
    private String _prefFilePath;
    
    public static QLSettings getInstance() {
        if (_instance == null) {
            _instance = new QLSettings();
        }
        return _instance;
    }
    
    private QLSettings() {
    	try (FileReader f = new FileReader(FILEPATH_SETTINGS)) {
    		Gson gson = new Gson();
    		_prefFilePath = gson.fromJson(f, String.class);
    	} catch (FileNotFoundException e) {
			_prefFilePath = FILEPATH_DEFAULT_SAVE;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public String getPrefFilePath() {
    	return _prefFilePath;
    }
    
    public String getDefaultFilePath() {
        return FILEPATH_DEFAULT_SAVE;
    }
    
    public void updatePrefFilePath(String filePath) {
    	_prefFilePath = filePath;
    	saveSettingsToFile();
    }
    
    private void saveSettingsToFile() {
    	try (FileWriter f = new FileWriter(FILEPATH_SETTINGS)) {
    		Gson gson = new Gson();
    		gson.toJson(_prefFilePath, f);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}