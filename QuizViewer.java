

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class QuizViewer extends Application{
    private static int numberOfQuestions;
    private static String[] answersOfUser;
    private static int current = 0;
    private static Scene[] questionsWithInterface;
    private static ArrayList<Question> questions;
    private static int count =0;

    private static Stage mainStage = new Stage();


    public void start(Stage primaryStage){
        StackPane stackPane = new StackPane();
        Button btLoad = new Button("Load File");
        stackPane.getChildren().add(btLoad);

        btLoad.setOnAction(e -> {
            try {
                FileChooser fileChooser= new FileChooser();
                Stage stageForChooser = new Stage();
                Pane pane = new Pane();
                stageForChooser.setScene(new Scene(pane, 500,500));
                File selectedFile = fileChooser.showOpenDialog(stageForChooser);
                Quiz quiz = Quiz.loadFromFile(selectedFile.getName());
                primaryStage.close();
                go(quiz);

            }catch (InvalidQuizFormatException ex){
                Stage stageForException = new Stage();

                BorderPane paneForException = new BorderPane();
                paneForException.setPadding(new Insets(10));

                HBox hBox = new HBox(40);
                hBox.setAlignment(Pos.CENTER);
                hBox.getChildren().add(new Label("InvalidQuizFormatException"));
                paneForException.setTop(hBox);

                Label label = new Label("The file selected does not fit the requirements for a standard Quiz text file format");
                label.setWrapText(true);
                paneForException.setCenter(new StackPane(label));

                Button btOk = new Button("OK");

                btOk.setOnAction(f ->{
                    stageForException.close();
                });

                paneForException.setBottom(btOk);
                paneForException.setAlignment(btOk, Pos.BOTTOM_CENTER);

                stageForException.setScene(new Scene(paneForException, 300,200));
                stageForException.show();
            }
        });

        Scene scene = new Scene(stackPane, 400,400);
        primaryStage.setTitle("QuizViewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void go(Quiz quiz){
        questions = quiz.getQuestions();
        Collections.shuffle(questions);
        numberOfQuestions = questions.size();
        answersOfUser = new String[numberOfQuestions];
        questionsWithInterface = new Scene[numberOfQuestions];

        for (int i = 0; i < numberOfQuestions; i++) {
            Question currentQuestion = questions.get(i);
            if (currentQuestion instanceof Test) {
                questionsWithInterface[i] = testQuestion((Test) currentQuestion, i);
            } else if (currentQuestion instanceof FillIn) {
                questionsWithInterface[i] = fillInQues((FillIn) currentQuestion, i);
            }
        }

        mainStage.setScene(questionsWithInterface[current]);
        mainStage.setTitle("Quiz Viewer");
        mainStage.show();
    }


    public static Scene fillInQues(FillIn question, int number){
        VBox mainPane = new VBox();
        TextArea taDescription = new TextArea();
        taDescription.setText(question.toString());
        taDescription.setEditable(false);
        taDescription.setWrapText(true);

        BorderPane paneForFillIn = new BorderPane();

        Button btNext = new Button(">>");
        Button btPrevious = new Button("<<");
        BorderPane paneForButtons = new BorderPane();
        paneForButtons.setLeft(btPrevious);
        paneForButtons.setRight(btNext);
        paneForFillIn.setTop(paneForButtons);


        StackPane paneForAnswer = new StackPane();
        paneForAnswer.setPadding(new Insets(100));
        TextField tfAnswer = new TextField();
        paneForAnswer.getChildren().add(tfAnswer);
        paneForFillIn.setCenter(paneForAnswer);

        HBox paneForStatusAndCheck = new HBox(40);
        paneForStatusAndCheck.setAlignment(Pos.CENTER);
        Label status = new Label("Status: "+ (number+1)+"/"+numberOfQuestions);
        Button btCheckAns = new Button("Check Answers");

        btCheckAns.setOnAction(e -> {
            answersOfUser[current] = tfAnswer.getText();
            checkingAns();
            resultBox();
            System.out.println("Number of correct: "+ count);
            count =0;
        });

        btNext.setOnAction(f -> {
            answersOfUser[current] = tfAnswer.getText();
            if (status.getText().contains("Start")){
                int index = status.getText().indexOf("\n");
                status.setText(status.getText().substring(0, index));
            }
            next();
            if (current > numberOfQuestions - 1){
                if (!status.getText().contains("End")){
                    status.setText(status.getText()+"\nEnd of Quiz");
                }
                previous();
            }else{
                mainStage.setScene(questionsWithInterface[current]);
            }
        });

        btPrevious.setOnAction(g -> {
            answersOfUser[current] = tfAnswer.getText();
            if (status.getText().contains("End")){
                int index = status.getText().indexOf("\n");
                status.setText(status.getText().substring(0, index));
            }
            previous();
            if (current < 0){
                if (!status.getText().contains("Start")){
                    status.setText(status.getText()+"\nStart of quiz!");
                }
                next();
            }else{
                mainStage.setScene(questionsWithInterface[current]);
            }
        });

        paneForStatusAndCheck.getChildren().addAll(status, btCheckAns);
        paneForFillIn.setBottom(paneForStatusAndCheck);

        mainPane.getChildren().addAll(taDescription, paneForFillIn);
        Scene scene = new Scene(mainPane, 450, 400);
        return scene;
    }

    public static void next(){
            current = current +1;
    }

    public static void previous(){
            current = current - 1;
    }

    public static void resultBox(){
        Stage stageForResult = new Stage();

        BorderPane mainPane = new BorderPane();

        HBox hBox = new HBox(10);
        Label label = new Label("Number of correct answers: " + count+"/"+numberOfQuestions);
        label.setFont(Font.font("Times", 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().add(label);

        BorderPane borderPane = new BorderPane();
        Label inf = new Label("You may try again");
        inf.setFont(Font.font("Times",14));
        borderPane.setCenter(inf);
        borderPane.setAlignment(label, Pos.CENTER_LEFT);
        borderPane.setPadding(new Insets(10));

        Button btOk = new Button("OK");
        borderPane.setBottom(btOk);
        borderPane.setAlignment(btOk, Pos.BOTTOM_RIGHT);

        btOk.setOnAction(e ->{
            stageForResult.close();
        });

        mainPane.setTop(hBox);
        mainPane.setCenter(borderPane);

        stageForResult.setScene(new Scene(mainPane, 400,300));
        stageForResult.setTitle("Quiz Viewer: Result");
        stageForResult.show();
    }

    public static void checkingAns(){
        for (int i = 0; i < numberOfQuestions; i++) {
            Question currentQuestion = questions.get(i);
            if (currentQuestion instanceof Test) {
                if (answersOfUser[i] != null){
                    if (!answersOfUser[i].equals(""))
                        if (answersOfUser[i].equals(currentQuestion.getAnswer())){
                            count++;
                        }
                }
            } else if (currentQuestion instanceof FillIn) {
                if (answersOfUser[i] != null) {
                    if (!answersOfUser[i].equals(""))
                        if (answersOfUser[i].toLowerCase().equals(currentQuestion.getAnswer().toLowerCase())) {
                            count++;
                        }
                }
            }
        }
    }

    public static Scene testQuestion(Test question, int number){
        VBox mainPane = new VBox();

        TextArea taDescription = new TextArea();
        taDescription.setText(question.getDescription());
        taDescription.setEditable(false);
        taDescription.setWrapText(true);

        BorderPane paneForTest = new BorderPane();

        Button btNext = new Button(">>");
        Button btPrevious = new Button("<<");

        BorderPane paneForButtons = new BorderPane();
        paneForButtons.setLeft(btPrevious);
        paneForButtons.setRight(btNext);
        paneForTest.setTop(paneForButtons);

        String[] options = new String[4];
        options[0] = question.getOptionAt(0);
        options[1] = question.getOptionAt(1);
        options[2] = question.getOptionAt(2);
        options[3] = question.getAnswer();

        shuffle(options);

        VBox paneForOptions = new VBox(10);
        RadioButton rbFirst = new RadioButton(options[0]);
        RadioButton rbSecond = new RadioButton(options[1]);
        RadioButton rbThird = new RadioButton(options[2]);
        RadioButton rbFourth = new RadioButton(options[3]);
        paneForOptions.getChildren().addAll(rbFirst, rbSecond, rbThird, rbFourth);
        paneForOptions.setPadding(new Insets(30));
        paneForTest.setCenter(paneForOptions);


        ToggleGroup group = new ToggleGroup();
        rbFirst.setToggleGroup(group);
        rbFourth.setToggleGroup(group);
        rbSecond.setToggleGroup(group);
        rbThird.setToggleGroup(group);

        rbFirst.setOnAction(e -> {
            if (rbFirst.isSelected()){
                answersOfUser[current] = rbFirst.getText();
            }
        });

        rbSecond.setOnAction(e -> {
            if (rbSecond.isSelected()){
                answersOfUser[current] = rbSecond.getText();
            }
        });

        rbThird.setOnAction(e -> {
            if (rbThird.isSelected()){
                answersOfUser[current] = rbThird.getText();
            }
        });

        rbFourth.setOnAction(e -> {
            if (rbFourth.isSelected()){
                answersOfUser[current] = rbFourth.getText();
            }
        });


        HBox paneForStatusAndCheck = new HBox(40);
        paneForStatusAndCheck.setAlignment(Pos.CENTER);
        Label status = new Label("Status: "+ (number+1)+"/"+numberOfQuestions);
        Button btCheckAns = new Button("Check Answers");

        btCheckAns.setOnAction(e -> {
            checkingAns();
            resultBox();
            System.out.println("Number of correct: "+ count);
            count=0;
        });

        btNext.setOnAction(f -> {
            if (status.getText().contains("Start")){
                int index = status.getText().indexOf("\n");
                status.setText(status.getText().substring(0, index));
            }
            next();
            if (current > numberOfQuestions - 1){
                if (!status.getText().contains("End"))
                    status.setText(status.getText()+"\nEnd of Quiz");
                previous();
            }else {
                mainStage.setScene(questionsWithInterface[current]);
            }
        });

        btPrevious.setOnAction(g -> {
            if (status.getText().contains("End")){
                int index = status.getText().indexOf("\n");
                status.setText(status.getText().substring(0, index));
            }
            previous();
            if (current < 0){
                if (!status.getText().contains("Start")){
                    status.setText(status.getText()+"\nStart of quiz!");
                }
                next();
            }else {
                mainStage.setScene(questionsWithInterface[current]);
            }
        });

        paneForStatusAndCheck.getChildren().addAll(status, btCheckAns);
        paneForTest.setBottom(paneForStatusAndCheck);

        mainPane.getChildren().addAll(taDescription, paneForTest);
        Scene scene =  new Scene(mainPane, 450, 400);
        return scene;
    }

    public static void shuffle(String[] array){
        for (int i = array.length -1; i >0; i--){
            int j = (int)(Math.random() * (i + 1));

            String temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
