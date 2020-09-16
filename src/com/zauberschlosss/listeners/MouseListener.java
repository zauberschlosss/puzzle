package com.zauberschlosss.listeners;

import com.zauberschlosss.main.*;

import javax.swing.BorderFactory;
import java.awt.Color;
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
        Button.buttonReleased = (Button) e.getComponent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Button.buttonPressed = (Button) e.getComponent();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Button.buttonPressed != null && Button.buttonReleased != null) {
            int pressedButtonIndex = puzzle.getButtons().indexOf(Button.buttonPressed);
            int releasedButtonIndex = puzzle.getButtons().indexOf(Button.buttonReleased);

            if (pressedButtonIndex != releasedButtonIndex) {
                Collections.swap(puzzle.getButtons(), pressedButtonIndex, releasedButtonIndex);
                Button.buttonPressed.setBorder(BorderFactory.createLineBorder(Color.blue));
                Button.buttonReleased.setBorder(BorderFactory.createLineBorder(Color.gray));
            }

            puzzle.updateButtons();
            puzzle.checkSolution();
            puzzle.getPanel().grabFocus();
        }
    }
}