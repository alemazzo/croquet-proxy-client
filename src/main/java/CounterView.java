public class CounterView extends View<CounterModel> {

    public CounterView() {
        super(CounterModel.class);
        subscribe("counter", "updated", (data) -> this.onCounterUpdated());
        subscribe("manual-counter", "updated", (data) -> this.onManualCounterUpdated());
    }

    void onCounterUpdated() {
        printData();
    }

    void onManualCounterUpdated() {
        printData();
    }

    void printData() {
        System.out.println("Counter: " + getData().counter + ", Manual Counter: " + getData().manualCounter);
    }

    void incrementManualCounter() {
        publish("manual-counter", "increment");
    }

    void decrementManualCounter() {
        publish("manual-counter", "decrement");
    }
}
