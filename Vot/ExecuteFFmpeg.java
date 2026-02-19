package Vot;

import util.Logger;

import java.io.*;

public class ExecuteFFmpeg {

    public ExecuteFFmpeg() {
    }

    public void split(String path) {
        Logger.debug("meow");
        ProcessBuilder pb = new ProcessBuilder("ffmpeg","-i", path,"frames\\%04d.png");
        Logger.debug("meow");
        pb.directory(new File("."));
        Logger.debug("meow");
        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;

            while ((line = error.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


            int exitCode = p.waitFor();
            Logger.info("Ffmpeg exited" + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String pathIntput,String pathOutput) {
        ProcessBuilder pb = new ProcessBuilder("ffmpeg","-y","-framerate","30","-i", pathIntput+"/%04d.png", "-c:v", "libx264", "-pix_fmt", "yuv420p",pathOutput+"/output.mp4");
        pb.directory(new File("."));
        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;

            while ((line = error.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


            int exitCode = p.waitFor();
            Logger.info("Ffmpeg exited" + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
