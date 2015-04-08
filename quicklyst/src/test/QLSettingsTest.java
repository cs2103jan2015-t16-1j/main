package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import quicklyst.QLSettings;

public class QLSettingsTest {
    
    QLSettings _settings = QLSettings.getInstance();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetPref() {
        _settings.updatePrefFilePath("savefile.json");
        assertEquals("savefile.json", _settings.getPrefFilePath());
        _settings.updatePrefFilePath("tasks.json");
        assertEquals("tasks.json", _settings.getPrefFilePath());
    }

}
