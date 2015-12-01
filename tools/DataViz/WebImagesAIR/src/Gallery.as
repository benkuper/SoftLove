package
{
	import benkuper.util.StageUtil;
	import com.greensock.TweenLite;
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.net.URLRequest;
	import flash.ui.Keyboard;
	import org.tuio.connectors.UDPConnector;
	import org.tuio.osc.IOSCListener;
	import org.tuio.osc.OSCManager;
	import org.tuio.osc.OSCMessage;
	
	/**
	 * ...
	 * @author Ben Kuper
	 */
	public class Gallery extends Sprite
	{
		private var container:Sprite;
		
		public function Gallery() 
		{
			container  = new Sprite();
			addChild(container);
			addEventListener(Event.ADDED_TO_STAGE, addedToStage);
		}
		
		private function addedToStage(e:Event):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, addedToStage);
			stage.addEventListener(KeyboardEvent.KEY_DOWN, keyDown);
			stage.addEventListener(MouseEvent.MOUSE_WHEEL, mouseWheel);
		}
		
		private function mouseWheel(e:MouseEvent):void 
		{
			TweenLite.to(container, .3, { y:container.y+e.delta * 50 } );
		}
		
		private function keyDown(e:KeyboardEvent):void 
		{
			switch(e.keyCode)
			{
				case Keyboard.SPACE:
					clear();
					break;
			}
		}
		
		public function loadImages(data:Array):void 
		{
			for each(var imgData:Object in data) loadImage(imgData);
		}
		
		private function loadImage(data:Object):void 
		{
			var targetSize:Number = 220;
			var loader:ThumbnailLoader = new ThumbnailLoader(data);
			container.addChild(loader);
			loader.x = targetSize / 2 + (container.numChildren % 6) * targetSize;
			loader.y = targetSize / 2 + Math.floor(container.numChildren / 6) * targetSize;
		}
		
		
		
		
		
		public function clear():void
		{
			container.y = 0;
			for (var i:int = 0; i < container.numChildren; i++) (container.getChildAt(i) as ThumbnailLoader).clean();
			container.removeChildren();
		}
		
		
		
	}
	
}