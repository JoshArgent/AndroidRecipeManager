package psyja2.coursework3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Josh on 21/11/2017.
 */
public class RecipeContentProvider extends ContentProvider {

    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes", 1);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/#", 2);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/appetisers", 3);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/soups", 4);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/pasta", 5);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/meat", 6);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/seafood", 7);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/vegetarian", 8);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "recipes/desserts", 9);
        uriMatcher.addURI(RecipeContentProviderContract.AUTHORITY, "*", 10);
    }

    @Override
    public boolean onCreate()
    {
        // Get the Recipes database helper
        this.dbHelper = new DBHelper(this.getContext(), "recipes", null, 1);
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        // Gets the type of content (single if getting recipe by ID, multiple otherwise)
        String contentType;
        if (uri.getLastPathSegment() == null)
        {
            contentType = RecipeContentProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else
        {
            contentType = RecipeContentProviderContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        // Get the SQLite database object
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Match the table name; only valid for 'recipes' table
        String tableName;
        switch(uriMatcher.match(uri))
        {
            case 1:
                tableName = "recipes";
                break;
            default:
                return null;
        }

        // Insert the values into the table
        long id = db.insert(tableName, null, values);
        db.close();

        // Notify listeners of change
        Uri nu = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(nu, null);

        return nu;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // Get the SQLite database object
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Match the URI to the appropriate query
        switch(uriMatcher.match(uri))
        {
            case 2:
                // Query by recipe ID
                selection = "_id = " + uri.getLastPathSegment();
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 1:
                // Normal query on the recipes table
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 3:
                // Query by category
                selection = "recipecategory = 'Appetisers'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 4:
                // Query by category
                selection = "recipecategory = 'Soups'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 5:
                // Query by category
                selection = "recipecategory = 'Pasta'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 6:
                // Query by category
                selection = "recipecategory = 'Meat'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 7:
                // Query by category
                selection = "recipecategory = 'Seafood'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 8:
                // Query by category
                selection = "recipecategory = 'Vegetarian'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 9:
                // Query by category
                selection = "recipecategory = 'Desserts'";
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 10:
                // Query every record from the recipes table
                return db.rawQuery("SELECT * FROM recipes", selectionArgs);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Get the SQLite database object
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Find what table/rows are to be updated from the URI
        String tableName;
        switch(uriMatcher.match(uri))
        {
            case 1:
                // Recipes table based on selection query
                tableName = "recipes";
                break;
            case 2:
                // A particular recipe ID
                selection = "_id = " + uri.getLastPathSegment();
                tableName = "recipes";
                break;
            case 3:
                // All recipes in a category
                selection = "recipecategory = 'Appetisers'";
                tableName = "recipes";
                break;
            case 4:
                // All recipes in a category
                selection = "recipecategory = 'Soups'";
                tableName = "recipes";
                break;
            case 5:
                // All recipes in a category
                selection = "recipecategory = 'Pasta'";
                tableName = "recipes";
                break;
            case 6:
                // All recipes in a category
                selection = "recipecategory = 'Meat'";
                tableName = "recipes";
                break;
            case 7:
                // All recipes in a category
                selection = "recipecategory = 'Seafood'";
                tableName = "recipes";
                break;
            case 8:
                // All recipes in a category
                selection = "recipecategory = 'Vegetarian'";
                tableName = "recipes";
                break;
            case 9:
                // All recipes in a category
                selection = "recipecategory = 'Desserts'";
                tableName = "recipes";
                break;
            case 10:
                // All recipes
                tableName = "recipes";
                selection = "";
                break;
            default:
                return -1;
        }

        // Execute the update operation on the database
        int result = db.update(tableName, values, selection, selectionArgs);
        db.close();

        // Notify of changes
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // Get the SQLite database object
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Find what table/rows are to be deleted from the URI
        String tableName;
        switch(uriMatcher.match(uri))
        {
            case 1:
                // Recipes table based on selection query
                tableName = "recipes";
                break;
            case 2:
                // A particular recipe ID
                selection = "_id = " + uri.getLastPathSegment();
                tableName = "recipes";
                break;
            case 3:
                // All recipes in a category
                selection = "recipecategory = 'Appetisers'";
                tableName = "recipes";
                break;
            case 4:
                // All recipes in a category
                selection = "recipecategory = 'Soups'";
                tableName = "recipes";
                break;
            case 5:
                // All recipes in a category
                selection = "recipecategory = 'Pasta'";
                tableName = "recipes";
                break;
            case 6:
                // All recipes in a category
                selection = "recipecategory = 'Meat'";
                tableName = "recipes";
                break;
            case 7:
                // All recipes in a category
                selection = "recipecategory = 'Seafood'";
                tableName = "recipes";
                break;
            case 8:
                // All recipes in a category
                selection = "recipecategory = 'Vegetarian'";
                tableName = "recipes";
                break;
            case 9:
                // All recipes in a category
                selection = "recipecategory = 'Desserts'";
                tableName = "recipes";
                break;
            case 10:
                // All recipes
                selection = "";
                tableName = "recipes";
                break;
            default:
                return -1;
        }

        // Execute the delete operation on the database
        int result = db.delete(tableName, selection, selectionArgs);
        db.close();

        // Notify of changes
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }
}
