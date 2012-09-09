package so.q.grom.subqueries

class Task {
    Date dateCreated
    static belongsTo = [project: Project]
}
