package argo.testcamelproject

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.builder.AdviceWithRouteBuilder

import org.junit.Test

import java.util.concurrent.TimeUnit

@CompileStatic
class CriticalPathTest extends ArgoCamelTestSupport {
    static final String DEMONSTRATION_FILE = '''\
<Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
    <Body>
        <QAS_GETQUERYRESULTS_RESP_MSG
            xmlns="http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_GETQUERYRESULTS_RESP_MSG.VERSION_1">
            <query
                numrows="5"
                queryname="DEMONSTRATION_QUERY"
                xmlns="http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_QUERYRESULTS_XMLP_RESP.VERSION_1">
                <row rownumber='1'>
                    <EMPLID>10000000000</EMPLID>
                    <NAME>Frodo Baggins</NAME>
                </row>
                <row rownumber='2'>
                    <EMPLID>20000000000</EMPLID>
                    <NAME>Samwise Gamgee</NAME>
                </row>
                <row rownumber='3'>
                    <EMPLID>30000000000</EMPLID>
                    <NAME>Legolas</NAME>
                </row>
                <row rownumber='4'>
                    <EMPLID>40000000000</EMPLID>
                    <NAME>Gimli</NAME>
                </row>
                <row rownumber='5'>
                    <EMPLID>50000000000</EMPLID>
                    <NAME>Boromir</NAME>
                </row>
            </query>
        </QAS_GETQUERYRESULTS_RESP_MSG>
    </Body>
</Envelope>
'''    
    static final String EXPECTED_RESULT = '''\
EMPLOYEE_NAME,EMPLOYEE_ID
"Frodo Baggins","10000000000"
"Samwise Gamgee","20000000000"
"Legolas","30000000000"
"Gimli","40000000000"
"Boromir","50000000000"
'''

    @Override
    protected void addAdvice() {
        // TODO: replace the live SFTP routes with injected source, target, and error endpoints as appropriate
        advise(context, [
                fileSource: { AdviceWithRouteBuilder rb ->
                    rb.replaceFromWith(source)
                    rb.weaveById('marshaller').replace().to(target2)
                    rb.weaveById('archiveSystem').replace().to(target)

                    rb.weaveById('archiveFile').replace().convertBodyTo(String)
                            .to('file:src/test/test-output/sftparchive')
                },
                errorHandler: { AdviceWithRouteBuilder rb ->
                    rb.weaveById('errorWeavingHook').replace().to(errors)
                },
            ])
        routesToKeep.addAll()
        keepAllRoutes()
    }

    @Test
    void ensureCriticalPathIsSatisfied() {
        // TODO: send the string DEMONSTRATION_FILE to source
        // TODO: ensure 1 message is received by the target endpoint, containing EXPECTED_RESULT as its message contents and 'FELLOWSHIP_OF_THE_RING.txt' as its file name only header
        // TODO: ensure 0 messages are received by the error endpoint
        
        target2.assertPeriod = TimeUnit.SECONDS.toMillis(3)
        target2.expectedMessageCount = 1
        target.assertPeriod = TimeUnit.SECONDS.toMillis(3)
        target.expectedMessageCount = 1
        errors.assertPeriod = TimeUnit.SECONDS.toMillis(3)
        errors.expectedMessageCount = 0

        //      signatures
        //      sendBodyAndHeaders(Endpoint endpoint, Object body, Map<String,Object> headers)
        //      ProducerTemplate template;
        //      ConsumerTemplate consumer;

        template.setDefaultEndpoint(source)

        template.sendBodyAndHeaders(
                source,
                new File(CriticalPathTest.getResource('/DemonstrationFile.txt').getFile()).getText('UTF-8'),
                [
                        (Exchange.FILE_NAME)     : 'DemonstrationFileRenamed.txt',
                        (Exchange.FILE_NAME_ONLY): 'DemonstrationFileRenamed.txt',
                ] as Map<String, Object>
        )

        checkHeadersForValidationGradStudents(target.getExchanges())
        assertMockEndpointsSatisfied()
    }

    static void checkHeadersForValidationGradStudents(List<Exchange> listOfExchanges) {

        Object msgBody = listOfExchanges.get(0).getIn().getBody()

        Record record1  = ((List<Record>)msgBody).get(0)
        Record record2  = ((List<Record>)msgBody).get(1);

        assertEquals("10000000000", record1.getEmployeeId());
        assertEquals("Frodo Baggins", record1.getEmployeeName());

        assertEquals("20000000000", record2.getEmployeeId());
        assertEquals("Samwise Gamgee", record2.getEmployeeName());

    }
}
