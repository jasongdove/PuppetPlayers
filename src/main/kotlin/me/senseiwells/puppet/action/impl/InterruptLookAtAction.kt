package me.senseiwells.puppet.action.impl

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.MapCodec
import me.senseiwells.puppet.PuppetPlayer
import me.senseiwells.puppet.action.PuppetPlayerAction
import me.senseiwells.puppet.action.PuppetPlayerActionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.Identifier

object InterruptLookAtAction: PuppetPlayerAction, PuppetPlayerActionProvider {
    override val id: Identifier = Identifier.withDefaultNamespace("interrupt_look_at")

    override val codec: MapCodec<out PuppetPlayerAction> = MapCodec.unit(this)

    override val immediate: Boolean get() = true

    override fun run(player: PuppetPlayer): PuppetPlayerAction.Result {
        player.actions.remove { it is LookAtAction }
        return PuppetPlayerAction.Result.Complete
    }

    override fun provider(): PuppetPlayerActionProvider {
        return this
    }

    override fun addCommandArguments(
        builder: LiteralArgumentBuilder<CommandSourceStack>,
        command: Command<CommandSourceStack>
    ) {
        builder.executes(command)
    }

    override fun createCommandAction(context: CommandContext<CommandSourceStack>): PuppetPlayerAction {
        return this
    }
}