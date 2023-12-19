package settlementexpansion.updater;

import necesse.engine.modLoader.LoadedMod;
import settlementexpansion.util.Lazy;
import settlementexpansion.util.gitcheck.GitCheck;
import settlementexpansion.util.gitcheck.GitCheckResult;
import settlementexpansion.util.gitcheck.git.GitRepository;
import settlementexpansion.util.gitcheck.git.GitTag;

import java.util.concurrent.CompletableFuture;

public class UpdaterService {

    private static final GitRepository GIT_REPOSITORY = GitRepository.of("Vierdant", "SettlementExpansion");

    private final GitCheck gitCheck = new GitCheck();
    protected final Lazy<GitCheckResult> gitCheckResult;
    private boolean shouldRead = false;

    public UpdaterService() {

        this.gitCheckResult = new Lazy<>(() -> {
            String version = LoadedMod.getRunningMod().version;

            return this.gitCheck.checkRelease(GIT_REPOSITORY, GitTag.of("v" + version));
        });

        if (this.gitCheckResult.get() != null) {
            this.shouldRead = true;
        }
    }

    public CompletableFuture<Boolean> isUpToDate() {
        if (!this.shouldRead) {
            return null;
        }
        return CompletableFuture.supplyAsync(() -> {
            GitCheckResult result = this.gitCheckResult.get();

            return result.isUpToDate();
        });
    }
}
