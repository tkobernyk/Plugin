package TeamCity.UI.Tabs;

import TeamCity.Models.HelixDeploySettings;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.CustomTab;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import TeamCity.Helpers.PropertyRepository;

public class SettingsAdminPage extends AdminPage implements CustomTab {
    private final PluginDescriptor _descriptor;

    public SettingsAdminPage(@NotNull PagePlaces pagePlaces,
                             @NotNull PluginDescriptor descriptor) {
        super(pagePlaces);
        setPluginName("deploySettings");
        setIncludeUrl(descriptor.getPluginResourcesPath("Views/admin/HelixDeploySettingsAdminPage.jsp"));
        setTabTitle("Helix Deploy Settings");
        setPosition(PositionConstraint.last());
        register();
        _descriptor = descriptor;
    }

    @NotNull
    @Override
    public String getGroup() {
        return "Custom Settings";
    }
    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request)
    {
        PropertyRepository repository = new PropertyRepository(request.getServletContext(), _descriptor.getPluginResourcesPath("settings.properties"));
        HelixDeploySettings settings = new HelixDeploySettings();
        try {
            settings.PowerShellScriptPath = repository.getProperties().getProperty("powershellpath", "{teamcityPluginResourcesPath}/PSScript/Deploy.ps1");
            settings.Environments = repository.getProperties().getProperty("environments", "Dev,Test,Show,Production").split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.put("powershellpath", settings.PowerShellScriptPath);
        model.put("environments", settings.Environments);
    }
}
