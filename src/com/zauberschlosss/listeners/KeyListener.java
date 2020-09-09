package com.zauberschlosss.listeners;

import com.zauberschlosss.main.Button;
import com.zauberschlosss.main.Puzzle;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyListener extends KeyAdapter {
    private final Puzzle puzzle;

    public KeyListener(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            puzzle.rotateIcon(Button.buttonPressed, -90);
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            puzzle.rotateIcon(Button.buttonPressed, 90);
        }

        if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_S)) {
            puzzle.rotateIcon(Button.buttonPressed, 180);
        }


        puzzle.updateButtons();
        puzzle.checkSolution();
    }
}