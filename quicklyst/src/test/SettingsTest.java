package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.MessageConstants;
import quicklyst.Settings;

//@author A01112707N
public class SettingsTest {
    
    private Settings _settings;
    private String _filePath;

    @Before
    public void setUp() throws Exception {
        _filePath = "testingSettings.json";
        _settings = Settings.getTestInstance(_filePath);
    }

    @After
    public void tearDown() throws Exception {
        _settings.testCleanUp();
        File file = new File(_filePath);
        file.delete();
    }
    
    @Test
    public void testGetDefault() {
        _settings.updatePrefFilePath("savefile.json");
        assertEquals("savefile.json", _settings.getPrefFilePath());
        assertEquals("save.json", _settings.getDefaultFilePath());
        _settings.updatePrefFilePath("tasks.json");
        assertEquals("tasks.json", _settings.getPrefFilePath());
        assertEquals("save.json", _settings.getDefaultFilePath());
    }

    @Test
    public void testSetPref() {
        _settings.updatePrefFilePath("savefile.json");
        assertEquals("savefile.json", _settings.getPrefFilePath());
        _settings.updatePrefFilePath("tasks.json");
        assertEquals("tasks.json", _settings.getPrefFilePath());
    }
    
    @Test
    public void testInvalidFile() throws IOException {
        try (FileWriter fw = new FileWriter(_filePath)) {
            fw.write("::::");
        }
        Error caught = null;
        try {
            _settings.getPrefFilePath();
        } catch (Error e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(MessageConstants.ERROR_READ_SETTINGS, caught.getMessage());
    }
    
        

}
