package org.duckdns.auby.reindev.auth.server.mixins;

import java.util.List;

import net.minecraft.silveros.Config;
import net.minecraft.src.server.ChatAllowedCharacters;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.server.ServerConfigurationManager;
import net.minecraft.src.server.packets.Packet;
import net.minecraft.src.server.packets.Packet3Chat;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.game.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.duckdns.auby.reindev.auth.DuckServer;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {
	@Inject(method="playerLoggedIn",at=@At(value="HEAD"),cancellable=true)
	public void becomeSilly1(EntityPlayerMP ep, CallbackInfo ci) {
	//	if(!DuckServer.getInstance().onDuckJoin(ep.username, ep)) {
		DuckServer.getInstance().onDuckJoin(ep.username, ep);
	//		ci.cancel();
	//	}
	}
	@Inject(method="playerLoggedOut",at=@At(value="TAIL"))
	public void becomeSilly2(EntityPlayerMP ep, CallbackInfo ci) {
		DuckServer.getInstance().onDuckLeave(ep.username);
	}
}
