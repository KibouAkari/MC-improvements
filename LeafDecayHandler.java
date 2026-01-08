package de.example.serverrules;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;


@EventBusSubscriber(modid = ServerRulesMod.MOD_ID)
public class LeafDecayHandler {


@SubscribeEvent
public static void onLogBreak(BlockEvent.BreakEvent event) {
if (!(event.getLevel() instanceof ServerLevel level)) return;
if (!event.getState().is(BlockTags.LOGS)) return;


BlockPos center = event.getPos();


for (BlockPos pos : BlockPos.betweenClosed(center.offset(-6, -6, -6), center.offset(6, 6, 6))) {
if (level.getBlockState(pos).is(BlockTags.LEAVES)) {
level.scheduleTick(pos, level.getBlockState(pos).getBlock(), 2);
}
}
}
}
