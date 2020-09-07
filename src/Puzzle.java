import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Puzzle extends JFrame {
    private JPanel panel;
    private BufferedImage source;
    private ArrayList<Button> buttons = new ArrayList<>();;

    ArrayList<Point> solution = new ArrayList<>();

    private Image image;
    private Button lastButton;
    private int width, height;
    private final int DESIRED_WIDTH = 1000;
    private BufferedImage resized;

    public Puzzle() throws URISyntaxException {
        initUI();
    }

    private void initUI() throws URISyntaxException {
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));

        try {
            source = loadImage();
            int desiredHeight = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, desiredHeight, BufferedImage.TYPE_INT_ARGB);

        } catch (IOException e) {
            e.printStackTrace();
        }

        width = resized.getWidth();
        height = resized.getHeight();

        add(panel);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {

                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 4,
                                (width / 3), height / 4)));
                Button button = new Button(image);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new Button();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }

        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < 12; i++) {
            Button button = buttons.get(i);
            panel.add(button);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
            button.addActionListener(new ClickAction());
        }

        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage() {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("C:\\Users\\Wint3rzaub3rschloss\\Pictures\\Wallpapers\\cropped-1920-1080-736806.jpg"));

        } catch (IOException e) {
            e.getStackTrace();
        }

        return bufferedImage;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width,
                                      int height, int type) throws IOException {

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();

        return resizedImage;
    }

    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {
            int lidx = 0;
            for (Button button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if (lidx != bidx) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();

            for (JComponent btn : buttons) {
                panel.add(btn);
            }

            panel.validate();
        }
    }

    private void checkSolution() {
        ArrayList<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Puzzle assembled!","Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List<Point> ls1, List<Point> ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {
        Puzzle puzzle = null;
        try {
            puzzle = new Puzzle();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        puzzle.setVisible(true);
    }
}