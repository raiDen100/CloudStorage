package com.raiden.cloudstorage.utils;

import com.raiden.cloudstorage.entities.StoredFile;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

@Component
public class VideoUtil {

    public BufferedImage getMiddleFrame(File file){
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
        Frame frame;
        try{
            frameGrabber.start();
            int middleFrameIndex =  frameGrabber.getLengthInFrames() / 2;

            frameGrabber.setFrameNumber(middleFrameIndex);
            frame = frameGrabber.grabImage();

            BufferedImage bufferedImage = FrameToBufferedImage(frame);

            frameGrabber.stop();
            frameGrabber.close();

            return bufferedImage;

        }catch (FFmpegFrameGrabber.Exception e){

            e.printStackTrace();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private BufferedImage FrameToBufferedImage(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }
}
