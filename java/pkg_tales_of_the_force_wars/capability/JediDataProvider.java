package pkg_tales_of_the_force_wars.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JediDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<JediData> JEDI_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private final JediData data = new JediDataImpl();
    private final LazyOptional<JediData> optional = LazyOptional.of(() -> data);

    public interface JediData {
        String getStage();
        void setStage(String stage);
        double getForceValue();
        void setForceValue(double forceValue);
        void addForceValue(double amount);
        String[] getAbilitySlots();
        void setAbilitySlot(int index, String ability);
        String[] getUnlockedAbilities();
        void unlockAbility(String ability);
        boolean isAbilityUnlocked(String ability);
        CompoundTag saveNBTData();
        void loadNBTData(CompoundTag nbt);
        boolean isJumpActive();
        void setJumpActive(boolean active);
        // Force kullanıcı seçim durumu
        String getForceStatus(); // "force_sensitive" | "non_force" | "unknown"
        void setForceStatus(String status);
        boolean isForceEvaluated();
        void setForceEvaluated(boolean evaluated);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == JEDI_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = data.saveNBTData();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null) {
            return;
        }
        data.loadNBTData(nbt);
    }

    private static class JediDataImpl implements JediData {
        private String stage = "youngling";
        private double forceValue = 0.0;
        private String[] abilitySlots = new String[4];
        private List<String> unlockedAbilities = new ArrayList<>();
        private boolean jumpActive = false;
        private String forceStatus = "unknown";
        private boolean forceEvaluated = false;

        @Override
        public String getStage() {
            return stage;
        }

        @Override
        public void setStage(String stage) {
            this.stage = stage != null ? stage : "youngling";
        }

        @Override
        public double getForceValue() {
            return forceValue;
        }

        @Override
        public void setForceValue(double forceValue) {
            this.forceValue = forceValue;
        }

        @Override
        public void addForceValue(double amount) {
            this.forceValue += amount;
        }

        @Override
        public String[] getAbilitySlots() {
            return abilitySlots != null ? abilitySlots.clone() : new String[4];
        }

        @Override
        public void setAbilitySlot(int index, String ability) {
            if (index >= 0 && index < abilitySlots.length) {
                abilitySlots[index] = ability;
            }
        }

        @Override
        public String[] getUnlockedAbilities() {
            return unlockedAbilities != null ? unlockedAbilities.toArray(new String[0]) : new String[0];
        }

        @Override
        public void unlockAbility(String ability) {
            if (ability != null && !unlockedAbilities.contains(ability)) {
                unlockedAbilities.add(ability);
            }
        }

        @Override
        public boolean isAbilityUnlocked(String ability) {
            return ability != null && unlockedAbilities.contains(ability);
        }

        @Override
        public CompoundTag saveNBTData() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("stage", stage != null ? stage : "youngling");
            nbt.putDouble("forceValue", forceValue);
            ListTag slotsTag = new ListTag();
            for (String slot : abilitySlots) {
                slotsTag.add(StringTag.valueOf(slot != null ? slot : ""));
            }
            nbt.put("abilitySlots", slotsTag);
            CompoundTag legacySlotsTag = new CompoundTag();
            for (int i = 0; i < abilitySlots.length; i++) {
                if (abilitySlots[i] != null) {
                    legacySlotsTag.putString("slot" + i, abilitySlots[i]);
                }
            }
            nbt.put("legacyAbilitySlots", legacySlotsTag);
            ListTag abilitiesTag = new ListTag();
            for (String ability : unlockedAbilities) {
                abilitiesTag.add(StringTag.valueOf(ability != null ? ability : ""));
            }
            nbt.put("unlockedAbilities", abilitiesTag);
            nbt.putBoolean("jumpActive", jumpActive); // Jump aktifliğini kaydet
            nbt.putString("forceStatus", forceStatus != null ? forceStatus : "unknown");
            nbt.putBoolean("forceEvaluated", forceEvaluated);
            return nbt;
        }

        @Override
        public void loadNBTData(CompoundTag nbt) {
            if (nbt == null) {
                return;
            }
            stage = nbt.getString("stage");
            if (stage.isEmpty()) stage = "youngling";
            forceValue = nbt.getDouble("forceValue");
            if (nbt.contains("abilitySlots", Tag.TAG_LIST)) {
                ListTag slotsTag = nbt.getList("abilitySlots", Tag.TAG_STRING);
                for (int i = 0; i < abilitySlots.length && i < slotsTag.size(); i++) {
                    String slot = slotsTag.getString(i);
                    abilitySlots[i] = slot.isEmpty() ? null : slot;
                }
            } else if (nbt.contains("legacyAbilitySlots", Tag.TAG_COMPOUND) || nbt.contains("abilitySlots", Tag.TAG_COMPOUND)) {
                CompoundTag slotsTag = nbt.contains("legacyAbilitySlots") ? nbt.getCompound("legacyAbilitySlots") : nbt.getCompound("abilitySlots");
                for (int i = 0; i < abilitySlots.length; i++) {
                    abilitySlots[i] = slotsTag.contains("slot" + i) ? slotsTag.getString("slot" + i) : null;
                }
            }
            unlockedAbilities.clear();
            if (nbt.contains("unlockedAbilities", Tag.TAG_LIST)) {
                ListTag abilitiesTag = nbt.getList("unlockedAbilities", Tag.TAG_STRING);
                for (int i = 0; i < abilitiesTag.size(); i++) {
                    String ability = abilitiesTag.getString(i);
                    if (!ability.isEmpty()) {
                        unlockedAbilities.add(ability);
                    }
                }
            }
            jumpActive = nbt.getBoolean("jumpActive"); // Jump aktifliğini yükle
            forceStatus = nbt.contains("forceStatus") ? nbt.getString("forceStatus") : "unknown";
            forceEvaluated = nbt.getBoolean("forceEvaluated");
        }

        @Override
        public boolean isJumpActive() {
            return jumpActive;
        }

        @Override
        public void setJumpActive(boolean active) {
            this.jumpActive = active;
        }

        @Override
        public String getForceStatus() {
            return forceStatus;
        }

        @Override
        public void setForceStatus(String status) {
            this.forceStatus = status != null ? status : "unknown";
        }

        @Override
        public boolean isForceEvaluated() {
            return forceEvaluated;
        }

        @Override
        public void setForceEvaluated(boolean evaluated) {
            this.forceEvaluated = evaluated;
        }
    }
}