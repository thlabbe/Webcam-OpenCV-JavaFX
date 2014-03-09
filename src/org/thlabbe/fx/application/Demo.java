package org.thlabbe.fx.application;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Demo extends Application {

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	private Mat matriceImage;

	@Override
	public void start(final Stage stage) throws Exception {
		loadImage(/*"clooney_set.png"*/"2013_06_16_7533.JPG");
		
		final WritableImage imgIn = new WritableImage (this.matriceImage.width(), this.matriceImage.height());
		final WritableImage imgOut = new WritableImage(this.matriceImage.width(), this.matriceImage.height());
		ImageView viewInput = drawImage(imgIn);
		Text txt = new Text("x");
		ImageView viewOutput = process(imgOut);
		
		HBox pane = new HBox();
		pane.getChildren().addAll(viewInput,txt,viewOutput);
		//Group group = new Group(/* line, start, end, */viewInput,txt, viewOutput);

		stage.setTitle("Line Manipulation Sample");
		stage.setScene(new Scene(pane));
		stage.show();
	}

	private ImageView process(WritableImage img) {
		MatOfRect faceDetections = new MatOfRect();
		CascadeClassifier faceDetector = new CascadeClassifier(
				"lbpcascade_frontalface.xml");
		faceDetector.detectMultiScale(this.matriceImage, faceDetections);
		for (Rect rect : faceDetections.toArray()) {
	        Core.rectangle(this.matriceImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	        System.out.println(rect);
	    }
		int width = (int) Math.min(matriceImage.width(), img.getWidth());
		int height = (int) Math.min(matriceImage.height(), img.getHeight());
		PixelWriter pw = img.getPixelWriter();
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				int b = (int) (matriceImage.get(x, y)[0]);
				int g = (int) (matriceImage.get(x, y)[1]);
				int r = (int) (matriceImage.get(x, y)[2]);
				pw.setColor(y, x, Color.rgb(r, g, b));
			}
		}
		ImageView view = new ImageView(img);
		return view;
		}

	private ImageView drawImage(WritableImage output) {
		System.out.println(matriceImage.channels() + " : "
				+ matriceImage.depth() + " : ");
		int width = (int) Math.min(matriceImage.width(), output.getWidth());
		int height = (int) Math.min(matriceImage.height(), output.getHeight());
		PixelWriter pw = output.getPixelWriter();
		int maxR = 0;
		int maxG = 0;
		int maxB = 0;
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				int b = (int) (matriceImage.get(x, y)[0]);
				int g = (int) (matriceImage.get(x, y)[1]);
				int r = (int) (matriceImage.get(x, y)[2]);
				maxR = Math.max(maxR, r);
				maxG = Math.max(maxG, g);
				maxB = Math.max(maxB, b);

				// double a = img.get(x, y)[3];

				pw.setColor(y, x, Color.rgb(r, g, b));
			}
		}
		System.out.println("max : " + maxR + " " + maxG + " " + maxB);
		ImageView view = new ImageView(output);
		return view;
	}

	private void loadImage(String filename) {
		Mat mat = Highgui.imread(filename);
		Imgproc.resize(mat, matriceImage, new Size(.25, .25));
		infos(matriceImage);

	}

	private void infos(Mat img) {
		System.out.println(img.toString());

	}

	/*
	 * class BoundLine extends Line { BoundLine(DoubleProperty startX,
	 * DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
	 * startXProperty().bind(startX); startYProperty().bind(startY);
	 * endXProperty().bind(endX); endYProperty().bind(endY); setStrokeWidth(2);
	 * setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
	 * setStrokeLineCap(StrokeLineCap.BUTT); getStrokeDashArray().setAll(10.0,
	 * 5.0); setMouseTransparent(true); } }
	 * 
	 * // a draggable anchor displayed around a point. class Anchor extends
	 * Circle { Anchor(Color color, DoubleProperty x, DoubleProperty y) {
	 * super(x.get(), y.get(), 10); setFill(color.deriveColor(1, 1, 1, 0.5));
	 * setStroke(color); setStrokeWidth(2); setStrokeType(StrokeType.OUTSIDE);
	 * 
	 * x.bind(centerXProperty()); y.bind(centerYProperty()); enableDrag(); }
	 * 
	 * // make a node movable by dragging it around with the mouse. private void
	 * enableDrag() { final Delta dragDelta = new Delta(); setOnMousePressed(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { // record a delta
	 * distance for the drag and drop operation. dragDelta.x = getCenterX() -
	 * mouseEvent.getX(); dragDelta.y = getCenterY() - mouseEvent.getY();
	 * getScene().setCursor(Cursor.MOVE); } }); setOnMouseReleased(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) {
	 * getScene().setCursor(Cursor.HAND); } }); setOnMouseDragged(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { double newX =
	 * mouseEvent.getX() + dragDelta.x; if (newX > 0 && newX <
	 * getScene().getWidth()) { setCenterX(newX); } double newY =
	 * mouseEvent.getY() + dragDelta.y; if (newY > 0 && newY <
	 * getScene().getHeight()) { setCenterY(newY); } } }); setOnMouseEntered(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if
	 * (!mouseEvent.isPrimaryButtonDown()) { getScene().setCursor(Cursor.HAND);
	 * } } }); setOnMouseExited(new EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(MouseEvent mouseEvent) { if
	 * (!mouseEvent.isPrimaryButtonDown()) {
	 * getScene().setCursor(Cursor.DEFAULT); } } }); }
	 * 
	 * // records relative x and y co-ordinates. private class Delta { double x,
	 * y; } }
	 */
}