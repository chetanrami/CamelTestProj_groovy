package argo.testcamelproject

import com.google.common.base.Function
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import com.google.common.collect.FluentIterable
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import org.apache.camel.Endpoint
import org.apache.camel.EndpointInject
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport
import org.apache.commons.io.FileUtils
import org.junit.BeforeClass

import javax.annotation.Nonnull

abstract class ArgoCamelTestSupport extends CamelBlueprintTestSupport {
    static long ASSERT_PERIOD_IN_MILLIS = 2_500L

    @Override
    final boolean isUseAdviceWith() {
        true
    }

    @Override
    final protected String getBundleFilter() {
        '(!(Bundle-Name=ilstu-ArgoCamelTest))'
    }

    @Override
    final protected String getBlueprintDescriptor() {
        '/OSGI-INF/blueprint/testDependencies.xml,/OSGI-INF/blueprint/TCP.xml'
    }

    @EndpointInject(uri = 'direct:source')
    protected Endpoint source

    @EndpointInject(uri = 'direct:trigger')
    protected Endpoint trigger_TCP


    @EndpointInject(uri = 'mock:target')
    protected MockEndpoint target

    @EndpointInject(uri = 'mock:target2')
    protected MockEndpoint target2

    @EndpointInject(uri = 'mock:target3')
    protected MockEndpoint target3

    @EndpointInject(uri = 'mock:target4')
    protected MockEndpoint target4

    @EndpointInject(uri = 'mock:errors')
    protected MockEndpoint errors

    abstract protected void addAdvice()

    protected Set<String> routesToKeep = Sets.newHashSet()

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FileUtils.deleteDirectory(new File('src\\test\\resources\\test-output'))
    }

    @Override
    final protected void doPostSetup() throws Exception {
        addAdvice()

        if (!routesToKeep.empty) {
            final ImmutableSet<String> routeIds = FluentIterable.from(context.getRouteDefinitions()).transform(
                    {
                        RouteDefinition input
                            ->
                            (null == input ? null : input.getId())
                    } as Function
            ).filter(Predicates.notNull())
                    .filter({ !routesToKeep.contains(it) } as Predicate)
                    .toSet()
            for (String routeId : routeIds) {
                context.removeRouteDefinition(context.getRouteDefinition(routeId))
            }
        }
    }

    final protected void keepAllRoutes() {
        // since doPostSetup() checks the emptiness of routesToKeep, we provide a descriptive method name to encapsulate the intent to keep all routes.
        routesToKeep.clear()

        assert routesToKeep.empty
    }

    final protected void advise(
            @Nonnull final ModelCamelContext context,
            @Nonnull final Map<String, Closure<AdviceWithRouteBuilder>> advice
    ) {
        advice.entrySet().each { Map.Entry<String, Closure<AdviceWithRouteBuilder>> entry ->
            routesToKeep.add(entry.key)
            context.getRouteDefinition(entry.key).with { RouteDefinition route ->
                route.autoStartup = true
                route.adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    void configure() throws Exception {
                        entry.value.call(this)
                    }
                })
            }
        }
    }
}