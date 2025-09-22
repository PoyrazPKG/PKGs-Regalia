package pkg_tales_of_the_force_wars.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.block.entity.HolocronBlockEntity;

public class PKGTalesoftheForceWarsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, PKGTalesoftheForceWars.MODID);

    public static final RegistryObject<BlockEntityType<HolocronBlockEntity>> HOLOCRON = REGISTRY.register("holocron",
            () -> BlockEntityType.Builder.of(HolocronBlockEntity::new, PKGTalesoftheForceWarsBlocks.HOLOCRON_BLOCK.get()).build(null));

    private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
        return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }
}