package psyja2.coursework3;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class RecipeListActivity extends AppCompatActivity {

    // GUI components
    ListView recipeListView;
    FloatingActionButton addButton;
    Spinner categorySpinner;

    // List view data adapter
    SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        // Set the title of the activity
        this.setTitle("Recipe List");

        // Get the GUI components
        recipeListView = (ListView) findViewById(R.id.recipeListView);
        addButton = (FloatingActionButton) findViewById(R.id.addRecipeIcon);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        // Add click handler to the create recipe button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch a blank RecipeEditorActivity
                Intent intent = new Intent(RecipeListActivity.this, RecipeEditorActivity.class);
                startActivity(intent);
            }
        });

        // Add click handler to the recipe list view
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Check the adaptor and cursor is not null
                if(dataAdapter == null)
                    return;
                if(dataAdapter.getCursor() == null)
                    return;

                // Get the recipe ID of the clicked item from the cursor
                Cursor cursor = dataAdapter.getCursor();
                cursor.moveToPosition(i);
                int recipeID = cursor.getInt(cursor.getColumnIndex(RecipeContentProviderContract.ID));

                // Now launch the recipe viewer activity for this recipe ID by passing the ID through binder
                Intent intent = new Intent(RecipeListActivity.this, RecipeViewerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("recipe_id", recipeID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Add event handler for the category spinner value changing
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Update the list view - the category filter may have changed
                reloadRecipeList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        /* Populate the list view - items may have been added/deleted */
        reloadRecipeList();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);
    }

    private void reloadRecipeList()
    {
        // Execute a query for the recipes from the content provider

        // Setup the projection for the query
        String[] projection = new String[] {
                RecipeContentProviderContract.ID,
                RecipeContentProviderContract.TITLE,
                RecipeContentProviderContract.CATEGORY,
                RecipeContentProviderContract.PHOTO
        };

        Uri queryUri = RecipeContentProviderContract.RECIPES_URI;
        if(!categorySpinner.getSelectedItem().equals("All Categories"))
        {
            // If not 'all categories' selected then => filter by category
            if(categorySpinner.getSelectedItem().equals("Appetisers"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_APPETISERS);
            else if(categorySpinner.getSelectedItem().equals("Soups"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_SOUPS);
            else if(categorySpinner.getSelectedItem().equals("Pasta"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_PASTA);
            else if(categorySpinner.getSelectedItem().equals("Meat"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_MEAT);
            else if(categorySpinner.getSelectedItem().equals("Seafood"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_SEAFOOD);
            else if(categorySpinner.getSelectedItem().equals("Vegetarian"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_VEGETARIAN);
            else if(categorySpinner.getSelectedItem().equals("Desserts"))
                queryUri = Uri.withAppendedPath(queryUri, RecipeContentProviderContract.CATEGORY_DESSERTS);
        }

        // Run a query to the content provider and get the cursor object
        Cursor cursor = getContentResolver().query(queryUri, projection, null, null, null);

        // Filter the columns that I want to show
        String colsToDisplay [] = new String[] {
                RecipeContentProviderContract.TITLE,
                RecipeContentProviderContract.CATEGORY,
                RecipeContentProviderContract.PHOTO
        };

        // Associated IDs for the list view item components
        int[] colResIds = new int[]{
                R.id.title,
                R.id.subtitle,
                R.id.icon
        };

        // Set the data adapter for the list view
        dataAdapter = new SimpleCursorAdapter(this, R.layout.recipe_list_item, cursor, colsToDisplay, colResIds, 0);
        recipeListView.setAdapter(dataAdapter);
    }
}
