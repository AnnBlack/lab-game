import java.util.ArrayList;

public class PlayerState
{
    /*private Room currentRoom;
    private Room previousRoom; // 1
    private ArrayList<Item> inventory;
    private ArrayList<Item> previousInventory; // 2
    private int score = 0;
    private int previousScore = 0; // 3
    private int weight = 0;
    private int previousWeight = 0; // 4
    */
    
    private ArrayList<Player> stateTrackList;
    
    /**
     * Constructor for objects of class PlayerState
     */
    public PlayerState()
    {
        // initialise instance variables
        this.stateTrackList = new ArrayList<>();
    }
    
    public void setPlayerState(Player player)
    {
        stateTrackList.add(player);
    }
    
    public Player getPlayerState(int step)
    {
        /*for(int i = 0; i < stateTrackList.size(); i++) {
            return stateTrackList.get(i);
        } */
        System.out.println("step: " + step);
        return stateTrackList.get(step - 1);
        //return null;
    }
    
    private void printScoreState() 
    {
        for(int i = 0; i < stateTrackList.size(); i++) {
            System.out.println(stateTrackList.get(i).score);
        }
    }
}
