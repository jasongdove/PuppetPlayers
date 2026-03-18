package me.senseiwells.puppet.action.impl

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.senseiwells.puppet.PuppetPlayer
import me.senseiwells.puppet.action.PuppetPlayerAction
import me.senseiwells.puppet.action.PuppetPlayerActionProvider
import net.casual.arcade.commands.argument
import net.casual.arcade.commands.getArgumentOrElse
import net.casual.arcade.utils.serialization.codec.ArcadeExtraCodecs
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec3

class LookAtAction(
    private val anchor: EntityAnchorArgument.Anchor,
    private val target: Vec3
): PuppetPlayerAction {
    override fun run(player: PuppetPlayer): PuppetPlayerAction.Result {
        player.lookAt(this.anchor, this.target)
        return PuppetPlayerAction.Result.Complete
    }

    override fun provider(): PuppetPlayerActionProvider {
        return LookAtAction
    }

    companion object: PuppetPlayerActionProvider {
        private val ANCHOR_CODEC = ArcadeExtraCodecs.enum<EntityAnchorArgument.Anchor> { it.name.lowercase() }

        override val id: Identifier = Identifier.withDefaultNamespace("look_at")

        override val codec: MapCodec<out LookAtAction> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ANCHOR_CODEC.fieldOf("anchor").forGetter(LookAtAction::anchor),
                Vec3.CODEC.fieldOf("target").forGetter(LookAtAction::target)
            ).apply(instance, ::LookAtAction)
        }

        override fun addCommandArguments(
            builder: LiteralArgumentBuilder<CommandSourceStack>,
            command: Command<CommandSourceStack>
        ) {
            builder.argument("target", Vec3Argument.vec3()) {
                executes(command)
                argument("anchor", EntityAnchorArgument.anchor()) {
                    executes(command)
                }
            }
        }

        override fun createCommandAction(context: CommandContext<CommandSourceStack>): PuppetPlayerAction {
            val target = Vec3Argument.getVec3(context, "target")
            val anchor = context.getArgumentOrElse("anchor", EntityAnchorArgument::getAnchor) {
                EntityAnchorArgument.Anchor.EYES
            }
            return LookAtAction(anchor, target)
        }
    }
}