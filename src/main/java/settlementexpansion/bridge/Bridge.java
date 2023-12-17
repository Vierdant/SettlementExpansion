package settlementexpansion.bridge;

import necesse.engine.modLoader.LoadedMod;

public abstract class Bridge {

    public String name;
    public LoadedMod mod;

    public abstract void init();
}
