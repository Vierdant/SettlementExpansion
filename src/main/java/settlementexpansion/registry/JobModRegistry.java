package settlementexpansion.registry;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.entity.mobs.job.JobType;
import settlementexpansion.map.job.StudyBookLevelJob;

public class JobModRegistry {

    public static int studyingId;

    public static void registerJobTypes() {
        JobTypeRegistry.registerType("studying", new JobType(true, true,
                (level) -> level.getWorldSettings().jobSearchRange,
                new LocalMessage("jobs", "studyingname"),
                new LocalMessage("jobs", "studyingtip")));
    }

    public static void registerLevelJobs() {
        studyingId = LevelJobRegistry.registerJob("studybook", StudyBookLevelJob.class, "studying");
    }
}
