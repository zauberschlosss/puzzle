package com.zauberschlosss.main;

import com.zauberschlosss.listeners.FileChooserListener;
import com.zauberschlosss.listeners.MKeyListener;
import com.zauberschlosss.listeners.URListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class Puzzle extends JFrame {
    private JPanel panel = new JPanel();
    private JPanel sourceImageTab;
    private JPanel puzzlePicture;
    private JTabbedPane tabsPane;
    private JMenuItem playAgain;
    private JMenuItem newPuzzle;
    private JMenuItem rotateClockwise;
    private JMenuItem rotateAnticlockwise;
    private JMenuItem rotateAround;
    private JMenuItem magicButton;
    private JComboBox<Integer> gridSelection;
    private JCheckBox checkBoxIsRotated;

    private List<Button> buttons = new ArrayList<>();
    private List<Point> solutionPoints = new ArrayList<>();
    private List<Integer> solutionAngles = new ArrayList<>();
    private List<Icon> icons = new ArrayList<>();

    private List<Integer> lowerBorders = new ArrayList<>();
    private List<Integer> upperBorders = new ArrayList<>();
    private List<Integer> leftBorders = new ArrayList<>();
    private List<Integer> rightBorders = new ArrayList<>();
    private List<Double> upDownBorders = new ArrayList<>();
    private List<Double> leftRightBorders = new ArrayList<>();
    private List<BufferedImage> bufferedImages = new ArrayList<>();

    private Image image;
    private int width, height;
    private final int DESIRED_WIDTH = 800;
    private BufferedImage source;
    private BufferedImage resized;
    private String dataSource;
    private String uri;

    private boolean initRotated = false;
    private int rows;
    private int columns;
    private double averageUpDown = 0;
    private double averageLeftRight = 0;
    private Date startTime;
    private double precisionPercent = 1;
    private boolean timeTrigger = true;

    public Puzzle() throws URISyntaxException {
        setupResourcesAndUI();
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
            int desiredHeight = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, desiredHeight, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        width = resized.getWidth();
        height = resized.getHeight();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / rows, i * height / columns, (width / rows), height / columns)));

                BufferedImage piece = imageToBufferedImage(image);

                savePuzzlePieceToDisk(piece);

                Button button;
                int newRandomAngle;
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

    private void setupResourcesAndUI() {
        setTitle("Puzzle");
        setVisible(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.addKeyListener(new MKeyListener(this));

        panel.addKeyListener(new MKeyListener(this));
        tabsPane = new JTabbedPane();
        JPanel puzzleTab = panel;
        sourceImageTab = new JPanel(new FlowLayout(FlowLayout.CENTER));
        puzzlePicture = new JPanel(new FlowLayout(FlowLayout.CENTER));

        tabsPane.addTab("Puzzle", puzzleTab);
        tabsPane.addTab("Picture", puzzlePicture);
        tabsPane.addTab("Source", sourceImageTab);

        tabsPane.setEnabledAt(0, false);
        tabsPane.setEnabledAt(1, false);

        JButton selectFromHardDriveButton = new JButton("Load from HDD");
        sourceImageTab.add(selectFromHardDriveButton);

        JButton selectFromURL = new JButton("Load from URL");
        sourceImageTab.add(selectFromURL);

        JLabel grid = new JLabel("Select Grid");

        gridSelection = new JComboBox<>();
        gridSelection.addItem(2);
        gridSelection.addItem(3);

        gridSelection.setSelectedIndex(1);
        gridSelection.setMaximumRowCount(2);

        sourceImageTab.add(grid);
        sourceImageTab.add(gridSelection);

        JLabel isRotated = new JLabel("Rotate");
        checkBoxIsRotated = new JCheckBox();

        sourceImageTab.add(isRotated);
        sourceImageTab.add(checkBoxIsRotated);

        JMenuBar menuBar = new JMenuBar();

        JMenu game = new JMenu("Game");

        playAgain = new JMenuItem("Play again");
        playAgain.setAccelerator(KeyStroke.getKeyStroke("R"));
        playAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                initResources();
                tabsPane.setSelectedIndex(0);
            }
        });
        playAgain.setEnabled(false);
        game.add(playAgain);

        newPuzzle = new JMenuItem("New puzzle");
        newPuzzle.setAccelerator(KeyStroke.getKeyStroke("N"));
        newPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabsPane.setEnabledAt(0, false);
                tabsPane.setEnabledAt(1, false);
                playAgain.setEnabled(false);
                newPuzzle.setEnabled(false);
                rotateClockwise.setEnabled(false);
                rotateAnticlockwise.setEnabled(false);
                rotateAround.setEnabled(false);
                magicButton.setEnabled(false);
                puzzlePicture.removeAll();

                tabsPane.setSelectedIndex(2);
            }
        });
        newPuzzle.setEnabled(false);
        game.add(newPuzzle);

        game.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        game.add(exit);

        JMenu controls = new JMenu("Controls");

        rotateClockwise = new JMenuItem("Rotate clockwise");
        rotateClockwise.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_D));
        rotateClockwise.addActionListener(e -> rotateIcon(Button.buttonPressed, 90));
        rotateClockwise.setEnabled(false);
        controls.add(rotateClockwise);

        rotateAnticlockwise = new JMenuItem("Rotate anticlockwise");
        rotateAnticlockwise.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_A));
        rotateAnticlockwise.addActionListener(e -> rotateIcon(Button.buttonPressed, -90));
        rotateAnticlockwise.setEnabled(false);
        controls.add(rotateAnticlockwise);

        rotateAround = new JMenuItem("Rotate 180");
        rotateAround.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_W));
        rotateAround.addActionListener(e -> rotateIcon(Button.buttonPressed, 180));
        rotateAround.setEnabled(false);
        controls.add(rotateAround);

        magicButton = new JMenuItem("Magic button");
        magicButton.setAccelerator(KeyStroke.getKeyStroke("M"));
        magicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPiecesAndCalculateGradients();
                magicButton();
            }
        });
        magicButton.setEnabled(false);
        controls.add(magicButton);

        menuBar.add(game);
        menuBar.add(controls);

        setJMenuBar(menuBar);

        selectFromHardDriveButton.addActionListener(new FileChooserListener(this));
        selectFromURL.addActionListener(new URListener(this));

        add(tabsPane);
        tabsPane.setSelectedIndex(2);
    }

    private void reset() {
        buttons = new ArrayList<>();
        solutionPoints = new ArrayList<>();
        solutionAngles = new ArrayList<>();
        lowerBorders = new ArrayList<>();
        upperBorders = new ArrayList<>();
        leftBorders = new ArrayList<>();
        rightBorders = new ArrayList<>();
        upDownBorders = new ArrayList<>();
        leftRightBorders = new ArrayList<>();
        bufferedImages = new ArrayList<>();
        icons = new ArrayList<>();
        precisionPercent = 1;
        timeTrigger = true;
        panel.removeAll();
        puzzlePicture.removeAll();

        File[] pieces = new File("./pieces").listFiles();
        if (Files.exists(Paths.get("./pieces"))) {
            for (File piece : pieces) {
                piece.delete();
            }
        }
    }

    public void initResources() {
        try {
            rows = (int) gridSelection.getSelectedItem();
            columns = (int) gridSelection.getSelectedItem();
            if (checkBoxIsRotated.isSelected()) {
                initRotated = true;
            }

            reset();
            initUI();
            setLocationRelativeTo(null);

            JLabel picLabel = new JLabel(new ImageIcon(resized));
            puzzlePicture.add(picLabel);

            tabsPane.setEnabledAt(0, true);
            tabsPane.setEnabledAt(1, true);
            playAgain.setEnabled(true);
            newPuzzle.setEnabled(true);
            rotateClockwise.setEnabled(true);
            rotateAnticlockwise.setEnabled(true);
            rotateAround.setEnabled(true);
            magicButton.setEnabled(true);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void savePuzzlePieceToDisk(BufferedImage piece) {
        Path path = Paths.get("./pieces");

        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }

            ImageIO.write(piece, "png", new File("./pieces/" + piece.hashCode() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPiecesAndCalculateGradients() {
        File[] pieces = new File("./pieces").listFiles();
        int columnsCounter = 0;

        for (int i = 0; i < pieces.length; i++) {
            BufferedImage piece = null;
            try {
                piece = ImageIO.read(pieces[i]);
                bufferedImages.add(piece);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int sumRGBlowerBorder = 0;
            int sumRGBupperBorder = 0;

            for (int x = 0; x < piece.getWidth(); x++) {
                int rgbLower = piece.getRGB(x, piece.getHeight() - 1);
                int redLower = (rgbLower >> 16) & 0xff;
                int greenLower = (rgbLower >> 8) & 0xff;
                int blueLower = (rgbLower) & 0xff;
                sumRGBlowerBorder += redLower + greenLower + blueLower;

                int rgbUpper = piece.getRGB(x, 0);
                int redUpper = (rgbUpper >> 16) & 0xff;
                int greenUpper = (rgbUpper >> 8) & 0xff;
                int blueUpper = (rgbUpper) & 0xff;
                sumRGBupperBorder += redUpper + greenUpper + blueUpper;
            }

            lowerBorders.add(sumRGBlowerBorder);
            upperBorders.add(sumRGBupperBorder);

            int sumRGBleftBorder = 0;
            int sumRGBrightBorder = 0;

            for (int y = 0; y < piece.getHeight(); y++) {
                int rgbLeft = piece.getRGB(0, y);
                int redLeft = (rgbLeft >> 16) & 0xff;
                int greenLeft = (rgbLeft >> 8) & 0xff;
                int blueLeft = (rgbLeft) & 0xff;
                sumRGBleftBorder += redLeft + greenLeft + blueLeft;

                int rgbRight = piece.getRGB(piece.getWidth() - 1, y);
                int redRight = (rgbRight >> 16) & 0xff;
                int greenRight = (rgbRight >> 8) & 0xff;
                int blueRight = (rgbRight) & 0xff;
                sumRGBrightBorder += redRight + greenRight + blueRight;
            }

            leftBorders.add(sumRGBleftBorder);
            rightBorders.add(sumRGBrightBorder);
        }

        calculateBordersMatchPercentage(columnsCounter);

        while (averageUpDown > precisionPercent || averageLeftRight > precisionPercent) { // Puzzle assembling precision
            columnsCounter = 0;
            long nanoSeed = System.nanoTime();
            Collections.shuffle(lowerBorders, new Random(nanoSeed));
            Collections.shuffle(upperBorders, new Random(nanoSeed));
            Collections.shuffle(leftBorders, new Random(nanoSeed));
            Collections.shuffle(rightBorders, new Random(nanoSeed));
            Collections.shuffle(bufferedImages, new Random(nanoSeed));

            upDownBorders = new ArrayList<>();
            leftRightBorders = new ArrayList<>();

            calculateBordersMatchPercentage(columnsCounter);
        }

        System.out.println(Collections.max(upDownBorders));
        System.out.println(Collections.max(leftRightBorders));
        System.out.println(averageUpDown);
        System.out.println(averageLeftRight);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setIcon(new ImageIcon(bufferedImages.get(i)));
            updateButtons();
        }

        for (File piece : pieces) {
            piece.delete();
        }
    }

    private void calculateBordersMatchPercentage(int columnsCounter) {
        for (int i = 0; i < upperBorders.size() - rows; i++) {
            upDownBorders.add(Math.abs(((((lowerBorders.get(i) * 1.0) / upperBorders.get(i + rows) * 1.0) * 100) - 100)));
        }

        for (int i = 1; i < leftBorders.size(); i++) {
            leftRightBorders.add(Math.abs(((((leftBorders.get(i) * 1.0) / rightBorders.get(i - 1) * 1.0) * 100) - 100)));
            if (i == columns + columnsCounter - 1) {
                i++;
                columnsCounter += columns;
            }
        }

        averageUpDown = upDownBorders.stream().mapToDouble(Double::doubleValue).sum() / upDownBorders.size();
        averageLeftRight = leftRightBorders.stream().mapToDouble(Double::doubleValue).sum() / leftRightBorders.size();

        if (timeTrigger) {
            startTime = new Date();
            timeTrigger = false;
        }

        long secondsDifference = new Date().getTime() - startTime.getTime();
        if (TimeUnit.MILLISECONDS.toSeconds(secondsDifference) >= 1) {
            timeTrigger = true;
            precisionPercent += 0.5; // precision percentage auto increment
        }
    }

    private void magicButton() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).putClientProperty("position", solutionPoints.get(i));
            buttons.get(i).setAngle(0);
        }

        pack();
        setLocationRelativeTo(null);
        checkSolution();
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

    public void updateButtons() {
        panel.removeAll();

        for (JComponent btn : buttons) {
            panel.add(btn);
        }

        panel.validate();
    }

    public void checkSolution() {
        List<Point> currentPoints = new ArrayList<>();
        List<Integer> currentAngles = new ArrayList<>();

        buttons.forEach((e) -> currentPoints.add((Point) e.getClientProperty("position")));
        buttons.forEach((e) -> currentAngles.add(e.getAngle()));

        if (compareList(solutionPoints, currentPoints) && compareList(solutionAngles, currentAngles)) {
            JOptionPane.showMessageDialog(panel, "Puzzle assembled!","Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            solutionPoints = new ArrayList<>();
            panel.setBorder(BorderFactory.createLineBorder(Color.green));
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

    public static Icon rotateImage(Image img, double angle) {
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
        setLocationRelativeTo(null);

        return button;
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

    public List<Button> getButtons() {
        return buttons;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JPanel getSourceImageTab() {
        return sourceImageTab;
    }

    public JTabbedPane getTabsPane() {
        return tabsPane;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}