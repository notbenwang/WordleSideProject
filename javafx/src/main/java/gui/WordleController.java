package gui;
import wordle.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.HashMap;

public class WordleController{
    /*
    Not the prettiest thing, but it was very hard to get FXML to do what we wanted, and everytime I tried to
    implement something that would make the code nicer FXML just didn't work, so we had to settle with this
    very large class. We did our best to make this big class as nice looking as possible though.
    If we had more time, we could probably make it a lot nicer with actual modules and such.
    Sorry :(
     */

    @FXML
    private Label errorMessage, playAgainMessage;
    @FXML
    private TextField input;
    @FXML
    private Rectangle background;
    @FXML // Grid Labels
    private Label L01, L02, L03, L04, L05, L06, L07, L08, L09, L10, L11, L12, L13, L14, L15,
            L16, L17, L18, L19, L20, L21, L22, L23, L24, L25, L26, L27, L28, L29, L30;
    @FXML // Grid Boxes
    private Rectangle R01, R02, R03, R04, R05, R06, R07, R08, R09, R10, R11, R12, R13, R14, R15,
            R16, R17, R18, R19, R20, R21, R22, R23, R24, R25, R26, R27, R28, R29, R30;
    @FXML // Alphabet Boxes
    private Rectangle BoxA,BoxB,BoxC,BoxD,BoxE,BoxF,BoxG,BoxH,BoxI,
            BoxJ,BoxK,BoxL,BoxM,BoxN,BoxO,BoxP,BoxQ,BoxR,BoxS,BoxT,
            BoxU, BoxV, BoxW, BoxX, BoxY, BoxZ, BoxENTER, BoxDEL;
    @FXML
    private Button nobutton, yesbutton, giveUpButton; // "End Buttons"

    private final int WORD_LENGTH = 5;

    // Wordle Model Variables
    private Wordle wordle;
    private StringBuilder input_guess;
    private int rowNumber;
    private WordleDictionary legalGuessDictionary;
    HashMap<String, Rectangle> AlphabetMap;

