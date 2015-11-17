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

    Transform nodeContainer;
    Transform connectionContainer;

    int numStartNodes = 20;
    float connectionProba = .1f;


    Node currentNode;
    
	// Use this for initialization
	void Start () {
        cam = Camera.main.GetComponent<CamControl>();
        nodes = new List<Node>();
        connections = new List<Connection>();

        nodeContainer = transform.FindChild("nodes");
        connectionContainer = transform.FindChild("connections");


        for (int i = 0; i < numStartNodes; i++)
        {
            addNode();
        }
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
                    Node n = focusRandomNode();
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
            
        }

        cam.focus(node.transform);
        node.setFocus(true);
        currentNode = node;

        updateFocusConnections();


    }

    //
    void updateFocusConnections()
    {
        foreach (Connection c in connections)
        {
            c.setActive(c.hasNode(currentNode));
        }
    }


    //Create / Remove
    void addNode()
    {
        Node n = ((GameObject)GameObject.Instantiate(nodePrefab,Random.insideUnitSphere*10,Quaternion.identity)).GetComponent<Node>();
        n.transform.parent = nodeContainer;
        n.transform.localScale = Vector3.zero;
        n.transform.DOScale(.1f, .5f);

        foreach (Node n2 in nodes)
        {
            if (Random.value < connectionProba) addConnection(n, n2); 
        }

        nodes.Add(n);
    }

    void removeNode(Node n)
    {

        List<Connection> connectionsToRemove = getConnectionsForNode(n);

        foreach (Connection c2 in connectionsToRemove)
        {
            removeConnection(c2);
        }

        nodes.Remove(n);
        GameObject.Destroy(n.gameObject);
    }

    void addConnection(Node n1, Node n2)
    {
        Connection c = ((GameObject)GameObject.Instantiate(connectionPrefab,Random.insideUnitSphere*10,Quaternion.identity)).GetComponent<Connection>();
        c.transform.parent = connectionContainer;
        c.setNodes(n1, n2);

        connections.Add(c);
    }

    void removeConnection(Connection c)
    {
        connections.Remove(c);
        GameObject.Destroy(c.gameObject);
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
