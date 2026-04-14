package dev.diena.fluxium.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Factory function that receives configured [BlockBehaviour.Properties] and returns
 * a [Block] instance. Use an object expression to override methods inline:
 * ```kotlin
 * factory = { props -> object : Block(props) { override fun ... } }
 * ```
 */
typealias BlockFactory = (props: BlockBehaviour.Properties) -> Block

