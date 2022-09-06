import croquet.proxy.client.ModelData;
import croquet.proxy.client.ViewClient;

public abstract class View<M extends ModelData> {

    final ViewClient<M> client;

    public View(Class<M> clazz) {
        this.client = new ViewClient<>(clazz);
    }

    public final void start() {
        this.client.connect();
    }

    public final void subscribe(final String scope, final String event, final Runnable onEvent) {
        this.client.subscribe(scope, event, onEvent);
    }

    public final void publish(final String scope, final String event) {
        this.client.publish(scope, event);
    }

    public M getData() {
        return this.client.data;
    }

}
