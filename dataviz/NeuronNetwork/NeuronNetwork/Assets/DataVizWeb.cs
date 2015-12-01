using UnityEngine;
using System.Collections;
using System;

public class DataVizWeb : MonoBehaviour {

    ImagePlane[] planes;


    //string apiKey = "AIzaSyBzgCg7v-tfVZ4-iEx7ZjDALsnKr06wGzU";
	//string baseLinksAPIURL = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	string baseImageAPIURL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";

    void Start()
    {
        OSCMaster.webSearchReceived += webSearchReceived;
        planes = GetComponentsInChildren<ImagePlane>();
    }

   

    // Update is called once per frame
    void Update()
    {


    }

    void OnGUI()
    {
        Event e = Event.current;
        switch(e.type)
        {
            case EventType.KeyDown:
                switch(e.keyCode)
                {
                    case KeyCode.Keypad1:
                        searchImages("poney");
                        break;

                    case KeyCode.Keypad2:
                        searchImages("very Cool");
                        break;

                    case KeyCode.Keypad3:
                        searchImages("j'ai soif");
                        break;

                }
                break;
        }
    }


    private void webSearchReceived(string searchType, string search)
    {
        if (searchType.Equals("images"))
        {
            searchImages(search);
        }
    }



    void searchImages(string search)
    {
        DataText.log("Searching images for : " + search);
        string searchURL = baseImageAPIURL + search.Replace(" ","%20");
        StartCoroutine("searchImagesCoroutine", searchURL);
    }

    IEnumerator searchImagesCoroutine(string searchURL)
    {
        WWW www = new WWW(searchURL);
        yield return www;

        try
        { 
            JSONObject json = JSONObject.Create(www.text);
            JSONObject results = json.GetField("responseData").GetField("results");
            int i = 0;
            foreach(JSONObject j in results.list)
            {
                string url = j.GetField("url").str;
                planes[i].setImageURL(url);
                i++;
            }
        }catch(Exception e)
        {
            DataText.log("Error retrieving result ("+e.Message+"), www data :" + www.text);
        }
    }
}
