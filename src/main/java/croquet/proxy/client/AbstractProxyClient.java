package croquet.proxy.client;

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.net.URI.create;

public abstract class AbstractProxyClient {

    private final Socket socket;

    public AbstractProxyClient() {
        this(3000);
    }

    public AbstractProxyClient(final int port) {
        this.socket = IO.socket(create("ws://localhost:" + port));
        this.socket.on("ready", (data) -> this.onReady());
        this.socket.on("data", (data) -> this.onData(String.valueOf(data[0])));
        this.socket.on("data-update", (patches) -> this.onDataUpdate(String.valueOf(patches[0])));
        this.socket.on("event", (data) -> this.onEvent(String.valueOf(data[0]), String.valueOf(data[1])));
    }

    public void connect() {
        this.socket.connect();
        this.socket.emit("join");

    }

    public void subscribe(final String scope, final String event) {
        this.socket.emit("subscribe", scope, event);
    }

    public void unsubscribe(final String scope, final String event) {
        this.socket.emit("unsubscribe", scope, event);
    }

    public void publish(final String scope, final String event) {
        this.socket.emit("publish", scope, event);
    }

    public abstract void onReady();
    public abstract void onData(final String jsonData);
    public abstract void onDataUpdate(final String jsonPatch);
    public abstract void onEvent(final String scope, final String event);
}
