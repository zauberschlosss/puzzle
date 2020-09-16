package com.zauberschlosss.listeners;

import com.zauberschlosss.main.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MKeyListener extends KeyAdapter {
    private final Puzzle puzzle;

    public MKeyListener(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            puzzle.rotateIcon(MButton.buttonPressed, -90);
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            puzzle.rotateIcon(MButton.buttonPressed, 90);
        }

        if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_S)) {
            puzzle.rotateIcon(MButton.buttonPressed, 180);
        }


        puzzle.updateButtons();
        puzzle.checkSolution();
    }
}