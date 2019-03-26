## BugTracker Application

### Description
This application provides REST API for creating/viewing/updating/deleting projects and tasks. One project can contain many tasks. Soft-delete is implemented for deleting entities: i.e. entities aren't removed from DB, but 'deleted' property is set to 'true' for them instead. 

### Installation guide
BugTracker Application is developed using Spring Boot. In order to launch it you need to do the following steps:

* Install PostgreSQL database. (You can use any other, but in this case you need to provide maven dependency in pom.xml)    
* Clone/Download current repository
* Edit `application.yml` (datasource section) and provide your DB "host/username/password" settings
* Compile and run as common java app, `Application.java` contains main method that start Tomcat container.

During application startup 2 DB tables will be created: *project* and *task*. You can find DB schema inside `schema.sql`. 
Also several projects and tasks will be inserted into corresponding tables. You can find DB data inside `data.sql`.

### Request examples:

#### Basic queries
* Get projects:
`curl http://localhost:8080/projects`
* Get project with ID:
`curl http://localhost:8080/projects/1`
* Create project: 
`curl -H "Content-Type: application/json" -X POST -d '{"title": "New project", "description": "New description"}' http://localhost:8080/projects`
* Update project with ID: 
`curl -H "Content-Type: application/json" -X PUT -d '{"title": "Updated title", "description": "Updated description"}' http://localhost:8080/projects/1`
* Delete project with ID: 
`curl -X DELETE http://localhost:8080/projects/1`
* Get tasks for all projects:
`curl http://localhost:8080/projects/-/tasks`
* Get tasks for project with ID:
`curl http://localhost:8080/projects/1/tasks`
* Get task with taskID for project with projectID:
`curl http://localhost:8080/projects/1/tasks/1`
* Create task for project with ID:
`curl -H "Content-Type: application/json" -X POST -d '{"title": "New task", "description": "New task description", "status":"NEW", priority": 1}' http://localhost:8080/projects/1/tasks`
* Update task with taskID for project with projectID:
`curl -H "Content-Type: application/json" -X PUT -d '{"title": "Updated task", "description": "Updated description", "status":"IN_PROGRESS", priority": 3}' http://localhost:8080/projects/1/tasks/1`
* Delete task with taskID for project with projectID
`curl -X DELETE http://localhost:8080/projects/1/tasks/1`

#### Additional queries
You can provide additional optional parameters in order to use pagination, sorting and filtering.

* For pagination: `?page=0&size=3`
* For sorting:    `?sort=priority,asc&sort=created,desc`
* For filtering:  `?createdStart=18-03-2019&createdEnd=26-03-2019&modifiedStart=18-03-2019&modifiedEnd=26-03-2019&status=NEW&priority=1` 

Please note: filtering is implemented only for method that requests all tasks for all projects: (http://localhost:8080/projects/-/tasks)
