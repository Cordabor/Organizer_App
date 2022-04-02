package com.e.organizer_app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TasksProvider extends ContentProvider {

    // Глобально уникальная строка для идентификации провайдера контента в платформе Android
    private static final String AUTHORITY = "com.e.organizer_app.tasksprovider";

    // представление для всего набора данных
    private static final String BASE_PATH = "tasks";

    // CONTENT_URI - это унифицированный идентификатор ресурса, который идентифицирует провайдера контента
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Константы для идентификации запрошенной операции. Числовые значения являются произвольными
    private static  final int TASKS = 1;
    private static final int TASKS_ID = 2;

    // цель класса UriMatcher - проанализировать URI и затем сообщить, какая операция была запрошена
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Task";

    // этот блок будет выполняться при первом вызове чего-либо из этого класса
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
        // это подстановочный знак, означает любое числовое значение, что означает,
        // что если мы получем URI, который начинается с base_path, а затем заканчивается
        //с / и числом, которое означает, что ищем определенную заметку, определенную строку в таблице базы данных
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TASKS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DbHelper helper = new DbHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String selection, @Nullable String[] strings1, @Nullable String s1) {
        // Метод запроса будет получать данные из задач таблицы базы данных, либо получаем все задачи
        //или только одну строку

        if (uriMatcher.match(uri) == TASKS_ID){
            selection = DbHelper.TASK_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DbHelper.TABLE_TASKS, DbHelper.ALL_COLUMNS,
                selection, null, null, null,
                DbHelper.TASK_START_DATE_TIME) ;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Метод вставки возвращает URI. И этот URI должен соответствовать этому шаблону: base_path, за которым следует
        // а / а затем значение первичного ключа записи

        // Значения содержимого - это класс, который имеет коллекцию пар имя-значение
        long id = database.insert(DbHelper.TABLE_TASKS,
                null, values);

        // Метод parse позволяет собрать строку и вернуть эквивалентный URI
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DbHelper.TABLE_TASKS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DbHelper.TABLE_TASKS, values, selection, selectionArgs);
    }
}
