import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.StandardCopyOption

String cwd = new File("..").absolutePath
String testProject = new File(cwd + File.separator + "b-vsa-ls22-project1").absolutePath
String studentGroup = "a"
String feedbackDir = "feedback"
String sourceTestDir = File.separator + String.join(File.separator, ['src', 'test', 'java', 'sk', 'stuba', 'fei', 'uim', 'vsa', 'pr1'])
String targetTestDir = File.separator + String.join(File.separator, ['src', 'test', 'java', 'sk', 'stuba', 'fei', 'uim', 'vsa', 'pr1' + studentGroup])
String reportsDir = File.separator + String.join(File.separator, ['target', 'surefire-reports'])

class Evaluation {
    String student = ''
    Integer testRun = 0
    Integer success = 0
    Integer failure = 0
    Integer error = 0
    Integer skip = 0
    Integer points = 0

    void calcPoints() {
        points = Math.ceil((20.0 / testRun.doubleValue()) * success.doubleValue()).intValue()
    }

    void calcSuccess() {
        success = testRun - (failure + error + skip)
    }
}

List<Evaluation> results = []

// Test steps:
// cd to student project
// copy necessery files
// edit some file
// run maven test
// aggregate test reports to one file
// evaluate results and assign points to a student
// git push student's repo changes

def copyDir = { File from, File to ->
    to.mkdirs()
    File[] files = from.listFiles()
    for (File file : files) {
        if (file.isDirectory()) continue
        File target = new File(to.absolutePath + File.separator + file.getName())
        Files.copy(
                file.toPath(),
                target.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING
        )
        target.text = target.text.replace('sk.stuba.fei.uim.vsa.pr1', 'sk.stuba.fei.uim.vsa.pr1' + studentGroup)
    }
}

def createDeps = { Node pom, String group, String artifact, String versionValue, Boolean testScope ->
    Node dep = pom.dependencies.dependency.find { it.groupId[0].text() == group && it.artifactId[0].text() == artifact } as Node
    Node newDep = new NodeBuilder().dependency() {
        groupId(group)
        artifactId(artifact)
        version(versionValue)
        if (testScope) {
            scope('test')
        }
    }
    if (dep) {
        dep.replaceNode(newDep)
    } else {
        pom.dependencies[0].append(newDep)
    }
}

def editPom = { File project ->
    def pomFile = new File(project.absolutePath + File.separator + "pom.xml")
    if (!pomFile.exists()) throw new RuntimeException("Cannot find pom.xml file")
    def pom = new XmlParser().parse(pomFile)

    if (pom.'properties'.'junit.version'.size() > 0) {
        pom.'properties'.'junit.version'[0].setValue('5.8.2')
    } else {
        def junitProperty = new NodeBuilder().'junit.version'('5.8.2')
        pom.'properties'[0].append(junitProperty)
    }

    createDeps(pom, 'org.junit.jupiter', 'junit-jupiter-engine', '\${junit.version}', true)
    createDeps(pom, 'org.junit.jupiter', 'junit-jupiter-api', '\${junit.version}', true)
    createDeps(pom, 'org.reflections', 'reflections', '0.10.2', false)

    def build = new NodeBuilder().build() {
        plugins() {
            plugin() {
                groupId('org.apache.maven.plugins')
                artifactId('maven-surefire-plugin')
                version('2.22.2')
            }
        }
    }
    if (pom.build.size() > 0) {
        pom.build[0].replaceNode(build)
    } else {
        pom.append(build)
    }
    def xmlString = XmlUtil.serialize(pom)
    pomFile.text = xmlString

    if (pom.developers.developer.size() > 0) {
        Node dev = pom.developers.developer[0]
        return dev.id?.text() + ';' + dev.name?.text() + ';' + dev.email?.text()
    }
    return null
}

def editProperty = { Node properties, String name, String value ->
    def prop = properties.property.find { it.'@name' == name }
    if (prop) {
        prop.'@value' = value
    } else {
        properties.append(new NodeBuilder().property(name: name, value: value) {})
    }
}

def editPersistence = { File project ->
    def persistFile = new File(project.absolutePath + File.separator + String.join(File.separator, ['src', 'main', 'resources', 'META-INF', 'persistence.xml']))
    if (!persistFile.exists()) throw new RuntimeException("Cannot find persistence.xml file")
    def persist = new XmlParser().parse(persistFile)
    def props = persist?.'persistence-unit'?.'properties'[0]
    if (!props) throw new RuntimeException("persistence.xml file is setup incorrectly")
    editProperty(props as Node, 'javax.persistence.jdbc.driver', 'com.mysql.jdbc.Driver')
    editProperty(props as Node, 'javax.persistence.jdbc.url', 'jdbc:mysql://localhost:3306/VSA_PR1')
    editProperty(props as Node, 'javax.persistence.jdbc.user', 'vsa')
    editProperty(props as Node, 'javax.persistence.jdbc.password', 'vsa')
    editProperty(props as Node, 'javax.persistence.schema-generation.database.action', 'drop-and-create')
    editProperty(props as Node, 'eclipselink.target-database', 'MySQL')
    def xmlString = XmlUtil.serialize(persist)
    persistFile.text = xmlString
}

