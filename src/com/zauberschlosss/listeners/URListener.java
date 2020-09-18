package com.zauberschlosss.listeners;

import com.zauberschlosss.main.Puzzle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class URListener implements ActionListener {
    private Puzzle puzzle;

    public URListener(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object result = JOptionPane.showInputDialog(puzzle.getPanel(), "Enter URL");
        puzzle.setDataSource("URL");
        puzzle.setUri((String) result);
        puzzle.initResources();

        puzzle.getTabsPane().setSelectedIndex(0);
    }
}
