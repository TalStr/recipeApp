package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.recipeapp.api.RecipeInfo;

import java.util.ArrayList;
import java.util.LinkedList;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "AppData.db";
    public static final String USERS_TABLE = "Users";
    //Cols userid,username,email,password,profilepic

    public static final String RECIPES_TABLE = "Recipes";
    //Cols recipe_id,recipe_name,preptime,cooktime,servings,image,price,privacy_level, creation_datetime

    public static final String INGREDIENTS_TABLE = "Ingredients";
    //Cols ingredient_id,ingredient_name

    public static final String INSTRUCTIONS_TABLE = "Instructions";
    //Cols recipe_id,step,desc

    public static final String RECIPE_INGREDIENTS_TABLE = "Recipe_Ingredients";
    //Cols recipe_id,ingredient_id,quantity,unit

    public static final String REVIEWS_TABLE = "Reviews";
    //Cols recipe_id,user_id,stars,comment

    public static final String FOLLOWING_TABLE = "Following";
    //Cols follower_id, following_id

    private static DBHelper instance;
    private DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS_TABLE +
                " (user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "profilepic TEXT)");

        db.execSQL("CREATE TABLE " + RECIPES_TABLE +
                " (recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " + // add this line
                "recipe_name TEXT NOT NULL, " +
                "preptime INTEGER, " +
                "cooktime INTEGER, " +
                "servings INTEGER, " +
                "image TEXT, " +
                "price INTEGER, " +
                "privacy_level INTEGER, " +
                "creation_datetime DATETIME DEFAULT CURRENT_TIMESTAMP," + // add this line
                "FOREIGN KEY (user_id) REFERENCES " + USERS_TABLE + "(user_id))"); // add this line

        db.execSQL("CREATE TABLE " + INGREDIENTS_TABLE +
                " (ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ingredient_name TEXT NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE " + INSTRUCTIONS_TABLE +
                " (recipe_id INTEGER NOT NULL, " +
                "step INTEGER NOT NULL, " +
                "description TEXT NOT NULL, " +
                "FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + "(recipe_id))");

        db.execSQL("CREATE TABLE " + RECIPE_INGREDIENTS_TABLE +
                " (recipe_id INTEGER NOT NULL, " +
                "ingredient_id INTEGER NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit INTEGER NOT NULL, " +
                "FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + "(recipe_id), " +
                "FOREIGN KEY (ingredient_id) REFERENCES " + INGREDIENTS_TABLE + "(ingredient_id), " +
                "PRIMARY KEY (recipe_id, ingredient_id))");

        db.execSQL("CREATE TABLE " + REVIEWS_TABLE +
                " (recipe_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "stars INTEGER NOT NULL, " +
                "comment TEXT, " +
                "FOREIGN KEY (recipe_id) REFERENCES " + RECIPES_TABLE + "(recipe_id), " +
                "FOREIGN KEY (user_id) REFERENCES " + USERS_TABLE + "(userid))");

        db.execSQL("CREATE TABLE " + FOLLOWING_TABLE +
                " (following_id INTEGER NOT NULL, " +
                "followed_id INTEGER NOT NULL, " +
                "FOREIGN KEY (follower_id) REFERENCES " + USERS_TABLE + "(userid), " +
                "FOREIGN KEY (following_id) REFERENCES " + USERS_TABLE + "(userid), " +
                "PRIMARY KEY (follower_id, following_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECIPES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INGREDIENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INSTRUCTIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_INGREDIENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REVIEWS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FOLLOWING_TABLE);
        onCreate(db);
    }
    public boolean usernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + USERS_TABLE + " WHERE LOWER(username)=LOWER(?)";
        Cursor cursor = db.rawQuery(query, new String[]{username.toLowerCase()});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return (count > 0);
    }
    public int getIDbyUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT user_id FROM " + USERS_TABLE + " WHERE LOWER(username)=LOWER(?)";
        Cursor cursor = db.rawQuery(query, new String[]{username.toLowerCase()});
        int userID = -1;
        if (cursor.moveToFirst()) {
            userID = cursor.getInt(0);
        }
        cursor.close();
        return userID;
    }
    public String getUsernameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT username FROM " + USERS_TABLE + " WHERE user_id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }
        cursor.close();
        return username;
    }
    public int getIngredientID(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ingredient_id FROM " + INGREDIENTS_TABLE + " WHERE ingredient_name=?";
        Cursor cursor = db.rawQuery(query, new String[]{name.toLowerCase()});
        int ingredientID = -1;
        if (cursor.moveToFirst()) {
            ingredientID = cursor.getInt(0);
        }
        cursor.close();
        return ingredientID;
    }
    public String getIngredientName(int ingredientID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ingredient_name FROM " + INGREDIENTS_TABLE + " WHERE ingredient_id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ingredientID)});
        String ingredientName = null;
        if (cursor.moveToFirst()) {
            ingredientName = cursor.getString(0);
        }
        cursor.close();
        return ingredientName;
    }
    public int addRecipe(int user_id, String recipe_name, int prep_time, int cook_time,
                         int servings, String image, int price, int privacyLevel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("recipe_name", recipe_name);
        values.put("preptime", prep_time);
        values.put("cooktime", cook_time);
        values.put("servings", servings);
        values.put("image", image);
        values.put("price", price);
        values.put("privacy_level", privacyLevel);
        return (int)db.insert(RECIPES_TABLE, null, values);
    }
    public boolean addInstructionToRecipe(int recipeID, int step, String text)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeID);
        values.put("step", step);
        values.put("description", text);
        return (db.insert(INSTRUCTIONS_TABLE, null, values) != -1);
    }
    public int addIngredient(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ingredient_name", name.toLowerCase());
        return (int) db.insert(INGREDIENTS_TABLE, null, values);
    }
    public boolean addIngredientToRecipe(int recipeID, int ingredientID, double quantity, int unit)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeID);
        values.put("ingredient_id", ingredientID);
        values.put("quantity", quantity);
        values.put("unit", unit);
        return (db.insert(RECIPE_INGREDIENTS_TABLE, null, values) != -1);
    }
    public boolean emailTaken(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + USERS_TABLE + " WHERE LOWER(EMail)=LOWER(?)";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return (count > 0);
    }

    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Username", username);
        values.put("EMail", email);
        values.put("Password", password);
        return (db.insert(USERS_TABLE, null, values) != -1);
    }

    public boolean validLoginInfo(String username, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT password FROM " + USERS_TABLE + " WHERE LOWER(Username)=LOWER(?)";
        Cursor cursor = db.rawQuery(query, new String[]{username.toLowerCase()});
        boolean valid = (cursor.moveToFirst() && cursor.getString(0).equals(password));
        cursor.close();
        return valid;
    }

