package org.duckdns.auby.reindev.auth;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.mitask.PlayerCommandHandler;
import net.minecraft.mitask.command.Command;
import net.minecraft.src.server.packets.Packet252CommandList;

public class CommandListPacket extends Packet252CommandList {
	@Override
	public void writePacketData(DataOutputStream a) throws IOException {
		this.writeString("login register confirm", a);
	}
}
