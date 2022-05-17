import groovy.sql.Sql
@GrabConfig(systemClassLoader = true)
@Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.49')

import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.time.LocalDateTime

// CONSTANTS
String CWD = new File("..").absolutePath
String TEST_PROJECT = new File(CWD + File.separator + "b-vsa-ls22-project1").absolutePath
String STUDENT_GROUP = "a"
String FEEDBACK_DIR = "feedback"
String SOURCE_TEST_DIR = File.separator + String.join(File.separator, ['src', 'test', 'java', 'sk', 'stuba', 'fei', 'uim', 'vsa', 'pr1'])
String TARGET_TEST_DIR = File.separator + String.join(File.separator, ['src', 'test', 'java', 'sk', 'stuba', 'fei', 'uim', 'vsa', 'pr1' + STUDENT_GROUP])
String RESPORTS_DIR = File.separator + String.join(File.separator, ['target', 'surefire-reports'])
def DB = [url : "jdbc:mysql://localhost:3306/VSA_PR1?useUnicode=true&characterEncoding=UTF-8",
          user: 'vsa', password: 'vsa', driver: 'com.mysql.jdbc.Driver']
def CSV_HEADER = ['AISID', 'Name', 'Email', 'GitHub', 'Test Run', 'Succeeded', 'Failures', 'Errors', 'Skipped', 'Points', 'Notes']
String CSV_DELIMITER = ';'

// CLASSES
class Student {
    String aisId = ''
    String name = ''
    String email = ''

    @Override
    String toString(String delimiter = ';') {
        return String.join(delimiter, aisId, name, email)
    }

    Node toXml() {
        def xml = new NodeBuilder().student {
            'aisId'(aisId)
            'name'(name)
            'email'(email)
        }
        return xml as Node
    }
}

class Evaluation {
    String exam = 'B-VSA 21/22 Semestrálny projekt 1'
    Student student = new Student()
    String github = ''
    Integer testRun = 0
    Integer success = 0
    Integer failure = 0
    Integer error = 0
    Integer skip = 0
    Integer points = 0
    String notes = ''
    Duration testDuration

    void calcPoints() {
        points = Math.ceil((20.0 / testRun.doubleValue()) * success.doubleValue()).intValue()
    }

    void calcSuccess() {
        success = testRun - (failure + error + skip)
    }

    @Override
    String toString(String delimiter = ';') {
        return String.join(delimiter, [
                student.toString(delimiter),
                github,
                testRun as String,
                success as String,
                failure as String,
                error as String,
                skip as String,
                points as String,
                notes])
    }

    Node toXml() {
        Node xml = new NodeBuilder().evaluation {
            'exam'(exam)
            'student'('Dummy student')
            'github'(github)
            'testRun'(testRun)
            'success'(success)
            'failure'(failure)
            'error'(error)
            'skip'(skip)
            'points'(points)
            'notes'(notes)
            'testDuration'(testDuration.toString())
        }
        (xml.student[0] as Node).replaceNode(student.toXml())
        return xml
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
        target.text = target.text.replace('sk.stuba.fei.uim.vsa.pr1', 'sk.stuba.fei.uim.vsa.pr1' + STUDENT_GROUP)
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
        return new Student(
                'aisId': dev.id?.text(),
                'name': dev.name?.text(),
                'email': dev.email?.text()
        )
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

def clearDatabase = {
    def sql = Sql.newInstance(DB.url, DB.user, DB.password, DB.driver)
    if (!sql)
        throw new RuntimeException("Cannot connect to the MySQL DB")
    sql.execute 'SET FOREIGN_KEY_CHECKS = 0'
    sql.execute 'SET GROUP_CONCAT_MAX_LEN = 32768'
    sql.execute 'SET @tables = NULL'
    sql.execute '''
        SELECT GROUP_CONCAT('`', table_name, '`') INTO @tables
        FROM information_schema.tables
        WHERE table_schema = (SELECT DATABASE())
    '''
    sql.execute 'SELECT IFNULL(@tables,\'dummy\') INTO @tables'
    sql.execute 'SET @tables = CONCAT(\'DROP TABLE IF EXISTS \', @tables)'
    sql.execute 'PREPARE stmt FROM @tables'
    sql.execute 'EXECUTE stmt'
    sql.execute 'DEALLOCATE PREPARE stmt'
    sql.execute 'SET FOREIGN_KEY_CHECKS = 1'
    sql.close()
}

def runMvnTest = { File project ->
    def outFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'test-output.txt')
    def errFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'test-error-output.txt')
    def args = ['cmd', '/c', 'mvn', 'clean', 'compile', 'test']
    def builder = new ProcessBuilder(args)
    builder.directory(project)
    builder.redirectOutput(outFile)
    builder.redirectError(errFile)
    def process = builder.start()
    process.waitFor()
}

