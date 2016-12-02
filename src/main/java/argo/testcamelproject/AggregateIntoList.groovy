package argo.testcamelproject

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

class AggregateIntoList implements AggregationStrategy {
    @Override
    Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        def newBody = newExchange.in.body;
        if (!oldExchange) {
            newExchange.in.body = [newBody] as ArrayList
            newExchange
        } else {
            def list = oldExchange.in.getBody(ArrayList.class)
            list.add(newBody)
            oldExchange
        }
    }
}
