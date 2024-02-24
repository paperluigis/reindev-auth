package org.duckdns.auby.reindev.auth.server.mixins;

import java.util.List;

import net.minecraft.silveros.Config;
import net.minecraft.src.server.ChatAllowedCharacters;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.server.packets.Packet;
import net.minecraft.src.server.packets.Packet1Login;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.entity.player.PlayerCapabilities;
import net.minecraft.src.game.nbt.PlayerNBTManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.duckdns.auby.reindev.auth.DuckServer;

@Mixin(PlayerNBTManager.class)
public class MixinPlayerNBTManager {
	@Inject(method="readPlayerData",at=@At(value="HEAD"),cancellable=true)
	public void beASillyDuck(EntityPlayer ep, CallbackInfo ci) {
	//	if(!DuckServer.getInstance().isDuckVerified(ep.username)) {
	//		ci.cancel();
	//	}
	}
	@Inject(method="writePlayerData",at=@At(value="HEAD"),cancellable=true)
	public void beMoreSillyNow(EntityPlayer ep, CallbackInfo ci) {
	//	if(!DuckServer.getInstance().isDuckVerified(ep.username)) {
	//		ci.cancel();
	//	}
	}
//	@Inject(method="doLogin",at=@At(value="INVOKE",target="Ljava/util/logging/Logger;info",ordinal=0),locals=LocalCapture.CAPTURE_FAILHARD)
//	public void becomeSilly1(Packet1Login p1l, CallbackInfo ci, PlayerNBTManager ep) {
//		DuckServer.getInstance().onDuckJoin(ep.username);
//	}
}
