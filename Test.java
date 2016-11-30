import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoWriter;
import org.opencv.core.*;
import org.opencv.imgproc.*;




public class Test
{
   private Timer timer;
   static String fileName ="";
   static String saveName ="";
   private JLabel lblTime;
   static boolean play = false,save = false;
   public Test()
   {

	    //needed to properly link the library from opencv
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    Mat frame = new Mat();
	    
	    //setting up the UI
	    JFrame jframe = new JFrame("fileName");
	    JPanel panel1 = new JPanel();
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel panel2 = new JPanel();
	    

	    lblTime = new JLabel("0");
	    panel2.setLayout(new BorderLayout());
	    panel1.setSize(1600,100);
	    panel2.setSize(1600,900);
	    jframe.setSize(1600,900);
       
	    JButton btnLoad = new JButton("Load");  
	    JButton btnPlay = new JButton("Play"); 
	    JButton btnCrop = new JButton("Crop");
	    JButton btnSave = new JButton("Save");
	    JButton btnGrab = new JButton("Chroma Playback");
	    JTextField x1 = new JTextField();
	    JTextField x2 = new JTextField();
	    JTextField y1 = new JTextField();
	    JTextField y2 = new JTextField();
	    JTextField frameGrab = new JTextField();
	    Rect rect = new Rect(0,0,0,0);
	    
	    x1.setPreferredSize( new Dimension( 200, 24 ) );
	    x2.setPreferredSize( new Dimension( 200, 24 ) );
	    y1.setPreferredSize( new Dimension( 200, 24 ) );
	    y2.setPreferredSize( new Dimension( 200, 24 ) );
	    frameGrab.setPreferredSize( new Dimension( 200, 24 ) );

	    JLabel vidpanel = new JLabel();
	    JPanel panel3 = new JPanel();
	    panel3.setSize(1600,800);
	    panel3.add(vidpanel);
	    panel1.add(btnPlay);
	    panel1.add(btnLoad);
	    panel1.add(btnSave);
	    panel1.add(btnCrop);

	    
	    panel1.add(x1);
	    panel1.add(y1);
	    panel1.add(x2);
	    panel1.add(y2);
	    
	    panel1.add(btnGrab);
	    panel1.add(frameGrab);
	    panel1.add(lblTime);
	    
	    //creating a panel that contains the video and the
	    //bar with buttons and putting them in the frame.
	    panel2.add(panel3, BorderLayout.PAGE_START);
	    panel2.add(panel1, BorderLayout.PAGE_END);

	    jframe.add(panel2);
	    jframe.setContentPane(panel2);
	    jframe.setVisible(true);
   		Mat firstFrame = new Mat();
	    VideoCapture v1 = new VideoCapture();
	    
        
        //button to that loads a video
	    btnLoad.addActionListener(new ActionListener()
	    {
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		   JFileChooser chooser = new JFileChooser();
	    	       int x = chooser.showOpenDialog(null);
	    	       if (x == JFileChooser.APPROVE_OPTION) {
	    	           File file = chooser.getSelectedFile();
	    	           if (file == null) {
	    	               return;
	    	           }

	    	           fileName = chooser.getSelectedFile().getAbsolutePath();
	    	           v1.open(fileName);
	    	           VideoCapture v1Play = v1;
	    	       	   Mat roi;
	 	    		   Rect rect2 = new Rect(0,0,(int)v1.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)v1.get(Videoio.CAP_PROP_FRAME_HEIGHT));
	    	           if (v1Play.read(firstFrame)) {
	    	           	   roi = firstFrame.submat(rect2);
	    	               ImageIcon image = new ImageIcon(Mat2BufferedImage(roi));
	    	               vidpanel.setIcon(image);
	    	               vidpanel.repaint();
	    	           }
	 	    		  rect.x = 0;
		    		  rect.y = 0;
		    		  rect.width = 0;
		    		  rect.height = 0;
	    	       }
	    	  }
	    	});
	    
	    //btnGrab takes a time in seconds and displays the frame at that time, it is the basic method I am going to use
	    //in order to display the frames of the video at the current position when i implement timeline editing with both videos
	    //loaded
	    btnGrab.addActionListener(new ActionListener()
	    {
	    	  public void actionPerformed(ActionEvent e)
	    	  { 	
	    		  	int tolerance = Integer.parseInt(frameGrab.getText());
		    		VideoCapture playVid = v1;
		    		try {
						removeColor(playVid,vidpanel,rect,tolerance);
					} catch (InterruptedException e1) {

					}
	    	  
	    		  /*
	    		  VideoCapture vidFrame = v1;
	    		  Mat roi;
	    		  Mat mat = new Mat();
	    		  Rect rect2 = new Rect(0,0,(int)v1.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)v1.get(Videoio.CAP_PROP_FRAME_HEIGHT));   
	    		  if(rect.height != 0 && rect.width != 0){
	    				rect2 = rect;
	    		  }
	    		  int frameNum = Integer.parseInt(frameGrab.getText());
	    		  
	    		  int fps = 1000/(int) vidFrame.get(Videoio.CAP_PROP_FPS);
	    		  //int frame = (int) (vidFrame.get(Videoio.CAP_PROP_FRAME_COUNT) / fps);
	    		  int frame = fps * frameNum;
	    		  System.out.println(frame + " " + fps + " "+ vidFrame.get(Videoio.CAP_PROP_FRAME_COUNT));
	    		  vidFrame.set(Videoio.CAP_PROP_POS_FRAMES, frame);
   	           if (vidFrame.read(mat)) {
	           	   roi = mat.submat(rect2);
	               ImageIcon image = new ImageIcon(Mat2BufferedImage(roi));
	               vidpanel.setIcon(image);
	               vidpanel.repaint();
	           }
   	           */
	    	  }
	    	});
	        
	    //opens the default file viewer to select a file to save to
	    //creates the file if it doesnt exist or overwrites one that does exist
	    btnSave.addActionListener(new ActionListener()
	    {
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		   JFileChooser chooser = new JFileChooser();
	    	       int x = chooser.showSaveDialog(null);
	    	       if (x == JFileChooser.APPROVE_OPTION) {
	    	           File file = chooser.getSelectedFile();
	    	           if (file == null) {
	    	               return;
	    	           }
	    	           saveName = chooser.getSelectedFile().getAbsolutePath();

	    	           File f = new File(chooser.getSelectedFile().getAbsolutePath());
	    	           if(!f.exists()) { 
	    	        	   try {
							f.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	    	           }	    		
	    	           VideoCapture playVid = v1;	    		
	    	           saveVideo(playVid,rect,saveName);
	    	           System.out.println(saveName);
	    	           save = true;

	    	       }
	    	  }
	    	});
	    
	    //button sets the region of interest (area to be cropped) and repaints it to the screen
	    btnCrop.addActionListener(new ActionListener()
	    {
	    	  public void actionPerformed(ActionEvent e)
	    	  {
	    		  rect.x = Integer.parseInt(x1.getText());
	    		  rect.y = Integer.parseInt(y1.getText());
	    		  rect.width = Integer.parseInt(x2.getText());
	    		  rect.height = Integer.parseInt(y2.getText());
	    		  Mat roi;
	    		  Mat temp = firstFrame;
	           	  roi = temp.submat(rect);
	              ImageIcon image = new ImageIcon(Mat2BufferedImage(roi));
	              vidpanel.setIcon(image);
	              vidpanel.repaint();	    		  
	    		  
	    	  }
	    	});    
	    
        
	    //calls the playVideo function.
	    btnPlay.addActionListener(new ActionListener()
	    {
	    	  public void actionPerformed(ActionEvent e)
	    	  {
		    		VideoCapture playVid = v1;
		    		try {
						playVideo(playVid,vidpanel,rect);
					} catch (InterruptedException e1) {

					}
	    	  }
	    	});
	    
   }

