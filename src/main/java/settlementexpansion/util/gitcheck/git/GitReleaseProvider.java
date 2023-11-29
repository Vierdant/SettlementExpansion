package settlementexpansion.util.gitcheck.git;

public interface GitReleaseProvider {

    GitRelease getLatestRelease(GitRepository repository);

}