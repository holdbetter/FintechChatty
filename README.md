# FintechChatty
Курсовой проект с курса Тинькофф Финтех.

Стек: MVI (elmslie) / Kotlin / Dagger2 / RxJava3 / Retrofit / Glide / Room / View Binding / Custom View

### Описание
Приложение представляет собой мессенджер в формате дискорда / слака. Конференц-общение в топиках и стримах

Основные возможности приложения:
 - Общение в общих темах;
 - Общение в отдельных темах;
 - Добавление реакций;
 - Офлайн доступ;
 - Поиск пользователей / просмотр аккаунтов;
 - Отображение статусов пользоватей.
 
 [Screenshots](https://drive.google.com/drive/folders/1XZn6rEYqwfbmA93Tr91jR3ayd3AdI6gf?usp=sharing) | 
 [Feature demo](https://drive.google.com/file/d/1iZG-Lhf2asNRJrwExN8_tb6kbKJTs2eA/view?usp=sharing) |
 [Sustain demo](https://drive.google.com/file/d/10Z5xXgJOgVoSe05OZccwRlGwVGXXrgs5/view?usp=sharing)
 
### Детали реализации
Дизайн приложения из ТЗ, работа приложения построена на REST API - тоже по ТЗ. В качестве API используется - [Zulip](https://zulip.com/). Для сборки проекта нужны соответствующие Credentials, [contact me](t.me/holdbetter)

Дизайн [загрузочного экрана](https://drive.google.com/file/d/1uyvRL1EDAok5CoMnNbIJQ7Ww8WO_IKVh/view?usp=sharing) и [диалога](https://drive.google.com/file/d/1R6_D54CgQKVJxd9rMyTvAAaLYOO8MeHl/view?usp=sharing) 
с выбором топика - личные, придуманы и нарисованы мной. 
 
  - Single activity приложение, всё на фрагментах;
  - В качестве DI - чистый Dagger2, использую такие фичи, как Dependent Component / Custom Scopes / Assisted Inject;
  - Все экраны поддерживают состояния загрузки, обработки ошибок, демонстрации данных, демонстрации пустых данных;
  - Все экраны поддерживают офлайн доступ к данным, кэширование = Room + Glide;
  - Все экраны поддерживают swipe-to-refresh;
  - Взаимодействие с данными возможно только тогда, когда эти данные доступны пользователю, например, поиск закрыт, если данные в состоянии загрузки;
  - Все походы в сеть имеют проверку наличия сети, также в походах использую rx операторы `timeout`, `retry`;
  - Чат с сообщениями использует кастомную пагинацию;
  - Поиск данных доступен в офлайн и построен на Regex;
  - Экраны построены на CoordinatorLayout и переопределяют логику для Snackbar.
