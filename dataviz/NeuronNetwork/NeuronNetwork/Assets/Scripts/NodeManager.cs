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

    int numStartNodes = 50;
    float connectionProba = .8f;

    public float universeSize = 3;

    Node currentNode;

    bool isInFocus;
    Vector3 camInitPos;
    
	// Use this for initialization
	void Start () {
        cam = Camera.main.GetComponent<CamControl>();
        camInitPos = cam.transform.position;

        nodes = new List<Node>();
        connections = new List<Connection>();
        focusConnections = new List<Connection>();

        nodeContainer = transform.FindChild("nodes");
        connectionContainer = transform.FindChild("connections");

        for (int i = 0; i < numStartNodes; i++)
        {
            addNode();
        }

		OSCMaster.init ();
        OSCMaster.neuronPulseReceived += neuronPulseReceived;
        OSCMaster.neuronZoomReceived += neuronZoomReceived;
        OSCMaster.nodeSizeReceived += nodeSizeReceived;
	}

    private void nodeSizeReceived(float size)
    {
        foreach (Node n in nodes) n.setBaseScale(size);
    }

    private void neuronZoomReceived(int neuronID)
    {
        activeNode(neuronID,true);
    }

    private void neuronPulseReceived(int neuronID)
    {
        activeNode(neuronID, false);
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
                    activeNode(-1,false);
                    break;

                case KeyCode.A:
                    activeNode(-1,true);
                    break;
            }
        }
    }


    //Navigation
    Node activeNode(int index,bool focus)
    {
        DataText.log("Activate node " + index + ", set focus : " + focus);

        Node rn = null;
        if (index == -1)
        {
            rn = nodes[Random.Range(0, nodes.Count - 1)];
        }
        else if(index > 0)
        {
            if (index <= nodes.Count) rn = nodes[index-1];
            else
            {
                DataText.log("Node Manager : Node " + index + "does not exists");
            }
        }else
        {
            dezoom();
        }
        
        setCurrentNode(rn);
        //dezoom();

        //CancelInvoke("zoomCurrentNode");
        //if (focus) Invoke("zoomCurrentNode", 1f);
        if (focus) zoomCurrentNode();
        else dezoom();
        return rn;
    }




    void dezoom()
    {
        cam.focus(null,0);
        isInFocus = false;
    }

    void zoomCurrentNode()
    {
        if (currentNode == null) return;
        cam.focus(currentNode.transform, 5);
        isInFocus = true;
    }


    void setCurrentNode(Node n)
    {
        if (currentNode != null)
        {
            currentNode.setFocus(false);
            List<Connection> oldC = getConnectionsForNode(currentNode);
            foreach (Connection oc in oldC) oc.setActive(false);

            removeConnectionsForNode(currentNode,true);
        }

        currentNode = n;

        if(currentNode != null)
        {
            currentNode.setFocus(true);
            addConnectionsForNode(n,true);
        }
    }
       

    //Create / Remove
    void addNode()
    {
        Node n = ((GameObject)GameObject.Instantiate(nodePrefab,Random.insideUnitSphere*universeSize,Quaternion.identity)).GetComponent<Node>();
        n.transform.parent = nodeContainer;
        n.posSpeed = Random.Range(.05f, .1f);
        n.posRadius = Random.Range(1f, 1f);
        n.rotSpeed = Random.Range(.01f, .02f);

        foreach (Node n2 in nodes)
        {
            if (Random.value < connectionProba) addConnection(n, n2); 
        }

        nodes.Add(n);
    }

    void removeNode(Node n)
    {

        removeConnectionsForNode(n,false);
        removeConnectionsForNode(n, true);

        nodes.Remove(n);
        GameObject.Destroy(n.gameObject);
    }

    void addConnection(Node n1, Node n2, bool active = false, bool focusConnection = false)
    {
        Connection c = ((GameObject)GameObject.Instantiate(connectionPrefab,Random.insideUnitSphere*10,Quaternion.identity)).GetComponent<Connection>();
        c.transform.parent = connectionContainer;
        c.setNodes(n1, n2);
        c.setActive(active);

        if (focusConnection) focusConnections.Add(c);
        else connections.Add(c);
    }

    void removeConnection(Connection c)
    {
        connections.Remove(c);
        focusConnections.Remove(c);

        c.clean();
        GameObject.Destroy(c.gameObject,1);
    }

    void addConnectionsForNode(Node n,bool focusConnection)
    {
        foreach (Node n2 in nodes)
        {
            if (n2 == n) continue;
            if (Random.value < connectionProba) addConnection(n, n2,true,focusConnection);
        }
    }

    void removeConnectionsForNode(Node n, bool focusConnection)
    {
        List<Connection> connectionsToRemove = getConnectionsForNode(n,focusConnection);

        foreach (Connection c2 in connectionsToRemove)
        {
            removeConnection(c2);
        }
    }

    List<Connection> getConnectionsForNode(Node n, bool focusConnection = false)
    {
        List<Connection> cList = new List<Connection>();

        List<Connection> sourceConnections = focusConnection?focusConnections:connections;
        foreach (Connection c in sourceConnections)
        {
            if (c.hasNode(n)) cList.Add(c);
        }

        return cList;
    }

   
}
