using UnityEngine;
using System.Collections;
using System;

public class DataVizWeb : MonoBehaviour {

    ImagePlane[] planes;

 

    const string apiKey = "AIzaSyBzgCg7v-tfVZ4-iEx7ZjDALsnKr06wGzU";
    const string cx = "002534967567206735624:20dlqzadhkw";

    string baseImageAPIURL = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&searchType=image&imgSize=medium&fileType=jpeg&alt=json&num=4&start=1&q=";
		
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
        //DataText.log("Searching images for : " + search);
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
            JSONObject results = json.GetField("items");
            int i = 0;
            Debug.Log(results.Count);
            foreach(JSONObject j in results.list)
            {
                string url = j.GetField("link").str;
                planes[i].setImageURL(url);
                i++;
            }
        }catch(Exception e)
        {
            Debug.Log(e);//
            //DataText.log("Error retrieving result ("+e.Message+"), www data :" + www.text);
        }
    }
}
