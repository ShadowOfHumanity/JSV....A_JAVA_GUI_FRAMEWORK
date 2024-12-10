package com.example.test1;

public class MyJVXTemplate { // THIS IS A TEMPLATE ON HOW TO USE THIS.
    public static void main(String[] args) {
        WebFramework webFramework = new WebFramework("My Web App", 1024, 768);

        webFramework.setHtmlContent("""
        <div id='app'>
            <h1>Welcome to the WebFramework!</h1>
            <button id="animatedButton" onclick="buttonClicked()">Click Me!</button>
        </div>
        """);

        webFramework.setCssContent("""
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        #app {
            text-align: center;
        }
        #animatedButton {
            background: linear-gradient(135deg, #ff7eb3, #ff758c, #ff6f66);
            border: none;
            color: white;
            font-size: 18px;
            font-weight: bold;
            padding: 15px 40px;
            border-radius: 25px;
            box-shadow: 0px 10px 15px rgba(0, 0, 0, 0.1);
            cursor: pointer;
            transition: transform 0.2s ease, background 0.5s ease;
        }
        #animatedButton:hover {
            transform: scale(1.1);
            background: linear-gradient(135deg, #ff758c, #ff6f66, #ff7eb3);
        }
        #animatedButton:active {
            transform: scale(1);
            box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.2);
        }
      """);
        webFramework.setJsContent("""
            function buttonClicked() {
                console.log('Button was clicked!');
                if (typeof java !== 'undefined' && typeof java.invokeMethod === 'function') {
                    java.invokeMethod('testMethod');
                }
            }
        """);

        webFramework.addBridgedMethod("testMethod", (_) -> {
            System.out.println("Java method called from JS!");
        });


        webFramework.launch();
    }
}