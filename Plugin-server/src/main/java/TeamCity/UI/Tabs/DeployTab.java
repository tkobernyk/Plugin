package TeamCity.UI.Tabs;

import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.web.openapi.BuildTab;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class DeployTab extends BuildTab {

    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployTab.class.getName());

    protected DeployTab(@NotNull WebControllerManager webControllerManager,
                        @NotNull BuildsManager buildsManager,
                        @NotNull PluginDescriptor descriptor,
                        @NotNull String environment,
                        @NotNull String title) {
        super(environment,
                title,
                webControllerManager,
                buildsManager,
                descriptor.getPluginResourcesPath("Views/DeployTabPage.jsp"));
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> map, @NotNull SBuild sBuild) {
        String projectName = sBuild.getBuildTypeName().replace("IDCo.Client.", "");
        projectName = projectName.substring(0, projectName.indexOf(':'));
        String phase = sBuild.getBuildTypeName();
        phase = phase.substring(phase.indexOf(':') + 1, phase.indexOf('-'));
        Boolean isSuccessful = sBuild.getBuildStatus().isSuccessful();
        Log.info("TABBBBBBBBBBBBBBBBBBB ->>> "  + super.getTabId());
        map.put("environment", super.getTabId());
        map.put("status", isSuccessful ? "" : "disabled=''");
        map.put("buildId", sBuild.getBuildId());
        map.put("projectName", projectName);
        map.put("phase", phase);
    }
}