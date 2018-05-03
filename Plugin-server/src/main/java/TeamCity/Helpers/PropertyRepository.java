package TeamCity.Helpers;

/**
 * Created by Mykhailo_Moskura on 4/11/2018.
 */

import jetbrains.buildServer.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


//("/buildServerResources/setting.properties")
public class PropertyRepository {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PropertyRepository.class.getName());
    private String _fullPath;

    public PropertyRepository(@NotNull ServletContext context, String propertiesPath) {
        _fullPath = context.getRealPath(propertiesPath);
    }

    public Properties getProperties() throws IOException {
        return PropertiesUtil.loadProperties(new File(_fullPath));
    }

    public Boolean updateProperties(Properties properties) throws IOException {
        return PropertiesUtil.updateProperties(new File(_fullPath), properties, "Properties were updated.");
    }

    public void saveProperties(Properties properties) throws IOException {
        PropertiesUtil.storeProperties(properties, new File(_fullPath), "Properties were saved.");
    }
}