//saveVideo takes in the video, an area that is cropped or the whole video, and the file name it is to be saved too.   
//currently only saves things as an mp4, not to familiar with how to convert codecs and save something as a .avi or another file type.
public void removeColor(VideoCapture vid, JLabel panel,Rect crop, int tolerance) throws InterruptedException{
	
	final Rect rect = new Rect(0,0,(int)vid.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)vid.get(Videoio.CAP_PROP_FRAME_HEIGHT));   
	Mat image = Imgcodecs.imread("C:/Users/Kyler Pittman/Mountain_View_20177.jpg", Imgcodecs.CV_LOAD_IMAGE_COLOR);

	int fps = 1000/(int) vid.get(Videoio.CAP_PROP_FPS);
	Mat frame = new Mat();

	//with the way actionlisteners work in java i have to use a timer to
	//play a video. I am looking into otherways to play it, it seems like the video
	//sort of lags when it is played with the timer.
	ActionListener action = new ActionListener()
    {   
		@Override
		public void actionPerformed(ActionEvent e) {

			// TODO Auto-generated method stub
	        if (vid.read(frame)) {

	        	for(int i = 0; i<frame.rows();i++){
	        		for(int j = 0; j<frame.cols();j++){
	        			//x is getting the RGB of the pixel at i,j from the current frame of the video.
	        			//y is getting the RGB of the pixel at i,j at from the image being put into the background
	    	    		double[] x = frame.get(i,j);
	    	    		double[] y = image.get(i, j);
	    	    		if(x[1]>x[0] && x[1]>x[2]){
	    	    			double z = 2 * x[1] - x[0] - x[2];
	    	    			if(z>tolerance){
	    	    				x[0] = y[0];
	    	    				x[1] = y[1];
	    	    				x[2] = y[2];
	    			    		frame.put(i,j,x);
	    	    			}
	    	    			else{
	    	    				x[1] = x[1]-10;
	    	    				frame.put(i,j,x);
	    	    			}
	    	    		}
	        		}
	        	}

	        	Mat roi;
	        	roi = frame.submat(rect);
	            ImageIcon image = new ImageIcon(Mat2BufferedImage(roi));
	            panel.setIcon(image);	    
	            panel.repaint();
	            lblTime.setText(String.format("%.2f", (vid.get(Videoio.CAP_PROP_POS_MSEC)/1000)));
	            try {
					Thread.sleep(fps);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
	        else timer.stop();
		}

    };
    timer = new Timer(fps,action);
    timer.setInitialDelay(0);
    timer.start();

}
public void saveVideo(VideoCapture vid,Rect size,String saveFile){
	    vid.set(Videoio.CAP_PROP_POS_FRAMES, 0);
		System.out.println("Saving to "+ saveFile + "\n");
		Rect rect = new Rect(0,0,(int)vid.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)vid.get(Videoio.CAP_PROP_FRAME_HEIGHT));   
		if(size.height != 0 && size.width != 0){
			rect = size;
		}

		Mat frame = new Mat();
		Mat roi;
		System.out.println(vid.get(Videoio.CAP_PROP_FOURCC));
	    VideoWriter writer = new VideoWriter();
	    
	    //DIVX is the codec for mp4m, then it just needs the dimensions of the video and the fps in order to save the video
	    //for some reason it saves videos larger than they are when they opened, im not sure why at the moment.
	    //i.e the video im using for testing is 7.5 sec long and is 7.4 KB but it saves it at 10.4 KB
	    //writer.open(saveFile,VideoWriter.fourcc('D', 'I', 'V', 'X'),30, rect.size(), true );
	    writer.open(saveFile,VideoWriter.fourcc('D', 'I', 'V', 'X'),vid.get(Videoio.CAP_PROP_FPS), rect.size(), true );
		while(true){
	        if (vid.read(frame)) {
	        	roi = frame.submat(rect);
	        	writer.write(roi);
	        }
	        else break;
		}
		
	    writer.release();
	}
	
