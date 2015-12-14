import SimpleOpenNI.*;
import java.util.ArrayList;

import oscP5.*;
import netP5.*;

Kinect[] kinects;
ZoneManager zm;
OSCManager oscManager;

//View navigation
float distance = 3000;
boolean viewDragging;
boolean viewRotating;
PVector dragOffset;
PVector rotateOffset;
PVector initMousePos;

boolean navMode;
boolean tmpEditMode;
boolean kinectCalibMode;

//must be general
int precisionSteps = 10;
int precisionFactor;
boolean altPressed;
boolean shiftPressed;

boolean zonesAreExclusives = false;

boolean noDraw= false;

boolean showHelp = false;

String[] configLines;

void setup()
{
  configLines = loadStrings("config.txt");
  precisionSteps = parseInt(configLines[1]);
  precisionFactor = precisionSteps*precisionSteps;
  
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
  
  //draw after calib
  
  float camZ = ((height/2.0) / tan(PI*60.0/360.0));
  perspective(PI/3.0, width/height, camZ/10f, camZ*500f) ;
  
  
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
  perspective();
  textSize(12);
  pushStyle();
  
  if(navMode) fill(255,255,0);
   else fill(200);
   text("Navigation Mode (Hold SHIFT)",10,30);
   
   if(zm.editMode) fill(255,255,0);
   else fill(200);
   text("Zone Mode (Z to toggle)",10,60);
   
   textSize(16);
   if(showHelp)
   {
     text("Help (H to toggle) :\n"
          +"Navigation : Shift + click gauche pour tourner, Shift + click droit pour se deplacer\n"
          +"Edition de zone (Z pour alterner):\n"
          +" > Shift enfoncé active temporairement la navigation\n"
          +" > Tab pour changer de zone selectionnée\n"
          +" > Click gauche pour déplacer sur le plan horizontal, Clic droit pour déplacer en hauteur\n"
          +" > Alt+click gauche pour retailler XZ, click droit PUIS Alt enfoncé pour retailler hauteur (/!\\ Alt après le clic)\n"
          +" > Touches T et Y pour changer le seuil d'activation de la zone (texte au dessus de la zone = points actifs / seuil)\n"
          +"Calibration Kinects (C pour alterner):\n"
          +" > Déplacer les 3 points par caméra: violet = origine, rouge = axe X, bleu = axe Z\n"
          +" > flèches haut, bas, gauche, droite, pour déplacer l'origine de la 1ere kinect (+shit pour déplacer plus vite)\n"
          +"Touches / et * changer la résolution de la kinect (les seuils changent proportionnellement)\n"
          +"Enregistrement : S pour sauver la configuration, L pour recharger\n"
       ,10,150);   
   }else
   {
     text("Help (H to toggle)",10,150);   
   }

 
  text("Params :",0,height-100);
  if(zonesAreExclusives) fill(255,255,0);
   else fill(200);
  text("Zones Are Exclusives (E to toggle)",10,height-70);
  text("Precision steps :"+precisionSteps,10,height-50);
  text("FrameRate : "+frameRate,10,height-30);
  popStyle();
  
  
    
}

void saveConfig()
{
  configLines[1] = ""+precisionSteps;
  saveStrings("data/config.txt",configLines);
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
    
    case 'h':
      showHelp = !showHelp;
      break;
      
    case 'c':
    for(Kinect k:kinects) k.calib.calibMode = !k.calib.calibMode;
    kinectCalibMode = !kinectCalibMode;
    break;
    
    case 'r':
      for(Kinect k:kinects) k.calib.resetCalibration();
      break;
    
    case 'z':
    zm.setEditMode(!zm.editMode);
    break;
    
    /*
    case '+':
    zm.addZone();
    break;
    
    case '-':
    zm.removeLastZone();
    break;
    */
   
    
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
    saveConfig();
    break;
    
    case '*':
    precisionSteps--;
    precisionFactor = precisionSteps*precisionSteps;
    break;
    
    case '/':
    precisionSteps++;
    precisionFactor = precisionSteps*precisionSteps;
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
    if(!shiftPressed)
    {
      shiftPressed = true;
      navMode = true;
      tmpEditMode = zm.editMode;
      zm.setEditMode(false);
    }
    break;
    
    case ALT:
    altPressed = true;
    break;
  }
  
  zm.keyPressed();
  
  for(Kinect k : kinects) k.keyPressed();
  
}

void keyReleased()
{
  switch(keyCode)
  {
    case SHIFT:
    shiftPressed = false;
    navMode = false;
    println("Shift released, tmpEditMode ?"+tmpEditMode);
    zm.setEditMode(tmpEditMode);
    break;

    case ALT:
      altPressed = false;
      break;
  }
  
  zm.keyReleased();
  for(Kinect k : kinects) k.keyReleased();
}



