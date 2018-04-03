package TeamCity.websockets;

import TeamCity.Helpers.PowerShellRunner;
import TeamCity.Models.Deploy;
import lombok.Setter;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;

import java.util.function.Supplier;


@Setter
public class AsynchronoysMessageProcessing implements Supplier<String> {

    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(AsynchronoysMessageProcessing.class.getName());
    private AtmosphereFramework atmosphereFramework;
    private Deploy deploy;
    private String scriptPath;
    private AtmosphereRequest atmosphereRequest;
    private AtmosphereResponse atmosphereResponse;


    @Override
    public String get() {
        Log.info("INSIDE ASYNC GET");
        String message = PowerShellRunner.run(scriptPath, deploy);
        try {
            Log.info("Thread name inside AsynchronoysMessageProcessing : " + Thread.currentThread().getName());
            atmosphereFramework.doCometSupport(atmosphereRequest.body(message), atmosphereResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Finished";
    }
}
