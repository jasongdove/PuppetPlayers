package me.senseiwells.puppet.mixins;

import com.mojang.authlib.GameProfileRepository;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CachedUserNameToIdResolver.class)
public interface CachedUserNameToIdResolverAccessor {
    @Mutable
    @Accessor
    void setProfileRepository(GameProfileRepository repository);
}
