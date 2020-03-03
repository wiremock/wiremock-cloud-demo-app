package mocklab.build

import org.gradle.api.tasks.TaskAction
import org.kohsuke.github.GHRelease

class GithubReleasePublishTask extends GithubTaskBase {

    String tagName
    String attachmentFilePath

    @TaskAction
    def publishGithubRelease() {
        assert tagName, "Must supply a tag name"
        assert attachmentFilePath, "Must supply a file to attach to the release"

        GHRelease release = repository().createRelease(tagName)
                .name("Release $tagName")
                .create()

        release.uploadAsset(new File(attachmentFilePath), 'application/octet-stream')
    }
}
