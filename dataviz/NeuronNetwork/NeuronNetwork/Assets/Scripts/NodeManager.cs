using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using DG.Tweening;

public class NodeManager : MonoBehaviour {

    CamControl cam;
    public GameObject nodePrefab;
    public GameObject connectionPrefab;
    List<Node> nodes;
    List<Connection> connections;
    List<Connection> focusConnections;

    Transform nodeContainer;
    Transform connectionContainer;

    int numStartNodes = 20;
    float connectionProba = .8f;


    Node currentNode;

    DataText dt;
    
	// Use this for initialization
	void Start () {
        cam = Camera.main.GetComponent<CamControl>();
        nodes = new List<Node>();
        connections = new List<Connection>();

        nodeContainer = transform.FindChild("nodes");
        connectionContainer = transform.FindChild("connections");

        dt = GetComponentInChildren<DataText>();

        for (int i = 0; i < numStartNodes; i++)
        {
            addNode();
        }

		OSCMaster.init ();
		OSCMaster.pingReceived +=  pingReceived;
	}

	void pingReceived()
	{
		Debug.Log ("ping Received, focus Random");
		focusRandomNode ();
	}

    // Update is called once per frame
    void Update()
    {

    }

    void OnGUI()
    {
        Event e = Event.current;
        if (e.type == EventType.KeyDown)
        {
            switch (e.keyCode)
            {
                case KeyCode.Space:
                    focusRandomNode();
                    break;
            }
        }
    }


    //Navigation
    Node focusRandomNode()
    {
        Node rn = nodes[Random.Range(0,nodes.Count-1)];
        focusNode(rn);
        return rn;
    }

    void focusNode(Node node)
    {
        if (currentNode != null)
        {
            currentNode.setFocus(false);
            List<Connection> oldC = getConnectionsForNode(currentNode);
            foreach (Connection oc in oldC) oc.setActive(false);

            dt.setEnabled(false);
        }

        cam.focus(transform,20);
        currentNode = node;
        removeConnectionsForNode(currentNode);
        Invoke("lightFocus",1f);
        

    }

    void lightFocus()
    {
        currentNode.setFocus(true);
        addConnectionsForNode(currentNode);
        Invoke("zoomFocus", 1f);
    }

    void zoomFocus()
    {
        dt.setEnabled(true);
        //dt.transform.LookAt(cam.transform);
        dt.transform.position = currentNode.transform.position;

        cam.focus(currentNode.transform, 5);
    }




    //Create / Remove
    void addNode()
    {
        Node n = ((GameObject)GameObject.Instantiate(nodePrefab,Random.insideUnitSphere*10,Quaternion.identity)).GetComponent<Node>();
        n.transform.parent = nodeContainer;
        n.transform.localScale = Vector3.zero;
        n.transform.DOScale(.5f, .5f);

        foreach (Node n2 in nodes)
        {
            if (Random.value < connectionProba) addConnection(n, n2); 
        }

        nodes.Add(n);
    }

    void removeNode(Node n)
    {

        removeConnectionsForNode(n);

        nodes.Remove(n);
        GameObject.Destroy(n.gameObject);
    }

    void addConnection(Node n1, Node n2, bool active = false)
    {
        Connection c = ((GameObject)GameObject.Instantiate(connectionPrefab,Random.insideUnitSphere*10,Quaternion.identity)).GetComponent<Connection>();
        c.transform.parent = connectionContainer;
        c.setNodes(n1, n2);
        c.setActive(active);

        connections.Add(c);
    }

    void removeConnection(Connection c)
    {
        connections.Remove(c);
        c.clean();
        GameObject.Destroy(c.gameObject,1);
    }

    void addConnectionsForNode(Node n)
    {
        foreach (Node n2 in nodes)
        {
            if (n2 == n) continue;
            if (Random.value < connectionProba) addConnection(n, n2,true);
        }
    }

    void removeConnectionsForNode(Node n)
    {
        List<Connection> connectionsToRemove = getConnectionsForNode(n);

        foreach (Connection c2 in connectionsToRemove)
        {
            removeConnection(c2);
        }
    }

    List<Connection> getConnectionsForNode(Node n)
    {
        List<Connection> cList = new List<Connection>();

        foreach (Connection c in connections)
        {
            if (c.hasNode(n)) cList.Add(c);
        }

        return cList;
    }

   
}
