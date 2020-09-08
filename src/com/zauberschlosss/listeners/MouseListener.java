package com.zauberschlosss.listeners;

import com.zauberschlosss.main.Button;
import com.zauberschlosss.main.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

public class MouseListener extends MouseAdapter {
    private Button button;
    private Puzzle puzzle;

    public MouseListener(Button button, Puzzle puzzle) {
        this.button = button;
        this.puzzle = puzzle;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        button.setBorder(BorderFactory.createLineBorder(Color.blue));
        button.buttonReleased = (Button) e.getComponent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        button.buttonPressed = (Button) e.getComponent();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (button.buttonPressed != null && button.buttonReleased != null) {
            int pressedButtonIndex = puzzle.getButtons().indexOf(button.buttonPressed);
            int releasedButtonIndex = puzzle.getButtons().indexOf(button.buttonReleased);

            if (pressedButtonIndex != releasedButtonIndex) {
                Collections.swap(puzzle.getButtons(), pressedButtonIndex, releasedButtonIndex);
                button.buttonPressed.setBorder(BorderFactory.createLineBorder(Color.blue));
                button.buttonReleased.setBorder(BorderFactory.createLineBorder(Color.gray));
            }

            puzzle.updateButtons();
            puzzle.checkSolution();
        }
    }
}
