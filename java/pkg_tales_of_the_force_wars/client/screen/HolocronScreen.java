package pkg_tales_of_the_force_wars.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import pkg_tales_of_the_force_wars.network.AbilitySlotPacket;
import pkg_tales_of_the_force_wars.network.JediDataSyncPacket;
import pkg_tales_of_the_force_wars.world.menu.HolocronMenu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolocronScreen extends AbstractContainerScreen<HolocronMenu> {
    private static final ResourceLocation MAIN_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/jedi_holocron_main.png");
    private static final ResourceLocation ABILITY_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/jedi_holocron_ability.png");
    private static final ResourceLocation RANK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/jedi_holocron_rank.png");
    private static final ResourceLocation SELECT_RANK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/select_rank.png");
    private static final ResourceLocation SELECT_ABILITY_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/select_ability.png");
    private static final ResourceLocation ABILITY_KEY_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/ability_key.png");
    private static final ResourceLocation FORCE_CLOAK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_cloak.png");
    private static final ResourceLocation FORCE_DEFLECTION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_deflection.png");
    private static final ResourceLocation FORCE_HEAL_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_heal.png");
    private static final ResourceLocation FORCE_JUMP_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_jump.png");
    private static final ResourceLocation FORCE_LIFE_MAPPING_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_life_mapping.png");
    private static final ResourceLocation FORCE_PERSUASION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_persuasion.png");
    private static final ResourceLocation FORCE_PULL_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_pull.png");
    private static final ResourceLocation FORCE_PUSH_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_push.png");
    private static final ResourceLocation FORCE_REFLECTION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_reflection.png");
    private static final ResourceLocation FORCE_REJUVENATION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_rejuvenation.png");
    private static final ResourceLocation FORCE_SENSE_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_sense.png");
    private static final ResourceLocation FORCE_SLOW_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_slow.png");
    private static final ResourceLocation FORCE_STUN_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_stun.png");
    private static final ResourceLocation FORCE_SUPPRESSION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_suppression.png");
    private static final ResourceLocation FORCE_VISION_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_vision.png");
    private static final ResourceLocation FORCE_WAVE_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_wave.png");
    private static final ResourceLocation GROUP_HEAL_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/group_heal.png");
    private static final ResourceLocation MIND_TRICK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/mind_trick.png");
    private static final ResourceLocation X_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/x.png");
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/back.png");
    private static final ResourceLocation YOUNGLING_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/title/youngling.png");
    private static final ResourceLocation PADAWAN_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/title/padawan.png");
    private static final ResourceLocation JEDI_KNIGHT_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/title/jedi_knight.png");
    private static final ResourceLocation JEDI_MASTER_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/title/jedi_master.png");
    private static final ResourceLocation SCROLL_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/scroll.png");
    private static final ResourceLocation SCROLL_HITBOX_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/scroll_hitbox.png");
    private static final ResourceLocation FORCE_SPEED_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/force_speed.png");
    private static final ResourceLocation UPDATE_RANK_TEXTURE = new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/screens/button/update_rank.png");

    private final List<String> abilities = Arrays.asList(
            // 1-15
            "force_push", "force_pull", "force_jump", "enhanced_reflexes", "force_speed",
            "force_stun", "force_slow", "mind_trick", "force_persuasion", "force_sense",
            "force_vision", "battle_meditation", "force_heal", "group_heal", "force_rejuvenation",
            // 16-30
            "force_life_mapping", "force_immunity", "force_barrier", "force_absorb", "force_deflection",
            "force_reflection", "force_wave", "force_suppression", "force_cloak", "force_camouflage",
            "force_stealth", "force_link", "force_empathy", "force_guidance", "force_guardianship",
            // 31-45
            "force_inspiration", "force_valor", "force_stamina", "force_endurance", "force_clarity",
            "force_projection", "force_ghost_manifestation", "force_telepathy", "force_tranquility", "force_sanctuary",
            "force_hearing", "force_touch", "force_light", "force_silence", "force_memory_recollection",
            // 46-52
            "force_bond", "force_guidance_of_the_blade", "force_inspired_healing", "force_symbiosis", "force_hymn",
            "force_coalescence", "force_relic_sense"
    );

    private final Map<String, Integer> abilityCosts = new HashMap<>();
    private final Map<String, Integer> rankCosts = new HashMap<>();
    private final Map<Button, String> buttonTooltips = new HashMap<>();
    private static final java.util.Set<String> PASSIVE_ONLY = new java.util.HashSet<>(java.util.Arrays.asList(
            "force_life_mapping", "enhanced_reflexes"
    ));
    private int x, y;
    private ScreenState state = ScreenState.MAIN;
    private int selectedSlot = -1;
    private String[] slots = new String[4];
    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private boolean isDraggingScrollBar = false;
    private String errorMessage = null;
    private float alpha = 0f;
    private long startTime = -1;

    private enum ScreenState {
        MAIN, RANKS, ABILITIES
    }

    public HolocronScreen(HolocronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 330;
        this.imageHeight = 256;
        // Maliyetleri kademeli dağıt (500-1500)
        String[] tier500 = {"force_push","force_pull","force_speed","force_jump","force_heal","force_sense","force_slow","force_stun","mind_trick"};
        String[] tier800 = {"force_cloak","force_deflection","force_life_mapping","force_persuasion","force_reflection","force_wave","group_heal"};
        String[] tier1000 = {"force_suppression","force_vision","force_rejuvenation","enhanced_reflexes","force_immunity","force_barrier","force_absorb","force_camouflage","force_stealth","force_link","force_empathy","force_guidance","force_guardianship"};
        String[] tier1200 = {"force_inspiration","force_valor","force_stamina","force_endurance","force_clarity","battle_meditation","force_projection","force_telepathy","force_tranquility","force_sanctuary","force_hearing","force_touch","force_light","force_silence","force_memory_recollection","force_guidance_of_the_blade"};
        String[] tier1500 = {"force_ghost_manifestation","force_inspired_healing","force_symbiosis","force_hymn","force_coalescence","force_relic_sense"};
        for (String id : tier500) abilityCosts.put(id, 500);
        for (String id : tier800) abilityCosts.put(id, 800);
        for (String id : tier1000) abilityCosts.put(id, 1000);
        for (String id : tier1200) abilityCosts.put(id, 1200);
        for (String id : tier1500) abilityCosts.put(id, 1500);

        rankCosts.put("padawan", 1000);
        rankCosts.put("jedi_knight", 2000);
        rankCosts.put("jedi_master", 3500);
    }

    @Override
    protected void init() {
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;
        minecraft.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
            slots = data.getAbilitySlots();
        });
        requestDataFromServer();
        if (state == ScreenState.MAIN) {
            startTime = System.currentTimeMillis();
        }
        updateScreen();
    }

    private void requestDataFromServer() {
        PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket("request", -1, new String[4], new String[0]));
    }

    private void updateScreen() {
        clearWidgets();
        buttonTooltips.clear();
        if (state == ScreenState.MAIN) {
            ImageButton rankButton = new ImageButton(x + 44, y + 195, 114, 23, 0, 0, 0, SELECT_RANK_TEXTURE, 114, 23,
                    b -> switchState(ScreenState.RANKS));
            addRenderableWidget(rankButton);
            buttonTooltips.put(rankButton, "Select Rank");

            ImageButton abilityButton = new ImageButton(x + 173, y + 195, 114, 23, 0, 0, 0, SELECT_ABILITY_TEXTURE, 114, 23,
                    b -> switchState(ScreenState.ABILITIES));
            addRenderableWidget(abilityButton);
            buttonTooltips.put(abilityButton, "Select Ability");

            addRenderableWidget(new ImageButton(x + 50, y + 12, 20, 20, 0, 0, 0, X_TEXTURE, 20, 20,
                    b -> minecraft.setScreen(null)));
        } else if (state == ScreenState.RANKS) {
            addRenderableWidget(new ImageButton(x + 103, y + 180, 121, 17, 0, 0, 0, UPDATE_RANK_TEXTURE, 121, 17,
                    b -> updateRank()));
            addRenderableWidget(new ImageButton(x + 44, y + 215, 75, 17, 0, 0, 0, BACK_TEXTURE, 75, 17,
                    b -> switchState(ScreenState.MAIN)));
            addRenderableWidget(new ImageButton(x + 50, y + 12, 20, 20, 0, 0, 0, X_TEXTURE, 20, 20,
                    b -> minecraft.setScreen(null)));
        } else if (state == ScreenState.ABILITIES) {
            for (int i = 0; i < abilities.size(); i++) {
                float buttonY = y + 38 + (i - scrollOffset) * 27;
                if (buttonY < y + 28 || buttonY > y + 109) continue;

                String ability = abilities.get(i);
                String displayAbility = getDisplayAbility(ability);
                Button abilityButton;

                switch (ability) {
                    case "force_push":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_PUSH_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_pull":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_PULL_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_speed":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_SPEED_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_jump":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_JUMP_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_cloak":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_CLOAK_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_deflection":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_DEFLECTION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_heal":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_HEAL_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_life_mapping":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_LIFE_MAPPING_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_persuasion":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_PERSUASION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_reflection":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_REFLECTION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_rejuvenation":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_REJUVENATION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_sense":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_SENSE_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_slow":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_SLOW_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_stun":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_STUN_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_suppression":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_SUPPRESSION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_vision":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_VISION_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "force_wave":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, FORCE_WAVE_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "group_heal":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, GROUP_HEAL_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    case "mind_trick":
                        abilityButton = new ImageButton(x + 113, (int)buttonY, 119, 26, 0, 0, 0, MIND_TRICK_TEXTURE, 119, 26, b -> assignAbility(ability));
                        break;
                    default:
                        abilityButton = new Button(x + 113, (int)buttonY, 100, 20,
                                new TextComponent(displayAbility), b -> assignAbility(ability));
                }
                // Pasif yetenekler tıklanamaz
                boolean passive = PASSIVE_ONLY.contains(ability);
                if (passive) {
                    abilityButton.active = false;
                }
                addRenderableWidget(abilityButton);
                // Tooltip metnini çok satırlı ve yerelleştirilmiş yap
                String keyName = getAbilityNameKey(ability);
                String keyDesc = getAbilityDescKey(ability);
                String title = new TranslatableComponent(keyName).getString();
                String desc = new TranslatableComponent(keyDesc).getString();
                String lockLine = passive
                        ? new TranslatableComponent("tooltip.pkg_tales_of_the_force_wars.passive").getString()
                        : minecraft.player.getCapability(JediDataProvider.JEDI_DATA)
                        .map(data -> data.isAbilityUnlocked(ability)
                                ? new TranslatableComponent("tooltip.pkg_tales_of_the_force_wars.unlocked").getString()
                                : new TranslatableComponent("tooltip.pkg_tales_of_the_force_wars.cost", abilityCosts.getOrDefault(ability, 1000)).getString())
                        .orElse("");
                buttonTooltips.put(abilityButton, title + "\n" + desc + "\n" + lockLine);

            }
            for (int i = 0; i < 4; i++) {
                int slotIndex = i;
                ImageButton slotButton = new ImageButton(x + 123 + i * 22, y + 205, 17, 17, 0, 0, 0,
                        ABILITY_KEY_TEXTURE, 17, 17, b -> selectSlot(slotIndex));
                addRenderableWidget(slotButton);
                minecraft.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                    String ability = slots[slotIndex];
                    String displayName = ability != null && !ability.isEmpty() ? getDisplayAbility(ability) : "";
                    buttonTooltips.put(slotButton, "Ability Slot " + (slotIndex + 1) + (displayName.isEmpty() ? "" : ": " + displayName));
                });
            }
            addRenderableWidget(new ImageButton(x + 50, y + 12, 20, 20, 0, 0, 0, X_TEXTURE, 20, 20,
                    b -> minecraft.setScreen(null)));
            addRenderableWidget(new ImageButton(x + 44, y + 215, 75, 17, 0, 0, 0, BACK_TEXTURE, 75, 17,
                    b -> switchState(ScreenState.MAIN)));
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        ResourceLocation texture = switch (state) {
            case MAIN -> MAIN_TEXTURE;
            case ABILITIES -> ABILITY_TEXTURE;
            case RANKS -> RANK_TEXTURE;
        };
        RenderSystem.setShaderTexture(0, texture);
        if (state == ScreenState.MAIN && startTime != -1) {
            long currentTime = System.currentTimeMillis();
            float duration = 2000f; // 2 saniye
            float progress = Math.min(1f, (currentTime - startTime) / duration);
            alpha = progress;
            if (progress >= 1f) {
                startTime = -1;
            }
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        }
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        if (state == ScreenState.MAIN && startTime != -1) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        if (state == ScreenState.RANKS) {
            minecraft.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                slots = data.getAbilitySlots();
                String rank = data.getStage() != null && !data.getStage().isEmpty() ? data.getStage() : "youngling";
                ResourceLocation rankTexture = switch (rank.toLowerCase()) {
                    case "youngling" -> YOUNGLING_TEXTURE;
                    case "padawan" -> PADAWAN_TEXTURE;
                    case "jedi_knight" -> JEDI_KNIGHT_TEXTURE;
                    case "jedi_master" -> JEDI_MASTER_TEXTURE;
                    default -> YOUNGLING_TEXTURE;
                };
                RenderSystem.setShaderTexture(0, rankTexture);
                blit(poseStack, x + (imageWidth - 88) / 2, y + 110, 0, 0, 88, 26, 88, 26);

                double forceValue = data.getForceValue();
                String forceText = "Force Value: " + String.format("%.2f", forceValue);
                int forceWidth = font.width(forceText);
                int forceX = x + (imageWidth - forceWidth) / 2;
                int forceY = 80;
                // Siyah outline
                font.draw(poseStack, forceText, forceX - 1, forceY, 0x000000); // Sol
                font.draw(poseStack, forceText, forceX + 1, forceY, 0x000000); // Sağ
                font.draw(poseStack, forceText, forceX, forceY - 1, 0x000000); // Üst
                font.draw(poseStack, forceText, forceX, forceY + 1, 0x000000); // Alt
                font.draw(poseStack, forceText, forceX - 1, forceY - 1, 0x000000); // Sol-Üst
                font.draw(poseStack, forceText, forceX + 1, forceY - 1, 0x000000); // Sağ-Üst
                font.draw(poseStack, forceText, forceX - 1, forceY + 1, 0x000000); // Sol-Alt
                font.draw(poseStack, forceText, forceX + 1, forceY + 1, 0x000000); // Sağ-Alt
                // Yeşil metin
                font.draw(poseStack, forceText, forceX, forceY, 0x00FF00);

                if (errorMessage != null) {
                    int errorWidth = font.width(errorMessage);
                    int errorX = x + (imageWidth - errorWidth) / 2;
                    int errorY = 100;
                    // Siyah outline
                    font.draw(poseStack, errorMessage, errorX - 1, errorY, 0x000000); // Sol
                    font.draw(poseStack, errorMessage, errorX + 1, errorY, 0x000000); // Sağ
                    font.draw(poseStack, errorMessage, errorX, errorY - 1, 0x000000); // Üst
                    font.draw(poseStack, errorMessage, errorX, errorY + 1, 0x000000); // Alt
                    font.draw(poseStack, errorMessage, errorX - 1, errorY - 1, 0x000000); // Sol-Üst
                    font.draw(poseStack, errorMessage, errorX + 1, errorY - 1, 0x000000); // Sağ-Üst
                    font.draw(poseStack, errorMessage, errorX - 1, errorY + 1, 0x000000); // Sol-Alt
                    font.draw(poseStack, errorMessage, errorX + 1, errorY + 1, 0x000000); // Sağ-Alt
                    // Kırmızı metin
                    font.draw(poseStack, errorMessage, errorX, errorY, 0xFF5555);
                }
            });
        } else if (state == ScreenState.ABILITIES) {
            if (abilities.size() > 3) {
                RenderSystem.setShaderTexture(0, SCROLL_TEXTURE);
                blit(poseStack, x + 267, y + 6, 0, 0, 16, 225, 16, 225);
                int maxOffset = abilities.size() - 3;
                int barY = 8 + (int)(scrollOffset * (225 - 16) / maxOffset);
                RenderSystem.setShaderTexture(0, SCROLL_HITBOX_TEXTURE);
                blit(poseStack, x + 271, y + barY, 0, 0, 8, 16, 8, 16);
            }
            if (errorMessage != null) {
                int errorWidth = font.width(errorMessage);
                int errorX = x + (imageWidth - errorWidth) / 2;
                int errorY = 10;
                // Siyah outline
                font.draw(poseStack, errorMessage, errorX - 1, errorY, 0x000000); // Sol
                font.draw(poseStack, errorMessage, errorX + 1, errorY, 0x000000); // Sağ
                font.draw(poseStack, errorMessage, errorX, errorY - 1, 0x000000); // Üst
                font.draw(poseStack, errorMessage, errorX, errorY + 1, 0x000000); // Alt
                font.draw(poseStack, errorMessage, errorX - 1, errorY - 1, 0x000000); // Sol-Üst
                font.draw(poseStack, errorMessage, errorX + 1, errorY - 1, 0x000000); // Sağ-Üst
                font.draw(poseStack, errorMessage, errorX - 1, errorY + 1, 0x000000); // Sol-Alt
                font.draw(poseStack, errorMessage, errorX + 1, errorY + 1, 0x000000); // Sağ-Alt
                // Kırmızı metin
                font.draw(poseStack, errorMessage, errorX, errorY, 0xFF5555);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (state == ScreenState.ABILITIES && abilities.size() > 3) {
            float lerpSpeed = 0.1f;
            scrollOffset += (targetScrollOffset - scrollOffset) * lerpSpeed;
            scrollOffset = Math.max(0, Math.min(abilities.size() - 3, scrollOffset));
            updateScreen();
        }

        renderBackground(poseStack);
        if (state == ScreenState.MAIN && startTime != -1) {
            long currentTime = System.currentTimeMillis();
            float duration = 2000f; // 2 saniye
            float progress = Math.min(1f, (currentTime - startTime) / duration);
            alpha = progress;
            if (progress >= 1f) {
                startTime = -1;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        if (state == ScreenState.ABILITIES || state == ScreenState.MAIN) {
            for (Map.Entry<Button, String> entry : buttonTooltips.entrySet()) {
                Button button = entry.getKey();
                String tooltip = entry.getValue();
                if (button.isMouseOver(mouseX, mouseY)) {
                    java.util.List<Component> lines = new java.util.ArrayList<>();
                    for (String line : tooltip.split("\\n")) {
                        lines.add(new TextComponent(line));
                    }
                    this.renderComponentTooltip(poseStack, lines, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (state == ScreenState.ABILITIES && (startTime == -1 || System.currentTimeMillis() - startTime >= 2000)) {
            if (delta > 0) targetScrollOffset = Math.max(0, targetScrollOffset - 1);
            else if (delta < 0) targetScrollOffset = Math.min(abilities.size() - 3, targetScrollOffset + 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (startTime != -1 && System.currentTimeMillis() - startTime < 2000) {
            return true; // 2 saniye etkileşimi engelle
        }
        if (state == ScreenState.ABILITIES && abilities.size() > 3) {
            int scrollX = x + 267;
            int scrollY = y + 6;
            if (mouseX >= scrollX && mouseX <= scrollX + 16 && mouseY >= scrollY && mouseY <= scrollY + 225) {
                isDraggingScrollBar = true;
                updateScrollOffset(mouseY - y);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (state == ScreenState.ABILITIES && isDraggingScrollBar && (startTime == -1 || System.currentTimeMillis() - startTime >= 2000)) {
            updateScrollOffset(mouseY - y);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDraggingScrollBar) {
            isDraggingScrollBar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateScrollOffset(double mouseY) {
        int scrollY = 6;
        int maxOffset = abilities.size() - 3;
        double relativeY = Math.max(0, Math.min(225, mouseY - scrollY));
        double normalized = relativeY / 225.0;
        targetScrollOffset = (float)(normalized * maxOffset);
        targetScrollOffset = Math.max(0, Math.min(maxOffset, targetScrollOffset));
    }

    private String getDisplayAbility(String ability) {
        return new TranslatableComponent(getAbilityNameKey(ability)).getString();
    }

    private String getAbilityNameKey(String id) {
        return "ability." + PKGTalesoftheForceWars.MODID + "." + id + ".name";
    }
    private String getAbilityDescKey(String id) {
        return "ability." + PKGTalesoftheForceWars.MODID + "." + id + ".desc";
    }

    // Basit yardımcı: metnin ilk harfini büyük yap
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void switchState(ScreenState newState) {
        state = newState;
        scrollOffset = 0;
        targetScrollOffset = 0;
        errorMessage = null;
        if (newState == ScreenState.RANKS || newState == ScreenState.ABILITIES) {
            requestDataFromServer();
        }
        updateScreen();
    }

    private void assignAbility(String ability) {
        if (selectedSlot == -1) return;
        if (PASSIVE_ONLY.contains(ability)) {
            errorMessage = "Passive ability cannot be slotted";
            selectedSlot = -1;
            updateScreen();
            return;
        }
        minecraft.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
            if (data.isAbilityUnlocked(ability)) {
                slots[selectedSlot] = ability; // Kayıt adı olarak "force_jump" gibi kalacak
                data.setAbilitySlot(selectedSlot, ability);
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new AbilitySlotPacket(selectedSlot, ability));
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(
                        data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
                errorMessage = null;
            } else {
                double forceValue = data.getForceValue();
                int cost = abilityCosts.getOrDefault(ability, 0);
                if (forceValue >= cost) {
                    slots[selectedSlot] = ability; // Kayıt adı olarak "force_jump" gibi kalacak
                    data.setAbilitySlot(selectedSlot, ability);
                    data.unlockAbility(ability);
                    data.setForceValue(forceValue - cost);
                    PKGTalesoftheForceWars.CHANNEL.sendToServer(new AbilitySlotPacket(selectedSlot, ability));
                    PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(
                            data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
                    errorMessage = null;
                } else {
                    errorMessage = "Insufficient Force Value: " + cost + " required";
                }
            }
        });
        selectedSlot = -1;
        updateScreen();
    }

    private void selectSlot(int slotIndex) {
        selectedSlot = slotIndex;
        updateScreen();
    }

    private String getNextRank(String currentRank) {
        switch (currentRank.toLowerCase()) {
            case "youngling": return "padawan";
            case "padawan": return "jedi_knight";
            case "jedi_knight": return "jedi_master";
            default: return null;
        }
    }

    private void updateRank() {
        minecraft.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
            String currentRank = data.getStage() != null && !data.getStage().isEmpty() ? data.getStage() : "youngling";
            double forceValue = data.getForceValue();
            String nextRank = getNextRank(currentRank);

            if (nextRank != null) {
                int cost = rankCosts.getOrDefault(nextRank, Integer.MAX_VALUE);
                if (forceValue >= cost) {
                    data.setStage(nextRank);
                    data.setForceValue(forceValue - cost);
                    String[] currentSlots = data.getAbilitySlots();
                    if (currentSlots == null) currentSlots = new String[4];
                    PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(
                            nextRank, data.getForceValue(), currentSlots, data.getUnlockedAbilities()));
                    errorMessage = null;
                } else {
                    errorMessage = "Insufficient Force Value: " + cost + " required (" + capitalize(nextRank) + ")";
                }
            } else {
                errorMessage = "Maximum rank reached";
            }
        });
        updateScreen();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}