package 
{
	import benkuper.util.StageUtil;
	import flash.display.Sprite;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	import org.tuio.connectors.UDPConnector;
	import org.tuio.osc.IOSCListener;
	import org.tuio.osc.OSCManager;
	import org.tuio.osc.OSCMessage;
	/**
	 * ...
	 * @author ...
	 */
	public class Main extends Sprite implements IOSCListener
	{
		
		private var gsc:GoogleSearchClient;
		private var gallery:Gallery;
		
		private var oscM:OSCManager;
		
		public function Main() 
		{
			StageUtil.init(stage);
			StageUtil.setNoScale();
			
			oscM = new OSCManager(new UDPConnector("0.0.0.0", 7777));
			oscM.addMsgListener(this);
			
			gsc = new GoogleSearchClient();
			gsc.addEventListener(GoogleSearchEvent.IMAGES_FOUND, imagesFound);
			
			stage.addEventListener(KeyboardEvent.KEY_DOWN, keyDown);
			
			gallery = new Gallery();
			addChild(gallery);
			
		}
		
		/* INTERFACE org.tuio.osc.IOSCListener */
		
		public function acceptOSCMessage(msg:OSCMessage):void 
		{
			switch(msg.address)
			{
				case "/parole":
					gallery.clear();
					gsc.searchImages(msg.arguments[0]);
					break;
			}
		}
		
		private function keyDown(e:KeyboardEvent):void 
		{
			switch(e.keyCode)
			{
				case Keyboard.A:
					gsc.searchImages("arbre");
					break;
					
				case Keyboard.B:
					gsc.searchImages("poney");
					break;
			}
		}
		
		private function imagesFound(e:GoogleSearchEvent):void 
		{
			gallery.loadImages(e.data);
		}
		
	}

}