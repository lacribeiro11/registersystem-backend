# registersystem-backend

## Backend deployment

* Backend: [http://85.235.66.255:8080](http://85.235.66.255:8080)
* Swagger: [http://85.235.66.255:8080/swagger-ui.html](http://85.235.66.255:8080/swagger-ui.html)

## Get all Products endpoint

* To get all products: [http://85.235.66.255:8080/product/all](http://85.235.66.255:8080/product/all)
* From second page: [http://85.235.66.255:8080/product/all?page=1](http://85.235.66.255:8080/product/all?page=1)
  * **Important:** The first page is 0
* Page size 11: [http://85.235.66.255:8080/product/all?size=11](http://85.235.66.255:8080/product/all?size=11)
* Sort products by name ascending and amount
  descending: [http://85.235.66.255:8080/product/all?sort=name,asc&sort=amount,desc](http://85.235.66.255:8080/product/all?sort=name,asc&sort=amount,desc)
* Filter by name: [http://85.235.66.255:8080/product/all?name=Pepsi](http://85.235.66.255:8080/product/all?name=Pepsi)
  * *Tip:* it is possible to search by **eps** or **EPS**, it will for every product with these characters in the name
  * The search is case-insensitive

### Which columns can be sorted

* id
* name
* code
* foodType
* amount
* price

### The list of products is under content

```json
{"content":[{"id":"3fa85f64-5717-4562-b3fc-2c963f66afa6","name":"string","code":"string","foodType":"SOFT_DRINK","amount":0,"price":0.0}]...}
```
