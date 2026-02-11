package com.allinone.framework;

import com.allinone.ui.painters.SkillPainter;
import java.awt.Graphics;

public abstract class AbstractSkillSet implements SkillSet {

    protected Node rootNode;
    protected long startTime;
    protected SkillPainter painter;

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void onPaint(Graphics g) {
        if (painter != null) painter.paint(g);
    }
}
