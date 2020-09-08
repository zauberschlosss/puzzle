import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
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
    static JPanel panel;
    private BufferedImage source;
    static ArrayList<Button> buttons = new ArrayList<>();;

    ArrayList<Point> solution = new ArrayList<>();

    private Image image;
    private Button lastButton;
    private int width, height;
    private final int DESIRED_WIDTH = 600;
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

                System.out.println();

                System.out.println(image.getWidth(null));
                System.out.println(image.getHeight(null));

                Icon rotatedIcon = rotate(image, 0);

            /*    System.out.println(rotatedIcon.getIconWidth());
                System.out.println(rotatedIcon.getIconHeight());*/

                Button button = new Button(rotatedIcon);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new Button(rotatedIcon);
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

            /*Action action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Icon newIcon = button.getIcon();
                    Image newImage = iconToBufferedImage(newIcon);
                    newIcon = rotate(newImage, 90);
                    button.setIcon(newIcon);
                }
            };*/

            Icon newIcon = button.getIcon();

            Image newImage = iconToBufferedImage(newIcon);
            newIcon = rotate(newImage, 90);
            System.out.println(newIcon.getIconWidth());
            System.out.println(newIcon.getIconHeight());


//            button.setAction(action);
            updateButtons();

            KeyStroke keyStroke = KeyStroke.getKeyStroke("SPACE");
            InputMap inputMap = button.getInputMap(JComponent.WHEN_FOCUSED);
            inputMap.put(keyStroke, "rotateImage");

            panel.add(button);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
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

    private class MouseAction extends MouseAdapter {
        private void checkButton(ActionEvent e) {
                        System.out.println("|||||-->>");

            System.out.println(buttons.indexOf(Button.buttonPressed));
            System.out.println(buttons.indexOf(Button.buttonReleased));
            if (Button.buttonPressed != null && Button.buttonReleased != null) {
                int pressedButtonIndex = buttons.indexOf(Button.buttonPressed);
                int releasedButtonIndex = buttons.indexOf(Button.buttonReleased);
                Collections.swap(buttons, pressedButtonIndex, releasedButtonIndex);
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

        private void rotateIcon(ActionEvent e) {
            int lidx = 0;
            Button button = (Button) e.getSource();
            Icon newIcon = button.getIcon();
            Image newImage = iconToBufferedImage(newIcon);
            newIcon = rotate(newImage, 90);
            System.out.println("-----**");
            System.out.println(newIcon.getIconWidth());
            System.out.println(newIcon.getIconHeight());
            button.setIcon(newIcon);
//            button.setSize(newIcon.getIconWidth() + 2, newIcon.getIconHeight() + 2);
            System.out.println("Button width: " + button.getWidth());
            System.out.println("Button height: " + button.getHeight());
            pack();
            updateButtons();
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
//        puzzle.setResizable(true);
    }

    public static Icon rotate(Image img, double angle)
    {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))),
                cos = Math.abs(Math.cos(Math.toRadians(angle)));

        int w = img.getWidth(null), h = img.getHeight(null);

        int neww = (int) Math.floor(w*cos + h*sin),
                newh = (int) Math.floor(h*cos + w*sin);

        BufferedImage bimg = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bimg.createGraphics();

        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(Math.toRadians(angle), w/2, h/2);
        g.drawRenderedImage(imageToBufferedImage(img), null);
        g.dispose();

        Icon rotatedIcon = new ImageIcon(bimg);

        return rotatedIcon;
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage
                (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static BufferedImage iconToBufferedImage(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0,0);
        g.dispose();

        return bi;
    }

    public static void updateButtons() {
        panel.removeAll();

        for (JComponent btn : buttons) {
            panel.add(btn);
        }

        panel.validate();
    }
}