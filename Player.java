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
    private int maxCapacity;
    private int score;

    /**
     * Constructor for objects of class Player
     */
    public Player(int maxCapacity,Room currentRoom)
    {
        parser = new Parser();
        inventory = new ArrayList<>();
        this.currentRoom = currentRoom;
    }
    public void play() 
    {
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
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
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
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

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
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
        
        System.out.println("looking for " +itemName);
        if((extractedItem = currentRoom.extractItem(itemName))==null)
        {
            System.out.println("no such item");
            return;
        }
        else 
        {
            if(extractedItem.getIsCasket())//if the casket is found
        {   
            if(pickLock())
            {
                if(extractedItem.getIsTarget())//удалить условие после имплементации наследствия
                    {   //поменять на extractedItem.itemWithin;
                        extractedItem = new Item("ring", "apparently the one your aunt left you",1000,5,false,false);
                        System.out.println("There is something inside the casket.");
                    }
            }
        }
            inventory.add(extractedItem);
            System.out.println("you put the " +extractedItem.getName() + " inside your bag");
            System.out.println();
            System.out.println("your inventory has now:");
            printInventory();
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
    public boolean pickLock() //сделала отдельный метод для взлома, чтоыбы не зависел от взламываемого предмета
    {                         //т.е. чтобы при желании можно было двери тоже взламывать.
        boolean isOpened = false;
        int trueCode = getRandomNumberInRange(100,999);
        int permittedAttempts = 5;      
        
        System.out.println("You try to pick the lock with your hacking tool");
        System.out.println();
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
                System.out.println("You hear a sound of the lock getting stick and realize that");
                System.out.println("after sevelar attempts of hacking it gets broken. You decide to");
                System.out.println("leave it be for now. Your hacking tool is not perfect after all!");
            }
        return isOpened;
    }
    /** 
     * the method resposible for the logical hacking quiz
     */
    public boolean hack(int guessCode, int trueCode)
    {   
        boolean isHacked = false;
        int bulls=0;                //to be changed to dots and dashes later
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
        
        bullsCows.put("bulls",bulls);
        bullsCows.put("cows",cows);
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
        System.out.printf("%nyour guess: %d%nbulls: %d%ncows: %d%n",guessCode,bulls,cows);
        
    }
    
    private static int getRandomNumberInRange(int min, int max) 
    {
        //min = 100;
        //max = 999;
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
}
