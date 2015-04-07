package quicklyst;

public class QLSettings {
    
    public static QLSettings instance;
    
    public static QLSettings getInstance() {
        if (instance == null) {
            instance = new QLSettings();
        }
        return instance;
    }
    
    private QLSettings() {
    }
    
    public String getPrefFilePath() {
        return "save.json";
    }
    
    public String getDefaultFilePath() {
        return "save.json";
    }
    
    public void updatePrefFilePath(String filePath) {
    }
}
