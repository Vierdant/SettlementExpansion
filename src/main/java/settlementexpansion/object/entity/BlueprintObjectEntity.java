package settlementexpansion.object.entity;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
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
        this.preset = new BlueprintPreset(presetId, false, false);
        this.hudElement = null;
    }

    public BlueprintPreset getPreset() {
        return this.preset;
    }

    @Override
    public void init() {
        super.init();

        if (this.getLevel().isServerLevel()) {
            int rotation = getLevel().getObjectRotation(getTileX(), getTileY());
            if (rotation != 0) {
                try {
                    this.preset = preset.rotate(getBlueprintRotation(rotation));
                } catch (PresetRotateException ignored) {
                }
            }
        }

        if (this.getLevel().isClientLevel()) {
            this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
                public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                    int rotation = getLevel().getObjectRotation(getTileX(), getTileY());
                    final Point placeTile = getPlaceTile(rotation);
                    final TextureDrawOptionsEnd canApplyOptions;
                    if (preset.canApplyToLevel(this.getLevel(), placeTile.x, placeTile.y)) {
                        canApplyOptions = null;
                    } else {
                        canApplyOptions = Screen.initQuadDraw(preset.width * 32, preset.height * 32).color(1.0F, 0.0F, 0.0F, 0.2F).pos(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y));
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

    public Point getPlaceTile(int rotation) {
        if (rotation == 0) {
            return new Point(getTileX() - preset.width + 1, getTileY() + 1);
        } else if (rotation == 2) {
            return new Point(getTileX(), getTileY() - preset.height);
        } else if (rotation == 1) {
            return new Point(getTileX() - preset.width, getTileY() - preset.height + 1);
        } else {
            return new Point(getTileX() + 1, getTileY());
        }

    }

    public PresetRotation getBlueprintRotation(int rotation) {
        switch (rotation) {
            case 3: return PresetRotation.ANTI_CLOCKWISE;
            case 2: return PresetRotation.HALF_180;
            default: return PresetRotation.CLOCKWISE;
        }
    }

    public HudDrawElement getHudElement() {
        return hudElement;
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextStringLong(this.preset.getScript());
        writer.putNextBoolean(this.preset.canPlaceOnShore);
        writer.putNextBoolean(this.preset.canPlaceOnLiquid);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.preset = new BlueprintPreset(reader.getNextStringLong(), reader.getNextBoolean(), reader.getNextBoolean());
    }

    @Override
    public void onObjectDestroyed(GameObject previousObject, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onObjectDestroyed(previousObject, client, itemsDropped);
        if (hudElement != null) {
            this.hudElement.remove();
        }
    }
}
