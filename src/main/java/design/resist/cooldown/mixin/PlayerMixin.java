package design.resist.cooldown.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * COOLDOWN makes death cost time, not the player's gear.
 *
 * Force vanilla's keep-inventory decision to true for player item and XP drops
 * without changing the world's keep_inventory gamerule.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {
	@WrapOperation(
			method = "dropEquipment(Lnet/minecraft/server/level/ServerLevel;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
	private Object cooldown$keepInventoryForItemDrops(GameRules gameRules, GameRule<?> rule, Operation<Object> original) {
		return cooldown$overrideKeepInventory(gameRules, rule, original);
	}

	@WrapOperation(
			method = "getBaseExperienceReward(Lnet/minecraft/server/level/ServerLevel;)I",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
	private Object cooldown$keepInventoryForXpDrops(GameRules gameRules, GameRule<?> rule, Operation<Object> original) {
		return cooldown$overrideKeepInventory(gameRules, rule, original);
	}

	private Object cooldown$overrideKeepInventory(GameRules gameRules, GameRule<?> rule, Operation<Object> original) {
		if (rule == GameRules.KEEP_INVENTORY) {
			return true;
		}
		return original.call(gameRules, rule);
	}
}
