
/**
 * Write a description of class Casket here inherited from Item.
 */
public class Casket extends Item
{
    private Item containedItem;
    
    public Casket(String name, String description, int score, int weight, boolean isCasket, Item containedItem)
    {   
        super(name,description,score,weight,isCasket);
        this.containedItem = containedItem;
    }
    
    public Item getContainedItem()
    {   
        super.getContainedItem();
        return containedItem;
    }
}
