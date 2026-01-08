package de.example.serverrules;


import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;


@EventBusSubscriber(modid = ServerRulesMod.MOD_ID)
public class SleepHandler {


private static final double REQUIRED_SLEEP_RATIO = 0.5;


@SubscribeEvent
public static void onTick(LevelTickEvent.Post event) {
if (!(event.getLevel() instanceof ServerLevel level)) return;


long sleeping = level.players().stream().filter(p -> p.isSleeping()).count();
int total = level.players().size();


if (total > 0 && (double) sleeping / total >= REQUIRED_SLEEP_RATIO) {
level.setDayTime(level.getDayTime() + 24000);
level.players().forEach(p -> p.stopSleepInBed(false, false));
}
}
}
