package settlementexpansion.patch;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;
import settlementexpansion.ExpandedGame;
import settlementexpansion.GlobalModData;

public class MainGamePatch {
    @ModConstructorPatch(target = MainGame.class, arguments = {Client.class})
    public static class MainGameConstructorPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGame main, @Advice.Argument(0) Client client) {
            GlobalModData.addExpandedGame(main, client);
        }
    }

    @ModMethodPatch(target = MainGame.class, name = "setRunning", arguments = {boolean.class})
    public static class MainGameRunningStatePatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGame main, @Advice.Argument(0) boolean running) {
            ExpandedGame game = GlobalModData.getExpandedGame(main);
            if (game != null) {
                game.setRunning(running);
            }
        }
    }

    @ModMethodPatch(target = MainGame.class, name = "onWindowResized", arguments = {})
    public static class MainGameWindowResizePatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGame main) {
            ExpandedGame game = GlobalModData.getExpandedGame(main);
            if (game != null) {
                game.onWindowResized();
            }
        }
    }

    @ModMethodPatch(target = MainGame.class, name = "dispose", arguments = {})
    public static class MainGameDisposePatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGame main) {
            ExpandedGame game = GlobalModData.getExpandedGame(main);
            if (game != null) {
                game.dispose();
            }
        }
    }

    @ModMethodPatch(target = MainGame.class, name = "frameTick", arguments = {TickManager.class})
    public static class MainGameFrameTickPatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGame main, @Advice.Argument(0) TickManager tickManager) {
            ExpandedGame game = GlobalModData.getExpandedGame(main);
            if (game != null) {
                game.frameTick(tickManager);
            }
        }
    }

    @ModMethodPatch(target = MainGameFormManager.class, name = "updateActive", arguments = {boolean.class})
    public static class MainGameFormManagerUpdateActivePatch {

        @Advice.OnMethodExit
        static void onExit(@Advice.This MainGameFormManager main, @Advice.Argument(0) boolean force) {
            if (GlobalModData.getMainGame() != null && main == GlobalModData.getMainGame().formManager) {
                GlobalModData.getExpandedGame(GlobalModData.getMainGame()).formManager.updateActive(force);
            }
        }
    }
}
