package com.cleanup.todoc.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)
public abstract class SaveMyTasksDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile SaveMyTasksDatabase INSTANCE;

    // --- DAO ---
    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    // --- INSTANCE ---
    public static SaveMyTasksDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SaveMyTasksDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SaveMyTasksDatabase.class, "MyDatabase.db")
                            .addCallback(prepopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ---

    private static Callback prepopulateDatabase() {
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Log.println(Log.WARN, "database", "Calling prepopulateDatabase");
                for (Project project : Project.getAllProjects()) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", project.getId());
                    contentValues.put("name", project.getProjectName());
                    contentValues.put("color", project.getColor());

                    db.insert("Project", OnConflictStrategy.IGNORE, contentValues);
                }


            }
        };
    }
}



