package org.duckdns.auby.reindev.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.logging.*;
import java.util.Properties;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.fox2code.foxloader.loader.ModLoader;
import com.fox2code.foxloader.loader.ServerMod;
import com.fox2code.foxloader.registry.CommandCompat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.server.packets.*;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.game.level.WorldServer;
import net.minecraft.src.game.level.chunk.ChunkCoordinates;

import org.duckdns.auby.reindev.auth.commands.*;

class BruhInfo {
	public boolean logged_in = false;
	public boolean confirming = false;
	public boolean has_pass = false;
	public EntityPlayerMP ep;
	public double spX;
	public double spY;
	public double spZ;
	public byte[] pass_confirm = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public byte[] pass = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public BruhInfo(EntityPlayerMP a, byte[] b) {
		ep = a;
		if(ep != null) {
			spX = a.posX;
			spY = a.posY;
			spZ = a.posZ;
		}
		if(b != null) {
			pass = b;
			has_pass = true;
		}
	}
}

public class DuckServer extends DuckMod implements ServerMod {
	private final static File duckdir = new File(ModLoader.config, "duckyauth");
	private final static File duckfile = new File(duckdir, "pass.txt");
	private final static File duckconf = new File(duckdir, "config.properties");
	private final static File duckfile_tmp = new File(duckdir, "pass_new.txt");
	private final static Logger lmaogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final static MinecraftServer theMinecraft = MinecraftServer.getInstance();
	private final static HashMap<String, BruhInfo> duckmap = new HashMap<>();
	public final static Properties duckprop = new Properties();
	private final static MessageDigest mdt;
	static {
		MessageDigest a = null;
		try { a = MessageDigest.getInstance("SHA-256"); } catch(Throwable e) {
			// never happens, hopefully
			e.printStackTrace();
			System.exit(1);
		}
		mdt = a;
	}
	private static DuckServer theduck;

	private Integer tick_timer_welcome = 0;

	public static boolean validPassword(String password) {
		return !password.contains(" ") && password.trim() != "";
	}
	public static DuckServer getInstance() {
		return theduck;
	}
	public DuckServer() {
		CommandCompat.registerCommand(new ChangePassword());
		CommandCompat.registerCommand(new Unregister());
		CommandCompat.registerCommand(new DuckyAuthCommand());

		theduck = this;
		if (!duckdir.exists() && !duckdir.mkdirs()) {
			lmaogger.log(Level.SEVERE, "failed to create config directory :(( at "+duckdir.toString());
			System.exit(1);
		}
		loadMap();
		loadConfiguration();
	}

	@Override
	public void onPreInit() {}

	@Override
	public void onTick() {
		if(tick_timer_welcome++ > 100) {
			tick_timer_welcome = 0;
			for (Map.Entry<String, BruhInfo> entry : duckmap.entrySet()) {
				BruhInfo bruh = entry.getValue();
				if(bruh.logged_in || bruh.confirming || bruh.ep == null) continue;
				if(bruh.has_pass) {
					bruh.ep.playerNetServerHandler.sendPacket(new Packet3Chat(duckprop.getProperty("string_login")));
				} else {
					bruh.ep.playerNetServerHandler.sendPacket(new Packet3Chat(duckprop.getProperty("string_register")));
				}
			}
		}
	}

