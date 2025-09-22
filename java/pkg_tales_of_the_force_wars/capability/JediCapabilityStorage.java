package pkg_tales_of_the_force_wars.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class JediCapabilityStorage implements INBTSerializable<CompoundTag> {
    private boolean isJedi = false;
    private int jediLevel = 0;
    private int jediXP = 0;
    private int forcePushLevel = 0;
    private int forcePullLevel = 0;
    private int forcePushXP = 0;
    private int forcePullXP = 0;

    public boolean isJedi() { return isJedi; }
    public int getJediLevel() { return jediLevel; }
    public int getJediXP() { return jediXP; }
    public int getForcePushLevel() { return forcePushLevel; }
    public int getForcePullLevel() { return forcePullLevel; }
    public int getForcePushXP() { return forcePushXP; }
    public int getForcePullXP() { return forcePullXP; }

    public void setJedi(boolean isJedi) { this.isJedi = isJedi; }
    public void setJediLevel(int jediLevel) { this.jediLevel = jediLevel; }
    public void addJediXP(int jediXP) { this.jediXP += jediXP; }
    public void setForcePushLevel(int forcePushLevel) { this.forcePushLevel = forcePushLevel; }
    public void setForcePullLevel(int forcePullLevel) { this.forcePullLevel = forcePullLevel; }
    public void addForcePushXP(int forcePushXP) { this.forcePushXP += forcePushXP; }
    public void addForcePullXP(int forcePullXP) { this.forcePullXP += forcePullXP; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isJedi", isJedi);
        tag.putInt("jediLevel", jediLevel);
        tag.putInt("jediXP", jediXP);
        tag.putInt("forcePushLevel", forcePushLevel);
        tag.putInt("forcePullLevel", forcePullLevel);
        tag.putInt("forcePushXP", forcePushXP);
        tag.putInt("forcePullXP", forcePullXP);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isJedi = nbt.getBoolean("isJedi");
        jediLevel = nbt.getInt("jediLevel");
        jediXP = nbt.getInt("jediXP");
        forcePushLevel = nbt.getInt("forcePushLevel");
        forcePullLevel = nbt.getInt("forcePullLevel");
        forcePushXP = nbt.getInt("forcePushXP");
        forcePullXP = nbt.getInt("forcePullXP");
    }
}