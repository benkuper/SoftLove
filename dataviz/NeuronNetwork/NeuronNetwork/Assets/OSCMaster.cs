using UnityEngine;
using System.Collections;
using UnityOSC;

public class OSCMaster : MonoBehaviour {
	
	static OSCServer server;

	public delegate void PingReceived();
	#region Delegates
	public static event PingReceived pingReceived;
	#endregion

	static bool isInit;
	public static OSCMaster instance;

	static public void init()
	{
		if (isInit)
			return;

		server = new OSCServer (9010);
		server.PacketReceivedEvent += HandlePacketReceivedEvent;
		isInit = true;
	}

	// Use this for initialization
	void Start () {
		init ();
		instance = this;
	}

	static void HandlePacketReceivedEvent (OSCPacket packet)
	{
		if (packet.Address.Equals ("/ping")) {
			if (pingReceived != null)
				pingReceived ();
		}
	}
	
	// Update is called once per frame
	void Update () {
		server.Update ();
	}
}
