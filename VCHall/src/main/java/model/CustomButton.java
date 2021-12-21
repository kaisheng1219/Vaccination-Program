package model;

import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class CustomButton extends Button {
    public CustomButton(String info) {
        setText(info);
        setTextAlignment(TextAlignment.CENTER);
        setMinHeight(88);
        setMinWidth(100);
    }
}