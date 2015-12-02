class Kinect
{
  public PVector[] realWorldMap;
  public PImage rgbImage;
  KinectCalibration calib;

  SimpleOpenNI  context;
  int deviceIndex;

  PImage gridImage;




  int depthWidth;
  int depthHeight;

  ZoneManager zm;

  public Kinect(PApplet parent, ZoneManager zm, int dIndex)
  { 
    this.zm = zm;

    this.deviceIndex = dIndex;
    println("New kinect, deviceIndex : "+deviceIndex);
    context = new SimpleOpenNI(deviceIndex, parent);//, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
    calib = new KinectCalibration(this, context);

    gridImage = loadImage("grid.jpg");

    initKinect();
  }

  public void initKinect()
  {
    if (context.enableDepth() == false)
    {
      println("Can't open the depthMap, maybe the camera is not connected!");
    }

    if (context.enableRGB() == false)
    {
      println("Can't open the rgbMap, maybe the camera is not connected or there is no rgbSensor!");
    }

    depthWidth = context.depthWidth();
    depthHeight = context.depthHeight();

    context.alternativeViewPointDepthToImage();
  }

  public void draw()
  {    
    realWorldMap = context.depthMapRealWorld();
    rgbImage = context.rgbImage();

    int numPoints = realWorldMap.length;

    pushMatrix();
    pushStyle();

    calib.draw();
    if (calib.calibrated)
    {
      //drawGrid();
      pushStyle();
      noStroke();
      fill(255);
      sphere(30);
      popStyle();
    } 




    strokeWeight(2+precisionSteps/5);
    beginShape(POINTS);

    for (int iy = 0; iy<depthHeight; iy+=precisionSteps)
    {
      for (int ix = 0; ix<depthWidth; ix+=precisionSteps)
      {

        int index = ix + iy * depthWidth;
        PVector p = realWorldMap[index];
        color cp = rgbImage.get(ix, iy);
        boolean pointIsInZone = zm.checkPoint(p);
        if(!noDraw)
        {
          stroke(cp, pointIsInZone?255:100);
          vertex(p.x, p.y, p.z);
        }
      }
    }

    endShape();
    popStyle();

    popMatrix();
  }

  public void drawGrid()
  {
    pushMatrix();

    //translate(width / 2, height / 2);
    beginShape();
    fill(255, 100);
    texture(gridImage);

    vertex(0, 0, 0, 0, 0);
    vertex(1000, 0, 0, gridImage.width, 0);
    vertex(1000, 0, 1000, gridImage.width, gridImage.height);
    vertex(0, 0, 1000, 0, gridImage.height);
    endShape();

    popMatrix();
  }


  void mousePressed()
  {
    calib.mousePressed();
  }

  void mouseReleased()
  {
    calib.mouseReleased();
  }
}

class KinectCalibration
{
  Kinect kinect;
  SimpleOpenNI context;


  PVector originPoint = new PVector();
  PVector xPoint = new PVector();
  PVector zPoint = new PVector();
  
  PVector fixOPoint = new PVector();
  PVector fixXPoint = new PVector();
  PVector fixZPoint = new PVector();

  public boolean calibMode;
  public boolean calibrated;

  CalibHandle originHandle;
  CalibHandle xHandle;
  CalibHandle zHandle;

  CalibHandle[] handles;

  public PVector calibPos;
  public PVector calibSize;
  public PVector mouseRelativePosition;
  
  PVector[] realWorldSnapshot;

  public KinectCalibration(Kinect kinect, SimpleOpenNI context)
  {
    this.kinect = kinect;
    this.context = context;
    calibrated = false;

    mouseRelativePosition = new PVector();
    calibSize = new PVector(640,480);
    calibPos = new PVector(width-calibSize.x, kinect.deviceIndex*calibSize.y);

    originHandle = new CalibHandle(this, color(255, 0, 255));
    xHandle = new CalibHandle(this, color(255, 0, 0));
    zHandle = new CalibHandle(this, color(0, 0, 255));

    handles = new CalibHandle[] {
      originHandle, xHandle, zHandle
    };
  }

  public void draw()
  {
    if (!calibMode) return;

    mouseRelativePosition.x = mouseX - calibPos.x;
    mouseRelativePosition.y = mouseY - calibPos.y;

    pushMatrix();
    translate(calibPos.x, calibPos.y);
    image(kinect.rgbImage, 0, 0, calibSize.x, calibSize.y);
    for (CalibHandle ch : handles) ch.draw();
    popMatrix();
  }


  void mousePressed()
  {
    mouseRelativePosition.x = mouseX - calibPos.x;
    mouseRelativePosition.y = mouseY - calibPos.y;

 
    for (CalibHandle ch : handles) 
    {
      if (ch.mousePressed()) break;
    }
  }

  void mouseReleased()
  {
    for (CalibHandle ch : handles) ch.mouseReleased();
  }
  
