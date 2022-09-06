# croquet-proxy-client
A Java client for the Croquet proxy.

## Usage
1. Clone the repository
2. Create your own ModelData class
```java
// CounterModel.java
import croquet.proxy.client.ModelData;

public class CounterModel extends ModelData {
    public int counter = 0;
}
```
3. Create the View class
```java
// CounterView.java
public class CounterView extends View<CounterModel> {

    public CounterView() {
        super(CounterModel.class);
        subscribe("counter", "updated", this::onCounterUpdated);
    }

    void onCounterUpdated() {
        System.out.println("Counter: " + this.getData().counter);
    }
}
```
4. Create the main class
```java
// Main.java
public class Main {
    public static void main(String[] args) {
        final CounterView view = new CounterView();
        view.start();
    }
}
```
5. Start the [croquet-proxy](https://github.com/alemazzo/croquet-proxy) server that run the Model
6. Run the main class
