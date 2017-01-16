package javafxapp;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import jdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
 
 
public class JavaFXApplication extends Application {
    private Scene scene;
    @Override public void start(Stage stage) {
        // create the scene
        stage.setTitle("X5 Tender client");
        Browser browser = new Browser();
        scene = new Scene(browser, browser.computePrefWidth(0), 
                browser.computePrefHeight(0), Color.web("#666970"));
        stage.setScene(scene);
        scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
        stage.show();
    }
 
    public static void main(String[] args){
        launch(args);
    }
}
class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
    boolean isLoginned = false;
     
    public Browser() {
        
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
        webEngine.getLoadWorker().stateProperty()
                .addListener(
                (obs, oldValue, newValue) -> {
                    System.out.println(newValue);
                    if (newValue == Worker.State.SUCCEEDED) {
                        //System.out.println("finished loading");
                        
                        Document doc = webEngine.getDocument();
                        
                        if (doc.getDocumentURI().equals("https://tender.x5.ru/") 
                                && !isLoginned)
                        {
                            HTMLFormElement form = (HTMLFormElement) doc.getElementsByTagName("form")
                                    .item(0);
                            HTMLInputElement username = null;
                            HTMLInputElement password = null;
                            NodeList nodes = form.getElementsByTagName("input");
                            for (int i = 0; i < nodes.getLength(); i++) {
                                HTMLInputElement input = (HTMLInputElement) nodes.item(i);
                                if (input == null || input.getName() == null)
                                    continue;
                                switch (input.getName()) {
                                    case "Username":
                                        username = input;
                                        break;
                                    case "Password":
                                        password = input;
                                        break;
                                }
                            }
                            
                            if (username != null && password != null) {
                                username.setValue("kaskad-ltd");
                                password.setValue("master2019");
                                isLoginned = true;
                                form.submit();
                            }
                            
                            JSObject jsobj = (JSObject) webEngine
                                    .executeScript("document");
                        }
                    }
                }
                );
        
        webEngine.setCreatePopupHandler(
            new Callback<PopupFeatures, WebEngine>() {
                @Override public WebEngine call(PopupFeatures config) {
                    return null;
                }
             }
        );
        
    webEngine.setConfirmHandler(new Callback<String, Boolean>() {
      @Override public Boolean call(String msg) {
        return true;
      }
    });
    
   
        
    webEngine.locationProperty().addListener(new ChangeListener<String>() {
        @Override public void 
        changed(ObservableValue<? extends String> observableValue, String oldLoc, String newLoc) {
        // check if the newLoc corresponds to a file you want to be downloadable
        // and if so trigger some code and dialogs to handle the download.
      }
    });
        
        
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load("https://tender.x5.ru/");
        //add the web view to the scene
        getChildren().add(browser);
 
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 1024;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 800;
    }
}