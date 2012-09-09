import so.q.grom.subqueries.Task
import so.q.grom.subqueries.Project
import grails.plugin.spock.IntegrationSpec

class GormSubqueriesSpec extends IntegrationSpec {

    Project project
    Task firstTask
    Task secondTask
    Task taskFromOtherProject

    def setup() {
        project = new Project().save()
        firstTask = new Task(project: project).save()
        secondTask = new Task(project: project).save()
        taskFromOtherProject = new Task(project: new Project().save()).save()
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
            project == property('project').of { id == firstTask.id }
        }.list()

        then:
        tasks as Set == [firstTask, secondTask] as Set
    }

    def 'will get tasks from the same project using HQL'() {
        when:
        def tasks = Task.findAll(
                'from Task task where task.project = (select t.project from Task t where t.id = :taskId)',
                [taskId: firstTask.id]
        )

        then:
        tasks as Set == [firstTask, secondTask] as Set
    }
}
