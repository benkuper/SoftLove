public class ZoneManager
{
  public ArrayList<Zone> zones;
  color[] colors = new color[] {
    color(255, 0, 0), color(0, 255, 0), color(0, 0, 255), color(255, 0, 255), color(0, 255, 255)
  };
  
  public int editingZoneIndex;
  public boolean editMode;
  
  public ZoneManager()
  {
    editingZoneIndex = 0;
  }

  public void draw()
  {
    for (Zone z : zones) z.draw();
  }

  public void loadZones()
  {
    
    String[] zoneDefs = loadStrings("zones.txt");
    zones = new ArrayList<Zone>();
    int index = 0;
    for (String zoneD : zoneDefs)
    {
      String[] zSplit = zoneD.split(";");
      String zName = zSplit[0];
      PVector zBase = new PVector(parseFloat(zSplit[1]), parseFloat(zSplit[2]), parseFloat(zSplit[3]));
      PVector zSize = new PVector(parseFloat(zSplit[4]), parseFloat(zSplit[5]), parseFloat(zSplit[6]));
      int zThreshold = parseInt(zSplit[7]);
      color zCol = colors[index%colors.length];

      zones.add(new Zone(this, zName, zBase, zSize, zThreshold, zCol));

      index++;
    }
    
    setEditMode(false);
  }
  
  public void saveZones()
  {
   String[] zoneStrings = new String[zones.size()];
  for(int i =0;i<zones.size();i++) zoneStrings[i] = zones.get(i).getString();
  saveStrings("data/zones.txt",zoneStrings);
  println("Zones saved");
  }
  
  public void addZone()
  {
    zones.add(new Zone(this, "Zone "+(zones.size()+1), new PVector(0,100,0), new PVector(100,100,100), 300, colors[zones.size()%colors.length]));

  }
  
  public void removeLastZone()
  {
    if(zones.size() > 1) zones.remove(zones.size()-1);
  }


  public void update()
  {
    for (Zone z : zones) z.update();
  }



  public boolean checkPoint(PVector p)
  {
    boolean result = false;
    for (Zone z : zones) 
    {
      if (z.checkPoint(p)) 
     {
       result = true;
       if(zonesAreExclusives) break;
     }
    }

    return result;
  }

  public void checkActiveZones()
  {
    for (Zone z : zones) z.checkActive();
  }
  
  public void setEditMode(boolean val)
  {
    editMode = val;
    zones.get(editingZoneIndex).editing = editMode;
  }
  
  public void nextEditZone()
  {
    if(editMode)
    {
      zones.get(editingZoneIndex).editing = false;
      editingZoneIndex = (editingZoneIndex+1)%zones.size();
     zones.get(editingZoneIndex).editing = true;
    }
  }
  
  public void zoneActiveChanged(Zone zone)
  {
    println("Zone activation changed.");
    oscManager.sendZoneChange(zone);
  }
    
  public void zoneAveragePointMoved(Zone zone)
  {
    oscManager.sendZonePointMoved(zone); 
  }
  
  public void mousePressed()
  {
    if(editMode)  zones.get(editingZoneIndex).mousePressed();
    
  }
  
  public void mouseReleased()
  {
    if(editMode)  zones.get(editingZoneIndex).mouseReleased();
  }
  
  public void keyPressed()
  {
    if(editMode) zones.get(editingZoneIndex).keyPressed();
  }
  
  public void keyReleased()
  {
     if(editMode) zones.get(editingZoneIndex).keyReleased(); 
  }
}


public class Zone
{
  public String name;
  public PVector base;
  public PVector size;
  public PVector midSize;
  
  int rawThreshold;
  public int threshold;
  public int pointCount;

  public color zoneColor;
  ZoneManager zm;

  public boolean isActive;
  
  public boolean editing;
  boolean manipXZ;
  boolean manipY;
 
  PVector dragFactor;
  
  PVector averageZonePoint;
  PVector averageZonePointNormalized;
  