	public boolean onDuckJoin(String username, EntityPlayerMP ep) {
		BruhInfo bruh;
		if(!duckmap.containsKey(username)) {
			bruh = new BruhInfo(ep, null);
			duckmap.put(username, bruh);
		} else {
			bruh = duckmap.get(username);
		}
		if(bruh.ep == null) {
		//	lmaogger.log(Level.INFO, "this fine individual named "+username+" just joined..!");
			bruh.ep = ep;
			ep.capabilities.disableDamage = true;
			ep.playerNetServerHandler.sendPacket(new CommandListPacket());
			return false;
		} else {
			return this.isDuckVerified(username);
		}
	}
	public void onDuckLeave(String username) {
		BruhInfo a = duckmap.get(username);
		if(a == null || a.ep == null) return;
	//	lmaogger.log(Level.INFO, "this fine individual named "+username+" just left.. :(");
		a.logged_in = false;
		a.ep = null;
	}
	public void handleDuckCmd(String username, EntityPlayerMP ep, String command) {
		BruhInfo a = duckmap.get(username);
		a.ep = ep;
		NetServerHandler y = ep.playerNetServerHandler;
		String arr = command.trim();
		if(arr.startsWith("/")) {
			String[] arrgs = arr.split(" ", 2);
			String cmd = arrgs[0];
			lmaogger.log(Level.INFO, "("+username+": "+cmd+" ...)");
			String arg = arrgs.length>1 ? arrgs[1].trim() : "";
			if(cmd.equals("/login")) {
				if(arg.equals("")) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_usage_login")));
					return;
				}
				if(a.has_pass) {
					if(Arrays.equals(a.pass, hash(arg))) {
						verifyPlayer(username);
						y.sendPacket(new Packet3Chat(duckprop.getProperty("string_authed")));
					} else {
						y.sendPacket(new Packet3Chat(duckprop.getProperty("string_login_bad")));
					}
				} else {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_login_no_entry")));
				}
			} else if(cmd.equals("/register")) {
				if(arg.equals("")) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_usage_register")));
					return;
				}
				if(!validPassword(arg)) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_bad_password")));
					return;
				}
				if(!a.has_pass) {
					a.pass_confirm = hash(arg);
					a.confirming = true;
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_register_confirm")));
				} else {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_register_entry_exists")));
				}
			} else if(cmd.equals("/confirm")) {
				if(arg.equals("")) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_usage_confirm")));
					return;
				}
				if(!validPassword(arg)) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_bad_password")));
					return;
				}
				if(!a.confirming) {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_confirm_register_first")));
				}else if(Arrays.equals(a.pass_confirm, hash(arg))) {
					verifyPlayer(username);
					a.pass = a.pass_confirm;
					a.has_pass = true;
					saveMap();
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_authed_first")));
				} else {
					y.sendPacket(new Packet3Chat(duckprop.getProperty("string_confirm_bad")));
				}
			}
		} else {
			y.sendPacket(new Packet3Chat(duckprop.getProperty("string_auth_required")));
			return;
		}
	}
	public void verifyPlayer(String username) {
		BruhInfo a = duckmap.get(username);
		EntityPlayerMP ep = a.ep;
		NetServerHandler y = ep.playerNetServerHandler;

		ep.capabilities.disableDamage = false;

		lmaogger.log(Level.INFO, "this fine individual named "+username+" just authenticated!! :D");
		a.logged_in = true;
	}
	public boolean isDuckVerified(String username) {
		BruhInfo a = duckmap.get(username);
		return a != null && a.logged_in;
	}

	public void saveMap() {
		FileWriter bruh;
		String le = System.lineSeparator();
		try {
			bruh = new FileWriter(duckfile_tmp);
		} catch (IOException e) {
			lmaogger.log(Level.WARNING, "Failed to open file '"+duckfile.toString()+"' for writing: "+e.getMessage()+". Changed passwords will not be saved.");
			return;
		}
		try {
			for (Map.Entry<String, BruhInfo> entry : duckmap.entrySet()) {
				String key = entry.getKey();
				BruhInfo value = entry.getValue();
				if(value.has_pass)
					bruh.write(key+":"+bytesToHex(value.pass)+le);
			}
			bruh.close();
			duckfile_tmp.renameTo(duckfile);
		} catch(IOException e) {
			lmaogger.log(Level.WARNING, "Something happened while reading the file: "+e.getMessage());
		}
	}

	public void loadMap() {
		duckmap.clear();
		LineNumberReader bruh;
		try {
			bruh = new LineNumberReader(new FileReader(duckfile));
		} catch (FileNotFoundException e) {
			lmaogger.log(Level.WARNING, "Failed to open file '"+duckfile.toString()+"' for reading: "+e.getMessage()+". Creating a new password map.");
			return;
		}
		String line;
		try {
			while ((line = bruh.readLine()) != null) {
				String[] woah = line.split(":");
				if(woah.length < 2) {
					lmaogger.log(Level.WARNING, "Failed to parse whatever you did on line " + bruh.getLineNumber());
				}
				String username = woah[0];
				byte[] password = hexToBytes(woah[1]);
				BruhInfo user = new BruhInfo(null, password);
				if(password == null) {
					lmaogger.log(Level.WARNING, "Failed to parse whatever you did with the password on line " + bruh.getLineNumber());
				}
				duckmap.put(username, user);
			}
			bruh.close();
		} catch(IOException e) {
			lmaogger.log(Level.WARNING, "Something happened while reading the file: "+e.getMessage());
		}
	}

	public void loadConfiguration() {
		FileReader bruh;
		try {
			bruh = new FileReader(duckconf);
			duckprop.load(bruh);
			bruh.close();
		} catch (FileNotFoundException e) {
			// ignorance is bliss
		} catch (IOException e) {
			lmaogger.log(Level.WARNING, "Something happened while reading the configuration: "+e.getMessage()+". Defaults will be used.");
			// ...ignorance?
		}
		Properties defaults = new Properties();
		try {
			defaults.load(DuckServer.class.getClassLoader().getResourceAsStream("default_config.properties"));
		} catch (IOException wa) {
			// unreachable
			lmaogger.log(Level.SEVERE, "How did we get here?? [38127]");
			System.exit(1);
		}
		for(Map.Entry<Object,Object> baka : defaults.entrySet()) {
			duckprop.putIfAbsent(baka.getKey(), baka.getValue());
		}
		FileWriter yaa;
		try {
			yaa = new FileWriter(duckconf);
			duckprop.store(yaa, "the duck configuration");
			yaa.close();
		} catch (IOException e) {
			lmaogger.log(Level.WARNING, "Something happened while writing the configuration: "+e.getMessage()+". Nothing will happen, I guess?");
		}
	}

	public boolean checkPassword(String username, String password) {
		BruhInfo a = duckmap.get(username);
		if(a == null) return false;
		return Arrays.equals(a.pass, hash(password));
	}

	public boolean removeUser(String username) {
		boolean removed = duckmap.remove(username) != null;
		saveMap();
		return removed;
	}

	// returns true if a new user was registere
	public boolean setPassword(String username, String password) {
		BruhInfo bruh;
		boolean toRegister = !duckmap.containsKey(username);
		if(toRegister) {
			bruh = new BruhInfo(null, hash(password));
			duckmap.put(username, bruh);
		} else {
			bruh = duckmap.get(username);
			bruh.pass = hash(password);
		}
		saveMap();
		return toRegister;
	}

	private static byte[] hexToBytes(String thing) {
		if(thing == null || thing.length() != 64) return null;
		byte[] a = new byte[32];
		try {
			for(int i = 0; i < 64; i += 2) {
				a[i/2] = (byte) Integer.parseInt(thing.substring(i,i+2), 16);
			}
		} catch(NumberFormatException e) {
			return null;
		}
		return a;
	}
	private static String bytesToHex(byte[] thing) {
		StringBuilder da = new StringBuilder(2 * thing.length);
		for (int i = 0; i < thing.length; i++) {
			String yo = Integer.toHexString(thing[i] & 0xff);
			if(yo.length() == 1) {
				da.append('0');
			}
			da.append(yo);
		}
		return da.toString();
	}
	private static byte[] hash(String thing) {
		return mdt.digest(thing.getBytes(StandardCharsets.UTF_8));
	}
}

