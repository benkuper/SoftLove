/*
Thomas Sanchez Lengeling.
 http://codigogenerativo.com/

 KinectPV2, Kinect for Windows v2 library for processing

 Simple Face tracking, up-to 6 users with mode identifier
 */

import oscP5.*;
import netP5.*;

import KinectPV2.*;

KinectPV2 kinect;

FaceData [] faceData;
OscP5 oscP5;

NetAddress remoteLoc;

String remoteIP;
int remotePort;

int[] emotions;
String[] emotionLabels = new String[]{"Tracked","Happy","Engaged","2","3","4","5","6","7"};

Spout spout;

void setup() {
  size(640,480, P2D);

  oscP5 = new OscP5(this,12001);
  
  kinect = new KinectPV2(this);

  //for face detection based on the color Img
  kinect.enableColorImg(true);

  //for face detection base on the infrared Img
  kinect.enableDepthImg(true);

  //enable face detection
  kinect.enableFaceDetection(true);

  kinect.init();
  
  remoteIP = "127.0.0.1";
  remotePort = 13000;
  try
  {
    String[] config = loadStrings("config.txt");
    remoteIP = config[0];
    remotePort = int(config[1]);
  }catch(Exception e)
  {
     println("Error reading file config.txt"); 
  }
  
  remoteLoc = new NetAddress(remoteIP,remotePort);
  
  emotions = new int[9];
  frameRate(30);
  
   spout = new Spout();
  spout.initSender("KinectFace", width, height);
}

void draw() {
  background(0);

  kinect.generateFaceData();
  
  
  //draw face information obtained by the color frame
  /*
  pushMatrix();
  scale(0.5f);
  image(kinect.getColorImage(), 0, 0,640,480);
  getFaceMapColorData();
  popMatrix();
  */ 

  //draw face information obtained by the infrared frame
  pushMatrix();
  //translate(640*0.5f, 0.0f);
  image(kinect.getDepthImage(), 0,0);
  getFaceMapInfraredData();
  popMatrix();


  fill(255);
  text("frameRate "+frameRate, 50, 50);
  
  text("Sending OSC to "+remoteIP+":"+remotePort,10,height-30);
  
   spout.sendTexture();
}

public void getFaceMapInfraredData() {

  ArrayList<FaceData> faceData =  kinect.getFaceData();

    setEmotion(0,faceData.size() > 0?1:0);
    
  for (int i = 0; i < faceData.size(); i++) {
    FaceData faceD = faceData.get(i);
    
    
    if (faceD.isFaceTracked()) {
      //get the face data from the infrared frame
      PVector [] facePointsInfrared = faceD.getFacePointsInfraredMap();

      KRectangle rectFace = faceD.getBoundingRectInfrared();

      FaceFeatures [] faceFeatures = faceD.getFaceFeatures();

      //get the color of th user
      int col = faceD.getIndexColor();

      //for nose information
      PVector nosePos = new PVector();
      noStroke();

      fill(col);
      for (int j = 0; j < facePointsInfrared.length; j++) {
        if (j == KinectPV2.Face_Nose)
          nosePos.set(facePointsInfrared[j].x, facePointsInfrared[j].y);

        ellipse(facePointsInfrared[j].x, facePointsInfrared[j].y, 15, 15);
      }

      //Feature detection of the user
      if (nosePos.x != 0 && nosePos.y != 0)
        for (int j = 0; j < 8; j++) {
          int st   = faceFeatures[j].getState();
          int type = faceFeatures[j].getFeatureType();

          String str = getStateTypeAsString(st, type);
          
          if(type == KinectPV2.FaceProperty_Happy || type == KinectPV2.FaceProperty_Engaged)
          {
            setEmotion(type+1,st);
          }

          fill(255);
          text(str, nosePos.x + 150, nosePos.y - 70 + j*25);
        }
      stroke(255, 0, 0);
      noFill();
      rect(rectFace.getX(), rectFace.getY(), rectFace.getWidth(), rectFace.getHeight());
    }
  }
}

void setEmotion(int type, int val)
{
  //println("set emotion "+emotionLabels[type]+" > "+val);
  //if(emotions[type] == val) return;
   
   emotions[type] = val;
   String emoLabel = emotionLabels[type];
   sendEmotionOSC(emoLabel,val);
}


void sendEmotionOSC(String emo,int val)
{
  //println("send "+emo+" : "+val);
  OscMessage msg = new OscMessage("/emotion/kinect");
  msg.add(emo);
  msg.add(val);
  oscP5.send(msg,remoteLoc);
}


//Face properties
String getStateTypeAsString(int state, int type) {
  String  str ="";
  switch(type) {
  case KinectPV2.FaceProperty_Happy:
    str = "Happy";
    break;

  case KinectPV2.FaceProperty_Engaged:
    str = "Engaged";
    break;

  case KinectPV2.FaceProperty_LeftEyeClosed:
    str = "LeftEyeClosed";
    break;

  case KinectPV2.FaceProperty_RightEyeClosed:
    str = "RightEyeClosed";
    break;

  case KinectPV2.FaceProperty_LookingAway:
    str = "LookingAway";
    break;

  case KinectPV2.FaceProperty_MouthMoved:
    str = "MouthMoved";
    break;

  case KinectPV2.FaceProperty_MouthOpen:
    str = "MouthOpen";
    break;

  case KinectPV2.FaceProperty_WearingGlasses:
    str = "WearingGlasses";
    break;
  }

  switch(state) {
  case KinectPV2.DetectionResult_Unknown:
    str += ": Unknown";
    break;
  case KinectPV2.DetectionResult_Yes:
    str += ": Yes";
    break;
  case KinectPV2.DetectionResult_No:
    str += ": No";
    break;
  }

  return str;
}