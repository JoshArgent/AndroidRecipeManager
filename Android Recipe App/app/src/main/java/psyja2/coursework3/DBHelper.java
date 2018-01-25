package psyja2.coursework3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Josh on 21/11/2017.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create a database to hold recipe data
        db.execSQL("CREATE TABLE recipes (_id INTEGER PRIMARY KEY AUTOINCREMENT, recipetitle TEXT, recipecategory TEXT, recipeinstructions TEXT, recipephoto TEXT);");

        // Insert an example recipe for a chocolate cake
        // Recipe source: https://www.bbc.co.uk/food/recipes/easy_chocolate_cake_31070
        db.execSQL("INSERT INTO recipes (recipetitle, recipecategory, recipeinstructions, recipephoto) VALUES ('Chocolate Cake', 'Dessert', '1. Preheat the oven to 180C/350F/Gas 4. Grease and line two 20cm/8in sandwich tins.\n" +
                "2. For the cake, place all of the cake ingredients, except the boiling water, into a large mixing bowl. Using a wooden spoon, or electric whisk, beat the mixture until smooth and well combined.\n" +
                "3. Add the boiling water to the mixture, a little at a time, until smooth. (The cake mixture will now be very liquid.)\n" +
                "4. Divide the cake batter between the sandwich tins and bake in the oven for 25-35 minutes, or until the top is firm to the touch and a skewer inserted into the centre of the cake comes out clean.\n" +
                "5. Remove the cakes from the oven and allow to cool completely, still in their tins, before icing.\n" +
                "6. For the chocolate icing, heat the chocolate and cream in a saucepan over a low heat until the chocolate melts. Remove the pan from the heat and whisk the mixture until smooth, glossy and thickened. Set aside to cool for 1-2 hours, or until thick enough to spread over the cake.\n" +
                "7. To assemble the cake, run a round-bladed knife around the inside of the cake tins to loosen the cakes. Carefully remove the cakes from the tins.\n" +
                "8. Spread a little chocolate icing over the top of one of the chocolate cakes, then carefully top with the other cake.\n" +
                "9. Transfer the cake to a serving plate and ice the cake all over with the chocolate icing, using a palette knife.', '" + "android.resource://psyja2.coursework3/" + R.mipmap.default_recipe_icon + "');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This should never get called, unless the app is reinstalled
        db.execSQL("DROP TABLE IF EXISTS recipes");
        onCreate(db);
    }
}
