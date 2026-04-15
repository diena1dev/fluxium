package dev.diena.fluxium

import dev.diena.fluxium.block.CustomBlocks
import dev.diena.fluxium.component.ModDataComponents
import dev.diena.fluxium.item.CustomItems
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

/**
 * Main mod class.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(Fluxium.ID)
object Fluxium {
	const val ID = "fluxium"
	val LOGGER: Logger = LogManager.getLogger(ID)

	init {
		LOGGER.log(Level.INFO, "Hello world!")

		// Register deferred registries to the mod-specific event bus
		ModDataComponents.REGISTRY.register(MOD_BUS)
		CustomBlocks.REGISTRY.register(MOD_BUS)
		CustomBlocks.ITEM_REGISTRY.register(MOD_BUS)
		CustomItems.REGISTRY.register(MOD_BUS)
		MOD_BUS.addListener(::onCommonSetup)

		if (FMLEnvironment.dist == Dist.CLIENT) {
			MOD_BUS.addListener(::onClientSetup)
		} else {
			MOD_BUS.addListener(::onServerSetup)
		}
	}

	/**
	 * This is used for initializing client specific
	 * things such as renderers and keymaps
	 * Fired on the mod specific event bus.
	 */
	private fun onClientSetup(event: FMLClientSetupEvent) {
		// TODO: add shader loader backend, custom model loader backend
		LOGGER.log(Level.INFO, "Initializing client...")
	}

	/**
	 * Fired on the global Forge bus.
	 */
	private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
		LOGGER.log(Level.INFO, "Server starting...")
	}

	private fun onCommonSetup(event: FMLCommonSetupEvent) {
		LOGGER.log(Level.INFO, "welcome to a fluxuating world >:3")
	}
}
