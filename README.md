# wss://AutobahnAndroid

**wss://AutobahnAndroid** | A fork of [AutoBahnAndroid project] (https://github.com/crossbario/autobahn-android) to support SSL over NIO. 

* **[The WebSocket Protocol](http://tools.ietf.org/html/rfc6455)**

WebSocket allows [bidirectional real-time messaging on the Web](http://tavendo.com/blog/post/websocket-why-what-can-i-use-it/).

## Show me some code

Here is a simple Secure WebSocket echo client:

```java
private final WebSocketConnection mConnection = new WebSocketConnection();

private void start() {

   final String wsuri = "wss://echo.websocket.org";

   try {
      mConnection.connect(wsuri, new WebSocketHandler() {

         @Override
         public void onOpen() {
            Log.d(TAG, "Status: Connected to " + wsuri);
            mConnection.sendTextMessage("Hello, world!");
         }

         @Override
         public void onTextMessage(String payload) {
            Log.d(TAG, "Got echo: " + payload);
         }

         @Override
         public void onClose(int code, String reason) {
            Log.d(TAG, "Connection lost.");
         }
      });
   } catch (WebSocketException e) {

      Log.d(TAG, e.toString());
   }
}
```

... and with a custom TrustManager :

```java
private final WebSocketConnection mConnection = new WebSocketConnection();

/**  
 * Trust all certificates by default; useful for self-signed certificates and testing; 
 * DO NOT USE FOR PRODUCTION CODE  
 *  
 * @return  
 */ 
public final TrustManager[] createTrustAllManager() {
	TrustManager[] trustAllCerts = null;     
	if (BuildConfig.DEBUG) {          
		System.err.println("*** TRUSTING ALL CERTIFICATES ***");          
		trustAllCerts = new TrustManager[] {                 
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					X509Certificate[] myTrustedAnchors = new X509Certificate[0];
					return myTrustedAnchors;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
		};
	} else {
		throw new RuntimeException(new CertificateException("\n\n***TRUST ALL CERTIFICATES DISABLED IN NON-DEBUG MODE ***\n\n"));     
	}      
	return trustAllCerts; 
}

private void start() {

   final String wsuri = "wss://echo.websocket.org";
   final WebSocketSSLContext webSocketSSLContext = new WebSocketSSLContext();

   try {
      webSocketSSLContext.setSSLTrustManagers(createTrustAllManager());
      mConnection.setWebSocketSSLContext(webSocketSSLContext);    
      mConnection.connect(wsuri, new WebSocketHandler() {

         @Override
         public void onOpen() {
            Log.d(TAG, "Status: Connected to " + wsuri);
            mConnection.sendTextMessage("Hello, world!");
         }

         @Override
         public void onTextMessage(String payload) {
            Log.d(TAG, "Got echo: " + payload);
         }

         @Override
         public void onClose(int code, String reason) {
            Log.d(TAG, "Connection lost.");
         }
      });
   } catch (WebSocketException e) {

      Log.d(TAG, e.toString());
   }
}
```

## Features

* library for WebSocket and Secure-WebSocket clients
* implements WebSocket RFC6455, Draft Hybi-10+ 
* works on Android 2.2+
* very good standards conformance
* high-performance asynchronous design
* easy to use API
* seamless integration in Android UI apps
* no (really none) network activity on UI thread
* Open-source (Apache 2 license)

You can use wss://AutobahnAndroid to create native Android apps talking to WebSocket servers.

## More Information

For more information, take a look at the [project documentation](http://autobahn.ws/android). This provides:

* [AutobahnAndroid a quick 'Getting Started'](http://autobahn.ws/android/gettingstarted.html)
* [AutobahnAndroid a list of all examples in this repo](http://autobahn.ws/android/examples.html)
* [Autobahn a full API reference](http://autobahn.ws/python/packages.html)



