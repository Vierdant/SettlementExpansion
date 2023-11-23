package settlementexpansion.object.entity;

import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import settlementexpansion.map.preset.BlueprintPreset;
import settlementexpansion.map.preset.BlueprintPresetID;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlueprintObjectEntity extends ObjectEntity {
    private BlueprintPreset preset;
    private HudDrawElement hudElement;

    public BlueprintObjectEntity(Level level, BlueprintPresetID presetId, int x, int y) {
        super(level, "blueprint", x, y);
        this.preset = new BlueprintPreset(presetId);

        try {
            System.out.println("rotated to: " + level.getObjectRotation(x, y));
            this.preset = preset.rotate(PresetRotation.toRotationAngle(level.getObjectRotation(x, y)));
        } catch (PresetRotateException ignored) {
        }

        this.hudElement = null;
    }

    public BlueprintPreset getPreset() {
        return preset;
    }

    @Override
    public void init() {
        super.init();

        if (this.getLevel().isClientLevel()) {
            this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
                public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                    final Point placeTile = new Point(getTileX(), getTileY());
                    final TextureDrawOptionsEnd canApplyOptions;
                    if (preset.canApplyToLevel(this.getLevel(), placeTile.x, placeTile.y)) {
                        canApplyOptions = null;
                    } else {
                        canApplyOptions = Screen.initQuadDraw(preset.width * 32, preset.height * 32).color(1.0F, 0.0F, 0.0F, 0.5F).pos(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y));
                    }

                    list.add(new SortedDrawable() {
                        public int getPriority() {
                            return Integer.MIN_VALUE;
                        }

                        public void draw(TickManager tickManager) {
                            preset.drawPlacePreview(getLevel(), placeTile.x, placeTile.y, perspective, camera);
                            if (canApplyOptions != null) {
                                canApplyOptions.draw();
                            }
                        }
                    });
                }
            });
        }
    }

    public Point getPlaceTile(Preset preset) {
        return new Point((this.getTileX() - preset.width * 32 / 2 + 16) / 32, (this.getTileY() - preset.height * 32 / 2 + 16) / 32);
    }

    @Override
    public void onObjectDestroyed(GameObject previousObject, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onObjectDestroyed(previousObject, client, itemsDropped);
        if (hudElement != null) {
            System.out.println("removed");
            this.hudElement.remove();
        }
    }
}