//playVideo takes in a video, the panel the video is displayed to, and a rectangle for any area that might be cropped.

public void playVideo(VideoCapture vid, JLabel panel,Rect crop) throws InterruptedException{
		
		final Rect rect = new Rect(0,0,(int)vid.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)vid.get(Videoio.CAP_PROP_FRAME_HEIGHT));   


		int fps = 1000/(int) vid.get(Videoio.CAP_PROP_FPS);
		Mat frame = new Mat();

		//with the way actionlisteners work in java i have to use a timer to
		//play a video. I am looking into otherways to play it, it seems like the video
		//sort of lags when it is played with the timer.
		ActionListener action = new ActionListener()
        {   
			@Override
			public void actionPerformed(ActionEvent e) {

				// TODO Auto-generated method stub
		        if (vid.read(frame)) {
		        	Mat roi;
		        	roi = frame.submat(rect);
		            ImageIcon image = new ImageIcon(Mat2BufferedImage(roi));
		            panel.setIcon(image);	    
		            panel.repaint();
		            lblTime.setText(String.format("%.2f", (vid.get(Videoio.CAP_PROP_POS_MSEC)/1000)));
		            try {
						Thread.sleep(fps);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        }
		        else timer.stop();
			}

        };
	    timer = new Timer(fps,action);
	    timer.setInitialDelay(0);
	    timer.start();
	}





//this is code that I found that converts an opencv Mat to an image
//for loading it to the screen. I did not wrie any of the bufferedimage code.
public static BufferedImage Mat2BufferedImage(Mat m){
//source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
//Fastest code
//The output can be assigned either to a BufferedImage or to an Image

int type = BufferedImage.TYPE_BYTE_GRAY;

if ( m.channels() > 1 ) {
    type = BufferedImage.TYPE_3BYTE_BGR;
}

int bufferSize = m.channels()*m.cols()*m.rows();
byte [] b = new byte[bufferSize];
m.get(0,0,b); // get all the pixels
BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
System.arraycopy(b, 0, targetPixels, 0, b.length);  
return image;

}

public static void main(String[] args)
{
    SwingUtilities.invokeLater(new Runnable()
    {
        @Override
        public void run()
        {
            new Test();
        }
    });
}
}