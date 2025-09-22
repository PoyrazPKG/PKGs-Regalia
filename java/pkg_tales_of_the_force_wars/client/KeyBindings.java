package pkg_tales_of_the_force_wars.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping LIGHTSABER_IGNITE = new KeyMapping("Lightsaber Ignite", GLFW.GLFW_KEY_R, "PKG Tales of the Force Wars");
    public static final KeyMapping ABILITY_SLOT_1 = new KeyMapping("Ability 1", GLFW.GLFW_KEY_G, "PKG Tales of the Force Wars");
    public static final KeyMapping ABILITY_SLOT_2 = new KeyMapping("Ability 2", GLFW.GLFW_KEY_H, "PKG Tales of the Force Wars");
    public static final KeyMapping ABILITY_SLOT_3 = new KeyMapping("Ability 3", GLFW.GLFW_KEY_I, "PKG Tales of the Force Wars");
    public static final KeyMapping ABILITY_SLOT_4 = new KeyMapping("Ability 4", GLFW.GLFW_KEY_J, "PKG Tales of the Force Wars");

    public static void register() {
        ClientRegistry.registerKeyBinding(LIGHTSABER_IGNITE);
        ClientRegistry.registerKeyBinding(ABILITY_SLOT_1);
        ClientRegistry.registerKeyBinding(ABILITY_SLOT_2);
        ClientRegistry.registerKeyBinding(ABILITY_SLOT_3);
        ClientRegistry.registerKeyBinding(ABILITY_SLOT_4);
    }
}