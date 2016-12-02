package argo.testcamelproject.routes

import argo.testcamelproject.AggregateIntoList
import argo.testcamelproject.Record
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.PropertyInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat

import static org.apache.camel.component.stax.StAXBuilder.stax

class CamelTestProjectRoutes extends RouteBuilder {
    public static final String LOG_NAME = 'argo.testcamelproject.cameltestroutes'

    @Override
    void configure() throws Exception {
        def control_bus_filePoll_suspend = 'controlbus:route?routeId=fileSource&action=suspend&async=true'
        def control_bus_filePoll_resume = 'controlbus:route?routeId=fileSource&action=resume&async=true'

        onException(Exception.class)
                .log("error trace : \${exception.stacktrace}").handled(true);

        restConfiguration()
                .component("servlet").port(8181)
                .endpointProperty("servletName","camelServletGroovyTestProj")
                .contextPath("/restgroovy");

        rest("/route/").get("/{token}")
                .route().startupOrder(200).routeId("restRouteID")
                .from("direct:rest")
                .setBody(simple("\${in.header.token}")).id("setRestBody")
                .log(LoggingLevel.INFO, LOG_NAME, "body:----- \${body}")
                .to("direct:checkAuth").id("restAuthEnd");

        from(cronExp)
                .routeId("cronTimerCamelTestingRoutes_RouteId")
                .log(LoggingLevel.INFO, LOG_NAME, "CamelTestingRoutes: Started via Quartz Timer")
                .to(control_bus_filePoll_resume).id('control_bus_filePoll_resume_id')

        from(sourceSftp).routeId('fileSource').autoStartup(false)
                .errorHandler(deadLetterChannel('direct:errors'))
                .log(LoggingLevel.INFO, LOG_NAME, "Headers before pickup URI: \${headers}")
                .log(LoggingLevel.INFO, LOG_NAME, "properties before pickup URI: ${Exchange.properties}")
                .log(LoggingLevel.INFO, 'sftp picked body: \${body} \${body.class}')
                .choice()
                    .when(simple('${body} == ${null}'))
                        .log(LoggingLevel.INFO, LOG_NAME, "Suspending File Poll")
                    .endChoice()
                    .otherwise()
                        .convertBodyTo(InputStream)
                        .split(stax(Record), new AggregateIntoList())
                            .log(LoggingLevel.INFO, 'built instance of ${body.class}')
                        .end()
                        .log(LoggingLevel.INFO, 'aggregated body \${body.class}')
                        .marshal(new BindyCsvDataFormat(Record)).id('marshaller')
                        .to(targetSftp).id('archiveSystem')
                        .log(LoggingLevel.INFO, 'archiveSystem Body \${body.class}').id('archiveFile')
                    .endChoice()
                .end()
                .to(control_bus_filePoll_suspend).id('control_bus_filePoll_suspend_id')

        from('direct:errors').routeId('errorHandler')
                .log(LoggingLevel.ERROR, 'failed to process\n${body}').id('errorWeavingHook')
    }

    @PropertyInject('sftp://{{source.hostname}}:22{{source.path}}?username={{source.username}}&privateKeyUri={{source.privateKeyUri}}&delete=true&include={{source.fileName}}&idempotent=true&readLock=changed&readLockTimeout=30000&readLockCheckInterval=20000&stepwise=false&sendEmptyMessageWhenIdle=true')
    String sourceSftp

    @PropertyInject('sftp://{{target.hostname}}:22{{target.path}}?username={{target.username}}&privateKeyUri={{target.privateKeyUri}}')
    String targetSftp

    @PropertyInject('quartz2://TCP_Timer?cron={{source.cron.expression}}')
    String cronExp

    @PropertyInject(value = 'source.cron.autoStart', defaultValue = 'true')
    String cronStart
}