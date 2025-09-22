package pkgmultiversetitans.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class JediCapability implements INBTSerializable<CompoundTag> {
    private boolean isJedi = false;
    private int jediLevel = 0;
    private int forcePushLevel = 0;
    private int forcePullLevel = 0;
    private int jediXP = 0;
    private int forcePushXP = 0;
    private int forcePullXP = 0;

    public boolean isJedi() {
        return isJedi;
    }

    public void setJedi(boolean isJedi) {
        this.isJedi = isJedi;
    }

    public int getJediLevel() {
        return jediLevel;
    }

    public void setJediLevel(int level) {
        this.jediLevel = level;
    }

    public int getJediXP() {
        return jediXP;
    }

    public void addJediXP(int xp) {
        this.jediXP += xp;
        if (this.jediXP < 0) {
            this.jediXP = 0;
        }
    }

    public int getForcePushLevel() {
        return forcePushLevel;
    }

    public void setForcePushLevel(int level) {
        this.forcePushLevel = level;
    }

    public int getForcePullLevel() {
        return forcePullLevel;
    }

    public void setForcePullLevel(int level) {
        this.forcePullLevel = level;
    }

    public int getForcePushXP() {
        return forcePushXP;
    }

    public void addForcePushXP(int xp) {
        this.forcePushXP += xp;
        if (this.forcePushXP < 0) {
            this.forcePushXP = 0;
        }
    }

    public int getForcePullXP() {
        return forcePullXP;
    }

    public void addForcePullXP(int xp) {
        this.forcePullXP += xp;
        if (this.forcePullXP < 0) {
            this.forcePullXP = 0;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("isJedi", isJedi);
        nbt.putInt("jediLevel", jediLevel);
        nbt.putInt("forcePushLevel", forcePushLevel);
        nbt.putInt("forcePullLevel", forcePullLevel);
        nbt.putInt("jediXP", jediXP);
        nbt.putInt("forcePushXP", forcePushXP);
        nbt.putInt("forcePullXP", forcePullXP);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isJedi = nbt.getBoolean("isJedi");
        jediLevel = nbt.getInt("jediLevel");
        forcePushLevel = nbt.getInt("forcePushLevel");
        forcePullLevel = nbt.getInt("forcePullLevel");
        jediXP = nbt.getInt("jediXP");
        forcePushXP = nbt.getInt("forcePushXP");
        forcePullXP = nbt.getInt("forcePullXP");
    }
}