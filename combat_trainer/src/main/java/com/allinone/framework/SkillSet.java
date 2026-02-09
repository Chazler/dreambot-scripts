package com.allinone.framework;

public interface SkillSet {
    /**
     * Called when the script starts or when this skill is selected.
     * @param blackboard The shared blackboard
     */
    void onStart(Blackboard blackboard);

    /**
     * Returns the root node of the behavior tree for this skill.
     * @return The root Node
     */
    Node getRootNode();

    /**
     * Called on each repaint frame.
     * @param g Graphics context
     */
    default void onPaint(java.awt.Graphics g) {}
    
    /**
     * Returns the name of the skill set.
     * @return Name string
     */
    String getName();

    /**
     * Returns the current level for this skill set.
     * Used for weighted random selection (lower level = higher priority).
     * @return Level integer
     */
    int getCurrentLevel();
}
