package psyja2.coursework3;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeViewerActivity extends AppCompatActivity {

    // GUI components
    ImageView recipePhoto;
    TextView recipeCategory;
    TextView recipeInstructions;
    ImageButton editButton;
    ImageButton deleteButton;

    // The recipe being displayed
    private int recipeID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        // Get all the GUI components
        recipePhoto = (ImageView) findViewById(R.id.recipePhoto);
        recipeCategory = (TextView) findViewById(R.id.recipeCategory);
        recipeInstructions = (TextView) findViewById(R.id.recipeInstructions);
        editButton = (ImageButton) findViewById(R.id.editButton);
        deleteButton = (ImageButton) findViewById(R.id.deleteButton);

        // Add event handler for the edit button click
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepare an intent to launch the recipe editor activity
                Intent intent = new Intent(RecipeViewerActivity.this, RecipeEditorActivity.class);
                // Pass the recipe ID via binder
                Bundle bundle = new Bundle();
                bundle.putInt("recipe_id", recipeID);
                intent.putExtras(bundle);
                // Launch the activity
                startActivity(intent);
            }
        });

        // Add event handler for the delete button click event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Alert dialog to check if the user wants to delete the recipe
                // Credit to nikki, https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeViewerActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this recipe?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Close the dialog and delete the recipe
                        // Delete the recipe via the content provider
                        Uri queryUri = ContentUris.withAppendedId(RecipeContentProviderContract.RECIPES_URI, recipeID);
                        getContentResolver().delete(queryUri, null, null);
                        // Notify the user and close the activity
                        Toast.makeText(RecipeViewerActivity.this, "Recipe deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Close the dialog
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
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
        super.onResume();

        // Get the recipe ID from the bundle and display it's content
        // (Do this in resume so that content is updated if the user edits the content, then returns to this activity)
        if (getIntent().getExtras().containsKey("recipe_id"))
        {
            displayRecipe(getIntent().getExtras().getInt("recipe_id"));
        }
        else if(recipeID != -1) // The recipeID may have been restored via onRestoreInstanceState()
            displayRecipe(recipeID);
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

        // Save the recipe ID
        outState.putInt("recipe_id", recipeID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        // Get the saved recipe ID (onResume() will get the latest content from the content provider)
        recipeID = inState.getInt("recipe_id");
    }

    private void displayRecipe(int recipeID)
    {
        this.recipeID = recipeID;

        // Setup the projection for the query
        String[] projection = new String[] {
                RecipeContentProviderContract.ID,
                RecipeContentProviderContract.TITLE,
                RecipeContentProviderContract.CATEGORY,
                RecipeContentProviderContract.INSTRUCTIONS,
                RecipeContentProviderContract.PHOTO
        };

        // Run a query to the content provider and get the cursor object for the recipe with id
        Uri queryUri = ContentUris.withAppendedId(RecipeContentProviderContract.RECIPES_URI, recipeID);
        Cursor cursor = getContentResolver().query(queryUri, projection, null, null, null);

        // Get the recipe data from the returned record (if it exists)
        if(cursor.moveToFirst())
        {
            // Get the raw values
            String title = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.TITLE));
            String category = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.CATEGORY));
            String instructions = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.INSTRUCTIONS));
            String photoURI = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.PHOTO));

            // Display the content in the activity
            this.setTitle(title);
            recipeCategory.setText(category);
            recipeInstructions.setText(instructions);
            recipePhoto.setImageURI(Uri.parse(photoURI));
        }

    }

}
