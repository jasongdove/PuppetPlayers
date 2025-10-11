package me.senseiwells.puppet

import com.mojang.authlib.GameProfile
import me.senseiwells.puppet.action.PuppetPlayerActions
import me.senseiwells.puppet.network.PuppetGamePacketListenerImpl
import net.casual.arcade.npc.FakePlayer
import net.casual.arcade.npc.network.FakeGamePacketListenerImpl
import net.casual.arcade.utils.PlayerUtils.levelServer
import net.minecraft.network.Connection
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.jvm.optionals.getOrNull

class PuppetPlayer @Internal constructor(
    server: MinecraftServer,
    level: ServerLevel,
    profile: GameProfile,
    info: ClientInformation
): FakePlayer(server, level, profile, info) {
    val actions = PuppetPlayerActions(this)

    override fun createConnection(
        server: MinecraftServer,
        connection: Connection,
        cookie: CommonListenerCookie
    ): FakeGamePacketListenerImpl {
        return PuppetGamePacketListenerImpl(server, connection, this, cookie)
    }

    override fun createRespawned(
        server: MinecraftServer,
        level: ServerLevel,
        profile: GameProfile,
        info: ClientInformation
    ): PuppetPlayer {
        return PuppetPlayer(server, level, profile, info)
    }

    override fun connection(): PuppetGamePacketListenerImpl {
        return this.connection as PuppetGamePacketListenerImpl
    }

    override fun tick() {
        super.tick()

        this.levelServer.schedule(TickTask(this.levelServer.tickCount) {
            // All player actions should be handled in the packet phase
            this.actions.tick()
        })
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        val packed = input.read("fake_actions", PuppetPlayerActions.Packed.CODEC).getOrNull()
        if (packed != null) {
            this.actions.unpack(packed)
        }
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("fake_actions", PuppetPlayerActions.Packed.CODEC, this.actions.pack())
    }
}