package server.service;

public interface DBService {

    //Метод соединения с БД
   void getConnectionWithDB();

   // Метод разьединения от БД
   void disconnectDB();

   // Метод проверяющий авторизирован поьлзователь или нет
   boolean checkIfUserExistsForAuthorization(String login);

   // Метод проверяющий правильность введеного пароля
   boolean checkIfPasswordIsRight(String login, String password);

   // Метод регистрирующий нового пользователя
   boolean registerNewUser(String login, String password);
}
