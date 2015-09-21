package com.googlemaps.template.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.googlemaps.template.myapplication.database.DBHelper;
import com.googlemaps.template.myapplication.database.MyContentProvider;
import com.googlemaps.template.myapplication.network.PlacePoints;

public class PlaceDescriptionActivity extends AppCompatActivity {

    public static final String EXTRA_POINT = "point";
    public static final int RESULT_CHANGED_DESCRIPTION = 1;
    public static final int RESULT_ITEM_REMOVED = 2;

    PlacePoints.Point mPoint;

    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_description);

        mPoint = getIntent().getParcelableExtra(EXTRA_POINT);

        setTitle(mPoint.mName);

        tvDescription = (TextView) findViewById(R.id.tvDescription);

        tvDescription.setText(mPoint.mDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            showEditDialog();
            return true;
        } else if (id == R.id.action_delete) {
            delete();
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        Uri uri = Uri.parse("content://" +
                MyContentProvider.AUTHORITY + "/" + MyContentProvider.PLACES_PATH);

        getContentResolver().delete(uri, DBHelper.FIELD_ID + " = " + mPoint.mId, null);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_POINT, mPoint);
        setResult(RESULT_ITEM_REMOVED, intent);
        finish();
    }

    private void showEditDialog() {

        final EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Редактирование описания")
                .setView(editText)
                .setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editDescription(editText.getText().toString());
                    }
                }).show();
    }

    private void editDescription(String newDescription) {
        tvDescription.setText(newDescription);
        Uri uri = Uri.parse("content://" +
                MyContentProvider.AUTHORITY + "/" + MyContentProvider.PLACES_PATH);

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.FIELD_LONG_DESCRIPTION, newDescription);

        getContentResolver().update(uri, contentValues, DBHelper.FIELD_ID + " = " + mPoint.mId, null);

        mPoint.mDescription = newDescription;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_POINT, mPoint);
        setResult(RESULT_CHANGED_DESCRIPTION, intent);
    }
}
