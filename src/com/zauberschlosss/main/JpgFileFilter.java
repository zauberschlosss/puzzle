package com.zauberschlosss.main;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class JpgFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg");
    }

    @Override
    public String getDescription() {
        return "JPG Image";
    }
}