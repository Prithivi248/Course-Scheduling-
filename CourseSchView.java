import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

class Table extends StackPane {
    public Table(String name, double x, double y, double width, double height) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.AQUA);

        Label label = new Label(name);

        setTranslateX(x);
        setTranslateY(y);

        getChildren().addAll(rectangle, label);
    }
}

public class CourseSchView extends Application {

    static CourseSchController controller;
    static Stage primaryStage;
    static TabPane tabPane;
    static Tab inputTab;
    static Tab scheduleTab;
    static Tab errorTab;

    private double sceneWidth = 800;
    private double sceneHeight = 600;
    private int n =9;
    private int m =4;
    double gridWidth = sceneWidth / n;
    double gridHeight = sceneHeight / m;

    static int lg=0;

    TextField course_text[] = new TextField[CourseSchModel.MAX_COURSES];
    TextField enrol_text[] = new TextField[CourseSchModel.MAX_COURSES];
    TextField pref_text[] = new TextField[CourseSchModel.MAX_COURSES];

    public void startView(){
        Application.launch();
    }

    private void addFieldsToInputForm(VBox inputForm) {
        course_text[lg] = new TextField();
        course_text[lg].setPromptText("Course Name");
        enrol_text[lg] = new TextField();
        enrol_text[lg].setPromptText("Enrollment");
        pref_text[lg] = new TextField();
        pref_text[lg].setPromptText("Preferences");
        Separator separator = new Separator();
        inputForm.getChildren().addAll(course_text[lg], enrol_text[lg], pref_text[lg],separator);
        inputForm.setSpacing(10); 
        lg++;
    }

    public void start(Stage stage) {
        primaryStage = new Stage();
        tabPane = new TabPane();
        inputTab = new Tab("Input");
        scheduleTab = new Tab("Schedule");

        primaryStage.setTitle("Course Scheduling System");

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        VBox inputForm = new VBox();
        addFieldsToInputForm(inputForm);

        // Wrap the inputForm VBox in a ScrollPane
        ScrollPane inputFormScrollPane = new ScrollPane();
        inputFormScrollPane.setContent(inputForm);
        inputTab.setContent(inputFormScrollPane);

        tabPane.getTabs().addAll(inputTab, scheduleTab);
        borderPane.setCenter(tabPane);

        Button scheduleButton = new Button("Schedule Courses");
        scheduleButton.setOnAction(event -> {
            controller.schedule(event);
        });

        Button addButton = new Button("Add Course");
        addButton.setOnAction(event -> {
            addFieldsToInputForm(inputForm);
        });

        Button Submit = new Button("Submit ");
        Submit.setOnAction(event -> {
            controller.view = this;
            controller.submit(event);
        });

        BackgroundFill background_fill = new BackgroundFill(Color.LAVENDERBLUSH, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setBackground(background);

        inputFormScrollPane.setBackground(background); // Set the background for the scroll pane
        buttonContainer.setPadding(new Insets(10));
        buttonContainer.getChildren().addAll(Submit, addButton, scheduleButton);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        borderPane.setBottom(buttonContainer);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static HBox ErrorList(ErrorHan error,int y){
        Label l1 = new Label(error.c.course_no);
        Label l2 = new Label(""+error.c.enrol);
        Label l3 = new Label(error.c.lst.toString());
        Label l4 = new Label(error.err);

        HBox hbox = new HBox(30);
        hbox.setLayoutY(y*20);
        hbox.getChildren().addAll(l1,l2,l3,l4);
        return hbox;
    }

    void output(CourseSchModel model){
        Group root1 = new Group();
        Group root2 = new Group();
        String t1[][] = new String[model.TotRooms + 1][model.TotTimes + 1];
        t1[0][0] = " ";
        System.out.print("\t\t");

        for (int i = 0; i < model.TotTimes; i++) {
            System.out.print(model.TimesslotDB[i] + "\t");
            t1[0][i + 1] = model.TimesslotDB[i];
        }
        System.out.println();

        for (int i = 0; i < model.TotRooms; i++) {
            System.out.print(model.ClassroomDB[i].room_no + "\t");
            t1[i + 1][0] = model.ClassroomDB[i].room_no;
            for (int j = 0; j < model.TotTimes; j++) {
                if (model.TimeTable[i][j] == -1) {
                    System.out.print("\t\t");
                    t1[i+1][j+1] = " ";
                } 
                else {
                    System.out.print(model.CourseDB[model.TimeTable[i][j]]);
                    t1[i+1][j+1] = model.CourseDB[model.TimeTable[i][j]];
                }

            }
            System.out.println();
        }

        for (int i = 0; i < model.TotRooms + 1; i++) {
            for (int j = 0; j < model.TotTimes + 1; j++) {
                Table node = new Table(t1[i][j], j * gridWidth, i * gridHeight, gridWidth, gridHeight);
                root1.getChildren().add(node);

            }
            System.out.println();
        }
        scheduleTab.setContent(root1);

        HBox hb = new HBox(30);
        hb.getChildren().addAll(new Label("Course No"),new Label("Enrollment"),new Label("Preferences"),new Label("Error/Conflict"));
        root2.getChildren().add(hb);

        int y=2;
        for(ErrorHan err:model.Error){
            HBox hbox = ErrorList(err,y);
            y++;
            root2.getChildren().add(hbox);
            System.out.println(err.err);
        }
        
        //scheduleTab = new Tab("Schedule",root1);
        errorTab = new Tab("Errors",root2);
        tabPane.getTabs().add(errorTab);
        //scheduleTab.setContent(root1);
        errorTab.setContent(root2);
    }
}


