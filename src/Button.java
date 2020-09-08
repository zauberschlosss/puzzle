import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Button extends JToggleButton {

    private boolean isLastButton;
    static JToggleButton buttonPressed;
    static JToggleButton buttonReleased;

    public Button() {
        initUI();
    }

    public Button(Icon icon) {
        super(icon);
        initUI();
    }
/*
    public Button(Image image) {
        super(new ImageIcon(image));
        initUI();
    }
*/

    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.blue));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                buttonPressed = (JToggleButton) e.getSource();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                buttonReleased = (JToggleButton) e.getSource();
            }
        });
    }

    public void setLastButton() {
        isLastButton = true;
    }

    public boolean isLastButton() {
        return isLastButton;
    }
}