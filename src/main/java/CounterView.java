public class CounterView extends View<CounterModel> {

    public CounterView() {
        super(CounterModel.class);
        subscribe("counter", "updated", this::onCounterUpdated);
    }

    void onCounterUpdated() {
        System.out.println("Counter: " + this.getData().counter);
    }
}
