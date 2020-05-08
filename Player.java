import java.util.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Player implements java.io.Serializable
{
    private Parser parser;
    private Room currentRoom;
    private Room previousRoom; // 1
    private ArrayList<Item> inventory;
    private ArrayList<Item> previousInventory; // 2
    private final int maxCapacity = 430;
    private int score = 0;
    private int previousScore = 0; // 3
    private int weight = 0;
    private int previousWeight = 0; // 4
    private boolean isGameCompleted = false;
    
    /**
     * Constructor for objects of class Player
     */
    public Player(int maxCapacity, Room currentRoom)
    {
        inventory = new ArrayList<>();
        previousInventory = null;
        this.currentRoom = currentRoom;
        this.previousRoom = null;
        parser = new Parser();
    }

    // implementations of user commands:
    public boolean play()
    {
        boolean finished = false;
        currentRoom.printDescription();
        currentRoom.printShortDescription();
        // main player's loop
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        return isGameCompleted;
    }
    
    private void writeState(String inventory, int score, int weight)
    {
      try {
          saveToFile(inventory, score, weight);
      } catch (IOException ioException) {
          System.err.println("Error writing file.");
          ioException.printStackTrace();
      }  
    }
    
    private void saveToFile(String inventory, int score, int weight) throws IOException
    {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("temp.tmp"));
        List<Object> tempState = new ArrayList<Object>();

        tempState.add(inventory);
        tempState.add(score);
        tempState.add(weight);
        
        os.writeObject(tempState);
        readSavedObject();
        os.close();
    }
    
    private Object readSavedObject()
    {
        try (
            ObjectInputStream objectInput
                = new ObjectInputStream(new FileInputStream("temp.tmp"));
        ){
     
        while (true) {
            System.out.print(objectInput.readObject() + "\t");
            return objectInput.readObject();
        }
       
        } catch (EOFException eof) {
            System.out.println("Reached end of file");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private void back()
    {
        Object tempPlayerState = new Object();
        currentRoom = previousRoom;
        tempPlayerState = readSavedObject();
        System.out.print("hello " + tempPlayerState);
    }
    
    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the Game, false otherwise.
    */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;
        
        CommandWord commandWord = command.getCommandWord();
        
        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                return wantToQuit;
                
            case BACK:
                back();
                
            case HELP:
                printHelp();
                break;
                
            case GO:
                goToRoom(command);
                break;
                
            case TAKE:
                pickUp(command);
                break;
                
            case DROP:
                drop(command);
                break;
                
            case INSPECT:
                inspect(command);
                break;
                
            case LEAVE:
                return wantToQuit = leave(command);
                
            case QUIT:
                return wantToQuit = quit(command);
        }
        
        // else command not recognised.
        return wantToQuit;
    }
    
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
     * Try to go in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goToRoom(Command command) 
    {
        String direction = command.getSecondWord();
        Room nextRoom = null;
     
        if (command.hasSecondWord()) {
            if (direction.equals("back")){
                nextRoom = previousRoom;
            } else {    // Try to leave current room.
                nextRoom = currentRoom.getExit(direction);
            }
            if (nextRoom == null){
                System.out.println("There is no door!");
            } else if ((nextRoom.getName().contains("basement"))&&(!hasBasementKey())) {
                System.out.println("You don't have the key!"); 
            } else if (nextRoom.getName().contains("bedroom")) {
                nextRoom.printShortDescription(); 
            } else {
                previousRoom = currentRoom;
                currentRoom = nextRoom;
                currentRoom.printDescription(); //printing 1st part of description
                currentRoom.printShortDescription(); //printing 2nd part of description
            }
        } else {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }
    }

    private void inspect(Command command) 
    {
        if(command.hasSecondWord()) {
            String itemName = command.getSecondWord();
            boolean searching = true;
            for (Item item : currentRoom.getItems()) {
                if (item.getName().contains(itemName)) {
                    item.printDescription();
                    searching = false;
                }
            }
            if(searching) {   
                System.out.printf("%nYou don't see it anywhere in the room.%n");
            }
        } else {
            currentRoom.printShortDescription();  //only print second part of description
        }
    }

    /**
     * pick Up an item
     */
    private void pickUp(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Take what?");
            return;
        };
        
        String itemName = command.getSecondWord();
        Item extractedItem;
    
        if((extractedItem = currentRoom.getItem(itemName)) == null) {
            System.out.println("You don't see it anywhere in the room");
            return;
        } else {
            //if the casket is found
            if(extractedItem.getIsCasket()) { 
                if(pickLock()){   
                    currentRoom.removeItem(extractedItem);
                    extractedItem = extractedItem.getContainedItem();    //otherwise we get endless amount of caskets
                    currentRoom.setItem(extractedItem);
                    System.out.printf("There is a " + extractedItem.getName() +" inside. %n");
                    extractedItem.printDescription();
                    System.out.println();
                }
            }   
            if(weight + extractedItem.getWeight() <= maxCapacity) {
                inventory.add(extractedItem);
                currentRoom.removeItem(extractedItem);
                score += extractedItem.getScore();
                weight += extractedItem.getWeight();
                writeState(extractedItem.getName(), score, weight);
                System.out.println("you put the " + extractedItem.getName() + " inside your bag");
                System.out.println();
                System.out.printf("your inventory now: %d/%d%n%n", weight,maxCapacity);
                printInventory();
            } else if(extractedItem.getWeight()>500) {
                System.out.println("Did you really think you could somehow fit that in your bag?");
            } else {
                System.out.println("Your inventory is full! Looks like you'll have to give some of that loot up");
            }
        }
    }

    private void drop(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Drop what?");
            return;
        };
        
        String itemName = command.getSecondWord();
        
        for (Item item : inventory) {
            if (item.getName().contains(itemName)) {
                currentRoom.setItem(item);
                inventory.remove(item);
                score -= item.getScore();
                weight -= item.getWeight();
                writeState(item.getName(), score, weight);
                System.out.println("You drop the " + item.getName() + " on the floor");
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
        HackingTool hackingTool = new HackingTool();
        System.out.printf("%nYou try to pick the lock with your hacking tool%n");
        boolean isOpened = hackingTool.hack();
        
        if(!isOpened) {
            System.out.println("You hear the sound of the lock getting stuck and realize that");
            System.out.println("after several attempts  it gets broken. You decide to leave it be");
            System.out.println("for now. Your hacking tool is not perfect after all!");
        }
        return isOpened;
    }

    public void printInventory()
    {
        for (Item item : inventory) {   
            System.out.println(item.getName());
        }
        System.out.println();
    }

    private boolean hasBasementKey()
    {
        for (Item item : inventory) {  
            if(item.getName().contains("key")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasWinningCondition()
    {   
        int numberOfCaskets = 0;
            
        for (Item item : inventory) {  
            if(item.getName().contains("charm")) {
                    return true;
            }
            if(item.getIsCasket()) {
                   numberOfCaskets += 1;
            }
        }
            
        return numberOfCaskets == 3 ? true : false;
    }
    
    /** 
     * "quit" was entered. Check the rest of the command to see
     * whether we really quit the Game.
     * @return true, if this command quits (exit) the Game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to exit
        }
    }
    
    /** 
    * finish the game and print the ending according to your score
    */
    private boolean leave(Command command)  //leave as in leave the house and finish the game
    {   
        if(command.hasSecondWord()) {
            System.out.println("Leave what?");
            return false;
        };
        System.out.printf("%nYou decide it's time to leave this wicked house%n%n");
        isGameCompleted = true;
        return true;
    }
    
    public int getScore()
    {
        return score;
    }
}