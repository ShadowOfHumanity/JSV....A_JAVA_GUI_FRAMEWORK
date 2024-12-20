package com.example.test1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



// WELCOME TO THE WEBFRAMEWORK!
// this framework combines JAVAFX, Java Libraries and JAVASCRIPT to create a powerful local web application.
// For future developers that dont understand what the below does,
//** DO NOT TOUCH THE BELOW CODE
// Stage is the main window of the application, while StackPane, a layout implements a stack; children in a single stack.
// Scene is container equivelent to JPANEL, hold all of your content.
//  Webview & WebEngine are the main components of the JavaFX web component. WebEngine controls WebView.
// -------------------------- CONSTRUCTOR AND IMPORTANT NOTES --------------------------------
//  The Constructor initializes the title, width, height and bridgedMethods.
//  bridgeMethods is a map that stores the methods that are coded in Javascript and then called in Java.
// ------------------------------------ METHODS ----------------------------------------------
//  SetHtmlContent, SetCssContent, SetJsContent, SetHtmlContentFromFile, SetCssContentFromFile,
//  SetJsContentFromFile, addBridgedMethod are methods that set the content of the webview.
// are all self explanitory! --> those which are from file read the content from the path of the file given.
// JavaMethodHandler is an interface that is used to handle the methods that are called from Javascript.
// the start and launch methods are used to start the application. simple as that!
//
// ------------------------------------ EXTRAS ----------------------------------------------
// Why is this important?

// Well, Most people dont know how to write in FXML or XML, so this is a simple way to create a APP using
//  HTML with the perks of CSS & Javascript commands for those who dont know how to use Java!
//
//  How to use this?

//  1. Create a new instance of WebFramework
//  2. Set the HTML, CSS, JS content using the methods provided.
//  3. Add bridged methods that you want to call from Javascript.
//  4. Call the launch method to start the application.
//  5. Run the application and see the magic!
//
// Why did I make this?

// Well, I noticed that i dont know how to use JavaFX, and JSwing is terrible to use to make custom UIs.
// So, I decided to make a simple way to create a UI using HTML, CSS and Javascript.
// As a result, I created this framework to help those who dont know how to use JavaFX to create a UI (Including myself).
//
//  What are the benefits of this?

// 1. You can use HTML, CSS and Javascript to create a UI.
// 2. You can use Java to handle the backend of the application.
// 3. You can use Java to handle the methods that are called from Javascript.
//
//  TODO:
//   1. ADD MORE FEATURES TO THE FRAMEWORK
//   2. FIX THE CODE TO MAKE IT MORE USER FRIENDLY AND OPTIMIZED
//
//
// **//




public class WebFramework extends Application {
    private static final Logger LOGGER = Logger.getLogger(WebFramework.class.getName());

    private Stage primaryStage;
    private StackPane root;
    private Scene scene;
    private WebView webView;
    private WebEngine webEngine;

    private String title;
    private int width;
    private int height;
    private String htmlContent;
    private String cssContent;
    private String jsContent;

    private Map<String, JavaMethodHandler> bridgedMethods;
    //// CONSTRUCTORS
    public WebFramework() {
        this("WebApp", 800, 600);

        // Add a default console log method
        addBridgedMethod("consoleLog", (args) -> {
            if (args.length > 0) {
                System.out.println("JS Console: " + args[0]);
            }
        });
    }
    //// CONSTRUCTORS
    public WebFramework(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.bridgedMethods = new HashMap<>();
    }
    // Methods
    // self explanitory
    public WebFramework setHtmlContent(String htmlContent) {
        LOGGER.info("Setting HTML Content: " + htmlContent);
        this.htmlContent = htmlContent;
        return this;
    }
    // self explanitory
    public WebFramework setHtmlContentFromFile(String filePath) {
        try {
            this.htmlContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("HTML Content from file: " + this.htmlContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading HTML file", e);
        }
        return this;
    }
    // self explanitory
    public WebFramework setCssContent(String cssContent) {
        LOGGER.info("Setting CSS Content: " + cssContent);
        this.cssContent = cssContent;
        return this;
    }
    //self explanitory
    public WebFramework setCssContentFromFile(String filePath) {
        try {
            this.cssContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("CSS Content from file: " + this.cssContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSS file", e);
        }
        return this;
    }
    //self explanitory
    public WebFramework setJsContent(String jsContent) {
        LOGGER.info("Setting JS Content: " + jsContent);
        this.jsContent = jsContent;
        return this;
    }
    //self explanitory
    public WebFramework setJsContentFromFile(String filePath) {
        try {
            this.jsContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("JS Content from file: " + this.jsContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading JS file", e);
        }
        return this;
    }
    // Add bridged methods that you want to call from Javascript (JAVASCRIPT CALLS JAVA METHOD)
    public WebFramework addBridgedMethod(String methodName, JavaMethodHandler handler) {
        bridgedMethods.put(methodName, handler);
        return this;
    }
    // Interface to handle the methods that are called from Javascript
    public interface JavaMethodHandler {
        void execute(Object... args);
    }
    // self explanitory
    private void prepareWebView() {
        webView = new WebView();
        webEngine = webView.getEngine();

        String combinedContent = String.format( //combines the content of the HTML, CSS and JS files
                "<!DOCTYPE html><html><head><style>%s</style></head><body>%s<script>%s</script></body></html>",
                cssContent != null ? cssContent : "",
                htmlContent != null ? htmlContent : "",
                jsContent != null ? jsContent : ""
        );

        LOGGER.info("Combined Content: " + combinedContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) { // if loaded
                Platform.runLater(() -> { // run the bridged code when needed :
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("java", new JavaBridge());

                    // Add console.log redirection to help with debugging
                    webEngine.executeScript( //redirects console.log to java
                            "console.log = function(message) { " +
                                    "    java.invokeMethod('consoleLog', message);" +
                                    "};"
                    );
                });
            }
        });

        webEngine.loadContent(combinedContent);
    }

    public class JavaBridge {
        public void invokeMethod(String methodName, Object arg) { //invokes the method from JS to Java
            System.out.println("Method called: " + methodName);
            System.out.println("Argument: " + arg);

            JavaMethodHandler handler = bridgedMethods.get(methodName);

            if (handler != null) {
                System.out.println("Handler found for method: " + methodName);
                try {
                    Platform.runLater(() -> {
                        try {
                            handler.execute(new Object[]{arg}); // FIXED --> PARAMATERS NOW PASS!
                            System.out.println("Method executed successfully");
                        } catch (Exception e) {
                            System.err.println("Error executing method: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error in Platform.runLater: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("NO HANDLER FOUND FOR METHOD: " + methodName);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new StackPane();

        prepareWebView();
        root.getChildren().add(webView);

        scene = new Scene(root, width, height);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void launch() {
        Platform.startup(() -> {
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}