    @FXML
    public void initialize(){
        setUpWordleModel();
        setUpGraphics();
        setUpEndButtons();
        setUpErrorMessage();
        clearPlayAgainMessage();
    }
    @FXML
    private void setUpWordleModel(){
        wordle = new WordleImplementation();
        legalGuessDictionary = new DefaultDictionaryFactory().getDefaultGuessesDictionary();
        input_guess = new StringBuilder();
        rowNumber = 0;
        initializeAlphabetMap();
    }
    @FXML
    public void KeyEventHandler(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)){
            System.exit(1);
        }
        if (wordle.isGameOver()) {
            if (event.getCode().equals(KeyCode.ENTER)){
                initialize();
            } else checkGameState();
        }

        else {
            if (event.getCode().isLetterKey()){
                displayCurrentLetter(String.valueOf(event.getCode()));
            } if (event.getCode().equals(KeyCode.BACK_SPACE)){
                doBackSpace();
            } if (event.getCode().equals(KeyCode.ENTER)){
                submitThisLine();
            }
        }
    }
    private class ActionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (event.getSource().equals(nobutton)) {System.exit(1);}
            if (event.getSource().equals(yesbutton)) {
                initialize();}
            if (event.getSource().equals(giveUpButton)&& !wordle.isGameOver()){
                wordle.setGameStatus(WordleImplementation.GameStatus.LOST);
                checkGameState();
            }
        }
    }

    private void setUpErrorMessage(){
        clearErrorMessage();
        errorMessage.setTextFill(Color.BLACK);
        errorMessage.setStyle("-fx-font-weight: regular");
        errorMessage.setFont(new Font("Arial", 15));
    }
    private void setUpEndButtons(){
        nobutton.setOnAction(new ActionHandler());
        yesbutton.setOnAction(new ActionHandler());
        giveUpButton.setOnAction(new ActionHandler());
        setEndButtonVisibility(false);
    }
    private void setUpGraphics(){
        setUpAllBoxes();
        setUpAllLabels();
    }
    private void setUpAllBoxes(){
        Rectangle[] boxes = {BoxA,BoxB,BoxC,BoxD,BoxE,BoxF,BoxG,BoxH,BoxI, BoxJ,BoxK,BoxL,BoxM,BoxN,BoxO,BoxP,
                BoxQ,BoxR,BoxS,BoxT, BoxU, BoxV, BoxW, BoxX, BoxY, BoxZ, BoxENTER, BoxDEL, R01, R02,
                R03, R04, R05, R06, R07, R08, R09, R10, R11, R12, R13, R14, R15, R16, R17, R18, R19,
                R20, R21, R22, R23, R24, R25, R26, R27, R28, R29, R30};
        for (Rectangle box : boxes){
            box.setFill(Color.WHITE);
            box.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 0;");
            box.setOpacity(0.85);
        }
    }
    private void setUpAllLabels(){
        Label[] labels = {L01, L02, L03, L04, L05, L06, L07, L08, L09, L10, L11, L12, L13, L14, L15,
                L16, L17, L18, L19, L20, L21, L22, L23, L24, L25, L26, L27, L28, L29, L30};
        for (Label label : labels){
            label.setText("");
            label.setTextFill(Color.rgb(60,60,60));
        }
    }
    protected void initializeAlphabetMap(){
        // Hard Coded Alphabet bc fxml and scope is weird
        Rectangle[] alphabetBoxes = {BoxA,BoxB,BoxC,BoxD,BoxE,BoxF,BoxG,BoxH,BoxI,
                BoxJ,BoxK,BoxL,BoxM,BoxN,BoxO,BoxP,BoxQ,BoxR,BoxS,BoxT,
                BoxU, BoxV, BoxW, BoxX, BoxY, BoxZ, BoxENTER, BoxDEL};
        AlphabetMap = new Alphabet(alphabetBoxes).getMap();
    }


    private void doBackSpace(){
        if (input_guess.length()>0){
            clearCurrentLetter(input_guess);
            input_guess.deleteCharAt(input_guess.length()-1);
        }
    }
    protected void submitThisLine(){
        String guess = input_guess.toString();
        if (wordle.isGameOver()) checkGameState();
        else if (checkGuessIsValid(guess)){
            try{
                updateRow(guess);
            } catch (IllegalWordException e) {
                printIllegalWordMessage();
            } catch (GameAlreadyOverException e){
                checkGameState();
            }
        } else printIllegalWordMessage();
        input.clear();
    }
    protected boolean checkGuessIsValid(String guess){
        return (legalGuessDictionary.containsWord(guess) || guess.length() != WORD_LENGTH);
    }
    private void displayCurrentLetter(String s){
        if (input_guess.length()<WORD_LENGTH) {
            input_guess.append(s);
            displayCurrentLetter(input_guess);
        }
    }
    protected void displayCurrentLetter(StringBuilder s){
        Label[] RowLetters = {L01, L02, L03, L04, L05, L06, L07, L08, L09, L10, L11, L12, L13, L14, L15,
                L16, L17, L18, L19, L20, L21, L22, L23, L24, L25, L26, L27, L28, L29, L30};

        int index = s.length()-1;
        int rowIndex = (rowNumber*5) + index;
        char letter = s.charAt(index);

        Label label = RowLetters[rowIndex];
        label.setText(String.valueOf(letter));

        label.setStyle("-fx-font-weight: bold");
    }
    protected void clearCurrentLetter(StringBuilder s){
        Label[] RowLetters = {L01, L02, L03, L04, L05, L06, L07, L08, L09, L10, L11, L12, L13, L14, L15,
                L16, L17, L18, L19, L20, L21, L22, L23, L24, L25, L26, L27, L28, L29, L30};

        int index = (rowNumber*5) + s.length()-1;
        Label label = RowLetters[index];
        label.setText("");
    }
    protected void updateRow(String guess){
        Label[] RowLetters = {L01, L02, L03, L04, L05, L06, L07, L08, L09, L10, L11, L12, L13, L14, L15,
                L16, L17, L18, L19, L20, L21, L22, L23, L24, L25, L26, L27, L28, L29, L30};
        Rectangle[] RowColors = {R01, R02, R03, R04, R05, R06, R07, R08, R09, R10, R11, R12, R13, R14, R15,
                R16, R17, R18, R19, R20, R21, R22, R23, R24, R25, R26, R27, R28, R29, R30};
        LetterResult[] result = wordle.submitGuess(guess);

        // For each five letters
        int wordIndex=0;

        for (int rowIndex= rowNumber*WORD_LENGTH; rowIndex <WORD_LENGTH+(rowNumber*WORD_LENGTH);rowIndex++){

            // Variable instantiating
            Label label = RowLetters[rowIndex];
            LetterResult color = result[wordIndex];
            Rectangle box = RowColors[rowIndex];
            String letter = String.valueOf(guess.charAt(wordIndex));
            wordIndex++;
            // UPDATES---------------------
            label.setText(letter); // Update label
            label.setTextFill(javafx.scene.paint.Color.WHITE);
            label.setStyle("-fx-font-weight: bold");
            setRectColor(box, color);  // Update Rectangle
            updateAlphabet(letter, color); // Update Alphabet
        }
        // Clean Up
        rowNumber++;
        input_guess = new StringBuilder();
        clearErrorMessage();
        checkGameState();

    }

    protected void updateAlphabet(String letter, LetterResult color){
        Rectangle box = AlphabetMap.get(letter.toUpperCase());
        setRectColor(box, color);
    }
    public void setRectColor(Rectangle box, LetterResult color){
        if (color.equals(LetterResult.GREEN)) box.setFill(Color.rgb(94,200,102));
        if (color.equals(LetterResult.YELLOW)) box.setFill(javafx.scene.paint.Color.GOLD);
        if (color.equals(LetterResult.GRAY)) box.setFill(javafx.scene.paint.Color.GRAY);
    }
    protected void checkGameState(){
        if (wordle.isGameOver()) {
            setEndButtonVisibility(true);
            printPlayAgainPrompt();
            if (wordle.isWin()) printYouWonMessage();
            else printYouLostMessage();
        }
    }
    private void setEndButtonVisibility(boolean b){
        if (b) {
            nobutton.setOpacity(1);
            yesbutton.setOpacity(1);
        } else {
            nobutton.setOpacity(0);
            yesbutton.setOpacity(0);
        }
    }
    protected void printIllegalWordMessage(){errorMessage.setText("Error, not a valid word.");}
    protected void printYouLostMessage(){
        errorMessage.setText("You Lost! The correct answer was: "+wordle.getAnswer());
        errorMessage.setStyle("-fx-font-weight: bold");
        errorMessage.setTextFill(Color.rgb(224,70,70));

    }
    protected void printYouWonMessage(){
        errorMessage.setText("You Won!");
        errorMessage.setStyle("-fx-font-weight: bold");
        errorMessage.setFont(new Font("Arial", 25));
        errorMessage.setTextFill(Color.rgb(94,200,102));
    }
    private void printPlayAgainPrompt(){
        playAgainMessage.setText("Do you want to play again?");
        playAgainMessage.setTextFill(Color.rgb(198, 198, 198));
    }
    protected void clearErrorMessage() {errorMessage.setText("");}
    private void clearPlayAgainMessage() {playAgainMessage.setText("");}
}