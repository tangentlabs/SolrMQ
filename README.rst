SolrMQ
======

SolrMQ is a plugin for Solr that allows you to send updates to Solr using a AMQP messaging queue.
We use the RabbitMQ library.

It makes use of the current Solr update plugins and allows you to specify the Queue name and Update handler.

Sample SolrConfig config:::

	<requestHandler name="/mq" class="org.apache.solr.handler.ext.SolrMessageQueue" >
      	   <str name="messageQueueHost">localhost</str>
           <str name="queue">solrmq</str>
	   <str name="updateHandlerName">/update</str>
	</requestHandler>
	
You will need to put the ./bin/plugin-solr-mq.jar and the ./lib/rabbitmq-client.jar 
into the solr lib directory, or load it via the solrconfig.xml.

Note: the plugin should not be loaded lazily, and neither should the update handler.

At the moment it is a fire-and-forget standard queue. 
We will add in other queue mechanisms shortly, and add in error
handeling, such as logging, and error queues.

You can check the develop branch for this.
 

