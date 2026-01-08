package de.example.serverrules;


import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


@EventBusSubscriber(modid = ServerRulesMod.MOD_ID)
public class DayLengthHandler {


private static final int DAY_MULTIPLIER = 2; // 2x längere Tage


@SubscribeEvent
public static void onTick(LevelTickEvent.Pre event) {
if (!(event.getLevel() instanceof ServerLevel level)) return;


if (level.getGameTime() % DAY_MULTIPLIER != 0) {
level.setDayTime(level.getDayTime() - 1);
}
}
}
