import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final CounterView view = new CounterView();
        view.start();

        final Scanner scanner = new Scanner(System.in);
        while(true) {
            // Read from stdin
            final String line = scanner.nextLine();
            switch (line) {
                case "+":
                    view.incrementManualCounter();
                    break;
                case "-":
                    view.decrementManualCounter();
                    break;
                case "q":
                    return;
            }
        }
    }
}