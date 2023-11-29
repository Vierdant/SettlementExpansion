package settlementexpansion.util.gitcheck;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import settlementexpansion.util.gitcheck.git.GitRelease;
import settlementexpansion.util.gitcheck.git.GitTag;
import settlementexpansion.util.gitcheck.shared.Preconditions;

/**
 * Represents the result of a git check.
 */
public class GitCheckResult {

    private final GitRelease latestRelease;
    private final GitTag currentTag;

    GitCheckResult(@NotNull GitRelease latestRelease, @NotNull GitTag currentTag) {
        Preconditions.notNull(latestRelease, "latest release");
        Preconditions.notNull(currentTag, "current tag");

        this.latestRelease = latestRelease;
        this.currentTag = currentTag;
    }

    /**
     * Gets the latest release.
     *
     * @return the latest release
     */
    @NotNull
    public GitRelease getLatestRelease() {
        return this.latestRelease;
    }

    /**
     * Gets the current tag.
     *
     * @return the current tag
     */
    @NotNull
    public GitTag getCurrentTag() {
        return this.currentTag;
    }

    /**
     * Checks if the latest release is up to date.
     *
     * @return {@code true} if the latest release is up to date, {@code false} otherwise
     */
    @Contract(pure = true)
    public boolean isUpToDate() {
        return this.latestRelease.getTag().equals(this.currentTag);
    }

}
