package 
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	/**
	 * ...
	 * @author ...
	 */
	public class GoogleSearchClient extends EventDispatcher
	{
		
		//public var apiKey:String = "AIzaSyBzgCg7v-tfVZ4-iEx7ZjDALsnKr06wGzU";     
		//Ben 
		public var apiKey:String = "AIzaSyB66WulAs9UwZSNgE8D8HCajLnLZe2A4a0";
		private var cx:String = "005158438798295237180:f929is-f6ic"; // cx value is the custom search engine value got from https://cse.google.com/cse(if not created then create it).
		
		public var baseLinksAPIURL:String = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		public var baseImageAPIURL:String = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&searchType=image&imgSize=medium&fileType=jpeg&alt=json&num=10&start=1&q=";
		
		//private var params = {};
        //params.num = 10; // integer value range between 1 to 10 including
        //params.start = start; // integer value range between 1 to 101, it is like the offset
        //params.imgSize = "medium"; // for image size
        //params.searchType = "image"; // compulsory 
        //params.fileType = "jpg"; // you can leave these if extension does not matters you
        
		private var linksLoaders:Vector.<URLLoader>;
		private var imgLoaders:Vector.<URLLoader>;
		
		private var numPages:int = 1; //X pages * 8 result per page
		
		public function GoogleSearchClient() 
		{
			linksLoaders = new Vector.<URLLoader>();
			imgLoaders = new Vector.<URLLoader>();
			
			for (var i:int = 0; i < numPages; i++)
			{
				var linkL:URLLoader = new URLLoader();
				linkL.addEventListener(Event.COMPLETE, linksLoaderComplete);
				linksLoaders.push(linkL);
				
				var imgL:URLLoader = new URLLoader();
				imgL.addEventListener(Event.COMPLETE, imgLoaderComplete);
				imgLoaders.push(imgL);
			}
			
		}
		
		//LINKS
		public function searchLinks(search:String):void
		{
			
		}
		
		private function linksLoaderComplete(e:Event):void 
		{
			
		}
		
		//IMAGES
		public function searchImages(search:String):void 
		{
			var searchBaseURL:String = baseImageAPIURL + search.replace(" ", "%20");
			for (var i:int = 0; i < numPages; i++)
			{
				var sURL:String = searchBaseURL + "&start=" + (i * 8);
				trace("search URL :" +sURL);
				imgLoaders[i].load(new URLRequest(sURL));
			}
		}
		
		private function imgLoaderComplete(e:Event):void 
		{
			
			var data:Object = JSON.parse((e.target as URLLoader).data);
			trace(data);
			var results:Array = data.items;
			
			
			/* var urlArray:Array = new Array();
			
			for each(var r:Object in results) 
			{
				for (var name:String in r) 
				{
					trace(name+" > " + r[name]);
				}
				urlArray.push(r);
			}
			*/
			dispatchEvent(new GoogleSearchEvent(GoogleSearchEvent.IMAGES_FOUND, results));
		}
		

		
	}

}