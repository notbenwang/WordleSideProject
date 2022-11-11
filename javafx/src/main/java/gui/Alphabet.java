package gui;

import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

public class Alphabet {

    private HashMap<String, Rectangle> map;
    private Rectangle[] boxes;

    public Alphabet(Rectangle[] boxes){
        map = new HashMap<String, Rectangle>();
        this.boxes = boxes;
        initializeMap(boxes);
    }

    public HashMap<String, Rectangle> getMap(){
        return this.map;
    }
    @FXML
    private void initializeMap(Rectangle[] boxes){
        int i =0;
        for (char ch = 'A'; ch <= 'Z'; ch++){
            map.put(String.valueOf(ch), boxes[i++]);
        }
        map.put("ENTER", boxes[i++]);
        map.put("DELETE", boxes[i]);
    }


}