def runMvnTest = { File project ->
    def outFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-output.txt')
    def errFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-error-output.txt')
    def mvnProcess = "cmd /c cd ${project.absolutePath} && mvn clean compile test".execute()
    mvnProcess.waitForProcessOutput(outFile.newWriter(), errFile.newWriter())
}

def aggregateTestReports = { File project ->
    def reportXMLFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'surefire-test-reports.xml')
    reportXMLFile.text = '<?xml version="1.0" encoding="UTF-8"?>'
    def reportTXTFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'surefire-test-reports.txt')
    reportTXTFile.text = ''

    File[] reports = new File(project.absolutePath + reportsDir).listFiles()
    for (File report : reports) {
        if (report.name.endsWith(".xml")) {
            reportXMLFile.append(report.text.replace('<?xml version="1.0" encoding="UTF-8"?>', ''))
        } else if (report.name.endsWith(".txt")) {
            reportTXTFile.append(report.text)
            def delimeter = ''
            79.times { delimeter += '-' }
            reportTXTFile.append(delimeter + "\n \n")
        }
    }
}

def evaluateTests = { Evaluation eval, File project ->
    def reportTXTFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'surefire-test-reports.txt')
    reportTXTFile.eachLine { line ->
        if (!line.startsWith('Tests run:')) return
        String[] parts = line.split(',')
        eval.testRun += Integer.parseInt(parts[0].substring(parts[0].lastIndexOf(' ')).trim())
        eval.failure += Integer.parseInt(parts[1].substring(parts[1].lastIndexOf(' ')).trim())
        eval.error += Integer.parseInt(parts[2].substring(parts[2].lastIndexOf(' ')).trim())
        eval.skip += Integer.parseInt(parts[3].substring(parts[3].lastIndexOf(' ')).trim())

    }
    eval.calcSuccess()
    eval.calcPoints()
    return eval
}

File[] files = new File("$cwd${File.separator}skupina${studentGroup.toUpperCase()}").listFiles()
for (File project : files) {
    if (!project.isDirectory()) continue
    println "Starting student ${project.getName()}"
    Evaluation student = new Evaluation()
    println "Creating feedback folder"
    new File(project.absolutePath + File.separator + feedbackDir).mkdirs()
    def outFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-output.txt')
    def errFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-error-output.txt')
    outFile.text = ''
    errFile.text = ''

    try {
        println "Copying test files"
        copyDir(new File(testProject + sourceTestDir), new File(project.absolutePath + targetTestDir))
        copyDir(new File(testProject + sourceTestDir + File.separator + "tests"),
                new File(project.absolutePath + targetTestDir + File.separator + "tests"))
        copyDir(new File(testProject + sourceTestDir + File.separator + "group" + studentGroup.toUpperCase()),
                new File(project.absolutePath + targetTestDir + File.separator + "group" + studentGroup.toUpperCase()))
        println "Editing pom.xml"
        String studentId = editPom(project)
        if (!studentId) {
            println "Cannot parse student from pom.xml"
            studentId = ';' + project.getName() + ';'
        }
        student.student = studentId
        println "Detected student credentials: ${student.student}"
        println "Editing persistence.xml"
        editPersistence(project)
        println "Starting maven tests"
        runMvnTest(project)
        println "Aggregating Surefire reports into one file"
        aggregateTestReports(project)
        println "Evaluating test results"
        student = evaluateTests(student, project)
        println "Results: Run:${student.testRun}, Success:${student.success}, Failure:${student.failure}, Error:${student.error}, Skip:${student.skip}, Points:${student.points}"
    } catch (Exception ex) {
        ex.printStackTrace()
        errFile << ex.message
    }
    results << student
    println "-------------\n"
}

def resultFile = new File(cwd + File.separator + 'results.csv')
resultFile.text = "AISID;Name;Email;Test Run;Succeeded;Failures;Errors;Skipped;Points\n"
results.each {
    resultFile.append(String.join(';', it.student,
            it.testRun as String,
            it.success as String,
            it.failure as String,
            it.error as String,
            it.skip as String,
            it.points as String))
    resultFile.append("\n")
}



