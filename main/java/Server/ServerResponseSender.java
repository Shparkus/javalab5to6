package Server;

import Network.Response;
import Network.SerializationUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ServerResponseSender {
    private final DatagramChannel channel;

    public ServerResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }

    public void send(Response response, SocketAddress address) throws IOException {
        byte[] data = SerializationUtils.serialize(response);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, address);
    }
}
