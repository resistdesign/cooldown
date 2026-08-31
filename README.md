# COOLDOWN

**Hardcore with a Reset!**

COOLDOWN makes death matter without making it permanent.

When you die, you are done playing on that server until your real-time cooldown expires. Then you get another life.

## The rule

> Die once. Cool down. Come back.

- No instant respawn.
- No permanent character deletion.
- **No inventory or XP loss from death.**
- The lockout survives reconnects and server restarts.
- Time passes while the server is offline.
- Server owners choose the cooldown duration.

## Install

Download the latest `cooldown-*.jar` from **GitHub Releases**. Development builds are also attached to successful GitHub Actions builds as `cooldown-server-jar`.

### Paper / Paper-compatible servers

1. Stop the server.
2. Put `cooldown-*.jar` in the server's `plugins/` folder.
3. Start the server.

That's it. **Players install nothing.**

COOLDOWN's Paper build uses only server-side behavior and the same JAR is also the Fabric mod.

### Fabric servers

1. Make sure the server is running Fabric Loader for Minecraft 26.2 and has Fabric API installed.
2. Put `cooldown-*.jar` in the server's `mods/` folder.
3. Start the server.

Again, **players install nothing.**

### Vanilla server.jar

Vanilla Minecraft cannot load mods or plugins by itself. Convert the server to **Paper** or **Fabric**, then use the matching instructions above. Your world can remain the same; back it up before changing server software.

## Compatibility

| Server | Install location | Extra requirement | Client install |
| --- | --- | --- | --- |
| Paper 26.2 | `plugins/` | None | **No** |
| Fabric 26.2 | `mods/` | Fabric API | **No** |
| Vanilla | — | Switch to Paper or Fabric | — |

The **same COOLDOWN JAR** is used for Paper and Fabric.

## Default

The default cooldown is **20 minutes**.

COOLDOWN creates its config on first launch:

**Fabric:** `config/cooldown.properties`

**Paper:** `plugins/COOLDOWN/cooldown.properties`

```properties
cooldown-seconds=1200
```

Examples:

| Experience | Value |
| --- | ---: |
| 20 minutes | `1200` |
| 1 hour | `3600` |
| 5 hours | `18000` |
| 24 hours | `86400` |

Restart the server after changing the configuration.

## What COOLDOWN changes

On death COOLDOWN:

1. preserves inventory and XP;
2. records the player's cooldown using real-world wall-clock time;
3. disconnects the player;
4. rejects reconnects until the cooldown expires.

It does **not** globally enable Minecraft's `keep_inventory` gamerule.

## Platform

- Minecraft Java Edition 26.2
- Java 25
- Paper 26.2 or Fabric Loader 0.19.3+
- Fabric API required only on Fabric servers
- Dedicated-server clients do **not** need COOLDOWN

## Building

```bash
gradle build
```

The installable JAR is emitted under `build/libs/`. CI also uploads it as the `cooldown-server-jar` artifact, and version tags automatically create GitHub Releases with the JAR attached.

## Status

**0.1.0 — early release.**
