package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static java.util.stream.Collectors.toList;

/**
 * This class is the Main class for JavaFx application
 * 
 * @author Jesse, Archer
 *
 */
public class Main extends Application {

  private final String lineSeparator = System.lineSeparator();

  // Spectating caret Position
  int caretPosition;

  // Spectating focusedTextField
  private TextField focusedTextField = null;

  // Whether is undergoing analyzing process
  private boolean analyze = false;

  // Result shower
  BorderPane resultShower = new BorderPane();

  // CSS style for label
  String labelStyle =
      "-fx-font-size: 16px;-fx-text-fill: #333333;-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-border-color: black;";

  // lists storing steps
  List<CalSteps> lists = null;

  // State
  boolean state = false;

  // Correct?
  boolean correctness = true;

  // Recorder of buttons
  Button latestMOpera = null;
  
  // Recorder of results
  String resultNum = null;
  List<String[][]> results = new ArrayList<>();
  
  /**
   * This is the start method of the Main class
   * 
   * @param primaryStage the main Stage
   */
  @Override
  public void start(Stage primaryStage) {

    // Set the title of the primaryStage
    primaryStage.setTitle("Matrix Calculator - Developed by ateam2");

    try {
      // Set the application icon
      primaryStage.getIcons()
                  .add(new Image(getClass().getResource("calculator.png")
                                           .toExternalForm()));
    } catch (Exception e) {

    }

    resultShower.setStyle("-fx-background-color: lightgray;");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");

    // Main layout
    BorderPane root = new BorderPane();

    // Add to the top of root pane
    MenuBar menuBar = new MenuBar();

    // Add related MenuBar, Menu, and MenuItem

    // Set open file tag
    Menu menu = new Menu("Menu");
    MenuItem open = new MenuItem("Open");

    // Set save file tag
    MenuItem save = new MenuItem("Save");

    // Set Exit tag
    MenuItem exit = new MenuItem("Exit");
    exit.setOnAction(event -> System.exit(0));
    menu.getItems().addAll(open, save, exit);

    // Set About Tag with alert
    Menu about = new Menu("About");
    MenuItem developer = new MenuItem("Developer");
    developer.setOnAction(event -> {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("About Developer");
      alert.setHeaderText("Matrix-Calculator");
      alert.setContentText("Developedby:        " + lineSeparator
          + "    Chengpo Yan - cyan46@wisc.edu" + lineSeparator
          + "    Jinming Zhang - jzhang2279@wisc.edu" + lineSeparator
          + "    Zexin Li - zli885@wisc.edu" + lineSeparator
          + "    Houming Chen - hchen634@wisc.edu" + lineSeparator
          + "    Chengxu Bian - cbian4@wisc.edu");
      alert.showAndWait();
    });
    about.getItems().add(developer);

    menuBar.getMenus().addAll(menu, about);

    // Set the top scene
    HBox selector = new HBox();
    Button forward = new Button("<");
    TextField pages = new TextField();
    pages.setMaxWidth(40);
    Label slash = new Label("/");
    slash.setStyle(
        "-fx-font-size: 8px;-fx-text-fill: #333333;-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-border-color: black;");
    TextField total = new TextField();
    total.setMaxWidth(40);
    total.setEditable(false);
    Button backward = new Button(">");
    Button confirm = new Button("\u221A");
    Button quit = new Button("Quit");
    selector.getChildren()
            .addAll(forward, pages, slash, total, backward, confirm, quit);
    selector.alignmentProperty().set(Pos.CENTER);

    VBox vBox = new VBox();
    vBox.getChildren().addAll(menuBar, selector);
    root.setTop(vBox);
    selector.setDisable(true);

    // Set the left scene
    VBox vBoxL = new VBox();
    TextField input = new TextField();
    input.setMaxWidth(360.0);
    TextArea result = new TextArea();
    result.setWrapText(true);
    result.setEditable(false);
    result.setMaxWidth(360.0);
    result.setMinHeight(220.0);

    focusedTextField = input;

    // Set two parallel buttons
    HBox hBoxL = new HBox();
    Button analyzeSequence = new Button("Analyze Sequence");
    analyzeSequence.setMinWidth(180);
    analyzeSequence.setMinHeight(44);
    Button space = new Button("Space");
    space.setMinWidth(180);
    space.setMinHeight(44);

    // Activated under analyzing sequence
    space.setDisable(true);
    hBoxL.getChildren().addAll(analyzeSequence, space);

    // Set the gridPane
    GridPane gridPaneL = new GridPane();
    List<Button> buttons =
        List.of("\u03c0", "   e   ", "   C   ", "  <-   ", "   (   ", "   )   ",
            "  |x|  ", "   /   ", "   7   ", "   8   ", "   9   ", "   *   ",
            "   4   ", "   5   ", "   6   ", "   -   ", "   1   ", "   2   ",
            "   3   ", "   +   ", "  +/-  ", "   0   ", "   .   ", "   =   ")
            .stream()
            .map(Button::new)
            .collect(toList());
    int number = 0;
    for (int row = 0; row < 6; row++) {
      for (int column = 0; column < 4; column++) {
        Button button = buttons.get(number++);
        button.setMinSize(90.0, 40.0);
        gridPaneL.add(button, column, row);
      }
    }

    List<Button> notNumber =
        buttons.stream()
               .filter(b -> !(b.getText().trim().matches("\\d")
                   || b.getText().trim().matches("\\.")
                   || b.getText().trim().matches("\\+\\/\\-")))
               .collect(toList());

    // Set the caretPosition
    input.setOnMouseClicked(e -> {
      caretPosition = input.getCaretPosition();
      focusedTextField = input;
    });

    // Add event handler to the buttons
    buttons.stream().forEach(btn -> {
      btn.setOnAction(event -> {
        String temp = btn.getText().trim();

        if (temp.equals("C")) {
          input.clear();
          caretPosition = 0;
        } else if (temp.equals("<-")) {
          try {
            focusedTextField.setText(
                focusedTextField.getText().substring(0, caretPosition - 1)
                    + focusedTextField.getText().substring(caretPosition));
            caretPosition--;
          } catch (Exception e) {

          }
        } else if (temp.equals("+/-")) {
          try {
            String fromInput = focusedTextField.getText();
            focusedTextField.setText(
                fromInput.startsWith("-") ? fromInput.substring(1)
                    : "-" + fromInput);
          } catch (Exception e) {

          }
        } else if (temp.equals("=")) {
          try {
            if (!analyze) {
              result.appendText(input.getText() + "\n="
                  + Calculator.calcul("0" + input.getText()) + "\n");
            } else {
              result.appendText(SequenceSummary.summary(input.getText()));
              analyze = false;
              space.setDisable(true);
              notNumber.stream().forEach(b -> b.setDisable(false));
              input.setOnMouseEntered(e -> {
                notNumber.stream().forEach(b -> b.setDisable(false));
              });
            }
          } catch (Exception e) {
            alert("Wrong Expression",
                "The equation you entered cannot be calculated\nPlease press 'C' and try again");
          }
        } else if (temp.matches("\\d") || temp.matches("\\.")) {
          try {
            focusedTextField.insertText(caretPosition, temp.trim());
            ++caretPosition;
          } catch (Exception e) {

          }
        } else {
          try {
            input.insertText(caretPosition, temp.replace("x", "").trim());
            ++caretPosition;
          } catch (Exception e) {

          }
        }
      });
    });

    // Add to the root
    vBoxL.getChildren().addAll(input, result, hBoxL, gridPaneL);
    root.setLeft(vBoxL);

    // Set the right scene
    VBox vBoxR = new VBox();

    // Set for Right and Left Disabling
    vBoxR.setOnMouseEntered(e -> {
      notNumber.stream().forEach(b -> b.setDisable(true));
      buttons.get(3).setDisable(false);
    });
    input.setOnMouseEntered(e -> {
      notNumber.stream().forEach(b -> b.setDisable(false));
    });
    result.setOnMouseClicked(e -> {
      notNumber.stream().forEach(b -> b.setDisable(false));
    });

    // Set for Sequence Actions
    analyzeSequence.setOnMouseClicked(e -> {
      analyze = true;
      space.setDisable(false);
      notNumber.stream().forEach(b -> b.setDisable(true));
      buttons.get(buttons.size() - 1).setDisable(false);
      input.setOnMouseEntered(event -> {
        buttons.get(buttons.size() - 1).setDisable(false);
      });
    });
    space.setOnAction(event -> {
      try {
        input.insertText(caretPosition, " ");;
        ++caretPosition;
      } catch (Exception e) {

      }
    });


    BorderPane matrixes = new BorderPane();

    // Set the Matrix Panel
    List<TextField> matrix1Data = new ArrayList<>();
    List<TextField> matrix2Data = new ArrayList<>();
    List<TextField> rowAndCol1 = new ArrayList<>();
    List<TextField> rowAndCol2 = new ArrayList<>();
    VBox matrix1 = matrixGenerator(matrix1Data, rowAndCol1);
    VBox matrix2 = matrixGenerator(matrix2Data, rowAndCol2);

    rowAndCol1.stream().forEach(t -> t.setOnMouseClicked(e -> {
      if (t.isFocused()) {
        caretPosition = 1;
        focusedTextField = t;
      }
    }));
    rowAndCol2.stream().forEach(t -> t.setOnMouseClicked(e -> {
      if (t.isFocused()) {
        caretPosition = 1;
        focusedTextField = t;
      }
    }));

    // Should be enable when needed
    matrix2.setDisable(true);

    // Set the operation of Two Matrixes
    GridPane matrixOperators = new GridPane();
    CheckBox enableSecond = new CheckBox("?");

    matrixOperators.add(enableSecond, 0, 0);
    Button c1 = new Button("c1");
    c1.setMinWidth(35);
    matrixOperators.add(c1, 0, 1);
    List<Button> operators = List.of("c2", "+", "-", "*").stream().map(str -> {
      Button temp = new Button(str);
      temp.setMinWidth(35);
      return temp;
    }).collect(toList());
    for (int i = 0; i < 4; i++) {
      matrixOperators.add(operators.get(i), 0, i + 2);
    }
    matrixOperators.setVgap(19);
    operators.stream().forEach(b -> b.setDisable(true));
    matrix1.setMinWidth(400);
    matrix2.setMinWidth(400);
    matrix1.setMinHeight(233);
    matrix2.setMinHeight(233);
    matrixes.setLeft(matrix1);
    HBox cAndR = new HBox();
    cAndR.getChildren().addAll(matrixOperators, matrix2);
    matrixes.setCenter(cAndR);

    // Set the EventHandler for matrixOperators
    c1.setOnMouseClicked(event -> {
      matrix1Data.stream().forEach(TextField::clear);
    });
    operators.get(0).setOnMouseClicked(event -> {
      matrix2Data.stream().forEach(TextField::clear);
    });

    // Set the operation panel
    GridPane mOperations = new GridPane();
    mOperations.setHgap(145);
    mOperations.setVgap(10);

    // Set the Operations of one Matrix
    List<Button> mButtons = List.of("Det", "Inverse", "QR", "SVD", "Trace",
        "LUP", "Gauss-Elim", "Diagonalize", "EiValue", "Rank", "Transpose")
                                .stream()
                                .map(operator -> {
                                  Button temp = new Button(operator);
                                  temp.setMinWidth(100);
                                  return temp;
                                })
                                .collect(toList());

    // Add to the GridPane
    int count = 0;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 4; j++) {
        mOperations.add(mButtons.get(count++), j, i);
        if (count == 11) {
          break;
        }
      }
    }

    // Add the special Power button
    HBox power = new HBox();
    Button powerButton = new Button("Power");
    TextField powerInput = new TextField();
    powerInput.setMaxWidth(55);
    power.getChildren().addAll(powerButton, powerInput);
    mOperations.add(power, 3, 2);


    // Add eventListener of enableSecond
    enableSecond.setOnAction(event -> {
      if (enableSecond.isSelected()) {
        mOperations.setDisable(true);
        operators.stream().forEach(b -> b.setDisable(false));
        matrix2.setDisable(false);
      } else {
        mOperations.setDisable(false);
        operators.stream().forEach(b -> b.setDisable(true));
        matrix2.setDisable(true);
      }
    });


    resultShower.setMinHeight(207);
    resultShower.setMaxWidth(836);
    try {
      // Add Operations related to MatrixCalculator
      operators.get(1).setOnAction(event -> {
        latestMOpera = operators.get(1);
        String[][] dataFromMatrix1 = reader(matrix1Data, rowAndCol1);
        String[][] dataFromMatrix2 = reader(matrix2Data, rowAndCol2);
        MatrixCalculator matrixCalculator =
            new MatrixCalculator(dataFromMatrix1, dataFromMatrix2);
        try {
          String[][] resultMatrix = matrixCalculator.add();
          results.clear();
          results.add(resultMatrix);
          resultShower = resultBuilder("Operation: Add", "+", dataFromMatrix1,
              dataFromMatrix2, resultMatrix);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e1) {
          correctness = false;
          alert("MatrixDimensionError",
              "The dimensions of the Matrixs you entered did not match");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      operators.get(2).setOnAction(event -> {
        latestMOpera = operators.get(2);
        String[][] dataFromMatrix1 = reader(matrix1Data, rowAndCol1);
        String[][] dataFromMatrix2 = reader(matrix2Data, rowAndCol2);
        MatrixCalculator matrixCalculator =
            new MatrixCalculator(dataFromMatrix1, dataFromMatrix2);
        try {
          String[][] resultMatrix = matrixCalculator.subtract();
          results.clear();
          results.add(resultMatrix);
          resultShower = resultBuilder("Operation: Subtract", "-",
              dataFromMatrix1, dataFromMatrix2, resultMatrix);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e1) {
          correctness = false;
          alert("MatrixDimensionError",
              "The dimensions of the Matrixs you entered did not match");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      operators.get(3).setOnAction(event -> {
        latestMOpera = operators.get(3);
        String[][] dataFromMatrix1 = reader(matrix1Data, rowAndCol1);
        String[][] dataFromMatrix2 = reader(matrix2Data, rowAndCol2);
        MatrixCalculator matrixCalculator =
            new MatrixCalculator(dataFromMatrix1, dataFromMatrix2);
        try {
          String[][] resultMatrix = matrixCalculator.multiply();
          results.clear();
          results.add(resultMatrix);
          resultShower = resultBuilder("Operation: Multiply", "*",
              dataFromMatrix1, dataFromMatrix2, resultMatrix);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e1) {
          correctness = false;
          alert("MatrixDimensionError",
              "The dimensions of the Matrixs you entered did not match");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      // Add EventHandler to special matrix operation
      mButtons.get(0).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(0);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          MatrixCalculator matrix = new MatrixCalculator(dataFromMatrix);
          String resultDeterminant = matrix.getDeterminant();
          resultNum = resultDeterminant;
          resultShower = resultBuilder("Operation: Det", "Determinant",
              dataFromMatrix, resultDeterminant);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e1) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry, the matrix you entered is not a square matrix\nTo compute the determinant of a matrix, it has to be a square matrix");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      mButtons.get(1).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(1);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          Matrix matrix = new Matrix(dataFromMatrix);
          String[][] resultInverse = matrix.inverse().toStringMatrix();
          results.clear();
          results.add(resultInverse);
          resultShower = resultBuilder("Operation: Inverse", "Inverse",
              dataFromMatrix, resultInverse);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry, the matrix you entered is not a square matrix\nTo compute the inverse of a matrix, it has to be a square matrix");
        } catch (MatrixArithmeticException e2) {
          correctness = false;
          alert("MatriArithmeticError",
              "Sorry, the matrix you entered is not invertible");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      mButtons.get(2).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(2);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          Matrix matrix = new Matrix(dataFromMatrix);
          List<String[][]> resultQR = Arrays.stream(matrix.QRDecomposition())
                                            .map(Matrix::toStringMatrix)
                                            .collect(toList());
          results.addAll(resultQR);
          resultShower = resultBuilderQR("Operation: QR", "QR", dataFromMatrix,
              resultQR.get(0), resultQR.get(1));
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry,  the matrix you entered is not a square matrix\nTo do QR decomposition, it has to be a square matrix");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      // mButtons.get(3).setOnAction(event -> {
      // try {
//      latestMOpera = mButtons.get(3);
      // String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
      // Matrix matrix = new Matrix(dataFromMatrix);
      // List<String[][]> resultSVD = Arrays.stream(matrix.???)
      // .map(Matrix::toStringMatrix)
      // .collect(toList());
      // results.addAll(resultSVD);
      // resultShower = resultBuilderSVD("Operation: SVD", "SVD",
      // dataFromMatrix, resultInverse.get(0), resultInverse.get(1),
      // resultInverse.get(2));
      // scrollPane(vBoxR, resultShower);
      // correctness = true;
      // } catch (MatrixDimensionsMismatchException e) {
      // correctness = false;
      // alert("MatrixDimensionError",
      // "Sorry, the matrix you entered cannot perform SVD decomposition");
      // } catch(NumberFormatException e1) {
      // alert("Error", "Your input may contain invalid characters or empty");
      // }
      // });

      mButtons.get(4).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(4);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          MatrixCalculator matrix = new MatrixCalculator(dataFromMatrix);
          String resultTrace = matrix.getTrace();
          resultNum = resultTrace;
          resultShower = resultBuilder("Operation: Trace", "Trace",
              dataFromMatrix, resultTrace);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry, the matrix you entered cannot perform trace");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      mButtons.get(5).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(5);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          Matrix matrix = new Matrix(dataFromMatrix);
          List<String[][]> resultLUP = Arrays.stream(matrix.LUPDecomposition())
                                             .map(Matrix::toStringMatrix)
                                             .collect(toList());
          results.addAll(resultLUP);
          resultShower =
              resultBuilderLUP("Operation: LUP", "LUP", resultLUP.get(2),
                  dataFromMatrix, resultLUP.get(0), resultLUP.get(1));
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry, the matrix you entered cannot perform LUP decomposition");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      mButtons.get(6).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(6);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          String[][] resultGE =
              new MatrixCalculator(dataFromMatrix).getGuassianElimination();
          results.clear();
          results.add(resultGE);
          resultShower =
              resultBuilder("Operation: GE", "GE", dataFromMatrix, resultGE);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (SingularException e) {
          correctness = false;
          alert("Error", "The Matrix you entered is singular");
        } catch (NumberFormatException e) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      // mButtons.get(7).setOnAction(event -> {
      // try {
//      latestMOpera = mButtons.get(7);
      // String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
      // Matrix matrix = new Matrix(dataFromMatrix);
      // String[][] resultDI = matrix.???
      // results.clear();
      // results.add(resultDI);
      // resultShower =
      // resultBuilder("Operation: DI", "DI", dataFromMatrix, resultDI);
      // scrollPane(vBoxR, resultShower);
      // correctness = true;
      // } catch(NumberFormatException e) {
      // correctness = false;
      // alert("Error", "Your input may contain invalid characters or empty");
      // }
      // });

      mButtons.get(8).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(8);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          Matrix matrix = new Matrix(dataFromMatrix);
          double resultEIV = matrix.eigenValues()[0].doubleValue();
          resultNum = String.valueOf(resultEIV);
          resultShower =
              resultBuilder("Operation: EIV", "EIV", dataFromMatrix, resultEIV);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (MatrixDimensionsMismatchException e) {
          correctness = false;
          alert("MatrixDimensionError",
              "Sorry, the matrix you entered is not a square matrix\nTo compute the eigenvalue of a matrix, it has to be a square matrix");
        } catch (NumberFormatException e1) {
          correctness = false;
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      // mButtons.get(9).setOnAction(event -> {
      // try {
      // latestMOpera = mButtons.get(9);
      // String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
      // Matrix matrix = new Matrix(dataFromMatrix);
      // int resultRank = matrix.???;
      // resultNum = String.valueOf(resultRank);
      // resultShower =
      // resultBuilder("Operation: Rank", "Rank", dataFromMatrix, resultRank);
      // scrollPane(vBoxR, resultShower);
      // correctness = true;
      // } catch (NumberFormatException e) {
      // correctness = false;
      // alert("Error", "Your input may contain invalid characters or empty");
      // }
      // });

      mButtons.get(10).setOnAction(event -> {
        try {
          latestMOpera = mButtons.get(10);
          String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
          Matrix matrix = new Matrix(dataFromMatrix);
          String[][] resultTS = matrix.transpose().toStringMatrix();
          results.clear();
          results.add(resultTS);
          resultShower =
              resultBuilder("Operation: TS", "TS", dataFromMatrix, resultTS);
          scrollPane(vBoxR, resultShower);
          correctness = true;
        } catch (NumberFormatException e) {
          alert("Error", "Your input may contain invalid characters or empty");
        }
      });

      powerButton.setOnAction(event -> {
        try {
          latestMOpera = powerButton;
          int n = Integer.parseInt(powerInput.getText());
          try {
            String[][] dataFromMatrix = reader(matrix1Data, rowAndCol1);
            Matrix matrix = new Matrix(dataFromMatrix);
            String[][] resultPw = matrix.pow(n).toStringMatrix();
            results.clear();
            results.add(resultPw);
            resultShower = resultBuilder("Operation: POWER", "PowerOf " + n,
                dataFromMatrix, resultPw);
            scrollPane(vBoxR, resultShower);
            correctness = true;
          } catch (MatrixDimensionsMismatchException e1) {
            correctness = false;
            alert("MatrixDimensionError",
                "Sorry, the matrix you entered is not a square matrix\nTo compute the power of a matrix, it has to be a square matrix");
          } catch (MatrixArithmeticException e2) {
            correctness = false;
            alert("MatriArithmeticError",
                "Sorry, the matrix you entered is non-invertible, so it does not have negative exponent");
          } catch (NumberFormatException e3) {
            correctness = false;
            alert("Error",
                "Your input may contain invalid characters or empty");
          } catch (ArithmeticException e4) {
            correctness = false;
            alert("Error", "Sorry, Exception: " + e4.getMessage());
          }
        } catch (NumberFormatException e) {
          correctness = false;
          alert("NumberFormatError",
              "Sorry, the number you entered is not an Integer");
        }
      });
    } catch (Exception e) {
      correctness = false;
      alert("Error", "Your input may contain invalid characters or empty");
    }

    // Add to the overall panel
    vBoxR.getChildren().addAll(matrixes, mOperations, resultShower);
    root.setRight(vBoxR);

    // Set the action for FileChooser-open
    open.setOnAction(event -> {
      File file = fileChooser.showOpenDialog(primaryStage);
      if (file == null || !file.getName().endsWith(".json")) {
        alert("Error: File name mismatch", "Please rechoose the file"
            + lineSeparator + "The name of the file must end with '.json'!");
      } else {
        // Invoke Parser
        try {
          OpeartionParser parser = new OpeartionParser(file.getName());
          lists = parser.getCalculations();
          selector.setDisable(false);
          total.setText(String.valueOf(lists.size()));
          pages.setText("1");
          state = false;
          correctness = true;
          confirm.fire();
        } catch (MatrixDimensionsMismatchException e1) {
          alert("Error", "Matrix Dimension Mismatch");
        } catch (ParseException e2) {
          alert("Error", "Parse fail, please check you .json file");
        } catch (IOException e3) {
          alert("Error", "Fatal issues during IO processing");
        }
      }
    });

    confirm.setOnAction(event -> {
      try {
        if (state && correctness) {
          int page = Integer.parseInt(pages.getText());
          Matrix wMatrix1 = new Matrix(reader(matrix1Data, rowAndCol1));
          if(enableSecond.isSelected()) {
            
          }
        }
        state = false;
        correctness = true;
        int page = Integer.parseInt(pages.getText());
        if (page < 1 || page > lists.size()) {
          throw new IllegalArgumentException();
        }
        CalSteps step = lists.get(page - 1);
        String operationOperator = step.getOperation();
        switch (operationOperator) {
          case "+":
            setterOfTwoMatrixes(step, rowAndCol1, rowAndCol2, matrix1Data,
                matrix2Data);
            if (!enableSecond.isSelected()) {
              enableSecond.fire();
            }
            operators.get(1).fire();
            break;
          case "-":
            setterOfTwoMatrixes(step, rowAndCol1, rowAndCol2, matrix1Data,
                matrix2Data);
            if (!enableSecond.isSelected()) {
              enableSecond.fire();
            }
            operators.get(2).fire();
            break;
          case "*":
            setterOfTwoMatrixes(step, rowAndCol1, rowAndCol2, matrix1Data,
                matrix2Data);
            if (!enableSecond.isSelected()) {
              enableSecond.fire();
            }
            operators.get(3).fire();
            break;
          case "Det":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(0).fire();
            break;
          case "Inverse":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(1).fire();
            break;
          case "QR":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(2).fire();
            break;
          case "SVD":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(3).fire();
            break;
          case "Trace":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(4).fire();
            break;
          case "LUP":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(5).fire();
            break;
          case "GE":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(6).fire();
            break;
          case "Diag":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(7).fire();
            break;
          case "EIV":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(8).fire();
            break;
          case "Rank":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(9).fire();
            break;
          case "Trans":
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            mButtons.get(10).fire();
            break;
          default:
            String powerString = operationOperator.replace("PowerOf", "");
            cleanAndSet(matrix1Data, rowAndCol1, rowAndCol2, enableSecond,
                step);
            powerInput.setText(powerString);
            powerButton.fire();
            break;
        }
      } catch (IllegalArgumentException e1) {
        alert("Error", "The page number you entered is invalid");
      } catch (Exception e) {
        alert("Error",
            "Your json file contains invalid operations, please rechoose the file");
      }
    });

    forward.setOnAction(event -> {
      try {
        int num = Integer.parseInt(pages.getText());
        if (num != 1) {
          num -= 1;
          pages.setText(String.valueOf(num));
          confirm.fire();
        }
      } catch (Exception e) {
        alert("Error", "The page number you entered is invalid");
      }
    });

    backward.setOnAction(event -> {
      try {
        int num = Integer.parseInt(pages.getText());
        if (num != lists.size()) {
          num += 1;
          pages.setText(String.valueOf(num));
          confirm.fire();
        }
      } catch (Exception e) {
        alert("Error", "The page number you entered is invalid");
      }
    });

    // Set the action for FileChooser-save
    save.setOnAction(event -> {
      File file = fileChooser.showSaveDialog(primaryStage);
      if (file == null || !file.getName().endsWith(".json")) {
        alert("Error: File name mismatch", "Please rechoose the file"
            + lineSeparator + "The name of the file must end with '.json'!");
      } else {
        // Invoke Parser
      }
    });

    // Use the optimized width and height
    Scene mainScene =
        new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
    primaryStage.setScene(mainScene);
    primaryStage.setResizable(false);
    try {
      mainScene.getStylesheets()
               .add(getClass().getResource("styleSheet.css").toExternalForm());
    } catch (Exception e) {

    }
    primaryStage.show();
  }

  /**
   * Routine steps
   * 
   * @param matrix1Data  data from matrix 1
   * @param rowAndCol1   row and column textfield
   * @param rowAndCol2   row and column textfield
   * @param enableSecond the checkbox
   * @param step         information
   */
  private void cleanAndSet(List<TextField> matrix1Data,
      List<TextField> rowAndCol1, List<TextField> rowAndCol2,
      CheckBox enableSecond, CalSteps step) {
    clearer(rowAndCol2);
    setterOfSingleMatrix(step, rowAndCol1, matrix1Data);
    if (enableSecond.isSelected()) {
      enableSecond.fire();
    }
  }

  /**
   * Reset the second Matrix
   * 
   * @param rowAndCol2 row and column textfield
   */
  private void clearer(List<TextField> rowAndCol2) {
    rowAndCol2.get(0).setText("3");
    rowAndCol2.get(1).setText("3");
  }

  /**
   * Setter for Single Matrix environment
   * 
   * @param step        information
   * @param rowAndCol1  row and column textfield
   * @param matrix1Data data from matrix1
   */
  private void setterOfSingleMatrix(CalSteps step, List<TextField> rowAndCol1,
      List<TextField> matrix1Data) {
    Matrix matrix1 = step.getDatas().get(0);
    rowAndCol1.get(0).setText(String.valueOf(matrix1.getNumberOfRow()));
    rowAndCol1.get(1).setText(String.valueOf(matrix1.getNumberOfColumn()));
    int count = 0;
    for (int i = 0; i < matrix1.getNumberOfRow(); i++) {
      for (int j = 0; j < matrix1.getNumberOfColumn(); j++) {
        matrix1Data.get(count++).setText(matrix1.getEntry(i, j).toString());
      }
    }
  }

  /**
   * Setter for Double Matrixes environment
   * 
   * @param step        information
   * @param matrix2Data data from matrix2
   * @param matrix1Data data from matrix1
   * @param rowAndCol2  row and column textfield
   * @param rowAndCol1  row and column textfield
   */
  private void setterOfTwoMatrixes(CalSteps step, List<TextField> rowAndCol1,
      List<TextField> rowAndCol2, List<TextField> matrix1Data,
      List<TextField> matrix2Data) {
    Matrix matrix1 = step.getDatas().get(0);
    Matrix matrix2 = step.getDatas().get(1);
    rowAndCol1.get(0).setText(String.valueOf(matrix1.getNumberOfRow()));
    rowAndCol1.get(1).setText(String.valueOf(matrix1.getNumberOfColumn()));
    rowAndCol2.get(0).setText(String.valueOf(matrix2.getNumberOfRow()));
    rowAndCol2.get(1).setText(String.valueOf(matrix2.getNumberOfColumn()));
    int count = 0;
    for (int i = 0; i < matrix1.getNumberOfRow(); i++) {
      for (int j = 0; j < matrix1.getNumberOfColumn(); j++) {
        matrix1Data.get(count++).setText(matrix1.getEntry(i, j).toString());
      }
    }
    count = 0;
    for (int i = 0; i < matrix2.getNumberOfRow(); i++) {
      for (int j = 0; j < matrix2.getNumberOfColumn(); j++) {
        matrix2Data.get(count++).setText(matrix2.getEntry(i, j).toString());
      }
    }
  }

  /**
   * Add scrollPane
   * 
   * @param vBoxR        vBox
   * @param resultShower result
   */
  private void scrollPane(VBox vBoxR, BorderPane resultShower) {
    ScrollPane sP = new ScrollPane(resultShower);
    sP.setStyle("-fx-background-color: lightgray;");
    sP.setMinHeight(207);
    sP.setMaxHeight(207);
    sP.setMaxWidth(836);
    vBoxR.getChildren().remove(2);
    vBoxR.getChildren().add(sP);
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string         operation
   * @param  mathString     operation
   * @param  dataFromMatrix source Matrix
   * @param  resultTrace    the result
   * @return                resulted BorderPane
   */
  private BorderPane resultBuilder(String string, String mathString,
      String[][] dataFromMatrix, String result) {
    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);
    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    GridPane gridSrc = matrixGenerator(dataFromMatrix);

    Label resultedLabel = new Label(result);
    resultedLabel.setStyle(labelStyle);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren()
                .addAll(operationMath, gridSrc, equals, resultedLabel);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string         operation
   * @param  mathString     operation
   * @param  dataFromMatrix source Matrix
   * @param  l              the L
   * @param  u              the U
   * @param  p              the P
   * @return                resulted BorderPane
   */
  private BorderPane resultBuilderLUP(String string, String mathString,
      String[][] p, String[][] dataFromMatrix, String[][] l, String[][] u) {
    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);

    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    Label multiply1 = new Label("*");
    equals.setStyle(labelStyle);

    Label multiply2 = new Label("*");
    equals.setStyle(labelStyle);

    GridPane gridSrc = matrixGenerator(dataFromMatrix);
    GridPane lResult = matrixGenerator(l);
    GridPane uResult = matrixGenerator(u);
    GridPane pResult = matrixGenerator(p);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren()
                .addAll(operationMath, pResult, multiply1, gridSrc, equals,
                    lResult, multiply2, uResult);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string         operation
   * @param  mathString     operation
   * @param  dataFromMatrix source Matrix
   * @param  q              the Q
   * @param  r              the R
   * @return                resulted BorderPane
   */
  private BorderPane resultBuilderQR(String string, String mathString,
      String[][] dataFromMatrix, String[][] q, String[][] r) {
    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);
    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    Label multiply = new Label("*");
    equals.setStyle(labelStyle);

    GridPane gridSrc = matrixGenerator(dataFromMatrix);
    GridPane qResult = matrixGenerator(q);
    GridPane rResult = matrixGenerator(r);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren()
                .addAll(operationMath, gridSrc, equals, qResult, multiply,
                    rResult);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string         operation
   * @param  mathString     operation
   * @param  dataFromMatrix source Matrix
   * @param  result         the result
   * @return                resulted BorderPane
   */
  private BorderPane resultBuilder(String string, String mathString,
      String[][] dataFromMatrix, String[][] result) {
    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);
    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    GridPane gridSrc = matrixGenerator(dataFromMatrix);
    GridPane resultG = matrixGenerator(result);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren().addAll(operationMath, gridSrc, equals, resultG);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string         operation
   * @param  mathString     operation
   * @param  dataFromMatrix source Matrix
   * @param  result         the result
   * @return                resulted BorderPane
   */
  private BorderPane resultBuilder(String string, String mathString,
      String[][] dataFromMatrix, double result) {
    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);
    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    GridPane gridSrc = matrixGenerator(dataFromMatrix);

    Label resultedLabel = new Label(String.valueOf(result));
    resultedLabel.setStyle(labelStyle);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren()
                .addAll(operationMath, gridSrc, equals, resultedLabel);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Method that returns a BorderPane of finished result
   * 
   * @param  string       operation
   * @param  mathString   operation
   * @param  src1         source Matrix1
   * @param  src2         source Matrix2
   * @param  resultMatrix resulted Matrix
   * @return              resulted BorderPane
   */
  private BorderPane resultBuilder(String string, String mathString,
      String[][] src1, String[][] src2, String[][] resultMatrix) {

    BorderPane resultedPane = new BorderPane();

    resultedPane.setStyle("-fx-background-color: lightgray;");

    // Set the title of the operation
    Label operationName = new Label(string);
    operationName.setStyle(labelStyle);
    resultedPane.setTop(operationName);

    Label operationMath = new Label(mathString);
    operationMath.setStyle(labelStyle);

    Label equals = new Label("=");
    equals.setStyle(labelStyle);

    GridPane gridSrc1 = matrixGenerator(src1);
    GridPane gridSrc2 = matrixGenerator(src2);
    GridPane resultedGrid = matrixGenerator(resultMatrix);

    HBox resultedHBox = new HBox();
    resultedHBox.getChildren()
                .addAll(gridSrc1, operationMath, gridSrc2, equals,
                    resultedGrid);

    resultedPane.setCenter(resultedHBox);

    return resultedPane;
  }

  /**
   * Generate a GridPane representation of Matrix
   * 
   * @param  matrix parameter matrix
   * @return        GridPane representation of the matrix
   */
  private GridPane matrixGenerator(String matrix[][]) {

    GridPane resultedGrid = new GridPane();
    resultedGrid.setStyle(
        "-fx-background-color: lightgray;-fx-vgap: 1;-fx-hgap: 1;-fx-padding: 1;");
    resultedGrid.setMinHeight(207);
    List<Label> allLabels = new ArrayList<>();
    for (int i = 0; i < matrix.length; i++) {
      List<Label> labels = Arrays.stream(matrix[i]).map(str -> {
        Label strLabel = new Label(str);
        strLabel.setStyle(labelStyle);
        strLabel.autosize();
        return strLabel;
      }).collect(toList());
      allLabels.addAll(labels);
      for (int j = 0; j < labels.size(); j++) {
        resultedGrid.add(labels.get(j), j, i);
      }
    }

    int length = 0;
    for (Label single : allLabels) {
      int singleLength = single.getText().length();
      length = singleLength > length ? singleLength : length;
    }
    for (Label single : allLabels) {
      single.setMinWidth(length * 16);
    }
    return resultedGrid;
  }

  /**
   * Matrix's TextFields Reader
   * 
   * @param  matrix1Data
   * @param  rowAndCol1
   * @return             String[][] representation of the data within the Matrix
   */
  private String[][] reader(List<TextField> matrixData,
      List<TextField> rowAndCol) {

    int row = Integer.parseInt(rowAndCol.get(0).getText());
    int col = Integer.parseInt(rowAndCol.get(1).getText());

    String[][] stringMatrix = new String[row][col];
    int count = 0;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        stringMatrix[i][j] = matrixData.get(count++).getText();
      }
    }

    return stringMatrix;
  }

  /**
   * Generate a Matrix
   * 
   * @param  textFields
   * @param  textFields
   * @return            VBox of the Matrix
   */
  private VBox matrixGenerator(List<TextField> textFields,
      List<TextField> rowAndColumn) {

    // Create the Panel of the Matrix
    VBox vBoxMatrix = new VBox();

    // Row Label
    HBox rowMatrix = new HBox();
    Label labelRowMatrix = new Label("Row:      ");
    labelRowMatrix.setMinWidth(52);
    TextField inputRowMatrix = new TextField();
    inputRowMatrix.setMaxWidth(50);
    rowMatrix.getChildren().addAll(labelRowMatrix, inputRowMatrix);

    // Column Label
    HBox columnMatrix = new HBox();
    Label labelColumnMatrix = new Label("Column: ");
    labelColumnMatrix.setMaxWidth(52);
    TextField inputColumnMatrix = new TextField();
    inputColumnMatrix.setMaxWidth(50);
    columnMatrix.getChildren().addAll(labelColumnMatrix, inputColumnMatrix);

    rowAndColumn.add(inputRowMatrix);
    rowAndColumn.add(inputColumnMatrix);

    HBox dimension = new HBox();
    dimension.getChildren().addAll(rowMatrix, columnMatrix);

    // The GridPane for the Matrix
    GridPane gridMatrix = new GridPane();
    gridMatrix.setMaxWidth(400);
    inputRowMatrix.setText("3");
    inputColumnMatrix.setText("3");

    textFields.clear();

    // Constructing Input fields
    for (int i = 0; i < Integer.parseInt(inputRowMatrix.getText()); i++) {
      for (int j = 0; j < Integer.parseInt(inputColumnMatrix.getText()); j++) {

        TextField temp = new TextField();
        temp.textProperty().addListener(event -> {
          state = true;
        });
        correctness = false;
        textFields.add(temp);
        gridMatrix.add(temp, j, i);
      }
    }

    // Set focusedTextField
    textFields.stream().forEach(t -> t.setOnMouseClicked(e -> {
      if (t.isFocused()) {
        caretPosition = t.getCaretPosition();
        focusedTextField = t;
      }
    }));

    // Add EventListener to inputRowMatrix
    inputRowMatrix.textProperty().addListener(event -> {
      try {

        // change the state
        state = true;
        correctness = false;
        // Avoid to throw Exception when the TextField is empty
        if (inputRowMatrix.getText().equals("")) {
          return;
        }

        // For IllegalArgumentException
        if (Integer.parseInt(inputRowMatrix.getText()) <= 0
            || Integer.parseInt(inputRowMatrix.getText()) > 9) {
          throw new IllegalArgumentException();
        }

        gridMatrix.getChildren().clear();

        textFields.clear();

        // Constructing Input fields
        for (int i = 0; i < Integer.parseInt(inputRowMatrix.getText()); i++) {
          for (int j =
              0; j < Integer.parseInt(inputColumnMatrix.getText()); j++) {

            TextField temp = new TextField();
            temp.textProperty().addListener(e -> {
              state = true;
            });
            textFields.add(temp);
            gridMatrix.add(temp, j, i);
          }
        }

        // Set focusedTextField
        textFields.stream().forEach(t -> t.setOnMouseClicked(e -> {
          if (t.isFocused()) {
            caretPosition = t.getCaretPosition();
            focusedTextField = t;
          }
        }));
      } catch (Exception e) {

        // Alert when detecting IllegalArgument
        alert("Error", "Number you entered is invalid" + lineSeparator
            + "Please reenter an positive integer");
        inputRowMatrix.setText("2");
      }
    });

    // Add EventListener to inputColumnMatrix
    inputColumnMatrix.textProperty().addListener(event -> {
      try {

        // change the state
        state = true;
        correctness = false;
        // Avoid to throw Exception when the TextField is empty
        if (inputColumnMatrix.getText().equals("")) {
          return;
        }

        // For IllegalArgumentException
        if (Integer.parseInt(inputColumnMatrix.getText()) <= 0
            || Integer.parseInt(inputColumnMatrix.getText()) > 9) {
          throw new IllegalArgumentException();
        }

        gridMatrix.getChildren().clear();
        textFields.clear();

        // Constructing Input fields
        for (int i = 0; i < Integer.parseInt(inputRowMatrix.getText()); i++) {
          for (int j =
              0; j < Integer.parseInt(inputColumnMatrix.getText()); j++) {
            TextField temp = new TextField();
            temp.textProperty().addListener(e -> {
              state = true;
            });
            textFields.add(temp);
            gridMatrix.add(temp, j, i);
          }
        }

        // Set focusedTextField
        textFields.stream().forEach(t -> t.setOnMouseClicked(e -> {
          if (t.isFocused()) {
            caretPosition = t.getCaretPosition();
            focusedTextField = t;
          }
        }));
      } catch (Exception e) {

        // Alert when detecting IllegalArgument
        alert("Error", "Number you entered is invalid" + lineSeparator
            + "Please reenter an positive integer");
        inputColumnMatrix.setText("2");
      }
    });

    // Add event handler to the TextField
    inputRowMatrix.setOnKeyReleased(event -> {
      try {

        // change the state
        state = true;
        correctness = false;
        // Avoid to throw Exception when the TextField is empty
        if (inputRowMatrix.getText().equals("")) {
          return;
        }

        // For IllegalArgumentException
        if (Integer.parseInt(inputRowMatrix.getText()) <= 0
            || Integer.parseInt(inputRowMatrix.getText()) > 9) {
          throw new IllegalArgumentException();
        }

        gridMatrix.getChildren().clear();

        textFields.clear();

        // Constructing Input fields
        for (int i = 0; i < Integer.parseInt(inputRowMatrix.getText()); i++) {
          for (int j =
              0; j < Integer.parseInt(inputColumnMatrix.getText()); j++) {

            TextField temp = new TextField();
            temp.textProperty().addListener(e -> {
              state = true;
            });
            textFields.add(temp);
            gridMatrix.add(temp, j, i);
          }
        }

        // Set focusedTextField
        textFields.stream().forEach(t -> t.setOnMouseClicked(e -> {
          if (t.isFocused()) {
            caretPosition = t.getCaretPosition();
            focusedTextField = t;
          }
        }));
      } catch (Exception e) {

        // Alert when detecting IllegalArgument
        alert("Error", "Number you entered is invalid" + lineSeparator
            + "Please reenter an positive integer");
        inputRowMatrix.setText("2");
      }
    });

    // Add event handler to the TextField
    inputColumnMatrix.setOnKeyReleased(event -> {
      try {

        // change the state
        state = true;
        correctness = false;
        // Avoid to throw Exception when the TextField is empty
        if (inputColumnMatrix.getText().equals("")) {
          return;
        }

        // For IllegalArgumentException
        if (Integer.parseInt(inputColumnMatrix.getText()) <= 0
            || Integer.parseInt(inputColumnMatrix.getText()) > 9) {
          throw new IllegalArgumentException();
        }

        gridMatrix.getChildren().clear();
        textFields.clear();

        // Constructing Input fields
        for (int i = 0; i < Integer.parseInt(inputRowMatrix.getText()); i++) {
          for (int j =
              0; j < Integer.parseInt(inputColumnMatrix.getText()); j++) {
            TextField temp = new TextField();
            temp.textProperty().addListener(e -> {
              state = true;
            });
            textFields.add(temp);
            gridMatrix.add(temp, j, i);
          }
        }
      } catch (Exception e) {

        // Alert when detecting IllegalArgument
        alert("Error", "Number you entered is invalid" + lineSeparator
            + "Please reenter an positive integer");
        inputColumnMatrix.setText("2");
      }
    });

    // Add to the overall Panel
    vBoxMatrix.getChildren().addAll(dimension, gridMatrix);

    return vBoxMatrix;
  }

  /**
   * Show alert to remind user
   * 
   * @param title
   * @param content
   */
  private void alert(String title, String content) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Main method for this class
   * 
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