  public Zone(ZoneManager zm, String name, PVector base, PVector size, int threshold, color zoneColor)
  {
    this.name = name;
    this.base = base;
    this.size = size;
    this.midSize = PVector.mult(size,.5f);
    rawThreshold = threshold;
    this.threshold = rawThreshold/precisionFactor; // depends on stepping (square proportional)
    this.zoneColor= zoneColor;
    this.zm = zm;
    
    //println("new Zone, threshold :"+threshold);
    isActive = false;

    averageZonePoint = new PVector();
    averageZonePointNormalized = new PVector();
    
    dragFactor = new PVector();
  }

  public void draw()
  {
    
    if(editing)
    {
      if(manipXZ)
      {
         if(altPressed)
         {
           size.x += mouseX-initMousePos.x;
           size.z -= mouseY-initMousePos.y;
         }else
         {
            base.x += mouseX-initMousePos.x;
            base.z -= mouseY-initMousePos.y;
         }
         
         initMousePos.set(mouseX,mouseY);
      }
      
      if(manipY)
      {
         if(altPressed)  //APPUYER SUR ALT APRES LE CLIC DROIT 
         {
           size.y -= mouseY-initMousePos.y;
         }
         else  base.y -= mouseY-initMousePos.y; 
         initMousePos.set(mouseX,mouseY);
      }

     midSize = PVector.mult(size,.5f);
     
     base.x += dragFactor.x;
     base.y += dragFactor.y;
     base.z += dragFactor.z;      
    }
    
    pushMatrix();
    pushStyle();

    if(editing) fill(255,255,0,isActive?150:50);
    else fill(zoneColor, isActive?200:50);
    
    translate(base.x, base.y, base.z);
    box(size.x, size.y, size.z);
    fill(zoneColor);
    rotateX(PI);
    text(name+" : "+pointCount, 0, -size.y/2-40, 0);
    popStyle();
    popMatrix();
  }

  public void update()
  {
    pointCount = 0;
    averageZonePoint = new PVector();
  }

  public boolean checkPoint(PVector p)
  {
    PVector relP = PVector.sub(p,base);
    boolean result = relP.x > -midSize.x && relP.y > -midSize.y && relP.z > -midSize.z
      && relP.x < midSize.x && relP.y < midSize.y && relP.z < midSize.z;

    if (result) 
    {
      pointCount++;
      averageZonePoint.add(relP);
    }
    
    return result;
  }

  public void checkActive()
  {
    
    boolean newActive = pointCount > threshold;
    averageZonePoint.div(pointCount);
    averageZonePointNormalized.set(averageZonePoint.x/size.x,
                                  averageZonePoint.y / size.y,
                                  averageZonePoint.z / size.z);
    if (newActive != isActive)
    {
      isActive = newActive;
      zm.zoneActiveChanged(this);
    }
    
    if(isActive)
    {
      println(averageZonePoint);
      pushMatrix();
      translate(base.x,base.y,base.z);
      translate(averageZonePoint.x,averageZonePoint.y,averageZonePoint.z);
      pushStyle();
      fill(255);
      stroke(255,255,0);
      strokeWeight(1);
      sphere(20);
      popStyle();
      popMatrix();
      zm.zoneAveragePointMoved(this); 
    }
  }
  
  void mousePressed()
  {    
    if(editing)
    {
      if (mouseButton == RIGHT)
      {
        manipY = true;
        initMousePos = new PVector(mouseX,mouseY);
      }else if(mouseButton == LEFT) 
      {
        manipXZ = true;
        initMousePos = new PVector(mouseX,mouseY);
      }
      
    }
  }

  void mouseReleased()
  {
    manipXZ = false;
    manipY = false;
  }
  
  public void keyPressed()
  {
    
    if(editing)
    {
      switch(keyCode)
      {
        
        case UP:
        if(altPressed) dragFactor.y = 10;
        else dragFactor.z = 10;
        break;
        
        case DOWN:
        if(altPressed) dragFactor.y = -10;
        else dragFactor.z = -10;
        break;
        
        case LEFT:
        dragFactor.x = -10;
        break;
        
        case RIGHT:
        dragFactor.x = 10;
        break;
      }
    }
  }
  
  public void keyReleased()
  {
     dragFactor.set(0,0,0);
  }
  
  public String getString()
  {
    String[] props = new String[]{name,str(base.x),str(base.y),str(base.z),str(size.x),str(size.y),str(size.z),str(rawThreshold)};
    return join(props,';');
  }
}

