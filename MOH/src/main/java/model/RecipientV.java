package model;
import javafx.scene.control.CheckBox;
import org.msjth.model.Recipient;

public class RecipientV {
    private Recipient recipient;
    private CheckBox selected;

    public RecipientV(Recipient recipient) {
        this.recipient = recipient;
        this.selected = new CheckBox();
    }

    public Recipient getRecipient() {
        return this.recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public CheckBox getSelected() {
        return selected;
    }

    public void setSelected(CheckBox selected) {
        this.selected = selected;
    }
}