import java.util.*;

public class MineSweeper {
    private int[][] minefield;
    /*numbers in the minefield arrays:
    -1 --> mine
    0 --> no adjacent mine
    other --> number of adjacent mines*/
    private boolean[][] hidden;
    private boolean[][] flagged;
    private int noOfMines;

    /*initializes a random minefield as 2d array and a second 2d array to keep track of the hidden fields*/
    private void generateMinefield(int height, int width, int mines){
        if (mines > height * width -1){
            System.out.println("too many or too few many mines");
        } else {
            this.noOfMines = mines;
            //initialize arrays
            this.minefield = new int[height][width];
            this.hidden = new boolean [height][width];
            this.flagged = new boolean [height][width];
            int [][] mineCoordinates = generateMineCoordinates(height,width,mines);

            // set fields with mines to -1
            for (int[] coordinate : mineCoordinates) {
                minefield[coordinate[0]][coordinate[1]] = -1;
            }

            //set the non-mine fields to the number of adjacent mines
            this.setHints(mineCoordinates);

            // set all fields to hidden
            for (int y = 0; y < this.hidden.length; y++){
                Arrays.fill(this.hidden[y], true);
            }
            // set all fields to not flagged
            for (int y = 0; y < this.flagged.length; y++){
                Arrays.fill(this.flagged[y], false);
            }
        }
    }

    /*add +1 to fields surrounding mine, if they are not mines*/
    private void setHints(int[][] mines) {
        for (int[] coordinate: mines){

            if(coordinate[0] != 0 && coordinate[1] != 0){
                if (this.minefield[coordinate[0] - 1][coordinate[1] - 1] != -1) {
                    this.minefield[coordinate[0] - 1][coordinate[1] - 1] += 1;
                }
            }
            //one step up, one step left

            if(coordinate[0] != 0) {
                if(this.minefield[coordinate[0] - 1][coordinate[1]] != -1) {
                    this.minefield[coordinate[0] - 1][coordinate[1]] += 1;
                }
            }
            //one step up

            if(coordinate[0] != 0 && coordinate[1] != this.minefield[0].length - 1) {
                if(this.minefield[coordinate[0] - 1][coordinate[1] + 1] != -1) {
                    this.minefield[coordinate[0] - 1][coordinate[1] + 1] += 1;
                }
            }
            //one step up, one step right

            if (coordinate[1] != 0) {
                if (this.minefield[coordinate[0]][coordinate[1] - 1] != -1) {
                    this.minefield[coordinate[0]][coordinate[1] - 1] += 1;
                }
            }
            //one step left

            if (coordinate[1] != this.minefield[0].length - 1) {
                if(this.minefield[coordinate[0]][coordinate[1] + 1] != -1) {
                    this.minefield[coordinate[0]][coordinate[1] + 1] += 1;
                }
            }
            //one step right

            if (coordinate[0] != this.minefield.length-1 && coordinate[1] != 0) {
                if(this.minefield[coordinate[0] + 1][coordinate[1] - 1] != -1) {
                    this.minefield[coordinate[0] + 1][coordinate[1] - 1] += 1;
                }
            }
            //one step down, one step left

            if (coordinate[0] != this.minefield.length-1) {
                if(this.minefield[coordinate[0] + 1][coordinate[1]] != -1) {
                    this.minefield[coordinate[0] + 1][coordinate[1]] +=1;
                }
            }
            //one step down

            if (coordinate[0] != this.minefield.length-1 && coordinate[1] != this.minefield[0].length-1) {
                if (this.minefield[coordinate[0] + 1][coordinate[1] + 1] != -1) {
                    this.minefield[coordinate[0] + 1][coordinate[1] + 1] += 1;
                }
            }
            //one step down, one step right

        }
    }

    /*generates a set of random coordinates for the mines*/
    private int[][] generateMineCoordinates(int height, int width, int mines){
        Random rand = new Random();
        int num = 1;
        int[][] coordinates = new int[mines][2];
        while (num <= mines){
            int y = rand.nextInt(height);
            int x = rand.nextInt(width);
            int[] coordinate = {y, x};

            //prevents doubles
            if (!this.coordinatesAlreadyExisting(coordinates, y, x)){
                coordinates[num-1] = coordinate;
                num++;
            }
        }
        return coordinates;
    }

