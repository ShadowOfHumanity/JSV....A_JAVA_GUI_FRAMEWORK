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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class WebFramework extends Application { // WELCOME TO THE WEBFRAMEWORK!
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
    //   1. FIX JAVASCRIPT --> JAVA FUNCTIONS, AS THEY DONT WORK.
    //   2. FIX THE CODE TO MAKE IT MORE USER FRIENDLY AND OPTIMIZED
    //
    //
    // **//



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

    public WebFramework() {
        this("WebApp", 800, 600);
    }

    public WebFramework(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.bridgedMethods = new HashMap<>();
    }

    public WebFramework setHtmlContent(String htmlContent) {
        LOGGER.info("Setting HTML Content: " + htmlContent);
        this.htmlContent = htmlContent;
        return this;
    }

    public WebFramework setHtmlContentFromFile(String filePath) {
        try {
            this.htmlContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("HTML Content from file: " + this.htmlContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading HTML file", e);
        }
        return this;
    }

    public WebFramework setCssContent(String cssContent) {
        LOGGER.info("Setting CSS Content: " + cssContent);
        this.cssContent = cssContent;
        return this;
    }

    public WebFramework setCssContentFromFile(String filePath) {
        try {
            this.cssContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("CSS Content from file: " + this.cssContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSS file", e);
        }
        return this;
    }

    public WebFramework setJsContent(String jsContent) {
        LOGGER.info("Setting JS Content: " + jsContent);
        this.jsContent = jsContent;
        return this;
    }

    public WebFramework setJsContentFromFile(String filePath) {
        try {
            this.jsContent = new String(Files.readAllBytes(Paths.get(filePath)));
            LOGGER.info("JS Content from file: " + this.jsContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading JS file", e);
        }
        return this;
    }

    public WebFramework addBridgedMethod(String methodName, JavaMethodHandler handler) {
        bridgedMethods.put(methodName, handler);
        return this;
    }

    public interface JavaMethodHandler {
        void execute(Object... args);
    }

    private void prepareWebView() {
        webView = new WebView();
        webEngine = webView.getEngine();

        String combinedContent = String.format(
                "<html><head><style>%s</style></head><body>%s<script>%s</script></body></html>",
                cssContent != null ? cssContent : "",
                htmlContent != null ? htmlContent : "",
                jsContent != null ? jsContent : ""
        );

        LOGGER.info("Combined Content: " + combinedContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", new JavaBridge());
            }
        });

        webEngine.loadContent(combinedContent);
    }

    public class JavaBridge {
        public void invokeMethod(String methodName, Object... args) {
            JavaMethodHandler handler = bridgedMethods.get(methodName);
            if (handler != null) {
                Platform.runLater(() -> handler.execute(args));
            } else {
                LOGGER.warning("No handler found for method: " + methodName);
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