//    public LinkedList<RecipeInfo> getUsersRecipes(int userID) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + RECIPES_TABLE + " WHERE user_id=?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userID)});
//        LinkedList<RecipeInfo> list = new LinkedList<>();
//        int recipeID, prepTime, cookTime, servings, price, privacy;
//        String name, image, creationTime;
//        if (cursor.moveToFirst()) {
//            do {
//                recipeID = cursor.getInt(0);
//                name = cursor.getString(2);
//                prepTime = cursor.getInt(3);
//                cookTime = cursor.getInt(4);
//                servings = cursor.getInt(5);
//                image = cursor.getString(6);
//                price = cursor.getInt(7);
//                privacy = cursor.getInt(8);
//                creationTime = cursor.getString(9);
//                RecipeInfo row = new RecipeInfo(userID, recipeID, name, prepTime, cookTime, servings, image, price, privacy, creationTime);
//                list.add(row);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }
//    public RecipeInfo getRecipeInfo(int recipeID){
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + RECIPES_TABLE + " WHERE recipe_id=?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeID)});
//        RecipeInfo info = null;
//        int userID, prepTime, cookTime, servings, price, privacy;
//        String name, image, creationTime;
//        if (cursor.moveToFirst()) {
//            userID = cursor.getInt(1);
//            name = cursor.getString(2);
//            prepTime = cursor.getInt(3);
//            cookTime = cursor.getInt(4);
//            servings = cursor.getInt(5);
//            image = cursor.getString(6);
//            price = cursor.getInt(7);
//            privacy = cursor.getInt(8);
//            creationTime = cursor.getString(9);
//            info = new RecipeInfo(userID, recipeID, name, prepTime, cookTime, servings, image, price, privacy, creationTime);
//        }
//        cursor.close();
//        return info;
//    }