  public void resetCalibration()
  {
     originHandle.pos = new PVector(calibSize.x/2,calibSize.y/2);
     xHandle.pos = new PVector(calibSize.x/2+100,calibSize.y/2);
     zHandle.pos = new PVector(calibSize.x/2,calibSize.y/2-100);
  }

  public void  loadCalibration()
  {
   
    String[] calibStrings = loadStrings("kinect"+kinect.deviceIndex+".txt");
    String[] oSplit = calibStrings[0].split(";");
    String[] xSplit = calibStrings[1].split(";");
    String[] zSplit = calibStrings[2].split(";");

    PVector originPointTemp = new PVector(parseFloat(oSplit[0]), parseFloat(oSplit[1]), parseFloat(oSplit[2]));
    PVector xPointTemp = new PVector(parseFloat(xSplit[0]), parseFloat(xSplit[1]), parseFloat(xSplit[2]));
    PVector zPointTemp = new PVector(parseFloat(zSplit[0]), parseFloat(zSplit[1]), parseFloat(zSplit[2]));
    
    originPoint = originPointTemp;
    xPoint = xPointTemp;
    zPoint = zPointTemp;
    
    context.convertRealWorldToProjective(originPointTemp, originHandle.pos);
    context.convertRealWorldToProjective(xPointTemp, xHandle.pos);
    context.convertRealWorldToProjective(zPointTemp, zHandle.pos);
    
    calibrate();
  }
  
  public void startCalibration()
  {
    context.resetUserCoordsys();
    realWorldSnapshot = context.depthMapRealWorld();
  }
  
  public void updateCalibrationFromHandles()
  {
    context.update();
    //realWorldSnapshot = context.depthMapRealWorld();
    
    int originIndex = (int)originHandle.pos.x + (int)originHandle.pos.y * context.depthWidth();
    int xIndex = (int)xHandle.pos.x + (int)xHandle.pos.y * context.depthWidth();
    int zIndex = (int)zHandle.pos.x + (int)zHandle.pos.y * context.depthWidth();
    originIndex = min(max(originIndex,0),realWorldSnapshot.length-1);
    xIndex = min(max(xIndex,0),realWorldSnapshot.length-1);
    zIndex = min(max(zIndex,0),realWorldSnapshot.length-1);
    
    originPoint = realWorldSnapshot[originIndex];
    xPoint = realWorldSnapshot[xIndex];
    zPoint = realWorldSnapshot[zIndex];
    
    calibrate();    
    context.update();
  }

  public void calibrate()
  {

    context.setUserCoordsys(originPoint.x, originPoint.y, originPoint.z, 
    xPoint.x, xPoint.y, xPoint.z, 
    zPoint.x, zPoint.y, zPoint.z);
    
    fixOPoint = new PVector(originPoint.x,originPoint.y,originPoint.z);
    fixXPoint = new PVector(xPoint.x,xPoint.y,xPoint.z);
    fixZPoint = new PVector(zPoint.x,zPoint.y,zPoint.z); 
    calibrated = true;

    println("Kinect "+kinect.deviceIndex+" is calibrated !");
    println(originPoint);
    println(xPoint);
    println(zPoint);
  }
  
  public void saveCalib()
  { 
   // context.resetUserCoordsys();
    
    String[] calibString = new String[3];
    calibString[0] = fixOPoint.x+";"+fixOPoint.y+";"+fixOPoint.z;
    calibString[1] = fixXPoint.x+";"+fixXPoint.y+";"+fixXPoint.z;
    calibString[2] = fixZPoint.x+";"+fixZPoint.y+";"+fixZPoint.z;
    
    saveStrings("data/kinect"+kinect.deviceIndex+".txt",calibString);
    
    //calibrate();
    //context.update();
  }
}

public class CalibHandle
{
  PVector pos;  
  color handleColor;

  int radius = 20;
  boolean dragging;
  KinectCalibration kc;

  public CalibHandle(KinectCalibration kc, color c)
  {
    this.kc = kc;
    handleColor = c;
    pos = new PVector();
  } 

  public void draw()
  {
    if (dragging)
    {
      pos.x = kc.mouseRelativePosition.x;
      pos.y = kc.mouseRelativePosition.y;
      
      //kc.updateCalibrationFromHandles();
    }

    pushStyle();
    ellipseMode(CENTER); 
    if (dragging) fill(255, 255, 0, 100);
    else fill(handleColor, 100);
    if (isOver()) 
    {
      stroke(255);
      strokeWeight(2);
    }
    ellipse(pos.x, pos.y, radius, radius);
    popStyle();
  }


  public boolean mousePressed()
  {
    dragging = isOver();
    if(dragging) kc.startCalibration();
    return dragging;
  }

  public void mouseReleased()
  {
    if(dragging)
    {
      kc.updateCalibrationFromHandles();
    }
    dragging = false;
  }

  public boolean isOver()
  {
    pos.z = 0;
    float dist = PVector.dist(kc.mouseRelativePosition, pos) ;
    //println("is Over ?"+dist+" / "+kc.mouseRelativePosition+"/"+pos);
    return dist < radius *2;
  }
}