def aggregateTestReports = { File project ->
    def reportXMLFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'surefire-test-reports.xml')
    reportXMLFile.text = '<?xml version="1.0" encoding="UTF-8"?>'
    def reportTXTFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'surefire-test-reports.txt')
    reportTXTFile.text = ''

    File[] reports = new File(project.absolutePath + RESPORTS_DIR).listFiles()
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
    def reportTXTFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'surefire-test-reports.txt')
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

def buildSummaryFile = { Evaluation eval, File project ->
    def summaryFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'summary.xml')
    summaryFile.text = XmlUtil.serialize(eval.toXml())
}

LocalDateTime startOfScript = LocalDateTime.now()
File[] files = new File("$CWD${File.separator}skupina${STUDENT_GROUP.toUpperCase()}").listFiles()
for (File project : files) {
    if (!project.isDirectory()) continue
    LocalDateTime startOfTest = LocalDateTime.now()
    println "Starting student ${project.getName()}"
    Evaluation student = new Evaluation()
    student.github = project.getName()
    println "Creating feedback folder"
    new File(project.absolutePath + File.separator + FEEDBACK_DIR).mkdirs()
    def outFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'test-output.txt')
    def errFile = new File(project.absolutePath + File.separator + FEEDBACK_DIR + File.separator + 'test-error-output.txt')
    outFile.text = ''
    errFile.text = ''

    try {
        println "Copying test files"
        copyDir(new File(TEST_PROJECT + SOURCE_TEST_DIR), new File(project.absolutePath + TARGET_TEST_DIR))
        copyDir(new File(TEST_PROJECT + SOURCE_TEST_DIR + File.separator + "tests"),
                new File(project.absolutePath + TARGET_TEST_DIR + File.separator + "tests"))
        copyDir(new File(TEST_PROJECT + SOURCE_TEST_DIR + File.separator + "group" + STUDENT_GROUP.toUpperCase()),
                new File(project.absolutePath + TARGET_TEST_DIR + File.separator + "group" + STUDENT_GROUP.toUpperCase()))
        println "Editing pom.xml"
        Student studentId = editPom(project)
        if (!studentId) {
            println "Cannot parse student from pom.xml"
            studentId = new Student('name': project.getName())
        }
        student.student = studentId
        println "Detected student credentials: ${student.student}"
        println "Editing persistence.xml"
        editPersistence(project)
        println "Clearing database before tests"
        clearDatabase()
        println "Starting maven tests"
        runMvnTest(project)
        println "Aggregating Surefire reports into one file"
        aggregateTestReports(project)
        println "Evaluating test results"
        student = evaluateTests(student, project)
        println "Results: Run:${student.testRun}, Success:${student.success}, Failure:${student.failure}, Error:${student.error}, Skip:${student.skip}, Points:${student.points}"
    } catch (Exception ex) {
        ex.printStackTrace()
        errFile << "\n"
        errFile << ex.message
        student.notes += ex.message
    }
    if (outFile.text.contains('BUILD FAILURE')) {
        if (!student.notes.isEmpty()) student.notes += ';'
        if (outFile.text.contains('COMPILATION ERROR'))
            student.notes += 'Maven Compilation Error'
        else if (outFile.text.contains('T E S T S'))
            student.notes += 'Maven Tests failed'
    }
    results << student
    student.testDuration = Duration.between(startOfTest, LocalDateTime.now())
    buildSummaryFile(student, project)
    println "Test took ${student.testDuration.toString()}"
    println "-----------------------------\n"
}

def resultFile = new File(CWD + File.separator + 'results.csv')
resultFile.text = String.join(CSV_DELIMITER, CSV_HEADER) + '\n'
results.each {
    resultFile.append(it.toString(CSV_DELIMITER))
    resultFile.append("\n")
}
println "Tests run for all tests took ${Duration.between(startOfScript, LocalDateTime.now()).toString()}"



