package dev.diena.fluxium.component

import dev.diena.fluxium.Fluxium
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModDataComponents {
	val REGISTRY: DeferredRegister.DataComponents =
		DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Fluxium.ID)

	/**
	 * Stores a [BlockState] on an item stack (e.g. the block currently held by a block-mover item).
	 * Persisted to disk and synced over the network.
	 */
	val STORED_BLOCK_STATE: DataComponentType<BlockState> by REGISTRY.registerComponentType("stored_block_state") { builder ->
		builder
			.persistent(BlockState.CODEC)
			.networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC))
	}
}


