import SimpleOpenNI.*;
import java.util.ArrayList;

import oscP5.*;
import netP5.*;

Kinect[] kinects;
ZoneManager zm;
OSCManager oscManager;

//View navigation
float distance = 700;
boolean viewDragging;
boolean viewRotating;
PVector dragOffset;
PVector rotateOffset;
PVector initMousePos;

boolean navMode;
boolean tmpEditMode;

//must be general
int precisionSteps = 30;
int precisionFactor = precisionSteps*precisionSteps;
boolean altPressed;

boolean zonesAreExclusives = false;

boolean noDraw= false;

void setup()
{
  
  size(1280,960, P3D);
  frameRate(30);
  zm = new ZoneManager();
  zm.loadZones();
  
  oscManager = new OSCManager(this);
  
  background(0);
  fill(255);
  text("Init...",10,30);
  initKinects();
  
  rotateOffset = new PVector(-23.0,-173.0);
  dragOffset = new PVector(29.0,152.0);
  
  hint(DISABLE_DEPTH_TEST);
}

void initKinects()
{
  println("Init Kinects ...");
  SimpleOpenNI.start();
  StrVector strList = new StrVector();
  SimpleOpenNI.deviceNames(strList);
  
  kinects = new Kinect[(int)strList.size()];
  println("Found "+kinects.length+" devices.");
  for(int i=0;i<kinects.length;i++)
  {
    kinects[i] = new Kinect(this,zm, i);
    kinects[i].calib.loadCalibration();
  }
}


void draw()
{
  background(0);
  
  SimpleOpenNI.updateAll();
  for(Kinect k : kinects) 
  {
    k.calib.draw();
  }
  
  //View nav handling
  if(viewRotating)
  {
    rotateOffset.x += mouseX-initMousePos.x;
    rotateOffset.y += mouseY-initMousePos.y;
    initMousePos.set(mouseX,mouseY);
    
    
  }
  
  if(viewDragging)
  {
    dragOffset.x += mouseX-initMousePos.x;
    dragOffset.y += mouseY-initMousePos.y;
    initMousePos.set(mouseX,mouseY);
  }

  pushMatrix();
  translate(width / 2+dragOffset.x*2, height / 2+dragOffset.y*2);
  
  translate(0,0, -distance);
  rotateX(-rotateOffset.y*PI/180f);
  rotateY(-rotateOffset.x*PI/180f);
    
  zm.update();
  for(Kinect k : kinects) k.draw();
  
  zm.draw();
  zm.checkActiveZones();
  
  popMatrix();
  textSize(12);
  pushStyle();
  
  if(navMode) fill(255,255,0);
   else fill(200);
   text("Navigation Mode (Hold SHIFT)",10,30);
   
   if(zm.editMode) fill(255,255,0);
   else fill(200);
   text("Zone Mode (Z to toggle)",10,60);


  text("Params :",0,height-100);
  if(zonesAreExclusives) fill(255,255,0);
   else fill(200);
  text("Zones Are Exclusives (E to toggle)",0,height-70);
  popStyle();
  
  
    
}

void mouseWheel(MouseEvent event)
{
  distance += event.getCount()*30;
}

void mousePressed()
{
  for(Kinect k : kinects ) k.mousePressed();
  
  if(zm.editMode) zm.mousePressed();
  
  if(navMode)
  {
    if (mouseButton == LEFT)
    {
      viewRotating = true;
      initMousePos = new PVector(mouseX,mouseY);
    }else if(mouseButton == RIGHT) 
    {
      viewDragging = true;
      initMousePos = new PVector(mouseX,mouseY);
    }
  }
}
 
void mouseReleased()
{
  
  for(Kinect k : kinects ) k.mouseReleased();
  zm.mouseReleased();
  
  if(navMode)
  {
    viewDragging = false;
    viewRotating = false;
    
    //println("rotateOffset = new PVector("+rotateOffset.x+","+rotateOffset.y+")");
    //println("dragOffset = new PVector("+dragOffset.x+","+dragOffset.y+")");
  }
  
}

void keyPressed(KeyEvent e)
{
  
  switch(key)
  {
    
    case 'c':
    for(Kinect k:kinects) k.calib.calibMode = !k.calib.calibMode;
    break;
    
    case 'r':
      for(Kinect k:kinects) k.calib.resetCalibration();
      break;
    
    case 'z':
    zm.setEditMode(!zm.editMode);
    break;
    
    case '+':
    zm.addZone();
    break;
    
    case '-':
    zm.removeLastZone();
    break;
    
   case 'l':
   for(Kinect k:kinects) k.calib.loadCalibration();
   zm.loadZones();
   break;
   
   case 'e':
   zonesAreExclusives = !zonesAreExclusives;
   break;
    
    case 's':
    zm.saveZones();
    for(Kinect k:kinects) k.calib.saveCalib();
    
    break;
    
    case ' ':
    noDraw = !noDraw;
    break;
  }
  
  switch(keyCode)
  {
    case TAB:
    zm.nextEditZone();
    break;
    
    case SHIFT:
    navMode = true;
    tmpEditMode = zm.editMode;
    zm.setEditMode(false);
    break;
    
    case ALT:
    altPressed = true;
    break;
    
    
  }
  
  zm.keyPressed();
}

void keyReleased()
{
  switch(keyCode)
  {
    case SHIFT:
    navMode = false;
    zm.setEditMode(tmpEditMode);
    break;

    case ALT:
      altPressed = false;
      break;
  }
  
  zm.keyReleased();
}



