package argo.testcamelproject

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.camel.dataformat.bindy.annotation.CsvRecord
import org.apache.camel.dataformat.bindy.annotation.DataField

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'row', namespace = 'http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_QUERYRESULTS_XMLP_RESP.VERSION_1')
@CsvRecord(quoting = true, crlf = 'UNIX', generateHeaderColumns = true, separator = ',')
@ToString(includes = ['rowNumber', 'employeeId', 'employeeName'], includePackage = false)
@EqualsAndHashCode
class Record implements Serializable {
    @XmlAttribute(name = 'rownumber')
    int rowNumber

    @XmlElement(name = 'EMPLID', namespace = 'http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_QUERYRESULTS_XMLP_RESP.VERSION_1')
    @DataField(pos = 2, columnName = 'EMPLOYEE_ID')
    String employeeId

    @XmlElement(name = 'NAME', namespace = 'http://xmlns.oracle.com/Enterprise/Tools/schemas/QAS_QUERYRESULTS_XMLP_RESP.VERSION_1')
    @DataField(pos = 1, columnName = 'EMPLOYEE_NAME')
    String employeeName
}
