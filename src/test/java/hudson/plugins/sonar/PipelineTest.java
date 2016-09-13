package hudson.plugins.sonar;

import hudson.cli.CLI;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.assertj.core.api.Assertions.assertThat;

// TODO goals:
// verify simplest step run: sonarScanner()
// verify step run with a param: sonarScanner installationName: "scannerToolName"
// verify simplest wrapper run: withSonarQubeEnv { echo }
// verify wrapper run with a param: withSonarQubeEnv("scannerToolName") { echo }
public class PipelineTest {

  @Rule
  public JenkinsRule j = new JenkinsRule();

  @Test
  public void test_run_echo() throws Exception {
    String script = "node { echo 'Hello from Pipeline' }";
    String result = runAndVerifyScript(script);
    assertThat(result).contains("Hello from Pipeline");
  }

  @Test
  public void test_run_sh_echo() throws Exception {
    String script = "node { sh 'echo \"Hello from Pipeline\"' }";
    String result = runAndVerifyScript(script);
    assertThat(result).contains("Hello from Pipeline");
  }

  //@Test
  public void test_run_sonarScanner_without_params() throws Exception {
    // cannot work: restarts Jenkins instance in the test
    CLI cli = new CLI(new URL(j.getInstance().getRootUrl()));
//    int exitCode = cli.execute("install-plugin", "target/sonar.hpi", "-deploy");
//    assertThat(exitCode).isEqualTo(0);

    String script = "node { sonarScanner() }";
    String result = runAndVerifyScript(script);
    System.out.println(result);
    assertThat(result).contains("Hello from Pipeline");
  }

  private String runAndVerifyScript(String script) throws java.io.IOException, InterruptedException, java.util.concurrent.ExecutionException {
    WorkflowJob job = j.createProject(WorkflowJob.class);
    FlowDefinition flowDefinition = new CpsFlowDefinition(script, false);
    job.setDefinition(flowDefinition);

    WorkflowRun build = job.scheduleBuild2(0).get();

    String result = FileUtils.readFileToString(build.getLogFile());
    assertSuccess(result);

    return result;
  }

  private void assertSuccess(String result) {
    assertThat(result.trim()).endsWith("Finished: SUCCESS");
  }
}
