package design.resist.cooldown.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Preserve inventory, XP, and score when Minecraft creates the post-death
 * ServerPlayer instance after the cooldown has expired.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@WrapOperation(
			method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
	private Object cooldown$keepInventoryOnRespawn(GameRules gameRules, GameRule<?> rule, Operation<Object> original) {
		if (rule == GameRules.KEEP_INVENTORY) {
			return true;
		}
		return original.call(gameRules, rule);
	}
}
