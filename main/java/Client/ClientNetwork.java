package Client;

import Network.Request;
import Network.Response;
import Network.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class ClientNetwork {
    private final InetSocketAddress serverAddress;
    private final DatagramChannel channel;
    private final Selector selector;
    private final int timeoutMs;

    public ClientNetwork(String host, int port, int timeoutMs) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        this.timeoutMs = timeoutMs;
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.connect(serverAddress);
        this.selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_READ);
    }

    public Response send(Request request, int attempts) {
        for (int i = 0; i < attempts; i++) {
            try {
                byte[] data = SerializationUtils.serialize(request);
                channel.write(ByteBuffer.wrap(data));

                Response response = waitForResponse();
                if (response != null) {
                    return response;
                }
            } catch (IOException | ClassNotFoundException e) {
                return new Response(false, "Ошибка обмена: " + e.getMessage());
            }
        }
        return null;
    }

    private Response waitForResponse() throws IOException, ClassNotFoundException {
        if (selector.select(timeoutMs) == 0) {
            return null;
        }
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(65507);
                channel.receive(buffer);
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                Object obj = SerializationUtils.deserialize(bytes);
                if (obj instanceof Response) {
                    return (Response) obj;
                }
            }
        }
        return null;
    }
}
