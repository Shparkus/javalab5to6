package Server;

import java.net.SocketAddress;

public class ReceivedDatagram {
    private final byte[] data;
    private final SocketAddress address;

    public ReceivedDatagram(byte[] data, SocketAddress address) {
        this.data = data;
        this.address = address;
    }

    public byte[] getData() {
        return data;
    }

    public SocketAddress getAddress() {
        return address;
    }
}
