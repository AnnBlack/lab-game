
/**
 * Write a description of class Item here.
 *
 */
public class Item
{
    private String description;
    private int score;
    private int weight;
    private String name;
    boolean isCasket;
    boolean isTarget;

    /**
     * Constructor for objects of class Item
     */
    public Item(String name, String description, int score, int weight, boolean isCasket, boolean isTarget) 
    {
        this.name = name;
        this.description = description;
        this.score = score;
        this.weight = weight;
        this.isCasket = isCasket;
        this.isTarget = isTarget;
    }
    /**
     * methods:
     */
    public String getShortDescription()
    {
        return description;
    }
    
    public String getName()
    {
        return name;
    }
    public boolean getIsCasket()
    {
        return isCasket;
    }
    public boolean getIsTarget()
    {
        return isTarget;
    }
}
