import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * This is the class for the "main character" in the action.
 * 
 * @author R. Gordon
 * @version May 8, 2019
 */
public abstract class Player extends Collision
{
    /**
     * Instance variables
     * 
     * These are available for use in any method below.
     */    
    // Horizontal speed (change in horizontal position, or delta X)
    private int deltaX = 4;

    // Vertical speed (change in vertical position, or delta Y)
    private int deltaY = 4;

    // Acceleration for falls
    private int acceleration = 2;

    // Strength of a jump
    private int jumpStrength = -24;

    // Track whether game is over or not
    private boolean isGameOver;

    // Constants to track vertical direction
    private static final String JUMPING_UP = "up";
    private static final String JUMPING_DOWN = "down";
    private String verticalDirection;

    // Constants to track horizontal direction
    private static final String FACING_RIGHT = "right";
    private static final String FACING_LEFT = "left";
    private String horizontalDirection;

    // For walking animation
    private GreenfootImage walkingRightImages[];
    private GreenfootImage walkingLeftImages[];
    private static final int WALK_ANIMATION_DELAY = 8;

    // Keeps track of total number of walking image frames (varies by character)
    int countOfWalkingImages;
    
    // Keep track of what key to respond to
    private String moveLeftKey;
    private String moveRightKey;
    private String jumpKey;

    // Keeps track of what frame is currently being used in animation
    private int walkingFrames;

    // Name of player images
    private String imageNamePrefix;

    /**
     * Constructor
     * 
     * This runs once when the Player object is created.
     */
    Player(int startingX, String playerName, int walkingImagesCount,
            String moveLeftWithKey, String moveRightWithKey, String jumpWithKey)
    {
        // Assigned how many walking image frames there are
        countOfWalkingImages = walkingImagesCount;
        
        // Assign keystrokes that this object will respond to
        moveLeftKey = moveLeftWithKey;
        moveRightKey = moveRightWithKey;
        jumpKey = jumpWithKey;

        // Game on
        isGameOver = false;

        // First jump will be in 'down' direction
        verticalDirection = JUMPING_DOWN;

        // Facing right to start
        horizontalDirection = FACING_RIGHT;

        // Set the image name prefix (guile, viga, et cetera);
        this.imageNamePrefix = playerName;

        // Set image
        setImage(imageNamePrefix + "-jump-down-right.png");

        // Initialize the 'walking' arrays
        walkingRightImages = new GreenfootImage[countOfWalkingImages];
        walkingLeftImages = new GreenfootImage[countOfWalkingImages];

        // Load walking images from disk
        for (int i = 0; i < walkingRightImages.length; i++)
        {
            walkingRightImages[i] = new GreenfootImage(imageNamePrefix + "-walk-right-" + i + ".png");

            // Create left-facing images by mirroring horizontally
            walkingLeftImages[i] = new GreenfootImage(walkingRightImages[i]);
            walkingLeftImages[i].mirrorHorizontally();
        }

        // Track animation frames for walking
        walkingFrames = 0;
    }

    /**
     * Act - do whatever the Player wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        checkKeys();
        checkFall();
        if (!isGameOver)
        {
            checkGameOver();
        }
    }

    /**
     * Respond to keyboard action from the user.
     */
    private void checkKeys()
    {
        // Walking keys
        if (Greenfoot.isKeyDown(moveLeftKey) && !isGameOver)
        {
            moveLeft();
        }
        else if (Greenfoot.isKeyDown(moveRightKey) && !isGameOver)
        {
            moveRight();
        }
        else
        {
            // Standing still; reset walking animation
            walkingFrames = 0;
        }

        // Jumping
        if (Greenfoot.isKeyDown(jumpKey) && !isGameOver)
        {
            // Only able to jump when on a solid object
            if (onPlatform())
            {
                jump();
            }
        }
    }

    /**
     * Should the player be falling right now?
     */
    public void checkFall()
    {
        if (onPlatform())
        {
            // Stop falling
            deltaY = 0;

            // Set image
            if (horizontalDirection == FACING_RIGHT && Greenfoot.isKeyDown("right") == false)
            {
                setImage(imageNamePrefix + "-right.png");
            }
            else if (horizontalDirection == FACING_LEFT && Greenfoot.isKeyDown("left") == false)
            {
                setImage(imageNamePrefix + "-left.png");
            }

            // Get a reference to any object that's created from a subclass of Platform,
            // that is below (or just below in front, or just below behind) the player
            Actor directlyUnder = getOneObjectAtOffset(0, getImage().getHeight() / 2, Platform.class);
            Actor frontUnder = getOneObjectAtOffset(getImage().getWidth() / 3, getImage().getHeight() / 2, Platform.class);
            Actor rearUnder = getOneObjectAtOffset(0 - getImage().getWidth() / 3, getImage().getHeight() / 2, Platform.class);

            // Bump the player back up so that they are not "submerged" in a platform object
            if (directlyUnder != null)
            {
                int correctedYPosition = directlyUnder.getY() - directlyUnder.getImage().getHeight() / 2 - this.getImage().getHeight() / 2;
                setLocation(getX(), correctedYPosition);
            }
            if (frontUnder != null)
            {
                int correctedYPosition = frontUnder.getY() - frontUnder.getImage().getHeight() / 2 - this.getImage().getHeight() / 2;
                setLocation(getX(), correctedYPosition);
            }
            if (rearUnder != null)
            {
                int correctedYPosition = rearUnder.getY() - rearUnder.getImage().getHeight() / 2 - this.getImage().getHeight() / 2;
                setLocation(getX(), correctedYPosition);
            }
        }
        else
        {
            fall();
        }
    }

