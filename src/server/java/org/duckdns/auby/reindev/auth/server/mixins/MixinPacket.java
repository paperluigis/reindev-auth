package org.duckdns.auby.reindev.auth.server.mixins;

import net.minecraft.src.server.packets.Packet;

import org.duckdns.auby.reindev.auth.CommandListPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Packet.class)
class MixinPacket {
	@Inject(method="getPacketId",at=@At(value="HEAD"),cancellable=true)
	public void yay(CallbackInfoReturnable<Integer> ci) {
		if((Object)this instanceof CommandListPacket)
			ci.setReturnValue(252);
	}
}
