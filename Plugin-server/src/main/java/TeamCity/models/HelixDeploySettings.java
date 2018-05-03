package TeamCity.models;

import lombok.Data;

import java.util.Collection;

@Data
public class HelixDeploySettings {
    public String powerShellScriptPath;
    public Collection<String> environments;
}
