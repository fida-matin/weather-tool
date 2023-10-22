JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  server/AggregationServer.java \
		  client/GETClient.java \
		  content/ContentServer.java \
		  HTTP/HTTPClient.java \
		  HTTP/HTTPConnection.java \
		  HTTP/HTTPServer.java \
		  HTTP/messages/HTTPMessage.java \
		  HTTP/messages/HTTPRequest.java \
		  HTTP/messages/HTTPResponse.java \
		  util/JSONObject.java \
		#   test/AggregationServerTest.java \
		#   test/ContentServerTest.java \
		#   test/GETClientTest.java \
		#   test/IntegrationTests.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) server/*.class
	$(RM) client/*.class
	$(RM) content/*.class
	$(RM) HTTP/*.class
	$(RM) HTTP/messages/*.class
	$(RM) util/*.class
	$(RM) test/*.class