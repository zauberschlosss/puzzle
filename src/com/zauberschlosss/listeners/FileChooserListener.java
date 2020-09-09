package com.zauberschlosss.listeners;

import com.zauberschlosss.main.JpgFileFilter;
import com.zauberschlosss.main.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileChooserListener implements ActionListener {
    private Puzzle puzzle;

    public FileChooserListener(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(700, 600));
        fileChooser.setFileFilter(new JpgFileFilter());

        if (fileChooser.showDialog(puzzle.getSourceImageTab(), "Open") == JFileChooser.OPEN_DIALOG) {
            puzzle.setDataSource("HDD");
            puzzle.setUri(fileChooser.getSelectedFile().toString());
            puzzle.initResources();
            puzzle.getTabsPane().setSelectedIndex(0);
        }
    }
}