package org.thlabbe.fx.application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

public class WebCamDemo extends Application {
	private static final String FRONTFACE = "src\\org\\thlabbe\\fx\\resources\\lbpcascade_frontalface.xml";
	private static final String PROFILEFACE = "src\\org\\thlabbe\\fx\\resources\\lbpcascade_profileface.xml";
	private Mat frame;
	private VideoCapture cap;
	private ImageView view;
	private final static int VIEW_INDEX = 0;
	private VBox pane;
	private boolean paneExist;
	private HBox buttonBar;

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		setup();

		pane = new VBox();

		Button refreshBtn = new Button("Refresh");
		refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				actionRefresh();
			}
		});

		Button faceDetectBtn = new Button("Face Detect");
		faceDetectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				actionDetect(FRONTFACE);
			}
		});
		Button profileDetectBtn = new Button("Profile Detect");
		profileDetectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				actionDetect(PROFILEFACE);
			}
		});
		buttonBar = new HBox();
		buttonBar.getChildren().addAll(refreshBtn, faceDetectBtn,
				profileDetectBtn);
		pane.getChildren().addAll(view, buttonBar);
		paneExist = true;
		stage.setScene(new Scene(pane));
		stage.show();
	}

	private void actionDetect(String classifier) {
		MatOfRect detections = new MatOfRect();
		CascadeClassifier detector = new CascadeClassifier(classifier);
		detector.detectMultiScale(frame, detections);
		for (Rect rect : detections.toArray()) {
			Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}
		view = draw(frame);
		if (paneExist) {
			pane.getChildren().remove(VIEW_INDEX);
			pane.getChildren().add(VIEW_INDEX, view);
		}
	}

	private void actionRefresh() {
		if (cap.isOpened()) {
			boolean done = false;

			while (frame.empty() || !done) {
				cap.read(frame);
				done = true;
			}

			System.out.println(frame);
			view = draw(frame);
			if (paneExist) {
				pane.getChildren().remove(VIEW_INDEX);
				pane.getChildren().add(VIEW_INDEX, view);
			}

		} else {
			System.out.println("webcam not open");
		}
	}

	private void setup() {
		this.frame = new Mat();
		cap = new VideoCapture(0);

		// (profileDetectBtn)
		// new VideoCapture(0);
		/*
		 * try { Thread.sleep(500); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */
		paneExist = false;
		System.out.println(cap.open(0));
		actionRefresh();
	}

	private ImageView draw(Mat matrix) {

		int width = matrix.width();
		int height = matrix.height();
		WritableImage image = new WritableImage(width, height);

		PixelWriter pw = image.getPixelWriter();

		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				int b = (int) (matrix.get(x, y)[0]);
				int g = (int) (matrix.get(x, y)[1]);
				int r = (int) (matrix.get(x, y)[2]);
				pw.setColor(y, x, Color.rgb(r, g, b));
			}
		}

		return new ImageView(image);
	}
}