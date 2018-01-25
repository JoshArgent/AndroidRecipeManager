package psyja2.coursework3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class RecipeEditorActivity extends AppCompatActivity {

    private static int TAKE_PHOTO_REQUEST = 1;
    private static int CAMERA_STORAGE_PERMISSION_REQUEST = 1;

    // UI components
    FloatingActionButton saveButton;
    ImageView recipePhotoView;
    ImageButton recipeTakePhotoButton;
    EditText recipeTitleEditText;
    Spinner recipeCategorySpinner;
    EditText recipeInstructionsEditText;

    // State variables
    private String photoURL = "android.resource://psyja2.coursework3/" + R.mipmap.default_recipe_icon; // default photo for a recipe
    private int recipeID = -1; // -1 indicates this is a new recipe

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_editor);

        // Get the UI components
        saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        recipePhotoView = (ImageView) findViewById(R.id.recipePhoto);
        recipeTakePhotoButton = (ImageButton) findViewById(R.id.recipeTakePhotoButton);
        recipeTitleEditText = (EditText) findViewById(R.id.recipeTitle);
        recipeCategorySpinner = (Spinner) findViewById(R.id.recipeCategory);
        recipeInstructionsEditText = (EditText) findViewById(R.id.recipeInstructions);

        // Set the default recipe photo
        recipePhotoView.setImageURI(Uri.parse(photoURL));

        // Event handler for take photo button
        recipeTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // First make sure the user has appropriate permissions to take photos + store photos
                // Permission checking credit: https://developer.android.com/training/permissions/requesting.html
                if (ContextCompat.checkSelfPermission(RecipeEditorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(RecipeEditorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(RecipeEditorActivity.this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_STORAGE_PERMISSION_REQUEST);
                }
                else
                {
                    // Launch the camera activity for result
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PHOTO_REQUEST);
                }
            }
        });

        // Event handler for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Check that all form fields have been filled out (there doesn't need to be a photo)
                if(recipeTitleEditText.getText().toString().length() <= 0 ||
                        recipeInstructionsEditText.getText().toString().length() <= 0 ||
                        recipeCategorySpinner.getSelectedItem() == null)
                {
                    Toast.makeText(RecipeEditorActivity.this, "Unable to save! Not all fields have been completed.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Form filled out correctly - save the recipe
                    saveRecipe();
                    Toast.makeText(RecipeEditorActivity.this, "Recipe saved!", Toast.LENGTH_SHORT).show();
                    // close the activity
                    finish();
                }
            }
        });

        // Load content for a recipe, if an ID is passed in the intent bundle
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("recipe_id"))
        {
            // Get the recipe ID
            recipeID = getIntent().getExtras().getInt("recipe_id");

            // Now query the content provider for a recipe with this ID
            // Setup the projection for the query
            String[] projection = new String[] {
                    RecipeContentProviderContract.ID,
                    RecipeContentProviderContract.TITLE,
                    RecipeContentProviderContract.CATEGORY,
                    RecipeContentProviderContract.INSTRUCTIONS,
                    RecipeContentProviderContract.PHOTO
            };

            // Run a query to the content provider and get the cursor object for the recipe with id = recipeID
            Uri queryUri = ContentUris.withAppendedId(RecipeContentProviderContract.RECIPES_URI, recipeID);
            Cursor cursor = getContentResolver().query(queryUri, projection, null, null, null);

            // Get the recipe data from the returned record (if it exists)
            if(cursor.moveToFirst())
            {
                // Get the raw values
                String title = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.TITLE));
                String category = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.CATEGORY));
                String instructions = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.INSTRUCTIONS));
                photoURL = cursor.getString(cursor.getColumnIndex(RecipeContentProviderContract.PHOTO));

                // Display the content in the activity
                recipeTitleEditText.setText(title);
                recipeCategorySpinner.setSelection(getCategoryIndex(category));
                recipeInstructionsEditText.setText(instructions);
                recipePhotoView.setImageURI(Uri.parse(photoURL));
            }
        }

        // Set the title of the activity
        if(recipeID == -1)
        {
            setTitle("New Recipe");
        }
        else
        {
            setTitle("Edit: " + recipeTitleEditText.getText().toString());
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == TAKE_PHOTO_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                Bitmap recipePhoto = (Bitmap) data.getExtras().get("data");
                photoURL = saveImageToStorage(recipePhoto);
                recipePhotoView.setImageURI(Uri.parse(photoURL));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        // Handle when a permission request has been completed
        // Credit: https://developer.android.com/training/permissions/requesting.html
        if(requestCode == CAMERA_STORAGE_PERMISSION_REQUEST)
        {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                // All permissions granted, can now launch camera to take recipe photo
                // Launch the camera activity for result
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        // Alert dialog to check if the user wants to discard any changes made
        // Credit to nikki, https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to discard changes?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog and activity
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Close the dialog
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        // Save form fields and state variables to the bundle
        bundle.putInt("recipe_id", recipeID);
        bundle.putString("recipe_photo", photoURL);
        bundle.putString("recipe_title", recipeTitleEditText.getText().toString());
        bundle.putString("recipe_category", recipeCategorySpinner.getSelectedItem().toString());
        bundle.putString("recipe_instructions", recipeInstructionsEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onRestoreInstanceState(bundle);

        // Load the recipe ID
        recipeID = bundle.getInt("recipe_id");

        // Load the photo URI
        photoURL = bundle.getString("recipe_photo");
        if(!photoURL.equalsIgnoreCase(""))
        {
            recipePhotoView.setImageURI(Uri.parse(photoURL));
        }

        // Load the title
        recipeTitleEditText.setText(bundle.getString("recipe_title"));

        // Load the category
        recipeCategorySpinner.setSelection(getCategoryIndex(bundle.getString("recipe_category")));

        // Load the instructions
        recipeInstructionsEditText.setText(bundle.getString("recipe_instructions"));
    }

    /*
    Will save the recipe to the content provider
     */
    private void saveRecipe()
    {
        // Package all the recipe data into a ContentValues object
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContentProviderContract.TITLE, recipeTitleEditText.getText().toString());
        recipeValues.put(RecipeContentProviderContract.CATEGORY, recipeCategorySpinner.getSelectedItem().toString());
        recipeValues.put(RecipeContentProviderContract.INSTRUCTIONS, recipeInstructionsEditText.getText().toString());
        recipeValues.put(RecipeContentProviderContract.PHOTO, photoURL);

        if(recipeID == -1) // If this is a new recipe, insert new record
        {
            // Insert the new recipe into the recipe database
            Uri result = getContentResolver().insert(RecipeContentProviderContract.RECIPES_URI, recipeValues);
            recipeID = Integer.valueOf(result.getLastPathSegment());
        }
        else // Otherwise update the recipe
        {
            // Update the recipe via the content provider
            Uri queryUri = ContentUris.withAppendedId(RecipeContentProviderContract.RECIPES_URI, recipeID);
            getContentResolver().update(queryUri, recipeValues, null, null);
        }
    }

    /*
    Finds the index of a given category value in the category spinner
    (Useful for setting the selected item by value)
     */
    private int getCategoryIndex(String category)
    {
        for (int i = 0; i < recipeCategorySpinner.getCount(); i++)
        {
            if (recipeCategorySpinner.getItemAtPosition(i).equals(category))
            {
                return i;
            }
        }
        return -1;
    }

    /*
    Function to save a bitmap to the external storage in a 'Recipe Photos' folder
    Returns a file URI path
    Credit to Gaby Bou Tayeh, https://stackoverflow.com/questions/15662258/how-to-save-a-bitmap-on-internal-storage
     */
    private String saveImageToStorage(Bitmap bitmap)
    {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Recipe Photos");
        myDir.mkdirs();
        String fileName = "photo-"+ System.currentTimeMillis() +".jpg";
        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

}
