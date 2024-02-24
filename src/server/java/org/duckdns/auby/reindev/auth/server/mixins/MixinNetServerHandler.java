package org.duckdns.auby.reindev.auth.server.mixins;

import java.util.List;

import net.minecraft.silveros.Config;
import net.minecraft.src.server.ChatAllowedCharacters;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.server.packets.*;
import net.minecraft.src.game.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.duckdns.auby.reindev.auth.DuckServer;

@Mixin(NetServerHandler.class)
public abstract class MixinNetServerHandler {
	@Shadow
	public EntityPlayerMP playerEntity;
	@Shadow
	public abstract void sendPacket(Packet p);
	@Shadow
	public abstract void teleportTo(double x, double y, double z, float yaw, float pitch);

	public boolean verified = false;

	@Inject(method="handleFlying",at=@At(value="HEAD"),cancellable=true)
	public void handleFlying(Packet10Flying p10f, CallbackInfo ci) {
		if(!verified) {
			teleportTo(
				playerEntity.posX, playerEntity.posY, playerEntity.posZ,
				playerEntity.rotationYaw, playerEntity.rotationPitch );
			ci.cancel();
		}
	}

	// the great chinese wall?
	// i wish rust macros were a thing in java
	@Inject(method="handleBlockDig",at=@At(value="HEAD"),cancellable=true)
	public void china0(Packet14BlockDig a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handlePlace",at=@At(value="HEAD"),cancellable=true)
	public void china1(Packet15Place a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleUseEntity",at=@At(value="HEAD"),cancellable=true)
	public void china2(Packet7UseEntity a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleCauldron",at=@At(value="HEAD"),cancellable=true)
	public void china3(Packet66Cauldron a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleCreativeSetSlot",at=@At(value="HEAD"),cancellable=true)
	public void china4(Packet107CreativeSetSlot a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleBlockItemSwitch",at=@At(value="HEAD"),cancellable=true)
	public void china5(Packet16BlockItemSwitch a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleNameTag",at=@At(value="HEAD"),cancellable=true)
	public void china6(Packet91NameTag a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleSlashCommand",at=@At(value="HEAD"),cancellable=true)
	public void china7(String a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleWindowClick",at=@At(value="HEAD"),cancellable=true)
	public void china8(Packet102WindowClick a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="func_20008_a",at=@At(value="HEAD"),cancellable=true)
	public void china9(Packet106Transaction a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }
	@Inject(method="handleUpdateSign",at=@At(value="HEAD"),cancellable=true)
	public void china_(Packet130UpdateSign a, CallbackInfo ci) { if(!verified) { ci.cancel(); } }

	@Inject(method="handleChat",at=@At(value="HEAD"),cancellable=true)
	public void handleChat(Packet3Chat p3c, CallbackInfo ci) {
		if(!verified) {
			DuckServer a = DuckServer.getInstance();
			a.handleDuckCmd(playerEntity.username, playerEntity, p3c.message);
			verified = a.isDuckVerified(playerEntity.username);
			if(verified) {
				if(qp13 != null) sendPacket(qp13);
			}
			ci.cancel();
		}
	}

	public Packet13PlayerLookMove qp13;
	@Inject(method="sendPacket",at=@At(value="HEAD"),cancellable=true)
	public void sendSillyPacket(Packet p, CallbackInfo ci) {
		if(verified) return;
		if(p instanceof Packet5PlayerInventory) { ci.cancel(); }
		else if(p instanceof Packet13PlayerLookMove) {
			Packet13PlayerLookMove r = (Packet13PlayerLookMove) p;
			qp13 = new Packet13PlayerLookMove(r.xPosition, r.yPosition, r.stance, r.zPosition, r.yaw, r.pitch, r.onGround);
			r.xPosition = 0;
			r.yPosition = 256;
			r.zPosition = 0;
			r.onGround = false;
		}
	//	else if(p instanceof Packet) { qp = p; ci.cancel(); }
	}
}
