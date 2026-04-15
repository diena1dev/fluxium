package dev.diena.fluxium.item

import dev.diena.fluxium.Fluxium
import dev.diena.fluxium.block.CustomBlocks
import dev.diena.fluxium.component.ModDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister

object CustomItems {
	val REGISTRY = DeferredRegister.createItems(Fluxium.ID)

	/**
	 * Registers an item under [name].
	 *
	 * Two override strategies are available — and they compose:
	 * - **Action lambdas** (`onUse`, `onUseOn`, `onInteractEntity`): per-event
	 *   callbacks backed by [FluxiumItem].
	 * - **Object expression** (`factory`): receives props + action lambdas, expected
	 *   to subclass [FluxiumItem] so actions are preserved alongside extra overrides.
	 *
	 * @param name             registry name (snake_case)
	 * @param factory          optional constructor — subclass [FluxiumItem] to keep actions
	 * @param onUse            called when the player right-clicks in the air
	 * @param onUseOn          called when the player right-clicks a block
	 * @param onInteractEntity called when the player right-clicks a living entity
	 * @param onHurtEntity     called when the player left-clicks a living entity
	 * @param configure        receiver lambda applied to [Item.Properties]
	 */
	internal fun item(
		name: String,
		factory: ItemFactory? = null,
		onUse: UseAction? = null,
		onUseOn: UseOnAction? = null,
		onInteractEntity: InteractEntityAction? = null,
		onHurtEntity: HurtEntityAction? = null,
		configure: Item.Properties.() -> Unit = {},
	) = REGISTRY.register(name) { ->
		val props = Item.Properties().apply(configure)
		when {
			factory != null -> factory(props, onUse, onUseOn, onInteractEntity, onHurtEntity)
			onUse != null || onUseOn != null || onInteractEntity != null || onHurtEntity != null ->
				FluxiumItem(
					props,
					onUse,
					onUseOn,
					onInteractEntity,
					onHurtEntity
				)
			else -> Item(props)
		}
	}

	val TEST_ITEM = item(
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
	val GLINT_ITEM = item(
		name = "glint_item",
		factory = { props, onUse, onUseOn, onInteractEntity, onHurtEntity ->
			object : FluxiumItem(props, onUse, onUseOn, onInteractEntity, onHurtEntity) {
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

	val FLUXITE_ATOMIZER = item(
		"fluxite_atomizer",
		onHurtEntity = onHurtEntity@{ _, entity, player ->

			val player: Player = player as? Player ?: return@onHurtEntity false
			val level = player.level() as? ServerLevel ?: return@onHurtEntity false

			player.displayClientMessage(
				Component.literal("Atomized ").withStyle(ChatFormatting.GRAY)
					.append(entity.displayName?.copy()?.withStyle(ChatFormatting.LIGHT_PURPLE) ?: Component.empty()),
				true
			)
			level.playSound(
				null,
				entity.x,
				entity.y,
				entity.z,
				SoundEvents.ANVIL_LAND,
				SoundSource.PLAYERS,
				0.5f,
				10.9f
			)
			level.sendParticles(
				ParticleTypes.PORTAL,
				entity.x,
				entity.y + 0.5,
				entity.z,
				30,
				1.0,
				1.0,
				1.0,
				0.0
			)
			level.sendParticles(
				ParticleTypes.ENCHANTED_HIT,
				entity.x,
				entity.y + 0.5,
				entity.z,
				10,
				0.5,
				0.5,
				0.5,
				0.0)

			entity.remove(Entity.RemovalReason.DISCARDED)

			// return true because the entity is now *gone*
			true

		}
	)

	/** Blocks that the block mover is allowed to pick up. Lazy to avoid resolving deferred holders at class init. */
	private val MOVABLE_BLOCKS: Set<Block> by lazy {
		setOf(
			CustomBlocks.EXAMPLE_BLOCK
		)
	}

	/** Helper to avoid overload ambiguity between DataComponentType and Supplier overloads. */
	private val STORED_BLOCK: DataComponentType<BlockState> get() = ModDataComponents.STORED_BLOCK_STATE

	// TODO: standardized error messages, maybe a custom way of communicating to the player
	val FLUXITE_MANIPULATOR = item(
		name = "fluxite_manipulator",
		onUseOn = onUseOn@{ context ->
			val level = context.level
			val player = context.player ?: return@onUseOn InteractionResult.FAIL
			val stack = context.itemInHand

			val stored = stack.get(STORED_BLOCK)

			if (stored == null) {
				// pickup
				val clickedPos = context.clickedPos
				val clickedState = level.getBlockState(clickedPos)

				if (clickedState.block !in MOVABLE_BLOCKS) {
					player.displayClientMessage(
						Component.literal("Invalid Object"), true
					)
					return@onUseOn InteractionResult.FAIL
				}

				stack.set(STORED_BLOCK, clickedState)
				level.removeBlock(clickedPos, false)

				val blockName = clickedState.block.name.string
				player.displayClientMessage(
					Component.literal("Picked up ").withStyle(ChatFormatting.GRAY)
						.append(Component.literal(blockName).withStyle(ChatFormatting.LIGHT_PURPLE)),
					true
				)

				player.playSound(SoundType.AMETHYST.breakSound)
				InteractionResult.SUCCESS
			} else {
				// place
				val placePos = context.clickedPos.relative(context.clickedFace)

				if (!level.getBlockState(placePos).canBeReplaced()) {
					player.displayClientMessage(
						Component.literal("Invalid Position").withStyle(ChatFormatting.GRAY),
						true
					)
					return@onUseOn InteractionResult.FAIL
				}

				level.setBlockAndUpdate(placePos, stored)
				stack.remove(STORED_BLOCK)

				val blockName = stored.block.name.string
				player.displayClientMessage(
					Component.literal("Placed ").withStyle(ChatFormatting.GRAY)
						.append(Component.literal(blockName).withStyle(ChatFormatting.LIGHT_PURPLE)),
					true
				)

				player.playSound(SoundType.AMETHYST_CLUSTER.placeSound)
				InteractionResult.SUCCESS
			}
		},
		factory = { props, onUse, onUseOn, onInteractEntity, onHurtEntity ->
			object : FluxiumItem(props, onUse, onUseOn, onInteractEntity, onHurtEntity) {
				//override fun isFoil(stack: ItemStack): Boolean =
				//	stack.has(STORED_BLOCK)

				override fun appendHoverText(
					stack: ItemStack,
					context: TooltipContext,
					tooltipComponents: MutableList<Component>,
					tooltipFlag: TooltipFlag,
				) {
					val stored = stack.get(STORED_BLOCK)
					if (stored != null) {
						val key = BuiltInRegistries.BLOCK.getKey(stored.block)
						tooltipComponents += Component.literal("Holding $key")
							.withStyle(ChatFormatting.LIGHT_PURPLE)
					} else {
						tooltipComponents += Component.literal("Empty")
							.withStyle(ChatFormatting.GRAY)
					}
				}
			}
		},
	) {
		stacksTo(1)
		rarity(Rarity.UNCOMMON)
	}
}

