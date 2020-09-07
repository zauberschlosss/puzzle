import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Button extends JButton {

    private boolean isLastButton;

    public Button() {
        initUI();
    }

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
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    public void setLastButton() {
        isLastButton = true;
    }

    public boolean isLastButton() {
        return isLastButton;
    }

    /*@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.rotate(Math.PI / 2, cx, cy);
        super.paintComponent(g2);
    }*/
}