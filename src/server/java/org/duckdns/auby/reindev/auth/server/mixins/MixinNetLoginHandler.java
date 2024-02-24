package org.duckdns.auby.reindev.auth.server.mixins;

import java.util.List;

import net.minecraft.silveros.Config;
import net.minecraft.src.server.ChatAllowedCharacters;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.server.packets.NetLoginHandler;
import net.minecraft.src.server.packets.Packet;
import net.minecraft.src.server.packets.Packet1Login;
import net.minecraft.src.game.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.duckdns.auby.reindev.auth.DuckServer;

@Mixin(NetLoginHandler.class)
public class MixinNetLoginHandler {
//	@Inject(method="doLogin",at=@At(value="INVOKE",target="Ljava/util/logging/Logger;info",ordinal=0),locals=LocalCapture.CAPTURE_FAILHARD)
//	public void becomeSilly1(Packet1Login p1l, CallbackInfo ci, EntityPlayerMP ep) {
//		DuckServer.getInstance().onDuckJoin(ep.username);
//	}
}
