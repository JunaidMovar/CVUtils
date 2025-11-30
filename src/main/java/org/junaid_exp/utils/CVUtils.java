package org.junaid_exp.utils;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_face.FacemarkLBF;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgproc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class CVUtils {

    VideoCapture vc;
    JFrame jFrame;
    JLabel jLabel;
    Mat output,gray;
    CascadeClassifier cascadeClassifier;
    FacemarkLBF facemarkLBF;
    RectVector rectVector;
    Point2fVectorVector faceVector;
    Point2fVector points;
    Point2f point2f;

    public BufferedImage matToBufferImage(Mat mat) {
        BufferedImage br ;
        try {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if(mat.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            int bufferSize = mat.channels() * mat.cols() * mat.rows();
            br = new BufferedImage(mat.cols(),mat.rows(),type);
            byte[] b = ((DataBufferByte) br.getRaster().getDataBuffer()).getData();
            mat.data().get(b);
            br.getRaster().setDataElements(mat.cols()-1,mat.rows()-1,b);
        } catch (Exception e) {
            br = new BufferedImage(0,0,0);
        }
        return br;
    }
    private Mat resizeFrame(Mat input, int width, int height) {
        output = new Mat();
        resize(input,output,new Size(width,height));
        return output;
    }
    private void loadFrame() {
        jFrame = new JFrame("");
        jLabel = new JLabel();
        jFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(Boolean.TRUE);
    }
    private void printImage(Mat mat) {
        BufferedImage bufferedImage;
        bufferedImage = matToBufferImage(mat);
        jLabel = new JLabel(new ImageIcon(bufferedImage));
    }
    private void printVideo(Mat mat) {
        BufferedImage bufferedImage;
        bufferedImage = matToBufferImage(mat);
        jLabel.setIcon(new ImageIcon(bufferedImage));
        jFrame.pack();
        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    public void playVideo(Integer fileName) {
            playVideo(fileName,800,600);
    }
    public void playVideo(Integer fileName,Boolean landmarks) {
        if(landmarks) {
            playVideoWithLandmarks(fileName,800,600);
        } else {
            playVideo(fileName,800,600);
        }
    }
    public void playVideo(String fileName) {
        playVideo(fileName,800,600);
    }
    public void playVideo(Integer fileName,int width,int height) {

        Loader.load(opencv_core.class);
        vc = new VideoCapture(fileName);
        if (!vc.isOpened()) {
            System.out.println("Failed to Open the Video!");
            return;
        }
        loadFrame();
        Mat frame = new Mat();
        while(vc.read(frame)) {
            System.out.println(frame.toString());
            if(frame.empty()) break;
            printVideo(resizeFrame(frame,width,height));
        }

    }
    public static String getFileNameWithoutExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        } else {
            return fileName;
        }
    }
    public static String getFileExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0) {
            return fileName.substring(dotIndex,fileName.length()-1);
        } else {
            return fileName;
        }
    }
    private String loadResources(String absolutePath) {
        File temp;
        try (InputStream is = getClass().getResourceAsStream(absolutePath)) {
            temp = File.createTempFile(getFileNameWithoutExtension(absolutePath),getFileExtension(absolutePath));
            Files.copy(is,temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return temp.getAbsolutePath();
    }
    public Boolean detectFaces(Mat frame,CascadeClassifier classifier) {
        gray = new Mat();
        cvtColor(frame,gray,COLOR_BGR2GRAY);
        rectVector = new RectVector();
        classifier.detectMultiScale(gray,rectVector);
        faceVector = new Point2fVectorVector();
        return facemarkLBF.fit(gray,rectVector,faceVector);
    }
    public void playVideoWithLandmarks(Integer fileName,int width,int height) {

        cascadeClassifier = new CascadeClassifier(loadResources("/classifiers/haarcascade_frontalface_alt.xml"));
        facemarkLBF = FacemarkLBF.create();
        facemarkLBF.loadModel(loadResources("/models/lbfmodel.yaml"));

        Loader.load(opencv_core.class);
        vc = new VideoCapture(fileName);
        if (!vc.isOpened()) {
            System.out.println("Failed to Open the Video!");
            return;
        }
        loadFrame();
        Mat frame = new Mat();
        while(vc.read(frame)) {
            System.out.println(frame.toString());
            if(frame.empty()) break;
            if(detectFaces(frame,cascadeClassifier)) {
                for(long i = 0; i<faceVector.size();i++) {
                    points = faceVector.get(i);
                    for(long j = 0; j < points.size() ; j++) {
                        point2f = points.get(j);
                        circle(frame,new Point((int) point2f.x(),(int) point2f.y()), 2,
                                Scalar.RED,0,0,0);
                    }
                }
            }
            printVideo(resizeFrame(frame,width,height));
        }

    }
    public void playVideoWithLandmarks(String fileName,int width,int height) {

        cascadeClassifier = new CascadeClassifier(loadResources("/classifiers/haarcascade_frontalface_alt.xml"));
        facemarkLBF = FacemarkLBF.create();
        facemarkLBF.loadModel(loadResources("/models/lbfmodel.yaml"));

        Loader.load(opencv_core.class);
        vc = new VideoCapture(fileName);
        if (!vc.isOpened()) {
            System.out.println("Failed to Open the Video!");
            return;
        }
        loadFrame();
        Mat frame = new Mat();
        while(vc.read(frame)) {
            System.out.println(frame.toString());
            if(frame.empty()) break;
            if(detectFaces(frame,cascadeClassifier)) {
                for(long i = 0; i<faceVector.size();i++) {
                    points = faceVector.get(i);
                    for(long j = 0; j < points.size() ; j++) {
                        point2f = points.get(j);
                        circle(frame,new Point((int) point2f.x(),(int) point2f.y()), 2,
                                Scalar.RED,0,0,0);
                    }
                }
            }
            printVideo(resizeFrame(frame,width,height));
        }

    }
    public void playVideo(String fileName,int width,int height) {

        Loader.load(opencv_core.class);
        vc = new VideoCapture(fileName);
        if (!vc.isOpened()) {
            System.out.println("Failed to Open the Video!");
            return;
        }
        loadFrame();
        Mat frame = new Mat();
        while(vc.read(frame)) {
            System.out.println(frame.toString());
            if(frame.empty()) break;
            printVideo(resizeFrame(frame,width,height));
        }

    }

}
