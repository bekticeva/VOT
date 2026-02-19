package Vot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Frame {
    private String path;
    private BufferedImage img;
    private int[][] map;
    private int[][] groups;
    long pixelCount = 0;

    final int NOISE_THRESHOLD = 50;
    final int CLOSENESS_THRESHOLD = 150;


    HashMap<Integer, Bobby> bobbys = new HashMap<>();


    public Frame(String path) throws IOException {
        this.path = path;
        this.img = ImageIO.read(new File(path));
    }

    public void compare (Frame prev) {

        map = new int[img.getHeight()][img.getWidth()];

        pixelCount = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int  clr1   = this.img.getRGB(x, y);
                int  red1   = (clr1 & 0x00ff0000) >> 16;
                int  green1 = (clr1 & 0x0000ff00) >> 8;
                int  blue1  =  clr1 & 0x000000ff;

                int  clr2   = prev.img.getRGB(x, y);
                int  red2   = (clr2 & 0x00ff0000) >> 16;
                int  green2 = (clr2 & 0x0000ff00) >> 8;
                int  blue2  =  clr2 & 0x000000ff;

                int d1 = Math.abs(red1 - red2);
                int d2 = Math.abs(green1 - green2);
                int d3 = Math.abs(blue1 - blue2);

                int diff = d1+d2+d3;

                if (diff > 100) {
                    map[y][x] = 1;
                    //Logger.debug(x +","+ y);
                    pixelCount++;
                }
            }
        }

    }

    public void highlight() {
        if(pixelCount == 0){
            return;
        }

        int alpha = 180;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                int id = groups[y][x];

                if (groups[y][x] != 0 && bobbys.containsKey(id) ) {
                    Color c = bobbys.get(id).color;

                    int org =img.getRGB(x, y);
                    Color oc = new Color(org);

                    int r = (oc.getRed() * (225-alpha) + c.getRed() * alpha) / 255 ;
                    int g = (oc.getGreen() * (250-alpha) + c.getGreen() * alpha) / 255 ;
                    int b = (oc.getBlue() * (250-alpha) + c.getBlue() * alpha) / 255 ;

                    Color blend = new Color(r,g,b);

                    img.setRGB(x, y, blend.getRGB());

                }
            }
        }
    }

    public void save() throws IOException {
        ImageIO.write(img, "PNG", new File(path));
    }

    public void save2() throws IOException {
        File original = new File(path);
        String name = original.getName();
        File duplicate = new File("markedFrames/" + name);
        ImageIO.write(img, "PNG", duplicate);
    }


    public void mapPointCleanup () {

        for (int y = 1; y < img.getHeight()-1; y++) {
            for (int x = 1; x < img.getWidth()-1; x++) {

                //goes outta bounds- notz anymore
                if (map[y][x] == 1) {
                    if (map[y][x-1] == 0 && map[y][x+1] == 0 &&
                        map[y-1][x] == 0 && map[y+1][x] == 0) {
                        map[y][x] = 0;
                    }
                }


            }
        }
    }

    public void group(){
        int id = 2;
        groups = new int[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (map[y][x] == 1 && groups[y][x] == 0) {
                    floodfill(y,x,id);
                    id++;
                }
            }
        }
    }

    //geeksforgeeks
    public void floodfill(int y, int x, int id) {
        int[][] dir = { {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{y, x});

        groups[y][x] = id;

        while (!q.isEmpty()) {

            int[] front = q.poll();
            int p = front[0], r = front[1];

            // Traverse all 4 directions
            for (int[] it : dir) {
                int nx = p + it[0];
                int ny = r + it[1];

                // Check boundary conditions and color match
                if (nx >= 0 && nx < map.length && ny >= 0 && ny < map[0].length &&
                        map[nx][ny] == 1 && groups[nx][ny] == 0) {
                    groups[nx][ny] = id;

                    q.add(new int[]{nx, ny});
                }
            }
        }

    }


    public void extractBobby(){
        int id = 2;
        int count = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (groups[y][x] > 1) {
                    id = groups[y][x];

                    Bobby bobby = bobbys.get(id);
                    if (bobby == null) {
                        bobby = new Bobby(id);
                        bobbys.put(id, bobby);
                    }
                    else {
                        bobby.update(y,x);
                    }

                }
            }
        }
    }


   public void bobbyCleanup(){
        //geeksforgeeks cleaning up noise
        Iterator itr = bobbys.entrySet().iterator();

        while (itr.hasNext()) {

            HashMap.Entry el= (HashMap.Entry)itr.next();
            Bobby bobby = (Bobby)el.getValue();
            if (bobby.size < NOISE_THRESHOLD){
                itr.remove();
            }

        }
        //bobbyFiltering();
    }

    public void bobbyCompare(Frame prev){
        for (Bobby b1 : this.bobbys.values()) {
            int x1 = b1.centerX;
            int y1 = b1.centerY;
            for (Bobby b2 : prev.bobbys.values()) {
                int x2 = b2.centerX;
                int y2 = b2.centerY;

                double dx = x1-x2;
                double dy = y1-y2;
                double d = Math.sqrt(dx*dx + dy*dy);

                if (d < CLOSENESS_THRESHOLD) {
                    b1.color = b2.color;
                }
            }
        }

    }


    private static double getDistance(Map.Entry<Integer, Bobby> A, Map.Entry<Integer, Bobby> B) {
        int AxMin = A.getValue().minX;
        int AxMax = A.getValue().maxX;
        int BxMin = B.getValue().minX;
        int BxMax = B.getValue().maxX;
        int AyMin = A.getValue().minY;
        int AyMax = A.getValue().maxY;
        int ByMin = B.getValue().minY;
        int ByMax = B.getValue().maxY;

        int dx = Math.max(0,Math.max( AxMin - BxMax, BxMin - AxMax));
        int dy = Math.max(0,Math.max( AyMin - ByMax, ByMin - AyMax));

        double distance = Math.sqrt(dx*dx + dy*dy);
        return distance;
    }


    public int[][] getMap(){
        return map;
    }
    public int[][] getGroups() {
        return groups;
    }
    public String getPath(){
        return path;
    }

}














/*
    public void mapCleanup () {

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                if (map[y][x] == 1) {
                    int onecnt = 1;
                    for (int f = y-3; f < y+3; y++) {
                        for (int l = x-3; l < x+3; x++) {
                            if (map[f][l] == 1) {
                                onecnt++;
                            }
                        }}
                    if (onecnt == 1) {
                        map[y][x] = 0;
                    }
                }


            }
        }
    }





       I wanted to group regions that are close together

    public void bobbyFiltering(){
        for (HashMap.Entry<Integer,Bobby> A : bobbys.entrySet()) {
            for (HashMap.Entry<Integer,Bobby> B : bobbys.entrySet()) {

                double distance = getDistance(A, B);

                *//*what i did here was very stupid but it does the job
 * i have to write it down so i dont forget
 * if the distance between bobbies is less than a certain treshold it changes
 * the id of the other bobby and also changes the id of pixels under
 * that bobby*//*
                if (distance < 10){
                    B.getValue().setId(A.getKey());

                    for (int y = 0; y < img.getHeight(); y++) {
                        for (int x = 0; x < img.getWidth(); x++) {

                            if (groups[y][x] == B.getKey()) {
                                groups[y][x] = A.getKey() ;
                            }
                        }
                    }
                }
            }
        }
    }
    */