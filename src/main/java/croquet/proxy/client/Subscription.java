package croquet.proxy.client;

import java.util.Objects;

public class Subscription {
    final String scope;
    final String event;

    public Subscription(final String scope, final String event) {
        this.scope = scope;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(scope, that.scope) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, event);
    }
}
