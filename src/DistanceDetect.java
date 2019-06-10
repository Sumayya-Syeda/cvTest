import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

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

//find proportion between real life distance from box to camera and size of image on screen
//measure distance, take pictures *done*
//draw a box around the filter field box on screen
//find function to draw around specific objects on screen
//use this box to measure the dimensions of the box
//find the equation
//return distance based on object position on screen

//contoursFrame = 
public class DistanceDetect implements Runnable {

	String path;
	private Scalar upperBoundValue;
	private Scalar lowerBoundValue;
	InterpolatingMap data;
	private Mat img;

	private int x, y;
	private final int imgHeight = 1008;
	private final int imgWidth = 756;
	

	public DistanceDetect(String p, Scalar lowerBoundVal, Scalar upperBoundVal) {
		// file = f;
		path = p;
		data = new InterpolatingMap();

		data.addDataPoint(608d, 1d);
		data.addDataPoint(70d, 10d);
		data.addDataPoint(64d, 11d);
		data.addDataPoint(60d, 12d);
		data.addDataPoint(367d, 2d);
		data.addDataPoint(253d, 3d);
		data.addDataPoint(189d, 4d);
		data.addDataPoint(158d, 5d);
		data.addDataPoint(111d, 6d);
		data.addDataPoint(95d, 7d);
		data.addDataPoint(87d, 8d);
		data.addDataPoint(77d, 9d);

		upperBoundValue = upperBoundVal;
		lowerBoundValue = lowerBoundVal;

	}

	public void run() {
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File file : directoryListing) {
				Mat hsv = convertToHsv(file);
				printSize(hsv);
			}
		}

	}

	public Mat convertToHsv(File file) {// (Mat img) {
		System.out.println("\nChecking for " + path + "/" + file.getName());
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String fileName = file.getName();
		img = Imgcodecs.imread("res/boxes/b3/" + fileName);
		Imgproc.resize(img, img, new Size(imgWidth, imgHeight));
		
		Mat hsvImg = new Mat();
		Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_RGB2HSV);
	
		Core.inRange(hsvImg, lowerBoundValue, upperBoundValue, hsvImg);
		Mat kernel = Mat.ones(new Size(5, 5), CvType.CV_8U);
		Imgproc.erode(hsvImg, hsvImg, kernel, new Point(0, 0), 7);
		Imgproc.dilate(hsvImg, hsvImg, kernel, new Point(0, 0), 7);

		return hsvImg;
	}

	public void printSize(Mat hsvImg) {
		int height = 60;
		int width = 0;
		int x = 0;
		int y = 0;
		String distance = "";
		
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// convert to grayscale
		Imgproc.blur(hsvImg, hsvImg, new Size(10, 10));

		Imgproc.findContours(hsvImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		// go through the contours
		for (int i = 0; i < contours.size(); i++) { // for each contour
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());

			double approxDistance = Imgproc.arcLength(contour2f, true) * .02; // measure the length of a closed contour
																				// curve
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true); // connect the points of the contours to approximate a closed polygon

			MatOfPoint points = new MatOfPoint(approxCurve.toArray()); // convert back to matofpoint
			int verticies = points.height();
			if (verticies == 4) {
				Rect rect = Imgproc.boundingRect(points); // create bounding box
				x = rect.x;
				y = rect.y;
				height = rect.height;
				width = rect.width;
				Imgproc.rectangle(hsvImg, new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 255), 3);// draw the rect
				
				if (height > 60) {
					// System.out.println(data.getValue((double) height) + " raw distance");
					
					distance = new DecimalFormat("##.##").format(data.getValue((double) height));
					double angle = getAngle(imgHeight, imgWidth, x, y, height, width);
					System.out.println(distance + " at height " + height);
				
					Imgproc.putText(hsvImg, "Distance(ft): " + distance, new Point(rect.x, rect.y - 20),
							Core.FONT_HERSHEY_PLAIN, 1.2, new Scalar(250, 0, 0), 1);
					Imgproc.putText(hsvImg, "Angle(deg) " + new DecimalFormat("##.##").format(angle), new Point(rect.x, rect.y - 40), 
							Core.FONT_HERSHEY_PLAIN, 1.2, new Scalar(250, 0, 0), 1);
				}
			}
		}
		Main.showImage(hsvImg);
	}

	public double getAngle(int imgHeight, int imgWidth, int x, int y, int boxH, int boxW) {
		int height = imgHeight - (y + boxH);
		int width = ((x + boxW/2) - imgWidth/2); //camera is always at center of frame
		
		return Math.toDegrees(Math.atan2(width, height)); //angle in degrees
		
	
		
	}

	// IGNORE THE REST
	public double findDistance(int p) { // some constant parameter of object, use height

		int focal = 683; // f = (image of object width(pixels) * distance(in))/width of object(in); focal
							// is in pixels; 756px for orig box width
		int objWidth = 12;
		double distance = ((double) (focal * objWidth) / p) / 12;

		// System.out.println("Distance to box is " + distance + " feet");

		return distance;

	}

}