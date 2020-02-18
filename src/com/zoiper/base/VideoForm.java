package com.zoiper.base;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import com.zoiper.zdk.*;
import com.zoiper.zdk.EventHandlers.*;
import com.zoiper.zdk.Types.VideoFrameFormat;

/**
 * Based on Webcam Capture Examples from http://webcam-capture.sarxos.pl
 */
public class VideoForm implements Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener, VideoRendererEventsHandler {

	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private WebcamPicker picker = null;
	private JFrame captureFrame = null;

	private Call call = null;
	
	private boolean shutdown = false;
	
	public static final Dimension resolution = WebcamResolution.VGA.getSize();

	public VideoForm(Call call) {
		this.call = call;
		call.setVideoRendererNotificationsListener(this);
	}

	@Override
	public void run() {
		Webcam.addDiscoveryListener(this);

		captureFrame = new JFrame("Java Webcam Capture POC");
		captureFrame.setResizable(true);
		captureFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		captureFrame.setLayout(new BorderLayout());

		captureFrame.addWindowListener(this);

		picker = new WebcamPicker();
		picker.addItemListener(this);

		webcam = picker.getSelectedWebcam();

		if (webcam == null) {
			System.out.println("No webcams found...");
			System.exit(1);
		}

		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.addWebcamListener(VideoForm.this);

		panel = new WebcamPanel(webcam, false);
		panel.setFPSDisplayed(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(false);
		// panel.setFillArea(true);

		captureFrame.add(picker, BorderLayout.NORTH);
		captureFrame.add(panel, BorderLayout.CENTER);

		captureFrame.pack();
		captureFrame.setVisible(true);

		Thread cameraInitializer = new Thread() {
			@Override
			public void run() {
				panel.start();
			}
		};
		cameraInitializer.setName("cameraInitializer");
		cameraInitializer.setDaemon(true);
		cameraInitializer.setUncaughtExceptionHandler(this);
		cameraInitializer.start();
		
		Thread capturer = new Thread() {
			@Override
			public void run() {
				captureRunnable();
			}
		};
		capturer.setName("capturer");
		capturer.setDaemon(true);
		capturer.setUncaughtExceptionHandler(this);
		capturer.start();
	}

	public void start() {
		run();
	}

	public void stop() {
		shutdown = true;

		if (webcam != null) {
			panel.stop();

			captureFrame.remove(panel);

			webcam.removeWebcamListener(this);
			webcam.close();
		}
	}

	private void captureRunnable() {
		webcam.open();

		while (!shutdown) {
			if (!webcam.isOpen()) {
				break;
			}

			ByteBuffer imgBuff = webcam.getImageBytes();
			if (imgBuff == null) {
				continue;
			}
			byte[] imgRGB = new byte[imgBuff.remaining()];
			imgBuff.get(imgRGB);
			byte[] imgYUV = convertRGBtoYUV420(imgRGB, resolution.width, resolution.height);
			
			if(call != null) {
				call.sendVideoFrame(imgYUV, imgYUV.length, VideoFrameFormat.YUV420p);
			}
		}
	}

	private static byte[] convertRGBtoYUV420(byte[] rgb, int width, int height) {
		int image_size = width * height;
		int upos = image_size;
		int vpos = upos + upos / 4;
		int i = 0;

		int yuvSize = width * height * 3 / 2;
		byte[] yuv = new byte[yuvSize];

		for (int line = 0; line < height; ++line) {
			if ((line % 2) == 0) {
				for( int x = 0; x < width; x += 2 ) {
					byte r = rgb[3 * i];
					byte g = rgb[3 * i + 1];
					byte b = rgb[3 * i + 2];

					yuv[i++] = (byte) (((66*r + 129*g + 25*b) >> 8) + 16);

					yuv[upos++] = (byte) (((-38*r + -74*g + 112*b) >> 8) + 128);
					yuv[vpos++] = (byte) (((112*r + -94*g + -18*b) >> 8) + 128);

					r = rgb[3 * i];
					g = rgb[3 * i + 1];
					b = rgb[3 * i + 2];

					yuv[i++] = (byte) (((66*r + 129*g + 25*b) >> 8) + 16);
				}
			}
			else {
				for (int x = 0; x < width; x += 1) {
					byte r = rgb[3 * i];
					byte g = rgb[3 * i + 1];
					byte b = rgb[3 * i + 2];

					yuv[i++] = (byte) (((66*r + 129*g + 25*b) >> 8) + 16);
				}
			}
		}

		return yuv;
	}

	@Override
	public void onVideoFrameReceived(byte[] pBuffer, int length, int width, int height) {
		//TODO: IMPLEMENT ME!!!
		//TODO: NOTE! The incoming frame is in YUV!
	}

	@Override
	public void webcamOpen(WebcamEvent we) {
		System.out.println("webcam open");
	}

	@Override
	public void webcamClosed(WebcamEvent we) {
		System.out.println("webcam closed");
	}

	@Override
	public void webcamDisposed(WebcamEvent we) {
		System.out.println("webcam disposed");
	}

	@Override
	public void webcamImageObtained(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		webcam.close();
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println("webcam viewer resumed");
		panel.resume();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println("webcam viewer paused");
		panel.pause();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println(String.format("Exception in thread %s", t.getName()));
		e.printStackTrace();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != webcam) {
			if (webcam != null) {
				panel.stop();

				captureFrame.remove(panel);

				webcam.removeWebcamListener(this);
				webcam.close();

				webcam = (Webcam) e.getItem();
				webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.addWebcamListener(this);

				System.out.println("selected " + webcam.getName());

				panel = new WebcamPanel(webcam, false);
				panel.setFPSDisplayed(true);
				panel.setImageSizeDisplayed(true);
				panel.setMirrored(false);
				// panel.setFillArea(true);

				captureFrame.add(panel, BorderLayout.CENTER);
				captureFrame.pack();

				Thread t = new Thread() {

					@Override
					public void run() {
						panel.start();
					}
				};
				t.setName("example-stoper");
				t.setDaemon(true);
				t.setUncaughtExceptionHandler(this);
				t.start();
			}
		}
	}

	@Override
	public void webcamFound(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.addItem(event.getWebcam());
		}
	}

	@Override
	public void webcamGone(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.removeItem(event.getWebcam());
		}
	}
}
