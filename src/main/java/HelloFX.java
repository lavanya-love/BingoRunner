import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloFX extends Application {

  @Override
  public void start(Stage primaryStage) {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 500, 500);

    NumberGenerator generator = new NumberGenerator();
    NumberDisplay display = new NumberDisplay(root, generator);
    StartButton startBtn = new StartButton(generator, display);

    root.setTop(startBtn);
    root.setAlignment(startBtn, Pos.CENTER);
    root.setPadding(new Insets(20, 20, 20, 20));

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

class NumberGenerator {
  private List<Integer> numbers = new ArrayList<>();

  public List<Integer> getNumbers() {
    return numbers;
  }

  public void generateNumbers() {
    // Generate an array of 36 random numbers within the range 1-49 (inclusive) with no repetitions.
    Random random = new Random();
    for (int i = 0; i < 36; i++) {
      int number = random.nextInt(49) + 1;
      if (!numbers.contains(number)) {
        numbers.add(number);
      } else {
        i--;
      }
    }
  }
}

class StartButton extends Button {
  public StartButton(NumberGenerator generator, NumberDisplay display) {
    super("Start Calling");
    setOnAction(
        e -> {
          generator.generateNumbers();
          display.displayNumbers();
        });
  }
}

class NumberDisplay {
  private BorderPane root;
  private int gridRow = 0, gridColumn = 0;
  private NumberGenerator generator;

  public NumberDisplay(BorderPane root, NumberGenerator generator) {
    this.root = root;
    this.generator = generator;
  }

  public void displayNumbers() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 20, 20, 20));
    root.setCenter(grid);

    // Create the "Current Call" area
    BorderPane currentCall = new BorderPane();
    currentCall.setStyle("-fx-background-color: lightgray");
    currentCall.setMinHeight(100);
    root.setTop(currentCall);

    // Display each number from the sequence
    new Thread(
            () -> {
              List<Integer> numbers = generator.getNumbers();
              for (int i = 0; i < 36; i++) {
                int number = numbers.get(i);

                int finalI = i;
                Platform.runLater(
                    () -> {
                      Text callText = new Text("Call " + (finalI + 1) + " Out of 36");
                      callText.setFont(new Font(20));
                      currentCall.setTop(callText);
                      currentCall.setAlignment(callText, Pos.TOP_CENTER);

                      Text numberText = new Text(String.valueOf(number));
                      numberText.setFont(new Font(40));
                      currentCall.setCenter(numberText);
                      currentCall.setAlignment(numberText, Pos.CENTER);

                      DropShadow dropShadow = new DropShadow();
                      dropShadow.setRadius(5.0);
                      dropShadow.setOffsetX(3.0);
                      dropShadow.setOffsetY(3.0);
                      dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                      numberText.setEffect(dropShadow);

                      Label label = new Label(String.valueOf(number));

                      grid.add(label, gridRow++, gridColumn);
                      if (gridRow == 6) {
                        gridRow = 0;
                        gridColumn++;
                      }

                      // Add animation to number label
                      ScaleTransition scaleTransition =
                          new ScaleTransition(Duration.seconds(1), label);
                      scaleTransition.setToX(1.5);
                      scaleTransition.setToY(1.5);
                      scaleTransition.setAutoReverse(true);
                      scaleTransition.setCycleCount(2);
                      scaleTransition.play();
                    });

                try {
                  Thread.sleep(1000);
                } catch (InterruptedException ex) {
                  ex.printStackTrace();
                }
              }
            })
        .start();
  }
}
