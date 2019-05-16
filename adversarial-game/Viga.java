import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Viga here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Viga extends Player
{

    /**
     * Constructor
     * 
     * Runs once when this object is created
     */
    Viga(int startingX, String moveLeftWithKey, String moveRightWithKey, String jumpWithKey, String punchWithKey)
    {
        // Invoke the superclass constructor
        super(startingX, "viga", 2, 3, moveLeftWithKey, moveRightWithKey, jumpWithKey, punchWithKey);
    }

    /**
     * Act - do whatever the Viga wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Add your action code here.
        super.act();
    }    
}
