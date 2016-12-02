package argo.testcamelproject

import org.apache.camel.component.seda.SedaComponent

/**
 * A placeholder component to ensure we don't accidentally try to use SFTP in a unit test.
 */
class DummyComponent extends SedaComponent {
    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
    }
}
