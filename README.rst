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

Note: the plugin should be loaded non-lazily.

At the moment it is a fire-and-forget standard queue. 
We will add in other queue mechanisms shortly, and add in error
handeling, such as logging, and error queues.

Shameless Company Plug:
I wrote this while at Tangent-Snowball, as part of Labs, tangential to a project.
I was given permission to build this and open this.
http://www.tangentlabs.co.uk/
 
Thanks Guys.

