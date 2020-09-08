import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

class Button extends JButton {

    private boolean isLastButton;
    static Button buttonPressed;
    static Button buttonReleased;

    public Button() {
        initUI();
    }

/*    public Button(Icon icon) {
        super(icon);
        initUI();
    }*/

    public Button(Image image) {
        super(new ImageIcon(image));
        initUI();
    }

    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.blue));
                buttonReleased = (Button) e.getComponent();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));

            }

            @Override
            public void mousePressed(MouseEvent e) {
                buttonPressed = (Button) e.getComponent();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (Button.buttonPressed != null && Button.buttonReleased != null) {
                    int pressedButtonIndex = Puzzle.buttons.indexOf(Button.buttonPressed);
                    int releasedButtonIndex = Puzzle.buttons.indexOf(Button.buttonReleased);
                    Collections.swap(Puzzle.buttons, pressedButtonIndex, releasedButtonIndex);

                    Button.buttonPressed.setBorder(BorderFactory.createLineBorder(Color.blue));
                    Button.buttonReleased.setBorder(BorderFactory.createLineBorder(Color.gray));
//                    setBorder(BorderFactory.createLineBorder(Color.blue));


                    Puzzle.updateButtons();
                    Puzzle.checkSolution();
                }
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