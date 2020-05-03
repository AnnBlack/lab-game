import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player
{
    private Parser parser;
    private Room currentRoom;
    private ArrayList<Item> inventory;
    private final int maxCapacity = 430;
    private int score=0;
    private int weight=0;
    private boolean gameCompleted=false;

    /**
     * Constructor for objects of class Player
     */
    public Player(int maxCapacity,Room currentRoom)
    {
        parser = new Parser();
        inventory = new ArrayList<>();
        this.currentRoom = currentRoom;
    }
    public boolean play() 
    {
        boolean finished = false;
        currentRoom.printDescription();
        currentRoom.printShortDescription();
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        return gameCompleted;
    }
    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the Game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("take")) {
           pickUp(command);
        }
        else if (commandWord.equals("drop")) {
           drop(command);
        }
        else if (commandWord.equals("inspect")) {
           inspect(command);
        }
        else if (commandWord.equals("leave")) {
           wantToQuit = leave(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        
        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {   
        System.out.println();
        System.out.println("You say the magic word. Nothing happens at first, then");
        System.out.println("a striking realization hits you: ");
        System.out.println("You are on a mission of retrieving your inheritance");
        System.out.println("in a creepy mansion full of all sorts of uncanny relics");
        System.out.println("and ancient artifacts. A totally normal situation ");
        System.out.println("to find yourself in.");
        System.out.println();
        System.out.println("All the next possible steps become very clear to you:");
        
        parser.showCommands();
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) 
        {
            System.out.println("There is no door!");
        }
        else if((nextRoom.getName().contains("basement"))&&(!hasBasementKey()))
        {
            System.out.println("You don't have the key!"); 
        }
        else if(nextRoom.getName().contains("bedroom"))
        {
            nextRoom.printShortDescription(); 
        }
        else {
            currentRoom = nextRoom;
            currentRoom.printDescription();//printing 1st part of description
            currentRoom.printShortDescription();//printing 2nd part of description
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the Game.
     * @return true, if this command quits the Game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    private void inspect(Command command) 
    {
        if(command.hasSecondWord()) {
            String itemName = command.getSecondWord();
            boolean searching = true;
            for (Item item : currentRoom.getItems()) 
            {
                if (item.getName().contains(itemName))
                {item.printDescription();
                searching = false;}
            }
            if(searching)
            {   
                System.out.printf("%nYou don't see it anywhere in the room.%n");
            }
        }
        else 
        {
            currentRoom.printShortDescription();  //only print second part of description
        }
    }
    
    /**
     * pick Up
     */
    public void pickUp(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Take what?");
            return;
        };
        
        String itemName = command.getSecondWord();
        Item extractedItem;
        
        //System.out.println("looking for " +itemName);
        if((extractedItem = currentRoom.getItem(itemName))==null)
        {
            System.out.println("You don't see it anywhere in the room");
            return;
        }
        else 
        {
            if(extractedItem.getIsCasket())//if the casket is found
        {   
            if(pickLock())
            {   
                currentRoom.removeItem(extractedItem);
                extractedItem = extractedItem.getContainedItem();    //otherwise we get endless amount of caskets
                currentRoom.setItem(extractedItem);
                System.out.printf("There is a " + extractedItem.getName() +" inside. %n");
                extractedItem.printDescription();
                System.out.println();
            }
        }   
            if(weight+extractedItem.getWeight()<=maxCapacity)
            {
            inventory.add(extractedItem);
            currentRoom.removeItem(extractedItem);
            score+=extractedItem.getScore();
            weight+=extractedItem.getWeight();
            System.out.println("you put the " +extractedItem.getName() + " inside your bag");
            System.out.println();
            System.out.printf("your inventory now: %d/%d%nscore: %d%n", weight,maxCapacity,score);
            printInventory();
            }
            else if(extractedItem.getWeight()>500)
            {System.out.println("Did you really think you could somehow fit that in your bag?");}
            else
            {System.out.println("Your inventory is full! Looks like you'll have to give some of that loot up");}
        }

    }
    
    public void drop(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Drop what?");
            return;
        };
        
        String itemName = command.getSecondWord();
        
        for (Item item : inventory) 
        {
            if (item.getName().contains(itemName)) 
            {
            currentRoom.setItem(item);
            inventory.remove(item);
            score-=item.getScore();
            weight-=item.getWeight();
            System.out.println("You drop the " +item.getName() + " on the floor");
            return;
            }
        };
        System.out.println("You don't have it!");
        return;
    }
    /** 
     * the method that will loop and thus read the user input either until the number is quessed
     * or until all the attempts are used (currently 5)
     */
    public boolean pickLock() 
    {                         
        boolean isOpened = false;
        int trueCode = getRandomNumberInRange(100,999);
        int permittedAttempts = 5;      
        
        System.out.printf("%nYou try to pick the lock with your hacking tool%n");
        System.out.println("the generated true code: " +trueCode + " shouldn't be here in the final version ofc");
        for (int i = 0; i<permittedAttempts; i++)
            {
                Scanner input = new Scanner(System.in);  //сканнер пока не проверяет френдли юзера
                int guessCode = input.nextInt();
                if(hack(guessCode, trueCode)) //может не разделять на пикап и хак методы? чтобы не пассить 
                {                             //один и тот же труКод макс 5 раз
                    isOpened = true;          //с другой стороны, мне кажется проще понять код если они разделены 
                    System.out.println("You've managed to open the lock");
                    break; 
                }
            }
        if(!isOpened)
            {
                System.out.println("You hear the sound of the lock getting stuck and realize that");
                System.out.println("after several attempts  it gets broken. You decide to leave it be");
                System.out.println("for now. Your hacking tool is not perfect after all!");
            }
        return isOpened;
    }
    /** 
     * the method resposible for the logical hacking quiz
     */
    public boolean hack(int guessCode, int trueCode)
    {   
        boolean isHacked = false;
        int bulls=0;                //represent • and -
        int cows=0;
        
        HashMap<String,Integer> bullsCows=new HashMap<String,Integer>();
        String trueCodeString = Integer.toString(trueCode);
        String guessCodeString = Integer.toString(guessCode);
        for (int i = 0; i < trueCodeString.length(); i++)
        {   
            for (int j = 0; j < guessCodeString.length(); j++)
            {   
                if (trueCodeString.charAt(i)==guessCodeString.charAt(j))
                {
                    if(i==j){ bulls++; }
                    else{cows++;}
                }
            }
        }
        
        if(bulls==3)
        {
            isHacked = true;
            return isHacked;
        }
        
        bullsCows.put("•",bulls);
        bullsCows.put("-",cows);
        printTable(guessCode,bulls,cows);
        
        return isHacked;
    }
    
    public void printInventory()
    {
        for (Item item:inventory)
        {   System.out.println(item.getName());}
        System.out.println();
    }
    
    public void printTable(int guessCode,int bulls, int cows)
    {
        System.out.printf("%nyour guess: %d%n•: %d%n-: %d%n",guessCode,bulls,cows);
        
    }
    
    private static int getRandomNumberInRange(int min, int max) 
    {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
    
    public boolean hasBasementKey()
    {
        for (Item item:inventory)
        {  if(item.getName().contains("key"))
            {return true;}
        }
        
        return false;
    }
    
    public boolean hasWinningCondition()
    {   
        int numberOfCaskets =0;
        
        for (Item item:inventory)
        {  if(item.getName().contains("charm"))
            {return true;}
           if(item.getIsCasket())
           {numberOfCaskets+=1;}
        }
        
        if(numberOfCaskets==3)
        {
            return true;
        }
        else{
        return false;}
    }
    
    public int getScore()
    {
        return score;
    }
    
    public boolean leave(Command command)
    {   if(command.hasSecondWord()) {
            System.out.println("Leave what?");
            return false;
        };
        System.out.printf("%nYou decide it's time to leave this wicked house%n%n");
        gameCompleted = true;
        return true;
    }
}
