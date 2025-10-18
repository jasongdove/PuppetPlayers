package me.senseiwells.puppet.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.casual.arcade.npc.FakePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @WrapWithCondition(
        method = "kickUnlistedPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private boolean shouldDisconnectPlayer(
        ServerGamePacketListenerImpl instance,
        Component component
    ) {
        return !(instance.player instanceof FakePlayer);
    }
}
