package TeamCity.Controllers;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbAccessTest extends BaseController {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DbAccessTest.class.getName());

    public DbAccessTest(@NotNull SBuildServer server,
                        @NotNull WebControllerManager webControllerManager) {
        super(server);
        webControllerManager.registerController("/dbtest.html", this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        String userName = "TeamCity";
        String password = "!teamcity!";
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=TeamCity;";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, userName, password);
            Statement statement = conn.createStatement();
            String queryString = "SELECT [Column] FROM [dbo].test";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                Log.info(rs.getString("Column"));
                response.getWriter().write(rs.getString("Column"));
            }
            response.getWriter().flush();
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
