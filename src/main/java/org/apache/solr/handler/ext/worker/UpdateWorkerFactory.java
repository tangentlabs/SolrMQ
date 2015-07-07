package org.apache.solr.handler.ext.worker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;

import com.rabbitmq.client.QueueingConsumer;

public class UpdateWorkerFactory {

	@SuppressWarnings("unchecked")
	public QueueUpdateWorker getUpdateWorker(
			QueueListenerThread listener, NamedList<String> workerSettings,
			ISolrCoreWrapper core, IChannelWrapper channel2,
			String updateHandler, QueueingConsumer.Delivery delivery) {
		String workerClass = workerSettings.get("workerClass");
		if (workerClass != null) {
			try {
				Class<QueueUpdateWorker> worker = (Class<QueueUpdateWorker>) Class
						.forName(workerClass);
				Constructor<QueueUpdateWorker> workerConstructer = (Constructor<QueueUpdateWorker>) worker
						.getConstructor(new Class[] {
								workerSettings.getClass(), core.getClass(),
								channel2.getClass(), updateHandler.getClass(),
								delivery.getClass() });
				return (QueueUpdateWorker) workerConstructer
						.newInstance(new Object[] { workerSettings, core,
								channel2, updateHandler, delivery });
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return new DefaultWorker(workerSettings, core, channel2, updateHandler,
				delivery);
	}
}
