# COOLDOWN

**Hardcore with a Reset!**

COOLDOWN is a Minecraft server mod that makes death matter without making it permanent.

When you die, you are done playing on that server until your real-time cooldown expires. Then you get another life.

## The rule

> Die once. Cool down. Come back.

That is intentionally most of the game design.

- No instant respawn.
- No permanent character deletion.
- The lockout survives reconnects and server restarts.
- Time passes while the server is offline.
- Server owners choose the cooldown duration.

## Default

The default cooldown is **5 hours**.

On first launch COOLDOWN creates `config/cooldown.properties`:

```properties
cooldown-seconds=18000
```

Examples:

| Experience | Value |
| --- | ---: |
| 20 minutes | `1200` |
| 1 hour | `3600` |
| 5 hours | `18000` |
| 24 hours | `86400` |

Restart the server after changing the configuration.

## Platform

- Minecraft Java Edition 26.2
- Fabric Loader 0.19.3+
- Fabric API
- Java 25
- Server-side mechanics; install on the server

Single-player support is possible when the mod is installed in the local Fabric instance because Minecraft runs an integrated server.

## Development

The project follows the current Fabric 26.2 toolchain.

```bash
gradle build
```

The built mod is emitted under `build/libs/`.

## Status

**0.1.0 — core mechanic under development.**

The first milestone is deliberately narrow: detect a real player death, persist the lockout using wall-clock time, disconnect the player, and reject reconnects until the cooldown expires.
