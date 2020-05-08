import java.util.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Player implements java.io.Serializable
{
    public Room currentRoom;
    public ArrayList<Item> inventory;
    public int score;
    public int weight;
    
    public Room previousRoom; //1
    private ArrayList<Item> previousInventory; // 2
    private int previousScore = 0; // 3
    private int previousWeight = 0; // 4
    
    private final int maxCapacity = 430;
    
    /**
     * Constructor for objects of class Player
     */
    public Player(Room currentRoom)
    {
        this.currentRoom = currentRoom;
        this.inventory = new ArrayList<>();
        this.score = 0;
        this.weight = 0;
        
        this.previousRoom = currentRoom;
        this.previousInventory = inventory;
        this.previousScore = score;
        this.previousWeight = weight;
    }

    public void back(int step, PlayerState state)
    {
        if(step >= 1) {
            Player playerPrevState = state.getPlayerState(step);
            
            currentRoom = playerPrevState.previousRoom;
            previousRoom = playerPrevState.previousRoom;
            inventory = playerPrevState.previousInventory;
            previousInventory = playerPrevState.previousInventory;
            score = playerPrevState.previousScore;
            weight = playerPrevState.previousScore;
            previousScore = playerPrevState.previousScore;
            previousScore = playerPrevState.previousScore;

            System.out.println(score);
            System.out.println("Now you're in: " + currentRoom.getName());
            System.out.printf("your inventory now: %d/%d%n%n", weight, maxCapacity);
            printInventory();
        } else {
            System.out.println("There is no way back..");
        }

        // drop previous inventory item
        // reduce score
        // go to previous room
        // reduce weight
    }
    
    /** 
     * Try to go in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    public void goToRoom(Command command) 
    {
        String direction = command.getSecondWord();
        Room nextRoom = null;
        
        currentRoom.printDescription();
        currentRoom.printShortDescription();
     
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

    /**
     * pick Up an item
     */
    public void pickUp(Command command)
    {
        ArrayList<Item> prevInventory = inventory;
        
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
            if (extractedItem.getIsCasket()) { 
                if(pickLock()){   
                    currentRoom.removeItem(extractedItem);
                    extractedItem = extractedItem.getContainedItem();    //otherwise we get endless amount of caskets
                    currentRoom.setItem(extractedItem);
                    System.out.printf("There is a " + extractedItem.getName() +" inside. %n");
                    extractedItem.printDescription();
                    System.out.println();
                }
            }   
            if (weight + extractedItem.getWeight() <= maxCapacity) {
                inventory.add(extractedItem);
                previousInventory = prevInventory;
                
                currentRoom.removeItem(extractedItem);
                
                previousScore = score;
                previousWeight = weight;
                
                score += extractedItem.getScore();
                weight += extractedItem.getWeight();
                
                System.out.println("you put the " + extractedItem.getName() + " inside your bag");
                System.out.println();
                System.out.printf("your inventory now: %d/%d%n%n", weight,maxCapacity);
                printInventory();
            } else if (extractedItem.getWeight() > 500) {
                System.out.println("Did you really think you could somehow fit that in your bag?");
            } else {
                System.out.println("Your inventory is full! Looks like you'll have to give some of that loot up");
            }
        }
    }

    public void drop(Command command)
    {
        ArrayList<Item> prevInventory = inventory;
        
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
                previousInventory = prevInventory;
                
                previousScore = score;
                previousWeight = weight;
                
                score -= item.getScore();
                weight -= item.getWeight();
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
    
    public void inspect(Command command) 
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
    public boolean quit(Command command) 
    {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to exit
        }
    }
    
    public int getScore()
    {
        return score;
    }
}