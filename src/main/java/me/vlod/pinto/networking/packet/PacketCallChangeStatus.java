package me.vlod.pinto.networking.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.vlod.pinto.CallStatus;
import me.vlod.pinto.Utils;
import me.vlod.pinto.networking.NetworkHandler;

public class PacketCallChangeStatus implements Packet {
	public CallStatus callStatus;
	public String details;
	
	public PacketCallChangeStatus() { }

	public PacketCallChangeStatus(CallStatus callStatus, String details) {
		this.callStatus = callStatus;
		this.details = details;
	}
	
	@Override
	public void read(DataInputStream stream) throws IOException {
		this.callStatus = CallStatus.fromIndex(stream.readInt());
		this.details = Utils.readPintoStringFromStream(stream, 64);
	}
	
	@Override
	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.callStatus.getIndex());
		Utils.writePintoStringToStream(stream, this.details, 64);
	}

	@Override
	public int getID() {
		return 11;
	}

	@Override
	public void handle(NetworkHandler netHandler) {
		netHandler.handleCallChangeStatusPacket(this);
	}
}
