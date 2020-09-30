package com.zauberschlosss.main;

import com.zauberschlosss.listeners.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
import java.util.*;

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
    private Map<Integer, Long> solutionBitMap = new HashMap<>();
    private Map<Integer, Long> piecesBitMap = new HashMap<>();
    private List<Icon> icons = new ArrayList<>();

    private Image image;
    private int width, height;
    private int rows, columns;
    private int staticWidth = 800;
    private int staticHeight = 800;
    private boolean initRotated = false;
    private BufferedImage source;
    private BufferedImage resized;
    private String dataSource;
    private String uri;

    public Puzzle() throws URISyntaxException {
        setupResourcesAndUI();
    }

    private void setupResourcesAndUI() {
        setTitle("Puzzle");
        setVisible(true);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.addKeyListener(new KeyListener(this));

        tabsPane = new JTabbedPane();
        sourceImageTab = new JPanel(new FlowLayout(FlowLayout.CENTER));
        puzzlePicture = new JPanel(new FlowLayout(FlowLayout.CENTER));

        tabsPane.addTab("Puzzle", panel);
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
        gridSelection.addItem(4);
        gridSelection.addItem(5);
        gridSelection.addItem(6);
        gridSelection.addItem(7);
        gridSelection.addItem(8);
        gridSelection.addItem(9);
        gridSelection.addItem(10);

        gridSelection.setSelectedIndex(1);
        gridSelection.setMaximumRowCount(9);

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
        playAgain.addActionListener(e -> {
            reset();
            initResources();
            tabsPane.setSelectedIndex(0);
        });
        playAgain.setEnabled(false);
        game.add(playAgain);

        newPuzzle = new JMenuItem("New puzzle");
        newPuzzle.setAccelerator(KeyStroke.getKeyStroke("N"));
        newPuzzle.addActionListener(e -> {
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
        magicButton.addActionListener(e -> {
            loadPiecesAndInitPiecesBitMap();
            magicButton();
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

            if (source.getWidth() < staticWidth) {
                staticWidth = source.getWidth();
            }
            if (source.getHeight() < staticHeight) {
                staticHeight = source.getHeight();
            }

            if (source.getWidth() >= source.getHeight()) {
                int desiredHeight = getNewHeight(source.getWidth(), source.getHeight());
                resized = resizeImage(source, staticWidth, desiredHeight, BufferedImage.TYPE_INT_ARGB);
            } else {
                int desiredWidth = getNewWidth(source.getWidth(), source.getHeight());
                resized = resizeImage(source, desiredWidth, staticHeight, BufferedImage.TYPE_INT_ARGB);
            }
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

                long sumRGB = 0;
                for (int y = 0; y < piece.getHeight(); y++) {
                    for (int x = 0; x < piece.getWidth(); x++) {
                        sumRGB += piece.getRGB(x, y);
                    }
                }

                solutionBitMap.put(solutionBitMap.size(), sumRGB);

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

    private void reset() {
        buttons = new ArrayList<>();
        solutionPoints = new ArrayList<>();
        solutionAngles = new ArrayList<>();
        solutionBitMap = new HashMap<>();
        piecesBitMap = new HashMap<>();
        icons = new ArrayList<>();
        staticWidth = 800;
        staticHeight = 800;
        panel.removeAll();
        puzzlePicture.removeAll();

        File[] pieces = new File("./pieces").listFiles();
        if (Files.exists(Paths.get("./pieces"))) {
            Arrays.stream(pieces).forEach(File::delete);
        }
    }

    public void initResources() {
        try {
            rows = (int) gridSelection.getSelectedItem();
            columns = (int) gridSelection.getSelectedItem();
            initRotated = checkBoxIsRotated.isSelected();

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

            Path path = Paths.get("./pieces");
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void savePuzzlePieceToDisk(BufferedImage piece) {
        try {
            ImageIO.write(piece, "png", new File("./pieces/" + piece.hashCode() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPiecesAndInitPiecesBitMap() {
        File[] pieces = new File("./pieces").listFiles();
        for (int i = 0; i < pieces.length; i++) {
            BufferedImage piece = null;
            try {
                piece = ImageIO.read(pieces[i]);
                icons.add(new ImageIcon(piece));
            } catch (IOException e) {
                e.printStackTrace();
            }

            long sumRGB = 0;
            for (int y = 0; y < piece.getHeight(); y++) {
                for (int x = 0; x < piece.getWidth(); x++) {
                    sumRGB += piece.getRGB(x, y);
                }
            }

            piecesBitMap.put(piecesBitMap.size(), sumRGB);
        }

        for (File piece : pieces) {
            piece.delete();
        }
    }

    private void magicButton() {
        piecesBitMap.forEach((piecesKey, piecesValue) -> {
            solutionBitMap.forEach((solutionKey, solutionValue) -> {
                if (piecesValue.equals(solutionValue)) {
                    buttons.get(solutionKey).setIcon(icons.get(piecesKey));
                    updateButtons();
                }
            });
        });

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).putClientProperty("position", solutionPoints.get(i));
            buttons.get(i).setAngle(0);
        }

        pack();
        setLocationRelativeTo(null);
        checkSolution();
    }

    private int getNewHeight(int width, int height) {
        double ratio = staticWidth / (double) width;
        int newHeight = (int) (height * ratio);
        return newHeight;
    }

    private int getNewWidth(int width, int height) {
        double ratio = staticHeight / (double) height;
        int newWidth = (int) (width * ratio);
        return newWidth;
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