    /**
     * Is the player currently touching a solid object? (any subclass of Platform)
     */
    public boolean onPlatform()
    {
        // Get an reference to a solid object (subclass of Platform) below the player, if one exists
        Actor directlyUnder = getOneObjectAtOffset(0, getImage().getHeight() / 2, Platform.class);
        Actor frontUnder = getOneObjectAtOffset(getImage().getWidth() / 3, getImage().getHeight() / 2, Platform.class);
        Actor rearUnder = getOneObjectAtOffset(0 - getImage().getWidth() / 3, getImage().getHeight() / 2, Platform.class);

        // If there is no solid object below (or slightly in front of or behind) the player...
        if (directlyUnder == null && frontUnder == null && rearUnder == null)
        {
            return false;   // Not on a solid object
        }
        else
        {
            return true;
        }
    }

    /**
     * Make the player jump.
     */
    public void jump()
    {
        // Track vertical direction
        verticalDirection = JUMPING_UP;

        // Set image
        if (horizontalDirection == FACING_RIGHT)
        {
            setImage(imageNamePrefix + "-jump-up-right.png");
        }
        else
        {
            setImage(imageNamePrefix + "-jump-up-left.png");
        }

        // Change the vertical speed to the power of the jump
        deltaY = jumpStrength;

        // Make the character move vertically 
        fall();
    }

    /**
     * Make the player fall.
     */
    public void fall()
    {
        // See if direction has changed
        if (deltaY > 0)
        {
            verticalDirection = JUMPING_DOWN;

            // Set image
            if (horizontalDirection == FACING_RIGHT)
            {
                setImage(imageNamePrefix + "-jump-down-right.png");
            }
            else
            {
                setImage(imageNamePrefix + "-jump-down-left.png");
            }
        }

        // Fall (move vertically)
        int newYPosition = getY() + deltaY;
        setLocation(getX(), newYPosition);

        // Accelerate (fall faster next time)
        deltaY = deltaY + acceleration;
    }

    /**
     * Animate walking
     */
    private void animateWalk(String direction)
    {
        // Track walking animation frames
        walkingFrames += 1;

        // Get current animation stage
        int stage = walkingFrames / WALK_ANIMATION_DELAY;

        // Animate
        if (stage < walkingRightImages.length)
        {
            // Set image for this stage of the animation
            if (direction == FACING_RIGHT)
            {
                setImage(walkingRightImages[stage]);
            }
            else
            {
                setImage(walkingLeftImages[stage]);
            }
        }
        else
        {
            // Start animation loop from beginning
            walkingFrames = 0;
        }
    }

    /**
     * Move the player to the right.
     */
    public void moveRight()
    {
        // Track direction
        horizontalDirection = FACING_RIGHT;

        // Set image 
        if (onPlatform())
        {
            animateWalk(horizontalDirection);
        }
        else
        {
            // Set appropriate jumping image
            if (verticalDirection == JUMPING_UP)
            {
                setImage(imageNamePrefix + "-jump-up-right.png");
            }
            else
            {
                setImage(imageNamePrefix + "-jump-down-right.png");
            }
        }

        // Get object reference to world
        GameWorld world = (GameWorld) getWorld(); 

        if (getX() < world.VISIBLE_WIDTH)
        {
            // Move to right in visible world
            int newXPosition = getX() + deltaX;
            setLocation(newXPosition, getY());
        }

    }

    /**
     * Move the player to the left.
     */
    public void moveLeft()
    {
        // Track direction
        horizontalDirection = FACING_LEFT;

        // Set image 
        if (onPlatform())
        {
            animateWalk(horizontalDirection);
        }
        else
        {
            // Set appropriate jumping image
            if (verticalDirection == JUMPING_UP)
            {
                setImage(imageNamePrefix + "-jump-up-left.png");
            }
            else
            {
                setImage(imageNamePrefix + "-jump-down-left.png");
            }
        }

        // Get object reference to world
        GameWorld world = (GameWorld) getWorld(); 

        // Don't let player go off left edge of scrollable world 
        // (Allow movement only when not at left edge)
        if (getX() > 0)
        {
            // Move left in visible world
            int newXPosition = getX() - deltaX;
            setLocation(newXPosition, getY());
        }            

    }

    /**
     * When the player falls off the bottom of the screen,
     * game is over. We must remove them.
     */
    public void checkGameOver()
    {
        // Get object reference to world
        GameWorld world = (GameWorld) getWorld(); 

        // Vertical position where player no longer visible
        int offScreenVerticalPosition = (world.getHeight() + this.getImage().getHeight() / 2);

        // Off bottom of screen?
        if (this.getY() > offScreenVerticalPosition)
        {
            // Remove the player
            isGameOver = true;
            world.setGameOver();
            world.removeObject(this);

            // Tell the user game is over
            world.showText("GAME OVER", world.getWidth() / 2, world.getHeight() / 2);
        }
    }
}
