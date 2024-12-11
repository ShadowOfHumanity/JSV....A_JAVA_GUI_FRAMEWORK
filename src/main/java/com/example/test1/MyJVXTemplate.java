package com.example.test1;

import com.example.test1.WebFramework;

public class MyJVXTemplate {
    public static void main(String[] args) {
        WebFramework webFramework = new WebFramework("My Web App", 1024, 768);

        webFramework.setHtmlContent("""
                <div id='app'>
                    <h1>Welcome to the JSV!</h1>
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
                    function spawnRandomColorButton() {
                        // Create a new button and style it, and add click property
                        let newButton = document.createElement('button');
                       \s
                        
                        const randomColor = `rgb(${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)}, ${Math.floor(Math.random() * 256)})`;
                       \s
                        // Set button properties
                        newButton.textContent = 'Random Button';
                        newButton.style.backgroundColor = randomColor;
                        newButton.style.color = 'white';
                        newButton.style.padding = '10px 20px';
                        newButton.style.border = 'none';
                        newButton.style.borderRadius = '5px';
                        newButton.style.margin = '10px';
                       \s
                       
                        newButton.addEventListener('click', () => {
                            buttonClicked();
                        });
                       \s
                        document.getElementById('app').appendChild(newButton);
                    }
                    function buttonClicked() {
                
                        \s
                         try {
                             if (typeof java !== 'undefined' && typeof java.invokeMethod === 'function') {
       
                                 java.invokeMethod('testMethod');
                                 java.invokeMethod('print', 'Button clicked!');
                                 spawnRandomColorButton();
                                 
                                 
                             }
                         } catch (error) {
                             console.log('Error invoking Java method: ' + error);
                         }
                     }
                """);

        webFramework.addBridgedMethod("testMethod", (arg) -> {
            System.out.println("Java method called from JS!");
        });
        webFramework.addBridgedMethod("print", (arg) -> {
            System.out.println("args: " + arg);
        });

        webFramework.launch();
    }
}