package TeamCity.UI.Tabs;

import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;


public class ShowDeployTab extends DeployTab {

    private static final String ENVIRONMENT = "SHOW";
    private static final String TITLE = "Show";

    public ShowDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        super(webControllerManager, buildsManager, descriptor, ENVIRONMENT, TITLE);
    }
}
