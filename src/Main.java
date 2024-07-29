import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main extends JFrame {
    private final JLabel cameraScreen;
    private final VideoCapture capture;
    private final Mat image;

    public Main() throws UnknownHostException {
        // Graphical User Interface ( GUI )
        String hostname = InetAddress.getLocalHost().getHostName();
        System.out.println("[ SYS ] Hostname: " + hostname);
        setTitle("Webcam of " + hostname);

        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 640, 480);
        add(cameraScreen);

        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.err.println("[ ERR ] Cannot open the camera");
        }
        System.out.println("[ SYS ] Camera active");
        image = new Mat();
        System.out.println("[ SYS ] Capture active");

        setVisible(true);
    }

    // face detector
    public void startCamera() {
        // load the face detector
        CascadeClassifier faceCascade = new CascadeClassifier();
        if (!faceCascade.load("libs/data/haarcascades/haarcascade_frontalface_alt.xml")) {
            System.out.print("\n[ ERR ] error loading haarcascade_frontalface_alt.xml\n");
            return;
        }

        MatOfRect faces = new MatOfRect();
        boolean captureAllArea = false;
        int number = 900;
        // create directories
        File facedir = new File("faces");
        File faceLogdir = new File("faceLog");
        facedir.mkdir();

        if (captureAllArea) {
            System.out.println("[ SYS ] CAPTURE METHOD: ALL AREA");
        } else {
            System.out.println("[ SYS ] CAPTURE METHOD: ONLY FACE");
        }

        while (true) {
            capture.read(image);

            if (!image.empty()) {
                MatOfByte buf = new MatOfByte();
                Imgcodecs.imencode(".jpg", image, buf);
                faceCascade.detectMultiScale(image, faces);
                Rect[] facesArray = faces.toArray();
                Mat modifiedImage = new Mat();
                image.copyTo(modifiedImage);

                // face detection
                for (Rect faceRect : facesArray) {
                    if (number >= 1000) {
                        // Rotate and clean old files
                        for (int i = 1000; i >= 0; i--) {
                            if (!faceLogdir.exists()) {
                                faceLogdir.mkdir();
                            } else {
                                // copy the file into faceLog directory
                                File source = new File("faces/faceLog_" + i + ".jpg");
                                File destination = new File("faceLog/OldFaceLog" + i + ".jpg");
                                try (InputStream inputStr = new FileInputStream(source);
                                     OutputStream outputStr = new FileOutputStream(destination)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = inputStr.read(buffer)) > 0) {
                                        outputStr.write(buffer, 0, length);
                                    }
                                    System.out.println("[ SYS ] Copied: " + source);
                                } catch (FileNotFoundException e) {
                                    System.out.println("[ ERR ] file not found: " + e.getMessage());
                                } catch (IOException e) {
                                    System.out.println("[ ERR ] IOException: " + e.getMessage());
                                }

                                // delete the files in faces directory
                                if (source.exists() && source.delete()) {
                                    System.out.println("[ SYS ] Deleted: " + source.getName());
                                } else {
                                    System.out.println("[ ERR ] Failed to delete: " + source.getName());
                                }
                            }
                        }
                        number = 0; // reset counter after rotating old files
                    } else {
                        number++;
                    }

                    // draw a rectangle
                    Imgproc.rectangle(modifiedImage, faceRect.tl(), faceRect.br(), new Scalar(0, 0, 255), 3);
                    System.out.println("[ ! ] Face detected");

                    // mothod to save the image
                    if (captureAllArea) {
                        Imgcodecs.imwrite("faces/faceLog_" + number + ".jpg", image);

                    } else {
                        Mat faceROI = new Mat(image, faceRect);
                        Imgcodecs.imwrite("faces/faceLog_" + number + ".jpg", faceROI);
                    }
                }

                MatOfByte modifiedBuf = new MatOfByte();
                Imgcodecs.imencode(".jpg", modifiedImage, modifiedBuf);
                byte[] modifiedImageData = modifiedBuf.toArray();
                ImageIcon modifiedIcon = new ImageIcon(modifiedImageData);

                SwingUtilities.invokeLater(() -> cameraScreen.setIcon(modifiedIcon));
            } else {
                System.err.println("[ ERR ] No image captured from camera");
            }
        }
    }

    // main
    public static void main(String[] args) {
        // try to load openCV
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("[ SYS ] OpenCV library loaded");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("\n[ ERR ] failed to load opencv_java410.dll\n");
        }

        // functions initializer
        EventQueue.invokeLater(() -> {
            Main main;
            try {
                main = new Main();
                new Thread(main::startCamera).start();
            } catch (UnknownHostException e) {
                System.err.println("[ ERR ] UnknownHostException: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ ERR ] Exception: " + e.getMessage());
            }
        });
    }
}