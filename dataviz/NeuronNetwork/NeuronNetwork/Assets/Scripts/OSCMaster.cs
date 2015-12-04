using UnityEngine;
using System.Collections;
using UnityOSC;
using System;
using System.Net;

public class OSCMaster : MonoBehaviour {
	
	static OSCServer server;
    static OSCClient client;

    //GENERAL
	public delegate void PingReceived();
    public delegate void ConsoleReceived(bool active);
    public delegate void ClearConsoleReceived();
    public delegate void LogReceived(string text);
    public delegate void GlitchReceived(int glitchID, bool active);
    #region Delegates
    public static event PingReceived pingReceived;
    public static event ConsoleReceived consoleReceived;
    public static event ClearConsoleReceived clearConsoleReceived;
    public static event LogReceived logReceived;
	public static event GlitchReceived glitchReceived;
    #endregion

    //ZONES
    public delegate void ZoneReceived(string zoneID, bool active);
    public delegate void MainZoneReceived(string zoneID);
    public delegate void ZonePosReceived(string zoneID, Vector3 pos);
    #region Delegates
    public static event ZoneReceived zoneReceived;
    public static event MainZoneReceived mainZoneReceived;
    public static event ZonePosReceived zonePosReceived;
    #endregion

    //NEURONS
    public delegate void NeuronPulseReceived(int neuronID);
    public delegate void NeuronZoomReceived(int neuronID);
    public delegate void NodeSizeReceived(float size);
    #region Delegates
    public static event NodeSizeReceived nodeSizeReceived;
    public static event NeuronPulseReceived neuronPulseReceived;
    public static event NeuronZoomReceived neuronZoomReceived;
    #endregion

    //WEB
    public delegate void WebSearchReceived(string searchType,string search);
    #region Delegates
    public static event WebSearchReceived webSearchReceived;
    #endregion

    //BIODATA
    public delegate void HeartRateReceived(int heartRate);
    #region Delegates
    public static event HeartRateReceived heartReceived;
    #endregion

    //Emotion
    public delegate void EmotionReceived(string type, float value);
    #region Delegates
    public static event EmotionReceived emotionReceived;
    #endregion



    static bool isInit;
	public static OSCMaster instance;

	static public void init()
	{
		if (isInit)
			return;

		try{

		    server = new OSCServer (5555);
		    server.PacketReceivedEvent += HandlePacketReceivedEvent;
            isInit = true;
        }
        catch(Exception e)
		{
			Debug.LogWarning("Could not create server on port 5555.");
		}

        client = new OSCClient(IPAddress.Loopback, 7776, true);

		
	}

	// Use this for initialization
	void Awake () {
		init ();
		instance = this;
	}

    void Start()
    {
        if (isInit) DataText.log("Receiving OSC on port 5555");
        else DataText.log("Error with OSC init on port 5555");
    }

	void OnDestroy()
	{
		server.Close ();
	}


    public static void sendFloat(string address, float value)
    {
        OSCMessage msg = new OSCMessage(address);
        msg.Append<float>(value);
        client.Send(msg);
    }

	static void HandlePacketReceivedEvent (OSCPacket p)
	{
        //General
        if (p.Address.Equals("/ping"))
        {
            if (pingReceived != null) pingReceived();

        }else if (p.Address.Equals("/console/active"))
        {
            if (consoleReceived != null) consoleReceived((int)p.Data[0] == 1);
        }else if (p.Address.Equals("/console/clear"))
        {
            if (clearConsoleReceived != null) clearConsoleReceived();
        }
        else if (p.Address.Equals("/console/log"))
        {
            if (logReceived != null) logReceived((string)p.Data[0]);
        }
        else if (p.Address.Equals("/glitch"))
        {
            if(glitchReceived != null) glitchReceived((int)p.Data[0], (int)p.Data[1] == 1);
        }
        //Neuron
        else if (p.Address.Equals("/neuron/pulse"))
        {
            if (neuronPulseReceived != null) neuronPulseReceived(p.Data.Count > 0?(int)p.Data[0]:-1);
        }
        else if (p.Address.Equals("/neuron/zoom"))
        {
            if (neuronZoomReceived != null) neuronZoomReceived(p.Data.Count > 0 ? (int)p.Data[0] : -1);
        }else if(p.Address.Equals("/neuron/nodeSize"))
        {
            if (nodeSizeReceived != null) nodeSizeReceived((float)p.Data[0]);
        }
        //Web
        else if (p.Address.Equals("/web/search/images"))
        {
            if (webSearchReceived != null) webSearchReceived("images",(string)p.Data[0]);
        }
        //Zones
        else if (p.Address.Equals("/tracking/zone"))
        {
            if (zoneReceived != null) zoneReceived((string)p.Data[0], (int)p.Data[1] == 1);
        }
        else if (p.Address.Equals("/tracking/mainZone"))
        {
            if (mainZoneReceived != null) mainZoneReceived((string)p.Data[0]);
        }
        else if (p.Address.Equals("/tracking/zonePos"))
        {
            if (zonePosReceived != null) zonePosReceived((string)p.Data[0], new Vector3((float)p.Data[1], (float)p.Data[2], (float)p.Data[3]));
        }
        //Biodata
        else if (p.Address.Equals("/heart"))
        {
            if (heartReceived != null) heartReceived((int)p.Data[0]);
        }
        //Emotion
        else if(p.Address.Equals("/emotion"))
        {
            if (emotionReceived != null) emotionReceived((string)p.Data[0], (float)p.Data[1]);
        }else
        {
            DataText.log("OSC Message not handled : " + p.Address);
        }
    }
	
	// Update is called once per frame
	void Update () {
		server.Update ();
	}
}
