import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class FilterImg {
	Mat hsvImg = new Mat();
	
	public void convertImg(Mat img) {
		Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_RGB2HSV);
		//H   S  V
		Scalar lowerBoundYellow = new Scalar(85, 100, 100);
		Scalar upperBoundYellow = new Scalar(100, 255, 255);  //gray value is (100, 100, 100)
		Core.inRange(hsvImg, lowerBoundYellow, upperBoundYellow, hsvImg);
		
		Main.showImage(hsvImg);
	}
	
	public Mat getHsvImg() {
		return hsvImg;
	}
}