    //helper to check if coordinate is already part of array of coordinates, helps prevent doubles
    private boolean coordinatesAlreadyExisting (int[][] coordinates, int y, int x){
        for (int[] coordinate : coordinates){
            if (coordinate[0] == y && coordinate[1] == x){
                return true;
            }
        }
        return false;
    }

    private void drawMinefield(){
        String printLine = "";
        String header = "";

        // fill header
        int i = 0;
        while (i < this.minefield[0].length ){

            if (i < 10){                        //when coordinates reach 2 digits, space has to be omitted
                header = header + " # " + i;
            } else {
                header = header + " #" + i;
            }
            i++;
        }
        header = "   " + header + " # ";

        //actually print minefield line by line
        System.out.println(header);
        System.out.println(" # " + " + –".repeat(minefield[0].length)+" +");
        int j = 0;
        for (int y = 0; y < this.minefield.length; y++){
            for (int x = 0; x < this.minefield[0].length; x++){
                if (flagged[y][x]) {
                    printLine = printLine + " | " + "?";
                } else if (hidden[y][x]){
                    printLine = printLine + " | " + " ";
                } else{
                    printLine = printLine + " | " + this.minefield[y][x];
                }
            }

            //when coordinates reach 2 digits, space has to be omitted
            if (j < 10){
                printLine = " " + j + " " + printLine +" | ";
            } else {
                printLine =  j + " " + printLine +" | ";
            }
            j++;
            printLine = printLine.replace("-1", "*");   //display mines as *
            System.out.println(printLine);
            printLine = "";
            System.out.println(" # " + " + –".repeat(minefield[0].length)+" +"); //footer
        }
    }

    public boolean check (int y, int x) {
        //"clicks" one minefield
        //returns true, if you hit a mine
        this.hidden[y][x] = false;
        this.checkNeighbors(y,x);

        if(this.minefield[y][x] == -1){
            return true;
        }
        return false;
    }

    public void checkNeighbors(int y, int x){
        //recursively reveals neighboring fields and fields adjacent to neighboring 0s

         if (this.minefield[y][x] == 0) {       //base case 1: fields is not zero
             if (y != 0 && x != 0) {
                 if ((this.minefield[y - 1][x - 1] != -1) && this.hidden[y - 1][x - 1]){
                     //base cases 2 and 3: field is mine,  field is not hidden
                     this.hidden[y - 1][x - 1] = false;  //reveal field
                     checkNeighbors(y - 1, x - 1); //recursion case
                 }
             }
             ////one step up, one step left

             if (y != 0) {
                 if (this.minefield[y - 1][x] != -1 && (this.hidden[y - 1][x])){
                     this.hidden[y - 1][x] = false;
                     checkNeighbors(y - 1, x);
                 }
             }
             //one step left

             if (y != 0 && x != this.minefield[0].length - 1) {
                 if (this.minefield[y - 1][x + 1] != -1 && (this.hidden[y - 1][x + 1])){
                     this.hidden[y - 1][x + 1] = false;
                     checkNeighbors(y - 1, x + 1);
                 }
             }
             //one step up, one step right

             if (x != 0) {
                 if (this.minefield[y][x - 1] != -1 && (this.hidden[y][x - 1])){
                     this.hidden[y][x - 1] = false;
                     checkNeighbors(y, x - 1);
                 }
             }
             //one step left

             if (x != this.minefield[0].length - 1) {
                 if (this.minefield[y][x + 1] != -1 && (this.hidden[y][x + 1])){
                     this.hidden[y][x + 1] = false;
                     checkNeighbors(y, x + 1);
                 }
             }
             //one step right

             if (y != this.minefield.length - 1 && x != 0) {
                 if (this.minefield[y + 1][x - 1] != -1 && (this.hidden[y + 1][x - 1])){
                     this.hidden[y + 1][x - 1] = false;
                     checkNeighbors(y + 1, x - 1);
                 }
             }
             //one step down, one step left

             if (y != this.minefield.length - 1) {
                 if (this.minefield[y + 1][x] != -1 && (this.hidden[y + 1][x])){
                     this.hidden[y + 1][x] = false;
                     checkNeighbors(y + 1, x);
                 }
             }
             //one step down

             if (y != this.minefield.length - 1 && x != this.minefield[0].length - 1) {
                 if (this.minefield[y + 1][x + 1] != -1 && (this.hidden[y + 1][x + 1])){
                     this.hidden[y + 1][x + 1] = false;
                     checkNeighbors(y + 1, x + 1);
                 }
             }
             //one step down, one step right
         }
    }

