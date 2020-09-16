package com.zauberschlosss.listeners;

import com.zauberschlosss.main.*;

import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

public class MMouseListener extends MouseAdapter {
    private MButton button;
    private Puzzle puzzle;

    public MMouseListener(MButton button, Puzzle puzzle) {
        this.button = button;
        this.puzzle = puzzle;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        button.setBorder(BorderFactory.createLineBorder(Color.blue));
        MButton.buttonReleased = (MButton) e.getComponent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        MButton.buttonPressed = (MButton) e.getComponent();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (MButton.buttonPressed != null && MButton.buttonReleased != null) {
            int pressedButtonIndex = puzzle.getButtons().indexOf(MButton.buttonPressed);
            int releasedButtonIndex = puzzle.getButtons().indexOf(MButton.buttonReleased);

            if (pressedButtonIndex != releasedButtonIndex) {
                Collections.swap(puzzle.getButtons(), pressedButtonIndex, releasedButtonIndex);
                MButton.buttonPressed.setBorder(BorderFactory.createLineBorder(Color.blue));
                MButton.buttonReleased.setBorder(BorderFactory.createLineBorder(Color.gray));
            }

            puzzle.updateButtons();
            puzzle.checkSolution();
            puzzle.getPanel().grabFocus();
        }
    }
}