package com.zauberschlosss.main;

import com.zauberschlosss.listeners.KeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Puzzle extends JFrame {
    static JPanel panel = new JPanel();
    private BufferedImage source;

    private List<Button> buttons = new ArrayList<>();
    private List<Point> solutionPoints = new ArrayList<>();
    private List<Integer> solutionAngles = new ArrayList<>();

    private Image image;
    private int width, height;
    private final int DESIRED_WIDTH = 700;
    private BufferedImage resized;
    private String dataSource;
    private String uri;

    private boolean initRotated = true;
    private int rows = 2;
    private int columns = 2;

    public Puzzle() throws URISyntaxException {
        initResources();
    }

    private void initUI() throws URISyntaxException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                solutionPoints.add(new Point(i, j));
                solutionAngles.add(0);
            }
        }

        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(rows, columns, 0, 0));

        try {
            source = loadImage(dataSource, uri);
            System.out.println(dataSource);
            System.out.println(uri);
            int desiredHeight = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, desiredHeight, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = resized.getWidth();
        height = resized.getHeight();

        KeyListener keyListener = new KeyListener(this);
        panel.addKeyListener(keyListener);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / rows, i * height / columns,
                                (width / rows), height / columns)));

                Button button;
                int newRandomAngle = 0;
                if (!initRotated) {
                    button = new Button(image, this);
                } else {
                    newRandomAngle = new Random().nextInt(4);
                    button = new Button(image, this);
                    button = rotateIcon(button, Button.angles[newRandomAngle]);
                }

                button.putClientProperty("position", new Point(i, j));

                buttons.add(button);
            }
        }

        Collections.shuffle(buttons);

        for (int i = 0; i < rows * columns; i++) {
            Button button = buttons.get(i);
            panel.add(button);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
        }

        pack();
    }

    private void initResources() {
        setTitle("Puzzle");
        setSize(800, 600);
//        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabsPane = new JTabbedPane();
        JPanel puzzleTab = panel;
        JPanel sourceImageTab = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tabsPane.addTab("Puzzle", puzzleTab);
        tabsPane.addTab("Source Image", sourceImageTab);

        JButton selectFromHardDriveButton = new JButton("Load from HDD");
        sourceImageTab.add(selectFromHardDriveButton);

        JButton selectFromURL = new JButton("Load from URL");
        sourceImageTab.add(selectFromURL);

        sourceImageTab.add(new JLabel("Columns"));
        sourceImageTab.add(new JTextField("3", 3));

        sourceImageTab.add(new JLabel("Rows"));
        sourceImageTab.add(new JTextField("3", 3));

        selectFromHardDriveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setPreferredSize(new Dimension(700, 600));
                fileChooser.setFileFilter(new JpgFileFilter());
                fileChooser.showDialog(sourceImageTab, "Open");
                dataSource = "HDD";
                uri = fileChooser.getSelectedFile().toString();
                tabsPane.setSelectedIndex(0);

                try {
                    initUI();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        selectFromURL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object result = JOptionPane.showInputDialog(panel, "Enter URL");
                dataSource = "URL";
                uri = (String) result;
                tabsPane.setSelectedIndex(0);

                try {
                    initUI();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(tabsPane);
        tabsPane.setSelectedIndex(1);
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage(String dataSource, String uri) {
        BufferedImage bufferedImage = null;

        try {
            if (dataSource.equals("HDD")) {
                bufferedImage = ImageIO.read(new File(uri));
            } else if (dataSource.equals("URL")) {
                bufferedImage = ImageIO.read(new URL(uri));
            }
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

    public void checkSolution() {
        List<Point> currentPoints = new ArrayList<>();
        List<Integer> currentAngles = new ArrayList<>();

        buttons.forEach((e) -> currentPoints.add((Point) e.getClientProperty("position")));
        buttons.forEach((e) -> currentAngles.add(e.getAngle()));

        if (compareList(solutionPoints, currentPoints) && compareList(solutionAngles, currentAngles)) {
            JOptionPane.showMessageDialog(panel, "Puzzle assembled!","Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            solutionPoints = null;
            panel.setBorder(BorderFactory.createLineBorder(Color.blue));
            Button.buttonPressed.setBorder(BorderFactory.createLineBorder(Color.gray));
        }
    }

    public static boolean compareList(List<?> list1, List<?> list2) {
        return list1.toString().contentEquals(list2.toString());
    }

    public static void main(String[] args) {
        Puzzle puzzle = null;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            puzzle = new Puzzle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        puzzle.setVisible(true);
    }

    public static Icon rotateImage(Image img, double angle)
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

    public void updateButtons() {
        panel.removeAll();

        for (JComponent btn : buttons) {
            panel.add(btn);
        }

        panel.validate();
    }

    public Button rotateIcon(Button button, int angle) {
        Icon newIcon = button.getIcon();
        Image newImage = iconToBufferedImage(newIcon);
        newIcon = rotateImage(newImage, angle);
        button.setIcon(newIcon);
        button.setAngle(button.getAngle() + angle);

        if (button.getAngle() == 360 || button.getAngle() == -360) {
            button.setAngle(0);
        } else if (button.getAngle() > 360) {
            button.setAngle(button.getAngle() - 360);
        } else if (button.getAngle() < 0) {
            button.setAngle(button.getAngle() + 360);
        }

        pack();

        return button;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public JPanel getPanel() {
        return panel;
    }
}