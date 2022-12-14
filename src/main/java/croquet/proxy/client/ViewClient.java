package croquet.proxy.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ViewClient<M extends ModelData> {

    public M data;
    private final AbstractProxyClient proxyClient;
    private final Map<Subscription, Consumer<Optional<Object>>> subscriptionHandlersMap = new HashMap<>();

    public final void subscribe(final String scope, final String event, final Consumer<Optional<Object>> onEvent) {
        this.subscriptionHandlersMap.put(new Subscription(scope, event), onEvent);
    }

    public final void publish(final String scope, final String event) {
        this.proxyClient.publish(scope, event);
    }

    public final void publish(final String scope, final String event, final Object data) {
        this.proxyClient.publish(scope, event, data);
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
                ViewClient.this.subscriptionHandlersMap.keySet().forEach(s -> {
                    this.subscribe(s.scope, s.event);
                });
            }

            @Override
            public void onData(String jsonData) {
                try {
                    ViewClient.this.data = (M) ModelData.loadFromJson(jsonData, data.getClass());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataUpdate(String jsonPatch) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonPatch patch = mapper.readValue(jsonPatch, JsonPatch.class);
                    JsonNode result = patch.apply(mapper.convertValue(ViewClient.this.data, JsonNode.class));
                    ViewClient.this.data = (M) mapper.treeToValue(result, ViewClient.this.data.getClass());
                } catch (JsonProcessingException | JsonPatchException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(String scope, String event, Optional<Object> data) {
                ViewClient.this.subscriptionHandlersMap.get(new Subscription(scope, event)).accept(data);
            }
        };
    }

    public void connect() {
        this.proxyClient.connect();
    }
}
