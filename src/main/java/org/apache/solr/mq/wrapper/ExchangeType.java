package org.apache.solr.mq.wrapper;


public enum ExchangeType {
	FANOUT("fanout"), 
	DIRECT("direct"), 
	TOPIC("topic"), 
	HEADERS("headers");
	
	private String value;

	private ExchangeType(String value) {
	    this.value = value;
	}

    @Override
    public String toString() {
        return this.value;
    }
    
    public static ExchangeType fromValue(String value) {
    	for (ExchangeType type: ExchangeType.values()){
    		if (type.toString().equals(value)){
    			return type;
    		}
    	}
    	return null;
    }
}
