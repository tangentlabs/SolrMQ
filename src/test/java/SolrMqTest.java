import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.ext.SolrMessageQueue;
import org.apache.solr.handler.ext.exceptions.ResponseFailedException;
import org.apache.solr.handler.ext.exceptions.SolrMqException;
import org.apache.solr.handler.ext.exceptions.UpdateFailedException;
import org.apache.solr.handler.ext.worker.QueueListenerThread;
import org.apache.solr.handler.ext.worker.QueueUpdateWorker;
import org.apache.solr.handler.ext.worker.UpdateWorkerFactory;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.mq.wrapper.IConnectionFactoryWrapper;
import org.apache.solr.mq.wrapper.IConnectionWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.solrcore.wrapper.SolrCoreWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import static org.mockito.Mockito.*;


public class SolrMqTest {
	boolean hasRun = false;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Attempt to test this without full solr or rabbit implementation. 
	 * Really, really unpleasant.
	 */
	@Test
	public void test() {
		//better way to test
		//1. create Lists as queues
		//2. create solrcore replacement
		//3. use queue inputs and outputs.
		
		
		SolrMessageQueue queue = new SolrMessageQueue();

		SolrCoreWrapper coreWrapper = mock(SolrCoreWrapper.class);
		IConnectionFactoryWrapper connFactory = mock(IConnectionFactoryWrapper.class);
		IChannelWrapper channel = mock(IChannelWrapper.class);
		IConnectionWrapper conn = mock(IConnectionWrapper.class);
		QueueingConsumer.Delivery delivery = mock(QueueingConsumer.Delivery.class);
		
		
		UpdateWorkerFactory workerFactory = mock(UpdateWorkerFactory.class);
		final QueueUpdateWorker updateWorker = new QueueUpdateWorker(new NamedList(), coreWrapper, channel, "/update", delivery){

			@Override
			protected void handleError(SolrMqException e,
					SolrQueryRequest request, SolrQueryResponse response) {
			}

			@Override
			protected void handleResult(SolrQueryRequest request,
					SolrQueryResponse result) throws UpdateFailedException,
					ResponseFailedException {
			}
			
			@Override 
			public SolrQueryResponse performUpdateRequest(String handler,
					SolrQueryRequest request, SolrQueryResponse response) {
				Iterable<ContentStream> streams = request.getContentStreams();
				ContentStreamBase.StringStream stream = (ContentStreamBase.StringStream)streams.iterator().next();
				try {
					String myString = IOUtils.toString(stream.getStream(), "UTF-8");
					assertTrue("Check that the input is properly passed to the updater", "Message Body".equals(myString));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//core.executeSolrUpdateRequest(handler, request, response);
				return response;
			}
			
		};
		spy(updateWorker);
		
		String result = new String("Message Body");
		byte[] body = result.getBytes();
		SolrQueryRequest request = mock(SolrQueryRequest.class);
		SolrParams params = mock(SolrParams.class);
		final SolrQueryResponse response = mock(SolrQueryResponse.class);
		
		try {
			when(connFactory.newConnection()).thenReturn(conn);
			when(conn.createChannel()).thenReturn(channel);
			when(channel.getNextDelivery()).thenReturn(delivery);
			when(request.getParams()).thenReturn(params);
			when(params.get("task")).thenReturn("stop");
			when(workerFactory.getUpdateWorker((QueueListenerThread)anyObject() , (NamedList<String>)anyObject(), eq(coreWrapper), eq(channel), eq("/update"), eq(delivery)))
				.thenAnswer(new Answer<QueueUpdateWorker>(){

					public QueueUpdateWorker answer(InvocationOnMock invocation)
							throws Throwable {
						assertTrue("hit updateWorker", true);
						return updateWorker;
					}
					
				})
				.thenAnswer(new Answer(){

					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						Thread.sleep(1000);
						throw new Exception("breakout.");
					}
					
				});
			when(delivery.getBody()).thenReturn(body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		NamedList args = mock(NamedList.class);
		when(args.get("messageQueueHost")).thenReturn("localhost");
		when(args.get("queue")).thenReturn("queue");
		when(args.get("errorQueue")).thenReturn("errorQueue");
		when(args.get("updateHandlerName")).thenReturn("/update");
		when(args.get("workerSettings")).thenReturn(null);
		
		queue.setCoreWrapper(coreWrapper);
		queue.setFactoryWrapper(connFactory);
		queue.setUpdateWorkerFactory(workerFactory);
		queue.init(args);
		
		try {
			queue.handleRequestBody(request, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//byte[] res= verify(delivery.getBody());
		
		//fail("Not yet implemented");
	}

}
