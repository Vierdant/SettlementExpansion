package settlementexpansion.object.entity;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;
import settlementexpansion.entity.projectile.TrapCannonBallProjectile;

import java.awt.*;

public class CannonTrapObjectEntity extends TrapObjectEntity {
    public static GameDamage damage = new GameDamage(35.0F, 10.0F, 0.0F, 2.0F, 1.0F);

    public CannonTrapObjectEntity(Level level, int x, int y, long cooldownInMs) {
        super(level, x, y, cooldownInMs);
    }

    @Override
    public void triggerTrap(int wireID, int dir) {
        if (!this.getLevel().isClient() && !this.onCooldown()) {
            if (!this.otherWireActive(wireID)) {
                Point position = this.getPos(this.getX(), this.getY(), dir);
                Point targetDir = this.getDir(dir);
                int xPos = position.x * 32;
                if (targetDir.x == 0) {
                    xPos += 16;
                } else if (targetDir.x == -1) {
                    xPos += 30;
                } else if (targetDir.x == 1) {
                    xPos += 2;
                }

                int yPos = position.y * 32;
                if (targetDir.y == 0) {
                    yPos += 16;
                } else if (targetDir.y == -1) {
                    yPos += 30;
                } else if (targetDir.y == 1) {
                    yPos += 2;
                }

                float x = (float)xPos;
                float y = (float)yPos;
                float target = (float)(xPos + targetDir.x);
                this.getLevel().entityManager.projectiles.add(new TrapCannonBallProjectile(x, y, target, (float)(yPos + targetDir.y), 50f, 100, damage, null));
                this.startCooldown();
            }
        }
    }

    public Point getDir(int dir) {
        if (dir == 0) {
            return new Point(0, -1);
        } else if (dir == 1) {
            return new Point(1, 0);
        } else if (dir == 2) {
            return new Point(0, 1);
        } else {
            return dir == 3 ? new Point(-1, 0) : new Point();
        }
    }
}
