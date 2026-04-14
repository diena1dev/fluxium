package dev.diena.fluxium.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

/**
 * Factory function that receives a configured [Item.Properties] and returns
 * an [Item] instance. Use an object expression to override methods inline:
 * ```kotlin
 * factory = { props -> object : Item(props) { override fun isFoil(...) = true } }
 * ```
 */
typealias ItemFactory = (props: Item.Properties) -> Item

/**
 * Lambda invoked when a player right-clicks with this item in the air.
 * @param level the current level
 * @param player the player performing the action
 * @param hand  the hand holding the item
 * @param stack the item stack being used
 * @return an [InteractionResultHolder] wrapping the (possibly modified) stack
 */
typealias UseAction = (
    level: Level,
    player: Player,
    hand: InteractionHand,
    stack: ItemStack,
) -> InteractionResultHolder<ItemStack>

/**
 * Lambda invoked when a player right-clicks a block with this item.
 * @param context block-use context (position, direction, hit, level, player, etc.)
 * @return an [InteractionResult] indicating how the interaction was consumed
 */
typealias UseOnAction = (context: UseOnContext) -> InteractionResult

/**
 * Lambda invoked when a player right-clicks a living entity with this item.
 * @param stack  the item stack being used
 * @param player the player performing the action
 * @param target the entity being interacted with
 * @param hand   the hand holding the item
 * @return an [InteractionResult] indicating how the interaction was consumed
 */
typealias InteractEntityAction = (
    stack: ItemStack,
    player: Player,
    target: LivingEntity,
    hand: InteractionHand,
) -> InteractionResult

/**
 * An [Item] subclass that delegates interaction events to optional lambdas,
 * allowing per-registration behaviour without needing a dedicated subclass.
 *
 * Usage inside [CustomItems]:
 * ```kotlin
 * val MY_ITEM by item("my_item",
 *     onUse = { level, player, hand, stack ->
 *         player.sendSystemMessage(Component.literal("Used!"))
 *         InteractionResultHolder.success(stack)
 *     }
 * ) { stacksTo(1) }
 * ```
 */
class FluxiumItem(
    properties: Properties,
    private val onUse: UseAction? = null,
    private val onUseOn: UseOnAction? = null,
    private val onInteractEntity: InteractEntityAction? = null,
) : Item(properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> =
        onUse?.invoke(level, player, hand, player.getItemInHand(hand))
            ?: super.use(level, player, hand)

    override fun useOn(context: UseOnContext): InteractionResult =
        onUseOn?.invoke(context)
            ?: super.useOn(context)

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        target: LivingEntity,
        hand: InteractionHand,
    ): InteractionResult =
        onInteractEntity?.invoke(stack, player, target, hand)
            ?: super.interactLivingEntity(stack, player, target, hand)
}


