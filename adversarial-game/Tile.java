import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Abstract class used to move all subclasses when we need to make it look like the hero is moving.
 * 
 * @author R. Gordon
 * @version May 8, 2019
 */
public abstract class Tile extends Actor
{
    /**
     * Instance variables
     * 
     * Can be used by any method below.
     */
    // The location of this object in the scrollable world
    private int scrollableWorldPositionX;
    private int scrollableWorldPositionY;
    
    /**
     * Constructor
     * 
     * This runs once.
     */
    Tile(int scrollableWorldX, int scrollableWorldY)
    {
        scrollableWorldPositionX = scrollableWorldX;
        scrollableWorldPositionY = scrollableWorldY;
    }

    /**
     * Move to left (to make hero look like they are moving right)
     */
    public void moveLeft(int speed)
    {
        setLocation(getX() - speed, getY());
    }

    /**
     * Move to right (to make hero look like they are moving left)
     */
    public void moveRight(int speed)
    {
        setLocation(getX() + speed, getY());
    }
    
}
