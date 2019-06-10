import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {

	public static void main(String[] args) {
		String dir = "res/boxes/b3/b3_4.jpg";
		// String[] paths = new String[] {"res/one_foot.jpg", "res/two_feet.jpg",
		// "res/three_feet.jpg", "res/four_feet.jpg"};
		// String[] paths = new String[] {"res/whiteOutOne.jpg", "res/whiteOutTwo.jpg",
		// "res/whiteOutThree.jpg"};
		//DistanceDetect dist = new DistanceDetect(dir, new Scalar(85, 100, 100), new Scalar(100, 255, 255));
		GripPipeline gp = new GripPipeline("res/boxes/b3/b3_4.jpg");
		// File[] directoryListing = dir.listFiles();
		// if(directoryListing != null) {
		// for(File file : directoryListing) {
		// new DistanceDetect(file);
		// //System.out.println(file);
		// }
		// }

		Thread t1 = new Thread(gp);
		t1.start();
	}
	
	public static void showImage(Mat m, String s) {
		displayImage(Mat2BufferedImage(m), s);
	}

	public static void showImage(Mat m) {
		displayImage(Mat2BufferedImage(m));
	}

	public static BufferedImage Mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
	
	public static void displayImage(Image img) {
		displayImage(img, "");
	}

	public static void displayImage(Image img, String s) {
		ImageIcon icon = new ImageIcon(img);
		JFrame frame = new JFrame(s);
		frame.setLayout(new FlowLayout());
		frame.setSize(img.getWidth(null) + 50, img.getHeight(null) + 50);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