    public boolean winningCondition(){
        //count number of hidden fields
        int hiddenFields = 0;
        for(boolean[] y:hidden){
            for (boolean x:y){
                if (x){
                    hiddenFields++;
                }
            }
        }

        //if only mines remain hidden, you win
        if (hiddenFields == this.noOfMines){
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        MineSweeper ms = new MineSweeper();
        Scanner sc = new Scanner(System.in);
        int y;
        int x;

        //select difficulty
        while (true) {
            System.out.println("Choose difficulty (easy(e)/normal(n)/hard(h):");
            String diff = sc.nextLine();
            diff = diff.toLowerCase();
            if (diff.equals("easy") || diff.equals("e")){
                ms.generateMinefield(8,8,10);
                break;
            } else if (diff.equals("normal") || diff.equals("n")){
                ms.generateMinefield(16,16,40);
                break;
            } else if (diff.equals("hard") || diff.equals("h")){
                ms.generateMinefield(16,30,99);
                break;
            } else {
                System.out.println("invalid selection");
            }
        }

        System.out.println("GAME START");

        while (!ms.winningCondition()) { //game loop: while the winning condition is not fulfilled

            try {
                ms.drawMinefield();

                //ask player if he wants to flag a field
                String flagging;
                System.out.println("Do you want to flag or un-flag a field as possible mine? (y/n)");
                flagging = sc.nextLine();
                flagging = flagging.toLowerCase();

                if (flagging.equals("n")||flagging.equals("no")){
                    //this part allows the player to dig up a section of the minefield
                    //ask player to input coordinates
                    System.out.println("guess row (number):");
                    y = sc.nextInt();
                    System.out.println("guess column (number):");
                    x = sc.nextInt();
                    //add checks for out of bound and field already unhidden!

                    if (ms.flagged[y][x]) {
                        sc.nextLine(); //move scanner to next line to prevent error
                        System.err.println("Cannot check field. It is flagged.");
                        sc.nextLine();
                    } else {
                        boolean checkedField = ms.check(y, x);
                        if (checkedField) {
                            // unhide all fields
                            for (int i = 0; i < ms.hidden.length; i++) {
                                Arrays.fill(ms.hidden[i], false);
                            }
                            ms.drawMinefield();
                            System.out.println("You loose!!! Game over.");
                            break;
                        } else if (ms.winningCondition()) {
                            // unhide all fields
                            for (int i = 0; i < ms.hidden.length; i++) {
                                Arrays.fill(ms.hidden[i], false);
                            }
                            ms.drawMinefield();
                            System.out.println("You win!!!");
                            break;
                        }
                        sc.nextLine();
                    }
                } else if (flagging.equals("y")||flagging.equals("yes")) {
                   //this part allows the player to flag or un-flag a fields as possible mine
                    System.out.println("input row (number) to flag or un-flag:");
                    y = sc.nextInt();
                    System.out.println("input column (number) to flag or un-flag:");
                    x = sc.nextInt();

                    if (ms.hidden[y][x]) {
                        if (ms.flagged[y][x]) {
                            ms.flagged[y][x] = false;
                        } else {
                            ms.flagged[y][x] = true;
                        }
                    } else {
                        System.err.println("Only hidden fields can be flagged.");
                    }
                    sc.nextLine();
                } else {
                    System.err.println("Please select whether to flag or not.");
                }
            } catch (ArrayIndexOutOfBoundsException e){
                System.err.println("coordinate to large or to small");
                sc.nextLine();
            } catch (InputMismatchException e){
                System.err.println("incorrect input");
                sc.nextLine();
            }
        }
    }
}
