package TeamCity.UI.Tabs;

import TeamCity.Helpers.PropertyRepository;
import TeamCity.Models.HelixDeploySettings;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.web.openapi.BuildTab;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class DeployTab extends BuildTab {
    private final PluginDescriptor _descriptor;
    private final ServletContext _context;
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployTab.class.getName());

    public DeployTab(@NotNull WebControllerManager webControllerManager,
                     @NotNull BuildsManager buildsManager,
                     @NotNull PluginDescriptor descriptor,
                     @NotNull ServletContext context) {
        super("deploy",
                "Deploy",
                webControllerManager,
                buildsManager,
                descriptor.getPluginResourcesPath("Views/DeployTabPage.jsp"));
        _descriptor = descriptor;
        _context = context;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> map, @NotNull SBuild sBuild) {

        String projectName = sBuild.getBuildTypeName().replace("IDCo.Client.", "");
        projectName = projectName.substring(0, projectName.indexOf(':'));
        String phase = sBuild.getBuildTypeName();
        phase = phase.substring(phase.indexOf(':') + 1, phase.indexOf('-'));
        Boolean isSuccessful = sBuild.getBuildStatus().isSuccessful();
        PropertyRepository repository = new PropertyRepository(_context, _descriptor.getPluginResourcesPath("settings.properties"));
        HelixDeploySettings settings = new HelixDeploySettings();
        try {
            settings.setEnvironments(Arrays.asList(repository.getProperties().getProperty("environments", "Dev,Test,Show,Production").split(",")));
        } catch (IOException e) {
            e.printStackTrace();
            Log.error(e.getMessage(), e);
        }
        map.put("environments", settings.getEnvironments());
        map.put("status", isSuccessful ? "" : "disabled=''");
        map.put("buildId", sBuild.getBuildId());
        map.put("projectName", projectName);
        map.put("phase", phase);
    }
}