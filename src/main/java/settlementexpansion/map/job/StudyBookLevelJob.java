package settlementexpansion.map.job;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.job.*;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.LevelJob;
import settlementexpansion.inventory.lootTable.LootTableModPresets;
import settlementexpansion.object.entity.StudyTableObjectEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudyBookLevelJob extends LevelJob {

    public StudyBookLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public StudyBookLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isValid() {
        StudyTableObjectEntity ent = this.getObjectEntity();
        return ent != null && ent.getMaterialCount() > 0;
    }

    public List<InventoryItem> study() {
        StudyTableObjectEntity ent = this.getObjectEntity();
        ent.getInventory().setAmount(0, ent.getMaterialCount() - 1);
        ent.markDirty();

        List<InventoryItem> list = new ArrayList<>();
        LootTableModPresets.studyingResult.addItems(list, GameRandom.globalRandom, 1f);
        return list;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public StudyTableObjectEntity getObjectEntity() {
        ObjectEntity objectEntity = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        return objectEntity instanceof StudyTableObjectEntity ? (StudyTableObjectEntity)objectEntity : null;
    }

    public static <T extends StudyBookLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob) {
        //GameObject object = foundJob.job.getLevel().getObject(foundJob.job.tileX, foundJob.job.tileY);
        GameMessage activityDescription = new LocalMessage("activities", "studying");
        return new SingleJobSequence(foundJob.job.getActiveJob(worker, foundJob.priority), activityDescription);
    }

    public ActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority) {
        return new TileActiveJob(worker, priority, this.tileX, this.tileY) {
            public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
                return new JobMoveToTile(this.tileX, this.tileY, true);
            }

            public void tick(boolean isCurrent, boolean isMovingTo) {
                StudyBookLevelJob.this.reservable.reserve(this.worker.getMobWorker());
            }

            public boolean isValid(boolean isCurrent) {
                return !StudyBookLevelJob.this.isRemoved() && StudyBookLevelJob.this.reservable.isAvailable(this.worker.getMobWorker()) && StudyBookLevelJob.this.isValid();
            }

            public ActiveJobResult perform() {
                if (this.worker.isInWorkAnimation()) {
                    return ActiveJobResult.PERFORMING;
                } else {
                    List<InventoryItem> items = StudyBookLevelJob.this.study();
                    this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, null, 250);

                    for (InventoryItem item : items) {
                        this.worker.getWorkInventory().add(item);
                    }

                    return ActiveJobResult.FINISHED;
                }
            }
        };
    }
}
