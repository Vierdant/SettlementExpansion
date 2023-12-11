package settlementexpansion.entity.projectile;

import necesse.engine.tickManager.TickManager;
import necesse.entity.levelEvent.explosionEvent.CannonBallExplosionEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class TrapCannonBallProjectile extends Projectile {
    private long spawnTime;

    public TrapCannonBallProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = 200.0F;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(400);
    }

    public TrapCannonBallProjectile() {}

    public void init() {
        super.init();
        this.setWidth(15.0F);
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        this.spawnTime = this.getWorldEntity().getTime();
        this.doesImpactDamage = false;
        this.trailOffset = 0.0F;
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(30, 30, 30), 14.0F, 250, 18.0F);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
        }
    }

    public float getAngle() {
        return (float)(this.getWorldEntity().getTime() - this.spawnTime);
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (this.getLevel().isServer()) {
            CannonBallExplosionEvent event = new CannonBallExplosionEvent(x, y, this.getDamage(), this.getOwner());
            this.getLevel().entityManager.addLevelEvent(event);
        }
    }

    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("explosion", 3);
    }
}
