package com.zauberschlosss.main;

import com.zauberschlosss.listeners.MouseListener;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {
    public static Button buttonPressed;
    public static Button buttonReleased;
    static int[] angles = new int[] {0, 90, 180, 270};

    private int angle = 0;
    private Puzzle puzzle;
    private MouseListener mouseListener;

    public Button(Image image, Puzzle puzzle) {
        super(new ImageIcon(image));
        this.puzzle = puzzle;
        mouseListener = new MouseListener(this, puzzle);
        addMouseListener(mouseListener);
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}