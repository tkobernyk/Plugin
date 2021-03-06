package TeamCity.configuration;

import TeamCity.UI.Tabs.*;
import TeamCity.cache.CacheWrapper;
import TeamCity.controllers.DeployControllerAction;
import TeamCity.controllers.MessageListener;
import TeamCity.controllers.SettingAdminController;
import TeamCity.dao.HistoricalDataDao;
import TeamCity.dao.impl.HistoricalDataImpl;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellRunner;
import TeamCity.powershell.process.ProcessFactory;
import TeamCity.service.DeployService;
import TeamCity.service.MessageService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class BeansConfiguration {

    @Bean
    public DevDeployTab devDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        return new DevDeployTab(webControllerManager, buildsManager, descriptor);
    }

    @Bean
    public TestDeployTab testDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        return new TestDeployTab(webControllerManager, buildsManager, descriptor);
    }

    @Bean
    public ShowDeployTab showDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        return new ShowDeployTab(webControllerManager, buildsManager, descriptor);
    }

    @Bean
    public ProdDeployTab prodDeployTab(@NotNull WebControllerManager webControllerManager, @NotNull BuildsManager buildsManager, @NotNull PluginDescriptor descriptor) {
        return new ProdDeployTab(webControllerManager, buildsManager, descriptor);
    }

    @Bean
    public SettingsAdminPage settingsAdminPage(@NotNull PagePlaces pagePlaces,
                                               @NotNull PluginDescriptor descriptor) {
        return new SettingsAdminPage(pagePlaces, descriptor);
    }

    @Bean
    public SettingAdminController settingAdminController(@NotNull SBuildServer server,
                                                         @NotNull WebControllerManager webControllerManager,
                                                         @NotNull PluginDescriptor pluginDescriptor) {
        return new SettingAdminController(server, webControllerManager, pluginDescriptor);
    }

    @Bean
    public MessageListener messageListener(@NotNull SBuildServer server,
                                           @NotNull WebControllerManager webControllerManager) {
        return new MessageListener(server, webControllerManager, messageService());
    }


    @Bean
    PowerShellFactory powerShellFactory() {
        return new PowerShellFactory(cacheWrapper(), powerShellRunner());
    }

    @Bean
    DeployControllerAction deployControllerAction(@NotNull SBuildServer server,
                                                  @NotNull WebControllerManager webControllerManager,
                                                  @NotNull PluginDescriptor descriptor) {
        return new DeployControllerAction(server, webControllerManager, descriptor, deployService());
    }

    @Bean
    public CacheWrapper cacheWrapper() {
        return new CacheWrapper();
    }

    @Bean
    public ProcessFactory processFactory() {
        return new ProcessFactory(cacheWrapper());
    }

    @Bean
    public PowerShellRunner powerShellRunner() {
        return new PowerShellRunner(processFactory());
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        hikariConfig.setJdbcUrl("jdbc:sqlserver://localhost;databaseName=TeamCity;");
        hikariConfig.setUsername("TeamCity");
        hikariConfig.setPassword("!teamcity!");

        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("springHikariCP");

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        return new HikariDataSource(hikariConfig);
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public HistoricalDataDao historicalDataDao() {
        return new HistoricalDataImpl(jdbcTemplate());
    }

    @Bean
    public DeployService deployService() {
        return new DeployService(historicalDataDao(), powerShellFactory());
    }

    @Bean
    public MessageService messageService() {
        return new MessageService(powerShellFactory());
    }

}
