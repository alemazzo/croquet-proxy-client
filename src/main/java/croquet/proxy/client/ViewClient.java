package croquet.proxy.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewClient<M extends ModelData> {

    public M data;
    private final AbstractProxyClient proxyClient;
    private final Map<Subscription, Runnable> subscriptionRunnableMap = new HashMap<>();

    public final void subscribe(final String scope, final String event, final Runnable onEvent) {
        this.subscriptionRunnableMap.put(new Subscription(scope, event), onEvent);
    }

    public final void publish(final String scope, final String event) {
        this.proxyClient.publish(scope, event);
    }

    public ViewClient(final Class<M> clazz) {
        try {
            this.data = (M) clazz.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.proxyClient = new AbstractProxyClient() {

            @Override
            public void onReady() {
                ViewClient.this.subscriptionRunnableMap.keySet().forEach(s -> {
                    this.subscribe(s.scope, s.event);
                });
            }

            @Override
            public void onData(String jsonData) {
                try {
                    System.out.println("Loading initial data");
                    ViewClient.this.data = (M) ModelData.loadFromJson(jsonData, data.getClass());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataUpdate(String jsonPatch) {
                // TODO: Handle json patch
                try {
                    System.out.println("Handling update");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonPatch patch = mapper.readValue(jsonPatch, JsonPatch.class);
                    JsonNode result = patch.apply(mapper.convertValue(ViewClient.this.data, JsonNode.class));
                    ViewClient.this.data = (M) mapper.treeToValue(result, ViewClient.this.data.getClass());
                } catch (JsonProcessingException | JsonPatchException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(String scope, String event) {
                System.out.println("Handling event");
                ViewClient.this.subscriptionRunnableMap.get(new Subscription(scope, event)).run();
            }
        };
    }

    public void connect() {
        this.proxyClient.connect();
    }
}
