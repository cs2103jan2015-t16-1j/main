package quicklyst;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;

//@author A01112707N
public class GoogleLogin {
    
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    private static final String CLIENT_ID = 
            "657661987639-mula0sgv7qbs35pe24eq7vg2jj1fqvl8.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "Lk2nEQ38cw0of3vSLIjeY6VT";
    
    private String _user;
    private HttpTransport _httpTransport;
    private DataStoreFactory _dataStoreFactory;

    public GoogleLogin(String user, HttpTransport httpTransport, DataStoreFactory dataStoreFactory) {
        _user = user;
        _httpTransport = httpTransport;
        _dataStoreFactory = dataStoreFactory;
    }

    public Credential getCredential() throws IOException {
        
        Collection<String> scopes = new HashSet<String>();
        scopes.add(CalendarScopes.CALENDAR);
        scopes.add(TasksScopes.TASKS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                _httpTransport, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET,
                scopes).setDataStoreFactory(_dataStoreFactory)
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(_user);
    }
    
    public boolean logout() throws IOException {
        if (StoredCredential.getDefaultDataStore(_dataStoreFactory).containsKey(_user)) {
            StoredCredential.getDefaultDataStore(_dataStoreFactory).delete(_user);
            return true;
        }
        return false;
    }
}
