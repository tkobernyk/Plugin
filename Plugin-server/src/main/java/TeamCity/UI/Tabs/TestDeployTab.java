package TeamCity.UI.Tabs;

import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;


public class TestDeployTab extends DeployTab {

    private static final String ENVIRONMENT = "TEST";
    private static final String TITLE = "Test";

    public TestDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        super(webControllerManager, buildsManager, descriptor, ENVIRONMENT, TITLE);
    }
}
