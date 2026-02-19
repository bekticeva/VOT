package Vot;

import java.awt.*;
import java.util.Random;

public class Bobby {
    int id;
    int size;
    Color color;
    int minX, minY, maxX, maxY;
    int centerX, centerY;

    public Bobby(int id) {
        this.id = id;
        this.size = 1;
        this.color = randomColor();
    }

    static Color randomColor() {
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        Color randomColor = new Color(r, g, b);
        return randomColor;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }
    public void setMinX(int minX) {
        this.minX = minX;
    }
    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void update(int y,int x){
        if(y < minY){
            this.minY = y;
        }
        if(y > maxY){
            this.maxY = y;
        }
        if(x < minX){
            this.minX = x;
        }
        if(x > maxX){
            this.maxX = x;
        }
        //every time its called it has read one pix
        size++;
        center();
    }

    public void center(){
        centerX = (maxX + minX) / 2;
        centerY = (maxY + minY) / 2;
    }
}
