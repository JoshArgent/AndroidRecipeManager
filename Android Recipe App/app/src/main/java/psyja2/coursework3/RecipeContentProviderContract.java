package psyja2.coursework3;

import android.net.Uri;

/**
 * Created by Josh on 21/11/2017.
 */
public class RecipeContentProviderContract
{

    public static final String AUTHORITY = "com.example.psyja2.RecipeContentProvider";

    // Recipes table URI
    public static final Uri RECIPES_URI = Uri.parse("content://"+AUTHORITY+"/recipes");

    // Recipe table fields
    public static final String ID = "_id";
    public static final String TITLE = "recipetitle";
    public static final String INSTRUCTIONS = "recipeinstructions";
    public static final String CATEGORY = "recipecategory";
    public static final String PHOTO = "recipephoto";

    // Categories will behave like individual tables, despite all been stored in the 'recipes' table
    public static final String CATEGORY_APPETISERS = "appetisers";
    public static final String CATEGORY_SOUPS = "soups";
    public static final String CATEGORY_PASTA = "pasta";
    public static final String CATEGORY_MEAT = "meat";
    public static final String CATEGORY_SEAFOOD = "seafood";
    public static final String CATEGORY_VEGETARIAN = "vegetarian";
    public static final String CATEGORY_DESSERTS = "desserts";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyProvider.data.text";
}
