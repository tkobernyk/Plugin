package TeamCity.powershell.process;

import TeamCity.cache.CacheWrapper;
import TeamCity.exception.NoSupportedOSException;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ProcessFactory {

    private static final com.intellij.openapi.diagnostic.Logger LOGGER =
            com.intellij.openapi.diagnostic.Logger.getInstance(ProcessFactory.class.getName());

    private CacheWrapper cacheWrapper;

    public ProcessFactory(CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }


    public Process getOrCreateProcess(String fileNameProcessing, String scriptPath, String params) {
        if (cacheWrapper.getJavaProccessCache().get(fileNameProcessing.hashCode()) != null) {
            LOGGER.info("Getting process from cache ---------------------------");
            return cacheWrapper.getJavaProccessCache().get(fileNameProcessing.hashCode());
        }
        return createProcessWindowsOS(fileNameProcessing, scriptPath, params);
    }


    private Process createProcessWindowsOS(String fileName, String scriptPath, String params) {
        LOGGER.info("Creating  new process --------------------------------");
        LOGGER.info("OS NAME " + System.getProperty("os.name"));
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String powerShellExecutable = "powershell.exe";
            LOGGER.info("SCRIPT PATH = " + scriptPath);
            ProcessBuilder processBuilder = new ProcessBuilder(powerShellExecutable,
                    "-ExecutionPolicy", "Bypass", "-NoExit", "-File", scriptPath, params);
            try {
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                cacheWrapper.getJavaProccessCache().put(fileName.hashCode(), process);
                return process;
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        throw new NoSupportedOSException("os.name should be windows");
    }

}
