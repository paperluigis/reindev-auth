package org.duckdns.auby.reindev.auth.commands;

import org.duckdns.auby.reindev.auth.DuckServer;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;

public class Unregister extends CommandCompat {
	public Unregister() {
		super("unregister", false);
	}
	public String commandSyntax(){
		return DuckServer.duckprop.getProperty("string_usage_unregister");
	}
	public void onExecute(final String[] args, final NetworkPlayer cex) {
		if(args.length == 2) {
			if(!DuckServer.getInstance().checkPassword(cex.getPlayerName(), args[1])) {
				cex.displayChatMessage(DuckServer.duckprop.getProperty("string_login_bad"));
			} else {
				// why would it return false?
				DuckServer.getInstance().removeUser(cex.getPlayerName());
				cex.kick(DuckServer.duckprop.getProperty("string_unregistered"));
			}
		} else {
			cex.displayChatMessage(DuckServer.duckprop.getProperty("string_usage_unregister"));
		}
		// bruh
	}
}
