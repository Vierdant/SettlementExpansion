package settlementexpansion.registry;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.entity.mobs.job.JobType;
import settlementexpansion.map.job.StudyBookLevelJob;

public class JobModRegistry {

    public static int studyingId;

    public static void registerJobTypes() {
        JobTypeRegistry.registerType("needs", new JobType(false, false, (level) ->
                level.getWorldSettings().jobSearchRange, null, null));
        JobTypeRegistry.registerType("hauling", new JobType(true, false, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "haulingname"), new LocalMessage("jobs", "haulingtip")));
        JobTypeRegistry.registerType("crafting", new JobType(true, false, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "craftingname"), new LocalMessage("jobs", "craftingtip")));
        JobTypeRegistry.registerType("forestry", new JobType(true, false, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "forestryname"), new LocalMessage("jobs", "forestrytip")));
        JobTypeRegistry.registerType("farming", new JobType(true, false, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "farmingname"), new LocalMessage("jobs", "farmingtip")));
        JobTypeRegistry.registerType("fertilize", new JobType(true, true, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "fertilizename"), new LocalMessage("jobs", "fertilizetip")));
        JobTypeRegistry.registerType("husbandry", new JobType(true, true, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "husbandryname"), new LocalMessage("jobs", "husbandrytip")));
        JobTypeRegistry.registerType("fishing", new JobType(true, true, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "fishingname"), null));
        JobTypeRegistry.registerType("hunting", new JobType(true, true, (level) ->
                level.getWorldSettings().jobSearchRange, new LocalMessage("jobs", "huntingname"), new LocalMessage("jobs", "huntingtip")));
        JobTypeRegistry.registerType("studying", new JobType(true, true,
                (level) -> level.getWorldSettings().jobSearchRange,
                new LocalMessage("jobs", "studyingname"),
                new LocalMessage("jobs", "studyingtip")));
    }

    public static void registerLevelJobs() {
        studyingId = LevelJobRegistry.registerJob("studybook", StudyBookLevelJob.class, "studying");
    }
}
