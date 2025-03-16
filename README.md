# **DeliveryApp**  

DeliveryApp — это **REST API** для управления сервисом доставки еды, которое позволяет пользователям оформлять заказы, просматривать доступные рестораны и блюда, а также управлять статусом заказов. Приложение предоставляет удобные эндпоинты для взаимодействия с системой доставки.  

---

## **Основные функции**  

### **1. Управление заказами:**  
- Создание, редактирование и удаление заказов.  
- Отслеживание статуса заказов (*новый, в обработке, доставляется, доставлен*).  

### **2. Работа с ресторанами и блюдами:**  
- Получение списка доступных ресторанов.  
- Просмотр меню ресторана.  
- Фильтрация блюд по категории, цене и популярности.  
- Управление ингредиентами для каждого блюда.  

### **3. REST API:**  
- Поддержка различных HTTP методов: `GET`, `POST`, `PUT`, `DELETE`.  
- Эндпоинты для управления пользователями, ресторанами, заказами и ингредиентами.  
- Валидация данных на уровне сервисов и контроллеров.  

### **4. Документация API:**  
- Интерактивная документация с **Swagger** для удобного тестирования API.  
- Подробное описание эндпоинтов, форматов запросов и ответов.  

---

## **Технологии**  

- **Язык программирования:** Java 17  
- **Фреймворк:** Spring Boot 3.x  
- **База данных:** PostgreSQL  
- **ORM:** Hibernate  
- **Сборка:** Maven  
- **API-документация:** Postman  

---
