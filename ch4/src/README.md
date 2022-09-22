* [siha80님께서 준비하신 `ZLayer` 적용한 코드](https://github.com/siha80/2022-zio-web-app-study/tree/b0ac1c6b0b7a43ff136455b7765f4ec14ea57ee0/ch2/src/main/scala) 및 설명과 함께 스터디 진행
* 스터디 이후 다음 레퍼런스를 참고하여 다시 작성
  * [ghidei's funcprog2022 github repo](https://github.com/ghidei/funcprog2022/tree/aa6e76f36e69b5eae1ed4c03b71ee5e175649dfc)
  * [Structuring ZIO 2 applications](https://softwaremill.com/structuring-zio-2-applications/)


```shell
$ curl "http://localhost:8090/todo/1"
# {"id":1,"title":"title-1"}⏎   

$ curl "http://localhost:8090/todos"
# [{"id":1,"title":"title-1"},{"id":2,"title":"title-2"},{"id":3,"title":"title-3"}]⏎                                                                     

$ curl -X POST "http://localhost:8090/todo" -H 'Content-Type: application/json' -d '{"title": "helloworld"}'
# {"id":4,"title":"helloworld"}⏎  

$ curl "http://localhost:8090/todos"
# [{"id":1,"title":"title-1"},{"id":2,"title":"title-2"},{"id":3,"title":"title-3"},{"id":4,"title":"helloworld"}]⏎  
```