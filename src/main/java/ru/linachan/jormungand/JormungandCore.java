package ru.linachan.jormungand;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import ru.linachan.util.Pair;
import ru.linachan.yggdrasil.YggdrasilCore;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.storage.StorageScopes;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JormungandCore {

    private YggdrasilCore core;

    private GoogleCredential credentials;
    private JsonFactory jsonFactory;
    private HttpTransport httpTransport;

    private String bucketName;

    private Storage storage;

    public JormungandCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        String emailAddress = this.core.getConfig("JormungandEMail", "jormungand@developer.gserviceaccount.com");
        String credentialFile = this.core.getConfig("JormungandP12File", "JormungandCredentials.p12");
        String applicationName = this.core.getConfig("JormungandApplication", "JormungandApplication");

        this.bucketName = this.core.getConfig("JormungandBucket", "jormungand");

        try {
            jsonFactory = JacksonFactory.getDefaultInstance();
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.credentials = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKeyFromP12File(new File(credentialFile))
                    .setServiceAccountScopes(Collections.singleton(StorageScopes.DEVSTORAGE_READ_ONLY))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            this.core.logWarning("JormungandCore: Unable to get access token");
            this.core.logException(e);
        }

        if (this.credentials != null) {
            storage = new Storage.Builder(httpTransport, jsonFactory, credentials)
                    .setApplicationName(applicationName)
                    .build();
        }
    }

    public Pair<List<String>, List<StorageObject>> listObjectsByPath(String prefix) {
        List<StorageObject> objectList = null;
        List<String> directoryList = null;

        try {
            Storage.Objects.List listObjects = this.storage.objects().list(bucketName);

            listObjects.setPrefix(prefix);
            listObjects.setDelimiter("/");
            listObjects.setProjection("full");

            objectList = new LinkedList<>();
            directoryList = new LinkedList<>();

            Objects objects;

            do {
                objects = listObjects.execute();

                List<StorageObject> items = objects.getItems();
                List<String> directories = objects.getPrefixes();

                if (null != directories) {
                    directoryList.addAll(directories);
                }
                if (null != items) {
                    objectList.addAll(items);
                }

                listObjects.setPageToken(objects.getNextPageToken());
            } while (null != objects.getNextPageToken());
        } catch (IOException e) {
            this.core.logWarning("JormungandCore: Error: " + e.getMessage());
            this.core.logException(e);
        }

        return new Pair<>(directoryList, objectList);
    }

    public List<StorageObject> listObjects(String prefix) {
        List<StorageObject> objectList = null;

        try {
            Storage.Objects.List listObjects = this.storage.objects().list(bucketName);

            listObjects.setPrefix(prefix);
            listObjects.setProjection("full");

            objectList = new LinkedList<>();

            Objects objects;

            do {
                objects = listObjects.execute();

                List<StorageObject> items = objects.getItems();

                if (null != items) {
                    objectList.addAll(items);
                }

                listObjects.setPageToken(objects.getNextPageToken());
            } while (null != objects.getNextPageToken());
        } catch (IOException e) {
            this.core.logWarning("JormungandCore: Error: " + e.getMessage());
            this.core.logException(e);
        }

        return objectList;
    }

    public StorageObject getObjectByPath(String filePath) {
        StorageObject fileObject = null;

        try {
            Storage.Objects.List listObjects = this.storage.objects().list(bucketName);

            listObjects.setPrefix(filePath);
            listObjects.setDelimiter("/");
            listObjects.setProjection("full");

            Objects objects = listObjects.execute();

            List<StorageObject> items = objects.getItems();

            if ((items != null) && (items.size() > 0)) {
                fileObject = items.get(0);
            }
        } catch (IOException e) {
            this.core.logWarning("JormungandCore: Error: " + e.getMessage());
            this.core.logException(e);
        }

        return fileObject;
    }

    public boolean execute_tests() {
        return true;
    }
}
