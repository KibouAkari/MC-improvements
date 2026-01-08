package de.example.serverrules;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


@EventBusSubscriber(modid = ServerRulesMod.MOD_ID)
public class LavaDripstoneHandler {


@SubscribeEvent
public static void onLevelTick(LevelTickEvent.Post event) {
if (!(event.getLevel() instanceof ServerLevel level)) return;
if (level.random.nextFloat() > 0.25F) return;


BlockPos pos = level.getSharedSpawnPos().offset(
level.random.nextInt(64) - 32,
-5,
level.random.nextInt(64) - 32
);


if (level.getBlockState(pos).is(Blocks.CAULDRON)
&& level.getBlockState(pos.above()).is(Blocks.POINTED_DRIPSTONE)
&& level.getBlockState(pos.above(2)).is(Blocks.LAVA)) {
level.setBlock(pos, Blocks.LAVA_CAULDRON.defaultBlockState(), 3);
}
}
}
