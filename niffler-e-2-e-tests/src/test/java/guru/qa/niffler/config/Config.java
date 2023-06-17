package guru.qa.niffler.config;

public interface Config {
  static Config getConfig() {
//  if(System.getProperty("env").equals("docker")) - плохая запись, так как System.getProperty("env") может быть null
    if ("docker".equals(System.getProperty("env"))) {
      return new DockerConfig();
    }
    return new LocalConfig();
  }

  String getDbHost();
  int getDbPort();
  String getDBLogin();
  String getDBPassword();
  String getSpendUrl();
  String getFrontUrl();
  String getAuthUrl();
}