//    public LinkedList<RecipeIngredient> getRecipeIngredients(int recipeID) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + RECIPE_INGREDIENTS_TABLE + " WHERE recipe_id=?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeID)});
//        LinkedList<RecipeIngredient> list = new LinkedList<>();
//        int ingredientID, unit;
//        double quantity;
//        if (cursor.moveToFirst()) {
//            do {
//                ingredientID = cursor.getInt(1);
//                quantity = cursor.getDouble(2);
//                unit = cursor.getInt(3);
//                RecipeIngredient row = new RecipeIngredient(recipeID, ingredientID, quantity, unit);
//                list.add(row);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }

    public LinkedList<RecipeInstruction> getRecipeInstructions(int recipeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + INSTRUCTIONS_TABLE + " WHERE recipe_id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeID)});
        LinkedList<RecipeInstruction> list = new LinkedList<>();
        int step;
        String desc;
        if (cursor.moveToFirst()) {
            do {
                step = cursor.getInt(1);
                desc= cursor.getString(2);
                RecipeInstruction row = new RecipeInstruction(recipeID, step, desc);
                list.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public LinkedList<UserDBitem> getUserSearch(String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT user_id, username, profilepic FROM " + USERS_TABLE + " WHERE username LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchText + "%"});
        LinkedList<UserDBitem> list = new LinkedList<>();
        int userID;
        String username, profilePic;
        if (cursor.moveToFirst()) {
            do {
                userID = cursor.getInt(0);
                username = cursor.getString(1);
                profilePic = cursor.getString(2);
                UserDBitem row = new UserDBitem(userID, username, profilePic);
                list.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public boolean isFollowing(int followingID, int followedID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT following_id, followed_id FROM " + FOLLOWING_TABLE + " WHERE following_id=? AND followed_id=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(followingID), String.valueOf(followedID)});
        boolean isFollowing = cursor.getCount() > 0;
        cursor.close();
        return isFollowing;
    }
    public void follow(int followingID, int followedID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("following_id", followingID);
        contentValues.put("followed_id", followedID);
        db.insert(FOLLOWING_TABLE, null, contentValues);
        db.close();
    }

    public void unfollow(int followingID, int followedID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "following_id=? AND followed_id=?";
        String[] whereArgs = new String[]{String.valueOf(followingID), String.valueOf(followedID)};
        db.delete(FOLLOWING_TABLE, whereClause, whereArgs);
        db.close();
    }

    public LinkedList<UserDBitem> getFriends(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.user_id, u.username, u.profilepic FROM " + USERS_TABLE + " AS u " +
                "JOIN " + FOLLOWING_TABLE + " AS f ON u.user_id = f.followed_id " +
                "WHERE f.following_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userID)});
        LinkedList<UserDBitem> list = new LinkedList<>();
        int friendID;
        String username, profilePic;
        if (cursor.moveToFirst()) {
            do {
                friendID = cursor.getInt(0);
                username = cursor.getString(1);
                profilePic = cursor.getString(2);
                UserDBitem row = new UserDBitem(friendID, username, profilePic);
                list.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }













    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}
