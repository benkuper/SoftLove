package 
{
	import com.hurlant.crypto.Crypto;
	import com.hurlant.crypto.hash.HMAC;
	import com.hurlant.crypto.hash.MD5;
	import com.hurlant.math.BigInteger;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.media.Sound;
	import flash.media.SoundChannel;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	import org.tuio.connectors.UDPConnector;
	import org.tuio.osc.IOSCListener;
	import org.tuio.osc.OSCManager;
	import org.tuio.osc.OSCMessage;
	
	/**
	 * ...
	 * @author Ben Kuper
	 */
	public class MainVoxygen extends Sprite implements IOSCListener
	{
		private var loader:URLLoader;
		private var sound:Sound;
		private var channel:SoundChannel;
		
		public var user:String = "leclairobscur@gmail.com" ;
		public var passwd:String = "DrRg57k4" ;
		public var coding:String = "mp3:160-0"; //lin
		public var header:String = "headerless"; //wav-header
		
		public var oscM:OSCManager;
		
		public function MainVoxygen() 
		{
			super();
			
			oscM = new OSCManager(new UDPConnector("0.0.0.0", 6000));
			oscM.addMsgListener(this);
		}
		
		
		
		/* INTERFACE org.tuio.osc.IOSCListener */
		
		public function acceptOSCMessage(msg:OSCMessage):void 
		{
			switch(msg.address)
			{
				case "/voice":
					speech(msg.arguments[0]);
					break;
					
				case "/voice/utf8":
					var ba:ByteArray = msg.arguments[0] as ByteArray;
					ba.position = 0;
					var text:String = ba.readUTFBytes(ba.bytesAvailable);
					speech(text);
					break;
			}
		}
		
		public function speech(text:String):void
		{
			trace("Speech :" + text);
			var request:String = getRequest(text);
			//var request:String = "http://ws.voxygen.fr/tts1?coding=mp3:160-0&frequency=16000&header=headerless&text=Cool+ta+mere+encule&user=leclairobscur@gmail.com&voice=Philippe&hmac=aebfa4f77bee530af95a6a083bfb8aeb";
			sound = new Sound();
			sound.load(new URLRequest(request));			
			channel = sound.play();
		}
		
		
		
		//VOXYGEN REQUEST GENERATION
		
		
		public function getRequest(texte:String,voice:String="Philippe"):String
		{
			var url_base:String = "http://ws.voxygen.fr/tts1";
			var am:Array = 
			[
				{label:"coding", value:coding },
				{label:"frequency", value:48000 },
				{label:"header", value:header },
				{label:"text", value:texte },
				{label:"user", value:user },
				{label:"voice", value:voice }
			];
			
			// calcul hmac
			var hmac:String = calculhmac(am);
			// encode text
			
			//A VOIR
			var textEncoded:String = texte;// URLEncoder.encode(texte, "utf-8");
			// update TreeMap
			am[3].value = textEncoded; //text
			
			// create request, concat hmac at the end
			var request:String = treeMapToUrl(url_base, am);
			if (hmac != "") {
				request += "&hmac=" + hmac;
			}
			
			return request;
		}
		
		
		
		private function calculhmac(am:Array):String
		{
			var m:HMAC = new HMAC(new MD5());
			var b:ByteArray = new ByteArray();
			
			var keyB:ByteArray = new ByteArray();
			keyB.writeUTFBytes(passwd);
			keyB.position = 0;
			 
			for each(var elem:Object in am)
			{
				var s:String = elem.label + "=" + elem.value;
				b.writeUTFBytes(s);
			}
			
			b.position = 0;
			
			var hb:ByteArray = m.compute(keyB, b);
			hb.position = 0;
			var resS:BigInteger = new BigInteger(hb, 16,true);
			var result:String = resS.toString();
			
			if (result.length % 2 > 0) {
				result = "0" + result;
			}		
			return result;
		}
		
		private function treeMapToUrl(url:String , am:Array):String {
			var request:String = url + "?";
			
			for each (var elem:Object in am)
			{
				var s:String = elem.label + "=" + elem.value+"&";
				request += s;
			}
			
			request = request.slice(0, request.length - 1);
			return request;
		}
		
	}

}