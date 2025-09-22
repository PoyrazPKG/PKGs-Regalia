package pkg_tales_of_the_force_wars.capability;

import net.minecraft.nbt.CompoundTag;

public class JediData {
    private String stage = "youngling";
    private double forceValue = 0.0;
    private String[] abilitySlots = new String[4];

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public double getForceValue() {
        return forceValue;
    }

    public void setForceValue(double forceValue) {
        this.forceValue = forceValue;
    }

    public void addForceValue(double amount) {
        this.forceValue += amount;
    }

    public String[] getAbilitySlots() {
        return abilitySlots.clone();
    }

    public void setAbilitySlot(int index, String ability) {
        if (index >= 0 && index < abilitySlots.length) {
            abilitySlots[index] = ability;
        }
    }

    public CompoundTag saveNBTData() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("stage", stage);
        nbt.putDouble("forceValue", forceValue);
        for (int i = 0; i < abilitySlots.length; i++) {
            if (abilitySlots[i] != null) {
                nbt.putString("slot" + i, abilitySlots[i]);
            }
        }
        return nbt;
    }

    public void loadNBTData(CompoundTag nbt) {
        stage = nbt.getString("stage");
        forceValue = nbt.getDouble("forceValue");
        for (int i = 0; i < abilitySlots.length; i++) {
            abilitySlots[i] = nbt.contains("slot" + i) ? nbt.getString("slot" + i) : null;
        }
    }
}