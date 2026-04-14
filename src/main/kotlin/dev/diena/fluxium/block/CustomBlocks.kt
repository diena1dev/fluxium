package dev.diena.fluxium.block

import dev.diena.fluxium.Fluxium
import dev.diena.fluxium.item.CustomItems
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object CustomBlocks {
	val REGISTRY = DeferredRegister.createBlocks(Fluxium.ID)

	/**
	 * Registers a block under [name].
	 *
	 * Two override strategies (mirrors [dev.diena.fluxium.item.CustomItems]):
	 * - Default: plain [Block] with [configure] applied to its properties.
	 * - **Object expression** ([factory]): anonymous [Block] subclass inline.
	 *
	 * Set [withItem] to `true` to automatically register a companion [net.minecraft.world.item.BlockItem]
	 * in [CustomItems.REGISTRY]; configure its [Item.Properties] via [itemConfigure].
	 *
	 * @param name          registry name (snake_case)
	 * @param withItem      when `true`, registers a parallel BlockItem
	 * @param itemConfigure receiver lambda applied to the BlockItem's [Item.Properties]
	 * @param factory       optional custom block constructor (object-expression pattern)
	 * @param configure     receiver lambda applied to [BlockBehaviour.Properties]
	 */
	private fun block(
		name: String,
		withItem: Boolean = false,
		itemConfigure: Item.Properties.() -> Unit = {},
		factory: BlockFactory = ::Block,
		configure: BlockBehaviour.Properties.() -> Unit = {},
	) = REGISTRY.register(name) { ->
		factory(BlockBehaviour.Properties.of().apply(configure))
	}.also { deferred ->
		if (withItem) {
			CustomItems.REGISTRY.registerSimpleBlockItem(name, deferred, Item.Properties().apply(itemConfigure))
		}
	}

	val EXAMPLE_BLOCK by block("test_block", true) {
		lightLevel { 15 }
		strength(3.0f)
	}
}
