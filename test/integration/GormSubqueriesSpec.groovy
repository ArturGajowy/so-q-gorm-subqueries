import so.q.grom.subqueries.Task
import so.q.grom.subqueries.Project
import grails.plugin.spock.IntegrationSpec

class GormSubqueriesSpec extends IntegrationSpec {

    Project project
    Task firstTask
    Task secondTask

    def setup() {
        project = new Project().save()
        firstTask = new Task(project: project).save()
        secondTask = new Task(project: project).save()
        new Task(project: project).save()
        assert Task.count() == 3
    }

    def 'will get older tasks'() {
        when:
        def tasks = Task.findAll() {
            dateCreated < property('dateCreated').of { id == secondTask.id }
        }

        then:
        tasks == [firstTask]
    }

    def 'should get tasks from the same project'() {
        when:
        def tasks = Task.where {
            project.id == property('id').of { id == firstTask.id }
        }.list()

        then:
        tasks == Task.all
    }
}
