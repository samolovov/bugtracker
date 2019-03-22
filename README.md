Образцы запросов:

* Получить список проектов
curl http://localhost:8080/projects
* Получить проект
curl http://localhost:8080/projects/1
* Создать проект
curl -H "Content-Type: application/json" -X POST -d '{"title": "New project", "description": "New description"}' http://localhost:8080/projects
* Обновить проект
curl -H "Content-Type: application/json" -X PUT -d '{"title": "Updated title", "description": "Updated description"}' http://localhost:8080/projects/1
* Удалить проект
curl -X DELETE http://localhost:8080/projects/1
* Получить полный список задач
curl http://localhost:8080/projects/-/tasks
* Получить список задач проекта
curl http://localhost:8080/projects/1/tasks
* Получить задачу проекта
curl http://localhost:8080/projects/1/tasks/1
* Cоздать задачу проекта
curl -H "Content-Type: application/json" -X POST -d '{"title": "New task", "description": "New task description", "status":"NEW", priority": 1}' http://localhost:8080/projects/1/tasks
* Обновить задачу проекта
curl -H "Content-Type: application/json" -X PUT -d '{"title": "Updated task", "description": "Updated description", "status":"IN_PROGRESS", priority": 3}' http://localhost:8080/projects/1/tasks/1
* Удалить задачу проекта
curl -X DELETE http://localhost:8080/projects/1/tasks/1

Пагинация:
* Для пагинации в запросе необходимо передавать в урле следующие (опциональные) параметры:
  '?page=0&size=3'

Сортировка:
* Для сортировки в запросе необходимо передавать в урле следующие (опциональные) параметры:
  '?sort=priority,desc', '?sort=priority,asc&sort=created,desc'

Фильтрация:
* Для фильтрации в запросе необходимо передавать в урле следующие (опциональные) параметры:
  ?createdStart=18-03-2019&createdEnd=26-03-2019&modifiedStart=18-03-2019&modifiedEnd=26-03-2019&status=NEW&priority=1
  Фильтрация реализована для метода получения полного списка задач (http://localhost:8080/projects/-/tasks)



