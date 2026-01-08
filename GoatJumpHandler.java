package de.example.serverrules;


import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingJumpEvent;


@EventBusSubscriber(modid = ServerRulesMod.MOD_ID)
public class GoatJumpHandler {


private static final double MAX_JUMP_Y = 0.8D; // ~3 Blöcke


@SubscribeEvent
public static void onJump(LivingJumpEvent event) {
if (event.getEntity() instanceof Goat goat) {
Vec3 motion = goat.getDeltaMovement();
if (motion.y > MAX_JUMP_Y) {
goat.setDeltaMovement(motion.x, MAX_JUMP_Y, motion.z);
}
}
}
}
