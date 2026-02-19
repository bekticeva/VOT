package Vot;

import util.Logger;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ExecuteFFmpeg ffmpeg = new ExecuteFFmpeg();
        //random videos
        String path1 = "C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\videos\\Horizontally Spinning Pigeon.mp4";
        String path2 = "C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\videos\\6.mp4";
        String path3 = "C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\videos\\5.mp4";
        String path4 = "C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\videos\\2.mp4";
        String path5 = "C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\videos\\rari.mp4";
        String path6 = "videos/drift3.mp4";

        String pathFrames = "frames";
        String pathVideos = "videos";

        //===================================================
        long start1 = System.nanoTime();
        ffmpeg.split(path6);  //video to process (change path to try other videos)
        long end1 = System.nanoTime();
        double elapsed1 = (end1 - start1) / 1_000_000_000.0;
        //===================================================

        Frame temp1 = new Frame("frames/0001.png");
        Frame temp2 = new Frame("frames/0001.png");

        File dir = new File("frames");
        File[] imageList = dir.listFiles();

        long start2 = System.nanoTime();
        for (int i = 1; i < imageList.length; i++) {
            Frame imgFrame = new Frame(imageList[i].getAbsolutePath());
            imgFrame.compare(temp1);
            imgFrame.mapPointCleanup();
            imgFrame.group();
            imgFrame.extractBobby();
            imgFrame.bobbyCleanup();
            imgFrame.bobbyCompare(temp2);
            temp1 = new Frame(imageList[i].getAbsolutePath());

            imgFrame.highlight();
            imgFrame.save();
            temp2 = imgFrame;

        }
        long end2 = System.nanoTime();
        double elapsed2 = (end2 - start2) / 1_000_000_000.0;

        Logger.info("comparison finished");
        Logger.info("update finished");

        //===================================================
        long start3 = System.nanoTime();
        ffmpeg.connect(pathFrames,pathVideos);
        long end3 = System.nanoTime();
        double elapsed3 = (end3 - start3) / 1_000_000_000.0;
        //===================================================

        Logger.info("video assembly finished");
        Logger.info("cleaning up...");


        for (File img : imageList) {
            img.delete();
        }

        //Logger.info("done, Master.");


        Logger.info("===================================================================================");
        Logger.info("Time for splitting: " + elapsed1 + " seconds");
        Logger.info("Time for processing: " + elapsed2 + " seconds");
        Logger.info("Time for combining: " + elapsed3 + " seconds");
        Logger.info("===================================================================================");
    }
}







/*
        //Frame img2 = new Frame("C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\frames\\0003.png");
        //img2.compare(img1);
        //img2.setAlpha();


        File dir2 = new File("C:\\Users\\User\\Documents\\FAMNIT\\Y2\\PROG 3\\VOT\\markedFrames");
        File[] imageList2 = dir2.listFiles();

            //Logger.debug(imageList[i].getAbsolutePath());
            //System.out.println(imgFrame.bobbys);

           if (i == 31){
                System.out.println(Arrays.deepToString(imgFrame.getGroups()));
            }

        for (File img : imageList2) {
            img.delete();
        }
*/
