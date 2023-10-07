package me.vlod.pinto.consolehandler.commands.client;

import me.vlod.pinto.Coloring;
import me.vlod.pinto.GroupDatabaseEntry;
import me.vlod.pinto.PintoServer;
import me.vlod.pinto.UserDatabaseEntry;
import me.vlod.pinto.UserStatus;
import me.vlod.pinto.consolehandler.ConsoleCaller;
import me.vlod.pinto.consolehandler.ConsoleCommand;
import me.vlod.pinto.networking.NetworkHandler;
import me.vlod.pinto.networking.packet.PacketAddContact;

public class Add implements ConsoleCommand {
	@Override
	public String getName() {
		return "add";
	}

	@Override
	public String getDescription() {
		return "Adds a contact to this group chat";
	}

	@Override
	public String getUsage() {
		return "add <name>";
	}

	@Override
	public int getMinArgsCount() {
		return 1;
	}

	@Override
	public int getMaxArgsCount() {
		return 1;
	}

	@Override
	public void execute(PintoServer server, ConsoleCaller caller, String[] args) throws Exception {
		String groupID = caller.clientChat;
		String user = args[0];
		GroupDatabaseEntry groupDatabaseEntry = new GroupDatabaseEntry(server, groupID);
		groupDatabaseEntry.load();
		NetworkHandler userClient = server.getHandlerByName(user);
		
		if (!caller.client.databaseEntry.contacts.contains(user)) {
			caller.sendMessage(Coloring.translateAlternativeColoringCodes(
					String.format("&8[&5!&8]&4 %s is not on your contacts list", user)));
			return;
		}
		
		if (groupDatabaseEntry.members.contains(user)) {
			caller.sendMessage(Coloring.translateAlternativeColoringCodes(
					String.format("&8[&5!&8]&4 %s is already in the group", user)));
			return;
		}
		
		UserDatabaseEntry userDatabaseEntry = new UserDatabaseEntry(server, user);
		userDatabaseEntry.load();
		
		if (userDatabaseEntry.contacts.size() + 1 > 500) {
			caller.sendMessage(Coloring.translateAlternativeColoringCodes(
					String.format("&8[&5!&8]&4 %s has reached the 500 contacts limit", user)));
			return;
		}
		
		groupDatabaseEntry.members.add(user);
		groupDatabaseEntry.save();
		userDatabaseEntry.contacts.add(groupID);
		userDatabaseEntry.save();
		
		if (userClient != null) {
			userClient.databaseEntry.load();
			userClient.sendPacket(new PacketAddContact(groupID, UserStatus.ONLINE, "Pinto! Group"));	
		}
		caller.sendMessage(Coloring.translateAlternativeColoringCodes(
				String.format("&8[&5i&8]&4 You have added %s to this group", user)));
		server.sendMessageInGroup(groupID, "", Coloring.translateAlternativeColoringCodes(
				String.format("&8[&5i&8]&4 %s has added %s to this group", 
						caller.client.userName, user)));
	}
}
