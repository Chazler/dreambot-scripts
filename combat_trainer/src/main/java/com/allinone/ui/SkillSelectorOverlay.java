package com.allinone.ui;

import com.allinone.AllInOneScript;
import com.allinone.framework.SkillSet;
import org.dreambot.api.Client;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SkillSelectorOverlay implements MouseListener {

    private final AllInOneScript script;
    private final Rectangle area;
    private boolean isExpanded = false; // Toggle for skill list

    // Buttons
    private Rectangle expandButton;
    private Rectangle addTimeButton;
    // Skill buttons
    private Rectangle[] skillButtons;

    public SkillSelectorOverlay(AllInOneScript script) {
        this.script = script;
        // Position on Minimap? 
        // Fixed mode minimap center is roughly top right.
        // Let's put a small button panel below the minimap orb area?
        // Or simply in the top right corner of the VIEWPORT (if resizable).
        // For safety, let's use relative coordinates to the game canvas width.
        
        // Initial setup will be calculated in onPaint to handle resize
        this.area = new Rectangle(0, 0, 0, 0); 
    }

    public void onPaint(Graphics g) {
        int width = Client.getCanvas().getWidth();
        int baseX = width - 180; // Right aligned
        int baseY = 160; // Below minimap roughly
        
        // Setup Main Panel
        int rowHeight = 25;
        int padding = 5;
        
        this.expandButton = new Rectangle(baseX, baseY, 170, rowHeight);
        this.addTimeButton = new Rectangle(baseX, baseY + rowHeight + padding, 170, rowHeight);
        
        // Draw Toggle Button
        drawButton(g, expandButton, isExpanded ? "Close Skill Selector" : "Select Skill", new Color(70, 70, 70, 200));
        
        // Draw Add Time Button
        drawButton(g, addTimeButton, "+5 Minutes", new Color(0, 100, 0, 200));

        if (isExpanded) {
            java.util.List<SkillSet> skills = script.getAvailableSkills();
            skillButtons = new Rectangle[skills.size()];
            
            int y = baseY + (rowHeight + padding) * 2;
            
            for (int i = 0; i < skills.size(); i++) {
                SkillSet skill = skills.get(i);
                Rectangle btn = new Rectangle(baseX, y, 170, rowHeight);
                skillButtons[i] = btn;
                
                boolean isCurrent = script.getCurrentSkill() == skill;
                Color c = isCurrent ? new Color(0, 0, 150, 200) : new Color(50, 50, 50, 200);
                drawButton(g, btn, skill.getName(), c);
                
                y += rowHeight + padding;
            }
        }
    }
    
    private void drawButton(Graphics g, Rectangle r, String text, Color bg) {
        g.setColor(bg);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.WHITE);
        g.drawRect(r.x, r.y, r.width, r.height);
        
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
        
        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        // Handle Toggle
        if (expandButton != null && expandButton.contains(p)) {
            isExpanded = !isExpanded;
            e.consume();
            return;
        }
        
        // Handle Add Time
        if (addTimeButton != null && addTimeButton.contains(p)) {
            script.addTime(5 * 60 * 1000); // 5 mins
            e.consume();
            return;
        }
        
        // Handle Skill Selection
        if (isExpanded && skillButtons != null) {
            java.util.List<SkillSet> skills = script.getAvailableSkills();
            for (int i = 0; i < skillButtons.length; i++) {
                if (skillButtons[i].contains(p)) {
                    SkillSet selected = skills.get(i);
                    script.setCurrentSkill(selected);
                    isExpanded = false;
                    e.consume();
                    return;
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
