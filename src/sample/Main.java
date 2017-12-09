package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class Main extends Application {

    private String _getDeckAsString(String url) {
        if (url == null || url.isEmpty()) return "";
        URL deck;
        StringBuilder sb = new StringBuilder();
        try {
            String urlToUse = !url.toLowerCase().contains("?fmt=csv") ? url + "?fmt=csv" : url;
            deck = new URL(urlToUse);
            HttpURLConnection httpCon = (HttpURLConnection)deck.openConnection();
            httpCon.addRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            String line;
            while((line = in.readLine()) != null) {
                sb.append(line + "\n");
                System.out.println(line);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Bad form");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Bad IO");
        }
        System.out.println(url);
        return sb.toString();
    }

    private String _parseDeck(String deckInCsv) {
        String[] onePerLine = deckInCsv.split("\n");
        return Arrays.stream(onePerLine).skip(1).map(l -> {
            System.out.println(l);
            if (l.contains(",")) {
                String[] fields = l.split(",");
                return String.format("%s %s", fields[1], fields[2]);
            } else {
                return "";
            }
        }).filter(s -> s.length() > 0).distinct().reduce("", (acc, curr) -> acc + curr + "\n");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("TappedOut Fetcher");
        primaryStage.setScene(new Scene(root, 300, 275));
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Label deckUrlLabel = new Label("Enter Deck URL:");
        grid.add(deckUrlLabel, 0, 1);

        TextField deckUrl = new TextField();
        grid.add(deckUrl, 1, 1);

        Label success = new Label("Deck Parsed Successfully");
        success.setTextFill(Color.GREEN);

        Button btn = new Button("Submit");
        btn.setOnAction(e -> {
            grid.getChildren().remove(success);
            String deckAsString = this._getDeckAsString(deckUrl.getText());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString((this._parseDeck(deckAsString)));
            clipboard.setContent(content);
            grid.add(success, 1, 5);
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);


        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
