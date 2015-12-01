package 
{
	import flash.events.Event;
	
	/**
	 * ...
	 * @author Ben Kuper
	 */
	public class GoogleSearchEvent extends Event 
	{
		static public const IMAGES_FOUND:String = "imagesFound";
		static public const LINKS_FOUND:String = "linksFound";
		
		public var data:Array;
		
		public function GoogleSearchEvent(type:String, data:Array, bubbles:Boolean = false, cancelable:Boolean = false) 
		{ 
			super(type, bubbles, cancelable);
			this.data = data;
			
		} 
		
		public override function clone():Event 
		{ 
			return new GoogleSearchEvent(type, data, bubbles, cancelable);
		} 
		
		public override function toString():String 
		{ 
			return formatToString("GoogleSearchEvent", "type", "data", "bubbles", "cancelable", "eventPhase"); 
		}
		
	}
	
}