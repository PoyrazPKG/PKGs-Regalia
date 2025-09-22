package pkg_tales_of_the_force_wars.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeRegistry {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, PKGTalesoftheForceWars.MODID);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static final RegistryObject<Attribute> FORCE_JUMP = ATTRIBUTES.register("force_jump", () ->
            new RangedAttribute("Force Jump", 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> FORCE_JUMP_STRENGTH = ATTRIBUTES.register("force_jump_strength", () ->
            new RangedAttribute("Force Jump Strength", 0.0D, 0.0D, 15.0D).setSyncable(true));

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> {
            e.add(entity, FORCE_JUMP.get());
            e.add(entity, FORCE_JUMP_STRENGTH.get());
        });
    }
}