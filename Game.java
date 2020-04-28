/**
 *  Insert game description here
 */

public class Game 
{
    
    private Player player;
        
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
        Item hallCasket, hallFlowerPot, hallMagazine,secondFloorCasket;
        
      
        // create the rooms
        hall = new Room("hall the main entrance of the university");
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
        hallCasket = new Item("casket","a small wooden casket",20,100,true,true);
        hallFlowerPot = new Item("pot","a ceramic pot with a dead flower inside",1,100,false,false);
        hallMagazine = new Item("magazine","a magazine, supposedly delivered by mail",2,10,false,false);
        
        secondFloorCasket = new Item("casket","a small blue dyed wooden casket",20,100,true,false);
        
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
        System.out.println("here should be description");
    }

}
