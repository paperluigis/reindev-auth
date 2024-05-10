package org.duckdns.auby.reindev.auth.commands;

import org.duckdns.auby.reindev.auth.DuckServer;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;

public class ChangePassword extends CommandCompat {
	public ChangePassword() {
		super("chpasswd", false);
	}
	public String commandSyntax(){
		return DuckServer.duckprop.getProperty("string_usage_chpasswd");
	}
	public void onExecute(final String[] args, final NetworkPlayer cex) {
		if(args.length == 3) {
			if(!DuckServer.validPassword(args[2])) {
				cex.displayChatMessage(DuckServer.duckprop.getProperty("string_bad_password"));
				return;
			}
			if(!DuckServer.getInstance().checkPassword(cex.getPlayerName(), args[1])) {
				cex.displayChatMessage(DuckServer.duckprop.getProperty("string_login_bad"));
			} else {
				DuckServer.getInstance().setPassword(cex.getPlayerName(), args[2]);
				cex.displayChatMessage(DuckServer.duckprop.getProperty("string_password_changed"));
			}
		} else {
			cex.displayChatMessage(DuckServer.duckprop.getProperty("string_usage_chpasswd"));
		}
		// bruh
	}
}
