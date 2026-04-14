package dev.diena.fluxium.item

import dev.diena.fluxium.Fluxium
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object CustomItems {
	val REGISTRY = DeferredRegister.createItems(Fluxium.ID)

	/**
	 * Registers an item under [name].
	 *
	 * Two override strategies are available — pick one per item:
	 * - **Object expression** (`factory`): supply a lambda that returns an anonymous
	 *   [Item] subclass, overriding whichever methods you need inline.
	 * - **Action lambdas** (`onUse`, `onUseOn`, `onInteractEntity`): quick per-event
	 *   callbacks without a dedicated subclass; backed automatically by [FluxiumItem].
	 *
	 * @param name             registry name (snake_case)
	 * @param factory          optional custom item constructor (object-expression pattern)
	 * @param onUse            called when the player right-clicks in the air
	 * @param onUseOn          called when the player right-clicks a block
	 * @param onInteractEntity called when the player right-clicks a living entity
	 * @param configure        receiver lambda applied to [Item.Properties]
	 */
	private fun item(
		name: String,
		factory: ItemFactory? = null,
		onUse: UseAction? = null,
		onUseOn: UseOnAction? = null,
		onInteractEntity: InteractEntityAction? = null,
		configure: Item.Properties.() -> Unit = {},
	) = REGISTRY.register(name) { ->
		val props = Item.Properties().apply(configure)
		when {
			factory != null -> factory(props)
			onUse != null || onUseOn != null || onInteractEntity != null ->
				FluxiumItem(props, onUse, onUseOn, onInteractEntity)
			else -> Item(props)
		}
	}

	val TEST_ITEM by item(
		name = "test_item",
		onUse = { level, player, _, stack ->
			if (!level.isClientSide) {
				player.sendSystemMessage(Component.literal("[Fluxium] test_item used by ${player.name.string}"))
			}
			InteractionResultHolder.success(stack)
		},
	) {
		stacksTo(32)
	}

	/**
	 * Demonstrates Kotlin object-expression overriding: the item always has an
	 * enchantment glint and displays a custom tooltip line, achieved entirely via
	 * an anonymous [Item] subclass passed to [factory] — no dedicated class needed.
	 */
	val GLINT_ITEM by item(
		name = "glint_item",
		factory = { props ->
			object : Item(props) {
				override fun isFoil(stack: ItemStack) = true

				override fun appendHoverText(
					stack: ItemStack,
					context: TooltipContext,
					tooltipComponents: MutableList<Component>,
					tooltipFlag: TooltipFlag,
				) {
					tooltipComponents += Component.literal("Always shiny!").withStyle(ChatFormatting.AQUA)
				}
			}
		},
	) {
		stacksTo(1)
		rarity(Rarity.EPIC)
	}
}
