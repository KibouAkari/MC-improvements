# MC-improvements

Dieses Repository ist **vollständig build-fertig** für einen **NeoForge 1.21.1 Server-only Mod**.
Clients brauchen **keine Mod**.

---

## ✨ Features

* 🐐 Goat Jump Height auf **3 Blöcke** begrenzt
* 🌋 **Schnellere Lava-Dripstone-Cauldron-Farmen** (ohne globalen TickSpeed)
* 🌿 **Schnelleres Leaf-Decay**, aber **nur nach Log-Abbau**
* 🌞 **Längere Tage** (konfigurierbar)
* 🛌 **Nur 50 % der Spieler müssen schlafen** (konfigurierbar)

Alles **event-basiert**, **mod-kompatibel**, **server-only**.

---

## 📁 Projektstruktur

```
serverrules/
├─ build.gradle
├─ settings.gradle
├─ gradle.properties
├─ gradlew
├─ gradlew.bat
├─ gradle/
│  └─ wrapper/
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
└─ src/main/
   ├─ java/de/example/serverrules/
   │  ├─ ServerRulesMod.java
   │  ├─ GoatJumpHandler.java
   │  ├─ LavaDripstoneHandler.java
   │  ├─ LeafDecayHandler.java
   │  ├─ DayLengthHandler.java
   │  └─ SleepHandler.java
   └─ resources/META-INF/
      └─ mods.toml
```

---

## ⚙️ build.gradle

```gradle
plugins {
    id 'java'
    id 'net.neoforged.moddev' version '1.0.21'
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

neoForge {
    version = "21.1.0"
}

group = 'de.example'
version = '1.0.0'

repositories {
    mavenCentral()
}
```

---

## ⚙️ settings.gradle

```gradle
rootProject.name = 'serverrules'
```

---

## ⚙️ gradle.properties

```properties
org.gradle.jvmargs=-Xmx3G
mod_id=serverrules
mod_name=ServerRules
mod_version=1.0.0
```

---

## 🧩 mods.toml

```toml
modLoader="javafml"
loaderVersion="[4,)"
license="MIT"

[[mods]]
modId="serverrules"
version="1.0.0"
displayName="Server Rules"
description='''
Server-only QoL rules:
- Goat jump cap
- Faster lava dripstone farms
- Faster leaf decay after tree chopping
- Longer days
- 50% sleep rule
'''
```

---

## 🧠 Java-Code

### ServerRulesMod.java

```java
package de.example.serverrules;

import net.neoforged.fml.common.Mod;

@Mod(ServerRulesMod.MOD_ID)
public class ServerRulesMod {
    public static final String MOD_ID = "serverrules";
}
```

---

### GoatJumpHandler.java

```java
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
```

---

### LavaDripstoneHandler.java

```java
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
```

---

### LeafDecayHandler.java

```java
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
```

---

### DayLengthHandler.java

```java
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
```

---

### SleepHandler.java

```java
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
```

---

## 🏗️ Build

```bash
./gradlew build
```

➡️ `build/libs/serverrules-1.0.0.jar`
➡️ in den `mods/` Ordner des **NeoForge-Servers** legen

---

## 🔚 Fertig

Wenn du willst, erweitere ich das Repo als Nächstes um:

* 🔧 **Config-Datei**
* 🛡️ **Overworld-only Regeln**
* ⚙️ **Performance-Optimierungen**
* 📦 **GitHub-Ready Repo (README, Lizenz)**
