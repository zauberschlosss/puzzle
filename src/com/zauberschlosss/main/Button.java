package com.zauberschlosss.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

public class Button extends JButton {
    public static Button buttonPressed;
    public static Button buttonReleased;
    static int[] angles = new int[] {0, 90, 180, 270};

    private int angle = 0;
    private Puzzle puzzle;

    public Button(Image image, Puzzle puzzle) {
        super(new ImageIcon(image));
        this.puzzle = puzzle;
        initUI();
    }

    private void initUI() {
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
                if (buttonPressed != null && buttonReleased != null) {
                    int pressedButtonIndex = puzzle.getButtons().indexOf(buttonPressed);
                    int releasedButtonIndex = puzzle.getButtons().indexOf(buttonReleased);

                    if (pressedButtonIndex != releasedButtonIndex) {
                        Collections.swap(puzzle.getButtons(), pressedButtonIndex, releasedButtonIndex);
                        buttonPressed.setBorder(BorderFactory.createLineBorder(Color.blue));
                        buttonReleased.setBorder(BorderFactory.createLineBorder(Color.gray));
                    }

                    puzzle.updateButtons();
                    puzzle.checkSolution();
                }
            }
        });
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}