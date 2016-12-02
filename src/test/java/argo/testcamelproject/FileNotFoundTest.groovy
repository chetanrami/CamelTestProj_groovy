package argo.testcamelproject

import org.junit.Test

class FileNotFoundTest extends ArgoCamelTestSupport {
    static final String DEMONSTRATION_FILE = '''\
<Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
    <Body>
        <QAS_GETQUERYRESULTS_RESP_MSG
            xmlns="http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_GETQUERYRESULTS_RESP_MSG.VERSION_1">
            <query
                numrows="0"
                queryname="DEMONSTRATION_QUERY"
                xmlns="http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_QUERYRESULTS_XMLP_RESP.VERSION_1"/>
        </QAS_GETQUERYRESULTS_RESP_MSG>
    </Body>
</Envelope>
'''
    @Override
    protected void addAdvice() {
        // TODO: replace the live SFTP routes with injected source, target, and error endpoints as appropriate
    }

//    @Test
    void ensureCriticalPathIsSatisfied() {
        // TODO: send the string DEMONSTRATION_FILE to source
        // TODO: ensure 0 messages are received by the target endpoint
        // TODO: ensure 1 message are received by the error endpoint
    }
}