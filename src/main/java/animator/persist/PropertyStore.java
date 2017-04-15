package animator.persist;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class PropertyStore {

    private static final String USER_HOME = "user.home";
    private static final String FILE_NAME = "fx-animation-editor.properties";
    private static final File PROPERTIES_FILE = getPropertiesFile();
    private static final Properties PROPERTIES = loadProperties();

    public String getProperty(PropertyKey key) {
        return PROPERTIES.getProperty(key.getKey());
    }

    public void setProperty(PropertyKey key, String value) {
        if (value != null) {
            PROPERTIES.setProperty(key.getKey(), value);
        } else {
            PROPERTIES.remove(key.getKey());
        }
        CompletableFuture.runAsync(this::save);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        if (PROPERTIES_FILE != null) {
            try {
                if (PROPERTIES_FILE.exists() || PROPERTIES_FILE.createNewFile()) {
                    try (FileReader fileReader = new FileReader(PROPERTIES_FILE)) {
                        properties.load(fileReader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private static File getPropertiesFile() {
        String userHome = System.getProperty(USER_HOME);
        return userHome != null ? new File(userHome + File.separator + FILE_NAME) : null;
    }

    private void save() {
        if (PROPERTIES_FILE != null) {
            try (FileWriter fileWriter = new FileWriter(PROPERTIES_FILE)) {
                PROPERTIES.store(fileWriter, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
