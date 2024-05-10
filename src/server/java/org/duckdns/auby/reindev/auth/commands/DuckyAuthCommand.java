package org.duckdns.auby.reindev.auth.commands;

import org.duckdns.auby.reindev.auth.DuckServer;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;

public class DuckyAuthCommand extends CommandCompat {
	public DuckyAuthCommand() {
		super("duckyauth", true);
	}
	public String commandSyntax(){
		return DuckServer.duckprop.getProperty("string_usage_duckyauth");
	}
	public void onExecute(final String[] args, final NetworkPlayer cex) {
		switch(args.length > 1 ? args[1] : "") {
			case "reload": {
				DuckServer.getInstance().loadMap();
			} break;
			case "password": {
				if(args.length != 4) {
					cex.displayChatMessage(DuckServer.duckprop.getProperty("string_usage_duckyauth_password"));
					return;
				}
				if(DuckServer.validPassword(args[3])) {
					if(DuckServer.getInstance().setPassword(args[2], args[3])) {
						cex.displayChatMessage(DuckServer.duckprop.getProperty("string_user_created"));
					} else {
						cex.displayChatMessage(DuckServer.duckprop.getProperty("string_password_changed"));
					}
				} else {
					cex.displayChatMessage(DuckServer.duckprop.getProperty("string_bad_password"));
				}
			} break;
			case "unregister": {
				if(args.length != 3) {
					cex.displayChatMessage(DuckServer.duckprop.getProperty("string_usage_duckyauth_unregister"));
					return;
				}
				if(DuckServer.getInstance().removeUser(args[2])) {
					cex.displayChatMessage(DuckServer.duckprop.getProperty("string_user_unregistered"));
				} else {
					cex.displayChatMessage(DuckServer.duckprop.getProperty("string_no_such_user"));
				}
			} break;
			default:
				cex.displayChatMessage(DuckServer.duckprop.getProperty("string_usage_duckyauth"));
		}
	}
}
