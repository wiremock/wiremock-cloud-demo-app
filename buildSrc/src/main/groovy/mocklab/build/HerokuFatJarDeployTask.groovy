package mocklab.build

import org.apache.commons.io.IOUtils
import groovy.transform.Immutable
import org.gradle.api.tasks.TaskAction
import org.kohsuke.github.GHRelease

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class HerokuFatJarDeployTask extends GithubTaskBase {

    String tagName
    String herokuApp

    @TaskAction
    def deployToHeroku() {
        assert tagName, "Must supply a tag name"
        assert herokuApp, "Must supply a Heroku app name"

        int timeoutMinutes = (System.getenv('DEPLOY_TIMEOUT') ?: '20').toInteger()

        shell('which git')
        shell('which heroku')
        shell('heroku --version')
        shell('git --version')

        shell('git config http.postBuffer 524288000')
        shell('git config --list')

        String currentBranch = 'git rev-parse --abbrev-ref HEAD'.execute().text

        GHRelease release = repository().listReleases().find { it.tagName == tagName }
        assert release, "No release found for tag $tagName"
        println "Found release $release.id for tag $tagName"

        def assets = release.assets

        assert assets, "There are no assets attached to release $tagName"

        String downloadUrl = assets[0].url.toString()
        String branchName = "deploy-${System.currentTimeMillis()}"
        shell("git checkout --orphan $branchName")
        
        try {
            shell("git reset --hard")

            new File('Procfile').write('web: java ${JAVA_OPTS} -Dserver.port=$PORT -Dnetworkaddress.cache.ttl=0 -Dnetworkaddress.cache.negative.ttl=0 -jar application.jar run')
            new File('manifest.sh').write("""ARTIFACT_URL=$downloadUrl
    DOWNLOAD_FROM=github-releases
    """)

            shell('git add manifest.sh Procfile')
            shell('git commit -m Deployment')
            shell("heroku git:remote --app $herokuApp")
            shell("git push -f heroku HEAD:master", true, timeoutMinutes, MINUTES)
        } finally {
            shell("git checkout $currentBranch")
        }

    }

    private static ShellResult shell(String command, boolean assertOkExit = true, long timeout = 30, TimeUnit timeUnit = SECONDS) {
        println "\$ $command"
        Process proc = command.execute()

        Future<?> outStreamer = streamToConsole(proc.inputStream, System.out)
        Future<?> errorStreamer = streamToConsole(proc.errorStream, System.err)

        assert proc.waitFor(timeout, timeUnit)
        outStreamer.cancel(true)
        errorStreamer.cancel(true)

        def exitValue = proc.exitValue()

        if (assertOkExit) {
            assert exitValue == 0, "ERROR: Command exited with code $exitValue"
        }

        println()
        new ShellResult(exitCode: exitValue, output: proc.text)
    }

    private static Future<?> streamToConsole(InputStream stream, PrintStream dest) {
        ExecutorService executor = Executors.newSingleThreadExecutor()
        executor.submit {
            IOUtils.copy(stream, dest)
        }
    }
}

@Immutable
class ShellResult {

    int exitCode
    String output
}
