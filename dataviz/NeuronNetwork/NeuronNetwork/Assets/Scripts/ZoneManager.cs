using UnityEngine;
using System.Collections;

public class ZoneManager : MonoBehaviour {

    Zone[] zones;
    Zone mainZone;
    // Use this for initialization
    void Start () {
        OSCMaster.zoneReceived += zoneReceived;
        OSCMaster.mainZoneReceived += mainZoneReceived;
        OSCMaster.zonePosReceived += zonePosReceived;

        zones = GetComponentsInChildren<Zone>();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    void zoneReceived(string zoneID, bool active)
    {
        Zone z = getZoneByID(zoneID);
        if (z == null) return;
        z.setActive(active);
    }

    void mainZoneReceived(string zoneID)
    {
       
    }

    void setMainZone(Zone z)
    {
        
       // if (mainZone != null) mainZone.setActive(false);
       mainZone = z;
       //if (mainZone != null) mainZone.setActive(true);

    }

    void zonePosReceived(string zoneID, Vector3 pos)
    {
        //Debug.Log("Zone Pos Received : " + zoneID + " > " + pos);
        Zone z = getZoneByID(zoneID);
        if (z == null) return;
        z.setAimPos(pos);
    }

    Zone getZoneByID(string id)
    {
        foreach(Zone z in zones)
        {
            if (z.zoneID == id) return z;
        }

        return null;
    }
}
