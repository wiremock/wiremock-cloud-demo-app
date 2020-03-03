package mocklab.build

import org.gradle.api.DefaultTask
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

class GithubTaskBase extends DefaultTask {

    String repoOwner
    String repository
    String accessToken

    protected GHRepository repository() {
        accessToken = accessToken ?: System.getenv('GITHUB_ACCESS_TOKEN')

        assert repoOwner, "Must supply a repository owner"
        assert repository, "Must supply repository name"
        assert accessToken, "Github access token must be supplied, either as a param or via the GITHUB_ACCESS_TOKEN env var"

        GitHub gitHub = GitHub.connectUsingOAuth(accessToken)
        assert  gitHub.credentialValid, "github credentials invalid"
        gitHub.getRepository("$repoOwner/$repository")
    }


}
