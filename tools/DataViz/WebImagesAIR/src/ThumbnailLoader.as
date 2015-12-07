package 
{
	import com.greensock.TweenLite;
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.net.URLRequest;
	import flash.utils.getTimer;
	
	/**
	 * ...
	 * @author Ben Kuper
	 */
	public class ThumbnailLoader extends Sprite 
	{
		private var loader:Loader;
		private var targetSize:Number = 200;
		private var _loadFactor:Number;
		private var color:Number;
		
		private var useTBUrl:Boolean = true;
		private var data:Object;
		
		public function ThumbnailLoader(data:Object) 
		{
			super();
			this.data = data;
			
			color =  Math.random() * 0xffffff;
			loadFactor = 0;
			loader = new Loader();
			addChild(loader);
			
			var url:String = useTBUrl?data.image.thumbnailLink:data.link;
			
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loaderComplete);
			loader.load(new URLRequest(url));
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, loaderError);
			loader.contentLoaderInfo.addEventListener(ProgressEvent.PROGRESS, loaderProgress);
		}
		
		private function loaderProgress(e:ProgressEvent):void 
		{
			loadFactor = Math.min(e.bytesLoaded / e.bytesTotal,1);
		}
		
		private function draw():void 
		{
			
			var radius:Number = loadFactor * targetSize / 2;
			graphics.clear();
			if (radius <= 0 ||isNaN(radius)) return;
			
			graphics.beginFill(color);
			graphics.drawCircle(0, 0,radius);
			graphics.endFill();
		}
		
		
		private function loaderComplete(e:Event):void 
		{
			loader.content.x = -loader.content.width / 2;
			loader.content.y = -loader.content.height / 2;
			
			if (loader.width > loader.height)
			{
				loader.width = targetSize;
				loader.scaleY = loader.scaleX;
			}else
			{
				loader.height = targetSize;
				loader.scaleX = loader.scaleY;
			}
			
			Bitmap(loader.content).smoothing = true;
			TweenLite.from(loader, .3, {delay:.2, scaleX:0, scaleY:0, onComplete:graphics.clear } );
			TweenLite.to(this, .3, {loadFactor:0 } );
		}
		
		private function loaderError(e:IOErrorEvent):void 
		{
			trace("loaderError",e);
		}
		
		
		public function clean():void 
		{
			loader.contentLoaderInfo.removeEventListener(Event.COMPLETE, loaderComplete);
			loader.contentLoaderInfo.removeEventListener(ProgressEvent.PROGRESS, loaderProgress);
			removeChild(loader);
		}
		
		public function get loadFactor():Number 
		{
			return _loadFactor;
		}
		
		public function set loadFactor(value:Number):void 
		{
			_loadFactor = value;
			draw();
		}
		
	}

}