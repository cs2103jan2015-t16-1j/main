package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GUITests.class, LogicComponentTests.class, StorageTest.class,
        SettingsTest.class, GoogleIntegrationTest.class })
public class QuicklystTests {    
}
