import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

class Button extends JToggleButton {

    private boolean isLastButton;
    static JToggleButton buttonPressed;
    static JToggleButton buttonReleased;

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
                buttonReleased = (JToggleButton) e.getComponent();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));

            }

            @Override
            public void mousePressed(MouseEvent e) {
                buttonPressed = (JToggleButton) e.getSource();
                System.out.println(Puzzle.buttons.indexOf(Button.buttonPressed));
                System.out.println("Inside mousePressed method");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                buttonReleased = (JToggleButton) e.getComponent();
                System.out.println(Puzzle.buttons.indexOf(Button.buttonPressed));
                System.out.println(Puzzle.buttons.indexOf(Button.buttonReleased));
                System.out.println("Inside mouseRelease method");
                if (Button.buttonPressed != null && Button.buttonReleased != null) {
                    int pressedButtonIndex = Puzzle.buttons.indexOf(Button.buttonPressed);
                    int releasedButtonIndex = Puzzle.buttons.indexOf(Button.buttonReleased);
                    Collections.swap(Puzzle.buttons, pressedButtonIndex, releasedButtonIndex);

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