/**
 *  Insert game description here
 */

public class Game 
{
    private Player player;
    public static Item casketItemRing; //NOT OKAY  
    
    /**
     * Create the Game and initialise its internal map.
     */
    public Game() 
    {
        initialize();
    }

    /**
     * Create all the rooms, items and player.
     */
    private void initialize()
    {
        Room hall, secondFloor, questRoom, workshop, lab, bedroom;
        Item hallFlowerPot, hallMagazine, casketItemPendant;
        Casket hallCasket, secondFloorCasket;
        
        
      
        // create the rooms
        hall = new Room("You are in a hall of what looks to be collector's apartements. %nAmongst all the other strange items that fill the room, %none thing that sticks out to you is a portrait on the wall above %nthe staircase (You assume it's the owner of this house). %nThe subject's eyes seem to follow you no matter where you go.");
        questRoom = new Room("in a lecture bedroom");
        workshop = new Room("in the campus workshop");
        secondFloor = new Room("in a computing secondFloor");
        bedroom = new Room("in the computing admin bedroom");
        
        // initialise room exits
        hall.setExit("east", bedroom);
        hall.setExit("up", secondFloor);
        hall.setExit("west", workshop);

        bedroom.setExit("west", hall);

        workshop.setExit("east", hall);

        secondFloor.setExit("down", hall);
        secondFloor.setExit("east", bedroom);

        bedroom.setExit("west", secondFloor);
        
        //initializing items:
        casketItemRing = new Item("ring", "apparently the one your aunt left you",1000,5,false);
        hallCasket = new Casket("casket","a small wooden casket",20,100,true,casketItemRing);
        hallFlowerPot = new Item("pot","a ceramic pot with a dead flower inside",1,100,false);
        hallMagazine = new Item("magazine","a magazine, supposedly delivered by mail",2,10,false);
        
        casketItemPendant = new Item("pendant","a small pendant",200,5,false);
        secondFloorCasket = new Casket("casket","a small blue dyed wooden casket",20,100,true,casketItemPendant);
        
        //filling rooms with items:
        hall.setItem(hallCasket);
        hall.setItem(hallFlowerPot);
        hall.setItem(hallMagazine);
        
        secondFloor.setItem(secondFloorCasket);
        
        
        player = new Player(530, hall);  // start Game hall
        
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();
                
        boolean finished = false;
        while (! finished) {
            player.play();
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the Game.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("You are breaking into a house with a huge private collection (not entirely legal) of ancient relics and valuables to retrieve your family jewels.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        //System.out.println("here should be description");
    }

}
