import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.StandardCopyOption

String cwd = new File("..").absolutePath
String testProject = new File(cwd + File.separator + "b-vsa-ls22-project1").absolutePath
String testDir = File.separator + String.join(File.separator, ['src', 'test', 'java', 'sk', 'stuba', 'fei', 'uim', 'vsa', 'pr1'])
String studentGroup = "skupinaA"
String feedbackDir = "feedback"

// cd to student project
// copy necessery files
// edit some file
// run maven test
// aggregate test reports to one file
// evaluate results and assign points to a student

def copyDir = { File from, File to ->
    to.mkdirs()
    File[] files = from.listFiles()
    for (File file : files) {
        if (file.isDirectory()) continue
        Files.copy(
                file.toPath(),
                new File(to.absolutePath + File.separator + file.getName()).toPath(),
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING
        )
    }
}

def createDeps = { String group, String artifact, String versionValue, Boolean testScope ->
    return new NodeBuilder().dependency() {
        groupId(group)
        artifactId(artifact)
        version(versionValue)
        if (testScope) {
            scope('test')
        }
    }
}

def editPom = { File project ->
    def pomFile = new File(project.absolutePath + File.separator + "pom.xml")
    def pom = new XmlParser().parse(pomFile)
    def junitProperty = new NodeBuilder().'junit.version'('5.8.2')
    pom.'properties'[0].append(junitProperty)
    def dep = createDeps('org.junit.jupiter', 'junit-jupiter-engine', '\${junit.version}', true)
    pom.dependencies[0].append(dep)
    def dep1 = createDeps('org.junit.jupiter', 'junit-jupiter-api', '\${junit.version}', true)
    pom.dependencies[0].append(dep1)
    def dep2 = createDeps('org.reflections', 'reflections', '0.10.2', false)
    pom.dependencies[0].append(dep2)

    def build = new NodeBuilder().build() {
        plugins() {
            plugin() {
                groupId('org.apache.maven.plugins')
                artifactId('maven-surefire-plugin')
                version('2.22.2')
            }
        }
    }
    pom.append(build)
    def xmlString = XmlUtil.serialize(pom)
    pomFile.text = xmlString
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
    def persist = new XmlParser().parse(persistFile)
    def props = persist.'persistence-unit'.'properties'[0]
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
    def mvnProcess = "cmd /h".execute()
    mvnProcess.consumeProcessOutput(System.out, System.err)
    mvnProcess.waitForProcessOutput()
    def outFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-output.txt')
    def errFile = new File(project.absolutePath + File.separator + feedbackDir + File.separator + 'test-error-output.txt')
    outFile.text = "${System.out}"
    errFile.text = "${System.err}"
}

File[] files = new File("$cwd${File.separator}$studentGroup").listFiles()
for (File project : files) {
    if (!project.isDirectory()) continue
    println "Starting student ${project.getName()}"
    println "Copying test files"
    copyDir(new File(testProject + testDir), new File(project.absolutePath + testDir))
    copyDir(new File(testProject + testDir + File.separator + "tests"), new File(project.absolutePath + testDir + File.separator + "tests"))
    println "Editing pom.xml"
    editPom(project)
    println "Editing persistence.xml"
    editPersistence(project)
    println "Creating feedback folder"
    new File(project.absolutePath + File.separator + feedbackDir).mkdirs()
    println "Starting maven tests"
    runMvnTest(project)
    println "-------------\n"
}



